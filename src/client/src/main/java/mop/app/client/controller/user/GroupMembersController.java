package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import mop.app.client.Client;
import mop.app.client.dao.user.UserDAO;
import mop.app.client.model.user.Conversation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class GroupMembersController extends VBox {
    ObservableList<Conversation> adminList = FXCollections.observableArrayList();

    @FXML
    ListView<Conversation> admin;

    ObservableList<Conversation> memberList = FXCollections.observableArrayList();

    @FXML
    ListView<Conversation> member;

    public GroupMembersController(Conversation cur, Runnable onClose) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/group-members.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        ArrayList<ArrayList<Conversation>> l = new UserDAO().getMembers(cur.getConversationID());
        adminList.addAll(l.get(0));
        memberList.addAll(l.get(1));
        admin.setCellFactory(param -> new ListCell<Conversation>() {
            @Override
            protected void updateItem(Conversation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setMouseTransparent(true);
                } else {
                    setText(item.getName());
                    setStyle("-fx-font-size: 16; -fx-text-fill: white;");
                    setMouseTransparent(false);
                }
            }
        });
        List<Pair<String, Consumer<Conversation>>> rightIcons = new ArrayList<>();

        if (adminList.stream().anyMatch(item -> item.getConversationID() == (int) Client.currentUser.getUserId())) {
            rightIcons.add(new Pair<>("view/user/remove-ellipse-svgrepo-com.png", (item) -> {
//                new UserDAO().removeMember(cur.getConversationID(), item.getConversationID());
                memberList.remove(item);
            }));
            rightIcons.add(new Pair<>("view/user/admin-1-svgrepo-com.png", (item) -> {
//                new UserDAO().removeMember(cur.getConversationID(), item.getConversationID());
                memberList.remove(item);
                adminList.add(item);
            }));
        }

        member.setCellFactory(param -> new ListCellWithButtons((item)->{}, rightIcons ));
        admin.setItems(adminList);
        member.setItems(memberList);
    }
}
