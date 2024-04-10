package ds;

//JSON related packages
//import org.json.JSONException; //(not sure if needed yet)
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.json.JSONObject;
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
    static String finalJSOString;
    private String regex = "\\d{4}-\\d{2}-\\d{2}";      // regular expression to match the format YYYY-MM-DD

    ConsoleApp3(Room room) throws IOException
    {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .create();
        finalJSOString= gson.toJson(room);
        System.out.println(finalJSOString);
        sendrequest();
    }

    ConsoleApp3()
    {

    }

    public void welcome() {
        System.out.println("+---------------------------------+");
        System.out.println("|  You've logged in as a Manager  |");
        System.out.println("+---------------------------------+");

    }

    // Print menu
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
        while (choice < 0 || choice > 3) { // user input number out of bounds
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
                        finalJSOString = jsonObject.toString();
                        System.out.println("Final JSON File Content:\n" + finalJSOString);

                        // gson convert the JSON string into an object
                        // Gson gson = new Gson();
                        // Room r = gson.fromJson(jsonString, Room.class);
                        // r.setEndDate(end);
                        // r.setStartDate(start);
                        // System.out.println("Object created from JSON: " + r);
                        // System.out.println(r.getArea());
                        // System.out.println(r.getEndDate());
                        // System.out.println(r.getStartDate());

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
                System.out.println("These are your apartments' information:\n" + finalJSOString);
                break;
            case 3:
                // sendDataToServer(finalJSOString);
                try {
                    sendrequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    // public Room readJsonFile(String filePath) throws FileNotFoundException {
    // Gson gson = new Gson();
    // try (FileReader reader = new FileReader(filePath)) {
    // return gson.fromJson(reader, Room.class);
    // } catch (Exception e) {
    // throw new FileNotFoundException("Failed to read JSON file: " +
    // e.getMessage());
    // }
    // }

    // private void sendDataToServer(String data) {
    // try (Socket socket = new Socket("localhost", Server.PORT);
    // PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
    // out.println(data);
    // System.out.println("Data sent to server: " + data);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    public void sendrequest() throws IOException {
        String hostname = "localhost";
        int port = 8000;
        Socket socket = new Socket(hostname, port);
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            sendPostNewRoomRequest(in, out);

            // Read the response
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    private static void sendPostNewRoomRequest(BufferedReader in, PrintWriter out) {
        String jsonBody =  finalJSOString;
        out.println("POST /newRoom HTTP/1.1");
        out.println("Host: localhost");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonBody.length());
        out.println("Connection: close");
        out.println();
        out.println(jsonBody);
    }

    public static void main(String[] args) throws IOException {
        //ConsoleApp3 insertion = new ConsoleApp3();
        //insertion.runMenu();
        /* 
        Room room1 = new Room("Villa", null, 0, 40.0, 0, "Larisa", 0, null, null, null, true);
        Room room2 = new Room("HotelPoseidon", null, 0, 0, 0, "Athens", 0, null, null, null, true);
        Room room3 = new Room("StefFarm", null, 0, 0, 0, "Lamia", 0, null, null, null, true);
        */ 
        Room room1 = new Room("Luxury Suite", 2, 200.0, 5,
                                "Downtown", 100, "luxury_suite.jpg", "2024-04-01", "2024-04-07", true);
        Room room2 = new Room("Cozy Cabin", 4, 150.0, 4,
                                "Mountains", 80, "cozy_cabin.jpg", "2024-04-02", "2024-04-08", true);
        Room room3 = new Room("Beach House", 6, 300.0, 5,
                                "Beachfront", 120, "beach_house.jpg", "2024-04-03", "2024-04-09", true);
        Room room4 = new Room("City Apartment", 3, 180.0, 4,
                                "Urban", 90, "city_apartment.jpg", "2024-04-04", "2024-04-10", true);
        Room room5 = new Room("Country Cottage", 4, 160.0, 4,
                                "Rural", 85, "country_cottage.jpg", "2024-04-05", "2024-04-11", true);
        Room room6 = new Room("Mountain Chalet", 5, 250.0, 5,
                                "Mountains", 110, "mountain_chalet.jpg", "2024-04-06", "2024-04-12", true);
                                          
        new ConsoleApp3(room1).start();
        new ConsoleApp3(room2).start();
        new ConsoleApp3(room3).start();       
        new ConsoleApp3(room4).start();       
        new ConsoleApp3(room5).start();       
        new ConsoleApp3(room6).start();      
        /* 
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .create();
        String json = gson.toJson(room1);
        System.out.println(json);
    
        Gson gson2 = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();
        Room room = gson2.fromJson(json, Room.class);
        System.out.println(room.toString());
        */
    }
}