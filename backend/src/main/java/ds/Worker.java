package ds;
import java.util.ArrayList;

public class Worker extends Thread
{
    ArrayList<Room> rooms;      // Rooms array
    Room room;                  // Room instance

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

    // Worker constructor
    Worker(String name, Room r, ArrayList<Room> rs)
    {
        this.setName(name);         // Worker's name initialization
        this.room = r;              // Room initialization
        this.rooms = rs;            // Rooms array initialization
    }
}
