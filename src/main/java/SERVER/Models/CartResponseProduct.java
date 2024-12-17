package SERVER.Models;

public class CartResponseProduct {
    private int userId;
    private String userName;
    private int productId;
    private String brandName;
    private String description;
    private double price;
    private String editorNote;
    private String imageUrl;
    private String category;
    private String size;
    private int productQty;

    public CartResponseProduct(int userId, String userName, int productId, String brandName, String description, double price, String editorNote, String imageUrl, String category, String size, int productQty) {
        this.userId = userId;
        this.userName = userName;
        this.productId = productId;
        this.brandName = brandName;
        this.description = description;
        this.price = price;
        this.editorNote = editorNote;
        this.imageUrl = imageUrl;
        this.category = category;
        this.size = size;
        this.productQty = productQty;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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

    public String getEditorNote() {
        return editorNote;
    }

    public void setEditorNote(String editorNote) {
        this.editorNote = editorNote;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getProductQty() {
        return productQty;
    }

    public void setProductQty(int productQty) {
        this.productQty = productQty;
    }

    @Override
    public String toString() {
        return String.format(
                "%-20s: %s\n" +    // userId
                        "%-20s: %s\n" +    // userName
                        "%-20s: %d\n" +    // productId
                        "%-20s: %s\n" +    // brandName
                        "%-20s: %s\n" +    // description
                        "%-20s: %.2f\n" +  // price
                        "%-20s: %s\n" +    // editorNote
                        "%-20s: %s\n" +    // imageUrl
                        "%-20s: %s\n" +    // category
                        "%-20s: %s\n" +    // size
                        "%-20s: %d\n",       // productQty
                "userId", userId,
                "userName", userName,
                "productId", productId,
                "brandName", brandName,
                "description", description,
                "price", price,
                "editorNote", editorNote,
                "imageUrl", imageUrl,
                "category", category,
                "size", size,
                "productQty", productQty);
    }
}
