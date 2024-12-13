package mop.app.client.controller.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mop.app.client.Client;
import mop.app.client.util.PreProcess;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Objects;


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
    @FXML
    private HBox logOut;

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
            chatController.update();
            col2.getChildren().add(chatController);
        });
        logOut.setOnMouseClicked(e->{
            PreProcess.deleteUserInformation();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.close();
            Client.currentUser = null;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/index.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        dmNav.setOnMouseClicked(e -> {
            Stage stage = new Stage();

            try {
                Scene scene = new Scene(new EditProfileControl());
                stage.setScene(scene);
                stage.setResizable(false);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node)e.getSource()).getScene().getWindow() );
                try {
                    stage.getIcons().add(new Image(
                            Objects.requireNonNull(Client.class.getResourceAsStream("images/app-icon.png"))));
                } catch (Exception ex) {
                    logger.error("Failed to load application icon", ex);
                }

                stage.setTitle("MOP Application");
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
