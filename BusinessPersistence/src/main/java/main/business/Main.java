package main.business;

import main.Event;
import main.Participant;
import main.persistence.DbClient;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main {
    private static HttpServer server;
    private static DbClient dbClient;

    private static JSONObject readJSONRequest(HttpExchange exchange) {
        InputStreamReader r = new InputStreamReader(exchange.getRequestBody());
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(r);
        } catch (IOException | ParseException e) {
            // TODO: handle
            return null;
        }
    }

    private static void sendJSONResponse(HttpExchange exchange, JSONObject response) {
        try {
            exchange.sendResponseHeaders(200, 0);
            var output = exchange.getResponseBody();
            output.write(response.toJSONString().getBytes());
        } catch (IOException e) {
            // TODO: handle
        }
        exchange.close();
    }

    private static void sendResponse(HttpExchange exchange) {
        sendJSONResponse(exchange, new JSONObject());
    }

    private static class EventRequest implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            JSONObject json = readJSONRequest(exchange);

            String date         = (String) json.get("date");
            String time         = (String) json.get("time");
            String title        = (String) json.get("title");
            String description  = (String) json.get("desc");
            String hostEmail    = (String) json.get("email");
            String eventID      = (String) json.get("uuid");
            
            try {
                if (Objects.nonNull(eventID)) {
                    dbClient.addEvent(Event.create(eventID, date, time, title, description, hostEmail));
                } else {
                    dbClient.addEvent(Event.create(date, time, title, description, hostEmail));
                }
            } catch (Event.HandledIllegalValueException | SQLException e) {
                System.out.println("Failed to create event: " + e.getMessage());
            }
            sendResponse(exchange);
        }
    }

    private static class ParticipantRequest implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            JSONObject json = readJSONRequest(exchange);

            String eventID       = (String) json.get("eventID");
            String name          = (String) json.get("name");
            String email         = (String) json.get("email");
            String participantID = (String) json.get("participantID");

            try {
                if (participantID != null) {
                    dbClient.addParticipant(
                            Participant.create(participantID, eventID, name, email)
                    );
                } else {
                    dbClient.addParticipant(
                            Participant.create(eventID, name, email)
                    );
                }
            } catch (Event.HandledIllegalValueException | SQLException e) {
                System.out.println("Failed to create participant: " + e.getMessage());
            }
            sendResponse(exchange);
        }
    }

    private static class ListEventsRequest implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            List<Event> events;
            try {
                events = dbClient.getEvents();
            } catch (SQLException e) {
                System.out.println("Failed to retrieve events: " + e.getMessage());
                return;
            }

            JSONArray eventList = new JSONArray();
            JSONObject json;

            for (Event event : events) {
                json = new JSONObject(Map.of(
                    "date", event.eventDateTime().toLocalDate(),
                    "time", event.eventDateTime().toLocalTime(),
                    "title", event.title(),
                    "desc", event.description(),
                    "email", event.hEmail(),
                    "uuid", event.uuid()
                ));
                eventList.add(json);
            }

            sendJSONResponse(exchange, new JSONObject(Map.of("events", eventList)));
        }
    }

    private static class ListParticipantsRequest implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            JSONObject json = readJSONRequest(exchange);

            String eventID = (String) json.get("eventID");

            List<Participant> participants;
            try {
                participants = dbClient.getParticipants(eventID);
            } catch (SQLException e) {
                System.out.println("Failed to retrieve participants: " + e.getMessage());
                return;
            }
            
            JSONArray participantList = new JSONArray();
            JSONObject participantJson;

            for (Participant participant : participants) {
                participantJson = new JSONObject(Map.of(
                    "name", participant.name(),
                    "email", participant.email(),
                    "eventID", participant.eventId(),
                    "uuid", participant.uuid()
                ));
                participantList.add(participantJson);
            }

            sendJSONResponse(exchange, new JSONObject(Map.of("participants", participantList)));
        }
    }

    private static class TestRequest implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                exchange.sendResponseHeaders(200, 0);
                var output = exchange.getResponseBody();
                output.write("test".getBytes());
            } catch (IOException e) {
                //TODO: handle
            }
            exchange.close();
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        dbClient = new DbClient();

        try {
            String hostname = "localhost";
            int port = 6969;
            server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
        } catch (IOException e) {
            System.out.println("Failed to start server: " + e.getMessage());
            return;
        }
        
        server.createContext("/api/test",               new TestRequest());
        server.createContext("/api/list-events",        new ListEventsRequest());
        server.createContext("/api/list-participants",  new ListParticipantsRequest());
        server.createContext("/api/event",              new EventRequest());
        server.createContext("/api/participant",        new ParticipantRequest());

        server.start();
    }
}
