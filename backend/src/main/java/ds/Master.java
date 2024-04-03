// Master class
// Communication protocol is based on HTTP 
package ds;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import ds.JsonConverter.JsonUtils;
import com.google.gson.Gson;

public class Master
{
    private Request request;
    public static WorkerConfig workerConfig;        // workers configuration instance
    private static ServerSocket serverSocket;              // server socker 
	//private Socket connection = null;            // set connec null
    private static int port = 8000;                 // set port of master
    private static String hostname = "localhost";   // host name      
    // Master constructor
    Master() throws IOException
    {
        request = new Request();
        serverSocket = new ServerSocket(port);                           // create socket
        System.out.println("Master is listening on port " + port);
        while(true) {
            Socket connection = serverSocket.accept();
            new Thread(() -> {
                try {
                //synchronized(connection) {
                    runServer(connection);
                //}
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    // Hash function 
    public static int hashFunc(String roomName, int numOfWorkers) {
        int hashCode = roomName.hashCode();                         // hashing room's name
        int absHashCode = Math.abs(hashCode);                       // give absolute value
        int nodeID = absHashCode % 3;                               // node id inside boundries
        nodeID++;                                                   // starting from 1 to number of nodes
        return nodeID;
    }
    // Opens master's server side
    private void runServer(Socket connection) throws IOException, InterruptedException
    {
        System.out.println("Thread id: " + Thread.currentThread().getId());
        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));      // get input stream in buffered reader
        OutputStream output = connection.getOutputStream();                                                 // get output stream from master's socket

        String requestLine = input.readLine();                                                          // set request line 
        if (requestLine == null || requestLine.isEmpty()) {
            Thread.interrupted();       // kill thread running
        }
        // Header check
        if (requestLine.startsWith("POST /newRoom")) {               
            System.out.println("Received new room request...");
            handleNewRoomRequest(input, output);
        } else if (requestLine.startsWith("POST /searchRoom")) {
            //System.out.println("Received search room request...");
            handleSearchRoomRequest(input, output);
        } else {
            sendNotImplementedResponse(output);
        }
        //consumeRemainingRequest(input);
        connection.close();                                                                                 // close connection        
    }
    // Returns decided port of worker by hash function
    private static int getPort(int wID)
    {
        int resultPort = 0;
        for(Worker worker : workerConfig.workers) {     // for worker in workers array
            //System.out.println("WorkerID: " + worker.workerID);
            if(worker.workerID == wID) {           // if ids match
                resultPort = worker.port;                      // return port
            } else {
                //System.out.println("NO ID FOUND");
            }
        }
        return resultPort;   
    }
    // Handles requests for new room insertion
    private static void handleNewRoomRequest(BufferedReader in, OutputStream out) throws IOException {
        String jsonRoom = extractBody(in);                                              // extract json from request body
        System.out.println("Received new room data: " + jsonRoom);
        sendHttpResponse(out, 200, "OK",                        // send response for succesfull http request
                         "{\"message\":\"New room added\"}"
                            , "application/json");                          
        System.out.println("Room added...");

        // Client side of master
        Room room = new Gson().fromJson(jsonRoom, Room.class);                  // get room object from json
        //System.out.println(room.toString());
        int workerID = hashFunc(room.getRoomName(), workerConfig.nofWorkers);           // hash room name and get worker id to send request  
        int workerPort = getPort(workerID);                                             // get port of selected worker
        Socket socket = new Socket(hostname, workerPort);                               // open socket to worker's port
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);           // set output
        BufferedReader inputWorker = new BufferedReader(new InputStreamReader(socket.getInputStream()));    // buffer for inputs from worker 
        sendNewRoomRequest(output, jsonRoom);                                           // send request
        // Read the response
        String responseLine;
        while ((responseLine = inputWorker.readLine()) != null) {
            System.out.println(responseLine);
        }
        socket.close();                                                                                     // close socket
    }
    // Handles requests for searching room
    private void handleSearchRoomRequest(BufferedReader in, OutputStream out) throws IOException, InterruptedException {
        String jsonFilter = extractBody(in);                                // extract json from request body
        //System.out.println(jsonFilter);
        Filter filter = new Gson().fromJson(jsonFilter, Filter.class);  // create filter object from json 
        synchronized(request) {
            filter.setId(request.generateUniqueNumber());                        // master sets a unique id to filter object
            System.out.println("DEBUG!!!");
        }
        jsonFilter = new Gson().toJson(filter);                                  // convert filter object back to json
        //System.out.println("Received filter data: " + filter.toString());
        System.out.println("Received request: " + filter.getId() + " with " + filter.toString());
        //Thread.sleep(2000);
 
        // Client side of master
        for(int i = 0; i < 3; i++) {                         // for each worker configured
            Socket socket = new Socket(hostname, workerConfig.workers.get(i).port);                               // open socket to worker's port
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);           // set output
            BufferedReader inputWorker = new BufferedReader(new InputStreamReader(socket.getInputStream()));    // buffer for inputs from worker            
            sendSearchRoomRequest(output, jsonFilter);                                          // send request
            // Read the response
            String responseLine;
            while ((responseLine = inputWorker.readLine()) != null) {
                System.out.println(responseLine);
            }
            //consumeRemainingRequest(inputWorker);
            socket.close();                                                                                     // close socket                
        }
        sendHttpResponse(out, 200, "OK", "{\"message\":\"Search room completed\"}"
                        , "application/json");                                                   // send response for succesfull http request
        //System.out.println("Filter added...");
        //System.out.println("+-----------------------------+");

    }

    // Sends http request for searching room to worker
    private static void sendSearchRoomRequest(PrintWriter out, String jsonBody) {
        out.println("POST /searchRoom HTTP/1.1");
        out.println("Host: localhost");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonBody.length());
        out.println("Connection: close");
        out.println();
        out.println(jsonBody);
    }

