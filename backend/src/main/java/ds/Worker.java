// Worker Class
// Communication protocol is based on HTTP 
package ds;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.naming.spi.DirStateFactory.Result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Worker
{
    private ArrayList<Room> rooms;                                                  // rooms array
    //private Results results;
    private String[] propertyNames = {"area", "date", "guests", "price", "stars"};  // array with common properties of Room and Filter 
    private static ServerSocket serverSocket;                                       // server socket 
    //private ArrayList<Integer> indexes;                                             // array to collect room indexes of rooms 
    //private static ArrayList<Filter> filters;

    // Default constructor
    Worker(int port) throws IOException
    {
        //Worker.filters = new ArrayList<>();                                 
        this.rooms = new ArrayList<>();                                     // rooms array initialization
        //results = new Results();
        Room room1 = new Room("Villa", null, 0, 0, 0, "Larisa", 0, null, null, null, true);
        Room room2 = new Room("HotelPoseidon", null, 0, 0, 0, "Lamia", 0, null, null, null, true);
        Room room3 = new Room("StefFarm", null, 0, 0, 0, "Lamia", 0, null, null, null, true);
        addRoom(room1);
        addRoom(room2);
        addRoom(room3);
        //System.out.println(getRooms().toString());
        serverSocket = new ServerSocket(port);                              // create socket
        System.out.println("Worker is listening on port " + port);
        while(true) {
            Socket connection = serverSocket.accept();                      // accepting incoming connection
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

    Worker()
    {
        this.rooms = new ArrayList<>();
    }

    //private static ArrayList<Filter> getFilters()
    //{
    //    return filters;
    //}

    // Rooms array getter
    private ArrayList<Room> getRooms() 
    {
        return rooms;
    }

    // Adds room in rooms array
    private void addRoom(Room room)
    {
        synchronized(room) {
            rooms.add(room);
        }   
    }
    
    // Prints rooms of worker's array
    private void printRooms()
    {
        for(Room room : rooms) {
            System.out.println(room.toString() + "DEBUG");
        }
    }

    // Deserializes json file to a filter object
    private Filter deserializeFilter(String json)
    {
        return new Gson().fromJson(json, Filter.class);
    }

    // Deserializes json file to a room object
    private Room deserializeRoom(String json)
    {
        return new Gson().fromJson(json, Room.class);
    }    

    // Checking if worker has a room according to the incoming filter
    private ArrayList<Integer> hasRoom(Filter filter) throws InterruptedException
    {   // Double flag 
        boolean result = false;            
        //boolean finalResult = false;
        ArrayList<Integer> indexes = new ArrayList<>();                    // indexes array initialization   
        if (filter.numNonZero() == 0)                // if Filter object has only null or 0 values on properties
        {
            result = false;
        }
        for (Room room: rooms) {
            result = false;     // for each room initialize result value to false
            if (filter == null && room == null) {   // both objects are null
                result = false;
            }
            if (filter == null || room == null) {
                result = false;                       // filter is null and room is not or the opposite
            }
            if (room.numNonZero() == 0)             // if Room object has only null or 0 values on properties
            {
                result = false;
            }    
            // Iterate through 5 common properties of Filter and Room object
            // and checking on the same property in each iteration
            for (String propertyName : propertyNames) {
                try {
                    Field fieldF = filter.getClass().getDeclaredField(propertyName);
                    Field fieldR = room.getClass().getDeclaredField(propertyName);
                    fieldF.setAccessible(true);
                    fieldR.setAccessible(true);
                    Object valueF = fieldF.get(filter);
                    Object valueR = fieldR.get(room);
                    //System.out.println(propertyName + ":" + valueF + " DEBUG");
                    //System.out.println(propertyName + ":" + valueR + " DEBUG");
    
                    // Both values are not null or 0, compare them
                    if (valueF != null && valueR != null && !valueF.equals(0) && !valueR.equals(0) && 
                        !valueF.equals(0.0) && !valueR.equals(0.0)) {
                        if (valueF.equals(valueR)) {
                            result = true;   // properties are equal
                        } else {
                            result = false; // properties are not equal
                        }
                    } else if(valueF == null || valueR == null || valueF.equals(0) || valueR.equals(0) || 
                            valueF.equals(0.0) || valueR.equals(0.0)) {
                        continue;   // if some property of Room object or Filter object is null or 0
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if(result) {
                indexes.add(rooms.indexOf(room));       // add index of iterated room to array
                //System.out.println(room.getArea());
                //finalResult = true;                     // set final result equal to true
            }
        }
        System.out.println("INDEXES: " + indexes + " Thread ID: " + Thread.currentThread().threadId());
        return indexes;
    }
    
    // Returns an array of rooms according to given filters and passes the filters' id to rooms
    private RoomResult map(ArrayList<Integer> indx,  Filter filter) throws InterruptedException
    {
        //ArrayList<Room> resultRooms = new ArrayList<>();        // initialize array
        Room resultRoom;
        RoomResult results = new RoomResult();        
        //synchronized(rooms) {
        if (!indx.isEmpty()) {                                  // if worker has a room with the given filter
            for(Integer index : indx) {
                System.out.println(indx.toString());
                //synchronized(rooms) {        
                    //results = new Results();
                    resultRoom = rooms.get(index).copy();           // copy room
                    //System.out.println(resultRoom);
                    results.addRoom(resultRoom);                    // add copy of room in the results array
                    //System.out.println(results.getRooms().toString() + " DEBUG!!! " + Thread.currentThread().threadId());
                    results.setId(filter.getId());                           // set id of the selected room equal to filter's id        
                    //System.out.println(results.getId() + " DEBUG!!! " + Thread.currentThread().threadId());
                //}                    // for room index in indexes array
            }
            //System.out.println("We found it!!");
        } else {
            System.out.println("No match!!");
            return null;
        }
    //}
        return results;
    }

    // Serialize results of worker's to Json 
    private String serializeResults(RoomResult resRooms)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonResults = gson.toJson(resRooms);
        return jsonResults;
    }
        
    // Set the room not available and does the booking
    private void book(String roomName)
    {
        for(Room room : rooms) {
            if(room.getRoomName() == roomName) {
                room.setAvailable(false);
                //System.out.println(room.getAvailable());
            }
            System.out.println(room.getRoomName() + " is " + room.getAvailable());
        }
    }

    private void giveReview(String roomName ,int star){
        for(Room room : rooms) {
            if(room.getRoomName() == roomName){     
                int numberof = room.getReviews();
                numberof ++ ;
                room.setReviews(numberof);
                int starof = room.getStars();
                starof = (starof + star)/numberof;
                room.setStars(starof);
            }
        }
    }


    // Opens worker's server side
    private void runServer(Socket connection) throws IOException, InterruptedException
    {
        //System.out.println("Thread id: " + Thread.currentThread().getId());
        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));      // get input stream in buffered reader
        OutputStream output = connection.getOutputStream();                                                 // get output stream from master's socket
        //synchronized(input) {
            String requestLine = input.readLine();                                                          // set request line 
            if (requestLine == null || requestLine.isEmpty()) {
                Thread.interrupted();  // kill thread
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
            consumeRemainingRequest(input);
            connection.close();                                                                                 // close socket    
        //}
	}

    // Handles requests for new room insertion
    private void handleNewRoomRequest(BufferedReader in, OutputStream out) throws IOException {
        String jsonRoom = extractBody(in);                                                                // extract json from request body
        Room room = deserializeRoom(jsonRoom);                                                              // create filter object from json input file
        System.out.println("->-> " + room.toString());
        addRoom(room);                                                                                  // add room to array
        //printRooms();
        System.out.println("Received filter data: " + jsonRoom);
        sendHttpResponse(out, 200, "OK", "{\"message\":\"New room added\"}",
                             "application/json");                                               // send response for succesfull http request
        System.out.println("Room added...");
    }
    // Handles requests for searching room
    private void handleSearchRoomRequest(BufferedReader in, OutputStream out) throws IOException, InterruptedException {
        String jsonFilter = extractBody(in);                                                                // extract json from request body
        Filter filter = deserializeFilter(jsonFilter);                                                      // create filter object from json input file
        System.out.println("Received request: " + filter.getId() + " with " + filter.toString() + " is Thread: " + Thread.currentThread().threadId());
        RoomResult resultRooms = map(hasRoom(filter), filter);                                          // get array with results for reducer
        String jsonResults = serializeResults(resultRooms);                                                 // serialize results to json
        System.out.println("->->->" + jsonResults);
        if (jsonResults != null && !jsonResults.trim().isEmpty()) {                                                                            // if json with results is null
            sendHttpResponse(out, 200, "OK", jsonResults
            , "application/json");                                      // send response for succesfull http request                        
        } else {
            sendHttpResponse(out, 404, "Not Found", "{\"message\":\"Room not found\"}"
            , "application/json");
            System.out.println("DEBUG");                                        // send response with results in json
        }
    }

    // Sends error message when the server is incapable of performing the request
    private static void sendNotImplementedResponse(OutputStream out) throws IOException {
        sendHttpResponse(out, 501, "Not Implemented", "", "text/plain");
    }

    // Builds and sends the http response
    private static void sendHttpResponse(OutputStream out, int statusCode, String statusMessage, String body, String contentType) throws IOException {
        String httpResponse = String.format("HTTP/1.1 %d %s\r\nContent-Type: %s\r\nContent-Length: %d\r\n\r\n%s", statusCode, statusMessage, 
                                            contentType, body.getBytes().length, body);                     // format of http header
        //System.out.println(httpResponse);   
        out.write(httpResponse.getBytes());
    }
    // Consume the remainig request...
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
    public static void main(String[] args) throws IOException{
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter port");
        new Worker(sc.nextInt());
        sc.close();
        /*  
        Room room1 = new Room("Villa", null, 0, 0, 0, "Larisa", 0, null, null, null, true);
        Room room2 = new Room("HotelPoseidon", null, 0, 0, 0, "Lamia", 0, null, null, null, true);
        Worker worker = new Worker();
        worker.addRoom(room1);
        worker.addRoom(room2);
        Filter f1 = new Filter();
        f1.setArea("Athens");
        //ArrayList<Room> results = worker.map(0, f1);
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //String jsonResults = gson.toJson(results);
        //System.out.println(jsonResults);
        //worker.book("HotelPoseidon");
        worker.giveReview("Villa",5);
        worker.giveReview("Villa", 3);
        System.out.println(room1.getStars());
        */
    }
}