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
import java.time.LocalDate;
import java.util.ArrayList;
import ds.JsonConverter.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Master
{
    public static WorkerConfig workerConfig;        // workers configuration instance
    private static ServerSocket serverSocket;       // server socker 
    private static int port = 8000;                 // set port of master
    private static String hostname = "localhost";   // host name      
    private static int requestID = 0;               // id for request
    // Master constructor
    Master() throws IOException
    {
        serverSocket = new ServerSocket(port);                           // create socket
        System.out.println("Master is listening on port " + port);
        while(true) {
            Socket connection = serverSocket.accept();
            new Thread(() -> {
                try {
                    runServer(connection);                              // run server side of master
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    // Give unique number in order in the next request
    private synchronized int getUniqueNumber() {
        return requestID++;               
    }

    // Sets unique id to each request with filter and to reducer's id
    private void setRequestIDFilter(Filter filter, Reducer reducer)
    {
        //synchronized(req) {
            filter.setId(getUniqueNumber());
            reducer.setCurrentID(filter.getId());
        //}
    }

    // Sets unique id to each request with room and to reducer's id
    private void setRequestIDRoom(Room room, Reducer reducer)
    {
        room.setId(getUniqueNumber());          
        reducer.setCurrentID(room.getId());
    }

    // Sets unique id to each request with rooms
    private void setUniqueBookingID(Request req, Reducer reducer)
    {
        req.setId(getUniqueNumber());
        reducer.setCurrentID(req.getId());
    }

    // Hash function 
    private static int hashFunc(String roomName, int numOfWorkers) {
        int hashCode = roomName.hashCode();                         // hashing room's name
        int absHashCode = Math.abs(hashCode);                       // give absolute value
        int nodeID = absHashCode % 3;                               // node id inside boundries
        nodeID++;                                                   // starting from 1 to number of nodes
        return nodeID;
    }

    // Deserializes json to a filter object
    private Filter deserializeFilter(String json)
    {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();
        return gson.fromJson(json, Filter.class);
    }

    // Deserializes json to a room object
    private Room deserializeRoom(String json)
    {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();
        return gson.fromJson(json, Room.class);
    }    
    // Serializes room object to json  
    private String serializeRoom(Room room)
    {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .create();
        return gson.toJson(room);
    }    

    // Serializes filter object to json 
    private String serializeFilter(Filter filter)
    {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .create();
        return gson.toJson(filter);
    }

    // Deserialize json to results  
    private RoomResult deserializeResults(String json)
    {
        Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
        .create();
        return gson.fromJson(json, RoomResult.class);
    }

    // Serializes reducer object to json 
    private String serializeReducer(Reducer reducer)
    {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .create();
        return gson.toJson(reducer);
    }

    // Deserializes json file to an integer (manager ID)
    private int deserializeManagerID(String json)
    {
        return new Gson().fromJson(json, Integer.class);
    }

    // Serializes request object to json 
    private String serializeRequest(Request request)
    {
        return new Gson().toJson(request);
    }

    // Opens master's server side
    private void runServer(Socket connection) throws IOException, InterruptedException
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));      // get input stream in buffered reader
        OutputStream output = connection.getOutputStream();                                                 // get output stream from master's socket

        String requestLine = input.readLine();                                                          // set request line 
        if (requestLine == null || requestLine.isEmpty()) {
            Thread.interrupted();       // kill thread running
        }
        // Header check
        if (requestLine.startsWith("POST /newRoom")) {               
            //System.out.println("Received new room request...");
            handleNewRoomRequest(input, output);
        } else if (requestLine.startsWith("POST /searchRoom")) {
            //System.out.println("Received search room request...");
            handleSearchRoomRequest(input, output);
        } else if(requestLine.startsWith("POST /bookRoom")) {
            System.out.println("Received book room request...");
            handleBookRoomRequest(input, output);
        } else if(requestLine.startsWith("GET /getBooking")) {
            //System.out.println("Received get booking request...");
            handleGetBookRequest(input, output);
        } else if(requestLine.startsWith("GET /getAreaBooking")) {
            System.out.println("Received get area booking request...");
            handleAreaBookRequest(input, output);
        } else {
            sendNotImplementedResponse(output);
        }
        consumeRemainingRequest(input);
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
    private void handleNewRoomRequest(BufferedReader in, OutputStream out) throws IOException {
        String jsonRoom = extractBody(in);                                              // extract json from request body
        Room room = deserializeRoom(jsonRoom);                                          // get room object from json
        System.out.println("Received request: " + room.getId() + " is Thread: " + Thread.currentThread().threadId()
                            + " with: " + "\n" + room.toString());
        // Client side of master                                    
        int workerID = hashFunc(room.getRoomName(), workerConfig.nofWorkers);           // hash room name and get worker id to send request  
        int workerPort = getPort(workerID);                                             // get port of selected worker
        new Thread(() -> {
            Socket socket = null;
            try {
                socket = new Socket(hostname, workerPort);                              // open socket to worker's port
            } catch (IOException e) {
                e.printStackTrace();
            }                               
            PrintWriter output = null;
            try {
                output = new PrintWriter(socket.getOutputStream(), true);       // set output
            } catch (IOException e) {
                e.printStackTrace();
            } 
            try (BufferedReader inputWorker = new BufferedReader
                                        (new InputStreamReader(socket.getInputStream()))) {
                sendNewRoomRequest(output, jsonRoom);                                   // send request
                // Read the response
                String responseLine;
                while ((responseLine = inputWorker.readLine()) != null) {
                    System.out.println(responseLine);                                   // print response message
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }                                                                           // close socket
        }).start();   
        sendHttpResponse(out, 200, "OK",                        // send response for succesfull http request
        "{\"message\":\"New room added\"}"
           , "application/json");                          
    }
    // Handles requests for searching room
    private void handleSearchRoomRequest(BufferedReader in, OutputStream out) throws IOException, InterruptedException {
        Reducer reducer = new Reducer();                                            // create reducer object
        String jsonFilter = extractBody(in);                                        // extract json from http request body
        Filter filter = deserializeFilter(jsonFilter);                              // create filter object from json 
        setRequestIDFilter(filter, reducer);                                     // set a unique id to filter and reducer object
        jsonFilter = serializeFilter(filter);                                       // convert filter object back to json
        System.out.println("Received request: " + filter.getId() + 
                            " with " + filter.toString() + " is Thread: " + Thread.currentThread().threadId());
        // Client side of master                            
        final String json = jsonFilter;                                     // make it final because of try/catch
        for(Worker worker : workerConfig.workers) {                         // for each worker configured
            Thread work  = new Thread(() -> {
                Socket socket = null;
                try {
                    socket = new Socket(hostname, worker.port);             // open socket to worker's port
                } catch (IOException e) {
                    e.printStackTrace();
                }                                 
                PrintWriter outputWorker = null;                            // set output to worker
                try {
                    outputWorker = new PrintWriter(socket.getOutputStream(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }                                             
                BufferedReader inputWorker = null;                          // buffer for inputs from worker
                try {
                    inputWorker = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }                    
                sendSearchRoomRequest(outputWorker,json);                   // send request to each worker
                // Read the response
                String responseBody = null;                                
                try {
                    responseBody = extractBody(inputWorker);                // extract json with results 
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RoomResult results =  deserializeResults(responseBody);     // deserialize json with searching results
                try {
                    if(results != null) {
                        reducer.reduce(results.getId(), results);               // reduce results with same id
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } 
                try {
                    consumeRemainingRequest(inputWorker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    socket.close();                                         // close socket
                } catch (IOException e) {
                    e.printStackTrace();
                }                                                                                
            });
            work.start();
            work.join();
        }
        if(reducer.getResults().isEmpty()) {
            try {
                // Send response for succesfull http request
                sendHttpResponse(out, 404, "Not Found", "{\"message\":\"Bookings not found\"}"
                , "application/json");                  // send response for unsuccessful http request
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }                                                    
        } else {
            try {
                // Send response for succesfull http request
                sendHttpResponse(out, 200, "OK", 
                serializeReducer(reducer) , "application/json");
            } catch (IOException e) {
                e.printStackTrace();
            }                                                    
        }
    }

    // Handles requests for booking room
    private void handleBookRoomRequest(BufferedReader in, OutputStream out) throws IOException, InterruptedException 
    {
        String jsonRoomName = extractBody(in);                                            // extract json with room name from request body
        String roomName = new Gson().fromJson(jsonRoomName, String.class);       // create string with room name from json
        int workerID = hashFunc(roomName, workerConfig.nofWorkers);                     // hash room name and get worker id to send request  
        int workerPort = getPort(workerID);                                             // get port of selected worker
        Thread book  = new Thread(() -> {
            Socket socket = null;
            try {
                socket = new Socket(hostname, workerPort);                              // open socket to worker's port
            } catch (IOException e) {
                e.printStackTrace();
            }                               
            PrintWriter outputWorker = null;
            try {
                outputWorker = new PrintWriter(socket.getOutputStream(), true);       // set output
            } catch (IOException e) {
                e.printStackTrace();
            } 
            try (BufferedReader inputWorker = new BufferedReader
                                        (new InputStreamReader(socket.getInputStream()))) {
                sendBookRoomRequest(outputWorker, jsonRoomName);                                   // send request
                // Read the response
                String responseLine;
                while ((responseLine = inputWorker.readLine()) != null) {
                    System.out.println(responseLine);                                       // print response message
                    if(responseLine.startsWith("HTTP/1.1 409 Conflict")) {
                        sendHttpResponse(out, 409, "Conflict", "{\"message\":\"Room already booked\"}", 
                        "application/json");                                    // send response for unsuccessful http request            
                    } else if(responseLine.startsWith("HTTP/1.1 200 OK")) {
                        sendHttpResponse(out, 200, "OK", "{\"message\":\"Room booked\"}",
                        "application/json");                                    // send response for successful http request             
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }                                                                           // close socket
        });
        book.start();
        book.join();
    }
    // Handles requests for getting bookings
    private void handleGetBookRequest(BufferedReader in, OutputStream out) throws IOException, InterruptedException 
    {
        Reducer reducer = new Reducer();                                            // create reducer object
        String jsonMangerID = extractBody(in);                                      // extract json with managerID from request body
        int managerID = deserializeManagerID(jsonMangerID);                         // get manager ID from json 
        Request request = new Request();                                            // create request object
        request.setManagerID(managerID);                                            // set manager ID field of request object
        setUniqueBookingID(request, reducer);                                       // set unique ID to request and reducer object 
        String jsonRequest = serializeRequest(request);                             // create json from request object
        System.out.println("Received request: " + request.getId() + " is Thread: " + Thread.currentThread().threadId()
                            + " with: " + "\n" + "Manager ID: " + request.getManagerID());
        for(Worker worker : workerConfig.workers) {                                 // for each worker configured
            Thread work = new Thread(() -> { 
                Socket socket = null;
                try {
                    socket = new Socket(hostname, worker.port);                     // open socket to worker's port
                } catch (IOException e) {
                    e.printStackTrace();
                }                                 
                PrintWriter outputWorker = null;                                    // set output to worker
                try {
                    outputWorker = new PrintWriter(socket.getOutputStream(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }                                             
                BufferedReader inputWorker = null;                                  // buffer for inputs from worker
                try {
                    inputWorker = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendGetBookRequest(outputWorker, jsonRequest);                      // send request to get bookings
                // Read the response
                String responseBody = null;                                         // body of http response 
                try {
                    responseBody = extractBody(inputWorker);                        // extract json with results 
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RoomResult results =  deserializeResults(responseBody);     // deserialize json with searching results
                try {
                    if(results != null) {
                        reducer.reduce(results.getId(), results);               // reduce results with same id
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    consumeRemainingRequest(inputWorker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    socket.close();                                         // close socket
                } catch (IOException e) {
                    e.printStackTrace();
                }                                                                                
            });
            work.start();                                                   // start thread
            work.join();                                                    // call external thread to wait for inside thread to finish
        }
        if(reducer.getResults().isEmpty()) {
            try {
                // Send response for succesfull http request
                sendHttpResponse(out, 404, "Not Found", "{\"message\":\"Bookings not found\"}"
                , "application/json");                  
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }                                                    
        } else {
            try {
                // Send response for succesfull http request
                sendHttpResponse(out, 200, "OK", 
                serializeReducer(reducer) , "application/json");
            } catch (IOException e) {
                e.printStackTrace();
            }                                                    
        }
    }
    // Handles requests for getting bookings by area in given date range
    private void handleAreaBookRequest(BufferedReader in, OutputStream out) throws IOException, InterruptedException
    {
        Reducer reducer = new Reducer();                                            // create reducer object
        String jsonRoom = extractBody(in);                                          // extract json of room with given data range from http request body
        //System.out.println(jsonRoom);
        Room room = deserializeRoom(jsonRoom);                                      // get room object from json 
        setRequestIDRoom(room, reducer);                                            // set unique ID to room and reducer object
        String jsonDataRange = serializeRoom(room);                                 // create json of room with given data range from room object
        System.out.println("Received request: " + room.getId()  + " is Thread: " + Thread.currentThread().threadId()
                            + " with: " + "\n" +  "Date range: " + room.getDateRange());
        for(Worker worker : workerConfig.workers) {                                 // for each worker configured
            Thread work = new Thread(() -> {                                        // create thread 
                Socket socket = null;
                try {
                    socket = new Socket(hostname, worker.port);                     // open socket to worker's port
                } catch (IOException e) {
                    e.printStackTrace();
                }                                 
                PrintWriter outputWorker = null;                                    // set output to worker
                try {
                    outputWorker = new PrintWriter(socket.getOutputStream(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }                                             
                BufferedReader inputWorker = null;                                  // buffer for inputs from worker
                try {
                    inputWorker = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendAreaBookRequest(outputWorker, jsonDataRange);                   // send request with given date range
                // Read the response
                String responseBody = null;                                         // body of http response 
                try {
                    responseBody = extractBody(inputWorker);                        // extract json with results 
                    //System.out.println(responseBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RoomResult results =  deserializeResults(responseBody);     // deserialize json with searching results
                //System.out.println("Request ID: " +  results.getId());
                //results.printRooms();
                try {
                    if(results != null) {
                        reducer.reduce(results.getId(), results);               // reduce results with same id
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    consumeRemainingRequest(inputWorker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    socket.close();                                         // close socket
                } catch (IOException e) {
                    e.printStackTrace();
                }                                                                                
            });
            work.start();
            work.join();
        }           
        if(reducer.getResults().isEmpty()) {                            // if there are no results
            try {
                // Send response for unsuccesfull http request
                sendHttpResponse(out, 404, "Not Found", "{\"message\":\"Bookings not found\"}"
                , "application/json");          
            } catch (IOException e) {
                e.printStackTrace();
            }                                                    
        } else {
            try {
                // Send response for succesfull http request
                sendHttpResponse(out, 200, "OK", 
                serializeReducer(reducer) , "application/json");
            } catch (IOException e) {
                e.printStackTrace();
            }                                                    
        }
    }

    // Sends http request for searching a room to worker
    private static void sendSearchRoomRequest(PrintWriter out, String jsonBody) {
        out.println("POST /searchRoom HTTP/1.1");
        out.println("Host: localhost");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonBody.length());
        out.println("Connection: close");
        out.println();
        out.println(jsonBody);
    }

    // Sends http request for adding a room to worker
    private static void sendNewRoomRequest(PrintWriter out, String jsonBody) {
        out.println("POST /newRoom HTTP/1.1");
        out.println("Host: localhost");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonBody.length());
        out.println("Connection: close");
        out.println();
        out.println(jsonBody);
    }

    // Sends http request for booking a room to worker
    private static void sendBookRoomRequest(PrintWriter out, String jsonBody) {
        out.println("POST /bookRoom HTTP/1.1");
        out.println("Host: localhost");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonBody.length());
        out.println("Connection: close");
        out.println();
        out.println(jsonBody);
    }
    // Sends request to get manager's bookings
    private void sendGetBookRequest(PrintWriter out, String jsonBody) {
        out.println("GET /getBooking HTTP/1.1");
        out.println("Host: localhost");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonBody.length());
        out.println("Connection: close");
        out.println();
        out.println(jsonBody);
    }
    // Sends request to get bookings by area in a given date range      
    private void sendAreaBookRequest(PrintWriter out, String jsonBody) {
        out.println("GET /getAreaBooking HTTP/1.1");
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
            System.out.println(line);                   // print http response
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
        String filepath = JsonUtils.readFileToString("/home/secon/Documents/GitHub/DistributedSystems/workers.json"); // read file from args and convert it to string
        Gson gson = new Gson();
        workerConfig = gson.fromJson(filepath, WorkerConfig.class);    // create worker config object from json
        new Master();
    }
}