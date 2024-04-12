package ds;

import java.util.ArrayList;

public class RoomResult 
{
    private ArrayList<Room> rooms;                   // array with searching results 
    private int id;                                  // id of array 

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
        rooms.add(room);
    }

    // Prints rooms of rooms array
    public void printRooms()
    {
        for(Room room : rooms) {
            System.out.println(room.toString());
            System.out.println();
        }
    }
}
