// Dummy App for users
// Communication protocol is based on HTTP
package ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Dummy extends Thread {
   private boolean exit;
   private Scanner sc;
   private Filter filter;
   private static String hostname = "localhost";
   private static int port = 8000;
   static String[] inputArgs;
   public static String input;
   private static ArrayList<Reducer> reducers;

   Dummy(String area, String date, int guests, double price, int stars) {
      this.filter = new Filter();
      this.filter.setArea(area);
      this.filter.setDate(date);
      this.filter.setGuests(guests);
      this.filter.setPrice(price);
      this.filter.setStars(stars);
      System.out.println(filter.toString());
   }

   Dummy() 
   {
   }

   public void header() {
      System.out.println("+----------------------------+");
      System.out.println("|   Welcome to Housebooking  |");
      System.out.println("+----------------------------+");
   }

   public void menu() {
      System.out.println();
      System.out.println("Please select a number for filtering or press 6 to continue for booking. If you want to exit press 0.");
      System.out.println(" 1) Area");
      System.out.println(" 2) Date");
      System.out.println(" 3) Number of guests");
      System.out.println(" 4) Price");
      System.out.println(" 5) Number of stars");
      System.out.println(" 6) Continue");
      System.out.println(" 0) Exit");
   }

   public void runMenu() {
      this.header();

      while(!this.exit) {
         this.menu();
         int choice = this.getInput();
         this.performAction(choice);
      }
   }

   public int getInput() {
      this.sc = new Scanner(System.in);
      int choice = -1;

      while(choice < 0 || choice > 6) {
         try {
            System.out.println("Enter your selection: ");
            choice = Integer.parseInt(this.sc.nextLine());
         } catch (NumberFormatException var3) {
            System.out.println("Invalid selection! Please try again.");
         }
      }
      return choice;
   }

   public void performAction(int choice) {
      switch (choice) {
         case 0:
            System.out.println("Thank you for using HouseBooking!");
            System.exit(0);
            break;
         case 1:
            System.out.println("Enter area's name");
            this.filter.setArea(this.sc.nextLine());
            break;
         case 2:
            System.out.println("Enter a date");
            this.filter.setDate(this.sc.nextLine());
            break;
         case 3:
            System.out.println("Enter a number of guests");
            this.filter.setGuests(Integer.parseInt(this.sc.nextLine()));
            break;
         case 4:
            System.out.println("Enter a price");
            this.filter.setPrice(Double.parseDouble(this.sc.nextLine()));
            break;
         case 5:
            System.out.println("Enter a number of stars");
            this.filter.setStars(Integer.parseInt(this.sc.nextLine()));
         case 6:
            break;
         default:
            System.out.println("An unknown error has occured!");
      }
   }

   // Sends filter to master and prints results
   private void search() throws InterruptedException
   {
      reducers = new ArrayList<>();       // intialize arraylist with reduce objects
      new Dummy().start();                        // create request thread and send it to master
      Collections.sort(reducers);         // sort reducers array list based on current id
      for(Reducer reducer : reducers) {   // for each reducer obejct in reducers arraylist
         reducer.printRooms();            // print results
      }
   }

   public void run() 
   {
      Socket socket = null;
      try {
         socket = new Socket(hostname, port);
      } catch (IOException e) {
         e.printStackTrace();
      }
      try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
      if (input.equals("search")){
         sendSearchRoomRequest(out);
      } else {
         System.out.println("Unknown command. Use 'getRoom' or 'newRoom'.");
         sc.close();                 // close scanner
         return;
      }
      String json = extractBody(in);
      Reducer reducer = new GsonBuilder().setPrettyPrinting().create().fromJson(json, Reducer.class);
      //System.out.println(reducer.getResults() + " " + reducer.getCurrentID());
      //reducer.printRooms();
      synchronized(reducer) {
         reducers.add(reducer);
      }
      //reducer.printRooms();
      // Read the response
      //synchronized(in) {
      //   String responseLine;
      //   while ((responseLine = in.readLine()) != null) {
      //         System.out.println(responseLine); 
      //   }
      //}    
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            socket.close();             // close socket
         } catch (IOException e) {
               e.printStackTrace();
         }
      }         
   }

    // Extracts body from http search room request
    private static String extractBody(BufferedReader in) throws IOException 
    {
    StringBuilder requestBody = new StringBuilder();    // build the body of request into string
    String line;                                        // represents each line of http header
    // Extract content length
    int contentLength = 0;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.substring("content-length:".length()).trim());
            }
        }    
        // Read the body
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            requestBody.append(new String(buffer));
        }
        return requestBody.toString();
    }

   private void sendSearchRoomRequest(PrintWriter out) {
        String jsonBody = new Gson().toJson(filter);
        out.println("POST /searchRoom HTTP/1.1");
        out.println("Host: localhost");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonBody.length());
        out.println("Connection: close");
        out.println();
        out.println(jsonBody);
   }
   public static void main(String[] args) throws IOException, InterruptedException {
      Scanner sc = new Scanner(System.in);
      System.out.println("Give input");
      input = sc.nextLine();
      sc.close();
      // Search()
      reducers = new ArrayList<>(); 
      for(int i = 0; i < 2; i++) {
         //(new Dummy("Larisa", "3/4/24", 3, 30.0, 3)).start();
         //(new Dummy("Lamia", "4/4/24", 2, 40.0, 4)).start(); 
         (new Dummy("Lamia", null, 0, 0.0, 0)).start();
         (new Dummy("Larisa", null, 0, 0.0, 0)).start();
      }
      Thread.sleep(2000);
      Collections.sort(reducers);         // sort reducers array list based on current id
      for(Reducer reducer : reducers) {   // for each reducer obejct in reducers arraylist
         reducer.printRooms();            // print results        
      }
   }  
}