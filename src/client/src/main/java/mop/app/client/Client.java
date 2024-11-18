package mop.app.client;

import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import mop.app.client.util.ViewFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Starting JavaFX Application");
//        ViewFactory viewFactory = new ViewFactory();
//        viewFactory.getAdminView();
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/index.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        try {
            stage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("images/app-icon.png"))));
        } catch (Exception e) {
            logger.error("Failed to load application icon", e);
        }

        stage.setTitle("MOP Application");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {

    }
}