    // Sends http request for adding room to worker
    private static void sendNewRoomRequest(PrintWriter out, String jsonBody) {
        out.println("POST /newRoom HTTP/1.1");
        out.println("Host: localhost");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonBody.length());
        out.println("Connection: close");
        out.println();
        out.println(jsonBody);
    }
    // Sends error message when the server is incapable of performing the request
    private static void sendNotImplementedResponse(OutputStream out) throws IOException {
        sendHttpResponse(out, 501, "Not Implemented", "", "text/plain");
    }

    // Builds and sends the http response
    private static void sendHttpResponse(OutputStream out, int statusCode, String statusMessage, String body, String contentType) throws IOException {
        String httpResponse = String.format("HTTP/1.1 %d %s\r\nContent-Type: %s\r\nContent-Length: %d\r\n\r\n%s", statusCode, statusMessage, 
                                            contentType, body.getBytes().length, body); // format of http header
        //System.out.println(httpResponse);   
        out.write(httpResponse.getBytes());
    }

    private static void consumeRemainingRequest(BufferedReader in) throws IOException {
        while (in.ready()) {
            in.readLine();
        }
    }
    // Extracts body from http search room request
    private static String extractBody(BufferedReader in) throws IOException 
    {
    StringBuilder requestBody = new StringBuilder();    // build the body of request into string
    String line;                                        // represents each line of http header
    // Extract content length
    int contentLength = 0;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.substring("content-length:".length()).trim());
            }
        }    
        // Read the body
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            requestBody.append(new String(buffer));
        }
        return requestBody.toString();
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
    public static void main(String[] args) throws IOException {
        //if(args.length < 1) {
        //    System.out.println("Usage: java Master.java <json_path>");
        //    return;
        //}
        //System.out.println(workerConfig.toString()); 
        String filepath = JsonUtils.readFileToString("C:\\Users\\sotir\\DistributedSystems\\workers.json"); // read file from args and convert it to string
        Gson gson = new Gson();
        workerConfig = gson.fromJson(filepath, WorkerConfig.class);    // create worker config object from json
        new Master();
    }
}