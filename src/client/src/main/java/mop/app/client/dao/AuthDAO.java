package mop.app.client.dao;

import mop.app.client.dto.UserDTO;
import mop.app.client.util.EmailSender;
import mop.app.client.util.HibernateUtil;
import mop.app.client.util.PasswordUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthDAO {
    private static final Logger logger = LoggerFactory.getLogger(AuthDAO.class);

    public AuthDAO() {}

    public UserDTO getUserByEmail(String email) {
        UserDTO user = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            user = session.createQuery(
                    "FROM UserDTO WHERE email = :email", UserDTO.class)
                .setParameter("email", email)
                .uniqueResult();
        } catch (Exception e) {
            logger.error("Failed to execute getUserByEmail query: {}", e.getMessage());
        }
        return user;
    }

    // Check if email already exists
    public boolean isEmailExists(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            UserDTO user = session.createQuery(
                    "FROM UserDTO WHERE email = :email", UserDTO.class)
                .setParameter("email", email)
                .uniqueResult();
            return user == null;
        } catch (Exception e) {
            logger.error("Failed to check email uniqueness: {}", e.getMessage());
        }
        return false;
    }

    // Check if username already exists
    public boolean isUsernameExists(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            UserDTO user = session.createQuery(
                    "FROM UserDTO WHERE username = :username", UserDTO.class)
                .setParameter("username", username)
                .uniqueResult();
            return user == null;
        } catch (Exception e) {
            logger.error("Failed to check username uniqueness: {}", e.getMessage());
        }
        return false;
    }

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

    public UserDTO register(UserDTO user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Failed to register user: {}", e.getMessage());
        }
        return null;
    }

    public void sendResetPasswordEmail(String email) {
        UserDTO user = getUserByEmail(email);
        if (user != null) {
            // Generate random password
            String randomPassword = PasswordUtil.generateRandomPassword();
            // Update user's password
            updateUserPassword(user.getUserId(), randomPassword);
            // Send email to the user
            EmailSender.sendEmailAsync(
                email,
                "Password Reset",
                "Your new password is: " + randomPassword
            );
            logger.info("Reset password email sent to: {}", email);
        }
    }

    private void updateUserPassword(long userId, String randomPassword) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            UserDTO user = session.get(UserDTO.class, userId);
            user.setPassword(PasswordUtil.hash(randomPassword));
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Failed to update user password: {}", e.getMessage());
        }
    }

    public UserDTO getUserById(long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(UserDTO.class, userId);
        } catch (Exception e) {
            logger.error("Failed to get user by id: {}", e.getMessage());
        }
        return null;
    }

    public boolean changePassword(String email, String currentPassword, String newPassword) {
        UserDTO user = getUserByEmail(email);
        if (user != null && PasswordUtil.verify(currentPassword, user.getPassword())) {
            user.setPassword(PasswordUtil.hash(newPassword));
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.merge(user);
                transaction.commit();
                return true;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.error("Failed to change password: {}", e.getMessage());
            }
        }
        return false;
    }

    public UserDTO updateUserStatus(long userId, boolean isActive) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            UserDTO user = session.get(UserDTO.class, userId);
            user.setIsActive(isActive);
            session.merge(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Failed to update user status: {}", e.getMessage());
        }
        return null;
    }

    public boolean isUserExists(UserDTO user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            UserDTO userDTO = session.createQuery(
                    "FROM UserDTO WHERE username = :username AND email = :email AND password = :password", UserDTO.class)
                .setParameter("username", user.getUsername())
                .setParameter("email", user.getEmail())
                .setParameter("password", user.getPassword())
                .uniqueResult();
            if (userDTO != null) {
                return true;
            }
        } catch (Exception e) {
            logger.error("Failed to check user existence: {}", e.getMessage());
        }
        return false;
    }
}
