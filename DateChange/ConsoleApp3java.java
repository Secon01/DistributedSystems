package ds;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ConsoleApp3 {
    boolean done;
    Scanner inp;
    Info information = new Info();

    public void welcome() {
        System.out.println("+---------------------------------+");
        System.out.println("|  You've logged in as a Manager  |");
        System.out.println("+---------------------------------+");

    }

    // Print Menu

    public void menu() {
        System.out.println("Select an action");
        System.out.println(" 1) Insert House Information ");
        System.out.println(" 2) Insert Date / Availability");
        System.out.println(" 3) Continue ");
        System.out.println(" 0) Exit");
    }

    public void runMenu() {

        welcome();
        while (!done) {
            menu();
            int choice = getInput();
            performAction(choice);
        }

    }

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

    // Perform action according to choice
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
                        System.out.println("JSON File Content:\n" + jsonString);
                        System.out.println("Give me your starting Date");
                        value1 = new Scanner(System.in);
                        jsonString.put("StartDate", "value1");
                        System.out.println("Give me your starting Date");
                        value2 = new Scanner(System.in);
                        jsonString.put("EndDate", "value2");
                        System.out.println("JSON File Content:\n" + jsonString);
                        // gson convert the JSON string into an object
                        Gson gson = new Gson();
                        Room r = gson.fromJson(jsonString, Room.class);
                        System.out.println("Object created from JSON: " + r);
                        System.out.println(r.getArea());
                        System.out.println(r.getEndDate());
                        System.out.println(r.getStartDate());

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
                System.out.println("Enter you start date");
                information.setAvailabilityDateStart(inp.nextLine());
                System.out.println("Enter you end date");
                information.setAvailabilityDateEnd(inp.nextLine());

                break;
            case 3:
                System.out.println("These are your room's information ");
                // System.out.println(information.path);
                System.out.println(information.availableDateS);
                System.out.println(information.availableDateE);
                break;
            default:
                System.out.println("An unknown error has occured!");
                break;
        }
    }

    // Info chunck

    public class Info {

        private Room a;
        private String availableDateS;
        private String availableDateE;

        public void setRoom(Room room) {
            this.a = room;
            System.out.println(a);
        }

        public void setAvailabilityDateStart(String b) {
            this.availableDateS = b;
        }

        public void setAvailabilityDateEnd(String c) {
            this.availableDateE = c;
        }

    }

    public class JsonUtils {
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

    public static void main(String[] args) {

        ConsoleApp3 insertion = new ConsoleApp3();
        insertion.runMenu();

    }
}