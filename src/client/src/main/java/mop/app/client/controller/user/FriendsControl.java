package mop.app.client.controller.user;

import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import mop.app.client.Client;

import java.io.IOException;

public class FriendsControl extends HBox {
    HBox topBarCol3;

    public FriendsControl(HBox topBarCol3) throws IOException {
        this.topBarCol3 = topBarCol3;
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/friends.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
    }
    public void onDestroy(MouseEvent mouseEvent) {
        topBarCol3.getChildren().clear();
    }
}
