package mop.app.client.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import mop.app.client.util.ViewHelper;
import org.slf4j.Logger;

public class IndexController {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IndexController.class);

    @FXML
    private void handleGoogleLogin() {
        showInfo("Google login clicked");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            ViewHelper.getLoginScene(event);
        } catch (IOException e) {
            logger.error("Could not load login page: {}", e.getMessage());
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
