package mop.app.client.controller.admin;

import java.util.Date;
import java.text.SimpleDateFormat;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import mop.app.client.dao.UserManagementDAO;
import mop.app.client.dto.UserDTO;
import mop.app.client.util.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @FXML
    private TableView<UserDTO> userTable;
    @FXML
    private TextField filterField;
    @FXML
    private ComboBox<String> actionComboBox;
    @FXML
    private TableColumn<UserDTO, String> userNameCol;
    @FXML
    private TableColumn<UserDTO, String> emailCol;
    @FXML
    private TableColumn<UserDTO, String> displayNameCol;
    @FXML
    private TableColumn<UserDTO, Date> birthdayCol;
    @FXML
    private TableColumn<UserDTO, String> genderCol;
    @FXML
    private TableColumn<UserDTO, String> addressCol;
    @FXML
    public TableColumn<UserDTO, Boolean> blockCol;
    @FXML
    private TableColumn<UserDTO, String> updateCol;
    private final ObservableList<UserDTO> userList;
    private final UserManagementDAO userManagementDAO;

    public UserController() {
        userManagementDAO = new UserManagementDAO();
        userList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableView();
        loadUsersAsync();
    }

    private void setupTableColumns() {
        List<TableColumn<UserDTO, String>> strColumns =
            List.of(userNameCol, emailCol, displayNameCol, genderCol, addressCol);

        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        birthdayCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        blockCol.setCellValueFactory(new PropertyValueFactory<>("isBanned"));

        strColumns.forEach(this::styleTableColumn);

        birthdayCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                    String formattedDate = dateFormat.format(item);
                    setText(formattedDate);

                    setAlignment(Pos.CENTER_LEFT);
                    setStyle(
                        "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 12px;"
                    );
                }
            }
        });

        blockCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    boolean isBlocked = item;
                    setText(isBlocked ? "Blocked" : "Active");
                    setAlignment(Pos.CENTER_LEFT);
                    setStyle(
                        "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 12px;"
                    );
                }
            }
        });
    }

    private void styleTableColumn(TableColumn<UserDTO, String> column) {
        column.setCellFactory(stringTableColumn -> new TableCell<>() {
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
        });
    }

    private void setupTableView() {
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Setup update column
        updateCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button detailButton = createUpdateButton();
                    StackPane stackPane = new StackPane(detailButton);
                    stackPane.setPrefWidth(Double.MAX_VALUE);
                    stackPane.setPrefHeight(Double.MAX_VALUE);
                    setGraphic(stackPane);
                }
            }

            private Button createUpdateButton() {
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
                detailButton.setOnAction(event -> {
                    UserDTO user = getTableRow().getItem();
                    if (user != null) userDetails(user);
                });
                return detailButton;
            }
        });

        // Set row factory
        userTable.setRowFactory(param -> {
            TableRow<UserDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    UserDTO user = row.getItem();
                    userActivity(user);
                }
            });
            return row;
        });

        // Set items
        userTable.setItems(userList);

        actionComboBox.getItems().addAll("Block", "Unblock", "Delete");
    }

    private void loadUsersAsync() {
        Task<List<UserDTO>> task = new Task<>() {
            @Override
            protected List<UserDTO> call() {
                List<UserDTO> databaseUsers = userManagementDAO.getAllUsers();
                return databaseUsers != null
                    ? databaseUsers.stream()
                    .map(UserDTO::new)
                    .collect(Collectors.toList())
                    : List.of();
            }
        };

        task.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                userList.setAll(task.getValue());
                userTable.refresh();
                logger.info("Loaded {} users from database", userList.size());
            });
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                logger.error("Failed to load users", task.getException());
            });
        });

        new Thread(task).start();
    }

    private void userDetails(UserDTO user) {
        logger.info("User details: " + user.getUserId());
        ViewModel.getInstance().getViewFactory().getSelectedView()
            .set("User-" + user.getUserId() + "-" + user.getUsername());
    }

    private void userActivity(UserDTO user) {
        logger.info("User activity: " + user.getUserId());
        ViewModel.getInstance().getViewFactory().getSelectedView()
            .set("UserActivity-" + user.getUserId() + "-" + user.getUsername());
    }

    @FXML
    public void applyFilter(ActionEvent event) {
        // Implement filtering logic
    }

    @FXML
    public void applyAction(ActionEvent event) {
        String action = actionComboBox.getValue();

        List<UserDTO> selectedUsers = userTable.getSelectionModel().getSelectedItems();

        if (selectedUsers.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select at least one user to apply the action.");
            alert.showAndWait();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                switch (action) {
                    case "Block":
                        userManagementDAO.blockUsers(selectedUsers);
                        break;
                    case "Unblock":
                        userManagementDAO.unblockUsers(selectedUsers);
                        break;
                    case "Delete":
                        userManagementDAO.deleteUsers(selectedUsers);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid action selected.");
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> {
                    userTable.refresh();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, action + " action applied successfully.");
                    alert.showAndWait();
                });
            }

            @Override
            protected void failed() {
                super.failed();
                Platform.runLater(() -> {
                    logger.error("Action failed: " + action);
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to apply " + action + " action.");
                    alert.showAndWait();
                });
            }
        };

        new Thread(task).start();
    }

    public void createUser(ActionEvent event) {
        // Implement user creation logic
    }
}