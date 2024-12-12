package mop.app.client.controller.admin;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import mop.app.client.dao.AuthDAO;
import mop.app.client.dao.RoleDAO;
import mop.app.client.dto.UserDTO;
import mop.app.client.util.AlertDialog;
import mop.app.client.util.PasswordUtil;
import mop.app.client.util.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserController {
    private static final Logger logger = LoggerFactory.getLogger(CreateUserController.class);

    @FXML
    private TextField usernameField;
    @FXML
    private TextField displayNameField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private DatePicker birthdayPicker;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private TextArea addressArea;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    private final AuthDAO authDAO;
    private final RoleDAO roleDAO;

    public CreateUserController() {
        this.authDAO = new AuthDAO();
        this.roleDAO = new RoleDAO();
    }

    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        roleComboBox.getItems().addAll("Admin", "User");

        birthdayPicker.setConverter(new StringConverter<>() {
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
                            "For example: 05/16/2004");

                        birthdayPicker.setValue(null);
                    });

                    return null;
                }
            }
        });

        // Add additional validation when focus is lost
        birthdayPicker.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateDateOfBirth();
            }
        });
    }

    private void validateDateOfBirth() {
        LocalDate selectedDate = birthdayPicker.getValue();

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
                birthdayPicker.setValue(null);
                return;
            }

            if (selectedDate.isAfter(currentDate)) {
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Invalid Birth Date",
                    "Birth date cannot be in the future.",
                    "Please enter a valid birth date."
                );
                birthdayPicker.setValue(null);
            }
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        ViewModel.getInstance().getViewFactory().getSelectedView().set("User");
    }

    @FXML
    public void handleCreate(ActionEvent event) {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String displayName = displayNameField.getText();
        String gender = genderComboBox.getValue();
        String address = addressArea.getText();
        String role = roleComboBox.getValue();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            displayName.isEmpty() || address.isEmpty() || gender == null || role == null) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Validation Error",
                "Please fill in all fields.",
                ""
            );
            return;
        }

        if (!password.equals(confirmPassword)) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Validation Error",
                "Passwords do not match.",
                ""
            );
            return;
        }

        LocalDate birthDate = birthdayPicker.getValue();
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

        if (displayName.equals("Deleted User")) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Validation Error",
                "Display name cannot be 'Deleted User'.",
                ""
            );
            return;
        }

        long roleId = roleDAO.getRoleIdByRoleName(role.toUpperCase());

        UserDTO user = UserDTO.builder()
            .email(email)
            .username(username)
            .password(PasswordUtil.hash(password))
            .gender(gender)
            .displayName(displayName)
            .birthDate(sqlDate)
            .address(address)
            .roleID(roleId)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .isActive(false)
            .isBanned(false)
            .build();

        Task<UserDTO> createUserTask = new Task<>() {
            @Override
            protected UserDTO call() throws Exception {
                return authDAO.register(user);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                UserDTO registeredUser = getValue();
                if (registeredUser != null) {
                    Platform.runLater(() -> {
                        AlertDialog.showAlertDialog(
                            Alert.AlertType.INFORMATION,
                            "Registration Successful",
                            "You have successfully registered.",
                            "You can now login with your email and password."
                        );
                        emailField.clear();
                        usernameField.clear();
                        passwordField.clear();
                        confirmPasswordField.clear();
                        genderComboBox.setValue(null);
                        genderComboBox.setPromptText("Select your gender");
                        displayNameField.clear();
                        birthdayPicker.setValue(null);
                        birthdayPicker.setPromptText("Select your date of birth (MM/dd/yyyy)");
                        roleComboBox.setValue(null);
                        roleComboBox.setPromptText("Select your role");
                        addressArea.clear();
                    });
                } else {
                    Platform.runLater(() -> {
                        AlertDialog.showAlertDialog(
                            Alert.AlertType.ERROR,
                            "Registration Error",
                            "An error occurred during registration. Please try again.",
                            ""
                        );
                    });
                }
            }

            @Override
            protected void failed() {
                super.failed();
                Platform.runLater(() -> {
                    AlertDialog.showAlertDialog(
                        Alert.AlertType.ERROR,
                        "Registration Error",
                        "An error occurred during registration. Please try again.",
                        ""
                    );
                });
            }
        };

        new Thread(createUserTask).start();
    }
}
