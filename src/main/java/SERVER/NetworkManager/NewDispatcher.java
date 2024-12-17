package SERVER.NetworkManager;

import SERVER.Handlers.ProductHandler_;
import SERVER.Models.ProductModels.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NewDispatcher implements Runnable {

    private Socket socket;
    private Gson gson;
    private PrintWriter writer;

    public NewDispatcher(Socket socket) {
        this.socket = socket;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.setLenient(); // Allow lenient parsing
        gson = gsonBuilder.create();
    }


    @Override
    public void run() {

        boolean connectionAlive = true;

        StringBuilder headerbuilder = new StringBuilder();
        StringBuilder contentBuilder = new StringBuilder();

        String line = "";
        String connection = "";
        String method = "";
        String path = "";
        int contentLength = 0;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);



                while (connectionAlive) {

                    while ((line = reader.readLine()) != null && !line.isEmpty()) {

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

                        if (contentLength > 0) {
                            char[] bodyChars = new char[contentLength];
                            reader.read(bodyChars, 0, contentLength);
                            String body = new String(bodyChars);
                            contentBuilder.append(body);
                        }

                        methodHandler(method, path, contentBuilder);
                        // System.out.println("NewDispatcher: " + method + " " + path);

                    }
                 }

        } catch (IOException e) {
        e.printStackTrace();
        } finally {
            if(!connectionAlive){
                closeSocket();
            }
        }
    } // run



    public void methodHandler(String method, String path, StringBuilder contentBuilder) {

        switch (method){
            case "GET":
                        if("/FullProducts".equals(path)) {
                            ProductHandler_ fullProductHandler = new ProductHandler_();
                            List<Product> products =  fullProductHandler.fetchProducts();
                            String jsonData = gson.toJson(products);

                            if(products != null) {
                                chunkDataToSend(jsonData);
                            }

                        } break;
        }



    }

    public void chunkDataToSend(String jsonData){

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
                String hexSize = Integer.toHexString(bytesToRead);

                writer.write(hexSize + "\r\n");  // Write size in hexadecimal
                writer.write(chunkData + "\r\n");  // Write chunk data followed by CRLF
                offset += bytesToRead;
            }

            writer.write("0\r\n\r\n");
            writer.flush();
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




