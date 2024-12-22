package mop.app.client.controller.user;

import java.sql.Date;
import java.time.LocalDate;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import mop.app.client.Client;
import java.io.IOException;
import mop.app.client.dao.AuthDAO;
import mop.app.client.dao.UserManagementDAO;
import mop.app.client.dto.UserDTO;
import mop.app.client.util.AlertDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditProfileControl extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(EditProfileControl.class);

    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField fullNameField;
    @FXML
    private DatePicker dateOfBirthPicker;
    @FXML
    private TextArea addressArea;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private Button saveChange;

    private final AuthDAO authDAO;
    private final UserManagementDAO userManagementDAO;

    public EditProfileControl() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/edit-profile.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        authDAO = new AuthDAO();
        userManagementDAO = new UserManagementDAO();

        initializeFields();
        setupSaveButton();
    }

    private void initializeFields() {
        emailField.setText(Client.currentUser.getEmail());
        usernameField.setText(Client.currentUser.getUsername());
        fullNameField.setText(Client.currentUser.getDisplayName());
        dateOfBirthPicker.setValue(Client.currentUser.getBirthDate().toLocalDate());
        addressArea.setText(Client.currentUser.getAddress());
        genderComboBox.setValue(Client.currentUser.getGender());

        dateOfBirthPicker.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateDateOfBirth();
            }
        });
    }

    private void setupSaveButton() {
        saveChange.setOnAction(event -> handleSave());
    }

    private void validateDateOfBirth() {
        LocalDate selectedDate = dateOfBirthPicker.getValue();

        if (selectedDate != null) {
            LocalDate currentDate = LocalDate.now();

            if (selectedDate.isAfter(currentDate.minusYears(13))) {
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Invalid Birth Date",
                    "You must be at least 13 years old to register.",
                    ""
                );
                dateOfBirthPicker.setValue(Client.currentUser.getBirthDate().toLocalDate());
                return;
            }

            if (selectedDate.isAfter(currentDate)) {
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Invalid Birth Date",
                    "Birth date cannot be in the future.",
                    ""
                );
                dateOfBirthPicker.setValue(Client.currentUser.getBirthDate().toLocalDate());
            }
        }
    }

    @FXML
    private void handleSave() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();

        Task<Boolean> validationTask = new Task<>() {
            @Override
            protected Boolean call() {
                boolean isUsernameUnique = true;
                boolean isEmailUnique = true;

                if (!username.equals(Client.currentUser.getUsername())) {
                    isUsernameUnique = authDAO.isUsernameExists(username);
                }

                if (!email.equals(Client.currentUser.getEmail())) {
                    isEmailUnique = authDAO.isEmailExists(email);
                }

                return isUsernameUnique && isEmailUnique;
            }
        };

        validationTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                if (!validationTask.getValue()) {
                    AlertDialog.showAlertDialog(
                        Alert.AlertType.ERROR,
                        "Error",
                        "Username or email already exists",
                        ""
                    );
                    return;
                }

                if (!validateInput()) {
                    return;
                }

                updateUserProfile();
            });
        });

        validationTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Failed to validate user details",
                    ""
                );
                logger.error("Failed to validate user details", validationTask.getException());
            });
        });

        new Thread(validationTask).start();
    }

    private boolean validateInput() {
        if (usernameField.getText().trim().isEmpty()) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Username cannot be empty",
                ""
            );
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Email cannot be empty",
                ""
            );
            return false;
        }

        if (dateOfBirthPicker.getValue() == null) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Date of birth cannot be empty",
                ""
            );
            return false;
        }

        return true;
    }

    private void updateUserProfile() {
        UserDTO updatedUser = new UserDTO();
        updatedUser.setUserId(Client.currentUser.getUserId());
        updatedUser.setUsername(usernameField.getText().trim());
        updatedUser.setDisplayName(fullNameField.getText().trim());
        updatedUser.setEmail(emailField.getText().trim());
        updatedUser.setBirthDate(Date.valueOf(dateOfBirthPicker.getValue()));
        updatedUser.setAddress(addressArea.getText().trim());
        updatedUser.setGender(genderComboBox.getValue());

        // Preserve existing values
        updatedUser.setCreatedAt(Client.currentUser.getCreatedAt());
        updatedUser.setPassword(Client.currentUser.getPassword());
        updatedUser.setRoleID(Client.currentUser.getRoleID());
        updatedUser.setIsActive(Client.currentUser.getIsActive());
        updatedUser.setIsBanned(Client.currentUser.getIsBanned());

        Task<Void> updateTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                userManagementDAO.updateUser(updatedUser);
                return null;
            }
        };

        updateTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                // Update the Client.currentUser with new values
                Client.currentUser = updatedUser;

                AlertDialog.showAlertDialog(
                    Alert.AlertType.INFORMATION,
                    "Profile Updated",
                    "Your profile has been updated successfully",
                    ""
                );
                logger.info("User profile updated for user ID: {}", updatedUser.getUserId());
            });
        });

        updateTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Failed to update profile",
                    ""
                );
                logger.error("Failed to update user profile", updateTask.getException());
            });
        });

        new Thread(updateTask).start();
    }
}