package apigateway;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {
    private static HttpServer server;
    private final static String HOSTNAME = "";
    private final static int PORT = 3000;

    private static HttpClient client;

    private static class RequestHandler implements HttpHandler {
        private URI uri; 

        private RequestHandler(String uri) {
            this.uri = URI.create(uri);
        }

        public static RequestHandler routeTo(String uri) {
            return new RequestHandler(uri);
        }

        @Override
        public void handle(HttpExchange exchange) {
            var requestBuilder = HttpRequest.newBuilder(uri);
            exchange.getRequestHeaders().forEach((name, values) -> values.forEach(value -> requestBuilder.header(name, value)));
            requestBuilder.method(exchange.getRequestMethod(), BodyPublishers.ofInputStream(() -> exchange.getRequestBody()));
            requestBuilder.build();
        }
    }

    public static void main(String[] args) throws IOException {
        client = HttpClient.newHttpClient();

        try {
            server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 0);
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            return;
        }
        
        server.createContext("/api/list-events",        RequestHandler.routeTo(/* TODO */));
        server.createContext("/api/list-participants",  RequestHandler.routeTo(/* TODO */));
        server.createContext("/api/event",              RequestHandler.routeTo(/* TODO */));
        server.createContext("/api/participant",        RequestHandler.routeTo(/* TODO */));

        server.start();
        System.out.println("Server started");
    }
}
