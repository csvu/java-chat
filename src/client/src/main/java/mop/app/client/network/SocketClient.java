package mop.app.client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public SocketClient() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            logger.error("Failed to connect to server: {}", e.getMessage());
        }
    }

    public boolean isConnectionValid() {
        logger.info("Checking connection to server");
        return socket != null && out != null && in != null;
    }

    public String sendRequest(String request) {
        try {
            logger.info("Sending request to server: {}", request);
            out.writeObject(request);
            out.flush();

            String response = (String) in.readObject();
            logger.info("Received response from server: {}", response);

            return response;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to send request to server: {}", e.getMessage());
            return null;
        }
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            logger.error("Failed to close connection: {}", e.getMessage());
        }
    }
}
