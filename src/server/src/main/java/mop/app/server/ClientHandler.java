package mop.app.server;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.ToString;
import mop.app.server.controller.AuthController;
import mop.app.server.controller.MessageController;
import mop.app.server.dto.MessageDTO;
import mop.app.server.dto.Request;
import mop.app.server.dto.Response;
import mop.app.server.dto.UserDTO;
import mop.app.server.util.ObjectMapperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ToString
public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    @Getter
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

                    ObjectMapper objectMapper = ObjectMapperConfig.getObjectMapper();

                    Request request = objectMapper.readValue(jsonRequest, Request.class);

                    Response response = processRequest(request);
                    String jsonResponse = objectMapper.writeValueAsString(response);

                    synchronized (client.getOut()) {
                        client.getOut().writeObject(jsonResponse);
                        client.getOut().flush();
                    }
                    logger.info("Response: {}", response);
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Client has disconnected: {}", e.getMessage());
        } finally {
            Server.clients.remove(this);
            client.close();
        }
    }

    private Response processRequest(Request request) {
        switch (request.getType()) {
            case LOGIN:
                return authController.login(request.getData());
            case CHAT_ACTIVITY:
                UserDTO data = ObjectMapperConfig.getObjectMapper().convertValue(request.getData(), UserDTO.class);
                client.setUserId((int) data.getUserId());
                return new Response(true, "Chat activity registered");
            case SEND_MESSAGE:
                new MessageController().sendMessage(ObjectMapperConfig.getObjectMapper().convertValue(request.getData(), MessageDTO.class));
                return new Response(true, "Message sent");

            default:
                return new Response(false, "Invalid request type");
        }
    }

}
