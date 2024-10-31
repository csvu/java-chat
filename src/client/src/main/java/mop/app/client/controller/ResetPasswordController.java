package mop.app.client.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mop.app.client.Client;
import org.slf4j.Logger;

public class ResetPasswordController {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ResetPasswordController.class);

    public TextField emailField;
    public PasswordField currentPasswordField;
    public PasswordField newPasswordField;

    public PasswordField confirmPasswordField;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

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
    public void handleChangePassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password Changed");
        alert.setHeaderText(null);
        alert.setContentText("Password has been changed successfully.");
        alert.showAndWait();
    }
}
