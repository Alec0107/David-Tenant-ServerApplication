package SERVER.NetworkManager;

import OLD.*;
import OLD.CartResponse;
import OLD.ProductListsResponse;
import SERVER.Models.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        boolean connectionAlive = true;

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            StringBuilder headerbuilder = new StringBuilder();
            StringBuilder contentBuilder = new StringBuilder();

            String line = "";
            String connection = "";
            String method = "";
            String path = "";

             String username = "";
             String email = "";
             String password = "";

            int contentLength = 0;

            while (connectionAlive) {

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
                        connection = parts[1].trim();
                        if("Close".equals(connection)){
                            connectionAlive = false;
                        }
                    }
                }

                if (contentLength > 0) {
                    char[] bodyChars = new char[contentLength];
                    reader.read(bodyChars, 0, contentLength);
                    String body = new String(bodyChars);
                    contentBuilder.append(body);
                }



                 System.out.println(headerbuilder.toString());
                //System.out.println(contentBuilder.toString());
                //AuthHandler authHandler = new AuthHandler();
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                //executorService.execute(authHandler);


                methodHandler(method, path, contentBuilder, writer);




            }// while keepAlive


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(!connectionAlive){
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

                         } else if("/AddProductCart".equals(path)) {
                         CartHandler cartHandler = new CartHandler(contentBuilder);
                         String carResponse = cartHandler.cartRequest();
                         writer.print(carResponse);
                         writer.flush();

                        }break;

            case "GET":
                        if ("/Products".equals(path)) {
                            ProductHandler productHandler = new ProductHandler();
                            String responseGetProducts = productHandler.getProducts();
                            writer.println(responseGetProducts);
                            writer.flush();
                            // System.out.println(responseGetProducts);

                        }

                        else if ("/ProductCategories".equals(path)) {
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
                              

                                 // Write the size of the chunk followed by CRLF
                                 writer.write("Hex:" + hexSize + "\r\n");
                                 //System.out.print(hexSize + "\r\n");
                                 // Write the chunk data followed by CRLF
                                 writer.write(chunk + "\r\n");
                                 //System.out.print(chunk + "\r\n");
                                 writer.flush(); // Flush after each chunk (optional)

                                 offset += bytesToRead;
                               }

                                 // Write the terminating chunk
                                 writer.write("0\r\n\r\n"); // Final chunk with size 0 followed by CRLF
                                 //System.out.println("0\r\n\r\n");
                                 writer.flush();


                            }
                        }

                        else if("/CartProducts".equals(path)) {
                            StringBuilder headerBuilder = new StringBuilder();

                            CartProductHandler cartProductHandler = new CartProductHandler(contentBuilder);
                            CartResponse cartResponse = cartProductHandler.cartProductsRequest();
                            String jsonData = gson.toJson(cartResponse);


                            if(cartResponse != null) {
                                headerBuilder.append("HTTP/1.1 200 OK\r\n");
                                headerBuilder.append("Transfer-Encoding: chunked\r\n");
                                headerBuilder.append("Content-Type: application/json\r\n");
                                headerBuilder.append("\r\n");
                                writer.print(headerBuilder);
                                writer.flush();


                                int chunkSize = 1024;
                                int offset = 0;

                                while (offset < jsonData.length()) {

                                    int bytesToRead = Math.min(chunkSize, jsonData.length() - offset);

                                    String chunkData    = jsonData.substring(offset, offset + bytesToRead);
                                    String hexSize = Integer.toHexString(bytesToRead);


                                    // Write the chunk size in hex followed by CRLF
                                    writer.write("Hex:" + hexSize + "\r\n");

                                    // Write the actual chunk data followed by CRLF
                                    writer.write(chunkData + "\r\n");

                                    // Flush after sending each chunk (optional, but ensures immediate delivery)
                                    writer.flush();

                                    // Update the offset to send the next chunk
                                    offset += bytesToRead;
                                }

                                // Write the terminating chunk (indicating the end of the data)
                                writer.write("0\r\n\r\n"); // This ends the chunked transfer
                                writer.flush();

                            }
                        }

                        else if("/FullProducts".equals(path)) {
                               ProductHandler_ fullProductHandler = new ProductHandler_();
                               List<Product> products =  fullProductHandler.fetchProducts();
                               String jsonData = gson.toJson(products);

                               StringBuilder header = new StringBuilder();

                               if(products != null) {
                                   header.append("HTTP/1.1 200 OK\r\n");
                                   header.append("Transfer-Encoding: chunked\r\n");
                                   header.append("Content-Type: application/json\r\n");
                                   header.append("\r\n");

                                   writer.print(header);
                                   writer.flush();

                                   int chunkSize = 1024;
                                   int offset = 0;

                                   while (offset < jsonData.length()) {

                                       int bytesToRead = Math.min(chunkSize, jsonData.length() - offset);
                                       String chunkData = jsonData.substring(offset, offset + bytesToRead);
                                       String hexSize = Integer.toHexString(bytesToRead);

                                       writer.write(hexSize + "\r\n");  // Write size in hexadecimal
                                       writer.write(chunkData + "\r\n");  // Write chunk data followed by CRLF
                                       offset += bytesToRead;
                                   }

                                   writer.write("0\r\n\r\n");
                                   writer.flush();


                               }


                        }break;

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



