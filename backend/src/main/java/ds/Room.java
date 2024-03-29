package ds;
// Room Class
public class Room 
{
    private int id;
    private String roomName;
    private String date;
    private int guests;
    private double price;
    private int stars;
    private String area;
    private int reviews;
    private String roomImage;

    // Constructor
    Room(int id, String name, String date, int guests, double price, int stars, String area, int reviews, String image)
    {
        this.id = id;
        this.roomName = name;
        this.date = date;
        this.guests = guests;
        this.price = price;
        this.stars = stars;
        this.area = area;
        this.reviews = reviews;
        this.roomImage = image;
    }

    // Getters
    public int getId()
    {
        return id;
    }

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

    // Setters
    public void setId(int id)
    {
        this.id = id;
    }

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
}