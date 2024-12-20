package mop.app.client.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
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
import mop.app.client.controller.auth.RegisterController;
import mop.app.client.dao.user.MessageDAO;
import mop.app.client.dao.user.ConversationDAO;
import mop.app.client.dao.user.RelationshipDAO;
import mop.app.client.dao.user.UserDAO;
import mop.app.client.dto.Request;
import mop.app.client.dto.RequestType;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;
import mop.app.client.model.user.Relationship;
import mop.app.client.network.AsyncSocketClient;
import mop.app.client.util.ObjectMapperConfig;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
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
    private Label blockLabel;
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
    private TextField chatWindowName;
    @FXML
    private Button sendButton;

    private final IconLabel addFriend;

    private final ContextMenu msgContextMenu = new ContextMenu();
    private final MenuItem removeForYou = new MenuItem("Remove for you");
    private final MenuItem removeForEveryone = new MenuItem("Remove for Everyone");



    private final Text sizeHelper = new Text();;
    private double oldHeight = 0;

    private Conversation curConversation;
    private Relationship curRelationship;
    private final ObservableList<Message> convMsg = FXCollections.observableArrayList();
    private final int maxCursor = 1000000000;
    private int cursorMsgId = maxCursor;
    private final BiConsumer<Message, Conversation> onNewMessage;

    public ChatWindowController(Conversation inpConversation, Relationship inpRelationship, Runnable onChatControllerUpdate, BiConsumer<Message, Conversation> onNewMessage, Consumer<Conversation> onSeen) throws IOException {
        this.curConversation = inpConversation;
        this.onNewMessage = onNewMessage;
        this.curRelationship = inpRelationship;
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/chat-window-view.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        System.out.println(col2.getChildren().size());



        if (this.curRelationship == null) {
            addFriend = new IconLabel(null, "null", null, null);
        } else {
            addFriend = new IconLabel(!this.curRelationship.getStatus().equals("PENDING") ? null : Client.class.getResource("view/user/add-friend-basic-outline-svgrepo-com.png"), !this.curRelationship.getStatus().equals("PENDING") ? "Add Friend" : "Cancel Friend Request", null, null);
        }

        addFriend.setAlignment(Pos.CENTER);
        addFriend.getStyleClass().add("HoverWrapper");
        addFriend.setOnMouseClicked(e -> {
            if (curRelationship == null) return;
            if (curRelationship.getStatus().equals(Relationship.NA)) {
                String rel = RelationshipDAO.getReverseRelationship(curRelationship.getId());
                if (rel != null && rel.equals(Relationship.PENDING)) {
                    Conversation newItem = RelationshipDAO.acceptFriendRequest(curRelationship.getId(), curRelationship.getUserDisplayName());
                    this.curRelationship.setStatus(Relationship.FRIEND);
                    col2.getChildren().remove(addFriend);
                    ChatController.getDmList().add(newItem);
                } else {
                    RelationshipDAO.makeFriendRequest(curRelationship.getId());
                    this.curRelationship.setStatus(Relationship.PENDING);
                    addFriend.update(null, "Cancel Friend Request", null, null);
                }
            } else {
                RelationshipDAO.cancelFriendRequest(this.curRelationship.getId());
                this.curRelationship.setStatus(Relationship.NA);
                addFriend.update(null, "Add Friend", null, null);
            }
        });



        removeForYou.setOnAction(e->{
            Message msg = msgWindow.getSelectionModel().getSelectedItem();
            if (msg == null) return;
            MessageDAO.hideMessage(msg.getMsgId());
            convMsg.remove(msg);
        });

        removeForEveryone.setOnAction(e->{
            Message msg = msgWindow.getSelectionModel().getSelectedItem();
            if (msg == null) return;
            MessageDAO.deleteMessage(msg.getMsgId());
            convMsg.remove(msg);
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

        //Event Handling
        chatArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isShiftDown()) {
                    chatArea.appendText("\n");
                } else {
                    //sendd
                    try {
                        sendMessage();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                event.consume();
            }
        });

        groupAdd.setOnMouseClicked(e -> {
            try {
                ModalUtil modal = new ModalUtil(e);
                modal.setScene(new GroupAddController(this.curConversation, () -> {
                    onChatControllerUpdate.run();
                    modal.close();
                }));
                modal.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        searchInInfo.setOnMouseClicked(e->{
            ModalUtil modal = new ModalUtil(e);
            try {
                modal.setScene(new SearchMessageControl(this.curConversation, (msg) -> {
                    this.setConvMsg(msg.getMsgId());
                    modal.close();
                }));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            modal.show();
        });

        members.setOnMouseClicked(e -> {
            Stage stage = new Stage();
            Scene scene = null;
            try {
                scene = new Scene(new GroupMembersController(this.curConversation, () -> {
                    onChatControllerUpdate.run();
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
            try {
                sendMessage();
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });

        editName.setOnMouseClicked(e->{
            chatWindowName.setEditable(true);
            Platform.runLater(() -> {
                chatWindowName.requestFocus();
                chatWindowName.selectAll();
            });
        });

        chatWindowName.setOnKeyPressed(e->{
            if (e.getCode() == KeyCode.ENTER) {
                ConversationDAO.setConversationName(curConversation.getConversationID(), chatWindowName.getText());
                chatWindowName.setEditable(false);
                curConversation.setName(chatWindowName.getText());
                updateTopBarCol3(curConversation);
                onSeen.accept(curConversation);
            }
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

        msgWindow.setOnContextMenuRequested(e -> {
            Message msg = msgWindow.getSelectionModel().getSelectedItem();
            if (msg == null) return;
            msgContextMenu.getItems().clear();
            msgContextMenu.getItems().add(removeForYou);
            if (msg.getSenderId() == Client.currentUser.getUserId() && Duration.between(msg.getSentAt(), LocalDateTime.now()).toHours() < 24) {
                msgContextMenu.getItems().add(removeForEveryone);
            }
            msgContextMenu.show(msgWindow, e.getScreenX(), e.getScreenY());
        });

        deleteChat.setOnMouseClicked(e->{
            MessageDAO.hideAllMessages(curConversation.getConversationID());
            convMsg.clear();
            // update dmList
        });

        block.setOnMouseClicked(e->{
            if (blockLabel.getText().equals("Block")) {
                RelationshipDAO.block(curRelationship.getId());
                blockLabel.setText("Unblock");
                writeMsg.setDisable(true);
                topVBox.setDisable(true);
            } else {
                RelationshipDAO.unblock(curRelationship.getId());
                blockLabel.setText("Block");
                writeMsg.setDisable(false);
                topVBox.setDisable(false);

            }
        });

        report.setOnMouseClicked(e->{
            UserDAO.report(ConversationDAO.getRemainingInAPair(curConversation.getConversationID()));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Report");
            alert.setHeaderText("Reported");
            alert.setContentText("User has been reported");
            alert.showAndWait();

        });


        msgWindow.setItems(convMsg);
        Platform.runLater(() -> {
            try {
                ScrollBar bar = (ScrollBar) msgWindow.lookup(".scroll-bar");
                System.out.println("???" + bar);
                bar.valueProperty().addListener((src, ov, nv) -> {
                    if (nv.doubleValue() == 1.) {
                        System.out.print("Scrolled to bottom");
                        onSeen.accept(curConversation);
                    } else if (nv.doubleValue() == 0.) {
                        System.out.print("Scrolled to top");
                        convMsg.addAll(0, MessageDAO.getMessages(curConversation.getConversationID(), cursorMsgId));
                        if (convMsg.size() > 0) {
                            cursorMsgId = convMsg.get(0).getMsgId();
                        }
                    }
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
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
        if (curConversation.getType().equals(Conversation.GROUP)) {
            if (!topVBox.getChildren().contains(editName)) {
                topVBox.getChildren().add(1,editName);
            }
            chatOptions.getChildren().add(deleteChat);
            chatOptions.getChildren().add(leaveChat);
            chatInfoHBox.getChildren().add(searchInInfo);
            chatInfoHBox.getChildren().add(members);
            chatInfoHBox.getChildren().add(groupAdd);


        } else if (curConversation.getType().equals(Conversation.PAIR)){
            chatOptions.getChildren().add(block);
            chatOptions.getChildren().add(report);
            chatOptions.getChildren().add(deleteChat);
            chatInfoHBox.getChildren().add(searchInInfo);
            chatInfoHBox.getChildren().add(groupAdd);
            topVBox.getChildren().remove(editName);
        } else {
            chatOptions.getChildren().add(block);
            chatOptions.getChildren().add(report);
            chatInfoHBox.getChildren().add(searchInInfo);
            topVBox.getChildren().remove(editName);

        }

    }

    void updateTopBarIfNotFriend() {
        if (curConversation == null) {
            col2.setVisible(false);
            return;
        }
        col2.setVisible(true);

        if (curConversation.getType().equals(Conversation.GROUP) || this.curRelationship == null || (!this.curRelationship.getStatus().equals(Relationship.NA) && !this.curRelationship.getStatus().equals(Relationship.PENDING))) {
            col2.getChildren().remove(addFriend);
            if (!col2.getChildren().contains(writeMsg)) {
                col2.getChildren().add(writeMsg);
            }
        } else {
            if (!col2.getChildren().contains(addFriend)) {
                col2.getChildren().add(1, addFriend);
            }

            if (this.curRelationship.getStatus().equals(Relationship.PENDING)) {
                addFriend.update(null, "Cancel Friend Request", null, null);
            } else {
                String rel = RelationshipDAO.getReverseRelationship(this.curRelationship.getId());
                if (rel != null && rel.equals(Relationship.PENDING)) {
                    addFriend.update(null, "Accept Friend Request", null, null);

                } else {
                    addFriend.update(null, "Add Friend", null, null);

                }
            }

        }
    }

    public Conversation getConversation() {
        return curConversation;
    }


    void changeCurConv(Conversation item, Relationship relationship) {
        curConversation = item;
        System.out.println(item.getName());

        curRelationship = relationship;
        chatWindowName.setText(item == null ? null : item.getName());
        cursorMsgId = maxCursor;

        if (relationship != null && relationship.getStatus().equals(Relationship.BLOCK)) {
            blockLabel.setText("Unblock");
            writeMsg.setDisable(true);
            topVBox.setDisable(true);
        } else {
            blockLabel.setText("Block");
            writeMsg.setDisable(false);
            topVBox.setDisable(false);
        }




        if (relationship != null) {
            String rel = RelationshipDAO.getReverseRelationship(relationship.getId());
            this.setDisable(rel != null && rel.equals(Relationship.BLOCK));
        } else {
            this.setDisable(false);

        }


        updateChatInfo();
        updateTopBarIfNotFriend();
        //Update activity status too
        //Update blocked
        //Messages

        convMsg.clear();
        if (item != null && (item.getType().equals("PAIR") || item.getType().equals("GROUP"))) {
            convMsg.addAll(MessageDAO.getMessages(item.getConversationID(), cursorMsgId));
            if (convMsg.size() > 0) {
                cursorMsgId = convMsg.get(0).getMsgId();
            }
        }
        

        updateTopBarCol3(item);

        msgWindow.setItems(convMsg);

        msgWindow.scrollTo(convMsg.size());

        if (curConversation.getName().equals(RegisterController.DELETED_USER)) {
            this.setDisable(true);
        }
    }

    void updateTopBarCol3(Conversation item) {
        topBarCol3.getChildren().clear();
        if (item != null) topBarCol3.getChildren().add(new IconLabel(item.getIcon(), item.getName(), null, null));
    }

    void sendMessage() throws JsonProcessingException {
        if (!chatArea.getText().trim().isEmpty()) {
            ConversationDAO.setSeen(curConversation.getConversationID(), false);
            int msgId = MessageDAO.sendMessage(curConversation, curRelationship, chatArea.getText());
            sendMessageNet(new Message(Client.currentUser.getDisplayName(), null, LocalDateTime.now(), chatArea.getText(), curConversation.getConversationID(), (int)Client.currentUser.getUserId(), msgId));
            Message newMsg = new Message("You", null, LocalDateTime.now(),chatArea.getText(), curConversation.getConversationID(), (int)Client.currentUser.getUserId(), msgId);
            convMsg.add(newMsg);
            chatArea.clear();
            msgWindow.scrollTo(convMsg.size());
            onNewMessage.accept(newMsg, curConversation);
        }
    }

    void sendMessageNet(Message msg) throws JsonProcessingException {
            AsyncSocketClient socketClient = Client.socketClient;

            if (!socketClient.isConnectionValid()) {
                return;
            }


            ObjectMapper mapper = ObjectMapperConfig.getObjectMapper();

            Request req = new Request(RequestType.SEND_MESSAGE, msg);
            String rawReq = mapper.writeValueAsString(req);
            socketClient.sendAsyncRequest(rawReq);
    }


    public void handleNewMessage(Message msg) {
        if (curConversation != null && curConversation.getConversationID() == msg.getConversationId()) {
            convMsg.add(msg);
        }
    }

    public void setConvMsg(int msgId) {
        convMsg.clear();
        convMsg.addAll(MessageDAO.getMessages(curConversation.getConversationID(), msgId + 1));
        if (convMsg.size() > 0) {
            cursorMsgId = convMsg.get(0).getMsgId();
        }
    }
}


