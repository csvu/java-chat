package mop.app.client.controller.user;

import javafx.scene.control.ListCell;
import javafx.util.Pair;
import mop.app.client.controller.user.IconLabelWrapper;
import mop.app.client.model.user.Conversation;

import java.util.List;
import java.util.function.Consumer;

class ListCellWithButtons extends ListCell<Conversation> {
    IconLabelWrapper itemWrapper = null;
    final Consumer<Conversation> iconLabelCallback;
    final List<Pair<String, Consumer<Conversation>>> rightIcons;
    public ListCellWithButtons(Consumer<Conversation> iconLabelCallback, List<Pair<String, Consumer<Conversation>>> rightIcons) {
        this.iconLabelCallback = iconLabelCallback;
        this.rightIcons = rightIcons;
    }

    @Override
    protected void updateItem(Conversation item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            setMouseTransparent(true);
        } else {
            if (itemWrapper == null) {
                itemWrapper = new IconLabelWrapper(item, iconLabelCallback, rightIcons);
            } else {
                itemWrapper.update(item);
            }
            setGraphic(itemWrapper);
            setMouseTransparent(false);
        }
    }
}