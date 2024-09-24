package SERVER.Models;

public class Products {

    private String name;
    private String description;
    private double price;
    private String image_url;

    public Products(String name, String description, double price, String image_url) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
