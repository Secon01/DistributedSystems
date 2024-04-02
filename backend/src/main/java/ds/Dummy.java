// Dummy App for users
// Communication protocol is based on HTTP
package ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import com.google.gson.Gson;

public class Dummy extends Thread {
   private boolean exit;
   private Scanner sc;
   private Filter filter;
   private static String hostname = "localhost";
   private static int port = 8000;
   static String[] inputArgs;
   public static String input;

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
        if (input.equals("searchRoom")){
            sendSearchRoomRequest(out);
        } else {
            System.out.println("Unknown command. Use 'getRoom' or 'newRoom'.");
            sc.close();                 // close scanner
            return;
        }
        // Read the response
        //String responseLine;
        //while ((responseLine = in.readLine()) != null) {
        //    System.out.println(responseLine);
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

   Dummy(String area, String date, int guests, double price, int stars) {
        this.filter = new Filter();
        this.filter.setArea(area);
        this.filter.setDate(date);
        this.filter.setGuests(guests);
        this.filter.setPrice(price);
        this.filter.setStars(stars);
        System.out.println(filter.toString());
   }

   Dummy() {
   }

   public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Give input");
        input = sc.nextLine();
        for(int i = 0; i < 10; i++) {
            (new Dummy("Lamia", null, 0, 0.0, 0)).start();
            
        }
        //(new Dummy("Larisa", "25-03-2024", 0, 0.0, 0)).start();
        sc.close();
   }
}