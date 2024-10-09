package SERVER.Handlers;

import SERVER.Models.ProductListsResponse;
import SERVER.Service.DTService;
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
        String jsonString = null;
        DTService authService = new DTService();
        ProductListsResponse productListsResponse =  authService.getProducts();

        jsonString = gson.toJson(productListsResponse, ProductListsResponse.class);

        StringBuilder responseBuilder = new StringBuilder();

        if(!jsonString.equals(null)){
             responseBuilder.append("HTTP/1.1 200 OK\r\n");
             responseBuilder.append("Content-Type: application/json\r\n");
             responseBuilder.append("Content-Length:" + jsonString.getBytes().length).append("\r\n");
             responseBuilder.append("\r\n");
             responseBuilder.append(jsonString);
             return responseBuilder.toString();
        }else{
            responseBuilder.append("HTTP/1.1 400 Bad Request\r\n");
            responseBuilder.append("Content-Type: application/json\r\n");
            responseBuilder.append("Content-Length:" + jsonString.length()).append("\r\n");
            responseBuilder.append("\r\n");
            responseBuilder.append(jsonString);
            return responseBuilder.toString();
        }

    }


}
