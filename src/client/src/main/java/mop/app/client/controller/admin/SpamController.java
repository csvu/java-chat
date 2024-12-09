package mop.app.client.controller.admin;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
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
import javafx.util.StringConverter;
import mop.app.client.dao.AuthDAO;
import mop.app.client.dao.ReportDAO;
import mop.app.client.dao.UserManagementDAO;
import mop.app.client.dto.ReportDTO;
import mop.app.client.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpamController {
    private static final Logger logger = LoggerFactory.getLogger(SpamController.class);

    @FXML
    public TextField usernameFilter;
    @FXML
    public DatePicker dateFilter;
    @FXML
    private TableView<ReportDTO> spamReportsTable;
    @FXML
    private TableColumn<ReportDTO, Long> reportedByCol;
    @FXML
    private TableColumn<ReportDTO, Long> userReportedCol;
    @FXML
    private TableColumn<ReportDTO, Timestamp> reportDateCol;
    @FXML
    private TableColumn<ReportDTO, String> actionCol;
    private ObservableList<ReportDTO> spamReports;
    private ObservableList<ReportDTO> filteredSpamReports;
    private final ReportDAO reportDAO;
    private final AuthDAO authDAO;
    private final UserManagementDAO userManagementDAO;

    public SpamController() {
        spamReports = FXCollections.observableArrayList();
        filteredSpamReports = FXCollections.observableArrayList();
        reportDAO = new ReportDAO();
        authDAO = new AuthDAO();
        userManagementDAO = new UserManagementDAO();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableView();
        loadReports();
        setupUsernameFilter();
        setUpDateFilter();
    }

    private void setUpDateFilter() {
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
                        showError("Invalid Date Format",
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
                validateDateOfBirth();
            }
        });
    }

    private void validateDateOfBirth() {
        LocalDate selectedDate = dateFilter.getValue();

        if (selectedDate != null) {
            LocalDate currentDate = LocalDate.now();

            if (selectedDate.isAfter(currentDate)) {
                showError("Invalid Date", "Birth date cannot be in the future.");
                dateFilter.setValue(null);
            }
        }
    }

    private void setupUsernameFilter() {
        usernameFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filterReports(newValue.trim());
        });
    }

    private void filterReports(String username) {
        if (username.isEmpty()) {
            spamReportsTable.setItems(spamReports);
        } else {
            filteredSpamReports.clear();
            filteredSpamReports.addAll(
                spamReports.stream()
                    .filter(report -> {
                        UserDTO reportedByUser = authDAO.getUserById(report.getUserId1());
                        UserDTO reportedUser = authDAO.getUserById(report.getUserId2());

                        boolean reportedByMatch = reportedByUser != null &&
                            reportedByUser.getUsername().toLowerCase().contains(username.toLowerCase());

                        boolean reportedUserMatch = reportedUser != null &&
                            reportedUser.getUsername().toLowerCase().contains(username.toLowerCase());

                        return reportedByMatch || reportedUserMatch;
                    })
                    .toList()
            );
            spamReportsTable.setItems(filteredSpamReports);
        }
        spamReportsTable.refresh();
    }

    private void loadReports() {
        Task<List<ReportDTO>> task = new Task<>() {
            @Override
            protected List<ReportDTO> call() {
                List<ReportDTO> reportDTOList = reportDAO.getAllReports();
                return reportDTOList != null
                    ? reportDTOList.stream()
                    .map(ReportDTO::new)
                    .collect(Collectors.toList())
                    : List.of();
            }
        };

        task.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                spamReports.setAll(task.getValue());
                spamReportsTable.setItems(spamReports);
                spamReportsTable.refresh();
                logger.info("Loaded {} reports from database", spamReports.size());
            });
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                logger.error("Failed to load reports", task.getException());
                showAlert(Alert.AlertType.ERROR, "Failed to load reports");
            });
        });

        new Thread(task).start();
    }

    private void setupTableColumns() {
        reportedByCol.setCellValueFactory(new PropertyValueFactory<>("userId1"));
        userReportedCol.setCellValueFactory(new PropertyValueFactory<>("userId2"));
        reportDateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        reportedByCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    UserDTO user = authDAO.getUserById(item);
                    setText(user != null ? user.getUsername() : "Unknown");
                    setAlignment(Pos.CENTER_LEFT);
                    setStyle(
                        "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 14px;"
                    );
                }
            }
        });

        userReportedCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    UserDTO user = authDAO.getUserById(item);
                    setText(user != null ? user.getUsername() : "Unknown");
                    setAlignment(Pos.CENTER_LEFT);
                    setStyle(
                        "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 14px;"
                    );
                }
            }
        });

        reportDateCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    setText(dateFormat.format(item));
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

    private void setupTableView() {
        spamReportsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        actionCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ReportDTO report = getTableRow().getItem();
                    if (report != null) {
                        UserDTO reportedUser = authDAO.getUserById(report.getUserId2());
                        logger.info("Reported user: {}", reportedUser);

                        if (reportedUser != null) {
                            Button blockButton = new Button(reportedUser.getIsBanned() ? "Unblock" : "Block");
                            blockButton.setStyle(
                                reportedUser.getIsBanned()
                                    ? "-fx-background-color: #4CAF50; -fx-text-fill: white;"
                                    : "-fx-background-color: #ff3b30; -fx-text-fill: white;"
                            );
                            blockButton.setStyle(
                                blockButton.getStyle() +
                                    "-fx-border-radius: 20px; " +
                                    "-fx-background-radius: 20px; " +
                                    "-fx-font-size: 14px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 10 20 10 20; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0.0, 0, 1);"
                            );
                            blockButton.setMaxWidth(Double.MAX_VALUE);
                            blockButton.setMaxHeight(Double.MAX_VALUE);

                            blockButton.setOnAction(event -> {
                                List<UserDTO> userToBlock = List.of(reportedUser);
                                if (reportedUser.getIsBanned()) {
                                    unblockUser(userToBlock);
                                } else {
                                    blockUser(userToBlock);
                                }
                            });

                            StackPane stackPane = new StackPane(blockButton);
                            stackPane.setPrefWidth(Double.MAX_VALUE);
                            stackPane.setPrefHeight(Double.MAX_VALUE);
                            setGraphic(stackPane);
                        }
                    }
                }
            }
        });

        spamReportsTable.setItems(spamReports);

        spamReportsTable.setRowFactory(param -> {
            TableRow<ReportDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });
    }

    private void blockUser(List<UserDTO> users) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                userManagementDAO.blockUsers(users);
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    spamReportsTable.refresh();
                    logger.info("Users blocked successfully.");
                    showAlert(Alert.AlertType.INFORMATION, "Users blocked successfully.");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    logger.error("Failed to block users.");
                    showAlert(Alert.AlertType.ERROR, "Failed to block users.");
                });
            }
        };
        new Thread(task).start();
    }

    private void unblockUser(List<UserDTO> users) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                userManagementDAO.unblockUsers(users);
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    spamReportsTable.refresh();
                    logger.info("Users unblocked successfully.");
                    showAlert(Alert.AlertType.INFORMATION, "Users unblocked successfully.");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    logger.error("Failed to unblock users.");
                    showAlert(Alert.AlertType.ERROR, "Failed to unblock users.");
                });
            }
        };
        new Thread(task).start();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void applyFilter(ActionEvent event) {
        LocalDate selectedDate = dateFilter.getValue();
        String currentUsername = usernameFilter.getText().trim();

        if (selectedDate == null) {
            showError("Validation Error", "Please enter a valid date.");
            return;
        }

        Date sqlDate = Date.valueOf(selectedDate);

        if (currentUsername.isEmpty()) {
            filteredSpamReports.clear();
            filteredSpamReports.addAll(
                spamReports.stream()
                    .filter(report -> {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String reportDateString = dateFormat.format(report.getCreatedAt());
                        String selectedDateString = dateFormat.format(sqlDate);
                        return reportDateString.equals(selectedDateString);
                    })
                    .toList()
            );
            spamReportsTable.setItems(filteredSpamReports);
        } else {
            filteredSpamReports.clear();
            filteredSpamReports.addAll(
                spamReports.stream()
                    .filter(report -> {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String reportDateString = dateFormat.format(report.getCreatedAt());
                        String selectedDateString = dateFormat.format(sqlDate);
                        boolean dateMatches = reportDateString.equals(selectedDateString);

                        UserDTO reportedByUser = authDAO.getUserById(report.getUserId1());
                        UserDTO reportedUser = authDAO.getUserById(report.getUserId2());

                        boolean reportedByMatch = reportedByUser != null &&
                            reportedByUser.getUsername().toLowerCase().contains(currentUsername.toLowerCase());

                        boolean reportedUserMatch = reportedUser != null &&
                            reportedUser.getUsername().toLowerCase().contains(currentUsername.toLowerCase());

                        return dateMatches && (reportedByMatch || reportedUserMatch);
                    })
                    .toList()
            );
            spamReportsTable.setItems(filteredSpamReports);
        }

        spamReportsTable.refresh();

        if (filteredSpamReports.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No reports found matching the selected criteria.");
        }
    }
}