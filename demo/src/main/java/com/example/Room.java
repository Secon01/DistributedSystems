package demo.src.main.java.com.example;

public class Room
{
    int id;
    String name;
    int guests;
    String area;
    int stars;
    int reviews;
    double price;

    // Room constructor
    Room(int i, String n, int g, String a, int s, int r, double p)
    {
        this.id = i;
        this.name = n;
        this.guests = g;
        this.area = a;
        this.stars = s;
        this.reviews = r;
        this.price = p;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getters
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public int getGuests() {
        return guests;
    }

    public String getArea() {
        return area;
    }

    public int getStars() {
        return stars;
    }

    public int getReviews() {
        return reviews;
    }

    public double getPrice() {
        return price;
    }

    // Prints out room's information all together
    public void getAll()
    {
        System.out.println("Name: " + this.name);
        System.out.println("Guests: " + this.guests);
        System.out.println("Area: " + this.area);
        System.out.println("Stars " + this.stars);
        System.out.println("Reviews: " + this.reviews);
        System.out.println("Price: " + this.price);
    }

    @Override
    public String toString()
    {
        return "Room [name = " + name +
                ", guests = " + guests +
                ", area = " + area +
                ", stars = " + stars +
                ", reviews = " + reviews +
                ", price = " + price + "]";
    }
}   