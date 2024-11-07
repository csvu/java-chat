package mop.app.client.model.user;

public class Conversation {
    private int conversationID;
    private String type;
    private String icon;
    private String name;
    private boolean seen;

    public Conversation(int conversationID, String type, String icon, String name, boolean seen) {
        this.conversationID = conversationID;
        this.type = type;
        this.icon = icon;
        this.name = name;
        this.seen = seen;
    }

    public int getConversationID() {
        return conversationID;
    }

    public String getType() {
        return type;
    }

    public String getIcon() {
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

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
