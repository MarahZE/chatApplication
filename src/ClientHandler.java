import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private String clientName;
    private Socket socket;
    private BufferedReader Reader;
    private BufferedWriter Writer;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.Writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.Reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientName = Reader.readLine();
            clients.add(this);
            sendMessage("SERVER : " + clientName + " has entered the chat!");
        } catch (IOException e) {
            closeConnection(socket, Reader, Writer);
        }
    }

    @Override
    public void run() {
        String message;
        
        try {
            String host = socket.getInetAddress().getHostName();
            Writer.write(host+ ", antalet clients" +  clients.size());
            Writer.newLine();
            Writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (socket.isConnected()) {
            try{
                message = Reader.readLine();
                if (message != null) {
                    sendMessage(message);
                } else {
                    closeConnection(socket,Reader,Writer);
                }
            } catch (IOException e) {
                //closeConnection(socket,bufferedReader,bufferedWriter);
                break;
            }
        }

    }

    public void removeClientHandler() {
        clients.remove(this);
        sendMessage("Server: " + clientName + " has left the chat!");
    }

    public void sendMessage(String messageToSend) {
        for(ClientHandler clientHandler: clients) {
            try {
                if (!clientHandler.clientName.equals(clientName)) {
                    clientHandler.Writer.write(messageToSend);
                    clientHandler.Writer.newLine();
                    clientHandler.Writer.flush();
                }
            } catch (IOException e) {
                closeConnection(socket,Reader,Writer);
            }
        }
    }

    public void closeConnection(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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
}
