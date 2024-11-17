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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mop.app.client.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupDetailsController {
    private static final Logger logger = LoggerFactory.getLogger(GroupDetailsController.class);

    @FXML
    public Label groupNameLabel;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TableView<User> groupUserTable;
    @FXML
    private TableColumn<User, String> usernameCol;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User, String> displayNameCol;
    @FXML
    private TableColumn<User, String> birthdayCol;

    private ObservableList<User> userList;

    public GroupDetailsController() {
        userList = FXCollections.observableArrayList();

        Faker faker = new Faker(Locale.ENGLISH);
        for (int i = 0; i < 10; i++) {
            String username = faker.name().username();
            String email = faker.internet().emailAddress();
            String displayName = faker.name().fullName();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date randomDate = faker.date().past(30, TimeUnit.DAYS);
            String birthday = dateFormat.format(randomDate);
            boolean isAdmin = faker.bool().bool();
            userList.add(new User(username, email, displayName, birthday, isAdmin));
        }

        logger.info("GroupDetailController initialized");
    }

    public void setGroupId(long groupId) {
//        groupNameLabel.setText("Group: " + groupId);
        logger.info("GroupDetailController initialized for group {}", groupId);
    }

    public void setGroupName(String groupName) {
        groupNameLabel.setText("Group: " + groupName);
    }

    @FXML
    public void initialize() {
        groupUserTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        birthdayCol.setCellValueFactory(new PropertyValueFactory<>("birthday"));

        List<TableColumn<User, String>> columns = List.of(usernameCol, emailCol, displayNameCol, birthdayCol);

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

        groupUserTable.setItems(userList);

        groupUserTable.setRowFactory(param -> {
            TableRow<User> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });

        filterComboBox.getItems().addAll("All", "Admin", "User");
        filterComboBox.getSelectionModel().select("All");
    }

    @FXML
    private void applyFilter() {
        String selectedRole = filterComboBox.getValue();
        ObservableList<User> filteredList = FXCollections.observableArrayList();

        for (User user : userList) {
            if ("All".equals(selectedRole) ||
                ("Admin".equals(selectedRole) && user.isAdmin()) ||
                ("User".equals(selectedRole) && !user.isAdmin())) {
                filteredList.add(user);
            }
        }

        groupUserTable.setItems(filteredList);
    }
}
