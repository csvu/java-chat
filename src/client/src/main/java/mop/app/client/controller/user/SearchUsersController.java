package mop.app.client.controller.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import mop.app.client.Client;

import java.io.IOException;
import java.util.function.Consumer;


public class SearchUsersController extends HBox {
    @FXML
    private TextField searchUsersTextField;
    @FXML
    private ImageView cancel;

    private Consumer<String> onText;
    private Runnable offText;

    public SearchUsersController(Consumer<String> onText, Runnable offText) throws IOException {
        this.onText = onText;
        this.offText = offText;

        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/search-users.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        cancel.setVisible(false);

        searchUsersTextField.textProperty().addListener((observable, oldVal, newVal) -> cancel.setVisible(!searchUsersTextField.getText().isEmpty()));

        searchUsersTextField.setOnKeyPressed(e -> {
            System.out.print(e.getCode());

            if (e.getCode() == KeyCode.ENTER) {
                if (!searchUsersTextField.getText().trim().isEmpty()) {
                    onText.accept(searchUsersTextField.getText());
                }
            }
        });


        cancel.setOnMouseClicked(event -> {
            searchUsersTextField.clear();
            offText.run();
            this.requestFocus();
        });

    }

}
