package ds;
// Dummy App for users

import java.util.Scanner;

public class Dummy 
{
    boolean exit;                                           // exit menu
    Scanner sc;                                             // scanner 
    Filter filter = new Filter();                           // create filter object   
    // Header of menu
    public void header()
    {
        System.out.println("+----------------------------+");
        System.out.println("|   Welcome to Housebooking  |");
        System.out.println("+----------------------------+");
    }

    // Menu
    public void menu()
    {
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

    // Running menu in a loop
    public void runMenu()
    {
        header();
        while(!exit) {
            menu();                                         // show menu
            int choice = getInput();                        // get number input
            performAction(choice);                          // perform action according to choice
        }

    }

    // Getting user input
    public int getInput()
    {
        sc = new Scanner(System.in);                        // initialize scanner
        int choice = -1;
        while (choice < 0 || choice > 6) {                  // user input number out of bounds
            try {
                System.out.println("Enter your selection: ");
                choice = Integer.parseInt(sc.nextLine());   // user input
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection! Please try again.");
            }
        }
        return choice;
    }

    // Perform action according to choice
    public void performAction(int choice)
    {
        switch (choice) {
            case 0:
                System.out.println("Thank you for using HouseBooking!");
                System.exit(0);
                break;
            case 1:                                                         // area input 
                System.out.println("Enter area's name");
                filter.setArea(sc.nextLine());
                break;
            case 2:                                                         // date input
                System.out.println("Enter a date");
                filter.setDate(sc.nextLine());
                break;
            case 3:                                                         // number of guests input
                System.out.println("Enter a number of guests");
                filter.setGuests(Integer.parseInt(sc.nextLine()));
                break;
            case 4:                                                         // price input
                System.out.println("Enter a price");
                filter.setPrice(Integer.parseInt(sc.nextLine()));
                break;
            case 5:                                                         // number of stars input
                System.out.println("Enter a number of stars");
                filter.setStars(Integer.parseInt(sc.nextLine()));
                break;
            case 6:
                System.out.println("Your results are: ");
                System.out.println(filter.getArea());
                System.out.println(filter.getDate());
                System.out.println("Guests: "+ filter.getGuests());
                System.out.println("Price: " + filter.getPrice());
                System.out.println("Stars: " +filter.getStars());
                System.out.println("Number of filters: " + filter.numFilter());
                break;
            default:
                System.out.println("An unknown error has occured!");
                break;
        }
    }
    public static void main(String[] args) {
        Dummy dummyApp = new Dummy();
        dummyApp.runMenu();
    }    
}
