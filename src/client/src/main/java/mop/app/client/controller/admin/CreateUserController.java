package mop.app.client.controller.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import mop.app.client.util.AlertDialog;
import mop.app.client.util.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserController {
    private static final Logger logger = LoggerFactory.getLogger(CreateUserController.class);

    @FXML
    public TextField usernameField;
    @FXML
    public TextField displayNameField;
    @FXML
    public TextField emailField;
    @FXML
    public ComboBox<String> genderComboBox;
    @FXML
    public DatePicker birthdayPicker;
    @FXML
    public ComboBox<String> roleComboBox;
    @FXML
    public TextArea addressArea;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField confirmPasswordField;

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

    public void handleBack(ActionEvent event) {
        ViewModel.getInstance().getViewFactory().getSelectedView().set("User");
    }

    public void createSave(ActionEvent event) {
    }
}
