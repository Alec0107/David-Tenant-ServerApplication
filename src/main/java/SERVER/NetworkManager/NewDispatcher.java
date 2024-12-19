package SERVER.NetworkManager;

import SERVER.Handlers.AuthHandler;
import OLD.ProductHandler_;
import SERVER.Models.Account;
import SERVER.Models.Product;
import SERVER.hashing_token_encrypt.DecryptionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class NewDispatcher implements Runnable {

    private Socket socket;
    private Gson gson;
    private PrintWriter writer;
    boolean connectionAlive = true;


    public NewDispatcher(Socket socket) {
        this.socket = socket;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.setLenient(); // Allow lenient parsing
        gson = gsonBuilder.create();
    }


    @Override
    public void run() {

        StringBuilder headerBuilder = new StringBuilder();
        StringBuilder contentBuilder = new StringBuilder();

        String line = "";
        String connection = "";
        String method = "";
        String path = "";
        int contentLength = 0;
        String encryptedBody;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            InputStream inputStream = socket.getInputStream();
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

                while (connectionAlive) {
                    // Reset for each request
                    headerBuilder.setLength(0);
                    contentBuilder.setLength(0);
                    method = "";
                    path = "";
                    contentLength = 0;
                    encryptedBody = "";


                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        headerBuilder.append(line).append("\n");

                        if (line.startsWith("POST") || (line.startsWith("GET"))) {
                            String[] parts = line.split(" ");
                            method = parts[0];
                            path = parts[1];
                        }

                        if (line.toLowerCase().startsWith("content-length:")) {
                            contentLength = Integer.parseInt(line.split(":")[1].trim());
                        }

                        if (line.startsWith("Connection: ")) {
                            String[] parts = line.split(" ");
                            connection = parts[1].trim();
                            if ("Close".equals(connection)) {
                                connectionAlive = false;
                            }
                        }
                    } // INNER LOOP

                        if (contentLength > 0) {
                            byte[] bodyBytes = new byte[contentLength];
                            int totalRead = 0;

                            while(totalRead < contentLength) {
                                int bytesRead = inputStream.read(bodyBytes, totalRead, contentLength - totalRead);
                                    if(bytesRead == -1) break;
                                    totalRead += bytesRead;
                            }

                            // Log the raw body bytes for debugging
                            // Directly decode the bodyBytes using Base64
                              System.out.println("Raw Body Bytes: " + Arrays.toString(bodyBytes));
                            encryptedBody = new String(bodyBytes, StandardCharsets.UTF_8).trim();
                              System.out.println("Encrypted Body Received: '" + encryptedBody + "'");
                        }

                    // Logs
                    //System.out.println("Received Headers:\n" + headerBuilder.toString());
                    //System.out.println(path);
                    //System.out.println("Expected Content-Length: " + contentLength);
                    //System.out.println("Received Body: " + contentBuilder.toString());



                    // Call methodHandler AFTER processing the full request
                    if (method != null && path != null) {
                        methodHandler(method, path, encryptedBody);
                    }

                } // OUTER LOOP

        } catch (IOException e) {
        e.printStackTrace();
        } finally {
            if(!connectionAlive){
                closeSocket();
            }
        }
    } // run



    public void methodHandler(String method, String path, String body) {

        switch (method){
            case "GET":
                        if("/FullProducts".equals(path)) {
                            ProductHandler_ fullProductHandler = new ProductHandler_();
                            List<Product> products =  fullProductHandler.fetchProducts();
                            String jsonData = gson.toJson(products);

                            if(products != null) {
                                chunkDataToSend(jsonData);
                            }

                        }else if(path.startsWith("/Verification")) {
                             handleVerification(path);
                            //System.out.println(path);
                        }

                        break;


            case "POST":
                         if("/Register".equals(path)) {
                             try {
                                 // Step 1: Get the encrypted body content
                                 String encryptedBody = body;
                                 System.out.println("Encrypted Body Received: " + encryptedBody + "\r\n");

                                 // Step 2: Decrypt the body
                                 String decryptedBody = DecryptionUtil.decrypt(encryptedBody);
                                 System.out.println("Decrypted Body: " + decryptedBody + "\r\n");

                                 // Step 3: Parse the decrypted body as JSON
                                 Account account = gson.fromJson(decryptedBody, Account.class);
                                 AuthHandler authHandler = new AuthHandler();
                                 authHandler.registerUser(account);


                                 // Step 4: Send a success response
                                 String successResponse = "HTTP/1.1 200 OK\r\nContent-Length: 22\r\n\r\nRegistration Success!";
                                 writer.print(successResponse);
                                 writer.flush();

                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                         }
        }



    }


    public void handleVerification(String path) {

        try{
            // extract the token from the query
            String[] parts = path.split("\\?");
            if(parts.length < 2) {
                System.out.println("400 Bad Request");
                sendResponse("400 Bad Request", "Invalid Request");
                connectionAlive = false;
            }

            String queryString = parts[1];
            System.out.println("QueryString: " + queryString);
            String token = null;

            String[] keyValue = queryString.split("=");
            if(keyValue.length == 2 && keyValue[0].equals("token")) {
                token = keyValue[1];
            }

            if(token == null) {
                sendResponse("400 Bad Request", "Token Missing");
            }
            System.out.println("Token: " + token);
            sendResponse("200 OK", "200 OK");

            AuthHandler authHandler = new AuthHandler();
            boolean isVerified = authHandler.verifyEmailToken(token);

            if(isVerified) {
                sendResponse("200 OK", "Email Verified Successfully");
            }else{
                sendResponse("400 Bad Request", "Invalid or Expired Token");
            }

        }catch(Exception e){
            e.printStackTrace();
            sendResponse("500 Internal Server Error", "Internal Server Error");
        }



    }



    public void sendResponse(String statusCode, String responseBody) {
        writer.print("HTTP/1.1 "+  statusCode +" \r\n");
        writer.print("Connection: Keep-Alive\r\n");  // Ensure connection remains open
        writer.print("Content-Length: " + responseBody.length() + "\r\n");
        writer.print("\r\n");
        writer.print(responseBody);
        writer.flush();

        //connectionAlive = false;
    }



    public void chunkDataToSend(String jsonData){

        try{
            StringBuilder header = new StringBuilder();
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
                String hexSize = Integer.toHexString(bytesToRead).trim();

                writer.write(hexSize + "\r\n");  // Write size in hexadecimal
                writer.flush();

                writer.write(chunkData + "\r\n");  // Write chunk data followed by CRLF
                writer.flush();
                offset += bytesToRead;
            }

            writer.write("0\r\n\r\n");
            writer.flush();

        }catch (Exception e){
            e.printStackTrace();
        }
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




