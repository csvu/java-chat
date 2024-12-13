package mop.app.client.controller.admin;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import mop.app.client.dao.UserManagementDAO;
import mop.app.client.dto.UserDTO;
import mop.app.client.util.TableStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewUserController {
    private static final Logger logger = LoggerFactory.getLogger(NewUserController.class);

    @FXML
    private TextField emailFilter;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TableView<UserDTO> newUserTable;
    @FXML
    private TableColumn<UserDTO, String> usernameCol;
    @FXML
    private TableColumn<UserDTO, String> emailCol;
    @FXML
    private TableColumn<UserDTO, String> displayNameCol;
    @FXML
    private TableColumn<UserDTO, Timestamp> createdCol;

    private ObservableList<UserDTO> userList;
    private ObservableList<UserDTO> filteredUsers;
    private UserManagementDAO userManagementDAO;

    public NewUserController() {
        userManagementDAO = new UserManagementDAO();
        userList = FXCollections.observableArrayList();
        filteredUsers = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        filterComboBox.getItems().addAll("Today", "1 day ago", "1 week ago", "1 month ago", "1 year ago", "All time");
        filterComboBox.getSelectionModel().select("All time");

        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        TableStyle.styleTable(newUserTable,
            List.of(Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT),
            "14px"
        );

        newUserTable.setRowFactory(tv -> {
            TableRow<UserDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });

        newUserTable.setItems(userList);

        loadUsers();

        setupEmailFilter();
    }

    private void loadUsers() {
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
                newUserTable.refresh();
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

    private void setupEmailFilter() {
        emailFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            Task<List<UserDTO>> filterTask = new Task<>() {
                @Override
                protected List<UserDTO> call() throws Exception {
                    if (newValue == null || newValue.trim().isEmpty()) {
                        return userList;
                    }

                    String filterLower = newValue.toLowerCase().trim();
                    return userList.stream()
                        .filter(user ->
                            user.getEmail().toLowerCase().contains(filterLower) ||
                                user.getDisplayName().toLowerCase().contains(filterLower)
                        )
                        .collect(Collectors.toList());
                }
            };

            filterTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    filteredUsers.clear();
                    filteredUsers.addAll(filterTask.getValue());

                    applyDateFilter(filteredUsers);
                });
            });

            new Thread(filterTask).start();
        });
    }

    @FXML
    private void applyFilter() {
        // If email filter is active, filter from filtered users
        // Otherwise, filter from the full user list
        ObservableList<UserDTO> sourceList =
            !emailFilter.getText().trim().isEmpty() ? filteredUsers : userList;

        applyDateFilter(sourceList);
    }

    private void applyDateFilter(ObservableList<UserDTO> sourceList) {
        String selectedFilter = filterComboBox.getSelectionModel().getSelectedItem();
        ObservableList<UserDTO> filteredList = FXCollections.observableArrayList();

        LocalDate currentDate = LocalDate.now();
        long currentTime = System.currentTimeMillis();
        long timeLimit = 0;

        switch (selectedFilter) {
            case "Today":
                filteredList.setAll(sourceList.stream()
                    .filter(user -> {
                        LocalDate userCreationDate = user.getCreatedAt().toLocalDateTime().toLocalDate();
                        return userCreationDate.equals(currentDate);
                    })
                    .collect(Collectors.toList()));
                break;
            case "1 day ago":
                timeLimit = currentTime - TimeUnit.DAYS.toMillis(1);
                break;
            case "1 week ago":
                timeLimit = currentTime - TimeUnit.DAYS.toMillis(7);
                break;
            case "1 month ago":
                timeLimit = currentTime - TimeUnit.DAYS.toMillis(30);
                break;
            case "1 year ago":
                timeLimit = currentTime - TimeUnit.DAYS.toMillis(365);
                break;
            case "All time":
                filteredList = sourceList;
                break;
            default:
                filteredList = sourceList;
        }

        if (!selectedFilter.equals("All time") && !selectedFilter.equals("Today")) {
            long finalTimeLimit = timeLimit;
            filteredList.setAll(sourceList.stream()
                .filter(user -> user.getCreatedAt().getTime() >= finalTimeLimit)
                .collect(Collectors.toList()));
        }

        newUserTable.setItems(filteredList);
        newUserTable.refresh();
    }
}