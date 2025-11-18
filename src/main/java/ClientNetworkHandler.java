import java.io.*;
import java.net.Socket;

public class ClientNetworkHandler {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected;
    private ProjectThreeClient mainApp;
    private Thread listenerThread;

    public ClientNetworkHandler(ProjectThreeClient mainApp) {
        this.mainApp = mainApp;
        this.connected = false;
    }

    public boolean connectToServer(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;

            // Start listening for server messages
            startListening();
            return true;

        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            return false;
        }
    }

    private void startListening() {
        listenerThread = new Thread(() -> {
            while (connected) {
                try {
                    PokerInfo info = (PokerInfo) in.readObject();
                    // Use Platform.runLater to update JavaFX UI
                    javafx.application.Platform.runLater(() -> {
                        mainApp.handleServerMessage(info);
                    });
                } catch (IOException | ClassNotFoundException e) {
                    if (connected) {
                        System.err.println("Connection lost: " + e.getMessage());
                        disconnect();
                    }
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void sendPokerInfo(PokerInfo info) {
        if (connected) {
            try {
                out.writeObject(info);
                out.flush();
            } catch (IOException e) {
                System.err.println("Failed to send data: " + e.getMessage());
                disconnect();
            }
        }
    }

    public void disconnect() {
        connected = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected;
    }
}