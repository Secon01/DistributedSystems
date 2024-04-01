// Master class
// Communication protocol is based on HTTP 
package ds;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import ds.JsonConverter.JsonUtils;
import com.google.gson.Gson;

public class Master 
{
    private static int requestID = 0;           // id for request
    public static WorkerConfig workerConfig;  // workers configuration instance
    private ServerSocket serverSocket;
	private Socket socket = null;
    private static int port = 8000;
    // Master constructor
    Master()
    {
    }

    // Give unique number in order in the next request
    public synchronized static int generateUniqueNumber() {
        return requestID++;
    }

    // Opens master's server side
    private void openServer() throws IOException
    {
		serverSocket = new ServerSocket(port, 10);
        System.out.println("Master is listening on port " + port);

		while (true) {
			socket = serverSocket.accept();
            System.out.println(socket.getInputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));   //get input stream in buffered reader
            OutputStream output = socket.getOutputStream();    // get output stream from master's socket

            String requestLine = input.readLine();             // set request line 
            if (requestLine == null || requestLine.isEmpty()) continue;

            if (requestLine.startsWith("POST /newRoom")) {
                System.out.println("Received new room request...");
                handleNewRoomRequest(input, output);
            } else if (requestLine.startsWith("POST /searchRoom")) {
                System.out.println("Received search room request...");
                handleSearchRoomRequest(input, output);
            } else {
                sendNotImplementedResponse(output);
            }
            consumeRemainingRequest(input);
		}
    }

    // Handles requests for new room insertion
    private static void handleNewRoomRequest(BufferedReader in, OutputStream out) throws IOException {
        StringBuilder requestBody = new StringBuilder();    // build the body of request into string
        String roomJson;
        while (!(roomJson = in.readLine()).isEmpty()) {
            requestBody.append(roomJson);
        }
        Room room = new Gson().fromJson(roomJson, Room.class);  // create room object from json input file
        System.out.println(room.toString());

        System.out.println("Received new room data: " + requestBody);
        sendHttpResponse(out, 200, "OK", "{\"message\":\"New room added\"}", "application/json");
        System.out.println("Room added...");
    }

    // Handles requests for searching room
    private static void handleSearchRoomRequest(BufferedReader in, OutputStream out) throws IOException {
        StringBuilder requestBody = new StringBuilder();    // build the body of request into string
        String filterJson;
        while (!(filterJson = in.readLine()).isEmpty()) {
            requestBody.append(filterJson);
        }
        filterJson = in.readLine().repeat(1);   // repeat readline() one time in order to read json body
        System.out.println(filterJson);
        Filter filter = new Gson().fromJson(filterJson, Filter.class);  // create filter object from json input file
        System.out.println("->-> " + filter.toString());

        System.out.println("Received filter data: " + requestBody);
        sendHttpResponse(out, 200, "OK", "{\"message\":\"New room added\"}", "application/json");
        System.out.println("Room added...");
    }
    

    // Sends error message when the server is incapable of performing the request
    private static void sendNotImplementedResponse(OutputStream out) throws IOException {
        sendHttpResponse(out, 501, "Not Implemented", "", "text/plain");
    }

    // Builds and sends the http response
    private static void sendHttpResponse(OutputStream out, int statusCode, String statusMessage, String body, String contentType) throws IOException {
        String httpResponse = String.format("HTTP/1.1 %d %s\r\nContent-Type: %s\r\nContent-Length: %d\r\n\r\n%s", statusCode, statusMessage, 
                                            contentType, body.getBytes().length, body); // format of http header
        System.out.println(httpResponse);   
        out.write(httpResponse.getBytes());
    }

    private static void consumeRemainingRequest(BufferedReader in) throws IOException {
        while (in.ready()) {
            in.readLine();
        }
    }

    // Inner class worker
    private class Worker
    {
        private int workerID;           // ID of worker
        private String ipAddress;       // worker's ip address
        private int port;               // worker's port

        public String toString() 
        {
            return "Worker: " + workerID + 
                    " ipAddress='" + ipAddress + '\'' +
                    ", port=" + port;
        }
    }
    // Inner class worker configuration
    private class WorkerConfig
    {
        private int nofWorkers;                 // number of workers
        private ArrayList<Worker> workers;      // array of workers

        public String toString() {
            return "WorkerConfig: " +
                    "nofWorkers=" + nofWorkers +
                    ", workers=" + workers;
        }
    }

    // Main method
    public static void main(String[] args) throws IOException {
        //if(args.length < 1) {
        //    System.out.println("Usage: java Master.java <json_path>");
        //    return;
        //}

        String filepath = JsonUtils.readFileToString("/home/secon/Documents/GitHub/DistributedSystems/workers.json");  // read file from args and convert it to string
        Gson gson = new Gson();
        workerConfig = gson.fromJson(filepath, WorkerConfig.class);    // create worker config object from json
        //System.out.println(workerConfig.toString());        
        
        new Master().openServer();
    }
}