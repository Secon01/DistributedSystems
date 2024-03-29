public class Room {
    private String roomName;
    private String date;
    private int guestNum;
    private double price;
    private int stars;
    private String area;
    private int reviews;
    private String roomImage;

    // Getters
    public String getRoomName() {
        return roomName;
    }

    public String getDate() {
        return date;
    }

    public int getGuestNum() {
        return guestNum;
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
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGuestNum(int guestNum) {
        this.guestNum = guestNum;
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