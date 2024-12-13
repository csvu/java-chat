package mop.app.client.controller.auth;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mop.app.client.dao.AuthDAO;
import mop.app.client.util.AlertDialog;
import mop.app.client.util.PasswordUtil;
import mop.app.client.util.ViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetPasswordController {
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordController.class);

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            ViewHelper.getLoginScene(event);
        } catch (IOException e) {
            logger.error("Could not navigate to the previous page: {}", e.getMessage());
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Navigation Error",
                "Could not navigate to the previous page.",
                e.getMessage()
            );
        }
    }

    @FXML
    public void handleChangePassword() {
        String email = emailField.getText();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Please fill in all fields.",
                ""
            );
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Error",
                "New password and confirm password do not match.",
                ""
            );
            return;
        }

        if (!PasswordUtil.isStrongPassword(newPassword)) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Invalid password",
                """
                    Password must be at least 8 characters long and contain:
                    - At least one uppercase letter
                    - At least one lowercase letter
                    - At least one number
                    - At least one special character (@$!%*?&_-)
                    """
            );
            return;
        }

        AuthDAO authDAO = new AuthDAO();
        boolean isPasswordChanged = authDAO.changePassword(email, currentPassword, newPassword);

        if (isPasswordChanged) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.INFORMATION,
                "Success",
                "Password has been changed successfully.",
                ""
            );
        } else {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Failed to change password.",
                "Please check your email and password."
            );
        }
    }
}
