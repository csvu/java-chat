package mop.app.client.controller.user;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class CircleImage extends ImageView {
    public CircleImage(Image img) {
        setImage(img);
        double radius = 27;
        setFitHeight(radius * 2);
        setFitWidth(radius * 2);
        Circle clip = new Circle(getX() + radius, getY() + radius, radius);
        setClip(clip);
    }
}
