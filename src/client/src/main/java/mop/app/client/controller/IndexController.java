package mop.app.client.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import mop.app.client.util.AlertDialog;
import mop.app.client.util.ViewHelper;
import org.slf4j.Logger;

public class IndexController {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IndexController.class);

    @FXML
    private void handleGoogleLogin() {
        AlertDialog.showAlertDialog(
            Alert.AlertType.INFORMATION,
            "Google login clicked",
            "",
            ""
        );
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            ViewHelper.getLoginScene(event);
        } catch (IOException e) {
            logger.error("Could not load login page: {}", e.getMessage());
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Navigation Error",
                "Could not load email login page.",
                e.getMessage()
            );
        }
    }
}
