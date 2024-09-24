package SERVER.NetworkManager;

import SERVER.Handlers.LoginHandler;
import SERVER.Handlers.SignupHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestDispatcher implements Runnable {

    private Socket socket;

    public RequestDispatcher(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    writer = new PrintWriter(socket.getOutputStream(), true);

            String line = "";
            String method = "";
            String path = "";
            int contentLength = 0;

            StringBuilder headerBuilder  = new StringBuilder();
            StringBuilder contentBuilder = new StringBuilder();

            while((line = reader.readLine()) != null && !line.isEmpty()){

                headerBuilder.append(line).append("\n");

                if(line.startsWith("POST") || line.startsWith("GET")) {
                    String[] parts = line.split(" ");
                    method = parts[0];
                    path = parts[1];

                }

                if(line.startsWith("Content-Length:")){
                    String[] parts = line.split(":");
                    contentLength = Integer.parseInt(parts[1]);
                }

            }// end of while read


            System.out.println("Header: \n" + headerBuilder); // for debugging


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



            }











        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
