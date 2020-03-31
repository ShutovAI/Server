import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer implements Observable {
    public final static int PORT = 8290;
    private volatile List<Observer> users = new ArrayList<>();

    public void start() {
        System.out.println("==START SERVER==");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket socket = null;
            while (true) {
                if (socket == null) {
                    socket = serverSocket.accept();
                } else {
                    UserEntity userEntity = new UserEntity(socket, this);
                    new Thread(userEntity).start();
                    socket = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addObserver(Observer o) {
        users.add(o);
    }

    @Override
    public void stopObserver(Observer o) {
        users.remove(o);
    }

    @Override
    public void notifyObserver(String message) {
        for (Observer user : users) {
            user.notifyObserver(message);
        }
    }

    public void numUsers(){
        for (Observer user : users){
            System.out.println(user);
        }
    }
}
