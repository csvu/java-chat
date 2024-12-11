package mop.app.client.controller.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import mop.app.client.Client;
import mop.app.client.dao.AuthDAO;
import mop.app.client.dto.Request;
import mop.app.client.dto.RequestType;
import mop.app.client.dto.Response;
import mop.app.client.dto.UserDTO;
import mop.app.client.network.SocketClient;
import mop.app.client.util.AlertDialog;
import mop.app.client.util.EmailSender;
import mop.app.client.util.ObjectMapperConfig;
import mop.app.client.util.ViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgotPasswordController {
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    @FXML
    private TextField emailField;

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
    public void handleSendEmail() {
        String email = emailField.getText();

        if (email.isEmpty()) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Please enter your email address.",
                ""
            );
        } else {
            AuthDAO authDAO = new AuthDAO();

            UserDTO user = authDAO.getUserByEmail(email);

            if (user == null) {
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Could not send email. Please check your email address",
                    ""
                );
                return;
            }

            authDAO.sendResetPasswordEmail(email);
        }
    }
}
