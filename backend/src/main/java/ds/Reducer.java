package ds;

import java.util.ArrayList;

public class Reducer implements Comparable<Reducer>
{
    private ArrayList<RoomResult> results;            // array with selected rooms based on given filter
    private int currentID;

    // Constructor
    Reducer()
    {
        results = new ArrayList<RoomResult>();    // array initialization
    }

    public void setCurrentID(int currentID) {
        this.currentID = currentID;
    }
    public int getCurrentID() {
        return currentID;
    }
    public ArrayList<RoomResult> getResults() {
        return results;
    }

    public synchronized void reduce(int id, RoomResult resRooms) throws InterruptedException
    {
        //synchronized(this) {
            if(id == this.currentID) {
                this.results.add(resRooms);
                notify();
            } else {
                wait();
            }    
       // }
    }

    // Prints request id and rooms of results arraylist
    public void printRooms() throws InterruptedException
    {
        System.out.println();
        System.out.println();
        System.out.println("+-------------- Request: " +  this.currentID + " --------------+");
        for(RoomResult result : results) {
            result.printRooms();        // prints each room's data
        }
    }

    // Compares reducer objects based on their current ID
    public int compareTo(Reducer other)
    {
        return Integer.compare(this.currentID, other.currentID);
    }
}