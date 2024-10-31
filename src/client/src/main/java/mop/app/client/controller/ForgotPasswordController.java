package mop.app.client.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mop.app.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgotPasswordController {
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    @FXML
    private TextField emailField;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(Client.class.getResource("view/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            currentStage.setResizable(false);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            logger.error("Could not navigate to the previous page.", e);
            showError("Navigation Error", "Could not navigate to the previous page.");
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleSendEmail() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Email Sent");
        alert.setHeaderText(null);
        alert.setContentText("An email has been sent to the provided email address.");
        alert.showAndWait();
    }
}
