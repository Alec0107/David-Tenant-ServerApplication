package SERVER.API;

import SERVER.NetworkManager.Dispatcher;
import SERVER.NetworkManager.NewDispatcher;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    public static void main (String[]a) {

        try {

                ServerSocket serverSocket = new ServerSocket(8080);
                System.out.println("SERVER IS LISTENING TO PORT " + serverSocket.getLocalPort());
                while(true){

                Socket socket = serverSocket.accept();
                System.out.println("New Connection from: " + socket.getRemoteSocketAddress() + "\n");
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new NewDispatcher(socket));


            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
}


}
