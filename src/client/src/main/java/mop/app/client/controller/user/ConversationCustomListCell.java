package mop.app.client.controller.user;

import javafx.scene.control.ListCell;
import mop.app.client.model.user.Conversation;

public class ConversationCustomListCell<T extends Conversation> extends ListCell<T> {
    IconLabel iconLabel = null;
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            setMouseTransparent(true);
        } else {
            if (iconLabel == null) {
                iconLabel = new IconLabel(item.getIcon(), item.getName(), null, item.getContent());
            } else {
                iconLabel.update(item.getIcon(), item.getName(), null, item.getContent());
            }
            setGraphic(iconLabel);
            setMouseTransparent(false);
        }
    }
}
