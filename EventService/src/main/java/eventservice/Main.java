package eventservice;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.sql.SQLException;
import java.util.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.morphia.query.Sort;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.FindOptions;

public class Main {
    private final static String MONGO_URI = "mongodb+srv://%s:%s@%s/"
            .formatted(
                    System.getenv("MONGO_USER"),
                    System.getenv("MONGO_PASS"),
                    System.getenv("MONGO_URL")
            );
    private final static String DATABASE = "";
    final static Datastore datastore = Morphia.createDatastore(MongoClients.create(MONGO_URI), DATABASE);
    private static HttpServer server;
    private final static String HOSTNAME = "ec2-54-145-190-43.compute-1.amazonaws.com";
    private final static int PORT = 3001;
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
    private static class ListHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {
            List<Event> events = datastore.find(Event.class)
                    .iterator(new FindOptions().sort(Sort.descending("eventDateTime")))
                    .toList();
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
    private static class AddHandler implements HttpHandler {
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

                datastore.insert(event);
                System.out.println("Event created: " + event);
            } catch(NoSuchElementException e){
                fail(exchange, 400, "Must provide json in request body");
                return;
            } catch (Event.HandledIllegalValueException e) {
                fail(exchange, 400, "Failed to create event: " + e.getMessage());
                return;
            }
            sendResponse(exchange);
        }
    }

    public static void main(String[] args) throws IOException {
        datastore.getMapper().mapPackage("com.mongodb.morphia.entities");
        datastore.ensureIndexes();

        try {
            server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 0);
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            return;
        }
        
        server.createContext("/api/list-events",        new ListHandler());
        server.createContext("/api/event",              new AddHandler());

        server.start();
        System.out.println("Server started");
    }
}

