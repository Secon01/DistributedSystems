package ds;

import java.lang.reflect.Field;

// Filters holder
public class Filter 
{
    private int id;
    private String area;
    private String date;
    private int guests;
    private int price;
    private int stars;

    // Setters for filter attributes
    public void setId(int id) {
        this.id = id;
    }

    public void setArea(String a)
    {
        this.area = a;
    }

    public void setDate(String d)
    {
        this.date = d;
    }

    public void setGuests(int g)
    {
        this.guests = g;
    }

    public void setPrice(int p)
    {
        this.price = p;
    }

    public void setStars(int s)
    {
        this.stars = s;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getArea() {
        return area;
    }

    public String getDate() {
        return date;
    }

    public int getGuests() {
        return guests;
    }

    public int getPrice() {
        return price;
    }

    public int getStars() {
        return stars;
    }

    // Computes how many non null or 0 values does this object has
    public int numFilter()
    {
        int count = 0;
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);

                if(value != null && !value.equals(0)) {
                    count++;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
}