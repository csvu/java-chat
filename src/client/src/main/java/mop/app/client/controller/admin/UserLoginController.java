package mop.app.client.controller.admin;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import mop.app.client.dao.LoginTimeDAO;
import mop.app.client.dto.UserDTO;
import mop.app.client.dto.UserLoginDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class UserLoginController {
    private static final Logger logger = LoggerFactory.getLogger(UserLoginController.class);

    @FXML
    public TextField usernameFilter;
    @FXML
    public DatePicker dateFilter;
    @FXML
    private TableView<UserLoginDTO> userLoginTable;
    @FXML
    private TableColumn<UserLoginDTO, String> usernameCol;
    @FXML
    private TableColumn<UserLoginDTO, String> displayNameCol;
    @FXML
    private TableColumn<UserLoginDTO, Timestamp> loginDateCol;
    private ObservableList<UserLoginDTO> loginTimeObservableList;
    private ObservableList<UserLoginDTO> filteredLoginTimeList;
    private final LoginTimeDAO loginTimeDAO;

    public UserLoginController() {
        logger.info("Initializing UserLoginController");
        loginTimeObservableList = FXCollections.observableArrayList();
        filteredLoginTimeList = FXCollections.observableArrayList();
        loginTimeDAO = new LoginTimeDAO();
    }

    public void initialize() {
        setupTableColumns();
        loadLoginData();
        setupUsernameFilter();
        setupDateFilter();
    }

    private void setupDateFilter() {
        dateFilter.setConverter(new StringConverter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }

                try {
                    return LocalDate.parse(string, formatter);
                } catch (DateTimeParseException e) {
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR, "Invalid Date Format",
                            "Please enter the date in MM/dd/yyyy format exactly. " +
                                "For example: 01/01/2001");

                        dateFilter.setValue(null);
                    });

                    return null;
                }
            }
        });

        dateFilter.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateDate();
            }
        });
    }

    private void validateDate() {
        LocalDate selectedDate = dateFilter.getValue();

        if (selectedDate != null) {
            LocalDate currentDate = LocalDate.now();

            if (selectedDate.isAfter(currentDate)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Date", "Date cannot be in the future");
                dateFilter.setValue(null);
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupUsernameFilter() {
        usernameFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filterByUsername(newValue.trim());
        });
    }

    private void setupTableColumns() {
        userLoginTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        loginDateCol.setCellValueFactory(new PropertyValueFactory<>("loginAt"));

        List<TableColumn<UserLoginDTO, String>> columns = List.of(usernameCol, displayNameCol);
        columns.forEach(column -> {
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
                                "-fx-font-size: 14px;"
                        );
                    }
                }
            });
        });

        loginDateCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    String formattedDate = dateFormat.format(item);
                    setText(formattedDate);
                    setAlignment(Pos.CENTER_LEFT);
                    setStyle(
                        "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 14px;"
                    );
                }
            }
        });

        userLoginTable.setRowFactory(param -> {
            TableRow<UserLoginDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });

        userLoginTable.setItems(filteredLoginTimeList);
    }

    private void loadLoginData() {
        Task<List<UserLoginDTO>> task = new Task<>() {
            @Override
            protected List<UserLoginDTO> call() {
                List<Object[]> loginTimes = loginTimeDAO.getAllLoginTimes();
                if (loginTimes != null) {
                    return loginTimes.stream().map(objects -> {
                        UserDTO user = (UserDTO) objects[0];
                        Timestamp loginTime = (Timestamp) objects[1];
                        return new UserLoginDTO(user, loginTime);
                    }).collect(Collectors.toList());
                }
                return List.of();
            }
        };

        task.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                loginTimeObservableList.setAll(task.getValue());
                filteredLoginTimeList.setAll(task.getValue());
                userLoginTable.refresh();
                logger.info("Loaded {} login times from database", loginTimeObservableList.size());
            });
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                logger.error("Failed to load login data", task.getException());
            });
        });

        new Thread(task).start();
    }

    private void filterByUsername(String filterText) {
        if (filterText.isEmpty()) {
            filteredLoginTimeList.setAll(loginTimeObservableList);
        } else {
            ObservableList<UserLoginDTO> filteredList = loginTimeObservableList.filtered(userLoginDTO -> {
                String username = userLoginDTO.getUsername() != null ? userLoginDTO.getUsername().toLowerCase() : "";
                String displayName = userLoginDTO.getDisplayName() != null ? userLoginDTO.getDisplayName().toLowerCase() : "";

                return username.contains(filterText.toLowerCase()) || displayName.contains(filterText.toLowerCase());
            });
            filteredLoginTimeList.setAll(filteredList);
        }
        userLoginTable.refresh();
    }

    @FXML
    public void applyFilter(ActionEvent event) {
        LocalDate selectedDate = dateFilter.getValue();
        String currentUsername = usernameFilter.getText().trim();

        if (selectedDate == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid date.");
            return;
        }

        Date sqlDate = Date.valueOf(selectedDate);

        if (currentUsername.isEmpty()) {
            filteredLoginTimeList.clear();
            filteredLoginTimeList.addAll(
                loginTimeObservableList.stream()
                    .filter(loginDTO -> {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String loginDateString = dateFormat.format(loginDTO.getLoginAt());
                        String selectedDateString = dateFormat.format(sqlDate);
                        return loginDateString.equals(selectedDateString);
                    })
                    .toList()
            );
            userLoginTable.setItems(filteredLoginTimeList);
        } else {
            filteredLoginTimeList.clear();
            filteredLoginTimeList.addAll(
                loginTimeObservableList.stream()
                    .filter(loginDTO -> {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String loginDateString = dateFormat.format(loginDTO.getLoginAt());
                        String selectedDateString = dateFormat.format(sqlDate);
                        boolean dateMatches = loginDateString.equals(selectedDateString);

                        String username = loginDTO.getUsername() != null ? loginDTO.getUsername().toLowerCase() : "";
                        String displayName = loginDTO.getDisplayName() != null ? loginDTO.getDisplayName().toLowerCase() : "";

                        boolean usernameMatches = username.contains(currentUsername.toLowerCase()) ||
                            displayName.contains(currentUsername.toLowerCase());

                        return dateMatches && usernameMatches;
                    })
                    .toList()
            );
            userLoginTable.setItems(filteredLoginTimeList);
        }

        userLoginTable.refresh();

        if (filteredLoginTimeList.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Results", "No results found for the given filters.");
        }
    }
}
