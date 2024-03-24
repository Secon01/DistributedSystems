import java.util.ArrayList;

// Master class
public class Master extends Thread
{
    ArrayList<Worker> workers = new ArrayList<>();  // Array list of workers
    // Mater initialization
    Master(int request)
    {
        for(int i =0; i < request; i++)     // for loop to create as many workers as the incoming requests
        {    
            workers.add(new Worker("Worker " + i));
        }
    }
    public static void main(String[] args) {
        Master m = new Master(11);
        for (Worker w : m.workers) {        // print out workers
            System.out.println(w.getName());
        }
    }
}