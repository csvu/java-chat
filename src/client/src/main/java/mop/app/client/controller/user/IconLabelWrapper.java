package mop.app.client.controller.user;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Pair;
import mop.app.client.Client;
import mop.app.client.model.user.Conversation;

import java.util.List;
import java.util.function.Consumer;

class IconLabelWrapper extends HBox {
    Conversation item;
    IconLabel iconLabel;

    public IconLabelWrapper(Conversation item, Consumer<Conversation> iconLabelCallback, List<Pair<String, Consumer<Conversation>>> rightIcons) {
        this.iconLabel = new IconLabel(item.getIcon(), item.getName(), null, item.getContent());
        this.iconLabel.getStyleClass().add("HoverWrapper");
        this.iconLabel.setOnMouseClicked(mouseEvent -> iconLabelCallback.accept(this.item));
        HBox.setHgrow(iconLabel, Priority.ALWAYS);
        getChildren().add(iconLabel);


        rightIcons.forEach(e -> {
            IconLabel icon = new IconLabel(e.getKey() == null ? null : Client.class.getResource(e.getKey()), null, null, item.getContent());
            icon.getStyleClass().add("HoverWrapper");
            icon.setOnMouseClicked(mouseEvent -> e.getValue().accept(this.item));
            getChildren().add(icon);
        });

    }
    public void update(Conversation item) {
        this.item = item;
        iconLabel.update(item.getIcon(), item.getName(), null, item.getContent());
    }
}