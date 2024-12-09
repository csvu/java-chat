package mop.app.client.dao.user;

import mop.app.client.Client;
import mop.app.client.dao.UtilityDAO;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDAO {
    public ArrayList<Conversation> getMatched(String query) {
        //Return Users!
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "SELECT\n" +
                        "\tR1.USER_ID AS \"id\",\n" +
                        "\tR1.DISPLAY_NAME AS DISPLAY_NAME,\n" +
                        "\tR1.AVATAR AS AVATAR,\n" +
                        "\tCOALESCE(R3.TYPE_NAME, 'N/A') AS \"type\"\n" +
                        "FROM\n" +
                        "\t(\n" +
                        "\t\tSELECT\n" +
                        "\t\t\tUSER_ID,\n" +
                        "\t\t\tDISPLAY_NAME,\n" +
                        "\t\t\tAVATAR\n" +
                        "\t\tFROM\n" +
                        "\t\t\tPUBLIC.\"user\"\n" +
                        "\t\tWHERE\n" +
                        "\t\t\t(\n" +
                        "\t\t\t\tUSERNAME ILIKE ?\n" +
                        "\t\t\t\tOR DISPLAY_NAME ILIKE ?\n" +
                        "\t\t\t)\n" +
                        "\t\t\tAND USER_ID <> ?\n" +
                        "\t) AS R1\n" +
                        "\tLEFT JOIN PUBLIC.RELATIONSHIP AS R2 ON R1.USER_ID = R2.USER_ID2 AND R2.USER_ID1 = ?\n" +
                        "\tLEFT JOIN PUBLIC.RELATIONSHIP_TYPE AS R3 ON R2.STATUS = R3.TYPE_ID\n" +
                        "WHERE\n" +
                        "\tR3.TYPE_NAME = 'PENDING'\n" +
                        "\tOR R3.TYPE_NAME IS NULL\n")) {
            preparedStatement.setString(1, "%" + query + "%");
            preparedStatement.setString(2, "%" + query + "%");
            preparedStatement.setInt(3, Client.currentUserId);
            preparedStatement.setInt(4, Client.currentUserId);


            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("id");
                System.out.print(userId);
                String displayName = rs.getString("DISPLAY_NAME");
                String avatar = rs.getString("AVATAR");
                String type = rs.getString("type");
                Conversation conversation = new Conversation(userId, type, avatar == null ? null : URL.of(URI.create(avatar), null) , displayName, false, null, null);
                list.add(conversation);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public ArrayList<Conversation> getMatchedMessages(String query) {
        //Return Users!
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "SELECT\n" +
                        "\tCASE\n" +
                        "\t\tWHEN CO_TYPE.TYPE_NAME = 'PAIR' THEN (\n" +
                        "\t\t\tSELECT\n" +
                        "\t\t\t\tPARTNER.DISPLAY_NAME\n" +
                        "\t\t\tFROM\n" +
                        "\t\t\t\tPUBLIC.ENROLLMENT AS EN\n" +
                        "\t\t\t\tJOIN PUBLIC.\"user\" AS PARTNER ON EN.USER_ID = PARTNER.USER_ID\n" +
                        "\t\t\tWHERE\n" +
                        "\t\t\t\tEN.USER_ID <> ?\n" +
                        "\t\t\t\tAND CONVERSATION_ID = CO.CONVERSATION_ID\n" +
                        "\t\t\tLIMIT\n" +
                        "\t\t\t\t1\n" +
                        "\t\t)\n" +
                        "\t\tELSE CO.NAME\n" +
                        "\tEND AS DISPLAY_NAME,\n" +
                        "\tCO.CONVERSATION_ID,\n" +
                        "\tME.CONTENT,\n" +
                        "\tCO_TYPE.TYPE_NAME\n" +
                        "FROM\n" +
                        "\tPUBLIC.ENROLLMENT EN\n" +
                        "\tJOIN PUBLIC.CONVERSATION CO ON EN.CONVERSATION_ID = CO.CONVERSATION_ID\n" +
                        "\tJOIN PUBLIC.CONVERSATION_TYPE CO_TYPE ON CO.TYPE_ID = CO_TYPE.TYPE_ID\n" +
                        "\tJOIN PUBLIC.MESSAGE ME ON EN.CONVERSATION_ID = ME.CONVERSATION_ID\n" +
                        "WHERE\n" +
                        "\tEN.USER_ID = ?\n" +
                        "\tAND ME.CONTENT ILIKE ?")) {

            preparedStatement.setInt(1, Client.currentUserId);
            preparedStatement.setInt(2, Client.currentUserId);
            preparedStatement.setString(3, "%" + query + "%");


            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("conversation_id");
                System.out.print(userId);
                String displayName = rs.getString("DISPLAY_NAME");
                String content = rs.getString("content");
                String type = rs.getString("type_name");
                Conversation conversation = new Conversation(userId, type, null , displayName, false, null, content);
                list.add(conversation);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public ArrayList<Conversation> getConv() {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        SqlReader reader = new SqlReader("src/main/java/mop/app/client/dao/user/getConv.sql");
        String query = reader.read();

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, Client.currentUserId);
            preparedStatement.setInt(2, Client.currentUserId);
            preparedStatement.setInt(3, Client.currentUserId);
            preparedStatement.setInt(4, Client.currentUserId);


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
                list.add(conversation);
            }
        } catch (SQLException e) {
            System.out.println("getConv" + e.getMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void makeFriendRequest(int userId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "insert into public.relationship\n" +
                        "values (?, ?, NOW(), 3)")) {

            preparedStatement.setInt(1, Client.currentUserId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public ArrayList<Conversation> getFriendRequests() {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "SELECT\n" +
                        "\tR2.USER_ID AS \"id\",\n" +
                        "\tR2.DISPLAY_NAME AS \"display_name\",\n" +
                        "\tR2.AVATAR AS \"avatar\",\n" +
                        "\tTYPE_NAME AS \"type\"\n" +
                        "FROM\n" +
                        "\t(\n" +
                        "\t\tSELECT\n" +
                        "\t\t\tUSER_ID1,\n" +
                        "\t\t\tTYPE_NAME\n" +
                        "\t\tFROM\n" +
                        "\t\t\tPUBLIC.RELATIONSHIP\n" +
                        "\t\t\tJOIN PUBLIC.RELATIONSHIP_TYPE ON TYPE_ID = STATUS\n" +
                        "\t\tWHERE\n" +
                        "\t\t\tUSER_ID2 = ?\n" +
                        "\t\t\tAND STATUS = 3\n" +
                        "\t) AS R1\n" +
                        "\tJOIN PUBLIC.\"user\" AS R2 ON R1.USER_ID1 = R2.USER_ID" )) {

            preparedStatement.setInt(1, Client.currentUserId);


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
            System.out.println(e.getMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void cancelFriendRequest(int userId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "delete \n" +
                        "from public.relationship\n" +
                        "where user_id1 = ? and user_id2 = ?" )) {

            preparedStatement.setInt(1, Client.currentUserId);
            preparedStatement.setInt(2, userId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void acceptFriendRequest(int userId, String displayName) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement relationshipStatement1 = conn.prepareStatement(
                     "update public.relationship\n" +
                             "set created_at = NOW(), status = 1\n" +
                             "where user_id1 = ? and user_id2 = ?\n");
             PreparedStatement relationshipStatement2 = conn.prepareStatement(
                     "insert into \n" +
                             "public.relationship\n" +
                             "values (?, ?, NOW(), 1)" );
             PreparedStatement relationshipStatement3 = conn.prepareStatement(
                     "insert into \n" +
                             "public.conversation(\"name\", icon, type_id)\n" +
                             "values (?, NULL, 1)" , Statement.RETURN_GENERATED_KEYS);
             PreparedStatement relationshipStatement4 = conn.prepareStatement(
                     "insert into \n" +
                             "public.enrollment\n" +
                             "values (?, ?, 2), (?, ?, 2)" );
        ) {
            conn.setAutoCommit(false);
            relationshipStatement1.setInt(1, userId);
            relationshipStatement1.setInt(2, Client.currentUserId);
            relationshipStatement1.executeUpdate();
            relationshipStatement2.setInt(1, Client.currentUserId);
            relationshipStatement2.setInt(2, userId);
            relationshipStatement2.executeUpdate();

            relationshipStatement3.setString(1, displayName);
            relationshipStatement3.executeUpdate();

            ResultSet rs = relationshipStatement3.getGeneratedKeys();
            rs.next();
            int conversationId = rs.getInt(1);
            relationshipStatement4.setInt(1, userId);
            relationshipStatement4.setInt(2, conversationId);
            relationshipStatement4.setInt(3, Client.currentUserId);
            relationshipStatement4.setInt(4, conversationId);
            relationshipStatement4.executeUpdate();

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

    public ArrayList<Conversation> getFriendsNotInConversation(int conversationId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "SELECT user_id2 as \"id\", display_name, avatar, 'FRIEND' as \"type\" FROM public.relationship join public.relationship_type on status = type_id join public.\"user\" on user_id2 = user_id\n" +
                        "where user_id1 = ? and type_name = 'FRIEND' and user_id2 not in (\n" +
                        "\tselect user_id\n" +
                        "\tfrom public.enrollment\n" +
                        "\twhere conversation_id = ?\n" +
                        ")\n" )) {

            preparedStatement.setInt(1, Client.currentUserId);
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
            System.out.println(e.getMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public ArrayList<ArrayList<Conversation>> getMembers(int conversationId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<ArrayList<Conversation>> ret = new ArrayList<>();
        ret.add(new ArrayList<>());
        ret.add(new ArrayList<>());
        Connection conn = utilityDAO.getConnection();
        if (conn == null) {
            return ret;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "select en.user_id, us.display_name, en_role.role_name\n" +
                        "from public.enrollment as en join public.\"user\" as us on us.user_id = en.user_id join enrollment_role as en_role on en.role_id = en_role.role_id\n" +
                        "where en.conversation_id = ?\n" +
                        "order by en_role.role_id" )) {


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

    public ArrayList<Conversation> getFriends() {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "SELECT user_id2 as \"id\", display_name, avatar, 'FRIEND' as \"type\", is_active FROM public.relationship join public.relationship_type on status = type_id join public.\"user\" on user_id2 = user_id\n" +
                        "where user_id1 = ? and type_name = 'FRIEND'" )) {

            preparedStatement.setInt(1, Client.currentUserId);

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
            System.out.println(e.getMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    public int getPairConversationId(int friendId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Conversation> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            throw new RuntimeException("Connection is null");
        }

        int res = -1;

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "select co.conversation_id\n" +
                        "from public.conversation as co join public.enrollment as en on co.conversation_id = en.conversation_id\n" +
                        "group by co.conversation_id\n" +
                        "having count(en.user_id) = 2 and SUM(CASE WHEN en.user_id = ? or en.user_id = ? THEN 1 ELSE 0 END) = 2" )) {

            preparedStatement.setInt(1, Client.currentUserId);
            preparedStatement.setInt(2, friendId);


            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            res = rs.getInt("conversation_id");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

    public void blockUser(int userId) {
        // BLOCKED is userid1 is blocked by userid2
    }

    public void addGroup(int convId, String name, String type, List<Integer> userIds) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement relationshipStatement1 = conn.prepareStatement(
                "insert into public.conversation(\"name\", icon, type_id)\n" +
                        "values (?, NULL, 2)" , Statement.RETURN_GENERATED_KEYS);

             PreparedStatement relationshipStatement2 = conn.prepareStatement(
                     "insert into public.enrollment\n" + "select user_id, ?, case when user_id = ? then 1 else 2 end\n" +
                             "from public.enrollment where conversation_id = ?" );

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
                relationshipStatement2.setInt(2, Client.currentUserId);
                relationshipStatement2.setInt(3, convId);
                relationshipStatement2.executeUpdate();
            } else {
                conversationId = convId;
            }



            for (int userId : userIds) {
                PreparedStatement relationshipStatement3 = conn.prepareStatement(
                        "insert into \n" +
                                "public.enrollment\n" +
                                "values (?, ?, 2)" );
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

    public void sendMessage(int convId, String content) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement relationshipStatement = conn.prepareStatement(
                "insert into public.message(conversation_id, user_id, \"content\") values(?, ?, ?)");

        ) {
            relationshipStatement.setInt(1, convId);
            relationshipStatement.setInt(2, Client.currentUserId);
            relationshipStatement.setString(3, content);
            relationshipStatement.executeUpdate();

        } catch (SQLException e) {
            try {
                System.err.print(e.getMessage());
                conn.rollback();
            } catch (SQLException excep) {
                excep.printStackTrace();
            }
        }
    }

    public ArrayList<Message> getMessages(int conversationId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Message> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "select us.user_id, us.display_name as sender, me.sent_at, me.content\n" +
                        "from public.message as me join public.\"user\" as us on me.user_id = us.user_id\n" +
                        "where conversation_id = ?" )) {

            preparedStatement.setInt(1, conversationId);

            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String sender = rs.getString("sender");
                int userId = rs.getInt("user_id");
                if (userId == Client.currentUserId) {
                    sender = "You";
                }
                String content = rs.getString("content");
                Timestamp ts = rs.getTimestamp("sent_at");
                Message msg = new Message(sender, null, ts.toLocalDateTime(), content);
                list.add(msg);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
