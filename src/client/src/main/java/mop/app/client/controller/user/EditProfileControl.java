package mop.app.client.controller.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import mop.app.client.Client;

import java.io.IOException;


public class EditProfileControl extends VBox {
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

    public EditProfileControl() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/edit-profile.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        emailField.setText(Client.currentUser.getEmail());
        usernameField.setText(Client.currentUser.getUsername());
        fullNameField.setText(Client.currentUser.getDisplayName());
        dateOfBirthPicker.setValue(Client.currentUser.getBirthDate().toLocalDate());
        addressArea.setText(Client.currentUser.getAddress());
        genderComboBox.setValue(Client.currentUser.getGender());


    }





}