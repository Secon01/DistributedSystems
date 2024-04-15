package ds;
import java.time.LocalDate;

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
        return (this.startDate.isAfter(other.startDate) || this.startDate.isEqual(startDate)) && 
                (this.endDate.isBefore(other.endDate) ||this.endDate.isEqual(other.endDate));
    }

    // Override equals method to compare if a given data range object 
    // is within the range of this data range object
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) {                                           // if two objects are the same 
            return true;
        }
        if (obj == null || DateRange.class != obj.getClass()) {     // if given obj is null or its class isn't DataRange 
            return false;
        }
        if (this.getStartDate() == null || this.getEndDate() == null) {
            return false;
        }
        DateRange other = (DateRange) obj;                          // cast given object to data range object
        return this.isWithinRange(other);                           // return boolean value of isWithinRange method
    }

    public String toString()
    {
        return "[" + startDate + " - " + endDate + "]";
    }

    public static void main(String[] args) {
        /* 
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
        */

        DateRange dr = new DateRange();
        dr.setStartDate("2024-04-02");
        dr.setEndDate("2024-04-08");

        DateRange dr2 = new DateRange();
        dr2.setStartDate("2024-04-02");
        dr2.setEndDate("2024-04-07");

        System.out.println(dr2.isWithinRange(dr));
        System.out.println(dr2.equals(dr));
    }
}