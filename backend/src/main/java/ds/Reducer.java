package ds;

import java.util.ArrayList;

public class Reducer 
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

    public void printRooms() throws InterruptedException
    {
        System.out.println(this.currentID);
        for(RoomResult result : results) {
            System.out.println(result.getRooms());
        }
    }
}