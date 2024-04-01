package ds;
// Dummy App for users
package ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Dummy extends Thread {
   private boolean exit;
   private Scanner sc;
   private Filter filter;
   private String hostname = "localhost";
   private int port = 8000;
   static String[] inputArgs;

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

   public void run() {
      Socket socket = null;

      try {
         socket = new Socket(this.hostname, this.port);
      } catch (IOException var41) {
         var41.printStackTrace();
      }

      try {
         try {
            Throwable var2 = null;
            Object var3 = null;

            try {
               PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

               label587: {
                  try {
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                     try {
                        if (inputArgs.length > 0 && inputArgs[0].equals("searchRoom")) {
                           sendSearchRoomRequest(out);

                           while(true) {
                              String responseLine;
                              if ((responseLine = in.readLine()) == null) {
                                 break label587;
                              }

                              System.out.println(responseLine);
                           }
                        }

                        System.out.println("Unknown command. Use 'getRoom' or 'newRoom'.");
                     } finally {
                        if (in != null) {
                           in.close();
                        }

                     }
                  } catch (Throwable var43) {
                     if (var2 == null) {
                        var2 = var43;
                     } else if (var2 != var43) {
                        var2.addSuppressed(var43);
                     }

                     if (out != null) {
                        out.close();
                     }

                     throw var2;
                  }

                  if (out != null) {
                     out.close();
                  }

                  return;
               }

               if (out != null) {
                  out.close();
                  return;
               }
            } catch (Throwable var44) {
               if (var2 == null) {
                  var2 = var44;
               } else if (var2 != var44) {
                  var2.addSuppressed(var44);
               }

               throw var2;
            }
         } catch (IOException var45) {
            var45.printStackTrace();
         }

      } finally {
         try {
            socket.close();
         } catch (IOException var40) {
            var40.printStackTrace();
         }

      }
   }

   private static void sendSearchRoomRequest(PrintWriter out) {
      String jsonBody = "{\"roomName\":\"Standard\",\"noOfPersons\":3,\"area\":\"Suburbs\",\"stars\":3,\"noOfReviews\":50,\"roomImage\":\"/images/standard.png\"}";
      out.println("POST /newRoom HTTP/1.1");
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
   }

   Dummy() {
   }

   public static void main(String[] args) {
      (new Dummy("Lamia", (String)null, 0, 0.0, 0)).start();
      (new Dummy("Larisa", "25-03-2024", 0, 0.0, 0)).start();
   }
}
