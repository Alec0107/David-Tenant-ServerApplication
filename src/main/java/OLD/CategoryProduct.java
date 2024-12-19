package OLD;

public class CategoryProduct {

    private int id;
    private String brand_name;
    private String description;
    private double price;
    private int stock;
    private String editor_note;
    private String image_url;
    private String category_name;

    public CategoryProduct(int id, String brand_name, String description, double price, int stock, String editor_note, String image_url, String category_name) {
        this.id = id;
        this.brand_name = brand_name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.editor_note = editor_note;
        this.image_url = image_url;
        this.category_name = category_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getEditor_note() {
        return editor_note;
    }

    public void setEditor_note(String editor_note) {
        this.editor_note = editor_note;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    @Override
    public String toString() {
        return "CategoryProduct{" +
                "id=" + id +
                ", brand_name='" + brand_name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", editor_note='" + editor_note + '\'' +
                ", image_url='" + image_url + '\'' +
                ", category_name='" + category_name + '\'' +
                '}';
    }
}
