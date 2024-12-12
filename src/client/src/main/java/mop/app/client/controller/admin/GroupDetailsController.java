package mop.app.client.controller.admin;

import com.github.javafaker.Faker;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mop.app.client.dao.GroupDAO;
import mop.app.client.dto.UserDTO;
import mop.app.client.util.TableStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupDetailsController {
    private static final Logger logger = LoggerFactory.getLogger(GroupDetailsController.class);

    @FXML
    private Label groupNameLabel;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TableView<UserDTO> groupUserTable;
    @FXML
    private TableColumn<UserDTO, String> usernameCol;
    @FXML
    private TableColumn<UserDTO, String> emailCol;
    @FXML
    private TableColumn<UserDTO, String> displayNameCol;
    @FXML
    private TableColumn<UserDTO, Date> birthdayCol;
    @FXML
    public TableColumn<UserDTO, String> genderCol;
    private ObservableList<UserDTO> userList;
    private final GroupDAO groupDAO;
    private long groupId;

    public GroupDetailsController() {
        userList = FXCollections.observableArrayList();
        groupDAO = new GroupDAO();
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
        logger.info("GroupDetailController initialized for group {}", groupId);
    }

    public void setGroupName(String groupName) {
        groupNameLabel.setText("Group: " + groupName);
    }

    @FXML
    public void initialize() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        birthdayCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableStyle.styleTable(
            groupUserTable,
            List.of(Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER_LEFT),
            "14px"
        );

        groupUserTable.setRowFactory(tv -> {
            TableRow<UserDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });

        groupUserTable.setItems(userList);

        filterComboBox.getItems().addAll("All", "Admin", "User");
        filterComboBox.getSelectionModel().select("All");
        applyFilter();
    }

    @FXML
    private void applyFilter() {
        String selectedRole = filterComboBox.getValue();
        Task<List<UserDTO>> filterTask = new Task<>() {
            @Override
            protected List<UserDTO> call() throws Exception {
                List<UserDTO> filteredList = null;

                switch (selectedRole) {
                    case "Admin":
                        filteredList = groupDAO.getGroupAdmins(groupId);
                        break;
                    case "User":
                        filteredList = groupDAO.getGroupMembers(groupId);
                        break;
                    case "All":
                        filteredList = groupDAO.getAllMembers(groupId);
                        break;
                }
                return filteredList;
            }
        };

        filterTask.setOnSucceeded(event -> {
            List<UserDTO> filteredList = filterTask.getValue();
            if (filteredList != null) {
                ObservableList<UserDTO> observableList = FXCollections.observableArrayList(filteredList);
                groupUserTable.setItems(observableList);
            }
        });

        filterTask.setOnFailed(event -> {
            logger.error("Error occurred while filtering users: ", filterTask.getException());
            groupUserTable.setItems(FXCollections.observableArrayList());
        });

        groupUserTable.refresh();

        new Thread(filterTask).start();
    }
}
