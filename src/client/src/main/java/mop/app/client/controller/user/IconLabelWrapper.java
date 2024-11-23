package mop.app.client.controller.user;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Pair;
import mop.app.client.Client;
import mop.app.client.controller.user.IconLabel;
import mop.app.client.model.user.Conversation;

import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

class IconLabelWrapper extends HBox {
    Conversation item;
    IconLabel iconLabel;

    public IconLabelWrapper(Conversation item, Consumer<Conversation> iconLabelCallback, List<Pair<String, Consumer<Conversation>>> rightIcons) {
        this.item = item;
        this.iconLabel = new IconLabel(item.getIcon(), item.getName(), null, "Online");
        this.iconLabel.getStyleClass().add("HoverWrapper");
        this.iconLabel.setOnMouseClicked(mouseEvent -> iconLabelCallback.accept(item));
        HBox.setHgrow(iconLabel, Priority.ALWAYS);
        getChildren().add(iconLabel);


        rightIcons.forEach(e -> {
            IconLabel icon = new IconLabel(Client.class.getResource(e.getKey()), null, null, null);
            icon.getStyleClass().add("HoverWrapper");
            icon.setOnMouseClicked(mouseEvent -> e.getValue().accept(item));
            getChildren().add(icon);
        });

    }
    public void update(URL icon, String title, String titleSide, String content) {
        iconLabel.update(icon, title, titleSide, content);
    }
}