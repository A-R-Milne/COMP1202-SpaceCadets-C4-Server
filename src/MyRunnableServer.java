import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MyRunnableServer implements Runnable {
    private String name;
    private final Socket socket;
    private ListOfRunnables myList;
    final int END_OF_SENTENCE = 10;
    private int index = -1;
    
    public MyRunnableServer(Socket socket, ListOfRunnables myList) {
        this.socket = socket;
        this.myList = myList;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            String message;
            int input;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            
            boolean nameAssigned = false;
            while(!nameAssigned) {
                input = inputStream.read();
                
                if (input != END_OF_SENTENCE) {
                    bytes.write((byte) input);
                    
                } else if (bytes.size() > 0) {
                    name = bytes.toString();
                    nameAssigned = true;
                    bytes.reset();
                }
            }
            String joinMessage = name+" entered the chat.";
            System.out.println(joinMessage);
            for (MyRunnableServer run : myList.list) {
                run.sendMessage(joinMessage);
            }
            
            boolean exitCommand = false;
            while (!exitCommand) {
                input = inputStream.read();
                
                if (input != END_OF_SENTENCE) {
                    bytes.write((byte) input);
                    
                } else if (bytes.size()>0) {
                    String strBytes = bytes.toString();
                    
                    if (strBytes.contains("|goodbye|")) {
                        exitCommand = true;
                        strBytes = strBytes.substring(0,strBytes.indexOf("|goodbye|"))+strBytes.substring(strBytes.indexOf("|goodbye|")+9);
                    }
                    message = "["+name+"] "+strBytes;
                    
                    if (!strBytes.isEmpty()) {
                        System.out.println(message);
                        for (MyRunnableServer run : myList.list) {
                            run.sendMessage(message);
                        }
                    }
                    bytes.reset();
                }
            }
            String leaveMessage = name+" left the chat.";
            System.out.println(leaveMessage);
            for (MyRunnableServer run : myList.list) {
                run.sendMessage(leaveMessage);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        myList.list.remove(index);
        for(MyRunnableServer runnableServer : myList.list) {
            if(runnableServer.index>index) {
                runnableServer.setIndex(runnableServer.index-1);
            }
        }
    }
    
    public void sendMessage(String message) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(message.getBytes());
        outputStream.write(END_OF_SENTENCE);
    }
}
