package mop.app.client;

import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import mop.app.client.dao.AuthDAO;
import mop.app.client.network.SocketClient;
import mop.app.client.util.HibernateUtil;
import mop.app.client.util.ViewFactory;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    public static int currentUserId = 3;
    public static SocketClient socketClient;

    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Starting JavaFX Application");

        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.close();
            AuthDAO authDAO = new AuthDAO();
            logger.info("User: {}", authDAO.getUerById(1L));

        } catch (Exception e) {
            logger.error("Error creating SessionFactory: {}", e.getMessage());
            throw new ExceptionInInitializerError(e);
        }

        socketClient = new SocketClient();
        // ViewFactory viewFactory = new ViewFactory();
        // viewFactory.getAdminView();
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/index.fxml"));
        // FXMLLoader fxmlLoader = new
        // FXMLLoader(Client.class.getResource("view/user/home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        try {
            stage.getIcons().add(new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("images/app-icon.png"))));
        } catch (Exception e) {
            logger.error("Failed to load application icon: {}", e.getMessage());
        }

        stage.setTitle("MOP Application");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (socketClient != null) {
            socketClient.close();
        }
        logger.info("Stopping JavaFX Application");
    }
}