package mop.app.client.model.user;

import lombok.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Setter
@Getter
public class Conversation {
    private int conversationID;
    private String type;
    private URL icon;
    private String name;
    private boolean seen;
    private LocalDateTime lastContentDateTime;
    private String content;

    public Conversation() {}

    public Conversation(int conversationID, String type, URL icon, String name, boolean seen, LocalDateTime lastContentDateTime, String content) {
        this.conversationID = conversationID;
        this.type = type;
        this.icon = icon;
        this.name = name;
        this.seen = seen;
        this.lastContentDateTime = lastContentDateTime;
        this.content = content;
    }

    public Conversation(Conversation other) {
        this.conversationID = other.conversationID;
        this.type = other.type;
        this.icon = other.icon;
        this.name = other.name;
        this.seen = other.seen;
        this.lastContentDateTime = other.lastContentDateTime;
        this.content = other.content;
    }

}

