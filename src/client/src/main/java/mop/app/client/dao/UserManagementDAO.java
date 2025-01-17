package mop.app.client.dao;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import mop.app.client.dto.FriendStatisticDTO;
import mop.app.client.dto.UserActivityDTO;
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

    public List<UserDTO> getActiveUsers() {
        List<UserDTO> users = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            users = session.createQuery("FROM UserDTO u WHERE u.isActive = true", UserDTO.class).list();
        } catch (Exception e) {
            logger.error("Failed to get active users: {}", e.getMessage());
        }
        return users;
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

    public long userLoginToday() {
        long count = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDate today = LocalDate.now();

            count = session.createQuery(
                    "SELECT COUNT(DISTINCT o.userId) FROM OpenTimeDTO o WHERE DATE(o.openAt) = :today", Long.class)
                .setParameter("today", today)
                .uniqueResult();
        } catch (Exception e) {
            logger.error("Failed to get user login count for today: {}", e.getMessage());
        }
        return count;
    }

    public List<Object[]> getLoginTimeByUserId(long userId) {
        List<Object[]> loginTimes = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT u, l FROM UserDTO u " +
                "JOIN LoginTimeDTO l ON u.id = l.userId " +
                "WHERE l.userId = :userId " +
                "ORDER BY l.loginAt DESC";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("userId", userId);
            loginTimes = query.list();
        } catch (Exception e) {
            logger.error("Failed to get login times for user {}: {}", userId, e.getMessage());
        }
        return loginTimes;
    }

    public List<Object[]> getOpenTimeByUserId(long userId) {
        List<Object[]> openTimes = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT u, o FROM UserDTO u " +
                "JOIN OpenTimeDTO o ON u.id = o.userId " +
                "WHERE o.userId = :userId " +
                "ORDER BY o.openAt DESC";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("userId", userId);
            openTimes = query.list();
        } catch (Exception e) {
            logger.error("Failed to get open times for user {}: {}", userId, e.getMessage());
        }
        return openTimes;
    }

    public List<Object[]> getFriendByUserId(long userId) {
        List<Object[]> friends = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT u, r FROM UserDTO u " +
                "JOIN RelationshipDTO r ON u.id = r.userId2 " +
                "JOIN RelationshipTypeDTO rt ON r.status = rt.id " +
                "WHERE r.userId1 = :userId AND rt.typeName = 'FRIEND'" +
                "ORDER BY r.createdAt DESC";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("userId", userId);
            friends = query.list();
        } catch (Exception e) {
            logger.error("Failed to get friends for user {}: {}", userId, e.getMessage());
        }
        return friends;
    }

    public List<Object[]> getFriendsOfFriendsByUserId(long userId) {
        List<Object[]> friendsOfFriends = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT u, directFriend, r1 FROM UserDTO u " +
                "JOIN RelationshipDTO r1 ON u.id = r1.userId2 " +
                "JOIN RelationshipTypeDTO rt1 ON r1.status = rt1.id " +
                "JOIN UserDTO directFriend ON r1.userId1 = directFriend.id " +
                "JOIN RelationshipDTO r2 ON r1.userId1 = r2.userId2 " +
                "JOIN RelationshipTypeDTO rt2 ON r2.status = rt2.id " +
                "WHERE r2.userId1 = :userId " +
                "AND rt1.typeName = 'FRIEND' " +
                "AND rt2.typeName = 'FRIEND' " +
                "AND u.id != :userId " +
                "AND u.id NOT IN (" +
                "SELECT f.userId2 FROM RelationshipDTO f " +
                "JOIN RelationshipTypeDTO ft ON f.status = ft.id " +
                "WHERE f.userId1 = :userId AND ft.typeName = 'FRIEND'" +
                ")";

            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("userId", userId);
            friendsOfFriends = query.list();
        } catch (Exception e) {
            logger.error("Failed to get friends of friends for user {}: {}", userId, e.getMessage());
        }
        return friendsOfFriends;
    }

    public long totalFriends(long userId) {
        long friends = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(r) FROM RelationshipDTO r " +
                "JOIN RelationshipTypeDTO rt ON r.status = rt.id " +
                "WHERE r.userId1 = :userId AND rt.typeName = 'FRIEND'";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("userId", userId);
            friends = query.uniqueResult();
        } catch (Exception e) {
            logger.error("Failed to get total friends for user {}: {}", userId, e.getMessage());
        }
        return friends;
    }

    public long totalFriendsOfFriends(long userId) {
        long friendsOfFriends = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(DISTINCT u) FROM UserDTO u " +
                "JOIN RelationshipDTO r1 ON u.id = r1.userId2 " +
                "JOIN RelationshipTypeDTO rt1 ON r1.status = rt1.id " +
                "JOIN RelationshipDTO r2 ON r1.userId1 = r2.userId2 " +
                "JOIN RelationshipTypeDTO rt2 ON r2.status = rt2.id " +
                "WHERE r2.userId1 = :userId " +
                "AND rt1.typeName = 'FRIEND' " +
                "AND rt2.typeName = 'FRIEND' " +
                "AND u.id != :userId " +
                "AND u.id NOT IN (" +
                "SELECT f.userId2 FROM RelationshipDTO f " +
                "JOIN RelationshipTypeDTO ft ON f.status = ft.id " +
                "WHERE f.userId1 = :userId AND ft.typeName = 'FRIEND'" +
                ")";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("userId", userId);
            friendsOfFriends = query.uniqueResult();
        } catch (Exception e) {
            logger.error("Failed to get total friends of friends for user {}: {}", userId, e.getMessage());
        }
        return friendsOfFriends;
    }

    public List<FriendStatisticDTO> getUserStatistics() {
        List<FriendStatisticDTO> userStatistics = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT " +
                "u.username, " +
                "u.email, " +
                "u.displayName, " +
                "(SELECT COUNT(r1) FROM RelationshipDTO r1 " +
                "JOIN RelationshipTypeDTO rt1 ON r1.status = rt1.id " +
                "WHERE r1.userId1 = u.userId AND rt1.typeName = 'FRIEND'), " +
                "(SELECT COUNT(DISTINCT u2) FROM UserDTO u2 " +
                "JOIN RelationshipDTO r1 ON u2.id = r1.userId2 " +
                "JOIN RelationshipTypeDTO rt1 ON r1.status = rt1.id " +
                "JOIN RelationshipDTO r2 ON r1.userId1 = r2.userId2 " +
                "JOIN RelationshipTypeDTO rt2 ON r2.status = rt2.id " +
                "WHERE r2.userId1 = u.userId " +
                "AND rt1.typeName = 'FRIEND' " +
                "AND rt2.typeName = 'FRIEND' " +
                "AND u2.id != u.userId " +
                "AND u2.id NOT IN (" +
                "SELECT f.userId2 FROM RelationshipDTO f " +
                "JOIN RelationshipTypeDTO ft ON f.status = ft.id " +
                "WHERE f.userId1 = u.userId AND ft.typeName = 'FRIEND')) " +
                "FROM UserDTO u";

            List<Object[]> results = session.createQuery(hql, Object[].class).list();

            for (Object[] result : results) {
                FriendStatisticDTO statistic = new FriendStatisticDTO(
                    (String) result[0],
                    (String) result[1],
                    (String) result[2],
                    (Long) result[3],
                    (Long) result[4]
                );
                userStatistics.add(statistic);
            }
        } catch (Exception e) {
            logger.error("Failed to get user statistics: {}", e.getMessage());
        }
        return userStatistics;
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

    public List<UserActivityDTO> getUserActivity() {
        List<UserActivityDTO> userActivity = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT u.username, u.displayName, u.createdAt, " +
                "COUNT(DISTINCT ot.instance) as totalOpenTime, " +
                "COUNT(DISTINCT CASE WHEN c.typeId = 1 THEN m.conversationId END) as friendChats, " +
                "COUNT(DISTINCT CASE WHEN c.typeId = 2 THEN m.conversationId END) as groupChats " +
                "FROM UserDTO u " +
                "LEFT JOIN OpenTimeDTO ot ON ot.userId = u.userId " +
                "LEFT JOIN MessageDTO m ON m.userId = u.userId " +
                "LEFT JOIN ConversationDTO c ON c.conversationId = m.conversationId " +
                "GROUP BY u.username, u.displayName, u.createdAt";

            List<Object[]> results = session.createQuery(hql, Object[].class).getResultList();

            for (Object[] result : results) {
                UserActivityDTO activity = new UserActivityDTO();
                activity.setUsername((String) result[0]);
                activity.setDisplayName((String) result[1]);
                activity.setCreatedAt((Timestamp) result[2]);
                activity.setAmountOpenTime((Long) result[3]);
                activity.setAmountChatWithFriend((Long) result[4]);
                activity.setAmountChatWithGroup((Long) result[5]);
                userActivity.add(activity);
            }
        } catch (Exception e) {
            logger.error("Failed to get user activity: {}", e.getMessage());
        }
        return userActivity;
    }

    public List<UserActivityDTO> getUserOpenInRange(LocalDate from, LocalDate to) {
        List<UserActivityDTO> userActivity = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT u.username, u.displayName, u.createdAt, " +
                "COUNT(DISTINCT ot.instance) as totalOpenTime, " +
                "COUNT(DISTINCT CASE WHEN c.typeId = 1 THEN m.conversationId END) as friendChats, " +
                "COUNT(DISTINCT CASE WHEN c.typeId = 2 THEN m.conversationId END) as groupChats " +
                "FROM UserDTO u " +
                "LEFT JOIN OpenTimeDTO ot ON ot.userId = u.userId " +
                "LEFT JOIN MessageDTO m ON m.userId = u.userId " +
                "LEFT JOIN ConversationDTO c ON c.conversationId = m.conversationId " +
                "WHERE DATE(ot.openAt) BETWEEN :from AND :to " +
                "GROUP BY u.username, u.displayName, u.createdAt";

            List<Object[]> results = session.createQuery(hql, Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();

            for (Object[] result : results) {
                UserActivityDTO activity = new UserActivityDTO();
                activity.setUsername((String) result[0]);
                activity.setDisplayName((String) result[1]);
                activity.setCreatedAt((Timestamp) result[2]);
                activity.setAmountOpenTime((Long) result[3]);
                activity.setAmountChatWithFriend((Long) result[4]);
                activity.setAmountChatWithGroup((Long) result[5]);
                userActivity.add(activity);
            }
        } catch (Exception e) {
            logger.error("Failed to get user activity: {}", e.getMessage());
        }
        return userActivity;
    }

    public static void main(String[] args) {
        UserManagementDAO dao = new UserManagementDAO();
//        List<Object[]> friendsOfFriendsData = dao.getFriendsOfFriendsByUserId(9);
//        for (Object[] data : friendsOfFriendsData) {
//            UserDTO friendOfFriend = (UserDTO) data[0];
//            UserDTO directFriend = (UserDTO) data[1];
//            System.out.println("Friend of Friend: " + friendOfFriend.getUsername() +
//                ", Direct Friend: " + directFriend.getUsername());
//        }

        List<FriendStatisticDTO> userStatistics = dao.getUserStatistics();
        for (FriendStatisticDTO statistic : userStatistics) {
            logger.info(statistic.toString());
        }

        List<UserActivityDTO> userActivity = dao.getUserActivity();
        for (UserActivityDTO activity : userActivity) {
            logger.info(activity.toString());
        }

        List<UserActivityDTO> userOpenInRange = dao.getUserOpenInRange(LocalDate.now().minusDays(7), LocalDate.now());
        for (UserActivityDTO activity : userOpenInRange) {
            logger.info(activity.toString());
        }
    }
}
