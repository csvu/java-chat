package mop.app.client.controller.auth;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import mop.app.client.util.ViewHelper;
import org.slf4j.Logger;

public class RegisterController {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RegisterController.class);

    public TextField emailField;
    public TextField usernameField;
    public PasswordField passwordField;
    public TextField fullNameField;
    public DatePicker dateOfBirthPicker;
    public TextArea addressArea;
    public Button registerButton;
    public ComboBox<String> genderComboBox;

    @FXML
    private void initialize() {
        // CSS styling for the genderComboBox after choosing a value
        genderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue != null) {
                    logger.info("New value: " + newValue);
                    Node textNode = genderComboBox.lookup(".text");
                    if (textNode != null) {
                        textNode.setStyle("-fx-fill: white;");
                    }
                } else {
                    Node textNode = genderComboBox.lookup(".text");
                    if (textNode != null) {
                        textNode.setStyle("-fx-fill: #808080;");
                    }
                }
            });
        });

        Node textNode = genderComboBox.lookup(".text");
        if (textNode != null) {
            textNode.setStyle("-fx-fill: #808080;");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            ViewHelper.getLoginScene(event);
        } catch (IOException e) {
            logger.error("Could not load login page", e);
            showError("Navigation Error", "Could not load email login page.");
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Action");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
