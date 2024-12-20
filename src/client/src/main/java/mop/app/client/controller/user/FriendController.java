package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;
import mop.app.client.Client;
import mop.app.client.dao.user.ConversationDAO;
import mop.app.client.dao.user.RelationshipDAO;
import mop.app.client.model.user.Conversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FriendController extends GridPane {
    private static final Logger log = LoggerFactory.getLogger(FriendController.class);
    @FXML
    private Label curOption;
    @FXML
    private HBox friendList;
    @FXML
    private HBox friendRequests;
    @FXML
    private ListView<Conversation> listF;
    @FXML
    private VBox col0;
    @FXML
    private VBox col2;
    @FXML
    private HBox friendListAllOnl;
    @FXML
    private Button all;
    @FXML
    private Button online;

    private SearchUsersController searchUsersController;

    private final SearchUsersController friendListSearchUsersController;
    private final SearchUsersController friendRequestsSearchUsersController;



    private final ChatWindowController chatWindowController;

    private Predicate<Conversation> showWhich;
    private final Predicate<Conversation> showOnline;
    Predicate<Conversation> filter = (conv)->true;


    Conversation curFriend;
    FilteredList<Conversation> friendListObservable;
    FilteredList<Conversation> friendRequestsList;


    public FriendController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/friend-view.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        // Default pressed
        friendListObservable = new FilteredList<>(FXCollections.observableArrayList(RelationshipDAO.getFriends()));
        friendList.getStyleClass().clear();
        friendList.getStyleClass().add("PressedWrapper");

        showOnline = (conv) -> conv.getContent().equals("Online");
        showWhich = (conv)->true;



        friendListSearchUsersController = new SearchUsersController(
                (text) -> {
                    filter = (conv) -> conv.getName().toLowerCase().contains(text.toLowerCase());
                    friendListObservable.setPredicate(filter.and(showWhich));
                },
                () -> {
                    filter = (conv)->true;
                    friendListObservable.setPredicate(filter.and(showWhich));
                }
        );

        friendRequestsSearchUsersController = new SearchUsersController(
                (text) -> {
                    friendRequestsList.setPredicate((conv) -> conv.getName().toLowerCase().contains(text.toLowerCase()));
                },
                () -> {
                    friendRequestsList.setPredicate(conv->true);
                }
        );

        searchUsersController = friendListSearchUsersController;

        all.setOnAction(e->{
            showWhich = (conv)->true;
            friendListObservable.setPredicate(filter.and(showWhich));

        });
        online.setOnAction(e->{
            showWhich = showOnline;
            friendListObservable.setPredicate(filter.and(showWhich));

        });


        col2.getChildren().add(col2.getChildren().indexOf(listF), friendListSearchUsersController);





        curFriend = friendListObservable == null ? null : !friendListObservable.isEmpty() ? friendListObservable.getFirst() : null;

        chatWindowController = new ChatWindowController(curFriend, null, () -> {}, (msg, curConv) -> {}, (item)->{
            //seen
            item.setSeen(true);
            ConversationDAO.setSeen(item.getConversationID(), true);
            //
        });
        //Init GUI


        Consumer<Conversation> changeToChat = item -> {
            curFriend = item;
            if (!getChildren().contains(chatWindowController)) {
                getChildren().remove(1);
                setConstraints(chatWindowController, 1, 0);
                getChildren().add(chatWindowController);
            }
            Conversation curConv = new Conversation(item);
            curConv.setType("PAIR");
            curConv.setConversationID(ConversationDAO.getPairConversationId(item.getConversationID()));
            chatWindowController.changeCurConv(curConv, ConversationDAO.getRelationShipInAPairConversation(curConv.getConversationID()));

        };


        Callback<ListView<Conversation>, ListCell<Conversation>> friendListFactory = param -> new ListCellWithButtons(
                changeToChat,
                Arrays.asList(new Pair<String, Consumer<Conversation>>(
                                "view/user/decline-svgrepo-com.png", item -> {
                                    RelationshipDAO.block(item.getConversationID());
                                    friendListObservable.getSource().remove(item);
                        }),
                        new Pair<String, Consumer<Conversation>>(
                                "view/user/unfriend-svgrepo-com.png", item -> {
                            RelationshipDAO.unfriend(item.getConversationID());
                            friendListObservable.getSource().remove(item);
                        })
                )
        );
        Callback<ListView<Conversation>, ListCell<Conversation>> friendRequestsFactory = param -> new ListCellWithButtons(
                (item)->{},
                Arrays.asList(new Pair<String, Consumer<Conversation>>(
                        "view/user/accept-svgrepo-com.png", item -> {
                            Conversation newItem = RelationshipDAO.acceptFriendRequest(item.getConversationID(), item.getName());
                            ChatController.getDmList().add(0, newItem);
                            ((ObservableList<Conversation>)friendListObservable.getSource()).add(newItem);
                            friendRequestsList.getSource().remove(item);
                        }),
                        new Pair<String, Consumer<Conversation>>(
                        "view/user/decline-svgrepo-com.png", item -> {})
                )
        );



        //Handlers
        friendList.setOnMouseClicked((e)->{
            friendListObservable = new FilteredList<>(FXCollections.observableArrayList(RelationshipDAO.getFriends()));
            col2.getChildren().set(col2.getChildren().indexOf(searchUsersController), friendListSearchUsersController);
            searchUsersController = friendListSearchUsersController;

            getChildren().remove(1);
            getChildren().add(col2);
            curOption.setText("Friend List");
            friendRequests.getStyleClass().clear();
            friendList.getStyleClass().clear();
            friendRequests.getStyleClass().add("HoverWrapper");
            friendList.getStyleClass().add("PressedWrapper");
            if (!col2.getChildren().contains(friendListAllOnl)) showFriendListAllOnl();
            listF.setItems(friendListObservable);
            listF.setCellFactory(friendListFactory);
        });
        friendRequests.setOnMouseClicked((e)->{
            friendRequestsList = new FilteredList<>(FXCollections.observableArrayList(RelationshipDAO.getFriendRequests()));
            col2.getChildren().set(col2.getChildren().indexOf(searchUsersController), friendRequestsSearchUsersController);
            searchUsersController = friendRequestsSearchUsersController;


            getChildren().remove(1);
            getChildren().add(col2);
            curOption.setText("Friend Requests");
            friendList.getStyleClass().clear();
            friendRequests.getStyleClass().clear();
            friendList.getStyleClass().add("HoverWrapper");
            friendRequests.getStyleClass().add("PressedWrapper");
            hideFriendListAllOnl();
            listF.setItems(friendRequestsList);
            listF.setCellFactory(friendRequestsFactory);
        });



        // Scroll

        listF.setCellFactory(friendListFactory);



        // start cell factory

        listF.setItems(friendListObservable);



    }

    private void showFriendListAllOnl() {
        col2.getChildren().add(1, friendListAllOnl);
    }

    private void hideFriendListAllOnl() {
        col2.getChildren().remove(friendListAllOnl);
    }

    public class FriendsControl extends HBox {
        public FriendsControl() throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/friends.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        }

    }


}
