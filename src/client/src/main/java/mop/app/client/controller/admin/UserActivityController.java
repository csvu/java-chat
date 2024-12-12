package mop.app.client.controller.admin;

import java.sql.Timestamp;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import mop.app.client.dto.FriendDTO;
import mop.app.client.dto.FriendsOfFriendsDTO;
import mop.app.client.dto.LoginTimeDTO;
import mop.app.client.dto.RelationshipDTO;
import mop.app.client.dto.UserDTO;
import mop.app.client.dao.UserManagementDAO;
import mop.app.client.dto.UserLoginDTO;
import mop.app.client.util.TableStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UserActivityController {
    private static final Logger logger = LoggerFactory.getLogger(UserActivityController.class);

    @FXML
    private Label usernameLabel;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private AnchorPane contentPane;
    private TableView<UserLoginDTO> activityTable;
    private TableView<FriendDTO> friendsTable;
    private TableView<FriendsOfFriendsDTO> friendsOfFriendsTable;
    private long userId;
    private String username;
    private UserManagementDAO userManagementDAO;

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

        userManagementDAO = new UserManagementDAO();
        applyFilter();
    }

    private void initializeActivityTable() {
        activityTable = new TableView<>();
        activityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<UserLoginDTO, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(300);

        TableColumn<UserLoginDTO, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(300);

        TableColumn<UserLoginDTO, String> displayNameCol = new TableColumn<>("Display Name");
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        displayNameCol.setPrefWidth(200);

        TableColumn<UserLoginDTO, Timestamp> loginDateCol = new TableColumn<>("Login At");
        loginDateCol.setCellValueFactory(new PropertyValueFactory<>("loginAt"));
        loginDateCol.setPrefWidth(300);

        AnchorPane.setTopAnchor(activityTable, 0.0);
        AnchorPane.setLeftAnchor(activityTable, 0.0);
        AnchorPane.setRightAnchor(activityTable, 0.0);
        AnchorPane.setBottomAnchor(activityTable, 0.0);

        activityTable.getColumns().addAll(usernameCol, displayNameCol, emailCol, loginDateCol);
        TableStyle.styleTable(
            activityTable,
            List.of(Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT),
            "14px"
        );

        activityTable.setRowFactory(param -> {
            TableRow<UserLoginDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });
    }

    private void initializeFriendsTable() {
        friendsTable = new TableView<>();

        TableColumn<FriendDTO, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(200);

        TableColumn<FriendDTO, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(300);

        TableColumn<FriendDTO, String> displayNameCol = new TableColumn<>("Display Name");
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        displayNameCol.setPrefWidth(200);

        TableColumn<FriendDTO, Timestamp> createdAtCol = new TableColumn<>("Created At");
        createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtCol.setPrefWidth(300);

        AnchorPane.setTopAnchor(friendsTable, 0.0);
        AnchorPane.setLeftAnchor(friendsTable, 0.0);
        AnchorPane.setRightAnchor(friendsTable, 0.0);
        AnchorPane.setBottomAnchor(friendsTable, 0.0);

        friendsTable.getColumns().addAll(usernameCol, emailCol, displayNameCol, createdAtCol);
        TableStyle.styleTable(
            friendsTable,
            List.of(Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT),
            "14px"
        );

        friendsTable.setRowFactory(param -> {
            TableRow<FriendDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });
    }

    private void initializeFriendsOfFriendsTable() {
        friendsOfFriendsTable = new TableView<>();
        friendsOfFriendsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<FriendsOfFriendsDTO, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(200);

        TableColumn<FriendsOfFriendsDTO, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(300);

        TableColumn<FriendsOfFriendsDTO, String> displayNameCol = new TableColumn<>("Display Name");
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        displayNameCol.setPrefWidth(200);

        TableColumn<FriendsOfFriendsDTO, String> directFriendCol = new TableColumn<>("Direct Friend");
        directFriendCol.setCellValueFactory(new PropertyValueFactory<>("directFriend"));
        directFriendCol.setPrefWidth(200);

        TableColumn<FriendsOfFriendsDTO, Timestamp> createdAtCol = new TableColumn<>("Created At");
        createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtCol.setPrefWidth(300);

        AnchorPane.setTopAnchor(friendsOfFriendsTable, 0.0);
        AnchorPane.setLeftAnchor(friendsOfFriendsTable, 0.0);
        AnchorPane.setRightAnchor(friendsOfFriendsTable, 0.0);
        AnchorPane.setBottomAnchor(friendsOfFriendsTable, 0.0);

        friendsOfFriendsTable.getColumns().addAll(usernameCol, emailCol, displayNameCol, directFriendCol, createdAtCol);
        TableStyle.styleTable(
            friendsOfFriendsTable,
            List.of(Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT),
            "14px"
        );

        friendsOfFriendsTable.setRowFactory(param -> {
            TableRow<FriendsOfFriendsDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });
    }

    @FXML
    private void applyFilter() {
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
        Task<List<Object[]>> task = new Task<>() {
            @Override
            protected List<Object[]> call() throws Exception {
                return userManagementDAO.getLoginTimeByUserId(userId);
            }
        };

        task.setOnSucceeded(e -> {
            List<Object[]> loginTimeDTOs = task.getValue();
            List<UserLoginDTO> activities = loginTimeDTOs.stream()
                .map(data -> {
                    UserDTO userDTO = (UserDTO) data[0];
                    LoginTimeDTO loginTimeDTO = (LoginTimeDTO) data[1];
                    return new UserLoginDTO(userDTO.getUsername(), userDTO.getEmail(), userDTO.getDisplayName(),
                        loginTimeDTO.getLoginAt());
                })
                .collect(Collectors.toList());

            activityTable.setItems(FXCollections.observableArrayList(activities));
        });

        task.setOnFailed(e -> logger.error("Failed to load activities"));

        new Thread(task).start();
    }

    private void loadFriends() {
        Task<List<Object[]>> task = new Task<>() {
            @Override
            protected List<Object[]> call() throws Exception {
                return userManagementDAO.getFriendByUserId(userId);
            }
        };

        task.setOnSucceeded(e -> {
            List<Object[]> friendsData = task.getValue();
            List<FriendDTO> friends = friendsData.stream()
                .map(data -> {
                    UserDTO userDTO = (UserDTO) data[0];
                    RelationshipDTO relationshipDTO = (RelationshipDTO) data[1];
                    return new FriendDTO(userDTO.getUsername(), userDTO.getEmail(), userDTO.getDisplayName(),
                        relationshipDTO.getCreatedAt());
                })
                .collect(Collectors.toList());

            friendsTable.setItems(FXCollections.observableArrayList(friends));
        });

        task.setOnFailed(e -> logger.error("Failed to load friends"));

        new Thread(task).start();
    }

    private void loadFriendsOfFriends() {
        Task<List<Object[]>> task = new Task<>() {
            @Override
            protected List<Object[]> call() throws Exception {
                return userManagementDAO.getFriendsOfFriendsByUserId(userId);
            }
        };

        task.setOnSucceeded(e -> {
            List<Object[]> friendsOfFriendsData = task.getValue();
            List<FriendsOfFriendsDTO> friendsOfFriends = friendsOfFriendsData.stream()
                .map(data -> {
                    UserDTO userDTO = (UserDTO) data[0];
                    UserDTO directFriendDTO = (UserDTO) data[1];
                    RelationshipDTO relationshipDTO = (RelationshipDTO) data[2];
                    return new FriendsOfFriendsDTO(userDTO.getUsername(), userDTO.getEmail(), userDTO.getDisplayName(),
                        directFriendDTO.getUsername(), relationshipDTO.getCreatedAt());
                })
                .collect(Collectors.toList());

            friendsOfFriendsTable.setItems(FXCollections.observableArrayList(friendsOfFriends));
        });

        task.setOnFailed(e -> logger.error("Failed to load friends of friends"));

        new Thread(task).start();
    }
}
