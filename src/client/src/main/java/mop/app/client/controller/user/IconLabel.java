package mop.app.client.controller.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;

class CustomLabel extends Label {
    public CustomLabel(String title) {
        setText(title);
        setTextFill(Color.WHITE);
    }
}

public class IconLabel extends HBox {

    public IconLabel(URL icon, String title, String titleSide, String content) {
        setPadding(new Insets(10, 10, 10, 10));
        setSpacing(10);


        VBox vbox = new VBox();

        HBox hbox = new HBox();
        Label titleLabel = new CustomLabel(title);
        titleLabel.setStyle("-fx-font-weight: bold;");
        Label titleSideLabel = new CustomLabel(titleSide);
        titleSideLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 10));
        hbox.setSpacing(10);
        hbox.getChildren().add(titleLabel);
        hbox.getChildren().add(titleSideLabel);
        vbox.getChildren().add(hbox);

        Label contentLabel = new CustomLabel(content);
        if (content != null) vbox.getChildren().add(contentLabel);
        vbox.setAlignment(Pos.CENTER_LEFT);

        if (icon != null) {
            getChildren().add(new CircleImage(icon.toString()));
        }
        if (title != null || titleSide != null) getChildren().add(vbox);
        setPadding(new Insets(10));


    }



    public void addIconLast(URL icon) {
        getChildren().add(new IconLabel(icon, null, null, null));
    }

}
