import org.apache.log4j.BasicConfigurator;

public class Main {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        MyServer server = new MyServer();
        server.start();
    }
}