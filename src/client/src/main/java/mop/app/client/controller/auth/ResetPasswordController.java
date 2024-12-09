package mop.app.client.controller.auth;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mop.app.client.dao.AuthDAO;
import mop.app.client.util.PasswordUtil;
import mop.app.client.util.ViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetPasswordController {
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordController.class);

    public TextField emailField;
    public PasswordField currentPasswordField;
    public PasswordField newPasswordField;
    public PasswordField confirmPasswordField;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            ViewHelper.getLoginScene(event);
        } catch (IOException e) {
            logger.error("Could not navigate to the previous page: {}", e.getMessage());
            showError("Navigation Error", "Could not navigate to the previous page.");
        }
    }

    private void showInfo(String success, String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(success);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Label label = new Label(content);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);

        alert.getDialogPane().setContent(label);
        alert.getDialogPane().setPrefWidth(450);
        alert.showAndWait();
    }

    @FXML
    public void handleChangePassword() {
        String email = emailField.getText();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Error", "Please fill in all fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Error", "New password and confirm password do not match.");
            return;
        }

        if (!PasswordUtil.isStrongPassword(newPassword)) {
            showError("Error", "Password must contain at least 8 characters, 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character.");
            return;
        }

        AuthDAO authDAO = new AuthDAO();
        boolean isPasswordChanged = authDAO.changePassword(email, currentPassword, newPassword);

        if (isPasswordChanged) {
            showInfo("Success", "Password has been changed successfully.");
        } else {
            showError("Error", "Failed to change password. Please check your email and password.");
        }
    }
}
