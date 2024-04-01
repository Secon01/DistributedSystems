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

public class Worker extends Thread
{
    private ArrayList<Room> rooms;                                                  // rooms array
    private Room room;                                                              // room instance
    private static Filter filter;                                                          // filter instance
    private String[] propertyNames = {"area", "date", "guests", "price", "stars"};  // array with common properties of Room and Filter 
    private static ServerSocket serverSocket;                                              // server socker 
	private static Socket socket = null;                                                   // set socket null
    private static int port;                                                        // port of worker

    // Default constructor
    Worker()
    {
        this.rooms = new ArrayList<>();             // rooms array initialization
    }

    // Rooms array getter
    public ArrayList<Room> getRooms() 
    {
        return rooms;
    }
    
    // Deserializes json file to a room object 
    public Room deserializeRoom(String jsonString)
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
    public void addRoom(Room room)
    {   
        rooms.add(room);
    }

    // Deserializes json file to a filter object
    public static Filter deserializeFilter(String jsonString)
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
    public boolean hasRoom(Filter filter)
    {
        if (filter.numFilter() == 0)                // if Filter object has only null or 0 values on properties
        {
            return false;
        }
        for (Room room: rooms) {
            if (filter == null && room == null) {   // both objects are null
                return false;
            }
            if (filter == null || room == null) {
                return false;                       // filter is null and room is not or the opposite
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
                    System.out.println(propertyName + ":" + valueF + " DEBUG");
                    System.out.println(propertyName + ":" + valueR + " DEBUG");
    
                    // Both values are not null or 0, compare them
                    if (valueF != null && valueR != null && !valueF.equals(0) && !valueR.equals(0) && 
                        !valueF.equals(0.0) && !valueR.equals(0.0)) {
                        if (!valueF.equals(valueR)) {
                            System.out.println("NO MATCHES");
                            return false;   // properties are not equal
                        }
                    } else if(valueF == null || valueR == null || valueF.equals(0) || valueR.equals(0) || 
                              valueF.equals(0.0) || valueR.equals(0.0)) {
                        continue;   // if some property of Room object or Filter object is null or 0
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    
    // Returns a room according to given filters 
    public Room map(int id , Filter filter)
    {
        Room resultRoom = null;
        if (hasRoom(filter)) {                      // if worker has a room with the given filters
            resultRoom = rooms.get(0).copy();       // make a copy of the room in the array
            //System.out.println(resultRoom.getId() + " Debug");
            resultRoom.setId(id);                   // set id of the selected room equal to filter's id
            System.out.println("We found it!");
        } 
        return resultRoom;
    }

    // Opens worker's server side
    private static void openServer(int p) throws IOException
    {
        port = p;                                                               
		serverSocket = new ServerSocket(port, 10);                                                      // create socket
        System.out.println("Worker is listening on port " + port);

		while (true) {
			socket = serverSocket.accept();
            System.out.println(socket.getInputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));      // get input stream in buffered reader
            OutputStream output = socket.getOutputStream();                                                 // get output stream from master's socket

            String requestLine = input.readLine();                                                          // set request line 
            if (requestLine == null || requestLine.isEmpty()) continue;
            // Header check
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
            socket.close();                                                                                 // close socket
		}
    }

    // Handles requests for new room insertion
    private static void handleNewRoomRequest(BufferedReader in, OutputStream out) throws IOException {
        StringBuilder requestBody = new StringBuilder();                                                    // build the body of request into string
        String roomJson;
        while (!(roomJson = in.readLine()).isEmpty()) {
            requestBody.append(roomJson);
        }
        Room room = new Gson().fromJson(roomJson, Room.class);                                     // create room object from json input file
        System.out.println(room.toString());

        System.out.println("Received new room data: " + requestBody);
        sendHttpResponse(out, 200, "OK", "{\"message\":\"New room added\"}",
                             "application/json");                                               // send response for succesfull http request
        System.out.println("Room added...");
    }

    // Handles requests for searching room
    private static void handleSearchRoomRequest(BufferedReader in, OutputStream out) throws IOException {
        StringBuilder requestBody = new StringBuilder();                                                    // build the body of request into string
        String filterJson;
        while (!(filterJson = in.readLine()).isEmpty()) {
            requestBody.append(filterJson);
        }
        filterJson = in.readLine().repeat(1);                                                               // repeat readline() one time in order to read json body
        System.out.println(filterJson);
        Filter filter = new Gson().fromJson(filterJson, Filter.class);                             // create filter object from json input file
        System.out.println("->-> " + filter.toString());

        System.out.println("Received filter data: " + requestBody);
        sendHttpResponse(out, 200, "OK", "{\"message\":\"New room added\"}"
                            , "application/json");                                              // send response for succesfull http request
        System.out.println("Room added...");
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

    // 
    private static void consumeRemainingRequest(BufferedReader in) throws IOException {
        while (in.ready()) {
            in.readLine();
        }
    }

    public static void main(String[] args) throws IOException{
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter port");

        openServer(sc.nextInt());
        sc.close();
    }
}