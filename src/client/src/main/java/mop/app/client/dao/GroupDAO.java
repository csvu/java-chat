package mop.app.client.dao;

import java.util.List;
import mop.app.client.dto.ConversationDTO;
import mop.app.client.dto.UserDTO;
import mop.app.client.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupDAO {
    private static final Logger logger = LoggerFactory.getLogger(GroupDAO.class);

    public GroupDAO() {}

    public List<ConversationDTO> getAllGroups() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c " +
                "FROM ConversationDTO c " +
                "JOIN ConversationTypeDTO ct ON c.typeId = ct.typeId " +
                "WHERE ct.typeName = 'GROUP'" +
                "ORDER BY c.createdAt DESC";

            Query<ConversationDTO> query = session.createQuery(hql, ConversationDTO.class);
            return query.list();
        } catch (Exception e) {
            logger.error("Error fetching all groups", e);
            return null;
        }
    }

    public List<UserDTO> getAllMembers(long groupId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT u " +
                "FROM ConversationDTO c " +
                "JOIN ConversationTypeDTO ct ON c.typeId = ct.typeId " +
                "JOIN EnrollmentDTO e ON c.conversationId = e.conversationId " +
                "JOIN UserDTO u ON e.userId = u.userId " +
                "WHERE ct.typeName = 'GROUP' " +
                "AND c.conversationId = :groupId";

            Query<UserDTO> query = session.createQuery(hql, UserDTO.class);
            query.setParameter("groupId", groupId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error fetching group members", e);
            return null;
        }
    }

    public List<UserDTO> getGroupMembers(long groupId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT u " +
                "FROM ConversationDTO c " +
                "JOIN ConversationTypeDTO ct ON c.typeId = ct.typeId " +
                "JOIN EnrollmentDTO e ON c.conversationId = e.conversationId " +
                "JOIN EnrollmentRoleDTO er ON e.roleId = er.roleId " +
                "JOIN UserDTO u ON e.userId = u.userId " +
                "WHERE ct.typeName = 'GROUP' " +
                "AND c.conversationId = :groupId " +
                "AND er.roleName = 'MEMBER'";

            Query<UserDTO> query = session.createQuery(hql, UserDTO.class);
            query.setParameter("groupId", groupId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error fetching group members", e);
            return null;
        }
    }

    public List<UserDTO> getGroupAdmins(long groupId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT u " +
                "FROM ConversationDTO c " +
                "JOIN ConversationTypeDTO ct ON c.typeId = ct.typeId " +
                "JOIN EnrollmentDTO e ON c.conversationId = e.conversationId " +
                "JOIN EnrollmentRoleDTO er ON e.roleId = er.roleId " +
                "JOIN UserDTO u ON e.userId = u.userId " +
                "WHERE ct.typeName = 'GROUP' " +
                "AND c.conversationId = :groupId " +
                "AND er.roleName = 'ADMIN'";

            Query<UserDTO> query = session.createQuery(hql, UserDTO.class);
            query.setParameter("groupId", groupId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error fetching group admins", e);
            return null;
        }
    }

    public long countAllMembers(long groupId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(u) " +
                "FROM ConversationDTO c " +
                "JOIN ConversationTypeDTO ct ON c.typeId = ct.typeId " +
                "JOIN EnrollmentDTO e ON c.conversationId = e.conversationId " +
                "JOIN UserDTO u ON e.userId = u.userId " +
                "WHERE ct.typeName = 'GROUP' " +
                "AND c.conversationId = :groupId";

            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("groupId", groupId);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error counting group members", e);
            return 0;
        }
    }

    public long countMembers(long groupId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(u) " +
                "FROM ConversationDTO c " +
                "JOIN ConversationTypeDTO ct ON c.typeId = ct.typeId " +
                "JOIN EnrollmentDTO e ON c.conversationId = e.conversationId " +
                "JOIN EnrollmentRoleDTO er ON e.roleId = er.roleId " +
                "JOIN UserDTO u ON e.userId = u.userId " +
                "WHERE ct.typeName = 'GROUP' " +
                "AND c.conversationId = :groupId " +
                "AND er.roleName = 'MEMBER'";

            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("groupId", groupId);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error counting group members", e);
            return 0;
        }
    }

    public long countAdmins(long groupId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(u) " +
                "FROM ConversationDTO c " +
                "JOIN ConversationTypeDTO ct ON c.typeId = ct.typeId " +
                "JOIN EnrollmentDTO e ON c.conversationId = e.conversationId " +
                "JOIN EnrollmentRoleDTO er ON e.roleId = er.roleId " +
                "JOIN UserDTO u ON e.userId = u.userId " +
                "WHERE ct.typeName = 'GROUP' " +
                "AND c.conversationId = :groupId " +
                "AND er.roleName = 'ADMIN'";

            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("groupId", groupId);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error counting group admins", e);
            return 0;
        }
    }

//    public static void main(String[] args) {
//        GroupDAO groupDAO = new GroupDAO();
//
//        logger.info("All members:");
//        logger.info("Total members: " + groupDAO.countAllMembers(2));
//        List<UserDTO> allMembers = groupDAO.getAllMembers(2);
//        if (allMembers != null) {
//            allMembers.forEach(user -> logger.info(user.toString()));
//        }
//
//        logger.info("Group members:");
//        logger.info("Total members: " + groupDAO.countMembers(2));
//        List<UserDTO> groupMembers = groupDAO.getGroupMembers(2);
//        if (groupMembers != null) {
//            groupMembers.forEach(user -> logger.info(user.toString()));
//        }
//
//        logger.info("Group admins:");
//        logger.info("Total admins: " + groupDAO.countAdmins(2));
//        List<UserDTO> groupAdmins = groupDAO.getGroupAdmins(2);
//        if (groupAdmins != null) {
//            groupAdmins.forEach(user -> logger.info(user.toString()));
//        }
//    }
}
