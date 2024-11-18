package mop.app.client.controller.auth;

import java.io.IOException;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mop.app.client.Client;
import mop.app.client.util.ViewFactory;
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
    private void handleLogin(ActionEvent event) throws IOException {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Validation Error", "Please fill in all fields.");
            return;
        }

        if (email.equals("admin") && password.equals("admin")) {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            currentStage.close();

            ViewFactory viewFactory = new ViewFactory();
            viewFactory.getAdminView();
        } else {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            currentStage.close();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/home-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            try {
                stage.getIcons().add(new Image(
                    Objects.requireNonNull(Client.class.getResourceAsStream("images/app-icon.png"))));
            } catch (Exception e) {
                logger.error("Failed to load application icon", e);
            }

            stage.setTitle("MOP Application");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
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
