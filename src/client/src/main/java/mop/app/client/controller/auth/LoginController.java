package mop.app.client.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Timestamp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mop.app.client.Client;
import mop.app.client.dao.AuthDAO;
import mop.app.client.dao.LoginTimeDAO;
import mop.app.client.dao.OpenTimeDAO;
import mop.app.client.dao.RoleDAO;
import mop.app.client.dto.Request;
import mop.app.client.dto.RequestType;
import mop.app.client.dto.Response;
import mop.app.client.dto.UserDTO;
import mop.app.client.network.SocketClient;
import mop.app.client.util.ObjectMapperConfig;
import mop.app.client.util.PreProcess;
import mop.app.client.util.ViewFactory;
import mop.app.client.util.ViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final AuthDAO authDAO;

    public LoginController() {
        authDAO = new AuthDAO();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            ViewHelper.getIndexScene(event);
        } catch (IOException e) {
            logger.error("Could not navigate to the previous page: {}", e.getMessage());
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

        logger.info("Attempting to login with email: {}", email);
        logger.info("Attempting to login with password: {}", password);

        SocketClient socketClient = Client.socketClient;

        if (!socketClient.isConnectionValid()) {
            showError("Connection Error", "Could not connect to the server.");
            return;
        }

        UserDTO loginData = UserDTO.builder()
            .email(email)
            .password(password)
            .build();

        ObjectMapper mapper = ObjectMapperConfig.getObjectMapper();

        Request loginRequest = new Request(RequestType.LOGIN, loginData);
        String jsonLoginRequest = mapper.writeValueAsString(loginRequest);
        String jsonLoginResponse = socketClient.sendRequest(jsonLoginRequest);
        Response loginResponse = mapper.readValue(jsonLoginResponse, Response.class);

        if (loginResponse.isSuccess()) {
            UserDTO user = mapper.convertValue(loginResponse.getData(), UserDTO.class);

            // Check if user is deleted
            if (user.getDisplayName().equals("Deleted User")) {
                showError("Account Deleted", "Your account has been deleted.");
                return;
            }

            // Check if user is banned
            if (user.getIsBanned()) {
                showError("Account Banned", "Your account has been banned.");
                return;
            }

            RoleDAO role = new RoleDAO();
            String roleName = role.getRoleByUserId(user.getUserId());

            if (roleName == null) {
                showError("Role Error", "Could not retrieve user role.");
                return;
            }

            if ("ADMIN".equals(roleName)) {
                // Admin role: Navigate to the admin view
                authDAO.updateUserStatus(user.getUserId(), true);
                Client.currentUser = user;
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();
                ViewFactory viewFactory = new ViewFactory();
                viewFactory.getAdminView();
            } else {
                // Regular user role: Navigate to the home view
                authDAO.updateUserStatus(user.getUserId(), true);
                Client.currentUser = user;
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/home-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                stage.setTitle("MOP Application");
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
            }
            LoginTimeDAO loginTimeDAO = new LoginTimeDAO();
            boolean isLoginTime = loginTimeDAO.addLoginTime(user.getUserId());
            if (!isLoginTime) {
                logger.error("Failed to add login time for user: {}", user.getUserId());
            }

            OpenTimeDAO openTimeDAO = new OpenTimeDAO();
            boolean isOpenTime = openTimeDAO.addOpenTime(user.getUserId());
            if (!isOpenTime) {
                logger.error("Failed to add open time for user: {}", user.getUserId());
            }

            boolean isSave = PreProcess.saveUserInformation(user);
            if (!isSave) {
                logger.error("Failed to save user information");
            }
            Client.registerActivity();
        } else {
            showError("Login Failed", loginResponse.getMessage());
        }
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        try {
            ViewHelper.getForgotPasswordScene(event);
        } catch (IOException e) {
            logger.error("Could not navigate to the forgot password page: {}", e.getMessage());
            showError("Navigation Error", "Could not navigate to the forgot password page.");
        }
    }

    @FXML
    private void handleResetPassword(ActionEvent event) {
        try {
            ViewHelper.getResetPasswordScene(event);
        } catch (IOException e) {
            logger.error("Could not navigate to the reset password page: {}", e.getMessage());
            showError("Navigation Error", "Could not navigate to the reset password page.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            ViewHelper.getRegisterScene(event);
        } catch (IOException e) {
            logger.error("Could not navigate to the register page: {}", e.getMessage());
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