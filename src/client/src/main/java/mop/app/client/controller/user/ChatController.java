package mop.app.client.controller.user;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import mop.app.client.Client;
import mop.app.client.dao.user.ConversationDAO;
import mop.app.client.dao.user.MessageDAO;
import mop.app.client.dao.user.UserDAO;
import mop.app.client.dto.FriendStatisticDTO;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;
import mop.app.client.model.user.MessageInConversation;
import mop.app.client.model.user.Relationship;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ChatController extends GridPane {
    public static final ObservableList<Conversation> dmList = FXCollections.observableArrayList();;

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
                    Task<ArrayList<Conversation>> task = new Task<>() {
                        @Override
                        protected ArrayList<Conversation> call() {
                            return ConversationDAO.getConv();
                        }
                    };

                    task.setOnSucceeded(event -> {
                        dmList.clear();
                        dmList.addAll(task.getValue());
                    });

                    new Thread(task).start();

                },
                (msg, curConv) -> {
                    dmList.remove(curConv);
                    curConv.setSeen(true);
                    curConv.setContent(msg.getContent());
                    curConv.setLastContentDateTime(msg.getSentAt());
                    dmList.add(0, curConv);
                    listViewCol2.getSelectionModel().select(0);
                },
                this::onSeen
        );

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
           ;
            chatWindowController.changeCurConv(msg,  ConversationDAO.getRelationShipInAPairConversation(msg.getConversationID()));
            chatWindowController.setConvMsg(msg.getMsgId());
        });

        //Handlers
        searchUsersController = new SearchUsersController(
                (textQuery) -> {
                    col0.getChildren().remove(listViewCol2);
                    if (!col0.getChildren().contains(listSearch)) col0.getChildren().add(listSearch);
                    System.out.println(textQuery);

                    Task<ArrayList<Relationship>> taskStranger = new Task<>() {
                        @Override
                        protected ArrayList<Relationship> call() {
                            return UserDAO.getMatched(textQuery);
                        }
                    };
                    taskStranger.setOnSucceeded(event -> {
                        listSearch.setStrangers(taskStranger.getValue());
                    });
                    new Thread(taskStranger).start();

                    Task<ArrayList<MessageInConversation>> taskMessage = new Task<>() {
                        @Override
                        protected ArrayList<MessageInConversation> call() {
                            return MessageDAO.getMatchedMessages(textQuery);
                        }
                    };

                    taskMessage.setOnSucceeded(event -> {
                        listSearch.setSearchMessages(taskMessage.getValue());
                    });
                    new Thread(taskMessage).start();




                },
                () -> {
                    Task<ArrayList<Conversation>> task = new Task<>() {
                        @Override
                        protected ArrayList<Conversation> call() {
                            return ConversationDAO.getConv();
                        }
                    };

                    task.setOnSucceeded(event -> {
                        dmList.clear();
                        dmList.addAll(task.getValue());
                        col0.getChildren().remove(listSearch);
                        if (!col0.getChildren().contains(listViewCol2)) col0.getChildren().add(listViewCol2);
                    });
                    new Thread(task).start();


                }
        );



        //Init
        col0.getChildren().add(0, searchUsersController);

        setConstraints(chatWindowController, 1, 0);
        getChildren().add(chatWindowController);

        // Scroll
        listViewCol2.setCellFactory(param -> new ConversationCustomListCell<>());
        listViewCol2.setOnMouseClicked(mouseEvent -> {
            Conversation selected = listViewCol2.getSelectionModel().getSelectedItem();
            if (selected != null) {
                chatWindowController.changeCurConv(selected, ConversationDAO.getRelationShipInAPairConversation(listViewCol2.getSelectionModel().getSelectedItem().getConversationID()));
                onSeen(selected);
            }
        });

        chatWindowController.changeCurConv(firstConversation, firstConversation == null ? null : ConversationDAO.getRelationShipInAPairConversation(firstConversation.getConversationID()));

        // start cell factory
        listViewCol2.setItems(dmList);
        listViewCol2.getSelectionModel().select(0);

    }

    void update() {
        Task<ArrayList<Conversation>> task = new Task<>() {
            @Override
            protected ArrayList<Conversation> call() {
                return ConversationDAO.getConv();
            }
        };

        task.setOnSucceeded(event -> {
            dmList.clear();
            dmList.addAll(task.getValue());
            listViewCol2.getSelectionModel().select(0);
            if (!dmList.isEmpty()) {
                if (listViewCol2.getSelectionModel().getSelectedItem() != null) chatWindowController.changeCurConv(listViewCol2.getSelectionModel().getSelectedItem(), new ConversationDAO().getRelationShipInAPairConversation(listViewCol2.getSelectionModel().getSelectedItem().getConversationID()));
            }
        });
        new Thread(task).start();

    }

    static ObservableList<Conversation> getDmList() {
        return dmList;
    }

    public void onSeen(Conversation selected) {
        //seen
        selected.setSeen(true);
        ConversationDAO.setSeen(selected.getConversationID(), true);
        //
        int idx = dmList.indexOf(selected);
        dmList.remove(selected);
        dmList.add(idx, selected);
        listViewCol2.getSelectionModel().select(idx);


    }


    public synchronized static void handleNewMessage(Message msg) {
        int cnt = 0;
        for (Conversation conv : dmList) {
            if (conv.getConversationID() == msg.getConversationId()) {
                dmList.remove(conv);
                conv.setSeen(false);
                conv.setContent(msg.getContent());
                conv.setLastContentDateTime(msg.getSentAt());
                dmList.add(0, conv);
                cnt++;
                break;
            }
        }
        if (cnt == 0) {
            Conversation conv = ConversationDAO.getConv(msg.getConversationId());
            if (conv.getType().equals(Conversation.PAIR)) {
                conv.setName(msg.getSender());
            }
            conv.setSeen(false);
            conv.setContent(msg.getContent());
            conv.setLastContentDateTime(msg.getSentAt());
            dmList.add(0, conv);
        }
        chatWindowController.handleNewMessage(msg);
        FriendController.chatWindowController.handleNewMessage(msg);




    }






}
