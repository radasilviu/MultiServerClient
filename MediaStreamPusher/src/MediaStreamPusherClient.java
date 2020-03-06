import utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class MediaStreamPusherClient {

    private Socket mediaStreamPusherFromMediaStreamingServer;
    private Socket mediaStreamPusherFromMediaServer;
    private InputStream mediaStreamPusherInputStream;
    private OutputStream mediaStreamPusherOutputStream;
    private InputStream mediaEndUserInputStream;
    private OutputStream mediaEndUserOutputStream;

    public void connectToServer(String classType, String serverAddress, String name, int firstServerPort, int secondServerPort) throws IOException {


        mediaStreamPusherFromMediaStreamingServer = new Socket(serverAddress, firstServerPort);
        mediaStreamPusherFromMediaServer = new Socket(serverAddress, secondServerPort);

        mediaStreamPusherInputStream = mediaStreamPusherFromMediaStreamingServer.getInputStream();
        mediaStreamPusherOutputStream = mediaStreamPusherFromMediaStreamingServer.getOutputStream();


        mediaEndUserInputStream = mediaStreamPusherFromMediaServer.getInputStream();
        mediaEndUserOutputStream = mediaStreamPusherFromMediaServer.getOutputStream();


        sendTypeAndNameToServer(classType, name);

        // while (true) {
        initiate(mediaStreamPusherInputStream);
        initiate(mediaEndUserInputStream);
        //  }

    }


    private void initiate(InputStream inputStream) {
        byte[] data;
        while (true) {
            data = new byte[120];
            try {
                inputStream.read(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendTypeAndNameToServer(String classType, String name) throws IOException {
        String message = "Class Type : " + classType + " : " + name;
        mediaEndUserOutputStream.write(message.getBytes());
        mediaEndUserOutputStream.flush();

        mediaStreamPusherOutputStream.write(message.getBytes());
        mediaStreamPusherOutputStream.flush();
    }


    public static void main(String[] args) throws IOException {
        System.out.println("Enter a name for client");
        Scanner sc = new Scanner(System.in);
        String name;

        do {
            System.out.print("Please enter your name: ");
            name = sc.nextLine();
        } while (name.trim().equals(""));

        String classType = MediaStreamPusherClient.class.getSimpleName();
        MediaStreamPusherClient mediaStreamPusherClient = new MediaStreamPusherClient();


        mediaStreamPusherClient.connectToServer(classType, Constants.SERVER_ADDRESS, name, Constants.MEDIA_EMITTER_SERVER_PORT, Constants.MEDIA_SERVER_PORT);

    }


}
