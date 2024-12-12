package mop.app.client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import mop.app.client.controller.user.ChatController;
import mop.app.client.dto.Request;
import mop.app.client.dto.RequestType;
import mop.app.client.model.user.Message;
import mop.app.client.util.ObjectMapperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mop.app.client.Client.registerActivity;

public class SocketClient implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    Thread thread = new Thread(this);;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public SocketClient() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());


            thread.setDaemon(true);


        } catch (IOException e) {
            logger.error("Failed to connect to server: {}", e.getMessage());
        }
    }

    public void start() {
        thread.start();
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
            String response;
            synchronized (in) {
                response = (String) in.readObject();
                logger.info("Received response from server: {}", response);
            }

            return response;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to send request to server: {}", e.getMessage());
            return null;
        }
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

    public void close() {
        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
            if (thread != null) thread.interrupt();
        } catch (IOException e) {
            logger.error("Failed to close connection: {}", e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Register CHAT_ACTIVITY
            registerActivity();
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
