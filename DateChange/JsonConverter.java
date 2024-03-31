package ds;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonConverter {
    static string value1;
    static string value2;

    public static void main(String[] args) {
        // Convert an object room to JSON
        // Room room = new Room(0, "Villa 1", 2, "New York", 3, 10, 70.00);
        System.out.println("Json representation of Object room is ");
        // System.out.println(new Gson().toJson(room));

        System.out.println();

        // Convert a JSON to an object room
        String filePath = "/home/secon/Documents/GitHub/DistributedSystems/Room.json";
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

            } else {
                System.out.println("Failed to read the JSON file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    // Convert json to string
    public class JsonUtils {
        public static String readFileToString(String filePath) throws IOException {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        }
    }
}