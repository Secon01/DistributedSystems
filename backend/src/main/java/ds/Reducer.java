package ds;

import java.util.ArrayList;

public class Reducer 
{
    ArrayList<Room> results;            // array with selected rooms based on given filters

    // Constructor
    Reducer()
    {
        results = new ArrayList<>();    // array initialization
    }

    public ArrayList<Room> reduce()
    {
        return results;
    }
}
