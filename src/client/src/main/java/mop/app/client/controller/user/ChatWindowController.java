package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mop.app.client.Client;
import mop.app.client.dao.user.UserDAO;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class ChatWindowController extends GridPane {
    @FXML
    private VBox topBarCol3;
    @FXML
    private TextArea chatArea;
    @FXML
    private ListView<Message> msgWindow;
    @FXML
    private VBox col2;
    @FXML
    private VBox chatOptions;
    @FXML
    private HBox block;
    @FXML
    private HBox report;
    @FXML
    private HBox deleteChat;
    @FXML
    private HBox leaveChat;

    @FXML
    private HBox searchInInfo;
    @FXML
    private HBox members;
    @FXML
    private HBox groupAdd;

    @FXML
    private VBox chatInfo;
    @FXML
    private HBox writeMsg;
    @FXML
    private VBox col3;
    @FXML
    private HBox chatInfoHBox;
    @FXML
    private VBox topVBox;
    @FXML
    private HBox editName;
    @FXML
    private Label chatWindowName;
    @FXML
    private Button sendButton;

    private IconLabel addFriend;

    private final Text sizeHelper = new Text();;
    private double oldHeight = 0;


    URL placeholder = Client.class.getResource("images/place-holder.png");

    Conversation curConversation;
    ObservableList<Message> convMsg = FXCollections.observableArrayList();
    BiConsumer<Message, Conversation> onNewMessage;

    public ChatWindowController(Conversation inpConversation, Runnable onUpdate, BiConsumer<Message, Conversation> onNewMessage) throws IOException {
        this.curConversation = inpConversation;

        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/chat-window-view.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        if (this.curConversation == null) {
            addFriend = new IconLabel(null, null, null, null);
        } else {
            addFriend = new IconLabel(!this.curConversation.getType().equals("PENDING") ? null : Client.class.getResource("view/user/add-friend-basic-outline-svgrepo-com.png"), !this.curConversation.getType().equals("PENDING") ? "Add Friend" : "Cancel Friend Request", null, null);

        }
        addFriend.setAlignment(Pos.CENTER);
        addFriend.getStyleClass().add("HoverWrapper");
        addFriend.setOnMouseClicked(e -> {
            if (this.curConversation.getType().equals("N/A")) {
                new UserDAO().makeFriendRequest(this.curConversation.getConversationID());
                this.curConversation.setType("PENDING");
                addFriend.update(null, "Cancel Friend Request", null, null);
            } else {
                new UserDAO().cancelFriendRequest(this.curConversation.getConversationID());
                this.curConversation.setType("N/A");
                addFriend.update(Client.class.getResource("view/user/add-friend-basic-outline-svgrepo-com.png"), "Add Friend", null, null);
            }
        });



        updateChatInfo();


        topBarCol3.getChildren().clear();
        topBarCol3.getChildren().add(new IconLabel(inpConversation == null ? null : inpConversation.getIcon(), inpConversation == null ? "You have no friend yet" : inpConversation.getName(), null, inpConversation == null ? null : inpConversation.getContent()));

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
        chatArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isShiftDown()) {
                    chatArea.appendText("\n");
                } else {
                    //sendd
                    sendMessage();
                }
                event.consume();
            }
        });

        groupAdd.setOnMouseClicked(e -> {
            Stage stage = new Stage();
            Scene scene = null;
            try {
                scene = new Scene(new GroupAddController(this.curConversation, () -> {
                    onUpdate.run();
                    stage.close();
                }));
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node)e.getSource()).getScene().getWindow() );
            try {
                stage.getIcons().add(new Image(Objects.requireNonNull(Client.class.getResourceAsStream("images/app-icon.png"))));
            } catch (Exception ex) {
                System.out.println("Failed to load application icon" +  ex);
            }

            stage.setTitle("MOP Application");
            stage.show();
        });

        members.setOnMouseClicked(e -> {
            Stage stage = new Stage();
            Scene scene = null;
            try {
                scene = new Scene(new GroupMembersController(this.curConversation, () -> {
                    onUpdate.run();
                    stage.close();
                }));
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node)e.getSource()).getScene().getWindow());
            try {
                stage.getIcons().add(new Image(Objects.requireNonNull(Client.class.getResourceAsStream("images/app-icon.png"))));
            } catch (Exception ex) {
                System.out.println("Failed to load application icon" +  ex);
            }
            stage.setTitle("MOP Application");
            stage.show();
        });

        sendButton.setOnAction(e->{
            sendMessage();
        });

        msgWindow.setCellFactory(params -> new ListCell<>(){
            IconLabel iconLabel = null;
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setMouseTransparent(true);

                } else {
                    if (iconLabel == null) {
                        iconLabel = new IconLabel(item.getSenderIcon(), item.getSender(), item.getSentAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)), item.getContent());
                    } else {
                        iconLabel.update(item.getSenderIcon(), item.getSender(), item.getSentAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)), item.getContent());
                    }
                    setGraphic(iconLabel);
                    setMouseTransparent(false);

                }
            }
        });


        msgWindow.setItems(convMsg);
        if (convMsg != null) msgWindow.scrollTo(convMsg.size());
    }

    void updateChatInfo() {
        chatOptions.getChildren().clear();
        chatInfoHBox.getChildren().clear();
        if (curConversation == null) {
            writeMsg.setVisible(false);
            col3.setVisible(false);
            return;
        }
        writeMsg.setVisible(true);
        col3.setVisible(true);
        if (curConversation.getType().equals("GROUP")) {
            if (!topVBox.getChildren().contains(editName)) {
                topVBox.getChildren().add(1,editName);
            }
            chatOptions.getChildren().add(deleteChat);
            chatOptions.getChildren().add(leaveChat);
            chatInfoHBox.getChildren().add(searchInInfo);
            chatInfoHBox.getChildren().add(members);
            chatInfoHBox.getChildren().add(groupAdd);


        } else if (curConversation.getType().equals("PAIR")){
            chatOptions.getChildren().add(block);
            chatOptions.getChildren().add(report);
            chatOptions.getChildren().add(deleteChat);
            chatInfoHBox.getChildren().add(searchInInfo);
            chatInfoHBox.getChildren().add(groupAdd);
            topVBox.getChildren().remove(editName);
        } else {
            chatOptions.getChildren().add(block);
            chatOptions.getChildren().add(report);
            topVBox.getChildren().remove(editName);

        }

    }

    void updateTopBarIfNotFriend() {
        if (curConversation == null) {
            col2.getChildren().clear();
            return;
        }
        if (Objects.equals(curConversation.getType(), "N/A") || Objects.equals(curConversation.getType(), "PENDING")) {
            if (!col2.getChildren().contains(addFriend)) {
                col2.getChildren().add(1, addFriend);
            }
            col2.getChildren().remove(writeMsg);
            if (this.curConversation.getType().equals("PENDING")) {
                addFriend.update(null, "Cancel Friend Request", null, null);
            } else {
                addFriend.update(Client.class.getResource("view/user/add-friend-basic-outline-svgrepo-com.png"), "Add Friend", null, null);
            }

        } else {
            col2.getChildren().remove(addFriend);
            if (!col2.getChildren().contains(writeMsg)) {
                col2.getChildren().add(writeMsg);
            }
        }
    }

    void changeCurConv(Conversation item) {
        curConversation = item;
        chatWindowName.setText(item == null ? null : item.getName());
        updateChatInfo();
        updateTopBarIfNotFriend();
        //Update activity status too
        //Update blocked
        //Messages
        convMsg.clear();
        if (item != null) convMsg.addAll(new UserDAO().getMessages(item.getConversationID()));
        

        topBarCol3.getChildren().clear();
        if (item != null) topBarCol3.getChildren().add(new IconLabel(item.getIcon(), item.getName(), null, "call"));

        msgWindow.setItems(convMsg);

        if (convMsg != null) msgWindow.scrollTo(convMsg.size());
    }

    Conversation getCurConv() {
        return curConversation;
    }

    void sendMessage() {
        if (!chatArea.getText().trim().isEmpty()) {
            new UserDAO().sendMessage(curConversation.getConversationID(), chatArea.getText());
            Message newMsg = new Message("You", null, LocalDateTime.now(),chatArea.getText());
            convMsg.add(newMsg);
            chatArea.clear();
            msgWindow.scrollTo(convMsg.size());
            onNewMessage.accept(newMsg, curConversation);
        }
    }
}
