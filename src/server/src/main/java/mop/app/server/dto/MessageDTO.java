package mop.app.server.dto;

import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private String sender;
    private URL senderIcon;
    private LocalDateTime sentAt;
    private String content;
    private int conversationId;
    private int senderId;


}


