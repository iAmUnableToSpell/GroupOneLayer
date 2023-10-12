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
import java.util.*;

public class Main {
    private static HttpServer server;
    private static DbClient dbClient;

    private final static String HOSTNAME = "ec2-54-145-190-43.compute-1.amazonaws.com";
    private final static int PORT = 6969;

    private static Optional<JSONObject> readJSONRequest(HttpExchange exchange) {
        InputStreamReader r = new InputStreamReader(exchange.getRequestBody());
        JSONParser parser = new JSONParser();
        try {
            return Optional.of((JSONObject) parser.parse(r));
        } catch (IOException | ParseException e) {
            // TODO: handle
            return Optional.empty();
        }
    }

    private static void sendJSONResponse(HttpExchange exchange, JSONObject response) {
        try {
            exchange.sendResponseHeaders(200, 0);
            var output = exchange.getResponseBody();
	    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
	    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
	    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "OPTIONS,POST,GET");  
            output.write(response.toJSONString().getBytes());
        } catch (IOException e) {
            // TODO: handle
        }
        exchange.close();
    }

    private static void sendResponse(HttpExchange exchange) {
        sendJSONResponse(exchange, new JSONObject());
    }

    private static void fail(HttpExchange exchange, int statusCode, String message){
        System.err.println(message);
        try {
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("error", message);
            JSONObject response = new JSONObject(responseMap);
            exchange.sendResponseHeaders(statusCode, 0);
	    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
	    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
	    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "OPTIONS,POST,GET");  
	    var output = exchange.getResponseBody();
            output.write(response.toJSONString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        exchange.close();
    }

    private static class EventRequest implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                JSONObject json = readJSONRequest(exchange).orElseThrow();

                String date         = (String) json.get("date");
                String time         = (String) json.get("time");
                String title        = (String) json.get("title");
                String description  = (String) json.get("desc");
                String hostEmail    = (String) json.get("email");
                String eventID      = (String) json.get("uuid");

                Event event = Event.create(eventID, date, time, title, description, hostEmail);
                System.out.println(event);
                dbClient.addEvent(event);

                System.out.println("Event created: " + event);
            } catch(NoSuchElementException e){
                fail(exchange, 400, "Must provide json in request body");
                return;
            } catch (Event.HandledIllegalValueException | SQLException e) {
                fail(exchange, 400, "Failed to create event: " + e.getMessage());
                return;
            }
            sendResponse(exchange);
        }
    }

    private static class ParticipantRequest implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                JSONObject json = readJSONRequest(exchange).orElseThrow();

                String eventID = (String) json.get("eventID");
                String name = (String) json.get("name");
                String email = (String) json.get("email");
                String participantID = (String) json.get("participantID");

                Participant participant = Participant.create(participantID, eventID, name, email);
                dbClient.addParticipant(participant);

                System.out.println("Participant created: " + participant);

                sendResponse(exchange);
            } catch(NoSuchElementException e){
                fail(exchange, 400, "Must provide json in request body");
            } catch (Event.HandledIllegalValueException | SQLException e) {
                fail(exchange, 400, "Failed to create participant: " + e.getMessage());
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
                fail(exchange, 500, "Failed to retrieve events: " + e.getMessage());
                return;
            }

            JSONArray eventList = new JSONArray();
            events.forEach(
                    event ->
                        eventList.add(
                            new JSONObject(Map.of(
                                    "date", event.eventDateTime().toLocalDate(),
                                    "time", event.eventDateTime().toLocalTime(),
                                    "title", event.title(),
                                    "desc", event.description(),
                                    "email", event.hEmail(),
                                    "uuid", event.uuid()
                            ))
                    )
            );
            sendJSONResponse(exchange, new JSONObject(Map.of("events", eventList)));
        }
    }

    private static class ListParticipantsRequest implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            JSONObject json;

            String eventID;

            List<Participant> participants;
            try {
                json = readJSONRequest(exchange).orElseThrow();
                eventID = (String) json.get("eventID");
                participants = dbClient.getParticipants(eventID);
            } catch(NoSuchElementException e){
                fail(exchange, 400, "Must provide json in request body");
                return;
            } catch (SQLException e) {
                fail(exchange, 400, "Failed to retrieve participants: " + e.getMessage());
                return;
            }
            
            JSONArray participantList = new JSONArray();
            participants.forEach(
                    participant ->
                            participantList.add(
                                    new JSONObject(Map.of(
                                            "name", participant.name(),
                                            "email", participant.email(),
                                            "eventID", participant.eventId(),
                                            "uuid", participant.uuid()
                                    ))
                            )
            );


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
            server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 0);
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            return;
        }
        
        server.createContext("/api/test",               new TestRequest());
        server.createContext("/api/list-events",        new ListEventsRequest());
        server.createContext("/api/list-participants",  new ListParticipantsRequest());
        server.createContext("/api/event",              new EventRequest());
        server.createContext("/api/participant",        new ParticipantRequest());

        server.start();
        System.out.println("Server started");
    }
}
