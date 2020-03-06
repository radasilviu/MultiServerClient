import utils.Constants;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MediaEndUserClient {

    private String name;
    private String classType;
    private static Scanner sc = new Scanner(System.in);
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private List<String> availableDevicesForConnect = new ArrayList<>();

    public void connectToMediaServer(String classType, String serverAddress, String name, int serverPort) throws IOException, ClassNotFoundException, InterruptedException {
        socket = new Socket(serverAddress, serverPort);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        this.classType = classType;
        this.name = name;

        sendTypeAndNameToServer();

        while (true) {
            initiate();
        }
    }


    public void sendTypeAndNameToServer() throws IOException {
        String message = "Class Type : " + classType + " : " + name;
        outputStream.write(message.getBytes());
        outputStream.flush();
    }


    public void getAvailableDevice(byte[] data) {
        String messageReceived = new String(data);
        if (messageReceived.contains("/")) {
            String dataString = new String(data);
            String[] devices = dataString.split("\u000F");

            for (String device : devices) {
                if (!device.equals("")) {
                    availableDevicesForConnect.add(device);
                }
            }


            for (String device2 : availableDevicesForConnect) {
                System.out.println(device2);
            }
        }
    }

    // simulate user select from available device to connect
    public void pairDevice() throws IOException {
        String message = "Pairing client : ";

        Random random = new Random();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(message + availableDevicesForConnect.get(random.nextInt(availableDevicesForConnect.size())));

        byte[] bytes = bos.toByteArray();

        outputStream.write(bytes);

    }


    private void initiate() {

        byte[] data = new byte[120];
        try {
            inputStream.read(data);

            final String messageReceived = new String(data);


            getAvailableDevice(data);

            if (messageReceived.contains("Ready to connect")) {
                pairDevice();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("Enter a name for client");

        String name;
        do {
            System.out.print("Please enter your name: ");
            name = sc.nextLine();
        } while (name.trim().equals(""));

        MediaEndUserClient mediaEndUserClient = new MediaEndUserClient();
        String classType = MediaEndUserClient.class.getSimpleName();
        mediaEndUserClient.connectToMediaServer(classType, Constants.SERVER_ADDRESS, name, Constants.MEDIA_SERVER_PORT);
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setAvailableDevicesForConnect(List<String> availableDevicesForConnect) {
        this.availableDevicesForConnect = availableDevicesForConnect;
    }
}
