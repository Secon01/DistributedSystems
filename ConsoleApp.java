import java.time.LocalDate;
import java.sql.Date;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

public class ConsoleApp {

    boolean done;
    Scanner inp;
    Info information = new Info();

    public void welcome() {
        System.out.println("+---------------------------------+");
        System.out.println("|  You've logged in as a Manager  |");
        System.out.println("+---------------------------------+");

    }

    // Print Menu

    public void menu() {
        System.out.println("Please select a number for filtering or press 6 to continue");
        System.out.println(" 1) House Information ");
        System.out.println(" 2) Date / Availability");
        System.out.println(" 3) Continue ");
        System.out.println(" 0) Exit");
    }

    public void runMenu() {

        welcome();
        while (!done) {
            menu();
            int choice = getInput();
            performAction(choice);
        }

    }

    public int getInput() {
        inp = new Scanner(System.in); // initialize scanner
        int choice = -1;
        while (choice < 0 || choice > 3) { // user input number out of bounds
            try {
                System.out.println("Enter your selection: ");
                choice = Integer.parseInt(inp.nextLine()); // user input
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection! Please try again.");
            }
        }
        return choice;
    }

    // Perform action according to choice
    public void performAction(int choice) {
        switch (choice) {
            case 0:
                System.out.println(" Your room is now available for booking");
                done = true;
                break;
            case 1: // Path Input
                System.out.println("Enter the path of your json file ");
                information.setPath(inp.nextLine());
                break;
            case 2:
                System.out.println("Enter you start date");
                information.setAvailabilityDateStart(inp.nextLine());
                information.setAvailabilityDateEnd(inp.nextLine());

                break;
            case 3:
                System.out.println("These are your room's information ");
                System.out.println(information.path);
                System.out.println(information.availableDateS);
                System.out.println(information.availableDateE);
                break;
            default:
                System.out.println("An unknown error has occured!");
                break;
        }
    }

    // Info chunck

    public class Info {

        private String path;
        private String availableDateS;
        private String availableDateE;

        public void setPath(String a) {
            this.path = a;
        }

        public void setAvailabilityDateStart(String b) {
            this.availableDateS = b;
        }

        public void setAvailabilityDateEnd(String c) {
            this.availableDateE = c;
        }

    }

    public static void main(String[] args) {
        ConsoleApp insertion = new ConsoleApp();
        insertion.runMenu();

    }
}
