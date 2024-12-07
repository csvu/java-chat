package mop.app.server;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import lombok.ToString;
import mop.app.server.controller.AuthController;
import mop.app.server.dto.Request;
import mop.app.server.dto.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ToString
public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private Client client;
    private AuthController authController;

    public ClientHandler(Socket clientSocket) {
        try {
            this.client = new Client(clientSocket, clientSocket.getPort());
            this.client.setIn(new ObjectInputStream(clientSocket.getInputStream()));
            this.client.setOut(new ObjectOutputStream(clientSocket.getOutputStream()));
            this.authController = new AuthController();
        } catch (IOException e) {
            logger.error("Error creating client handler: {}", e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String jsonRequest = client.getIn().readObject().toString();
                if (jsonRequest == null) {
                    logger.error("Client request is null");
                }
                logger.info("Request: {}", jsonRequest);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                Request request = objectMapper.readValue(jsonRequest, Request.class);

                Response response = processRequest(request);
                String jsonResponse = objectMapper.writeValueAsString(response);
                logger.info("Response: {}", response);
                client.getOut().writeObject(jsonResponse);
                client.getOut().flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Client has disconnected: {}", e.getMessage());
        } finally {
            client.close();
        }
    }

    private Response processRequest(Request request) {
        switch (request.getType()) {
            case LOGIN:
                return authController.login(request.getData());
            default:
                return new Response(false, "Invalid request type");
        }
    }
}
