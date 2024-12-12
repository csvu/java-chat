package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import mop.app.client.Client;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.MessageInConversation;
import mop.app.client.model.user.Relationship;

import java.io.IOException;
import java.util.List;

public class ListSearch extends VBox {
    ObservableList<Relationship> strangersObs = FXCollections.observableArrayList();
    ObservableList<MessageInConversation> searchMessagesObs = FXCollections.observableArrayList();
    @FXML
    ListView<Relationship> strangers;
    @FXML
    ListView<MessageInConversation> searchMessages;

    public ListSearch(ChatWindowController chatWindowController) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/search-vbox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        VBox.setVgrow(this, Priority.ALWAYS);
        strangers.setCellFactory(lv -> new UserCustomListCell());
        strangers.setItems(strangersObs);
        searchMessages.setCellFactory(lv -> new ConversationCustomListCell<>());
        searchMessages.setItems(searchMessagesObs);
    }

    public void setStrangers(List<Relationship> list) {
        strangersObs.clear();
        strangersObs.addAll(list);
    }

    public void setSearchMessages(List<MessageInConversation> list) {
        searchMessagesObs.clear();
        searchMessagesObs.addAll(list);
    }


}
