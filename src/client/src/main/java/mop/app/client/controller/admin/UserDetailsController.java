package mop.app.client.controller.admin;

import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDetailsController {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsController.class);

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
    public TextField passwordField;
    @FXML
    public TextField confirmPasswordField;

    public void handleSave(ActionEvent event) {
        logger.info("Save button clicked");
    }

    public void handleDelete(ActionEvent event) {
        logger.info("Delete button clicked");
    }
//    @FXML
//    public Label userNameLabel;
//
//    public void setUserId(long userId) {
//
//    }
//
//    public void setUserName(String userName) {
//        userNameLabel.setText("Username: " + userName);
//    }

    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        roleComboBox.getItems().addAll("Admin", "User");

        usernameField.setText("nam.160504");
        displayNameField.setText("Nam Nguyen");
        emailField.setText("namisme16052004@gmail.com");
        birthdayPicker.setValue(LocalDate.of(2004, 5, 16));
        addressArea.setText("227 Nguyen Van Cu, District 5, Ho Chi Minh City");
        passwordField.setText("123456789");
        confirmPasswordField.setText("123456789");
        genderComboBox.setValue("Male");
        roleComboBox.setValue("User");
    }
}
