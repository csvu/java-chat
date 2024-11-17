package mop.app.client.model.user;

import java.net.URL;
import java.time.LocalDateTime;

public class Message {
    private String sender;
    private URL senderIcon;
    private LocalDateTime sentAt;
    private String content;

    public URL getSenderIcon() {
        return senderIcon;
    }

    public void setSenderIcon(URL senderIcon) {
        this.senderIcon = senderIcon;
    }

    public Message(String sender, URL senderIcon, LocalDateTime sentAt, String content) {
        this.sender = sender;
        this.senderIcon = senderIcon;
        this.sentAt = sentAt;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


