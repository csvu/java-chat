package mop.app.client.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class TableStyle {
    public static <T> void styleTable(TableView<T> tableView, List<Pos> alignment, String fontSize) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        for (int i = 0; i < tableView.getColumns().size(); i++) {
            final int columnIndex = i;

            TableColumn<T, ?> column = tableView.getColumns().get(i);
            @SuppressWarnings("unchecked")
            TableColumn<T, Object> typedColumn = (TableColumn<T, Object>) column;

            typedColumn.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        if (item instanceof Timestamp) {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                            setText(sdf.format((Timestamp) item));
                        } else if (item instanceof Date) {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                            setText(sdf.format((Date) item));
                        } else if (item instanceof Boolean) {
                            setText((Boolean) item ? "Blocked" : "Active");
                        } else {
                            setText(item.toString());
                        }

                        setAlignment(alignment.get(columnIndex));
                        setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: " + fontSize + ";");
                    }
                }
            });
        }

        tableView.setStyle("-fx-background-color: transparent;");
    }
}