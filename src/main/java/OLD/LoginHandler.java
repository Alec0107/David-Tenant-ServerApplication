package OLD;

import SERVER.Models.GsonHelper;
import SERVER.Models.Account;
import SERVER.Service.DTService;
import com.google.gson.Gson;

public class LoginHandler {

    private GsonHelper gsonHelper = new GsonHelper();
    private Gson gson;
    private StringBuilder contentBuilder;

    public LoginHandler(StringBuilder contentBuilder) {
        this.contentBuilder = contentBuilder;
        this.gson = gsonHelper.getGson();
    }



    public String loginRequest(){

       Account account =  gson.fromJson(contentBuilder.toString().trim(), Account.class);

        //System.out.println("LOGINHANDLER");
        //System.out.println(gson.toJson(account));

        DTService authService = new DTService();
        AuthResponse loginResponse = authService.loginUser(account);
        String jsonString = gson.toJson(loginResponse, AuthResponse.class);

       // System.out.println(jsonString);

        StringBuilder responseBuilder = new StringBuilder();

        if(loginResponse.isExist()){
            responseBuilder.append("HTTP/1.1 /LoginRequest/  200 OK\r\n");
            responseBuilder.append("Content-Type: application/json\r\n");
            responseBuilder.append("Content-Length:" + jsonString.length()).append("\r\n");
            responseBuilder.append("\r\n");
            responseBuilder.append(jsonString);
            return responseBuilder.toString();
        }else{
            responseBuilder.append("HTTP/1.1 /LoginRequest/ 400 Bad Request\r\n");
            responseBuilder.append("Content-Type: application/json\r\n");
            responseBuilder.append("Content-Length:" + jsonString.length()).append("\r\n");
            responseBuilder.append("\r\n");
            responseBuilder.append(jsonString);
            return responseBuilder.toString();
        }

    }



}
