public class Room
{
    String name;
    int guests;
    String area;
    int stars;
    int reviews;
    double price;

    // Room constructor
    Room(String n, int g, String a, int s, int r, double p)
    {
        this.name = n;
        this.guests = g;
        this.area = a;
        this.stars = s;
        this.reviews = r;
        this.price = p;
    }

    // Setters
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
}   