package SERVER.NetworkManager;

import SERVER.Handlers.CategoryProductHandler;
import SERVER.Handlers.LoginHandler;
import SERVER.Handlers.ProductHandler;
import SERVER.Handlers.SignupHandler;
import SERVER.Models.ProductListsResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RequestDispatcher implements Runnable {

    private Socket socket;
    private Gson gson;

    public RequestDispatcher(Socket socket) {
        this.socket = socket;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    @Override
    public void run() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            String line = "";
            String method = "";
            String path = "";
            int contentLength = 0;

            StringBuilder headerBuilder  = new StringBuilder();
            StringBuilder contentBuilder = new StringBuilder();

            while((line = reader.readLine()) != null && !line.isEmpty()){

                headerBuilder.append(line).append("\n");

                if(line.startsWith("POST") || (line.startsWith("GET")) ) {
                    String[] parts = line.split(" ");
                    method = parts[0];
                    path = parts[1];
                }

                if(line.startsWith("Content-Length:")){
                    String[] parts = line.split(":");
                    contentLength = Integer.parseInt(parts[1]);
                }

            }// end of while read

            System.out.println("==============RECEIVED===============");
            System.out.println("Header: \n" + headerBuilder); // for debugging
            System.out.println("Path: " + path + "\n");
            System.out.println("==============RESPONSE===============");


            if(contentLength > 0){
                char[] bodyChars = new char[contentLength];
                reader.read(bodyChars, 0, contentLength);
                String body  = new String(bodyChars);
                contentBuilder.append(body);

            };


            switch(method){

                case "POST":
                    if("/Signup".equals(path)){
                        SignupHandler signupHandler = new SignupHandler(contentBuilder);
                        String responseStringSignup = signupHandler.handleRequest();
                        writer.println(responseStringSignup);
                        writer.flush();
                        System.out.println(responseStringSignup);

                    }else if("/Login".equals(path)){
                        LoginHandler loginHandler = new LoginHandler(contentBuilder);
                        String responseStringLogin = loginHandler.loginRequest();
                        writer.println(responseStringLogin);
                        writer.flush();
                        System.out.println(responseStringLogin);
                    }
                    break;

                case "GET":
                    if("/Products".equals(path)){
                        ProductHandler productHandler = new ProductHandler();
                        String responseGetProducts =  productHandler.getProducts();
                        writer.println(responseGetProducts);
                        writer.flush();
                        System.out.println(responseGetProducts);

                    }else if("/ProductCategories".equals(path)){
                        CategoryProductHandler categoryProductHandler = new CategoryProductHandler();
                        ProductListsResponse productListsResponse = categoryProductHandler.response();
                        String jsonData = gson.toJson(productListsResponse);

                        StringBuilder headerBuilder1 = new StringBuilder();

                        if(productListsResponse != null){
                            headerBuilder1.append("HTTP/1.1 200 OK\r\n");
                            headerBuilder1.append("Transfer-Encoding: chunked\r\n");
                            headerBuilder1.append("Content-Type: application/json\r\n");
                            headerBuilder1.append("\r\n");

                            writer.print(headerBuilder1);
                            System.out.print(headerBuilder1);

                            //Send the data in chunks

                            int chunkSize = 1024;
                            for(int i = 0; i < jsonData.length(); i += chunkSize){
                                int end = Math.min(i + chunkSize, jsonData.length());
                                writer.println(jsonData.substring(i, end));
                                System.out.println(jsonData.substring(i, end));
                            }

                            writer.println("END_OF_JSON");
                            writer.flush();

                        }
                    }
                    break;



            }// switch


        } catch (IOException e) {
            throw new RuntimeException(e);}

        finally{
            closeSocket(); }// Close the client socket
    }

    private void closeSocket(){
        if(socket != null && !socket.isClosed()){
            try {
                socket.close();
                System.err.println("Client Socket Closed..");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}