package mop.app.client.controller.user;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import mop.app.client.Client;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Objects;
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
