package ds;

import java.lang.reflect.Field;

// Room Class
public class Room extends Request
{
    private String roomName;
    private int guests;
    private double price;
    private int stars;
    private String area;
    private int reviews;
    private String roomImage;
    private String startDate;
    private String endDate;
    private boolean available;
    private DateRange dateRange;
    // Constructor
    Room(String name, int guests, double price, int stars,
            String area, int reviews, String image, String startDate, String endDate, boolean available)
    {
        this.roomName = name;
        this.guests = guests;
        this.price = price;
        this.stars = stars;
        this.area = area;
        this.reviews = reviews;
        this.roomImage = image;
        this.startDate = startDate;
        this.endDate = endDate;
        this.available = available;
        this.dateRange = new DateRange();
        this.dateRange.setStartDate(startDate);
        this.dateRange.setEndDate(endDate);
    }
    // Default constructor
    Room()
    {

    }

    // Getters
    public DateRange getDateRange() {
        return dateRange;
    }

    public String getRoomName() {
        return roomName;
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
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public boolean getAvailable()
    {
        return available;
    }
    // Setters
    public void setRoomName(String roomName) {
        this.roomName = roomName;
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
        this.startDate = StartDate;
    }

    public void setEndDate(String EndDate) {
        this.endDate = EndDate;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
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
        return new Room(this.roomName, this.guests, this.price, 
                        this.stars, this.area, this.reviews, this.roomImage, this.startDate, this.endDate, this.available);
    }

    public String toString()
    {
        return "Name: " + this.roomName + "\n" +
               "Area: " + this.area + "\n" +         
               "Number of guests: " + this.guests + "\n" + 
               "Price: " + this.price + "\n" + 
               "Stars: " + this.stars + "\n" +  
               "Reviews: " + this.reviews + "\n" +
               "Image: " + this.roomName + "\n" +
               "Available: " + this.available + "\n" +
               "Start date: " + this.startDate + "\n" +
               "End date: " + this.endDate;
    }
}