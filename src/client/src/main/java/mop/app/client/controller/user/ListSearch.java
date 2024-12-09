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

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ListSearch extends VBox {
    ObservableList<Conversation> strangersObs = FXCollections.observableArrayList();
    ObservableList<Conversation> searchMessagesObs = FXCollections.observableArrayList();
    @FXML
    ListView<Conversation> strangers;
    @FXML
    ListView<Conversation> searchMessages;

    public ListSearch(ChatWindowController chatWindowController) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/search-vbox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        VBox.setVgrow(this, Priority.ALWAYS);
        strangers.setCellFactory(lv -> new CustomListCell());
        strangers.setItems(strangersObs);
        searchMessages.setCellFactory(lv -> new CustomListCell());
        searchMessages.setItems(searchMessagesObs);
    }

    public void setStrangers(List<Conversation> list) {
        strangersObs.clear();
        strangersObs.addAll(list);
    }

    public void setSearchMessages(List<Conversation> list) {
        searchMessagesObs.clear();
        searchMessagesObs.addAll(list);
    }


}
