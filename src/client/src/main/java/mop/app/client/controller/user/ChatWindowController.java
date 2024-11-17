package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import mop.app.client.Client;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;


public class ChatWindowController extends GridPane {
    @FXML
    private VBox topBarCol3;
    @FXML
    private TextArea chatArea;
    @FXML
    private ListView<Message> msgWindow;
    @FXML
    private VBox col2;

    private final Text sizeHelper = new Text();;
    private double oldHeight = 0;


    URL placeholder = Client.class.getResource("images/place-holder.png");

    Conversation curConversation;
    HashMap<Integer, ObservableList<Message>> convMsg;


    public ChatWindowController(Conversation curConversation, HashMap<Integer, ObservableList<Message>> convMsg) throws IOException {
        this.curConversation = curConversation;
        this.convMsg = convMsg;

        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/chat-window-view.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        topBarCol3.getChildren().clear();
        topBarCol3.getChildren().add(new IconLabel(curConversation.getIcon(), curConversation == null ? "You have no friend yet" : curConversation.getName(), null, "Online"));

        // expandable text area
        chatArea.setPrefSize(200, 40);
        sizeHelper.textProperty().bind(chatArea.textProperty());
        sizeHelper.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (oldHeight != newValue.getHeight()) {
                oldHeight = newValue.getHeight();
                chatArea.setPrefHeight(sizeHelper.getLayoutBounds().getHeight());
                HBox par = (HBox) chatArea.getParent();
                par.setPrefHeight(chatArea.getPrefHeight() + par.getPadding().getBottom() + par.getPadding().getTop());
            }
        });


        msgWindow.setCellFactory(params -> new ListCell<>(){
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(new IconLabel(item.getSenderIcon(), item.getSender(), item.getSentAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)), item.getContent()));
                }
            }
        });

        msgWindow.setItems(convMsg.get(curConversation.getConversationID()));
        msgWindow.scrollTo(convMsg.get(curConversation.getConversationID()).size());
    }

    void changeCurConv(Conversation item) {
        topBarCol3.getChildren().clear();
        topBarCol3.getChildren().add(new IconLabel(placeholder, item.getName(), null, "Online"));
        curConversation = item;
        ObservableList<Message> msg = convMsg.get(curConversation.getConversationID());
        msgWindow.setItems(msg);
        if (Objects.equals(curConversation.getType(), "N/A")) {
            IconLabel iconLabel = new IconLabel(Client.class.getResource("view/user/add-friend-basic-outline-svgrepo-com.png"), "Add Friend", null, null);
            iconLabel.setAlignment(Pos.CENTER);
            iconLabel.getStyleClass().add("HoverWrapper");
            col2.getChildren().add(1, iconLabel);
        } else {
            if (col2.getChildren().size() == 4) {
                col2.getChildren().remove(1);
            }
        }
        if (msg != null) msgWindow.scrollTo(convMsg.get(curConversation.getConversationID()).size());
    }
}
