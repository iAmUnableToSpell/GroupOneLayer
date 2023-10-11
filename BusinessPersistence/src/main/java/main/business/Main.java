package main.business;

import main.Event;
import main.Participant;
import main.persistence.DbClient;

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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

//TODO: add json.simple to POM

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

    private static class EventRequest implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            JSONObject json = readJSONRequest(exchange);

            String date         = (String) json.get("date");
            String time         = (String) json.get("time");
            String ampm         = (String) json.get("ampm");
            String title        = (String) json.get("title");
            String description  = (String) json.get("description");
            String hostEmail    = (String) json.get("hostEmail");
            String eventID      = (String) json.get("eventID");
            
            try {
                if (Objects.nonNull(eventID)) {
                    dbClient.addEvent(Event.create(eventID, date, "%s %s".formatted(time, ampm), title, description, hostEmail));
                } else {
                    dbClient.addEvent(Event.create(date, "%s %s".formatted(time, ampm), title, description, hostEmail));
                }
            } catch (Event.HandledIllegalValueException | SQLException e) {
                System.out.println("Failed to create event: " + e.getMessage());
            }
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

            StringBuilder sb = new StringBuilder();

            for(Event event : events){
                sb.append(String.format(
                        "%s on %s at %s | Host Email: %s | Event ID: %s | '%s' \n\n",
                        event.title(),
                        event.eventDateTime().toLocalDate().format(
                                DateTimeFormatter.ISO_LOCAL_DATE
                        ),
                        //event.eventDateTime().getHour(), event.eventDateTime().getMinute(),
                        event.eventDateTime().toLocalTime().format(
                                DateTimeFormatter.ofPattern("hh:mm a")
                        ),
                        event.hEmail(),
                        event.uuid(),
                        event.description()
                ));
            }

            System.out.println(sb);
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
            
            StringBuilder sb = new StringBuilder();

            for(Participant participant : participants){
                sb.append(String.format(
                        "%s | Email : %s | Participant ID: %s",
                        participant.name(),participant.email(), participant.uuid()
                ));
            }

            System.out.println(sb);
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
                // handle
            }
            exchange.close();
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        //dbClient = new DbClient();

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
