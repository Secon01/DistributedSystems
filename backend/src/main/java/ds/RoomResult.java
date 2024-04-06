package ds;

import java.util.ArrayList;

public class RoomResult 
{
    private ArrayList<Room> rooms;                   // array with searching results 
    private int id;                                         // 

    // Costructor 
    RoomResult()
    {
        this.rooms = new ArrayList<>();            // initialize array    
    }

    // Getters, Setters
    public int getId() {
        return id;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Adds room to array
    public  void addRoom(Room room)
    {
        //synchronized(room) {
            rooms.add(room);
        //}
    }
}
