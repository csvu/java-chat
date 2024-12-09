package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import mop.app.client.Client;
import mop.app.client.dao.user.UserDAO;
import mop.app.client.model.user.Conversation;

import java.io.IOException;
import java.net.URL;


public class ChatController extends GridPane {
    public static ObservableList<Conversation> dmList = FXCollections.observableArrayList();;

    private final ChatWindowController chatWindowController;
    URL placeholder = Client.class.getResource("images/place-holder.png");

//    HashMap<Integer, ObservableList<Message>> convMsg = new HashMap<>();
    private SearchUsersController searchUsersController;

    @FXML
    private ListView<Conversation> listViewCol2;
    private ListSearch listSearch;
    private ObservableList<Conversation> listSearchObs;
    @FXML
    private VBox col0;

    public ChatController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/chat-view.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        // Mock data

        dmList.addAll(new UserDAO().getConv());
        //Init

        Conversation firstConversation = dmList == null || dmList.isEmpty() ? null : dmList.getFirst();
        chatWindowController = new ChatWindowController(
                firstConversation,
                () -> {
                    dmList.clear();
                    dmList.addAll(new UserDAO().getConv());
                },
                (msg, curConv) -> {
                    dmList.remove(curConv);
                    curConv.setContent(msg.getContent());
                    curConv.setLastContentDateTime(msg.getSentAt());
                    dmList.add(0, curConv);
                    listViewCol2.getSelectionModel().select(0);
                });

        listSearch = new ListSearch(chatWindowController);
        listSearch.strangers.setOnMouseClicked(mouseEvent -> chatWindowController.changeCurConv(listSearch.strangers.getSelectionModel().getSelectedItem()));
        listSearch.searchMessages.setOnMouseClicked(mouseEvent -> chatWindowController.changeCurConv(listSearch.searchMessages.getSelectionModel().getSelectedItem()));

        //Handlers
        searchUsersController = new SearchUsersController(
                (textQuery) -> {
                    col0.getChildren().remove(listViewCol2);
                    if (!col0.getChildren().contains(listSearch)) col0.getChildren().add(listSearch);
                    System.out.println(textQuery);

                    listSearch.setStrangers(new UserDAO().getMatched(textQuery));
                    listSearch.setSearchMessages(new UserDAO().getMatchedMessages(textQuery));
                },
                () -> {
                    col0.getChildren().remove(listSearch);
                    if (!col0.getChildren().contains(listViewCol2)) col0.getChildren().add(listViewCol2);
                }
        );



        //Init
        col0.getChildren().add(0, searchUsersController);

        setConstraints(chatWindowController, 1, 0);
        getChildren().add(chatWindowController);

        // Scroll
        listViewCol2.setCellFactory(param -> new CustomListCell());
        listViewCol2.setOnMouseClicked(mouseEvent -> {
            if (listViewCol2.getSelectionModel().getSelectedItem() != null) chatWindowController.changeCurConv(listViewCol2.getSelectionModel().getSelectedItem());
        });

        chatWindowController.changeCurConv(firstConversation);

        // start cell factory
        listViewCol2.setItems(dmList);
        listViewCol2.getSelectionModel().select(0);

    }

    void update() {
        dmList.clear();
        dmList.addAll(new UserDAO().getConv());
        listViewCol2.getSelectionModel().select(0);
        if (!dmList.isEmpty()) {
            chatWindowController.changeCurConv(listViewCol2.getSelectionModel().getSelectedItem());
        }
    }






}
