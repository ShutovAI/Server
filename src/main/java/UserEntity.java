import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while (true) {
            String clientMessage = clientReader.readLine();
            if (clientMessage.contains(":")) {
                String[] logPass = clientMessage.split(":");
                user = new User(logPass[0], logPass[1]);
                System.out.println("New user connected: " + logPass[0]);

                Connection conn = null;
                try {
                    conn = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC",
                            "root", "123456");
                    ResultSet resultSet = conn.prepareStatement("SELECT login, password from USER").executeQuery();
                    while (resultSet.next()) {
                        if(logPass[0].equals(resultSet.getString("login"))){
                            System.out.println("Такой пользователь уже зарегистрирован!");

                        }else {
                            resultSet = conn.prepareStatement("INSERT login, password from USER").executeQuery();
                        }
                        System.out.println(resultSet.getString("login"));
                    }
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                server.addObserver(this);
            } else if (clientMessage.contains("exit")) {
                System.out.println("User: " + user.getLogin() + " disconnected");
            } else {
                System.out.println(user.getLogin() + ": " + clientMessage);
                server.notifyObserver(user.getLogin() + " : " + clientMessage);
            }
        }

//    public boolean registration(){
//        while (){
//
//        }
    }

    @SneakyThrows
    @Override
    public void notifyObserver(String message) {
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.println(message);
        writer.flush();
    }
}

