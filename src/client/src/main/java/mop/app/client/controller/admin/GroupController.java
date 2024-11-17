package mop.app.client.controller.admin;

import com.github.javafaker.Faker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import mop.app.client.model.Group;
import mop.app.client.model.Spam;
import mop.app.client.util.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @FXML
    public TextField filterField;
    @FXML
    public TableView<Group> groupTable;
    @FXML
    public TableColumn<Group, String> groupNameCol;
    @FXML
    public TableColumn<Group, String> creationDateCol;
    @FXML
    public TableColumn<Group, Integer> membersCol;
    @FXML
    public TableColumn<Group, Integer> adminCol;
    @FXML
    public TableColumn<Group, String> detailsCol;

    private ObservableList<Group> groups;

    public GroupController() {
        groups = FXCollections.observableArrayList();

        Faker faker = new Faker(Locale.ENGLISH);
        for (int i = 0; i < 50; i++) {
            String name = faker.team().name();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");
            Date randomDate = faker.date().past(30, TimeUnit.DAYS);
            String date = dateFormat.format(randomDate);
            int members = faker.number().numberBetween(1, 100);
            int admins = members / 8;
            groups.add(new Group(i, name, date, members, admins));
        }

        logger.info("Generated 50 groups");
    }

    @FXML
    public void initialize() {
        groupTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        List<TableColumn<Group, String>> strColumns = List.of(groupNameCol, creationDateCol);
        List<TableColumn<Group, Integer>> intColumns = List.of(membersCol, adminCol);
        groupNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        creationDateCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        membersCol.setCellValueFactory(new PropertyValueFactory<>("memberCount"));
        adminCol.setCellValueFactory(new PropertyValueFactory<>("adminCount"));

        strColumns.forEach(column -> column.setCellFactory(stringTableColumn -> new TableCell<>() {
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
        }));

        intColumns.forEach(column -> column.setCellFactory(intTableColumn -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    setAlignment(Pos.CENTER);
                    setStyle(
                        "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 14px;"
                    );
                }
            }
        }));

        detailsCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Group, String> call(TableColumn<Group, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
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
                            detailButton.setOnAction(event -> groupDetails(getTableRow().getItem()));

                            StackPane stackPane = new StackPane(detailButton);
                            stackPane.setPrefWidth(Double.MAX_VALUE);
                            stackPane.setPrefHeight(Double.MAX_VALUE);
                            setGraphic(stackPane);
                        }
                    }
                };
            }
        });

        groupTable.setItems(groups);

        groupTable.setRowFactory(param -> {
            TableRow<Group> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });
    }

    private void groupDetails(Group group) {
        logger.info("Group details: " + group.getName());
        ViewModel.getInstance().getViewFactory().getSelectedView()
            .set("Group-" + group.getId() + "-" + group.getName());
    }

    public void applyFilter(ActionEvent event) {
    }
}
