package mop.app.client.controller.user;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mop.app.client.Client;

import java.util.Objects;

public class ModalUtil extends Stage {
    public ModalUtil(MouseEvent e) {
        super();

        setResizable(false);
        initModality(Modality.WINDOW_MODAL);
        initOwner(((Node)e.getSource()).getScene().getWindow() );
        try {
            getIcons().add(new Image(Objects.requireNonNull(Client.class.getResourceAsStream("images/app-icon.png"))));
        } catch (Exception ex) {
            System.out.println("Failed to load application icon" +  ex);
        }

        setTitle("MOP Application");
    }

    public void setScene(Parent root) {
        setScene(new Scene(root));
    }
}
