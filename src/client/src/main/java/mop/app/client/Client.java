package mop.app.client;

import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import mop.app.client.dao.AuthDAO;
import mop.app.client.dao.OpenTimeDAO;
import mop.app.client.dao.RoleDAO;
import mop.app.client.dto.Request;
import mop.app.client.dto.RequestType;
import mop.app.client.dto.Response;
import mop.app.client.dto.UserDTO;
import mop.app.client.network.SocketClient;
import mop.app.client.util.HibernateUtil;
import mop.app.client.util.ObjectMapperConfig;
import mop.app.client.util.PreProcess;
import mop.app.client.util.ViewFactory;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    public static SocketClient socketClient;

    public static UserDTO currentUser;

    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Starting JavaFX Application");

        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.close();
            AuthDAO authDAO = new AuthDAO();
            logger.info("User: {}", authDAO.getUserById(1L));

        } catch (Exception e) {
            logger.error("Error creating SessionFactory: {}", e.getMessage());
            throw new ExceptionInInitializerError(e);
        }

        socketClient = new SocketClient();
        currentUser = PreProcess.loadUserInformation();
        if (currentUser != null) {
            logger.info("User information loaded successfully");
            RoleDAO role = new RoleDAO();
            String roleName = role.getRoleByUserId(currentUser.getUserId());
            AuthDAO authDAO = new AuthDAO();
            authDAO.updateUserStatus(currentUser.getUserId(), true);

            //

            if ("ADMIN".equals(roleName)) {
                // Admin role: Navigate to the admin view
                ViewFactory viewFactory = new ViewFactory();
                viewFactory.getAdminView();
            } else if ("USER".equals(roleName)) {
                // Regular user role: Navigate to the home view
                FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("view/user/home-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                stage.setTitle("MOP Application");
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
                socketClient.start();
            } else {
                logger.error("Could not retrieve user role");
                throw new RuntimeException("Could not retrieve user role");
            }

            OpenTimeDAO openTimeDAO = new OpenTimeDAO();
            boolean isOpenTime = openTimeDAO.addOpenTime(currentUser.getUserId());
            if (!isOpenTime) {
                logger.error("Failed to add open time for user: {}", currentUser.getUserId());
            }
        } else {
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
    }

    public static void registerActivity() throws IOException {
        UserDTO data = UserDTO.builder()
                .userId(Client.currentUser.getUserId())
                .build();

        ObjectMapper mapper = ObjectMapperConfig.getObjectMapper();

        Request req = new Request(RequestType.CHAT_ACTIVITY, data);
        String jsonReq = mapper.writeValueAsString(req);
        socketClient.sendAsyncRequest(jsonReq);

//        String jsonRes =
//        Response res = mapper.readValue(jsonRes, Response.class);
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
        if (currentUser != null) {
            AuthDAO authDAO = new AuthDAO();
            authDAO.updateUserStatus(currentUser.getUserId(), false);
        }
        logger.info("Stopping JavaFX Application");
    }
}