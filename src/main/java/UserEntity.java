import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class UserEntity implements Runnable, Observer {
    private User user;
    private Socket socket;
    private MyServer server;

    public UserEntity(Socket socket, MyServer server) {
        this.socket = socket;
        this.server = server;
    }

    ArrayList<String> chat = new ArrayList();

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
                server.stopObserver(this);
                clientWriter.flush();
                return;
            } else {
                System.out.println(user.getLogin() + ": " + clientMessage);
                server.notifyObserver(user.getLogin() + ": " + clientMessage);
                FileWriter fileWriter = new FileWriter("chat.txt",true);
                fileWriter.write(user.getLogin() + ": " + clientMessage + "\n");
                fileWriter.flush();
//                for (int i = 0; i < chat.size(); i++) {
//                    fileWriter.write(chat.get(i));
//                    fileWriter.flush();
//                }
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