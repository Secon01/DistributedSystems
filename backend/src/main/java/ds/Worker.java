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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Worker
{
    private ArrayList<Room> rooms;                                                  // rooms array
    private Room room;                                                              // room instance
    private static Filter filter;                                                   // filter instance
    private String[] propertyNames = {"area", "date", "guests", "price", "stars"};  // array with common properties of Room and Filter 
    private static ServerSocket serverSocket;                                       // server socket 
    private ArrayList<Integer> indexes;                                             // array to collect room indexes of rooms 
    private static ArrayList<Filter> filters;

    // Default constructor
    Worker(int port) throws IOException
    {
        Worker.filters = new ArrayList<>();                                 
        this.rooms = new ArrayList<>();                                     // rooms array initialization
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

    private static ArrayList<Filter> getFilters()
    {
        return filters;
    }

    // Rooms array getter
    private ArrayList<Room> getRooms() 
    {
        return rooms;
    }
    
    // Deserializes json file to a room object 
    private Room deserializeRoom(String jsonString)
    {
        if(jsonString != null) {
            //System.out.println("JSON File Content:\n" + jsonString);
            // Convert the JSON string into an object
            Gson gson = new Gson();                                 
            room = gson.fromJson(jsonString, Room.class);
            //System.out.println("Object created from JSON: " + room);
        } else {
            System.out.println("Failed to read the JSON file.");
        }
        return room;
    }

    // Adds room in rooms array
    private void addRoom(Room room)
    {   
        rooms.add(room);
    }

    // Deserializes json file to a filter object
    private static Filter deserializeFilter(String jsonString)
    {
        if(jsonString != null) {
            //System.out.println("JSON File Content:\n" + jsonString);
            // Convert the JSON string into an object
            Gson gson = new Gson();                                 
            filter = gson.fromJson(jsonString, Filter.class);
            //System.out.println("Object created from JSON: " + filter);
        } else {
            System.out.println("Failed to read the JSON file.");
        }
        return filter;
    }

    // Checking if worker has a room according to the incoming filter
    private boolean hasRoom(Filter filter)
    {   // Double flag 
        boolean result = false;            
        boolean finalResult = false;
        indexes = new ArrayList<>();                    // indexes array initializatio   
        if (filter.numNonZero() == 0)                // if Filter object has only null or 0 values on properties
        {
            result = false;
        }
        for (Room room: rooms) {
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
                            //System.out.println("NO MATCHES");
                            result = true;   // properties are equal
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
                finalResult = true;                     // set final result equal to true
            }
        }
        return finalResult;
    }
    
    // Returns an array of rooms according to given filters and passes the filters' id to rooms
    private ArrayList<Room> map(int id , Filter filter)
    {
        ArrayList<Room> resultRooms = new ArrayList<>();        // initialize array
        Room resultRoom;
        if (hasRoom(filter)) {                                  // if worker has a room with the given filters
            for(Integer index : indexes) {                      // for room index in indexes array
                resultRoom = rooms.get(index).copy();           // copy room
                resultRooms.add(resultRoom);                    // add copy of room in the results array
                resultRoom.setId(id);                           // set id of the selected room equal to filter's id
            }
            //System.out.println("We found it!!");
        } else {
            System.out.println("No match!!");
            return null;
        }
        return resultRooms;
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
        System.out.println("Thread id: " + Thread.currentThread().getId());
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

    private void printRooms()
    {
        for(Room room : rooms) {
            System.out.println(room.toString() + "DEBUG");
        }
    }
    // Handles requests for new room insertion
    private void handleNewRoomRequest(BufferedReader in, OutputStream out) throws IOException {
        String jsonRoom = extractBody(in);                                                                // extract json from request body
        Room room = new Gson().fromJson(jsonRoom, Room.class);                             // create filter object from json input file
        System.out.println("->-> " + room.toString());
        addRoom(room);                                                                              // add room to array
        //printRooms();

        System.out.println("Received filter data: " + jsonRoom);
        sendHttpResponse(out, 200, "OK", "{\"message\":\"New room added\"}",
                             "application/json");                                               // send response for succesfull http request
        System.out.println("Room added...");
    }
    // Handles requests for searching room
    private void handleSearchRoomRequest(BufferedReader in, OutputStream out) throws IOException, InterruptedException {
        String jsonFilter = extractBody(in);                                                                // extract json from request body
        Filter filter = deserializeFilter(jsonFilter);                             // create filter object from json input file
        System.out.println("Received request: " + filter.getId() + " with " + filter.toString());
        //synchronized(filter) {
        //Worker.filters.add(filter);
        //}
        ArrayList<Room> resultRooms = map(filter.getId(), filter);                                          // get array with results for reducer

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonResults = gson.toJson(resultRooms);
        //System.out.println("->->->" + jsonResults);
        //Thread.sleep(2000);
        sendHttpResponse(out, 200, "OK", "{\"message\":\"Filter added\"}"
                            , "application/json");                                              // send response for succesfull http request
        //System.out.println("Filter added...");
        //System.out.println("+-----------------------------+");
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