// Dummy App for users
// Communication protocol is based on HTTP
package ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Dummy extends Thread {
   private boolean exit;
   private static Scanner sc;
   private Filter filter;
   private static String hostname = "localhost";
   private static int port = 8000;
   static String[] inputArgs;
   public static String input;
   private String roomName;                           // room name for booking
   private Review review;
   private String regex = "\\d{4}-\\d{2}-\\d{2}";           // regular expression to match the format YYYY-MM-DD

   Dummy(String area, String startDate, String endDate, int guests, double price, int stars) {
      this.filter = new Filter();
      this.filter.setArea(area);
      this.filter.setDate(startDate, endDate);
      this.filter.setGuests(guests);
      this.filter.setPrice(price);
      this.filter.setStars(stars);
      System.out.println(filter.toString());
   }

   Dummy(String roomName)
   {
      this.roomName = roomName;
   }

   Dummy(double stars, String roomName)
   {
      review = new Review(stars, roomName);
   }

   Dummy() throws InterruptedException 
   {
      this.filter = new Filter();
      runMenu();
   }

   public void header() {
      System.out.println("+----------------------------+");
      System.out.println("|   Welcome to Housebooking  |");
      System.out.println("+----------------------------+");
   }

   public void menu() {
      System.out.println();
      System.out.println("Please select a number and press enter to proceed. If you want to exit press 0 and enter to proceed.");
      System.out.println(" 1) Area");
      System.out.println(" 2) Date Range");
      System.out.println(" 3) Number of guests");
      System.out.println(" 4) Price");
      System.out.println(" 5) Number of stars");
      System.out.println(" 6) Search");
      System.out.println(" 7) Book");
      System.out.println(" 0) Exit");
   }

   public void runMenu() throws InterruptedException {
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

      while(choice < 0 || choice > 7) {
         try {
            System.out.println("Enter your selection: ");
            choice = Integer.parseInt(this.sc.nextLine());
         } catch (NumberFormatException var3) {
            System.out.println("Invalid selection! Please try again.");
         }
      }
      return choice;
   }

   public void performAction(int choice) throws InterruptedException {
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
            System.out.println("Enter a starting date");
            // Check if the user input matches the desired format  
            String startDate = null;
            while(true) {
               Scanner start = new Scanner(System.in);
               startDate = start.nextLine();    
               if (Pattern.matches(regex, startDate)) {    // check if regular expression of date format matches user's input
                  break;
               } else {
                  System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format!");
               }                                                                        
            }  
            System.out.println("Enter your ending date");
            while (true) {
               Scanner end = new Scanner(System.in);
               String endDate = end.nextLine();                            
               if (Pattern.matches(regex, endDate)) {      // check if regular expression of date format matches user's input
                  this.filter.setDate(startDate, endDate);
                  break;
               } else {
                  System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format!");
               }                                                                        
            }            
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
            input = "search";
            search();
            break;
         case 7:
            Scanner rn = new Scanner(System.in);
            System.out.println("Type the name of the room you wish to book");
            roomName = rn.nextLine();
            input = "book";
            book();         
            break;
         default:
            System.out.println("An unknown error has occured!");
      }
   }

   // Sends filter to master and prints results
   private void search() throws InterruptedException
   {
      ArrayList<Reducer> reducers = new ArrayList<>();   // array list with reducer objects for printing
      this.run();                                        // create request thread and send it to master
      Thread.sleep(1000);
      Collections.sort(reducers);                        // sort reducers array list based on current id
      for(Reducer reducer : reducers) {                  // for each reducer obejct in reducers arraylist
         reducer.printRooms();                           // print results
      }
   }

   // Sends request for booking a room
   private void book()
   {
      this.run();
   }

   // Serializes filter object to json 
   private String serializeFilter(Filter filter)
   {
      Gson gson = new GsonBuilder()
         .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
         .create();
      return gson.toJson(filter);
   }

    // Deserialize json to reducer object  
    private Reducer deserializeReducer(String json)
    {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();
        return gson.fromJson(json, Reducer.class);
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
      if (input.equals("search")) {                      // check if input is equal to 'search' 
         ArrayList<Reducer> reducers = new ArrayList<>();         // array list with reducer objects for printing
         sendSearchRoomRequest(out);                              // send request for searching a room
         String json = extractBody(in);                           // extract json from http request body
         if(json == null) {                                       // if json string is null 
            System.out.println("Room not found");     
            return;
         }
         Reducer reducer = deserializeReducer(json);              // create reducer object from json  
         synchronized(reducer) {
            reducers.add(reducer);                                // add reducer objects with results in reducers array
         }   
         Thread.sleep(1000);
         Collections.sort(reducers);                              // sort reducers array list based on current id
         for(Reducer r : reducers) {                              // for each reducer obejct in reducers arraylist
            r.printRooms();                                       // print results
         }   
      }  else if(input.equals("book")) {                 // check if input is equal to 'search'
         sendBookRoomRequest(out);                                // send request for booking a room
         // Read the response
         String responseLine;
         while ((responseLine = in.readLine()) != null) {
            System.out.println(responseLine); 
         }   
      } else if(input.equals("review")) {       // check if input is equal to 'search'
         sendNewReviewRequest(out);                         // send request for booking a room
         // Read the response
         String responseLine;
         while ((responseLine = in.readLine()) != null) {
         System.out.println(responseLine);
         }
      }
      else {
         System.out.println("Unknown command. Use 'getRoom' or 'newRoom'.");
         sc.close();                                  // close scanner
         return;
      }
      } catch (IOException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
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
      String jsonBody = serializeFilter(this.filter);
      out.println("POST /searchRoom HTTP/1.1");
      out.println("Host: localhost");
      out.println("Content-Type: application/json");
      out.println("Content-Length: " + jsonBody.length());
      out.println("Connection: close");
      out.println();
      out.println(jsonBody);
   }

   private void sendBookRoomRequest(PrintWriter out) {
      String jsonBody = new Gson().toJson(roomName);
      out.println("POST /bookRoom HTTP/1.1");
      out.println("Host: localhost");
      out.println("Content-Type: application/json");
      out.println("Content-Length: " + jsonBody.length());
      out.println("Connection: close");
      out.println();
      out.println(jsonBody);
   }

   private void sendNewReviewRequest(PrintWriter out) {
      String jsonBody = new Gson().toJson(this.review);
      //System.out.println(jsonBody);
      out.println("POST /giveReview HTTP/1.1");
      out.println("Host: localhost");
      out.println("Content-Type: application/json");
      out.println("Content-Length: " + jsonBody.length());
      out.println("Connection: close");
      out.println();
      out.println(jsonBody);
   }
   public static void main(String[] args) throws IOException, InterruptedException {
      //new Dummy().start(); 
      sc = new Scanner(System.in);
      System.out.println("Give input");
      input = sc.nextLine();
      sc.close();
      if(input .equals("search")) {
         // Search()
         ArrayList<Reducer> reducers = new ArrayList<>(); 
         for(int i = 0; i < 1; i++) {
            (new Dummy(null, "2024-04-06", "2024-04-07", 0, 0.0, 0)).start(); 
         }
         Thread.sleep(1000);
         Collections.sort(reducers);         // sort reducers array list based on current id
         for(Reducer reducer : reducers) {   // for each reducer obejct in reducers arraylist
            reducer.printRooms();            // print results        
         }  
      } else if (input.equals("book")){
         // Book()
         for(int i = 0; i < 2; i++) {
            new Dummy("Double Room").start();
            /* 
            new Dummy("Single Room").start();
            new Dummy("Family Room").start();
            new Dummy("Suite").start();
            new Dummy("Deluxe Suite").start();
            new Dummy("Luxury Suite 1").start();
            new Dummy("Economy Room").start();
            new Dummy("Penthouse").start();
            new Dummy("Standard Room").start();
            new Dummy("Executive Suite").start();
            */ 
            //Thread.sleep(1000);  
         }         
      } else if(input.equals("review")) {
            new Dummy(4.2,"Single Room").start();
            new Dummy(3.1,"Luxury Suite 1").start();
            new Dummy(2.6,"Executive Suite").start();
      }
   }  
}