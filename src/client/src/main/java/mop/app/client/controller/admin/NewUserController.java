package mop.app.client.controller.admin;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import javafx.scene.control.DatePicker;
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
    private TableView<UserDTO> newUserTable;
    @FXML
    private TableColumn<UserDTO, String> usernameCol;
    @FXML
    private TableColumn<UserDTO, String> emailCol;
    @FXML
    private TableColumn<UserDTO, String> displayNameCol;
    @FXML
    private TableColumn<UserDTO, Timestamp> createdCol;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;

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
        // Set default date pickers to current date
        LocalDate currentDate = LocalDate.now();
        fromDatePicker.setValue(currentDate);
        toDatePicker.setValue(currentDate);

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
        setupDatePickerListeners();
    }

    private void setupDatePickerListeners() {
        fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        toDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
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
                applyFilter(); // Apply initial filter
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

                    applyFilter();
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

        // Get the date range from date pickers
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        // Ensure fromDate is not after toDate
        if (fromDate.isAfter(toDate)) {
            // Swap dates if needed
            LocalDate temp = fromDate;
            fromDate = toDate;
            toDate = temp;
            fromDatePicker.setValue(fromDate);
            toDatePicker.setValue(toDate);
        }

        // Create the filtered list based on date range and email filter
        LocalDate finalFromDate = fromDate;
        LocalDate finalToDate = toDate;
        ObservableList<UserDTO> filteredList = FXCollections.observableArrayList(
            sourceList.stream()
                .filter(user -> {
                    LocalDate userCreationDate = user.getCreatedAt().toLocalDateTime().toLocalDate();
                    return !userCreationDate.isBefore(finalFromDate) &&
                        !userCreationDate.isAfter(finalToDate);
                })
                .collect(Collectors.toList())
        );

        newUserTable.setItems(filteredList);
        newUserTable.refresh();
    }
}