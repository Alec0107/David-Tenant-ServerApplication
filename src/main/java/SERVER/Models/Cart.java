package SERVER.Models;

public class Cart {

    private int userID;
    private int productID;
    private String size;
    private int qty;

    public Cart(int userID, int productID, String size, int qty) {
        this.userID = userID;
        this.productID = productID;
        this.size = size;
        this.qty = qty;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "userID=" + userID +
                ", productID=" + productID +
                ", size='" + size + '\'' +
                ", qty=" + qty +
                '}';
    }
}
