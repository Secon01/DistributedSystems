import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Reducer {
    public static void main(String[] args) throws Exception {
        // Set up the HTTP server on port 9090
        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);

        // Create a context for the server
        server.createContext("/", new ReducerHandler());

        // Start the server
        server.start();

        System.out.println("Reducer server started on port 9090...");
    }

    // Define the handler for incoming requests
    static class ReducerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the request body
            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).lines()
                    .collect(Collectors.joining("\n"));

            // Print the received data
            System.out.println("Received result from Worker: " + requestBody);

            // Process the result (You can define your logic here)
            String processedResult = "Processed result: " + requestBody;

            // Send a response to the Worker
            exchange.sendResponseHeaders(200, processedResult.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(processedResult.getBytes());
            os.close();

            // Send a response to the Master
            sendResponseToMaster(processedResult);
        }

        private void sendResponseToMaster(String processedResult) {
            try {
                // Connect to the Master server
                URL masterUrl = new URL("http://localhost:8080/response");
                HttpURLConnection connection = (HttpURLConnection) masterUrl.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Send the processed result
                OutputStream os = connection.getOutputStream();
                os.write(processedResult.getBytes());
                os.flush();
                os.close();

                // Get response from Master (optional)
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Response from Master: " + response.toString());

                // Disconnect
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
