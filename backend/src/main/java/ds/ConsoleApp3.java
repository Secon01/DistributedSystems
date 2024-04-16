package ds;

//JSON related packages
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.json.JSONObject;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.impl.factory.Bags;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.*;
import java.net.Socket;

public class ConsoleApp3 extends Thread {
    private boolean done;
    private Scanner value1;
    private Scanner value2;
    private Scanner inp;
    private String end;
    private String start;
    private String finalJSONString;
    private int managerID;                              // manager's personal ID instance
    private Room room;                                  // room object instance
    private String regex = "\\d{4}-\\d{2}-\\d{2}";      // regular expression to match the format YYYY-MM-DD
    private static String hostname = "localhost";       
    private static int port = 8000;
    private static String input;
    private static ArrayList<Reducer> reducers;
    // Constructor for add room function 
    ConsoleApp3(Room room) throws IOException
    {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .create();
        finalJSONString= gson.toJson(room);
        //System.out.println(finalJSONString);
    }
    // Constructor for get bookings function
    ConsoleApp3(int managerID)
    {
        this.managerID = managerID;
    }
    // Constructor for get bookings by area function
    ConsoleApp3(String start , String end)
    {
        this.room = new Room();                 // initialize room instance
        this.room.setDateRange(start, end);     // set starting and ending date of room        
    }
    // Default constructor for running menu
    ConsoleApp3()
    {
        runMenu();
    }
    // Welcomes the user manager
    public void welcome() {
        System.out.println("+---------------------------------+");
        System.out.println("|  You've logged in as a Manager  |");
        System.out.println("+---------------------------------+");

    }
    // Prints menu
    public void menu() {
        System.out.println("Select an action");
        System.out.println(" 1) Insert House Information ");
        System.out.println(" 2) Show Given Indormation ");
        System.out.println(" 0) Exit");
    }
    // Run menu
    public void runMenu() {

        welcome();
        while (!done) {
            menu();
            int choice = getInput();
            performAction(choice);
        }

    }
    // Get user input
    public int getInput() {
        inp = new Scanner(System.in); // initialize scanner
        int choice = -1;
        while (choice < 0 || choice > 4) { // user input number out of bounds
            try {
                System.out.println("Enter your selection: ");
                choice = Integer.parseInt(inp.nextLine()); // user input
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection! Please try again.");
            }
        }
        return choice;
    }

