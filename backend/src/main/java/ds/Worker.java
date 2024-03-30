package ds;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.google.gson.Gson;

import ds.JsonConverter.JsonUtils;

public class Worker extends Thread
{
    private ArrayList<Room> rooms;                                                  // rooms array
    private Room room;                                                              // room instance
    private Filter filter;                                                          // filter instance
    private String[] propertyNames = {"area", "date", "guests", "price", "stars"};  // array with common properties of Room and Filter 

    // Worker constructor (properly it takes the object from output stream!)
    Worker(Filter filter)
    {
        //this.room = r;                              // room initialization
        this.rooms = new ArrayList<>();             // rooms array initialization
        this.filter = filter;
    }

    // Default constructor
    Worker()
    {
        this.rooms = new ArrayList<>();             // rooms array initialization
    }

    public void run()
    {
        map(filter.getId(), filter);   
    }

    // Deserializes json file to a room object 
    public Room deserialize(String jsonString)
    {
        if(jsonString != null) {
            System.out.println("JSON File Content:\n" + jsonString);
            // Convert the JSON string into an object
            Gson gson = new Gson();                                 
            room = gson.fromJson(jsonString, Room.class);
            System.out.println("Object created from JSON: " + room);
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
                    if (valueF != null && valueR != null && !valueF.equals(0) && !valueR.equals(0)) {
                        if (!valueF.equals(valueR)) {
                            System.out.println("NO MATCHES");
                            return false;   // properties are not equal
                        }
                    } else if(valueF == null || valueR == null || valueF.equals(0) || valueR.equals(0)) {
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
            System.out.println(resultRoom.getId() + " Debug");
            resultRoom.setId(id);                   // set id of the selected room equal to filter's id
            System.out.println("We found it!");
        } 
        return resultRoom;
    }

    // Rooms array getter
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public static void main(String[] args)  {
        // Json deserialize check
        Worker worker = new Worker();
        String jsonString = null;
        try {
            jsonString = JsonUtils.readFileToString("/home/secon/Documents/GitHub/DistributedSystems/Room.json");
        } catch (IOException e) {
            e.printStackTrace();
        }       // convert file path of json to string
        worker.deserialize(jsonString);
    }
}