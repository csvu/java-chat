package mop.app.client.controller.admin;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
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

import java.util.List;
import java.util.stream.Collectors;

public class NewUserController {
    private static final Logger logger = LoggerFactory.getLogger(NewUserController.class);

    @FXML
    public TextField emailFilter;
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
    private UserManagementDAO userManagementDAO;

    public NewUserController() {
        userManagementDAO = new UserManagementDAO();
        userList = FXCollections.observableArrayList();
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

        newUserTable.setItems(userList);

        newUserTable.setRowFactory(tv -> {
            TableRow<UserDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });

        loadUsers();
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

    @FXML
    public void applyFilter(ActionEvent event) {
        String selectedFilter = filterComboBox.getSelectionModel().getSelectedItem();
        ObservableList<UserDTO> filteredList = FXCollections.observableArrayList();

        LocalDate currentDate = LocalDate.now();
        long currentTime = System.currentTimeMillis();
        long timeLimit = 0;

        switch (selectedFilter) {
            case "Today":
                filteredList.setAll(userList.stream()
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
                filteredList = userList;
                break;
            default:
                filteredList = userList;
        }

        if (!selectedFilter.equals("All time") && !selectedFilter.equals("Today")) {
            long finalTimeLimit = timeLimit;
            filteredList.setAll(userList.stream()
                .filter(user -> user.getCreatedAt().getTime() >= finalTimeLimit)
                .collect(Collectors.toList()));
        }

        newUserTable.setItems(filteredList);
        newUserTable.refresh();
    }
}
