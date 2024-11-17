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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import mop.app.client.model.Group;
import mop.app.client.model.User;
import mop.app.client.util.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @FXML
    public TableView<User> userTable;
    @FXML
    public TextField filterField;
    @FXML
    private ComboBox<String> actionComboBox;
    @FXML
    private TableColumn<User, String> userNameCol;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User, String> displayNameCol;
    @FXML
    private TableColumn<User, String> birthdayCol;
    @FXML
    private TableColumn<User, String> genderCol;
    @FXML
    private TableColumn<User, String> addressCol;
    @FXML
    public TableColumn<User, String> updateCol;

    private ObservableList<User> userList;

    public UserController() {
        userList = FXCollections.observableArrayList();

        Faker faker = new Faker(Locale.ENGLISH);
        for (int i = 0; i < 20; i++) {
            String username = faker.name().username();
            String email = faker.internet().emailAddress();
            String displayName = faker.name().fullName();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date randomDate = faker.date().past(30, TimeUnit.DAYS);
            String birthday = dateFormat.format(randomDate);
            int randNumber = faker.number().numberBetween(0, 3);
            String gender;
            if (randNumber == 1) {
                gender = "Male";
            } else if (randNumber == 2) {
                gender = "Female";
            } else {
                gender = "Other";
            }
            String address = faker.address().fullAddress();
            userList.add(new User(i, username, email, displayName, birthday, gender, address));
        }

        logger.info("UserController initialized");
    }

    @FXML
    public void initialize() {
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        List<TableColumn<User, String>> columns =
            List.of(userNameCol, emailCol, displayNameCol, birthdayCol, genderCol, addressCol);
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        birthdayCol.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

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
                            "-fx-font-size: 12px;"
                    );
                }
            }
        }));

        updateCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Button detailButton = new Button("Update");
                            detailButton.setStyle(
                                "-fx-background-color: #74aef6; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-border-radius: 20px; " +
                                    "-fx-background-radius: 20px; " +
                                    "-fx-font-size: 14px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 10 20 10 20; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0.0, 0, 1);"
                            );
                            detailButton.setMaxWidth(Double.MAX_VALUE);
                            detailButton.setMaxHeight(Double.MAX_VALUE);
                            detailButton.setOnAction(event -> userDetails(getTableRow().getItem()));

                            StackPane stackPane = new StackPane(detailButton);
                            stackPane.setPrefWidth(Double.MAX_VALUE);
                            stackPane.setPrefHeight(Double.MAX_VALUE);
                            setGraphic(stackPane);
                        }
                    }
                };
            }
        });

        userTable.setItems(userList);

        userTable.setRowFactory(param -> {
            TableRow<User> row = new TableRow<>();
            row.setPrefHeight(50);

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    User user = row.getItem();
                    userActivity(user);
                }
            });

            return row;
        });

//        userTable.setOnMouseClicked(event -> {
//            if (event.getClickCount() == 2 && !userTable.getSelectionModel().isEmpty()) {
//                User selectedUser = userTable.getSelectionModel().getSelectedItem();
//                userDetails(selectedUser);
//            }
//        });

        actionComboBox.getItems().addAll("Block", "Unblock", "Delete");
//        actionComboBox.getSelectionModel().select("Block");
    }

    private void userDetails(User user) {
        logger.info("User details: " + user.getId());
        ViewModel.getInstance().getViewFactory().getSelectedView()
            .set("User-" + user.getId() + "-" + user.getUsername());
    }

    private void userActivity(User user) {
        logger.info("User activity: " + user.getId());
        ViewModel.getInstance().getViewFactory().getSelectedView()
            .set("UserActivity-" + user.getId() + "-" + user.getUsername());
    }

    @FXML
    public void applyFilter(ActionEvent event) {
    }

    @FXML
    public void applyAction(ActionEvent event) {
    }

    public void createUser(ActionEvent event) {
    }
}
