package mop.app.client.controller.admin;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import mop.app.client.dao.GroupDAO;
import mop.app.client.dto.ConversationDTO;
import mop.app.client.util.AlertDialog;
import mop.app.client.util.TableStyle;
import mop.app.client.util.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @FXML
    private TextField filterField;
    @FXML
    private TableView<ConversationDTO> groupTable;
    @FXML
    private TableColumn<ConversationDTO, String> groupNameCol;
    @FXML
    private TableColumn<ConversationDTO, Timestamp> createdAtCol;
    @FXML
    private TableColumn<ConversationDTO, Long> membersCol;
    @FXML
    private TableColumn<ConversationDTO, Long> adminCol;
    @FXML
    private TableColumn<ConversationDTO, String> detailsCol;
    private ObservableList<ConversationDTO> groups;
    private ObservableList<ConversationDTO> filteredGroups;
    private final GroupDAO groupDAO;

    public GroupController() {
        groups = FXCollections.observableArrayList();
        filteredGroups = FXCollections.observableArrayList();
        groupDAO = new GroupDAO();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilterField();
        loadGroups();
    }

    private void setupTableColumns() {
        groupNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        membersCol.setCellValueFactory(cellData ->
            Bindings.createObjectBinding(() ->
                groupDAO.countMembers(cellData.getValue().getConversationId())
            )
        );
        adminCol.setCellValueFactory(cellData ->
            Bindings.createObjectBinding(() ->
                groupDAO.countAdmins(cellData.getValue().getConversationId())
            )
        );
        TableStyle.styleTable(
            groupTable,
            List.of(Pos.CENTER_LEFT, Pos.CENTER_LEFT, Pos.CENTER, Pos.CENTER, Pos.CENTER),
            "14px"
        );
        setupDetailsColumn();

        groupTable.setRowFactory(tv -> {
            TableRow<ConversationDTO> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });

        groupTable.setItems(groups);
    }

    private void setupDetailsColumn() {
        detailsCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button detailButton = createDetailsButton();
                    StackPane stackPane = new StackPane(detailButton);
                    stackPane.setPrefWidth(Double.MAX_VALUE);
                    stackPane.setPrefHeight(Double.MAX_VALUE);
                    setGraphic(stackPane);
                }
            }

            private Button createDetailsButton() {
                ConversationDTO group = getTableRow().getItem();
                Button detailButton = new Button("Details");
                detailButton.setStyle(
                    "-fx-background-color: #0084ff; " +
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
                detailButton.setOnAction(event -> groupDetails(group));
                return detailButton;
            }
        });
    }

    private void setupFilterField() {
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterGroups(newValue.trim());
        });
    }

    private void filterGroups(String groupName) {
        if (groupName.isEmpty()) {
            groupTable.setItems(groups);
        } else {
            filteredGroups.clear();
            filteredGroups.addAll(
                groups.stream()
                    .filter(group -> group.getName().toLowerCase().contains(groupName.toLowerCase()))
                    .toList()
            );
            groupTable.setItems(filteredGroups);
        }
        groupTable.refresh();
    }

    private void loadGroups() {
        Task<List<ConversationDTO>> task = new Task<>() {
            @Override
            protected List<ConversationDTO> call() {
                return groupDAO.getAllGroups();
            }
        };

        task.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                List<ConversationDTO> loadedGroups = task.getValue();
                if (loadedGroups != null) {
                    groups.setAll(loadedGroups);
                    groupTable.setItems(groups);
                    groupTable.refresh();
                    logger.info("Loaded {} groups from database", groups.size());
                } else {
                    logger.error("Failed to load groups");
                    AlertDialog.showAlertDialog(
                        Alert.AlertType.ERROR,
                        "Failed to load groups",
                        "Failed to load groups",
                        task.getMessage()
                    );
                }
            });
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                logger.error("Failed to load groups", task.getException());
                AlertDialog.showAlertDialog(
                    Alert.AlertType.ERROR,
                    "Failed to load groups",
                    "Failed to load groups",
                    task.getMessage()
                );
            });
        });

        new Thread(task).start();
    }

    private void groupDetails(ConversationDTO group) {
        logger.info("Group details: " + group.getName());
        ViewModel.getInstance().getViewFactory().getSelectedView()
            .set("Group-" + group.getConversationId() + "-" + group.getName());
    }
}