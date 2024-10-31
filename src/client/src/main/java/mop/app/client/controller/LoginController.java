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

public class LoginController {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(Client.class.getResource("view/index.fxml"));
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

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Validation Error", "Please fill in all fields.");
            return;
        }

        showInfo("Login Attempt", "Attempting to login with email: " + email);
    }

    @FXML
    private void handleForgotPassword() {
        try {
            Stage currentStage = (Stage) emailField.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(Client.class.getResource("view/forgot-password.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            currentStage.setResizable(true);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            logger.error("Could not navigate to the forgot password page.", e);
            showError("Navigation Error", "Could not navigate to the forgot password page.");
        }
    }

    @FXML
    private void handleResetPassword() {
        try {
            Stage currentStage = (Stage) emailField.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(Client.class.getResource("view/reset-password.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            currentStage.setResizable(true);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            logger.error("Could not navigate to the reset password page.", e);
            showError("Navigation Error", "Could not navigate to the reset password page.");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            Stage currentStage = (Stage) emailField.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(Client.class.getResource("view/register.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            currentStage.setResizable(true);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            logger.error("Could not navigate to the register page.", e);
            showError("Navigation Error", "Could not navigate to the register page.");
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
