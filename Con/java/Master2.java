import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

public class Master {
    public static void main(String[] args) throws Exception {
        // Set up the HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Create a context for the server
        server.createContext("/", new MyHandler());

        // Start the server
        server.start();

        System.out.println("Server started on port 8080...");
    }

    // Define the handler for incoming requests
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello, this is Master's HTTP server!";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }

    static class MasterResponseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the response body
            String responseBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).lines()
                    .collect(Collectors.joining("\n"));

            // Print the received response
            System.out.println("Received response from Reducer: " + responseBody);

            // You can process the response from Reducer here as needed

            // Send a response to Reducer (optional)
            String response = "Response received successfully by Master.";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}