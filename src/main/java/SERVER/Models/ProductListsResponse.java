package SERVER.Models;

import java.util.ArrayList;
import java.util.List;

public class ProductListsResponse {

    private String message;
    private List<Products> productLists;
    private ArrayList<CategoryProduct> categoryProducts;

    public ProductListsResponse() {
        categoryProducts = new ArrayList<>();
    }

    public ProductListsResponse(String message, List<Products> productLists) {
        this.message = message;
        this.productLists = productLists;
    }

    public ProductListsResponse(ArrayList<CategoryProduct> categoryProducts){
        this.categoryProducts = categoryProducts;
    }

    public ArrayList<CategoryProduct> getCategoryProducts() {
        return categoryProducts;
    }

    public void setCategoryProducts(ArrayList<CategoryProduct> categoryProducts) {
        this.categoryProducts = categoryProducts;
    }
}
