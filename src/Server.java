import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            ListOfRunnables myList = new ListOfRunnables();
            
            while (true) {
                Socket socket = serverSocket.accept();
                MyRunnableServer threadJob = new MyRunnableServer(socket,myList);
                myList.list.add(threadJob);
                threadJob.setIndex(myList.list.indexOf(threadJob));
                new Thread(threadJob).start();
            }
        }
    }
}