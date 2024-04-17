package ds;
import java.time.LocalDate;

public class DateRange  
{
    private LocalDate startDate;                                    // start date of date range
    private LocalDate endDate;                                      // end date of date range
    
    // Getters
    public LocalDate getStartDate()                                 // get the starting date
    {
        return startDate;
    }

    public LocalDate getEndDate()                                   // get the ending date
    {
        return endDate;
    }
    //Setters
    public void setStartDate(String startDate)                      // set the starting date
    {
        this.startDate = LocalDate.parse(startDate);                // parse a given string to local date object
    }
    public void setEndDate(String endDate)                          // set the ending date
    {
        this.endDate = LocalDate.parse(endDate);                    // parse a given string to local date object
    }
    // Checks if one date range object is within the range of this date range object
    public boolean isWithinRange(DateRange other) 
    {
        return (this.startDate.isAfter(other.startDate) || this.startDate.isEqual(other.startDate)) && 
                (this.endDate.isBefore(other.endDate) || this.endDate.isEqual(other.endDate));
    }
    // Override equals method to compare if a given data range object 
    // is within the range of this data range object
    @Override
    public boolean equals(Object obj)
    {
        //if(this == obj) {                                                // if two objects are the same 
        //    return true;
        //}
        if (obj == null || DateRange.class != obj.getClass()) {            // if given obj is null or its class isn't DataRange return false
            return false;
        }
        if (this.getStartDate() == null || this.getEndDate() == null) {    // if one of the properties, of the object that the method is called to, is null
            return false;                                                  // return false
        }
        DateRange other = (DateRange) obj;                                 // cast given object to data range object
        return this.isWithinRange(other);                                  // return boolean value of isWithinRange method
    }
    // Give the DateRange object's properties as a string
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
        /*
        DateRange filter = new DateRange();
        filter.setStartDate("2024-04-03");
        filter.setEndDate("2024-04-06");

        DateRange room = new DateRange();
        room.setStartDate("2024-04-02");
        room.setEndDate("2024-04-07");

        System.out.println(filter.isWithinRange(room));
        System.out.println(filter.equals(room));
        */
    }
}
