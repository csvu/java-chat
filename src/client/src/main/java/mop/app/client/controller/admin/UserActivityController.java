package mop.app.client.controller.admin;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import mop.app.client.dto.UserActivityDTO;
import mop.app.client.util.AlertDialog;
import mop.app.client.util.TableStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserActivityController {
    private static final Logger logger = LoggerFactory.getLogger(UserActivityController.class);

    @FXML
    private TextField nameFilter;
    @FXML
    private TextField openTimeFilter;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TableView<UserActivityDTO> userActivityTable;
    @FXML
    private TableColumn<UserActivityDTO, String> usernameCol;
    @FXML
    private TableColumn<UserActivityDTO, String> displayNameCol;
    @FXML
    private TableColumn<UserActivityDTO, Timestamp> createdCol;
    @FXML
    private TableColumn<UserActivityDTO, Long> amountOpenTimeCol;
    @FXML
    private TableColumn<UserActivityDTO, Long> amountChatWithFriendCol;
    @FXML
    private TableColumn<UserActivityDTO, Long> amountChatWithGroupCol;

    private ObservableList<UserActivityDTO> userActivities;
    private ObservableList<UserActivityDTO> filteredUserActivities;
    private final UserManagementDAO userManagementDAO;

    public UserActivityController() {
        userActivities = FXCollections.observableArrayList();
        filteredUserActivities = FXCollections.observableArrayList();
        this.userManagementDAO = new UserManagementDAO();
    }

    @FXML
    public void initialize() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        amountOpenTimeCol.setCellValueFactory(new PropertyValueFactory<>("amountOpenTime"));
        amountChatWithFriendCol.setCellValueFactory(new PropertyValueFactory<>("amountChatWithFriend"));
        amountChatWithGroupCol.setCellValueFactory(new PropertyValueFactory<>("amountChatWithGroup"));

        TableStyle.styleTable(
            userActivityTable,
            List.of(Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER, Pos.CENTER, Pos.CENTER),
            "14px"
        );

        userActivityTable.setRowFactory(tv -> {
            TableRow<UserActivityDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });

        Platform.runLater(() -> {
            List<UserActivityDTO> activities = userManagementDAO.getUserActivity();
            userActivities.clear();
            userActivities.addAll(activities);
            userActivityTable.setItems(userActivities);
        });
    }

    private void loadUserActivity() {
        try {
            userActivities.clear();
            List<UserActivityDTO> activities = userManagementDAO.getUserActivity();
            userActivities.addAll(activities);
        } catch (Exception e) {
            logger.error("Error loading user activities", e);
        }
    }

    private Predicate<UserActivityDTO> createOpenTimePredicate(String operator, long value) {
        return switch (operator) {
            case "<" -> activity -> activity.getAmountOpenTime() < value;
            case ">" -> activity -> activity.getAmountOpenTime() > value;
            case "=" -> activity -> activity.getAmountOpenTime() == value;
            default -> activity -> true;
        };
    }

    @FXML
    private void applyFilter() {
        String nameFilterText = nameFilter.getText().trim().toLowerCase();
        String openTimeFilterText = openTimeFilter.getText().trim();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        filteredUserActivities.clear();

        List<UserActivityDTO> sourceActivities;
        if (fromDate != null && toDate != null) {
            sourceActivities = userManagementDAO.getUserOpenInRange(fromDate, toDate);
        } else {
            sourceActivities = userManagementDAO.getUserActivity();
        }

        Predicate<UserActivityDTO> openTimePredicate;
        if (!openTimeFilterText.isEmpty()) {
            Pattern conditionPattern = Pattern.compile("^([<>=])(\\d+)$");
            Matcher matcher = conditionPattern.matcher(openTimeFilterText);

            if (!matcher.matches()) {
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Filter Error",
                    "Please enter a valid filter condition",
                    "Filter condition must be in the format: <, >, = followed by a number"
                );
                return;
            }

            String operator = matcher.group(1);
            long value = Long.parseLong(matcher.group(2));
            openTimePredicate = createOpenTimePredicate(operator, value);
        } else {
            openTimePredicate = activity -> true;
        }

        filteredUserActivities.addAll(
            sourceActivities.stream()
                .filter(activity ->
                    (nameFilterText.isEmpty() ||
                        activity.getUsername().toLowerCase().contains(nameFilterText) ||
                        activity.getDisplayName().toLowerCase().contains(nameFilterText))
                        && openTimePredicate.test(activity)
                )
                .toList()
        );

        userActivityTable.setItems(filteredUserActivities);
    }

    @FXML
    private void clearFilter() {
        nameFilter.clear();
        openTimeFilter.clear();
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);

        userActivityTable.setItems(userActivities);
    }
}
