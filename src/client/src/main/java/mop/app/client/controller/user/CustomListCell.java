package mop.app.client.controller.user;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;

public class CustomListCell extends ListCell<Conversation> {
    IconLabel iconLabel = null;
    @Override
    protected void updateItem(Conversation item, boolean empty) {
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
