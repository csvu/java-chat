package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import mop.app.client.Client;
import mop.app.client.dao.user.MessageDAO;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.MessageInConversation;

import java.io.IOException;
import java.util.function.Consumer;

public class SearchMessageControl extends VBox {
    ObservableList<MessageInConversation> messageListObservable = FXCollections.observableArrayList();

    SearchUsersController searchUsersController;
    @FXML
    ListView<MessageInConversation> listMessage;
    @FXML
    private Button jumpButton;

    public SearchMessageControl(Conversation cur, Consumer<MessageInConversation> onClose) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/search-modal.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        listMessage.setCellFactory(params -> new ConversationCustomListCell<>());
        listMessage.setItems(messageListObservable);

        searchUsersController = new SearchUsersController(
                (text) -> {
                    messageListObservable.clear();
                    messageListObservable.addAll(MessageDAO.getMatchedMessages(cur.getConversationID(), text));
                },
                () -> {}
        );


        getChildren().add(0, searchUsersController);



        jumpButton.setOnAction(e -> {
            MessageInConversation msg = listMessage.getSelectionModel().getSelectedItem();
            if (msg == null) {
                return;
            }
            onClose.accept(msg);
        });


    }

}
