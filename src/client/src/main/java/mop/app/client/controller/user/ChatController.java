package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import mop.app.client.Client;
import mop.app.client.dao.user.ConversationDAO;
import mop.app.client.dao.user.MessageDAO;
import mop.app.client.dao.user.UserDAO;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;
import mop.app.client.model.user.MessageInConversation;
import mop.app.client.model.user.Relationship;

import java.io.IOException;
import java.net.URL;


public class ChatController extends GridPane {
    private static ObservableList<Conversation> dmList = FXCollections.observableArrayList();;

    private static ChatWindowController chatWindowController;
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
        dmList.clear();
        dmList.addAll(ConversationDAO.getConv());
        //Init

        Conversation firstConversation = dmList == null || dmList.isEmpty() ? null : dmList.getFirst();
        chatWindowController = new ChatWindowController(
                firstConversation,
                (firstConversation == null) ? null : ConversationDAO.getRelationShipInAPairConversation(firstConversation.getConversationID()),
                () -> {
                    dmList.clear();
                    dmList.addAll(ConversationDAO.getConv());
                },
                (msg, curConv) -> {
                    dmList.remove(curConv);
                    curConv.setContent(msg.getContent());
                    curConv.setLastContentDateTime(msg.getSentAt());
                    dmList.add(0, curConv);
                    listViewCol2.getSelectionModel().select(0);
                });

        listSearch = new ListSearch(chatWindowController);
        listSearch.strangers.setOnMouseClicked(mouseEvent -> {
            Relationship selected = listSearch.strangers.getSelectionModel().getSelectedItem();
            int conversationID = ConversationDAO.getPairConversationId(selected.getId());
            Conversation conv;
            if (conversationID != -1) {
                conv = new Conversation(conversationID, "PAIR", null, selected.getUserDisplayName(), false, null, null);
            } else {
                conv = new Conversation(-1, "PAIR", null, selected.getUserDisplayName(), false, null, null);
            }
            chatWindowController.changeCurConv(conv, selected);
        });
        listSearch.searchMessages.setOnMouseClicked(mouseEvent -> {
            MessageInConversation msg = listSearch.searchMessages.getSelectionModel().getSelectedItem();
            chatWindowController.changeCurConv(msg, null);
            chatWindowController.setConvMsg(msg.getMsgId());
        });

        //Handlers
        searchUsersController = new SearchUsersController(
                (textQuery) -> {
                    col0.getChildren().remove(listViewCol2);
                    if (!col0.getChildren().contains(listSearch)) col0.getChildren().add(listSearch);
                    System.out.println(textQuery);

                    listSearch.setStrangers(UserDAO.getMatched(textQuery));
                    listSearch.setSearchMessages(MessageDAO.getMatchedMessages(textQuery));
                },
                () -> {
                    dmList.clear();
                    dmList.addAll(ConversationDAO.getConv());
                    col0.getChildren().remove(listSearch);
                    if (!col0.getChildren().contains(listViewCol2)) col0.getChildren().add(listViewCol2);
                }
        );



        //Init
        col0.getChildren().add(0, searchUsersController);

        setConstraints(chatWindowController, 1, 0);
        getChildren().add(chatWindowController);

        // Scroll
        listViewCol2.setCellFactory(param -> new ConversationCustomListCell<>());
        listViewCol2.setOnMouseClicked(mouseEvent -> {
            if (listViewCol2.getSelectionModel().getSelectedItem() != null) chatWindowController.changeCurConv(listViewCol2.getSelectionModel().getSelectedItem(), ConversationDAO.getRelationShipInAPairConversation(listViewCol2.getSelectionModel().getSelectedItem().getConversationID()));
        });

        chatWindowController.changeCurConv(firstConversation, firstConversation == null ? null : ConversationDAO.getRelationShipInAPairConversation(firstConversation.getConversationID()));

        // start cell factory
        listViewCol2.setItems(dmList);
        listViewCol2.getSelectionModel().select(0);

    }

    void update() {
        dmList.clear();
        dmList.addAll(ConversationDAO.getConv());
        listViewCol2.getSelectionModel().select(0);
        if (!dmList.isEmpty()) {
            if (listViewCol2.getSelectionModel().getSelectedItem() != null) chatWindowController.changeCurConv(listViewCol2.getSelectionModel().getSelectedItem(), new ConversationDAO().getRelationShipInAPairConversation(listViewCol2.getSelectionModel().getSelectedItem().getConversationID()));
        }
    }

    static ObservableList<Conversation> getDmList() {
        return dmList;
    }

    public synchronized static void handleNewMessage(Message msg) {
        int cnt = 0;
        for (Conversation conv : dmList) {
            if (conv.getConversationID() == msg.getConversationId()) {
                conv.setContent(msg.getContent());
                conv.setLastContentDateTime(msg.getSentAt());
                dmList.remove(conv);
                dmList.add(0, conv);
                cnt++;
                break;
            }
        }
        if (cnt == 0) {
            Conversation conv = ConversationDAO.getConv(msg.getConversationId());
            if (conv.getType().equals("PAIR")) {
                conv.setName(msg.getSender());
            }
            conv.setContent(msg.getContent());
            conv.setLastContentDateTime(msg.getSentAt());
            dmList.add(0, conv);
        }
        chatWindowController.handleNewMessage(msg);




    }






}
