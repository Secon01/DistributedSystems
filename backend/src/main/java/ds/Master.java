package ds;
import java.io.IOException;
import java.util.ArrayList;

import ds.JsonConverter.JsonUtils;

// Master class
public class Master 
{
    ArrayList<Worker> workers = new ArrayList<>();      // Array list of workers
    Worker worker;
    // Master constructor
    Master(int request)
    {
        for(int i =0; i < request; i++)                 // for loop to create as many workers as the incoming requests
        {    
            //workers.add(new Worker("Worker " + i));
        }
    }

    Master(Filter filter)
    {
        // Push a room to worker from json file
        worker = new Worker(filter);

        String jsonString = null;
        try {
            jsonString = JsonUtils.readFileToString("C:/Users/sotir/DistributedSystems/Room.json");
        } catch (IOException e) {
            e.printStackTrace();
        }       // convert file path of json to string
        Room roomNY = worker.deserialize(jsonString);
        worker.getRooms().add(roomNY);
        worker.start();
        /* 
        The proper initialization of worker and run 
        //worker = new Worker(filter);
        //worker.start();
        */
    }

    public static void main(String[] args) {
        
    }
}