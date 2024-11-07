package mop.app.client.controller.user;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import mop.app.client.Client;
import mop.app.client.model.user.Conversation;

import java.io.IOException;


public class HomeController {
    @FXML
    private ImageView dmNav;
    @FXML
    private HBox topBarCol3;
    @FXML
    private TextArea chatArea;
    private final Text sizeHelper = new Text();;
    private double oldHeight = 0;

    @FXML
    private ListView<Conversation> listViewCol1;
    @FXML
    private ListView<Conversation> listViewCol2;

    ObservableList<Conversation> dmList;
    ObservableList<Conversation> groupChad;

    @FXML
    public void initialize() {
        double radius = dmNav.getFitHeight() / 2;
        Circle clip = new Circle(dmNav.getX() + radius, dmNav.getY() + radius, radius);
        dmNav.setClip(clip);

        dmList = FXCollections.observableArrayList();
        for (int i = 0; i < 30; i++) {
            dmList.add(new Conversation(i, "PAIR", Client.class.getResource("images/place-holder.png"), "hiha", false));
        }

        groupChad = FXCollections.observableArrayList();
        for (int i = 0; i < 30; i++) {
            groupChad.add(new Conversation(i, "PAIR", Client.class.getResource("images/place-holder.png"), "hiha", false));
        }

        listViewCol1.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Conversation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setAlignment(Pos.CENTER);
                    setGraphic(new CircleImage(new Image(item.getIcon().toString())));
                }
            }
        });


        listViewCol2.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Conversation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(new CircleImage(new Image(item.getIcon().toString())));
                }
            }
        });

        listViewCol1.setItems(groupChad);
        listViewCol2.setItems(dmList);
        chatArea.setPrefSize(200, 40);

        sizeHelper.textProperty().bind(chatArea.textProperty());
        sizeHelper.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (oldHeight != newValue.getHeight()) {
                oldHeight = newValue.getHeight();
                chatArea.setPrefHeight(sizeHelper.getLayoutBounds().getHeight());
                HBox par = (HBox) chatArea.getParent();
                par.setPrefHeight(chatArea.getPrefHeight() + par.getPadding().getBottom() + par.getPadding().getTop());
            }
        });

    }


    @FXML
    private void onTopBarCol3Clicked() throws IOException {
        topBarCol3.getChildren().clear();
        topBarCol3.getChildren().add(new FriendsControl(topBarCol3));
    }

}
