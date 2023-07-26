import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String userName;
    private Socket socket;
    private BufferedReader Reader;
    private BufferedWriter Writer;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.Writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.Reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = username;
        } catch (IOException e) {
            closeConnection(socket,Reader,Writer);
        }
    }

    public void sendMessage() {
        try {
            Writer.write(userName);
            Writer.newLine();
            Writer.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                Writer.write(userName + " : " + message);
                Writer.newLine();
                Writer.flush();
            }
        } catch (IOException e) {
            closeConnection(socket,Reader, Writer);
        }
    }

    public void listenForServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;

                while(socket.isConnected()) {
                    try {
                        message = Reader.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        closeConnection(socket, Reader, Writer);
                    }
                }
            }
        }).start();
    }

    public void closeConnection(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws  IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your name: ");
        String userName = scanner.nextLine();

        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, userName);
        client.listenForServer();
        client.sendMessage();
    }
}
