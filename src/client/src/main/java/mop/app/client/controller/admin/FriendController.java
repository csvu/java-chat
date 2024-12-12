package mop.app.client.controller.admin;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import mop.app.client.dao.UserManagementDAO;
import mop.app.client.dto.FriendStatisticDTO;
import mop.app.client.util.AlertDialog;
import mop.app.client.util.TableStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendController {
    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);

    @FXML
    private TextField usernameFilter;
    @FXML
    private TextField conditionFilter;
    @FXML
    private TableView<FriendStatisticDTO> friendsTable;
    @FXML
    private TableColumn<FriendStatisticDTO, String> userNameCol;
    @FXML
    private TableColumn<FriendStatisticDTO, String> emailCol;
    @FXML
    private TableColumn<FriendStatisticDTO, String> displayNameCol;
    @FXML
    private TableColumn<FriendStatisticDTO, Long> friendCountCol;
    @FXML
    private TableColumn<FriendStatisticDTO, Long> friendsOfFriendsCountCol;
    private ObservableList<FriendStatisticDTO> friends;
    private ObservableList<FriendStatisticDTO> filteredFriends;
    private final UserManagementDAO userManagementDAO;

    public FriendController() {
        friends = FXCollections.observableArrayList();
        filteredFriends = FXCollections.observableArrayList();
        this.userManagementDAO = new UserManagementDAO();
    }

    @FXML
    public void initialize() {
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        friendCountCol.setCellValueFactory(new PropertyValueFactory<>("friendCount"));
        friendsOfFriendsCountCol.setCellValueFactory(new PropertyValueFactory<>("friendsOfFriendsCount"));

        TableStyle.styleTable(
            friendsTable,
            List.of(Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER, Pos.CENTER),
            "14px"
        );

        friendsTable.setRowFactory(tv -> {
            TableRow<FriendStatisticDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });

        friendsTable.setItems(friends);

        loadFriends();

        setupUsernameFilter();
    }

    private void loadFriends() {
        Task<List<FriendStatisticDTO>> loadTask = new Task<>() {
            @Override
            protected List<FriendStatisticDTO> call() throws Exception {
                return userManagementDAO.getUserStatistics();
            }
        };

        loadTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                friends.clear();
                friends.addAll(loadTask.getValue());
            });
        });

        loadTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                logger.error("Failed to load friends", loadTask.getException());
            });
        });

        new Thread(loadTask).start();
    }

    private void setupUsernameFilter() {
        usernameFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            Task<List<FriendStatisticDTO>> filterTask = new Task<>() {
                @Override
                protected List<FriendStatisticDTO> call() throws Exception {
                    if (newValue == null || newValue.trim().isEmpty()) {
                        return friends;
                    }

                    String filterLower = newValue.toLowerCase().trim();
                    return friends.stream()
                        .filter(friend ->
                            friend.getUsername().toLowerCase().contains(filterLower) ||
                                friend.getDisplayName().toLowerCase().contains(filterLower)
                        )
                        .collect(Collectors.toList());
                }
            };

            filterTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    filteredFriends.clear();
                    filteredFriends.addAll(filterTask.getValue());
                    friendsTable.setItems(filteredFriends);
                });
            });

            new Thread(filterTask).start();
        });
    }

    @FXML
    private void applyFilter() {
        Task<List<FriendStatisticDTO>> filterTask = new Task<>() {
            @Override
            protected List<FriendStatisticDTO> call() throws Exception {
                String conditionText = conditionFilter.getText().trim();

                if (conditionText.isEmpty()) {
                    return friends;
                }

                Pattern conditionPattern = Pattern.compile("^([<>=])?(\\d+)$");
                Matcher matcher = conditionPattern.matcher(conditionText);

                if (!matcher.matches()) {
                    AlertDialog.showAlertDialog(
                        Alert.AlertType.ERROR,
                        "Filter Error",
                        "Please enter a valid filter condition",
                        "Filter condition must be in the format: <, >, = followed by a number"
                    );
                    return friends;
                }

                if (!matcher.group(1).contains("<") && !matcher.group(1).contains(">") &&
                    !matcher.group(1).contains("=")) {
                    AlertDialog.showAlertDialog(
                        Alert.AlertType.ERROR,
                        "Filter Error",
                        "Please enter a valid filter condition",
                        "Filter condition must be in the format: <, >, = followed by a number"
                    );
                    return friends;
                }

                String operator = matcher.group(1) != null ? matcher.group(1) : "=";
                long value = Long.parseLong(matcher.group(2));

                Predicate<FriendStatisticDTO> condition = createFriendCountPredicate(operator, value);

                String usernameLower = usernameFilter.getText().toLowerCase().trim();
                return friends.stream()
                    .filter(friend ->
                        condition.test(friend) &&
                            (usernameLower.isEmpty() ||
                                friend.getUsername().toLowerCase().contains(usernameLower) ||
                                friend.getDisplayName().toLowerCase().contains(usernameLower))
                    )
                    .collect(Collectors.toList());
            }
        };

        filterTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                filteredFriends.clear();
                filteredFriends.addAll(filterTask.getValue());
                friendsTable.setItems(filteredFriends);
            });
        });

        filterTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Filter Error",
                    "Please enter a valid filter condition",
                    "Filter condition must be in the format: <, >, = followed by a number"
                );
            });
        });

        new Thread(filterTask).start();
    }

    private Predicate<FriendStatisticDTO> createFriendCountPredicate(String operator, long value) {
        switch (operator) {
            case ">":
                return friend -> friend.getFriendCount() > value;
            case "<":
                return friend -> friend.getFriendCount() < value;
            case "=":
            default:
                return friend -> friend.getFriendCount() == value;
        }
    }
}