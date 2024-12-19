package OLD;

import SERVER.Models.GsonHelper;
import SERVER.Service.DTService;
import com.google.gson.Gson;

public class CartHandler {

    private GsonHelper gsonHelper = new GsonHelper();
    private Gson gson;
    private StringBuilder contentBuilder;
    private StringBuilder responseBuilder;
    private DTService dtService;

    public CartHandler(StringBuilder contentBuilder) {
        this.contentBuilder = contentBuilder;
        this.gson = gsonHelper.getGson();
        dtService = new DTService();
    }

    public String cartRequest(){
        Cart cart = gson.fromJson(contentBuilder.toString(), Cart.class);

        responseBuilder = new StringBuilder();

        if(dtService.addToCart(cart)){
            responseBuilder.append("HTTP/1.1 /CartResponse/  200 OK\r\n");
            responseBuilder.append("isSuccess:True");
        }else{
            responseBuilder.append("HTTP/1.1 /CartResponse/  500 BAD REQUEST\r\n");
            responseBuilder.append("isSuccess:False");
        }
        return responseBuilder.toString();
    }

}
