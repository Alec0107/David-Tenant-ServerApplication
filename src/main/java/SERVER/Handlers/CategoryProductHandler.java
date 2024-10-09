package SERVER.Handlers;

import SERVER.Models.ProductListsResponse;
import SERVER.Service.DTService;

public class CategoryProductHandler {


    public ProductListsResponse response() {
        DTService authService = new DTService();

        ProductListsResponse productListsResponse = authService.getProductCategory();

        return productListsResponse;
    }

}

