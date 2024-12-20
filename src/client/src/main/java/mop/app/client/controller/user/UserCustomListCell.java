package mop.app.client.controller.user;

import javafx.scene.control.ListCell;
import mop.app.client.model.user.Relationship;

public class UserCustomListCell extends ListCell<Relationship> {
    IconLabel iconLabel = null;
    @Override
    protected void updateItem(Relationship item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            setMouseTransparent(true);
        } else {
            if (iconLabel == null) {
                iconLabel = new IconLabel(null, item.getUserDisplayName(), null, null);
            } else {
                iconLabel.update(null,item.getUserDisplayName(), null, null);
            }
            setGraphic(iconLabel);
            setMouseTransparent(false);
        }
    }
}
