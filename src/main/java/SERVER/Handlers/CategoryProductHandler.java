package SERVER.Handlers;

import SERVER.Models.CategoryProduct;
import SERVER.Models.ProductListsResponse;
import SERVER.Service.AuthService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class CategoryProductHandler {


    public ProductListsResponse response() {
        AuthService authService = new AuthService();

        ProductListsResponse productListsResponse = authService.getProductCategory();

        return productListsResponse;
    }

}

