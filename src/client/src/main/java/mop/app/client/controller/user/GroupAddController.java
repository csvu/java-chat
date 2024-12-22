package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import mop.app.client.Client;
import mop.app.client.dao.user.ConversationDAO;
import mop.app.client.dao.user.RelationshipDAO;
import mop.app.client.model.user.Conversation;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GroupAddController extends VBox {
    ObservableList<Conversation> friendListObservable = FXCollections.observableArrayList();

    @FXML
    ListView<Conversation> listFriend;
    @FXML
    private Text groupAddTitle;
    @FXML
    private TextField groupName;
    @FXML
    private Button addButton;

    public GroupAddController(Conversation cur, Runnable onClose) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/group-add.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        groupName.setText(cur.getName());
        friendListObservable.addAll(RelationshipDAO.getFriendsNotInConversation(cur.getConversationID()));
        if (friendListObservable.isEmpty()) {
            groupName.setDisable(true);
            addButton.setDisable(true);
            groupAddTitle.setText("No friends to add");
        }
        if (cur.getType().equals(Conversation.GROUP)) {
            groupName.setDisable(true);
        }
        listFriend.setCellFactory(params -> new ListCell<>() {
            @Override
            protected void updateItem(Conversation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setMouseTransparent(true);
                } else {
                    setText(item.getName());
                    // center text and font medium, white
                    setStyle("-fx-alignment: center; -fx-font-size: 16; -fx-text-fill: white;");
                    setMouseTransparent(false);
                }
            }
        });

        listFriend.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //multiple choice but not need to shift

        listFriend.setItems(friendListObservable);

        addButton.setOnAction(e -> {
            List<Integer> selected = Arrays.asList(listFriend.getSelectionModel().getSelectedItems().stream().map(Conversation::getConversationID).toArray(Integer[]::new));
            ConversationDAO.addGroup(cur.getConversationID(), groupName.getText(), cur.getType(), selected);
            onClose.run();
        });


    }

}
