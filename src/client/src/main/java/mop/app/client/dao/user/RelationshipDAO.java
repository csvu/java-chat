package mop.app.client.dao.user;

import mop.app.client.Client;
import mop.app.client.dao.UtilityDAO;
import mop.app.client.model.user.Conversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;



public class RelationshipDAO {
    public static Logger logger = LoggerFactory.getLogger(RelationshipDAO.class);
    
    public static void makeFriendRequest(int userId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "insert into public.relationship\n" +
                        "values (?, ?, NOW(), 3)")) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static ArrayList<Conversation> getFriendRequests() {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        SELECT
                        R2.USER_ID AS "id",
                        R2.DISPLAY_NAME AS "display_name",
                        R2.AVATAR AS "avatar",
                        TYPE_NAME AS "type"
                        FROM
                        (
                        SELECT
                        USER_ID1,
                        TYPE_NAME
                        FROM
                        PUBLIC.RELATIONSHIP
                        JOIN PUBLIC.RELATIONSHIP_TYPE ON TYPE_ID = STATUS
                        WHERE
                        USER_ID2 = ?
                        AND STATUS = 3
                        ) AS R1
                        JOIN PUBLIC."user" AS R2 ON R1.USER_ID1 = R2.USER_ID""")) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());


            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String displayName = rs.getString("DISPLAY_NAME");
                String avatar = rs.getString("AVATAR");
                String type = rs.getString("type");
                Conversation conversation = new Conversation(id, type, avatar == null ? null : URL.of(URI.create(avatar), null) , displayName, false, null, null);
                list.add(conversation);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static void cancelFriendRequest(int userId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        delete
                        from public.relationship
                        where user_id1 = ? and user_id2 = ?""")) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, userId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static Conversation acceptFriendRequest(int userId, String displayName) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return null;
        }

        try (PreparedStatement relationshipStatement1 = conn.prepareStatement(
                """
                        update public.relationship
                        set created_at = NOW(), status = 1
                        where user_id1 = ? and user_id2 = ?
                        """);
             PreparedStatement relationshipStatement2 = conn.prepareStatement(
                     """
                             insert into
                             public.relationship
                             values (?, ?, NOW(), 1)
                             on conflict(user_id1, user_id2)
                             do
                             update
                             set created_at = NOW(), status = 1""");
             PreparedStatement relationshipStatement3 = conn.prepareStatement(
                     """
                             insert into
                             public.conversation("name", icon, type_id)
                             values (NULL, NULL, 1)""", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement relationshipStatement4 = conn.prepareStatement(
                     """
                             insert into
                             public.enrollment
                             values (?, ?, 2), (?, ?, 2)""");
        ) {
            conn.setAutoCommit(false);
            relationshipStatement1.setInt(1, userId);
            relationshipStatement1.setInt(2, (int) Client.currentUser.getUserId());
            relationshipStatement1.executeUpdate();
            relationshipStatement2.setInt(1, (int) Client.currentUser.getUserId());
            relationshipStatement2.setInt(2, userId);
            relationshipStatement2.executeUpdate();

            System.out.println("AcceptFriendRequest " + userId + " " + Client.currentUser.getUserId());

            if (ConversationDAO.getPairConversationId(userId) != -1) {
                conn.commit();
                return null;
            }


            relationshipStatement3.executeUpdate();


            ResultSet rs = relationshipStatement3.getGeneratedKeys();
            rs.next();
            int conversationId = rs.getInt(1);
            relationshipStatement4.setInt(1, userId);
            relationshipStatement4.setInt(2, conversationId);
            relationshipStatement4.setInt(3, (int) Client.currentUser.getUserId());
            relationshipStatement4.setInt(4, conversationId);
            relationshipStatement4.executeUpdate();

            conn.commit();
            return new Conversation(conversationId, "PAIR", null, displayName, true, null, "No messages yet");

        } catch (SQLException e) {
            try {
                System.err.print(e.getMessage());
                conn.rollback();

            } catch (SQLException excep) {
                excep.printStackTrace();

            }

        }
        return null;
    }

    public static ArrayList<Conversation> getFriendsNotInConversation(int conversationId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        SELECT user_id2 as "id", display_name, avatar, 'FRIEND' as "type" FROM public.relationship join public.relationship_type on status = type_id join public."user" on user_id2 = user_id
                        where user_id1 = ? and type_name = 'FRIEND' and user_id2 not in (
                        select user_id
                        from public.enrollment
                        where conversation_id = ?
                        )
                        """)) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, conversationId);

            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String displayName = rs.getString("DISPLAY_NAME");
                String avatar = rs.getString("AVATAR");
                String type = rs.getString("type");
                Conversation conversation = new Conversation(id, type, avatar == null ? null : URL.of(URI.create(avatar), null) , displayName, false, null, null);
                list.add(conversation);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static ArrayList<Conversation> getFriends() {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "SELECT user_id2 as \"id\", display_name, avatar, 'FRIEND' as \"type\", is_active FROM public.relationship join public.relationship_type on status = type_id join public.\"user\" on user_id2 = user_id\n" +
                        "where user_id1 = ? and type_name = 'FRIEND'" )) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());

            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String displayName = rs.getString("DISPLAY_NAME");
                String avatar = rs.getString("AVATAR");
                String type = rs.getString("type");
                String isActive = rs.getBoolean("is_active") ? "Online" : "Offline";
                Conversation friend = new Conversation(id, type, avatar == null ? null : URL.of(URI.create(avatar), null) , displayName, false, null, isActive);
                list.add(friend);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static void unfriend(int friendId) {
        UtilityDAO utilityDAO = new UtilityDAO();

        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        delete
                        from public.relationship
                        where ((user_id1 = ? and user_id2 = ? ) or (user_id1 = ? and user_id2 = ?)) and status = 1
                    """)) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, friendId);
            preparedStatement.setInt(3, friendId);
            preparedStatement.setInt(4, (int) Client.currentUser.getUserId());


            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static void block(int friendId) {
        UtilityDAO utilityDAO = new UtilityDAO();

        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement1 = conn.prepareStatement(
                """
                        insert into public.relationship
                        values (?, ?, NOW(), 2)
                        on conflict(user_id1, user_id2)
                        do update
                        set created_at = NOW(), status = 2
                    """);
                PreparedStatement preparedStatement2 = conn.prepareStatement(
                        """
                                delete
                                from public.relationship
                                where user_id1 = ? and user_id2 = ? and status <> 2
                            """)
        ) {
            conn.setAutoCommit(false);
            preparedStatement1.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement1.setInt(2, friendId);
            preparedStatement1.executeUpdate();
            preparedStatement2.setInt(1, friendId);
            preparedStatement2.setInt(2, (int) Client.currentUser.getUserId());
            preparedStatement2.executeUpdate();
            conn.commit();


        } catch (SQLException e) {
            try {
                logger.error("Cannot Block: {}", e.getMessage());
                conn.rollback();
            } catch (SQLException excep) {
                excep.printStackTrace();
            }
            logger.error("Cannot Block: {}", e.getMessage());
        }
    }

    public static String getReverseRelationship(int userId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return null;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        select
                        type_name
                        from public.relationship join public.relationship_type on status = type_id
                        where user_id1 = ? and user_id2 = ?
                        """)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, (int) Client.currentUser.getUserId());

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString("type_name");
            } else {
                return null;
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    public static void unblock(int id) {
        UtilityDAO utilityDAO = new UtilityDAO();

        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        delete
                        from public.relationship
                        where user_id1 = ? and user_id2 = ? and status = 2
                    """)) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
}
