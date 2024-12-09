package mop.app.client.controller.admin;

import com.github.javafaker.Faker;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import mop.app.client.model.LoginTime;
import mop.app.client.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserActivityController {
    private static final Logger logger = LoggerFactory.getLogger(UserActivityController.class);

    @FXML
    private Label usernameLabel;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private AnchorPane contentPane;

    private TableView<LoginTime> activityTable;
    private TableView<User> friendsTable;
    private TableView<User> friendsOfFriendsTable;
    private long userId;
    private String username;

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUsername(String userName) {
        this.username = userName;
        usernameLabel.setText("Username: " + userName);
    }

    @FXML
    public void initialize() {
        logger.info("UserActivityController initialized");

        filterComboBox.getItems().addAll("Activities", "Friends", "Friends Of Friends");
        filterComboBox.setValue("Activities");

        initializeActivityTable();
        initializeFriendsTable();
        initializeFriendsOfFriendsTable();

        applyFilter(null);
    }

    private void initializeActivityTable() {
        activityTable = new TableView<>();
        activityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<LoginTime, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(300);

        TableColumn<LoginTime, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(300);

        TableColumn<LoginTime, String> loginDateCol = new TableColumn<>("Login Date");
        loginDateCol.setCellValueFactory(new PropertyValueFactory<>("loginDate"));
        loginDateCol.setPrefWidth(300);

        activityTable.getColumns().addAll(usernameCol, emailCol, loginDateCol);
        styleTable(activityTable);
    }

    private void initializeFriendsTable() {
        friendsTable = new TableView<>();
        friendsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(300);

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(300);

        TableColumn<User, String> displayNameCol = new TableColumn<>("Display Name");
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        displayNameCol.setPrefWidth(300);

        friendsTable.getColumns().addAll(usernameCol, emailCol, displayNameCol);
        styleTable(friendsTable);
    }

    private void initializeFriendsOfFriendsTable() {
        friendsOfFriendsTable = new TableView<>();
        friendsOfFriendsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(300);

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(300);

        TableColumn<User, String> displayNameCol = new TableColumn<>("Display Name");
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        displayNameCol.setPrefWidth(300);

        friendsOfFriendsTable.getColumns().addAll(usernameCol, emailCol, displayNameCol);
        styleTable(friendsOfFriendsTable);
    }

    private <T> void styleTable(TableView<T> tableView) {
        AnchorPane.setTopAnchor(tableView, 0.0);
        AnchorPane.setLeftAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);
        AnchorPane.setBottomAnchor(tableView, 0.0);

        for (TableColumn<T, ?> column : tableView.getColumns()) {
            @SuppressWarnings("unchecked")
            TableColumn<T, String> typedColumn = (TableColumn<T, String>) column;
            typedColumn.setCellFactory(col -> new TableCell<T, String>() {
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
            });
        }

        tableView.setRowFactory(param -> {
            TableRow<T> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });

        tableView.setStyle(
            "-fx-background-color: transparent; " +
                "-fx-control-inner-background: transparent; " +
                "-fx-background-insets: 0; " +
                "-fx-padding: 0;"
        );
    }


    @FXML
    public void applyFilter(ActionEvent event) {
        if (contentPane != null) {
            contentPane.getChildren().clear();

            String selectedFilter = filterComboBox.getValue();
            switch (selectedFilter) {
                case "Activities":
                    showActivityTable();
                    loadActivities();
                    break;
                case "Friends":
                    showFriendsTable();
                    loadFriends();
                    break;
                case "Friends Of Friends":
                    showFriendsOfFriendsTable();
                    loadFriendsOfFriends();
                    break;
            }
        }
    }

    private void showActivityTable() {
        contentPane.getChildren().add(activityTable);
    }

    private void showFriendsTable() {
        contentPane.getChildren().add(friendsTable);
    }

    private void showFriendsOfFriendsTable() {
        contentPane.getChildren().add(friendsOfFriendsTable);
    }

    private void loadActivities() {
        Faker faker = new Faker(Locale.ENGLISH);

        List<LoginTime> activities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");
            Date randomDate = faker.date().past(30, TimeUnit.DAYS);
            String date = dateFormat.format(randomDate);
            activities.add(new LoginTime(
                faker.name().username(),
                faker.internet().emailAddress(),
                faker.name().fullName(),
                date
            ));
        }

        ObservableList<LoginTime> observableList = FXCollections.observableArrayList(activities);
        activityTable.setItems(observableList);
    }

    private void loadFriends() {
        Faker faker = new Faker(Locale.ENGLISH);
        List<User> friends = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            friends.add(new User(
                faker.name().username(),
                faker.internet().emailAddress(),
                faker.name().fullName()
            ));
        }

        ObservableList<User> observableList = FXCollections.observableArrayList(friends);
        friendsTable.setItems(observableList);
    }

    private void loadFriendsOfFriends() {
        Faker faker = new Faker(Locale.ENGLISH);
        List<User> friendsOfFriends = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            friendsOfFriends.add(new User(
                faker.name().username(),
                faker.internet().emailAddress(),
                faker.name().fullName()
            ));
        }

        ObservableList<User> observableList = FXCollections.observableArrayList(friendsOfFriends);
        friendsOfFriendsTable.setItems(observableList);
    }
}