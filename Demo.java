import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Demo 
{
    public static void main(String[] args) {
        Room room1 = new Room(0, "Villa 1", 2, "New York", 3, 10, 70.00);
        Room room2 = new Room(1, "Villa 2", 4, "Arizona", 4, 13, 100.00);
        Room room3 = new Room(2, "Villa 3", 3, "Amsterdam", 5, 30, 80.00);
        Room room4 = new Room(3, "Villa 4", 1, "Athens", 2, 20, 40.00);

        ArrayList<Room> accomodations = new ArrayList<>();

        Worker w1 = new Worker("Worker1", room1, accomodations);
        Worker w2 = new Worker("Worker2", room2, accomodations);
        Worker w3 = new Worker("Worker3", room3, accomodations);
        Worker w4 = new Worker("Worker4", room4, accomodations);

        // Workers start working
        w1.start();
        w2.start();
        w3.start();
        w4.start();

        // Program sleep time
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Print array with rooms
        for(int i = 0; i < accomodations.size(); i++) {
            accomodations.get(i).getAll();
            System.out.println("-------------------------------");
        }
    }    
}