import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class MediaEmitterClientsHandler implements Runnable {
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private List<MediaEmitterClientsHandler> mediaEmitterClientsHandlers;

    public MediaEmitterClientsHandler(Socket socket, List<MediaEmitterClientsHandler> mediaEmitterClientsHandlers) throws IOException {
        this.socket = socket;
        this.mediaEmitterClientsHandlers = mediaEmitterClientsHandlers;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    @Override
    public void run() {

        byte[] data = new byte[120];
        String messageReceived = "";
        try {
            inputStream.read(data);
            messageReceived = new String(data);
            System.out.println("Message received : " + messageReceived);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String string = new String(data);
        String[] strings = string.split(":");

        String type = strings[1].trim();
        String clientName = strings[2].trim();

        String message = "Hello From Media Emitter Server ";
        sendMessageToMediaStreamPusherClient(message.getBytes());

    }


    public void sendMessageToMediaStreamPusherClient(byte[] message) {
        try {
            outputStream.write(message);
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    public List<MediaEmitterClientsHandler> getMediaEmitterClientsHandlers() {
        return mediaEmitterClientsHandlers;
    }

    public void setMediaEmitterClientsHandlers(List<MediaEmitterClientsHandler> mediaEmitterClientsHandlers) {
        this.mediaEmitterClientsHandlers = mediaEmitterClientsHandlers;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
