import utils.Constants;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MediaEmitterServer implements Serializable {

    private ServerSocket serverSocket;
    private ArrayList<MediaEmitterClientsHandler> mediaEmitterClientsHandlers;
    private boolean alive;

    public void listen(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("MediaEmitter is start listening");

        listenToClients();
    }

    public MediaEmitterServer() {
        alive = true;
        mediaEmitterClientsHandlers = new ArrayList<>();
    }

    public synchronized void listenToClients() throws IOException {

        MediaEmitterClientsHandler mediaEmitterClientsHandler;
        Socket clientSocket;

        while (alive) {

            clientSocket = serverSocket.accept();

            mediaEmitterClientsHandler = new MediaEmitterClientsHandler(clientSocket, mediaEmitterClientsHandlers);

            mediaEmitterClientsHandlers.add(mediaEmitterClientsHandler);

            new Thread(mediaEmitterClientsHandler).start();
            System.out.println("Client connected to server successfully from: " + clientSocket.getInetAddress().getHostAddress());

        }
    }

    public static void main(String[] args) throws IOException {
        MediaEmitterServer mediaEmitterServer = new MediaEmitterServer();
        try {
            mediaEmitterServer.listen(Constants.MEDIA_EMITTER_SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            mediaEmitterServer.killServer();
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

    public ArrayList<MediaEmitterClientsHandler> getMediaEmitterClientsHandlers() {
        return mediaEmitterClientsHandlers;
    }

    public void setMediaEmitterClientsHandlers(ArrayList<MediaEmitterClientsHandler> mediaEmitterClientsHandlers) {
        this.mediaEmitterClientsHandlers = mediaEmitterClientsHandlers;
    }
}
