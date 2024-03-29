package ds;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Worker extends Thread
{
    ArrayList<Room> rooms;                                                  // Rooms array
    Room room;                                                              // Room instance
    String[] propertyNames = {"area", "date", "guests", "price", "stars"};  // Array with common properties of Room and Filter 

    // Worker constructor
    Worker(String name, Room r)
    {
        this.setName(name);                         // Worker's name initialization
        this.room = r;                              // Room initialization
        this.rooms = new ArrayList<>();             // Rooms array initialization
    }

    public void run()
    {
        synchronized(rooms) {
            rooms.add(room);        // add room to array
            System.out.println(room.getId() + " added to array");
            //rooms.notify();
            //try {
            //    rooms.wait();       // wait for worker to add room to array
            //} catch (Exception e) {
            //    e.printStackTrace();
            //}
        }
    }

    // Map method uses filter instance from dummy app
    public Room map(Filter filter)
    {
        room.setId(filter.getId());
        return room;
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
                    System.out.println(valueF);
                    System.out.println(valueR);
    
                    // Both values are not null or 0, compare them
                    if (valueF != null && valueR != null && !valueF.equals(0) && !valueR.equals(0)) {
                        if (!valueF.equals(valueR)) {
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
    

    public static void main(String[] args) {
        Filter filter = new Filter();
        //filter.setArea("New York");
        //filter.setDate("17-04-2024");
        //filter.setGuests(6);
        
        Room room = new Room(0, "Villa", "17-04-2024", 6, 50.0, 3, "New York", MAX_PRIORITY, null);

        Worker worker = new Worker("Secon", room);
        worker.rooms.add(room);

        System.out.println(worker.hasRoom(filter));
    }
}
