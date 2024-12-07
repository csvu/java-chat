package mop.app.server.dao;

import mop.app.server.dto.UserDTO;
import mop.app.server.util.HibernateUtil;
import mop.app.server.util.PasswordUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthDAO {
    private static final Logger logger = LoggerFactory.getLogger(AuthDAO.class);

    public AuthDAO() {}

    public UserDTO login(String usernameOrEmail, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            UserDTO user = session.createQuery(
                "FROM UserDTO WHERE username = :identifier OR email = :identifier", UserDTO.class)
                .setParameter("identifier", usernameOrEmail)
                .uniqueResult();

            if (user != null && PasswordUtil.verify(password, user.getPassword())) {
                return user;
            }
        } catch (Exception e) {
            logger.error("Failed to execute login query", e);
        }
        return null;
    }

    public UserDTO getUerById(long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(UserDTO.class, userId);
        } catch (Exception e) {
            logger.error("Failed to get user by id: {}", e.getMessage());
        }
        return null;
    }
}
