package SERVER.NetworkManager;

import SERVER.Handlers.CategoryProductHandler;
import SERVER.Handlers.LoginHandler;
import SERVER.Handlers.ProductHandler;
import SERVER.Handlers.SignupHandler;
import SERVER.Models.ProductListsResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.FileNameMap;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Dispatcher implements Runnable {

    private Socket socket;
    private Gson gson;

    public Dispatcher(Socket socket) {
        this.socket = socket;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.setLenient(); // Allow lenient parsing
        gson = gsonBuilder.create();
    }


    @Override
    public void run() {

        boolean isKeepAlive = true;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            StringBuilder headerbuilder = new StringBuilder();
            StringBuilder contentBuilder = new StringBuilder();

            String line = "";
            String connection = "";
            String method = "";
            String path = "";

            int contentLength = 0;



            while (isKeepAlive) {

                while ((line = reader.readLine()) != null && !line.isEmpty()) {

                    headerbuilder.append(line).append("\r\n");

                    if (line.startsWith("POST") || (line.startsWith("GET"))) {
                        String[] parts = line.split(" ");
                        method = parts[0];
                        path = parts[1];
                    }

                    if (line.startsWith("Content-Length:")) {
                        String[] parts = line.split(":");
                        contentLength = Integer.parseInt(parts[1]);
                    }


                    if (line.startsWith("Connection: ")) {
                        String[] parts = line.split(": ");
                        connection = parts[1];
                        if("Close".equals(connection)){
                            isKeepAlive = false;
                        }
                    }
                }

                if (contentLength > 0) {
                    char[] bodyChars = new char[contentLength];
                    reader.read(bodyChars, 0, contentLength);
                    String body = new String(bodyChars);
                    contentBuilder.append(body);
                }

               // System.out.println(headerbuilder.toString());
              //  System.out.println(contentBuilder.toString());

                methodHandler(method, path, contentBuilder, writer);



            }// while keepAlive


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(!isKeepAlive){
                closeSocket();
            }
        }
    }// run method


    public void methodHandler(String method, String path, StringBuilder contentBuilder, PrintWriter writer) {


        switch (method) {

            case "POST":
                if ("/Signup".equals(path)) {

                    SignupHandler signupHandler = new SignupHandler(contentBuilder);
                    String responseStringSignup = signupHandler.handleRequest();
                    writer.println(responseStringSignup);
                    writer.flush();
                    //System.out.println(responseStringSignup);

                } else if ("/Login".equals(path)) {
                    LoginHandler loginHandler = new LoginHandler(contentBuilder);
                    String responseStringLogin = loginHandler.loginRequest();
                    //System.out.println(responseStringLogin);
                    writer.print(responseStringLogin);
                    writer.flush();

                }
                break;

            case "GET":
                if ("/Products".equals(path)) {
                    ProductHandler productHandler = new ProductHandler();
                    String responseGetProducts = productHandler.getProducts();
                    writer.println(responseGetProducts);
                    writer.flush();
                    System.out.println(responseGetProducts);

                } else if ("/ProductCategories".equals(path)) {

                        CategoryProductHandler categoryProductHandler = new CategoryProductHandler();
                        ProductListsResponse productListsResponse = categoryProductHandler.response();
                        String jsonData = gson.toJson(productListsResponse);

                        StringBuilder headerBuilder1 = new StringBuilder();



                        if(productListsResponse != null) {
                            headerBuilder1.append("HTTP/1.1 200 OK\r\n");
                            headerBuilder1.append("Transfer-Encoding: chunked\r\n");
                            headerBuilder1.append("Content-Type: application/json\r\n");
                            headerBuilder1.append("\r\n");


                            writer.print(headerBuilder1);
                            writer.flush();

                            int chunkSize = 1024;
                            int offset = 0;


                          while(offset < jsonData.length()) {

                              int bytesToRead = Math.min(chunkSize, jsonData.length() - offset);
                              String chunk    = jsonData.substring(offset, offset + bytesToRead);
                              String hexSize = Integer.toHexString(bytesToRead);
                              offset += bytesToRead;


                              // Write the size of the chunk followed by CRLF
                              writer.write("Hex:" + hexSize + "\r\n");
                              //System.out.print(hexSize + "\r\n");
                              // Write the chunk data followed by CRLF
                              writer.write(chunk + "\r\n");
                              //System.out.print(chunk + "\r\n");
                              writer.flush(); // Flush after each chunk (optional)
                          }

                            // Write the terminating chunk
                            writer.write("0\r\n\r\n"); // Final chunk with size 0 followed by CRLF
                            //System.out.println("0\r\n\r\n");
                            writer.flush();


                        }

                    }
                    break;

        }// method

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



