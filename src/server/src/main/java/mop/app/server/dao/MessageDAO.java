package mop.app.server.dao;

import mop.app.server.dto.EnrollmentDTO;
import mop.app.server.util.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MessageDAO {
    private static final Logger logger = LoggerFactory.getLogger(MessageDAO.class);

    public MessageDAO() {}

    public ArrayList<Integer> getUsersInConversation(int conversationId, int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var users = session.createQuery(
                "FROM EnrollmentDTO WHERE conversationId = :conversationId", EnrollmentDTO.class)
                .setParameter("conversationId", conversationId)
                .list();
            ArrayList<Integer> usersInConversation = new ArrayList<>();
            for (EnrollmentDTO user : users) {
                if (user.getUserId() != userId) {
                    usersInConversation.add(user.getUserId());
                }
            }
            return usersInConversation;


        } catch (Exception e) {
            logger.error("Failed to execute query", e);
        }
        return null;
    }

}
