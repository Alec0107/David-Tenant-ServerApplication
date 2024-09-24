package SERVER.Handlers;

import SERVER.Models.Account;
import SERVER.Models.AuthResponse;
import SERVER.Service.AuthService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SignupHandler {

    private StringBuilder stringBuilder;
    private Gson gson;
    private String string;

    public SignupHandler(StringBuilder stringBuilder){
        this.stringBuilder = stringBuilder;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();

    }

    public String handleRequest(){

        Account acc = gson.fromJson(String.valueOf(stringBuilder), Account.class);
        AuthService authService = new AuthService();

        AuthResponse signupResponse = authService.addUser(acc);
        String jsonString = gson.toJson(signupResponse, AuthResponse.class);

        StringBuilder responseBuilder = new StringBuilder();

        if(!signupResponse.isExist()){
            responseBuilder.append("HTTP/1.1 200 OK\r\n");
            responseBuilder.append("Content-Type: application/json\r\n");
            responseBuilder.append("Content-Length:" + jsonString.length()).append("\r\n");
            responseBuilder.append("\r\n");
            responseBuilder.append(jsonString);
            return responseBuilder.toString();
        }else{
            responseBuilder.append("HTTP/1.1 git400 Bad Request\r\n");
            responseBuilder.append("Content-Type: application/json\r\n");
            responseBuilder.append("Content-Length:" + jsonString.length()).append("\r\n");
            responseBuilder.append("\r\n");
            responseBuilder.append(jsonString);
            return responseBuilder.toString();
        }


    }



}
