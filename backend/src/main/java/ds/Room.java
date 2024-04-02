package ds;

import java.lang.reflect.Field;

// Room Class
public class Room extends Request
{
    private String roomName;
    private String date;
    private int guests;
    private double price;
    private int stars;
    private String area;
    private int reviews;
    private String roomImage;
    private String StartDate;
    private String EndDate;

    // Constructor
    Room(String name, String date, int guests, double price, int stars,
            String area, int reviews, String image, String startDate, String endDate)
    {
        this.roomName = name;
        this.date = date;
        this.guests = guests;
        this.price = price;
        this.stars = stars;
        this.area = area;
        this.reviews = reviews;
        this.roomImage = image;
        this.StartDate = startDate;
        this.EndDate = endDate;
    }
    // Default constructor
    Room()
    {

    }

    // Getters
    public String getRoomName() {
        return roomName;
    }

    public String getDate() {
        return date;
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

    public String getArea() {
        return area;
    }

    public int getReviews() {
        return reviews;
    }

    public String getRoomImage() {
        return roomImage;
    }

    public String getEndDate() {
        return EndDate;

    }

    public String getStartDate() {
        return StartDate;

    }
    // Setters
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGuests(int guestNum) {
        this.guests = guestNum;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }

    public void setStartDate(String StartDate) {
        this.StartDate = StartDate;
    }

    public void setEndDate(String EndDate) {
        this.EndDate = EndDate;
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

    // Copies this object to another
    public Room copy()
    {
        return new Room(this.roomName, this.date, this.guests, this.price, 
                        this.stars, this.area, this.reviews, this.roomImage, this.StartDate, this.EndDate);
    }

    public String toString()
    {
        return "[" + this.roomName + ", " + this.date + ", " + this.guests + ", " + this.price + ", " + this.stars + ", "
                    + this.area + ", " + this.reviews + ", " + this.roomImage + ", " + this.EndDate + ", " +this.StartDate + "]";
    }
}