// Filters holder
public class Filter extends Request
{
    private String area;
    private String date;
    private int guests;
    private int price;
    private int stars;

    // Setters for filter attributes
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
}