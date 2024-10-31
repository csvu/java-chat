package mop.app.client;

import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static Stage instance;

    private static final class StageHolder {
        private static final Stage INSTANCE = new Stage();
    }

    public static Stage getInstance() {
        return StageHolder.INSTANCE;
    }

    @Override
    public void start(Stage stage) throws IOException {
        instance = stage;
        logger.info("Starting JavaFX Application");
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/index.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        try {
            instance.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("images/app-icon.png"))));
        } catch (Exception e) {
            logger.error("Failed to load application icon", e);
        }

        instance.setTitle("MOP Application");
        instance.setResizable(false);
        instance.setScene(scene);
        instance.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {

    }
}