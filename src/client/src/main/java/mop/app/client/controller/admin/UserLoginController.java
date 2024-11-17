package mop.app.client.controller.admin;

import com.github.javafaker.Faker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mop.app.client.model.LoginTime;
import mop.app.client.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLoginController {
    private static final Logger logger = LoggerFactory.getLogger(UserLoginController.class);

    @FXML
    private TableView<LoginTime> userLoginTable;
    @FXML
    private TableColumn<LoginTime, String> usernameCol;
    @FXML
    private TableColumn<LoginTime, String> displayNameCol;
    @FXML
    private TableColumn<LoginTime, String> loginDateCol;

    private ObservableList<LoginTime> loginTimeObservableList;

    public UserLoginController() {
        logger.info("Initializing UserLoginController");
        loginTimeObservableList = FXCollections.observableArrayList();

        Faker faker = new Faker(Locale.ENGLISH);

        for (int i = 0; i < 50; i++) {
            String username = faker.name().username();
            String displayName = faker.name().fullName();
            String email = faker.internet().emailAddress();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");
            Date randomDate = faker.date().past(30, TimeUnit.DAYS);
            String date = dateFormat.format(randomDate);
            loginTimeObservableList.add(new LoginTime(username, email, displayName, date));
        }

    }

    public void initialize() {
        userLoginTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        loginDateCol.setCellValueFactory(new PropertyValueFactory<>("loginDate"));

        List<TableColumn<LoginTime, String>> columns = List.of(usernameCol, displayNameCol, loginDateCol);

        columns.forEach(column -> column.setCellFactory(stringTableColumn -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER_LEFT);
                    setStyle(
                        "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 14px;"
                    );
                }
            }
        }));

        userLoginTable.setItems(loginTimeObservableList);

        userLoginTable.setRowFactory(param -> {
            TableRow<LoginTime> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });
    }
}
