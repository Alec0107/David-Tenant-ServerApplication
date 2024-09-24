package SERVER.Handlers;

import SERVER.Models.ProductListsResponse;
import SERVER.Service.AuthService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProductHandler {

    private Gson gson;

    public ProductHandler(){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    public String getProducts(){
        AuthService authService = new AuthService();
        ProductListsResponse productListsResponse =  authService.getProducts();

        String jsonString = gson.toJson(productListsResponse, ProductListsResponse.class);

        StringBuilder responseBuilder = new StringBuilder();

        responseBuilder.append("HTTP/1.1 /GetProductRequest/  200 OK\r\n");
        responseBuilder.append("Content-Type: application/json\r\n");
        responseBuilder.append("Content-Length:" + jsonString.length()).append("\r\n");
        responseBuilder.append("\r\n");
        responseBuilder.append(jsonString);


        return responseBuilder.toString();
    }


}
