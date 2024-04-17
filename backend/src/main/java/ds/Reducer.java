package ds;

import java.util.ArrayList;

public class Reducer implements Comparable<Reducer>
{
    private ArrayList<RoomResult> results;            // Array with selected rooms based on given filter
    private int currentID;                            // current id

    // Constructor
    Reducer()
    {
        results = new ArrayList<RoomResult>();        // Array initialization
    }
    // Setter of id
    public void setCurrentID(int currentID) {
        this.currentID = currentID;
    }
    // Getters
    public int getCurrentID() {
        return currentID;
    }
    public ArrayList<RoomResult> getResults() {
        return results;
    }
    // Method that adds the RoomResult type object to the Array
    public synchronized void reduce(int id, RoomResult resRooms) throws InterruptedException
    {
        if(id == this.currentID) {                          
            this.results.add(resRooms);        
            notify();
        } else {
            wait();
        }    
    }
    // Checks if the ArrayList of the RoomResult type objects is empty
    public boolean isEmpty()
    {
        boolean empty = true;
        for(RoomResult result : results) {
            if(!result.getRooms().isEmpty()) {          // if element of the Arraylist is not empty
                empty = false;                          // make empty false
            }
        }
        return empty;
    }
    // Prints request id and rooms of results arraylist
    public void printRooms() throws InterruptedException
    {
        System.out.println();
        System.out.println();
        System.out.println("+-------------- Request: " +  this.currentID + " --------------+");
        for(RoomResult result : results) {
            result.printRooms();                       // prints each room's data
            System.out.println();
        }
    }
    // Compares reducer objects based on their current ID
    @Override
    public int compareTo(Reducer other)
    {
        return Integer.compare(this.currentID, other.currentID);
    }
}
