package mop.app.client.dao;

import java.sql.Timestamp;
import java.util.List;
import mop.app.client.dto.LoginTimeDTO;
import mop.app.client.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginTimeDAO {
    private static final Logger logger = LoggerFactory.getLogger(LoginTimeDAO.class);

    public LoginTimeDAO() {}

    public boolean addLoginTime(long userId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            LoginTimeDTO loginTime = LoginTimeDTO.builder()
                .userId(userId)
                .loginAt(new Timestamp(System.currentTimeMillis()))
                .build();
            session.persist(loginTime);
            transaction.commit();
            logger.info("New login time of user {} added successfully", loginTime.getUserId());
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error adding login time", e);
            return false;
        }
    }

    public List<Object[]> getAllLoginTimes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT u, l.loginAt " +
                "FROM UserDTO u " +
                "JOIN LoginTimeDTO l ON u.userId = l.userId " +
                "ORDER BY l.loginAt DESC";

            Query<Object[]> query = session.createQuery(hql, Object[].class);
            return query.list();
        } catch (Exception e) {
            logger.error("Error fetching all login times", e);
            return null;
        }
    }

    public List<Object[]> getLoginTimesByUserId(long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT u, l.loginAt " +
                "FROM UserDTO u " +
                "JOIN LoginTimeDTO l ON u.userId = l.userId " +
                "WHERE u.userId = :userId " +
                "ORDER BY l.loginAt DESC";

            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("userId", userId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error fetching login times by user id", e);
            return null;
        }
    }
}
