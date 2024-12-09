package mop.app.client.dao;

import java.sql.Timestamp;
import mop.app.client.dto.OpenTimeDTO;
import mop.app.client.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenTimeDAO {
    private static final Logger logger = LoggerFactory.getLogger(OpenTimeDAO.class);

    public OpenTimeDAO() {}

    public boolean addOpenTime(long userId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            OpenTimeDTO openTime = OpenTimeDTO.builder()
                .userId(userId)
                .openAt(new Timestamp(System.currentTimeMillis()))
                .build();
            session.persist(openTime);
            transaction.commit();
            logger.info("New open time of user {} added successfully", openTime.getUserId());
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error adding open time", e);
            return false;
        }
    }
}
