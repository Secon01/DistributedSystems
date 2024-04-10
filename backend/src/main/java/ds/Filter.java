package ds;

import java.lang.reflect.Field;

// Filters holder
public class Filter extends Request
{
    private String area;
    private DateRange dateRange;
    private int guests;
    private double price;
    private int stars;

    Filter()
    {
        this.dateRange = new DateRange();
    }

    // Setters for filter attributes
    public void setArea(String a)
    {
        this.area = a;
    }

    public void setDate(String start, String end)
    {
        this.dateRange.setStartDate(start);      // set end date of date range 
        this.dateRange.setEndDate(end);          // set end date of date range 
    }

    public void setGuests(int g)
    {
        this.guests = g;
    }

    public void setPrice(double p)
    {
        this.price = p;
    }

    public void setStars(int s)
    {
        this.stars = s;
    }

    // Getters
    public String getArea() {
        return area;
    }

    public DateRange getDate() {
        return dateRange;
    }

    public int getGuests() {
        return guests;
    }

    public double getPrice() {
        return price;
    }

    public int getStars() {
        return stars;
    }

    // Computes how many non null or 0 values does this object has
    public int numNonZero()
    {
        int count = 0;
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);

                if(value != null && !value.equals(0) && !value.equals(0.0)) {
                    count++;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public String toString()
    {
        return "[" + this.area + ", " + this.dateRange.toString() + ", " + this.guests + 
                ", " + this.price + ", " + this.stars + "]";
    }
}