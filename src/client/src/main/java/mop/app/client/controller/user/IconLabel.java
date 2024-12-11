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
import lombok.Getter;

import java.net.URL;

class CustomLabel extends Label {
    public CustomLabel(String title) {
        setText(title);
        setTextFill(Color.WHITE);
    }
}

public class IconLabel extends HBox {
    private VBox vbox;

    @Getter
    private Label titleLabel;
    private HBox hbox;
    private Label titleSideLabel;
    private Label contentLabel;
    private CircleImage circleImage;

    public IconLabel(URL icon, String title, String titleSide, String content) {
        setPadding(new Insets(10, 10, 10, 10));
        setSpacing(10);


        vbox = new VBox();

        hbox = new HBox();
        titleLabel = new CustomLabel(title);
        titleLabel.setStyle("-fx-font-weight: bold;");
        titleSideLabel = new CustomLabel(titleSide);
        titleSideLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 10));
        hbox.setSpacing(10);
        hbox.getChildren().add(titleLabel);
        hbox.getChildren().add(titleSideLabel);
        vbox.getChildren().add(hbox);

        contentLabel = new CustomLabel(content);
        if (content != null) vbox.getChildren().add(contentLabel);
        vbox.setAlignment(Pos.CENTER_LEFT);

        if (icon != null) {
            circleImage = new CircleImage(icon.toString());
            getChildren().add(circleImage);
        }
        if (title != null || titleSide != null) getChildren().add(vbox);
        setPadding(new Insets(10));


    }

    public void update(URL icon, String title, String titleSide, String content) {
        if (icon != null) {
            if (circleImage != null) circleImage.update(icon.toString());
        } else {
            if (circleImage != null) circleImage.update(null);
        }
        if (title != null) {
            titleLabel.setText(title);
        }
        if (titleSide != null) {
            titleSideLabel.setText(titleSide);
        }
        if (content != null) {
            contentLabel.setText(content);
        }
    }



    public void addIconLast(URL icon) {
        getChildren().add(new IconLabel(icon, null, null, null));
    }

}
