package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import mop.app.client.Client;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


public class ChatController extends GridPane {
    private final ChatWindowController chatWindowController;
    @FXML
    private ListView<Conversation> listViewCol2;


    URL placeholder = Client.class.getResource("images/place-holder.png");

    Conversation curConversation;
    ObservableList<Conversation> dmList;
    HashMap<Integer, ObservableList<Message>> convMsg = new HashMap<>();


    public ChatController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/chat-view.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        // Mock data
        dmList = FXCollections.observableArrayList();
        for (int i = 0; i < 30; i++) {
            Random rand = new Random();
            StringBuilder str = new StringBuilder();
            char x = 20;
            for (int j = 0; j < 10; j++) {
                Random r = new Random();
                char c = (char)(r.nextInt(26) + 'a');
                str.append(c);
            }
            dmList.add(new Conversation(i, "PAIR", placeholder, str.toString(), false));
            ObservableList<Message> messages = FXCollections.observableArrayList();
            for (int j = 0; j < 30; j++) {
                messages.add(new Message(Arrays.asList("You", "wgewg").get(rand.nextInt(2)),  Client.class.getResource("images/place-holder.png"), LocalDateTime.of(2024, 10, 10, 10, 10), "text"));
            }
            convMsg.put(i, messages);

        }

        //Init

        curConversation = dmList == null ? null : dmList.getFirst();
        chatWindowController = new ChatWindowController(curConversation, convMsg);
        setConstraints(chatWindowController, 1, 0);
        getChildren().add(chatWindowController);

        // Scroll
        listViewCol2.setCellFactory(param -> {
            ListCell<Conversation> listCell = new ListCell<>() {
                @Override
                protected void updateItem(Conversation item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        ObservableList<Message> lastMsg = convMsg.get(item.getConversationID());
                        setGraphic(new IconLabel(placeholder, item.getName(), null, lastMsg == null || lastMsg.isEmpty() ? "No message yet" : lastMsg.get(0).getContent()));
                    }
                }
            };

            return listCell;
        });

        listViewCol2.setOnMouseClicked(mouseEvent -> chatWindowController.changeCurConv(listViewCol2.getSelectionModel().getSelectedItem()));



        // start cell factory
        listViewCol2.setItems(dmList);
    }




}
