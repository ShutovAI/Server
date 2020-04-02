import lombok.SneakyThrows;

import java.sql.*;

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

    //    ArrayList<String> chat = new ArrayList();
    @SneakyThrows
    @Override
    public void run() {

        PrintWriter clientWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while (true) {
            String clientMessage = clientReader.readLine();
            if (clientMessage.contains(":") && clientMessage.contains("REGISTRATION")) {
                String[] logPass = clientMessage.split(":");
                String s = logPass[1].replaceAll("REGISTRATION", "");
                logPass[1] = s;
                while(true) {
                    try (Connection conn = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC",
                            "root", "123456")) {
                        ResultSet resultSet = conn.prepareStatement("SELECT login from users").executeQuery();
                        while (resultSet.next()) {
                            if (logPass[0].equalsIgnoreCase(resultSet.getString("login"))) {
//                                server.stopObserver(this);
                                clientWriter.println("Данный пользователь уже зарегистрирован!");
                                clientWriter.flush();
                                logPass[0]=null;
                                break;
                            }
                        }

                    if(logPass[0] == null){
//                        conn.close();
                        break;
                    }
                        PrintWriter clientWriter2 = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        clientWriter2.println("Регистация прошла успешно!");
                        clientWriter2.flush();
                        user = new User(logPass[0], logPass[1]);
                        System.out.println("New user connected: " + logPass[0]);
                        server.addObserver(this);

                        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO users(login, password) values(?, ?)");
                        preparedStatement.setString(1, user.getLogin());
                        preparedStatement.setString(2, user.getPassword());
                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
//            if (clientMessage.contains(":")) {
//                String[] logPass = clientMessage.split(":");
//                user = new User(logPass[0], logPass[1]);
//                System.out.println("New user connected: " + logPass[0]);
//                server.addObserver(this);
//                try (Connection conn = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC",
//                        "root", "123456")) {
//                    System.out.println("connected with DB");
//                 PreparedStatement preparedStatement =  conn.prepareStatement  ("INSERT INTO users(login,password) values(?, ?)");
//                   preparedStatement.setString(1, user.getLogin());
//                   preparedStatement.setString(2, user.getPassword());
//                   preparedStatement.executeUpdate();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }

            } else if (clientMessage.startsWith("exit")) {
                System.out.println("Пользователь: " + user.getLogin() + " disconnected");
                clientWriter.println(user.getLogin() + " " + "Покинул чат!");
                server.stopObserver(this);
                clientWriter.flush();
                return;
            } else {
                System.out.println(user.getLogin() + ": " + clientMessage);
                server.notifyObserver(user.getLogin() + ": " + clientMessage);
                FileWriter fileWriter = new FileWriter("chat.txt", true);
                fileWriter.write(user.getLogin() + ": " + clientMessage + "\n");
                fileWriter.flush();
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
//    private void connectionDBAddUser(User user) {
//        try (Connection conn = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC",
//                "root", "123456")) {
//            ResultSet resultSet = conn.prepareStatement("INSERT INTO user (id, login, password) VALUES (3, user.getLogin(), getPassword())").executeQuery();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


//    public boolean registration(){
//        while (){
//
//        }


//        Connection conn = null;
//        try {
//            conn = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC",
//                    "root", "123456");
//            ResultSet resultSet = conn.prepareStatement("SELECT login, password from USER").executeQuery();
//            while (resultSet.next()) {
//                System.out.println(resultSet.getString("login"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

