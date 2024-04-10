package ds;
import java.time.LocalDate;
import java.util.Scanner;

public class DateRange  
{
    private LocalDate startDate;                                    // start date of date range
    private LocalDate endDate;                                      // start date of date range
    // Getters, setters
    public LocalDate getStartDate() 
    {
        return startDate;
    }

    public LocalDate getEndDate() 
    {
        return endDate;
    }

    public void setStartDate(String startDate) 
    {
        this.startDate = LocalDate.parse(startDate);                // parse a given string to local date object
    }

    public void setEndDate(String endDate) 
    {
        this.endDate = LocalDate.parse(endDate);                    // parse a given string to local date object
    }

    // Checks if the given local date object is in range of this date range object
    public boolean isInRange(LocalDate date) 
    {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    // Checks if one date range object is within the range of this date range object
    public boolean isWithinRange(DateRange other) 
    {
        if(this.getStartDate() == null || this.getEndDate() == null) {
            return false;
        }
        return !this.startDate.isBefore(other.startDate) && !this.endDate.isAfter(other.endDate);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) {
            return true;
        }
        if (obj == null || DateRange.class != obj.getClass()) {
            return false;
        }
        DateRange other = (DateRange) obj;
        if(other.getStartDate() == null || other.getEndDate() == null) {
            return false;
        }
        return this.isWithinRange(other);
    }

    public String toString()
    {
        return "[" + startDate + " - " + endDate + "]";
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