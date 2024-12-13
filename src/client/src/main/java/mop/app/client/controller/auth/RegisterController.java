package mop.app.client.controller.auth;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import mop.app.client.dao.AuthDAO;
import mop.app.client.dao.RoleDAO;
import mop.app.client.dto.UserDTO;
import mop.app.client.util.AlertDialog;
import mop.app.client.util.PasswordUtil;
import mop.app.client.util.ViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField fullNameField;
    @FXML
    private DatePicker dateOfBirthPicker;
    @FXML
    private TextArea addressArea;
    @FXML
    private Button registerButton;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private CheckBox showPassword;
    private final AuthDAO authDAO = new AuthDAO();

    @FXML
    private void initialize() {
        showPassword.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                passwordField.setPromptText(passwordField.getText());
                passwordField.setText("");
                passwordField.setDisable(true);
            } else {
                passwordField.setText(passwordField.getPromptText());
                passwordField.setPromptText("");
                if (passwordField.getText().isEmpty()) {
                    passwordField.setPromptText("Choose a password");
                }
                passwordField.setStyle("-fx-background-color: #3a3a3a; -fx-text-fill: white;");
                passwordField.setDisable(false);
            }
        });

        // CSS styling for the genderComboBox after choosing a value
        genderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue != null) {
                    logger.info("New value: " + newValue);
                    Node textNode = genderComboBox.lookup(".text");
                    if (textNode != null) {
                        textNode.setStyle("-fx-fill: white;");
                    }
                } else {
                    Node textNode = genderComboBox.lookup(".text");
                    if (textNode != null) {
                        textNode.setStyle("-fx-fill: #808080;");
                    }
                }
            });
        });

        Node textNode = genderComboBox.lookup(".text");
        if (textNode != null) {
            textNode.setStyle("-fx-fill: #808080;");
        }

        dateOfBirthPicker.setConverter(new StringConverter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }

                try {
                    return LocalDate.parse(string, formatter);
                } catch (DateTimeParseException e) {
                    Platform.runLater(() -> {
                        AlertDialog.showAlertDialog(
                            Alert.AlertType.ERROR,
                            "Invalid Date Format",
                            "Please enter the date in MM/dd/yyyy format exactly.",
                            "For example: 05/16/2004"
                        );

                        // Clear the invalid input
                        dateOfBirthPicker.setValue(null);
                    });

                    return null;
                }
            }
        });

        // Add additional validation when focus is lost
        dateOfBirthPicker.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateDateOfBirth();
            }
        });
    }

    private void validateDateOfBirth() {
        LocalDate selectedDate = dateOfBirthPicker.getValue();

        if (selectedDate != null) {
            LocalDate currentDate = LocalDate.now();

            // Validate age (at least 13 years old)
            if (selectedDate.isAfter(currentDate.minusYears(13))) {
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Invalid Birth Date",
                    "You must be at least 13 years old to register.",
                    "Please enter a valid birth date."
                );
                dateOfBirthPicker.setValue(null);
                return;
            }

            // Ensure date is not in the future
            if (selectedDate.isAfter(currentDate)) {
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Invalid Date",
                    "Birth date cannot be in the future.",
                    "Please enter a valid date."
                );
                dateOfBirthPicker.setValue(null);
            }
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (showPassword.isSelected()) {
            password = passwordField.getPromptText();
        }
        String fullName = fullNameField.getText();
        String gender = genderComboBox.getValue();
        String address = addressArea.getText();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || fullName.isEmpty() || address.isEmpty() ||
            gender == null) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Validation Error",
                "Please fill in all fields.",
                ""
            );
            return;
        }

        LocalDate birthDate = dateOfBirthPicker.getValue();
        if (birthDate == null) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Validation Error",
                "Please enter a valid birth date.",
                ""
            );
            return;
        }
        Date sqlDate = Date.valueOf(birthDate);

        // Check if the email is unique
        if (!authDAO.isEmailExists(email)) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Validation Error",
                "Email is already taken.",
                ""
            );
            return;
        }

        // Check if the username is unique
        if (!authDAO.isUsernameExists(username)) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Validation Error",
                "Username is already taken.",
                ""
            );
            return;
        }

        // Check password strength
        if (!PasswordUtil.isStrongPassword(password)) {
            logger.info("Password is not strong enough: " + password);
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

        if (fullName.equals("Deleted User")) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Validation Error",
                "Display name cannot be 'Deleted User'.",
                ""
            );
            return;
        }

        RoleDAO roleDAO = new RoleDAO();
        long roleId = roleDAO.getRoleIdByRoleName("USER");

        UserDTO user = UserDTO.builder()
            .email(email)
            .username(username)
            .password(PasswordUtil.hash(password))
            .gender(gender)
            .displayName(fullName)
            .birthDate(sqlDate)
            .address(address)
            .roleID(roleId)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .isActive(false)
            .isBanned(false)
            .build();

        UserDTO registeredUser = authDAO.register(user);

        if (registeredUser != null) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.INFORMATION,
                "Registration Successful",
                "You have successfully registered.",
                "You can now login with your email and password."
            );

            // Clear all fields
            emailField.clear();
            usernameField.clear();
            passwordField.clear();
            genderComboBox.setValue(null);
            genderComboBox.setPromptText("Select your gender");
            fullNameField.clear();
            dateOfBirthPicker.setValue(null);
            dateOfBirthPicker.setPromptText("Select your date of birth (MM/dd/yyyy)");
            addressArea.clear();
        } else {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Registration Error",
                "An error occurred during registration. Please try again.",
                ""
            );
        }
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

