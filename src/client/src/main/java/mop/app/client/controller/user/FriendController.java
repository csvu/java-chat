package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;
import mop.app.client.Client;
import mop.app.client.dao.user.UserDAO;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

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

    private final ChatWindowController chatWindowController;

    Conversation curFriend;
    ObservableList<Conversation> friendListObservable = FXCollections.observableArrayList();;
    ObservableList<Conversation> groupChad;
    HashMap<Integer, ObservableList<Message>> convMsg = new HashMap<>();
    ObservableList<Conversation> friendRequestsList = FXCollections.observableArrayList();;


    public FriendController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/friend-view.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        URL placeholder = Client.class.getResource("images/place-holder.png");


        // Default pressed
        friendListObservable.addAll(new UserDAO().getFriends());
        friendList.getStyleClass().clear();
        friendList.getStyleClass().add("PressedWrapper");


        curFriend = friendListObservable == null ? null : !friendListObservable.isEmpty() ? friendListObservable.getFirst() : null;

        chatWindowController = new ChatWindowController(curFriend, () -> {}, (msg, curConv) -> {});
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
            curConv.setConversationID(new UserDAO().getPairConversationId(item.getConversationID()));
            chatWindowController.changeCurConv(curConv);

        };


        Callback<ListView<Conversation>, ListCell<Conversation>> friendListFactory = param -> new ListCellWithButtons(
                changeToChat,
                Arrays.asList(new Pair<String, Consumer<Conversation>>(
                                "view/user/decline-svgrepo-com.png", item -> {}),
                        new Pair<String, Consumer<Conversation>>(
                                "view/user/unfriend-svgrepo-com.png", item -> {})
                )
        );
        Callback<ListView<Conversation>, ListCell<Conversation>> friendRequestsFactory = param -> new ListCellWithButtons(
                (item)->{},
                Arrays.asList(new Pair<String, Consumer<Conversation>>(
                        "view/user/accept-svgrepo-com.png", item -> {
                            new UserDAO().acceptFriendRequest(item.getConversationID(), item.getName());
                            Conversation newItem = new Conversation(item);
                            newItem.setType("PAIR");
                            ChatController.getDmList().add(newItem);
                            friendListObservable.add(newItem);
                            friendRequestsList.remove(item);
                        }),
                        new Pair<String, Consumer<Conversation>>(
                        "view/user/decline-svgrepo-com.png", item -> {})
                )
        );




        //Handlers
        friendList.setOnMouseClicked((e)->{
            friendListObservable.clear();
            friendListObservable.addAll(new UserDAO().getFriends());
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
            friendRequestsList.clear();
            friendRequestsList.addAll(new UserDAO().getFriendRequests());
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
