import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class UserEntity implements Runnable, Observer {
    private User user;
    private Socket socket;
    private MyServer server;

    public UserEntity(Socket socket, MyServer server) {
        this.socket = socket;
        this.server = server;
    }

    @SneakyThrows
    @Override
    public void run() {

        PrintWriter clientWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while (true) {
            String clientMessage = clientReader.readLine();
            if (clientMessage.contains(":")) {
                String[] logPass = clientMessage.split(":");
                user = new User(logPass[0], logPass[1]);
                System.out.println("New user connected: " + logPass[0]);
                server.addObserver(this);
            } else if (clientMessage.startsWith("exit")) {
                System.out.println("Пользователь: " + user.getLogin() + " disconnected");
                clientWriter.println(user.getLogin() + " " + "Покинул чат!");
                clientWriter.flush();
                break;
            } else {
                System.out.println(user.getLogin() + ": " + clientMessage);
                server.notifyObserver(user.getLogin() + ": " + clientMessage);
            }
        }

    }

    @SneakyThrows
    @Override
    public void notifyObserver(String message) {
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.println(message);
        writer.flush();
    }
}

//    public boolean registration(){
//        while (){
//
//        }