    public void performAction(int choice) {
        switch (choice) {
            case 0:
                System.out.println(" Your room is now available for booking");
                done = true;
                break;
            case 1:
                System.out.println("Enter the path of your JSON file: ");
                String filePath = inp.nextLine();
                try {
                    String jsonString = JsonUtils.readFileToString(filePath); // convert file path of json to string
                    if (jsonString != null) {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        System.out.println("Main Information JSON File :\n" + jsonString);
                        System.out.println("Enter your starting date");
                        // Check if the user input matches the desired format  
                        while(true) {
                            value1 = new Scanner(System.in);
                            start = value1.nextLine();    
                            if (Pattern.matches(regex, start)) {    // check if regular expression of date format matches user's input
                                jsonObject.put("StartDate", start);
                                break;
                            } else {
                                System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format!");
                            }                                                                        
                        }  
                        System.out.println("Enter your ending date");
                        while (true) {
                            value2 = new Scanner(System.in);
                            end = value2.nextLine();                            
                            if (Pattern.matches(regex, end)) {      // check if regular expression of date format matches user's input
                                jsonObject.put("EndDate", end);
                                break;
                            } else {
                                System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format!");
                            }                                                                        
                        }
                        finalJSONString = jsonObject.toString();
                        System.out.println("Final JSON File Content:\n" + finalJSONString);
                    } else {
                        System.out.println("Failed to read the JSON file.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    System.err.println("Error parsing JSON: " + e.getMessage());
                } 
                break;
            case 2:
                System.out.println("These are your apartments' information:\n" + finalJSONString);
                break;
            case 3:
                inp = new Scanner(System.in);
                System.out.println("Type 'add room' and press enter to proceed");
                input = inp.nextLine();
                this.run();                 // call run of current thread
                break;
            case 4:
                inp = new Scanner(System.in);
                System.out.println("Type your peronal ID and press enter to proceed");
                managerID = Integer.parseInt(inp.nextLine());
                System.out.println("Type 'get booking' and press enter to proceed");
                input = inp.nextLine();
                this.run();                 // call run of current thread
                break;
            default:
                System.out.println("An unknown error has occured!");
                break;
        }
    }

    // Info chunck
    public static class JsonUtils {
        public static String readFileToString(String filePath) throws IOException {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        }
    }

    // Deserialize json to reducer object  
    private Reducer deserializeReducer(String json)
    {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();
        return gson.fromJson(json, Reducer.class);
    }

    // Serialize manager's personal ID
    private String serializeManagerID(int mID)
    {
        return new Gson().toJson(mID);
    }

    // Serializes room object to a json 
    private String serializeRoom(Room room)
    {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .create();
        return gson.toJson(room);
    } 
    /*
    // Returns number of bookings per area
    private int areaBookings(String area, RoomResult results) 
    {
        int bookings = 0;
        for(Room room : results.getRooms()) {
            if(area.equals(room.getArea())) {   
                bookings++;
            }
        }
        return bookings;
    }
    */
    public void run() 
    {
        Socket socket = null;
        try {
            socket = new Socket(hostname, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            if (input.equals("add room")) {                     // check if input is equal to 'add room'
                sendPostNewRoomRequest(out, finalJSONString);
                // Read the response
                String responseLine = extractBody(in);
                System.out.println(responseLine);
            } else if(input.equals("get booking")) {               // check if input is equal to 'get booking'
                reducers = new ArrayList<>();            // array list with reducer objects for printing
                sendGetBookRequest(out);                                    // send request in order to get the bookings
                String json = extractBody(in);                              // extract json from http request body 
                if (!json.startsWith("{\"message\"")) {
                    Reducer reducer = deserializeReducer(json);     // create reducer object from json
                    synchronized(reducer) {
                       reducers.add(reducer);                       // add reducer objects with results in reducers array
                    }
                } else {
                    System.out.println(json);                 // read the response
                }
            } else if(input.equals("area booking")) {              // check if input is equal to 'area bookings'
                reducers = new ArrayList<>();            // array list with reducer objects for printing
                sendAreaBookRequest(out);
                String json = extractBody(in);                              // extract json from http request body 
                if (!json.isEmpty()) {
                    Reducer reducer = deserializeReducer(json);     // create reducer object from json
                    synchronized(reducer) {
                       reducers.add(reducer);                       // add reducer objects with results in reducers array
                    }
                    MutableBag<String> areaBookings = Bags.mutable.empty();
                    for(RoomResult result: reducer.getResults()) {
                        for(Room room : result.getRooms()) {
                            int bookings = 1;
                            //System.out.println(room.toString());
                            //System.out.println();
                            areaBookings.addOccurrences(room.getArea(), bookings);
                        }
                    }
                    areaBookings.forEachWithOccurrences((key, occurrences) -> System.out.println(key + ": " +  occurrences));               
                } else {
                    String responseLine;
                    while ((responseLine = in.readLine()) != null) {
                        System.out.println(responseLine);
                    }    
                }
            } else {
                System.out.println("Unknown command. Use 'getRoom' or 'newRoom'.");
                inp.close();                 // close scanner
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();             // close socket
            } catch (IOException e) {
                    e.printStackTrace();
            }
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
            //System.out.println(line);
            if (line.toLowerCase().startsWith("content-length:")) {
            contentLength = Integer.parseInt(line.substring("content-length:".length()).trim());
            }
        }    
        // Read the body
        if(contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            requestBody.append(new String(buffer));
        }
        return requestBody.toString();
    }

    // Send request to add a new room 
    private void sendPostNewRoomRequest(PrintWriter out, String jsonBody) {
        out.println("POST /newRoom HTTP/1.1");
        out.println("Host: localhost");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonBody.length());
        out.println("Connection: close");
        out.println();
        out.println(jsonBody);
    }
    // Sends request to get manager's bookings
    private void sendGetBookRequest(PrintWriter out) {
        String jsonBody = serializeManagerID(managerID);
        out.println("GET /getBooking HTTP/1.1");
        out.println("Host: localhost");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonBody.length());
        out.println("Connection: close");
        out.println();
        out.println(jsonBody);
    }
    // Sends request to get bookings by area in a given date range      
    private void sendAreaBookRequest(PrintWriter out) {
        String jsonBody = serializeRoom(room);
        out.println("GET /getAreaBooking HTTP/1.1");
        out.println("Host: localhost");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonBody.length());
        out.println("Connection: close");
        out.println();
        out.println(jsonBody);
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        //new ConsoleApp3().start();
        Scanner sc = new Scanner(System.in);
        System.out.println("Give input");
        String in = sc.nextLine();
        if(in.equals("add room")) {
            /* 
            Room room1 = new Room(12,"Luxury Suite", 2, 200.0, 5,
            "Downtown", 100, "luxury_suite.jpg", "2024-04-01", "2024-04-07", true);
            Room room2 = new Room(12,"Cozy Cabin", 4, 150.0, 4,
                        "Mountains", 80, "cozy_cabin.jpg", "2024-04-02", "2024-04-08", true);
            Room room3 = new Room(12,"Beach House", 6, 300.0, 5,
                        "Beachfront", 120, "beach_house.jpg", "2024-04-03", "2024-04-09", true);
            Room room4 = new Room(34,"City Apartment", 3, 180.0, 4,
                        "Urban", 90, "city_apartment.jpg", "2024-04-04", "2024-04-10", true);
            Room room5 = new Room(34,"Country Cottage", 4, 160.0, 4,
                        "Rural", 85, "country_cottage.jpg", "2024-04-05", "2024-04-11", true);
            Room room6 = new Room(34, "Mountain Chalet", 5, 250.0, 5,
                        "Mountains", 110, "mountain_chalet.jpg", "2024-04-06", "2024-04-12", true);
            */
            
            for(int i = 0; i < 1; i++) {
                Room room1 = new Room(7, "Luxury Suite 1", 2, 200.0, 5,
                        "Athens", 100, "luxury_suite_1.jpg", "2024-04-01", "2024-04-07", true);
                Room room2 = new Room(7, "Deluxe Suite", 3, 250.0, 4,
                        "Athens", 90, "deluxe_suite.jpg", "2024-04-02", "2024-04-08", true);
                Room room3 = new Room(7, "Standard Room", 2, 150.0, 3,
                        "Thessaloniki", 80, "standard_room.jpg", "2024-04-03", "2024-04-09", true);
                Room room4 = new Room(7, "Economy Room", 1, 100.0, 2,
                        "Thessaloniki", 70, "economy_room.jpg", "2024-04-04", "2024-04-10", true);
                Room room5 = new Room(7, "Family Room", 4, 300.0, 5,
                        "Lamia", 120, "family_room.jpg", "2024-04-05", "2024-04-11", true);
                Room room6 = new Room(13, "Suite", 2, 180.0, 4,
                        "Lamia", 110, "suite.jpg", "2024-04-06", "2024-04-12", true);
                Room room7 = new Room(13, "Single Room", 1, 120.0, 3,
                        "Crete", 60, "single_room.jpg", "2024-04-07", "2024-04-13", true);
                Room room8 = new Room(13, "Double Room", 2, 220.0, 4,
                        "Crete", 80, "double_room.jpg", "2024-04-08", "2024-04-14", true);
                Room room9 = new Room(13, "Executive Suite", 3, 280.0, 5,
                        "Larisa", 150, "executive_suite.jpg", "2024-04-09", "2024-04-15", true);
                Room room10 = new Room(13, "Penthouse", 6, 500.0, 5,
                        "Larisa", 200, "penthouse.jpg", "2024-04-10", "2024-04-16", true);         
                input = in;
                new ConsoleApp3(room1).start();
                new ConsoleApp3(room2).start();
                new ConsoleApp3(room3).start();       
                new ConsoleApp3(room4).start();       
                new ConsoleApp3(room5).start();       
                new ConsoleApp3(room6).start(); 
                new ConsoleApp3(room7).start(); 
                new ConsoleApp3(room8).start(); 
                new ConsoleApp3(room9).start();
                new ConsoleApp3(room10).start();              
            }
        } else if(in.equals("get booking")) {
            input = in;
            for(int i = 0; i < 1; i++) {
                new ConsoleApp3(7).start();
                new ConsoleApp3(13).start();
                Thread.sleep(1000);
                Collections.sort(reducers);                     // sort reducers array list based on current id
                for(Reducer r : reducers) {                   // for each reducer obejct in reducers arraylist
                    r.printRooms();                           // print results
                }
            }
        } else if(in.equals("area booking")) {
            input = in;
            for(int i = 0; i < 1; i++) {
                new ConsoleApp3("2024-04-01", "2024-04-16").start();
            }         
        }
        sc.close();   
    }
}