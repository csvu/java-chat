package mop.app.client.util;

import java.io.IOException;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import mop.app.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
public class ViewHelper {
    private static final Logger logger = LoggerFactory.getLogger(ViewHelper.class);

    private static Parent parent;

    public static <T> T getView(String path) throws IOException {
        return new FXMLLoader(Client.class.getResource(path)).load();
    }

    public static void getStage(FXMLLoader loader, boolean resizable) {
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            logger.error("Failed to load scene: {}", e.getMessage());
        }
        Stage stage = new Stage();

        try {
            stage.getIcons().add(new Image(
                Objects.requireNonNull(Client.class.getResourceAsStream("images/app-icon.png"))));
        } catch (Exception e) {
            logger.error("Failed to load application icon: {}", e.getMessage());
        }

        stage.setTitle("MOP Application");
        stage.setResizable(resizable);
        stage.setScene(scene);
        stage.show();
    }

    // For ActionEvent Button
    public static void changeScreen(ActionEvent event, String path, boolean resizable) throws IOException {
        FXMLLoader loader = new FXMLLoader(Client.class.getResource(path));
        parent = loader.load();

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(parent);
        currentStage.setScene(scene);
        currentStage.setResizable(resizable);
        currentStage.show();
    }

    public static void getIndexScene(ActionEvent event) throws IOException {
        changeScreen(event, ViewPath.INDEX.getPath(), false);
    }

    public static void getLoginScene(ActionEvent event) throws IOException {
        changeScreen(event, ViewPath.LOGIN.getPath(), false);
    }

    public static void getForgotPasswordScene(ActionEvent event) throws IOException {
        changeScreen(event, ViewPath.FORGOT_PASSWORD.getPath(), false);
    }

    public static void getResetPasswordScene(ActionEvent event) throws IOException {
        changeScreen(event, ViewPath.RESET_PASSWORD.getPath(), false);
    }

    public static void getRegisterScene(ActionEvent event) throws IOException {
        changeScreen(event, ViewPath.REGISTER.getPath(), false);
    }
}
