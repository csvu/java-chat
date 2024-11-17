package mop.app.client.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mop.app.client.Client;
import mop.app.client.controller.IndexController;
import org.slf4j.Logger;

import java.io.IOException;


public class HomeController {
    private FriendController friendController;
    private ChatController chatController;
    @FXML
    private HBox friendsHBox;
    @FXML
    private HBox chatHBox;
    @FXML
    private ImageView dmNav;
    @FXML
    private VBox col2;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(HomeController.class);

    public HomeController() throws IOException {
    }

    @FXML
    public void initialize() throws IOException {
        chatController = new ChatController();
        friendController = new FriendController();

        //Event handling
        friendsHBox.setOnMouseClicked((e) -> {
            chatHBox.getStyleClass().clear();
            chatHBox.getStyleClass().add("HoverWrapper");
            friendsHBox.getStyleClass().clear();
            friendsHBox.getStyleClass().add("PressedWrapper");

            col2.getChildren().clear();
            col2.getChildren().add(friendController);
        });
        chatHBox.setOnMouseClicked((e) -> {
            friendsHBox.getStyleClass().clear();
            friendsHBox.getStyleClass().add("HoverWrapper");
            chatHBox.getStyleClass().clear();
            chatHBox.getStyleClass().add("PressedWrapper");

            col2.getChildren().clear();
            col2.getChildren().add(chatController);
        });
        dmNav.setOnMouseClicked(e -> {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/edit-profile.fxml"));

            try {
                Scene scene = new Scene(fxmlLoader.load());
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });


        //Clip
        double radius = dmNav.getFitHeight() / 2;
        Circle clip = new Circle(dmNav.getX() + radius, dmNav.getY() + radius, radius);
        dmNav.setClip(clip);
        // Set components
        col2.getChildren().add(chatController);
        VBox.setVgrow(chatController, Priority.ALWAYS);
        chatHBox.getStyleClass().clear();
        chatHBox.getStyleClass().add("PressedWrapper");

    }




}
