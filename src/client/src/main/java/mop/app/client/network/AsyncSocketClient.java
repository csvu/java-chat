package mop.app.client.network;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import mop.app.client.controller.user.ChatController;
import mop.app.client.dto.Request;
import mop.app.client.dto.RequestType;
import mop.app.client.model.user.Message;
import mop.app.client.util.ObjectMapperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncSocketClient extends SocketClient implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AsyncSocketClient.class);
    ;

    public AsyncSocketClient() throws IOException {
        super();
        Thread thread = new Thread(this);
        thread.start();
    }



    public synchronized void sendAsyncRequest(String request) {
        try {
            logger.info("Sending async request to server: {}", request);
            out.writeObject(request);
            out.flush();

        } catch (IOException e) {
            logger.error("Failed to send async request to server: {}", e.getMessage());
        }
    }


    @Override
    public void run() {
        try {
            while (true) {
                logger.info("Waiting for data from server...");
                String rawInputFromServer = in.readObject().toString();
                if (rawInputFromServer == null) {
                    logger.error("Data from server is null");
                }
                logger.info("Request: {}", rawInputFromServer);

                ObjectMapper objectMapper = ObjectMapperConfig.getObjectMapper();

                Request inputFromServer = objectMapper.readValue(rawInputFromServer, Request.class);
                if (inputFromServer.getType() == RequestType.SERVER_MESSAGE) {
                    System.out.println("Received message from server" + inputFromServer.getData());
                    Platform.runLater(() -> ChatController.handleNewMessage(ObjectMapperConfig.getObjectMapper().convertValue(inputFromServer.getData(), Message.class)));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Client has disconnected: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
        } finally {
            close();
        }
    }
}
