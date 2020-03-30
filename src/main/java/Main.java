import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        MyServer server = new MyServer();
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
        server.start();
    }
}