package SERVER.Handlers;

import SERVER.Models.Account;
import SERVER.Models.AuthResponse;
import SERVER.Service.DTService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LoginHandler {

    private StringBuilder contentBuilder;
    private Gson gson;

    public LoginHandler(StringBuilder contentBuilder) {
        this.contentBuilder = contentBuilder;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient(); // Allow lenient parsing
        gsonBuilder.setPrettyPrinting();
        this.gson = gsonBuilder.create();
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
