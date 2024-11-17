package mop.app.client.controller.user;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class CircleImage extends ImageView {
    public CircleImage(String img) {
        super(img);
        double radius = 16;
        setFitHeight(radius * 2);
        setFitWidth(radius * 2);
        Circle clip = new Circle(getX() + radius, getY() + radius, radius);
        setClip(clip);

    }
}
