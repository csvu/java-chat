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

import java.io.IOException;
import java.net.URL;

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
    private ListView<URL> listViewCol1;
    @FXML
    private ListView<URL> listViewCol2;

    ObservableList<URL> friendList;
    ObservableList<URL> groupChad;

    @FXML
    public void initialize() {
        double radius = dmNav.getFitHeight() / 2;
        Circle clip = new Circle(dmNav.getX() + radius, dmNav.getY() + radius, radius);
        dmNav.setClip(clip);

        friendList = FXCollections.observableArrayList();
        for (int i = 0; i < 30; i++) {
            friendList.add(Client.class.getResource("images/place-holder.png"));
        }

        groupChad = FXCollections.observableArrayList();
        for (int i = 0; i < 30; i++) {
            groupChad.add(Client.class.getResource("images/place-holder.png"));
        }

        listViewCol1.setCellFactory(param -> new ListCell<URL>() {
            @Override
            protected void updateItem(URL item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setAlignment(Pos.CENTER);
                    setGraphic(new CircleImage(new Image(item.toString())));
                }
            }
        });


        listViewCol2.setCellFactory(param -> new ListCell<URL>() {
            @Override
            protected void updateItem(URL item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(new CircleImage(new Image(item.toString())));
                }
            }
        });

        listViewCol1.setItems(groupChad);
        listViewCol2.setItems(friendList);
        chatArea.setPrefSize(200, 40);

        sizeHelper.textProperty().bind(chatArea.textProperty());
        sizeHelper.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                if (oldHeight != newValue.getHeight()) {
                    oldHeight = newValue.getHeight();
                    chatArea.setPrefHeight(sizeHelper.getLayoutBounds().getHeight());
                    HBox par = (HBox) chatArea.getParent();
                    par.setPrefHeight(chatArea.getPrefHeight() + par.getPadding().getBottom() + par.getPadding().getTop());
                }
            }
        });

    }


    @FXML
    private void onTopBarCol3Clicked() throws IOException {
        topBarCol3.getChildren().clear();
        topBarCol3.getChildren().add(new FriendsControl(topBarCol3));
    }

}
