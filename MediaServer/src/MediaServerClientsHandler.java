import utils.Constants;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MediaServerClientsHandler implements Runnable {

    private String clientName;
    private String clientType;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private List<MediaServerClientsHandler> mediaServerClientsHandlers;

    public MediaServerClientsHandler(Socket socket, List<MediaServerClientsHandler> mediaServerClientsHandlers) throws IOException {
        this.socket = socket;
        this.mediaServerClientsHandlers = mediaServerClientsHandlers;
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


        if (messageReceived.contains("Class Type")) {
            getNameAndClassTypeFromMessage(messageReceived);
        }

        for (MediaServerClientsHandler mediaServerClientsHandler : mediaServerClientsHandlers) {
            if (mediaServerClientsHandler.clientType.equals(Constants.MEDIA_END_USER_CLASS_TYPE)) {
                updateAvailableClients(mediaServerClientsHandler);
            }
        }

    }

    private void getNameAndClassTypeFromMessage(String messageReceived) {
        String[] strings = messageReceived.split(":");

        clientType = strings[1].trim();
        clientName = strings[2].trim();
    }

    private void updateAvailableClients(MediaServerClientsHandler mediaServerClientsHandler) {
        try {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            for (String device : getAvailableDevicesForConnect()) {
                dataOutputStream.writeUTF(device);
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();
            mediaServerClientsHandler.outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public List<MediaServerClientsHandler> getMediaServerClientsHandlers() {
        return mediaServerClientsHandlers;
    }

    public void setMediaServerClientsHandlers(List<MediaServerClientsHandler> mediaServerClientsHandlers) {
        this.mediaServerClientsHandlers = mediaServerClientsHandlers;
    }

    public List<String> getAvailableDevicesForConnect() {
        List<String> availableDevicesForConnect = new ArrayList<>();
        for (MediaServerClientsHandler mediaServerClientsHandler : mediaServerClientsHandlers) {
            if (mediaServerClientsHandler.clientType.equals(Constants.MEDIA_STREAM_PUSHER_CLASS_TYPE)) {
                String clientAddress = mediaServerClientsHandler.socket.getInetAddress().toString();
                String clientPort = String.valueOf(mediaServerClientsHandler.socket.getLocalPort());

                String clientAvailable = clientAddress + ":" + clientPort;

                availableDevicesForConnect.add(clientAvailable);
            }
        }
        return availableDevicesForConnect;
    }


}
