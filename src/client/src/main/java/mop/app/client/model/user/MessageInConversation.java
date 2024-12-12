package mop.app.client.model.user;

import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;

public class MessageInConversation extends Conversation {

    private int msgId;
    public MessageInConversation(int msgId, int conversationID, String type, URL icon, String name, boolean seen, LocalDateTime lastContentDateTime, String content) {
        super(conversationID, type, icon, name, seen, lastContentDateTime, content);
        this.msgId = msgId;
    }
    public int getMsgId() {
        return msgId;
    }
    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }
}
