package mop.app.client.dao;

import mop.app.client.dto.RoleDTO;
import mop.app.client.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleDAO {
    private static final Logger logger = LoggerFactory.getLogger(RoleDAO.class);

    public RoleDAO() {}

    public String getRoleByUserId(long userId) {
        String roleName = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT r.roleName FROM UserDTO u " +
                "JOIN RoleDTO r ON u.roleID = r.roleId " +
                "WHERE u.userId = :userId";
            Query<String> query = session.createQuery(hql, String.class);
            query.setParameter("userId", userId);

            roleName = query.uniqueResult();
        } catch (Exception e) {
            logger.error("Failed to get role by user id: {}", e.getMessage());
        }

        return roleName;
    }
}
