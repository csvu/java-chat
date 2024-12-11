package mop.app.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import mop.app.server.Client;
import mop.app.server.Server;
import mop.app.server.dao.MessageDAO;
import mop.app.server.dto.MessageDTO;
import mop.app.server.dto.Request;
import mop.app.server.dto.RequestType;
import mop.app.server.util.ObjectMapperConfig;

import java.io.IOException;
import java.util.ArrayList;

public class MessageController {
    public synchronized void sendMessage(MessageDTO message) {
        Request request = Request.builder()
            .type(RequestType.SERVER_MESSAGE)
            .data(message)
            .build();

        ArrayList<Integer> usersInConversation = new MessageDAO().getUsersInConversation(message.getConversationId(), message.getSenderId());

        Server.clients.forEach(clientHandler -> {
            Client client = clientHandler.getClient();
            System.out.println(client.getUserId());
            System.out.println(usersInConversation);
            System.out.println(client.getPort());
            if (usersInConversation.contains(client.getUserId())) {
                ObjectMapper objectMapper = ObjectMapperConfig.getObjectMapper();

                try {
                    String jsonResponse = objectMapper.writeValueAsString(request);
                    System.out.println("Sending message to client: " + jsonResponse);
                    synchronized (client.getOut()) {
                        client.getOut().writeObject(jsonResponse);
                        client.getOut().flush();
                    }

                } catch (IOException e) {
                    System.out.println("Failed to send message to client: " + e.getMessage());
                }
            }
        });

    }
}
