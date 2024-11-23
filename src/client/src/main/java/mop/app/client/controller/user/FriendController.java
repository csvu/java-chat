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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;
import mop.app.client.Client;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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
    private VBox col2;
    @FXML
    private HBox friendListAllOnl;

    private ChatWindowController chatWindowController;

    Conversation curConversation;
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

        Consumer<Conversation> changeToChat = item -> {
            curConversation = item;
            chatWindowController.changeCurConv(item);
            getChildren().remove(1);
            setConstraints(chatWindowController, 1, 0);
            getChildren().add(chatWindowController);
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
                changeToChat,
                Arrays.asList(new Pair<String, Consumer<Conversation>>(
                        "view/user/accept-svgrepo-com.png", item -> {}),
                        new Pair<String, Consumer<Conversation>>(
                        "view/user/decline-svgrepo-com.png", item -> {})
                )
        );




        //Handlers
        friendList.setOnMouseClicked((e)->{
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

        // Mock data
        for (int i = 0; i < 30; i++) {
            Random rand = new Random();
            StringBuilder str = new StringBuilder();
            char x = 20;
            for (int j = 0; j < 10; j++) {
                Random r = new Random();
                char c = (char)(r.nextInt(26) + 'a');
                str.append(c);
            }
            friendListObservable.add(new Conversation(i, "PAIR", placeholder, str.toString(), false));
            friendRequestsList.add(new Conversation(i + 30, "N/A", placeholder, str.toString(), false));
            ObservableList<Message> messages = FXCollections.observableArrayList();
            for (int j = 0; j < 30; j++) {
                messages.add(new Message(Arrays.asList("kaak", "wgewg").get(rand.nextInt(2)),  Client.class.getResource("images/place-holder.png"), LocalDateTime.of(10, 10, 10, 10, 10), "text"));
            }
            convMsg.put(i, messages);

        }


        groupChad = FXCollections.observableArrayList();
        for (int i = 0; i < 30; i++) {
            groupChad.add(new Conversation(i, "PAIR", placeholder, "hiha", false));
        }
        //Init
        friendList.getStyleClass().clear();
        friendList.getStyleClass().add("PressedWrapper");


        curConversation = friendListObservable == null ? null : friendListObservable.getFirst();

        chatWindowController = new ChatWindowController(curConversation, convMsg);

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
