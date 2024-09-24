package SERVER.Models;

import java.util.List;

public class ProductListsResponse {

    private String message;
    private List<Products> products;

    public ProductListsResponse(String message, List<Products> products) {
        this.message = message;
        this.products = products;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }
}
