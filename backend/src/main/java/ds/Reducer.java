package ds;

import java.util.ArrayList;

public class Reducer 
{
    private ArrayList<ArrayList<Room>> results;            // array with selected rooms based on given filters
    private Room room;

    // Constructor
    Reducer()
    {
        results = new ArrayList<>();    // array initialization
    }

    public ArrayList<ArrayList<Room>> getResults() {
        return results;
    }

    private ArrayList<ArrayList<Room>> reduce(ArrayList<ArrayList<Room>> rooms)
    {
        synchronized(room) {
            results.addAll(rooms);
            return results;    
        }
    }


}
