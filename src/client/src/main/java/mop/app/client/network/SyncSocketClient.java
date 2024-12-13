package mop.app.client.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SyncSocketClient extends SocketClient{
    private static final Logger logger = LoggerFactory.getLogger(SyncSocketClient.class);

    public SyncSocketClient() throws IOException {
        super();

    }

    public String sendRequest(String request) {
        try {

            logger.info("Sending request to server: {}", request);
            out.writeObject(request);
            out.flush();
            logger.info("Sent request to server: {}", request);

            String response;

            response = (String) in.readObject();
            logger.info("Received response from server: {}", response);
            return response;



        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to send request to server: {}", e.getMessage());
            return null;
        }
    }
}
