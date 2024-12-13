package mop.app.client.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    protected static final String SERVER_HOST = "localhost";
    protected static final int SERVER_PORT = 8888;

    protected final Socket socket = new Socket(SERVER_HOST, SERVER_PORT);;
    protected final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());;
    protected final ObjectInputStream in = new ObjectInputStream(socket.getInputStream());;


    public SocketClient() throws IOException {
    }


    public boolean isConnectionValid() {
        logger.info("Checking connection to server");
        return socket != null && out != null && in != null;
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
