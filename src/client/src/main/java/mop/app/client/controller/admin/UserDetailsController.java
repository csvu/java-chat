package mop.app.client.controller.admin;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import mop.app.client.dao.AuthDAO;
import mop.app.client.dao.RoleDAO;
import mop.app.client.dao.UserManagementDAO;
import mop.app.client.dto.UserDTO;
import mop.app.client.util.AlertDialog;
import mop.app.client.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDetailsController {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsController.class);

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
    private TextField passwordField;
    @FXML
    private TextField confirmPasswordField;

    private long userId;
    private String userName;
    private final UserManagementDAO userManagementDAO;
    private final AuthDAO authDAO;
    private final RoleDAO roleDAO;

    public UserDetailsController() {
        userManagementDAO = new UserManagementDAO();
        authDAO = new AuthDAO();
        roleDAO = new RoleDAO();
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
                            "For example: 01/01/2001"
                        );

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
                    ""
                );

                birthdayPicker.setValue(null);
                return;
            }

            if (selectedDate.isAfter(currentDate)) {
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Invalid Birth Date",
                    "Birth date cannot be in the future.",
                    ""
                );
                birthdayPicker.setValue(null);
            }
        }
    }

    public void loadUserDetails(long userId) {
        this.userId = userId;

        Task<UserDTO> task = new Task<>() {
            @Override
            protected UserDTO call() {
                return authDAO.getUserById(userId);
            }
        };

        task.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                UserDTO user = task.getValue();
                if (user != null) {
                    usernameField.setText(user.getUsername());
                    displayNameField.setText(user.getDisplayName());
                    emailField.setText(user.getEmail());

                    if (user.getBirthDate() != null) {
                        birthdayPicker.setValue(convertToLocalDate(user.getBirthDate()));
                    }

                    addressArea.setText(user.getAddress());
                    genderComboBox.setValue(user.getGender());

                    String roleName = roleDAO.getRoleByUserId(userId).toLowerCase();
                    roleName = roleName.substring(0, 1).toUpperCase() + roleName.substring(1);
                    roleComboBox.setValue(roleName);

                    passwordField.clear();
                    confirmPasswordField.clear();

                    logger.info("User details loaded successfully for user ID: {}", userId);
                } else {
                    AlertDialog.showAlertDialog(
                        Alert.AlertType.ERROR,
                        "Error",
                        "Failed to load user details",
                        ""
                    );
                }
            });
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                logger.error("Failed to load user details", task.getException());
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Failed to load user details",
                    ""
                );
            });
        });

        new Thread(task).start();
    }

    private LocalDate convertToLocalDate(Date dateToConvert) {
        return new Date(dateToConvert.getTime()).toLocalDate();
    }

    public void handleSave(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();

        Task<Boolean> validationTask = new Task<>() {
            @Override
            protected Boolean call() {
                UserDTO currentUser = authDAO.getUserById(userId);

                boolean isUsernameUnique = true;
                boolean isEmailUnique = true;

                if (currentUser != null) {
                    if (!username.equals(currentUser.getUsername())) {
                        isUsernameUnique = authDAO.isUsernameExists(username);
                    }

                    if (!email.equals(currentUser.getEmail())) {
                        isEmailUnique = authDAO.isEmailExists(email);
                    }
                }

                return isUsernameUnique && isEmailUnique;
            }
        };

        validationTask.setOnSucceeded(validationEvent -> {
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

                // Retrieve current user details
                Task<UserDTO> retrieveTask = new Task<>() {
                    @Override
                    protected UserDTO call() {
                        return authDAO.getUserById(userId);
                    }
                };

                retrieveTask.setOnSucceeded(retrieveEvent -> {
                    Platform.runLater(() -> {
                        UserDTO currentUser = retrieveTask.getValue();
                        if (currentUser == null) {
                            AlertDialog.showAlertDialog(
                                Alert.AlertType.ERROR,
                                "Error",
                                "Failed to retrieve current user details",
                                ""
                            );
                            return;
                        }

                        UserDTO updatedUser = new UserDTO();
                        updatedUser.setUserId(userId);
                        updatedUser.setUsername(username);
                        updatedUser.setDisplayName(displayNameField.getText().trim());
                        updatedUser.setEmail(email);
                        updatedUser.setBirthDate(Date.valueOf(birthdayPicker.getValue()));
                        updatedUser.setAddress(addressArea.getText().trim());
                        updatedUser.setGender(genderComboBox.getValue());
                        updatedUser.setCreatedAt(currentUser.getCreatedAt());

                        long roleId = roleDAO.getRoleIdByRoleName(roleComboBox.getValue().toUpperCase());
                        updatedUser.setRoleID(roleId);
                        updatedUser.setIsActive(currentUser.getIsActive());
                        updatedUser.setIsBanned(currentUser.getIsBanned());

                        String newPassword = passwordField.getText();
                        if (!newPassword.isEmpty()) {
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

                            updatedUser.setPassword(PasswordUtil.hash(newPassword));
                        } else {
                            updatedUser.setPassword(currentUser.getPassword());
                        }

                        Task<Void> updateTask = new Task<>() {
                            @Override
                            protected Void call() throws Exception {
                                userManagementDAO.updateUser(updatedUser);
                                return null;
                            }
                        };

                        updateTask.setOnSucceeded(e -> {
                            Platform.runLater(() -> {
                                AlertDialog.showAlertDialog(
                                    Alert.AlertType.INFORMATION,
                                    "User details updated",
                                    "User details updated successfully",
                                    ""
                                );
                                logger.info("User details updated for user ID: {}", userId);
                            });
                        });

                        updateTask.setOnFailed(e -> {
                            Platform.runLater(() -> {
                                AlertDialog.showAlertDialog(
                                    Alert.AlertType.ERROR,
                                    "Error",
                                    "Failed to update user details",
                                    ""
                                );
                                logger.error("Failed to update user details", updateTask.getException());
                            });
                        });

                        new Thread(updateTask).start();
                    });
                });

                retrieveTask.setOnFailed(retrieveEvent -> {
                    Platform.runLater(() -> {
                        AlertDialog.showAlertDialog(
                            Alert.AlertType.ERROR,
                            "Error",
                            "Failed to retrieve user details",
                            ""
                        );
                        logger.error("Failed to retrieve user details", retrieveTask.getException());
                    });
                });

                new Thread(retrieveTask).start();
            });
        });

        validationTask.setOnFailed(validationEvent -> {
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

        // Only check password match if a new password is entered
        if (!passwordField.getText().isEmpty() &&
            !passwordField.getText().equals(confirmPasswordField.getText())) {
            AlertDialog.showAlertDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Passwords do not match",
                ""
            );
            return false;
        }

        return true;
    }

    public void handleDelete(ActionEvent event) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION,
            "Are you sure you want to delete this user?",
            ButtonType.YES, ButtonType.NO);
        confirmDialog.setHeaderText("Confirm User Deletion");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        UserDTO userToDelete = authDAO.getUserById(userId);
                        userManagementDAO.deleteUsers(List.of(userToDelete));
                        return null;
                    }
                };

                task.setOnSucceeded(e -> {
                    Platform.runLater(() -> {
                        displayNameField.setText("Deleted User");
                        AlertDialog.showAlertDialog(
                            Alert.AlertType.INFORMATION,
                            "User deleted",
                            "User deleted successfully",
                            ""
                        );
                        logger.info("User deleted: {}", userId);
                    });
                });

                task.setOnFailed(e -> {

                });

                new Thread(task).start();
            }
        });
    }

    public void setUserId(long userId) {
        this.userId = userId;
        loadUserDetails(userId);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
