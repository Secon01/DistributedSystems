package ds;

//JSON related packages
//import org.json.JSONException; //(not sure if needed yet)
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.*;
import java.net.Socket;

public class ConsoleApp3 extends Thread {
    boolean done;
    Scanner value1;
    Scanner value2;
    Scanner inp;
    String end;
    String start;
    static String finalJSOString;

    ConsoleApp3(Room room) throws IOException
    {
        finalJSOString = new Gson().toJson(room);
        System.out.println(finalJSOString);
        sendrequest();
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
                        System.out.println("Main Indformation JSON File :\n" + jsonString);
                        System.out.println("Give me your starting Date");
                        value1 = new Scanner(System.in);
                        start = value1.nextLine();
                        jsonObject.put("StartDate", start);
                        System.out.println("Give me your ending Date");
                        value2 = new Scanner(System.in);
                        end = value2.nextLine();
                        jsonObject.put("EndDate", end);
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
        Room room1 = new Room("Luxury Suite", "2024-04-01", 2, 200.0, 5,
                                "Downtown", 100, "luxury_suite.jpg", "2024-04-01", "2024-04-07", true);
        Room room2 = new Room("Cozy Cabin", "2024-04-02", 4, 150.0, 4,
                                "Mountains", 80, "cozy_cabin.jpg", "2024-04-02", "2024-04-08", true);
        Room room3 = new Room("Beach House", "2024-04-03", 6, 300.0, 5,
                                "Beachfront", 120, "beach_house.jpg", "2024-04-03", "2024-04-09", true);
        Room room4 = new Room("City Apartment", "2024-04-04", 3, 180.0, 4,
                                "Urban", 90, "city_apartment.jpg", "2024-04-04", "2024-04-10", true);
        Room room5 = new Room("Country Cottage", "2024-04-05", 4, 160.0, 4,
                                "Rural", 85, "country_cottage.jpg", "2024-04-05", "2024-04-11", true);
        Room room6 = new Room("Mountain Chalet", "2024-04-06", 5, 250.0, 5,
                                "Mountains", 110, "mountain_chalet.jpg", "2024-04-06", "2024-04-12", true);
        new ConsoleApp3(room1).start();
        new ConsoleApp3(room2).start();
        new ConsoleApp3(room3).start();       
        new ConsoleApp3(room4).start();       
        new ConsoleApp3(room5).start();       
        new ConsoleApp3(room6).start();       
    }
}