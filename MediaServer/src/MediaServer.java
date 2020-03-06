import utils.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MediaServer {


    private ServerSocket serverSocket;
    private ArrayList<MediaServerClientsHandler> mediaServerClientsHandlers;
    private boolean alive;

    public void listen(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("MediaServer is start listening");

        listenToClients();
    }

    public MediaServer() {
        alive = true;
        mediaServerClientsHandlers = new ArrayList<>();
    }


    public synchronized void listenToClients() throws IOException {

        MediaServerClientsHandler mediaServerClientsHandler;
        Socket clientSocket;

        while (alive) {

            clientSocket = serverSocket.accept();

            mediaServerClientsHandler = new MediaServerClientsHandler(clientSocket, mediaServerClientsHandlers);

            mediaServerClientsHandlers.add(mediaServerClientsHandler);

            new Thread(mediaServerClientsHandler).start();
            System.out.println("Client connected to server successfully from: " + clientSocket.getInetAddress().getHostAddress());

        }
    }

    public static void main(String[] args) throws IOException {
        MediaServer mediaServer = new MediaServer();
        try {
            mediaServer.listen(Constants.MEDIA_SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            mediaServer.killServer();
        }
    }

    private void killServer() throws IOException {
        alive = false;
        // Simulate a new client socket connection to exit the blocking code of serverSocket.accept()
        // and break out of the infinite loop
        new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort()).close();
        serverSocket.close();
    }


    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }


}
