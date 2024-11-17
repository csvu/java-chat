package mop.app.client.controller.auth;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mop.app.client.util.ViewHelper;
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
            ViewHelper.getIndexScene(event);
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
    private void handleForgotPassword(ActionEvent event) {
        try {
            ViewHelper.getForgotPasswordScene(event);
        } catch (IOException e) {
            logger.error("Could not navigate to the forgot password page.", e);
            showError("Navigation Error", "Could not navigate to the forgot password page.");
        }
    }

    @FXML
    private void handleResetPassword(ActionEvent event) {
        try {
            ViewHelper.getResetPasswordScene(event);
        } catch (IOException e) {
            logger.error("Could not navigate to the reset password page.", e);
            showError("Navigation Error", "Could not navigate to the reset password page.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            ViewHelper.getRegisterScene(event);
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
