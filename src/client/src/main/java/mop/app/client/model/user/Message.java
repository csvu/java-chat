package mop.app.client.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.net.URL;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
    private String sender;
    private URL senderIcon;
    private LocalDateTime sentAt;
    private String content;
    private int conversationId;
    private int senderId;
    private int msgId;

}


