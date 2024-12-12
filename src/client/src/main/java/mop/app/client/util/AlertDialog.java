package mop.app.client.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class AlertDialog {
    public static void showAlertDialog(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Label label = new Label(contentText);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);

        alert.getDialogPane().setContent(label);
        alert.getDialogPane().setPrefWidth(450);
        alert.showAndWait();
    }
}
