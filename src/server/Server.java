package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class Server implements Runnable{

    private ServerSocket serverSocket;
    private ExecutorService executorService;


    public static void main(String[] args) {

    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(6666);
            while (true){
            Socket client = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(client);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
class ClientHandler implements Runnable{

    private Socket client;

    @Override
    public void run() {

    }

    public ClientHandler(Socket client) {
        this.client=client;
    }
}