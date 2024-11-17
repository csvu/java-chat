package mop.app.client.controller.admin;

import com.github.javafaker.Faker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mop.app.client.model.Group;
import mop.app.client.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewUserController {
    private static final Logger logger = LoggerFactory.getLogger(NewUserController.class);
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TableView<User> newUserTable;
    @FXML
    private TableColumn<User, String> usernameCol;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User, String> displayNameCol;
    @FXML
    private TableColumn<User, String> createdCol;

    private ObservableList<User> userList;

    public NewUserController() {
        logger.info("Initializing NewUserController");
        userList = FXCollections.observableArrayList();

        Faker faker = new Faker(Locale.ENGLISH);
        for (int i = 0; i < 50; i++) {
            String name = faker.team().name();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");
            Date randomDate = faker.date().past(30, TimeUnit.DAYS);
            String date = dateFormat.format(randomDate);
            userList.add(new User(name, faker.internet().emailAddress(), faker.name().fullName(), date, date));
        }
    }

    @FXML
    public void initialize() {
        filterComboBox.getItems().addAll("1 day ago", "1 week ago", "1 month ago", "1 year ago", "All time");
        filterComboBox.getSelectionModel().select("All time");

        logger.info("Initializing NewUserController");
        newUserTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        List<TableColumn<User, String>> columns = List.of(usernameCol, emailCol, displayNameCol, createdCol);

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

        newUserTable.setItems(userList);

        newUserTable.setRowFactory(param -> {
            TableRow<User> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });

        filterComboBox.getItems().addAll("All", "Admin", "User");
        filterComboBox.getSelectionModel().select("All");
    }

    public void applyFilter(ActionEvent event) {
    }
}
