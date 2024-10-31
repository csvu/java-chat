package mop.app.client.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mop.app.client.Client;
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

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Client.class.getResource("view/login.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setResizable(false);
            currentStage.show();
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
