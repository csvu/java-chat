package mop.app.client.controller.admin;

import com.github.javafaker.Faker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import mop.app.client.model.Spam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpamController {
    private static final Logger logger = LoggerFactory.getLogger(SpamController.class);

    @FXML
    private TableView<Spam> spamReportsTable;
    @FXML
    private TableColumn<Spam, String> userReportedCol;
    @FXML
    private TableColumn<Spam, String> reportedByCol;
    @FXML
    private TableColumn<Spam, String> reasonCol;
    @FXML
    private TableColumn<Spam, String> reportDateCol;
    @FXML
    private TableColumn<Spam, String> actionCol;

    private ObservableList<Spam> spamReports;

    public SpamController() {
        spamReports = FXCollections.observableArrayList();
        Faker faker = new Faker(Locale.JAPANESE);

        for (int i = 0; i < 50; i++) {
            String reporter = faker.name().fullName();
            String offender = faker.name().fullName();
            String content = faker.lorem().sentence();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");

            Date randomDate = faker.date().past(30, TimeUnit.DAYS);
            String date = dateFormat.format(randomDate);

            spamReports.add(new Spam(i + 1, reporter, offender, content, date));
        }
    }

    @FXML
    public void initialize() {
        // Bind columns to the appropriate data fields in the Spam class
        spamReportsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        List<TableColumn<Spam, String>> columns = List.of(userReportedCol, reportedByCol, reasonCol, reportDateCol);
        userReportedCol.setCellValueFactory(new PropertyValueFactory<>("userReported"));
        reportedByCol.setCellValueFactory(new PropertyValueFactory<>("reportedBy"));
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reportDateCol.setCellValueFactory(new PropertyValueFactory<>("reportDate"));

        columns.forEach(column -> column.setCellFactory(spamStringTableColumn -> new TableCell<>() {
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

        actionCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Spam, String> call(TableColumn<Spam, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Button blockButton = new Button("Block");
                            blockButton.setStyle(
                                "-fx-background-color: #ff3b30; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-border-radius: 20px; " +
                                    "-fx-background-radius: 20px; " +
                                    "-fx-font-size: 14px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 10 20 10 20; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0.0, 0, 1);"
                            );
                            blockButton.setMaxWidth(Double.MAX_VALUE);
                            blockButton.setMaxHeight(Double.MAX_VALUE);
                            blockButton.setOnAction(event -> blockUser(getTableRow().getItem()));

                            StackPane stackPane = new StackPane(blockButton);
                            stackPane.setPrefWidth(Double.MAX_VALUE);
                            stackPane.setPrefHeight(Double.MAX_VALUE);
                            setGraphic(stackPane);
                        }
                    }
                };
            }
        });

        spamReportsTable.setItems(spamReports);

        spamReportsTable.setRowFactory(param -> {
            TableRow<Spam> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });
    }

    private void blockUser(Spam spam) {
        logger.info("Blocking user: " + spam.getUserReported());
    }
}
