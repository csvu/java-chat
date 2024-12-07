package mop.app.client.dao;

import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import mop.app.client.dto.UserDTO;
import mop.app.client.util.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserManagementDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserManagementDAO.class);

    public UserManagementDAO() {}

    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            users = session.createQuery("FROM UserDTO", UserDTO.class).list();
        } catch (Exception e) {
            logger.error("Failed to get all users: {}", e.getMessage());
        }
        return users;
    }

    public void blockUsers(List<UserDTO> users) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();
                    for (UserDTO user : users) {
                        user.setIsBanned(true);
                        session.merge(user);
                        logger.info("Blocked user: {}", user.getEmail());
                    }
                    session.getTransaction().commit();
                    logger.info("Blocked {} users.", users.size());
                } catch (Exception e) {
                    logger.error("Failed to block users: {}", e.getMessage());
                    throw new Exception("Database error while blocking users.");
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> {
                    logger.info("Users blocked successfully.");
                });
            }

            @Override
            protected void failed() {
                super.failed();
                Platform.runLater(() -> {
                    logger.error("Failed to block users.");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to block users.");
                    alert.showAndWait();
                });
            }
        };
        new Thread(task).start();
    }

    public void unblockUsers(List<UserDTO> users) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();
                    for (UserDTO user : users) {
                        user.setIsBanned(false);
                        session.merge(user);
                        logger.info("Unblocked user: {}", user.getEmail());
                    }
                    session.getTransaction().commit();
                    logger.info("Unblocked {} users.", users.size());
                } catch (Exception e) {
                    logger.error("Failed to unblock users: {}", e.getMessage());
                    throw new Exception("Database error while unblocking users.");
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> {
                    logger.info("Users unblocked successfully.");
                });
            }

            @Override
            protected void failed() {
                super.failed();
                Platform.runLater(() -> {
                    logger.error("Failed to unblock users.");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to unblock users.");
                    alert.showAndWait();
                });
            }
        };
        new Thread(task).start();
    }

    public void deleteUsers(List<UserDTO> users) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();
                    for (UserDTO user : users) {
                        user.setDisplayName("Deleted User");
                        session.merge(user);
                    }
                    session.getTransaction().commit();
                    logger.info("Updated {} users to 'Deleted User'.", users.size());
                } catch (Exception e) {
                    logger.error("Failed to update users: {}", e.getMessage());
                    throw new Exception("Database error while updating users.");
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> {
                    logger.info("Users updated successfully.");
                });
            }

            @Override
            protected void failed() {
                super.failed();
                Platform.runLater(() -> {
                    logger.error("Failed to update users.");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update users.");
                    alert.showAndWait();
                });
            }
        };
        new Thread(task).start();
    }
}
