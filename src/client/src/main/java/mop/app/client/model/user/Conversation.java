package mop.app.client.model.user;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Conversation {
    private int conversationID;
    private String type;
    private URL icon;
    private String name;
    private boolean seen;
    private LocalDateTime lastContentDateTime;
    private String content;

    public LocalDateTime getLastContentDateTime() {
        return lastContentDateTime;
    }

    public void setLastContentDateTime(LocalDateTime lastContentDateTime) {
        this.lastContentDateTime = lastContentDateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
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

    public int getConversationID() {
        return conversationID;
    }

    public String getType() {
        return type;
    }

    public URL getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setConversationID(int conversationID) {
        this.conversationID = conversationID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIcon(URL icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
