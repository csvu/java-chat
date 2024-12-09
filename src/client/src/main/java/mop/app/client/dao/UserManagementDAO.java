package mop.app.client.dao;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import mop.app.client.dto.UserDTO;
import mop.app.client.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserManagementDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserManagementDAO.class);

    public UserManagementDAO() {
    }

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

    public void updateUser(UserDTO user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("Updated user: {}", user.getEmail());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Failed to update user: {}", e.getMessage());
        }
    }

    public long newRegistrationCount() {
        long count = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDate today = LocalDate.now();

            count = session.createQuery(
                    "SELECT COUNT(*) FROM UserDTO u WHERE DATE(u.createdAt) = :today", Long.class)
                .setParameter("today", today)
                .uniqueResult();
        } catch (Exception e) {
            logger.error("Failed to get new registration count: {}", e.getMessage());
        }
        return count;
    }

    public long activeUserCount() {
        long count = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            count = session.createQuery("SELECT COUNT(*) FROM UserDTO u WHERE u.isActive = true", Long.class)
                .uniqueResult();
        } catch (Exception e) {
            logger.error("Failed to get active user count: {}", e.getMessage());
        }
        return count;
    }

    public List<Object[]> getNewRegistrationsByMonth(int year) {
        List<Object[]> result = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT MONTH(u.createdAt), COUNT(u) " +
                "FROM UserDTO u " +
                "WHERE YEAR(u.createdAt) = :year " +
                "GROUP BY MONTH(u.createdAt) " +
                "ORDER BY MONTH(u.createdAt)";

            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("year", year);
            result = query.list();
        } catch (Exception e) {
            logger.error("Failed to get new registrations by month for year {}: {}", year, e.getMessage());
        }
        return result;
    }

    public List<Object[]> getActiveUsersByMonth(int year) {
        List<Object[]> result = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT MONTH(o.openAt), COUNT(DISTINCT o.userId) " +
                "FROM OpenTimeDTO o " +
                "WHERE YEAR(o.openAt) = :year " +
                "GROUP BY MONTH(o.openAt) " +
                "ORDER BY MONTH(o.openAt)";

            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("year", year);
            result = query.list();
        } catch (Exception e) {
            logger.error("Failed to get active users by month for year {}: {}", year, e.getMessage());
        }
        return result;
    }

//    public static void main(String[] args) {
//        UserManagementDAO dao = new UserManagementDAO();
//        List<Object[]> result = dao.getActiveUsersByMonth(2024);
//        int[] months = new int[12];
//        Arrays.fill(months, 0);
//        for (Object[] row : result) {
//            int month = (int) row[0];
//            long count = (long) row[1];
//            months[month - 1] = (int) count;
//        }
//
//        for (int i = 0; i < 12; i++) {
//            System.out.println("Month " + (i + 1) + ": " + months[i]);
//        }
//    }
}
