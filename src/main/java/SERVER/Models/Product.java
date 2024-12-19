package SERVER.Models;

import java.util.List;

public class Product {

    private int productID;
    private String productName;
    private String brandName;
    private String productDescription;
    private double basePrice;
    private String categoryName;
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getNewIn() {
        return isNewIn;
    }

    public void setNewIn(Boolean newIn) {
        isNewIn = newIn;
    }

    private Boolean isNewIn;
    private Boolean isPopular;
    private List<String> imageUrls;
    private List<SizeStock> sizeStocks;


    /** Sizes and Stocks **/

    public static class SizeStock{

        private String size;
        private int stock;

        public SizeStock(String size, int stock) {
            this.size = size;
            this.stock = stock;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        @Override
        public String toString() {
            return "Size=" + size + " Stock=" + stock;
        }
    }



    /** Product **/



    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Boolean getNew() {
        return isNewIn;
    }

    public void setNew(Boolean isNewIn) {
        this.isNewIn = isNewIn;
    }

    public Boolean getPopular() {
        return isPopular;
    }

    public void setPopular(Boolean popular) {
        isPopular = popular;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<SizeStock> getSizeStocks() {
        return sizeStocks;
    }

    public void setSizeStocks(List<SizeStock> sizeStocks) {
        this.sizeStocks = sizeStocks;
    }

    @Override
    public String toString() {
        return "Product{\n" +
                "  ProductID    : " + productID + "\n" +
                "  ProductName  : " + productName + "\n" +
                "  BrandName    : " + brandName + "\n" +
                "  Description  : " + productDescription + "\n" +
                "  BasePrice    : " + basePrice + "\n" +
                "  CategoryName : " + categoryName + "\n" +
                "  Gender       : " + gender + "\n" +
                "  isNew        : " + isNewIn + "\n" +
                "  isPopular    : " + isPopular + "\n" +
                "  ImageUrls    : " + imageUrls + "\n" +
                "  SizeStocks   : " + sizeStocks + "\n" +
                '}';
    }
}


