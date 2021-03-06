import lombok.SneakyThrows;
import org.apache.log4j.Logger;

import java.sql.*;
import java.io.*;
import java.net.Socket;

public class UserEntity implements Runnable, Observer {
    private User user;
    private Socket socket;
    private MyServer server;
    private static final Logger log = Logger.getLogger(UserEntity.class);

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
            if (clientMessage.contains(":") && clientMessage.contains("REGISTRATION")) {
                String[] logPass = clientMessage.split(":");
                String s = logPass[1].replaceAll("REGISTRATION", "");
                logPass[1] = s;
                log.info("Запрос дошел до проверки пользователя в БД!");
                try (Connection conn = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC",
                        "root", "123456")) {
                    log.info("Подключение к БД прошло упешно!");
                    ResultSet resultSet = conn.prepareStatement("SELECT login from users").executeQuery();
                    boolean flag = false;
                    while (resultSet.next()) {
                        if (logPass[0].equalsIgnoreCase(resultSet.getString("login"))) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag == true) {
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        writer.println("Данный пользователь уже зарегистрирован!");
                        writer.flush();
                        log.info("При попытке регистрации, произошла ошибка! Введенный пользователь уже зарегистрирован!");
                        log.error("При попытке регистрации, произошла ошибка! Введенный пользователь уже зарегистрирован!");
                    } else {
                        PrintWriter writer2 = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        writer2.println("Регистрация прошла успешно!");
                        writer2.flush();
                        user = new User(logPass[0], logPass[1]);
                        System.out.println("Подключен новый пользователь: " + logPass[0]);
                        server.addObserver(this);
                        log.info("Регистрация прошла успешно! Новый пользователь подключен!");
                        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO users(login, password) values(?, ?)");
                        preparedStatement.setString(1, user.getLogin());
                        preparedStatement.setString(2, user.getPassword());
                        preparedStatement.executeUpdate();
                        log.info("Добавление пользователя в БД");
                        log.error("Ошибка при добавлении пользователя в БД!");

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (clientMessage.contains(":") && clientMessage.contains("AUT")) {
                String[] logPass = clientMessage.split(":");
                String s = logPass[1].replaceAll("AUT", "");
                logPass[1] = s;
                String res = logPass[0] + logPass[1];
                String res2 = null;

                try (Connection conn = DriverManager.getConnection("jdbc:MySQL://localhost:3306/my_schema?serverTimezone=UTC",
                        "root", "123456")) {
                    ResultSet resultSet = conn.prepareStatement("SELECT login, password from users").executeQuery();
                    boolean flag = false;
                    while (resultSet.next()) {
                        res2 = res2 + resultSet.getString("login") + resultSet.getString("password");
                        if (res.equalsIgnoreCase(resultSet.getString("login") + resultSet.getString("password"))) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        PrintWriter clientWriter2 = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        clientWriter2.println("Вход успешно выполнен!");
                        clientWriter2.flush();
                        user = new User(logPass[0], logPass[1]);
                        System.out.println("Подключен пользователь: " + logPass[0]);
                        server.addObserver(this);
                    } else {
                        PrintWriter clientWriter3 = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        clientWriter3.println("Неверный логин или пароль!");
                        clientWriter3.flush();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else if (clientMessage.startsWith("сообщение для " )) {


            } else if (clientMessage.startsWith("exit")) {
                System.out.println("Пользователь: " + user.getLogin() + " покинул чат!");
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


