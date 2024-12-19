package OLD;

import SERVER.Models.GsonHelper;
import SERVER.Service.DTService;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CartProductHandler {

    private GsonHelper gsonHelper = new GsonHelper();
    private Gson gson;
    private StringBuilder contentBuilder;
    private StringBuilder responseBuilder;
    private DTService dtService;

    public CartProductHandler(StringBuilder contentBuilder) {
        this.contentBuilder = contentBuilder;
        this.gson = gsonHelper.getGson();
        dtService = new DTService();
    }

    public CartResponse cartProductsRequest() {

        int userId = extractUserId(contentBuilder.toString());

        return dtService.getCartProducts(userId);
    }

    public int extractUserId(String idString){
        Pattern pattern = Pattern.compile("User_id:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(idString);
        if(matcher.find()){
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

}
