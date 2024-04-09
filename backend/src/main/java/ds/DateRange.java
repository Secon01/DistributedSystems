package ds;
import java.time.LocalDate;
import java.util.Scanner;

public class DateRange  
{
    private LocalDate startDate;                                    // start date of date range
    private LocalDate endDate;                                      // start date of date range
    /* 
    // Date range constructor
    public DateRange(LocalDate startDate, LocalDate endDate) 
    {
        this.startDate = startDate;                                 // initialize start date
        this.endDate = endDate;                                     // initialize end date
    }
    */
    // Getters, setters
    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = LocalDate.parse(startDate);                // parse a given string to local date object
    }

    public void setEndDate(String endDate) {
        this.endDate = LocalDate.parse(endDate);                    // parse a given string to local date object
    }

    // Checks if the given date is in range
    public boolean isInRange(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the date (YYYY-MM-DD): ");
        String userInput = sc.nextLine();
        LocalDate date;
        try {
            date = LocalDate.parse(userInput);
            System.out.println("Given date: " + date);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format.");
        }
        sc.close();
    }
}