package mop.app.client.dao.user;

import java.util.Objects;
import mop.app.client.Client;
import mop.app.client.dao.UtilityDAO;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Relationship;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationDAO {



    public static int getRemainingInAPair(int conversationId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();

        int res = -1;
        if (conn == null) {
            return res;
        }
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "select user_id from public.enrollment where conversation_id = ? and user_id <> ?")) {
            preparedStatement.setInt(1, conversationId);
            preparedStatement.setInt(2, (int) Client.currentUser.getUserId());


            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                res = rs.getInt("user_id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

    public static Relationship getRelationShipInAPairConversation(int conversationId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if (conn == null) {
            return null;
        }
        Relationship res = null;

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        select user_id2, type_name
                        from public.relationship join public.relationship_type on status = type_id
                        where user_id1 = ? and user_id2 = (select user_id from public.enrollment where conversation_id = ? and user_id <> ?);""")) {
            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, conversationId);
            preparedStatement.setInt(3, (int) Client.currentUser.getUserId());


            System.out.println("GetRelationShipInAPairConversation " + conversationId + " " + Client.currentUser.getUserId());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                res = new Relationship();
                res.setId(rs.getInt("user_id2"));
                res.setStatus(rs.getString("type_name"));
            } else {
                res = new Relationship();
                res.setId(getRemainingInAPair(conversationId));
                res.setStatus("N/A");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;


    }

    public static void setSeen(int conversationId, boolean seen) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if (conn == null) {
            return;
        }

        if (!seen) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    """
                            update public.enrollment
                            set is_seen = false
                            where conversation_id = ? and user_id <> ?""")) {
                preparedStatement.setInt(1, conversationId);
                preparedStatement.setInt(2, (int) Client.currentUser.getUserId());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        update public.enrollment
                        set is_seen = true
                        where conversation_id = ? and user_id = ?""")) {
            preparedStatement.setInt(1, conversationId);
            preparedStatement.setInt(2, (int) Client.currentUser.getUserId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void setConversationName(int conversationId, String name) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        update public.conversation
                        set name = ?
                        where conversation_id = ?""")) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, conversationId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<Conversation> getConv() {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        URL url = Client.class.getClassLoader().getResource("getConv.sql");
        SqlReader reader = new SqlReader(Objects.requireNonNull(url).getPath());
        String query = reader.read();

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(3, (int) Client.currentUser.getUserId());


            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String displayName = rs.getString("DISPLAY_NAME");
                String avatar = rs.getString("AVATAR");
                String type = rs.getString("type");
                boolean seen = rs.getBoolean("is_seen");
                Timestamp lastContentDateTime = rs.getTimestamp("sent_at");
                String content = rs.getString("content");

                Conversation conversation = new Conversation(id, type, avatar == null ? null : URL.of(URI.create(avatar), null) , displayName, seen, lastContentDateTime == null ? null : lastContentDateTime.toLocalDateTime(), content);
                if (conversation.getContent() == null) {
                    conversation.setSeen(true);
                    conversation.setContent("No messages yet");
                }
                list.add(conversation);
            }
        } catch (SQLException e) {
            System.out.println("getConv" + e.getMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public static Conversation getConv(int conversationId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Conversation res = null;
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return res;
        }

        String query = "select * from public.conversation co join public.conversation_type co_type on co.type_id = co_type.type_id\n" +
                "where conversation_id = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, conversationId);


            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("conversation_id");
                String name = rs.getString("name");
                String type = rs.getString("type_name");

                res = new Conversation(id, type, null, name, false, null, null);

            }
        } catch (SQLException e) {
            System.out.println("getConv" + e.getMessage());
        }

        return res;
    }

    public static ArrayList<ArrayList<Conversation>> getMembers(int conversationId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<ArrayList<Conversation>> ret = new ArrayList<>();
        ret.add(new ArrayList<>());
        ret.add(new ArrayList<>());
        Connection conn = utilityDAO.getConnection();
        if (conn == null) {
            return ret;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        select en.user_id, us.display_name, en_role.role_name
                        from public.enrollment as en join public."user" as us on us.user_id = en.user_id join enrollment_role as en_role on en.role_id = en_role.role_id
                        where en.conversation_id = ?
                        order by en_role.role_id""")) {


            preparedStatement.setInt(1, conversationId);

            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("user_id");
                String displayName = rs.getString("DISPLAY_NAME");
                String type = rs.getString("role_name");
                Conversation conversation = new Conversation(id, null, null , displayName, false, null, null);
                if (type.equals("ADMIN")) {
                    ret.get(0).add(conversation);
                } else {
                    ret.get(1).add(conversation);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }

    public static int getPairConversationId(int friendId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            throw new RuntimeException("Connection is null");
        }

        int res = -1;

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        select co.conversation_id
                        from public.conversation as co join public.enrollment as en on co.conversation_id = en.conversation_id
                        where co.type_id = 1
                        group by co.conversation_id
                        having count(en.user_id) = 2 and SUM(CASE WHEN en.user_id = ? or en.user_id = ? THEN 1 ELSE 0 END) = 2""")) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, friendId);


            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                res = rs.getInt("conversation_id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

    public static void blockUser(int userId) {
        // BLOCKED is userid1 is blocked by userid2
    }

    public static void addGroup(int convId, String name, String type, List<Integer> userIds) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement relationshipStatement1 = conn.prepareStatement(
                "insert into public.conversation(\"name\", icon, type_id)\n" +
                        "values (?, NULL, 2)" , Statement.RETURN_GENERATED_KEYS);

             PreparedStatement relationshipStatement2 = conn.prepareStatement(
                     """
                             insert into public.enrollment
                             select user_id, ?, case when user_id = ? then 1 else 2 end
                             from public.enrollment where conversation_id = ?""");

        ) {
            conn.setAutoCommit(false);
            int conversationId = 0;
            if (type.equals("PAIR")) {
                relationshipStatement1.setString(1, name);
                relationshipStatement1.executeUpdate();

                ResultSet rs = relationshipStatement1.getGeneratedKeys();
                rs.next();
                conversationId = rs.getInt(1);
                relationshipStatement2.setInt(1, conversationId);
                relationshipStatement2.setInt(2, (int) Client.currentUser.getUserId());
                relationshipStatement2.setInt(3, convId);
                relationshipStatement2.executeUpdate();
            } else {
                conversationId = convId;
            }



            for (int userId : userIds) {
                PreparedStatement relationshipStatement3 = conn.prepareStatement(
                        """
                                insert into
                                public.enrollment
                                values (?, ?, 2)""");
                relationshipStatement3.setInt(1, userId);
                relationshipStatement3.setInt(2, conversationId);
                relationshipStatement3.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                System.err.print(e.getMessage());
                conn.rollback();
            } catch (SQLException excep) {
                excep.printStackTrace();
            }
        }
    }

    public static void removeMember(int conversationID, int userId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        delete from public.enrollment
                        where conversation_id = ? and user_id = ?""")) {
            preparedStatement.setInt(1, conversationID);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addAdmin(int conversationID, int userId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        update public.enrollment
                        set role_id = 1
                        where conversation_id = ? and user_id = ?""")) {
            preparedStatement.setInt(1, conversationID);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
