package mop.app.client.controller.auth;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mop.app.client.util.ViewHelper;
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
            ViewHelper.getLoginScene(event);
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
