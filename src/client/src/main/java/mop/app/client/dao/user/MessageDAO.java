package mop.app.client.dao.user;

import mop.app.client.Client;
import mop.app.client.dao.UtilityDAO;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;
import mop.app.client.model.user.MessageInConversation;
import mop.app.client.model.user.Relationship;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;

public class MessageDAO {
    public static int sendMessage(Conversation conversation, Relationship relationship, String content) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return -1;
        }
        int res = -1;

        try (PreparedStatement relationshipStatement = conn.prepareStatement(
                "insert into public.message(conversation_id, user_id, \"content\") values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement createConversation = conn.prepareStatement(
                     "insert into \n" +
                             "public.conversation(\"name\", icon, type_id)\n" +
                             "values (NULL, NULL, 1)" , Statement.RETURN_GENERATED_KEYS);
             PreparedStatement enroll = conn.prepareStatement(
                     "insert into \n" +
                             "public.enrollment\n" +
                             "values (?, ?, 2), (?, ?, 2)" );

        ) {
            if (relationship != null && ConversationDAO.getPairConversationId(relationship.getId()) == -1) {
                createConversation.executeUpdate();
                ResultSet rs = createConversation.getGeneratedKeys();
                rs.next();

                int receiver = relationship.getId();
                conversation.setConversationID(rs.getInt(1));
                enroll.setInt(1, (int) Client.currentUser.getUserId());
                enroll.setInt(2, conversation.getConversationID());
                enroll.setInt(3, receiver);
                enroll.setInt(4, conversation.getConversationID());
                enroll.executeUpdate();
            }

            relationshipStatement.setInt(1, conversation.getConversationID());
            relationshipStatement.setInt(2, (int) Client.currentUser.getUserId());
            relationshipStatement.setString(3, content);
            relationshipStatement.executeUpdate();
            ResultSet rs = relationshipStatement.getGeneratedKeys();
            rs.next();
            res = rs.getInt(1);

        } catch (SQLException e) {
            try {
                System.err.print(e.getMessage());
                conn.rollback();
            } catch (SQLException excep) {
                excep.printStackTrace();
            }
        }
        return res;
    }

    public static ArrayList<Message> getMessages(int conversationId, int msgId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Message> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "select us.user_id, us.display_name as sender, me.sent_at, me.content, me.msg_id\n" +
                        "from public.message as me join public.\"user\" as us on me.user_id = us.user_id\n" +
                        "where conversation_id = ? and me.msg_id < ? order by me.msg_id desc limit 10 " )) {

            preparedStatement.setInt(1, conversationId);
            preparedStatement.setInt(2, msgId);
            System.out.println("getMessages " + conversationId + " " + msgId);
            ResultSet rs = preparedStatement.executeQuery();


            while (rs.next()) {
                String sender = rs.getString("sender");
                int userId = rs.getInt("user_id");
                if (userId == (int) Client.currentUser.getUserId()) {
                    sender = "You";
                }
                String content = rs.getString("content");
                Timestamp ts = rs.getTimestamp("sent_at");
                int messId = rs.getInt("msg_id");
                Message msg = new Message(sender, null, ts.toLocalDateTime(), content, conversationId, userId, messId);
                list.add(msg);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(list);
        list.sort(Comparator.comparingInt(Message::getMsgId));
        return list;
    }

    public static ArrayList<MessageInConversation> getMatchedMessages(String query) {
        //Return Users!
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<MessageInConversation> list = new ArrayList<>();
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
                        "\tCO_TYPE.TYPE_NAME,\n" +
                        "\tME.MSG_ID\n" +
                        "FROM\n" +
                        "\tPUBLIC.ENROLLMENT EN\n" +
                        "\tJOIN PUBLIC.CONVERSATION CO ON EN.CONVERSATION_ID = CO.CONVERSATION_ID\n" +
                        "\tJOIN PUBLIC.CONVERSATION_TYPE CO_TYPE ON CO.TYPE_ID = CO_TYPE.TYPE_ID\n" +
                        "\tJOIN PUBLIC.MESSAGE ME ON EN.CONVERSATION_ID = ME.CONVERSATION_ID\n" +
                        "WHERE\n" +
                        "\tEN.USER_ID = ?\n" +
                        "\tAND ME.CONTENT ILIKE ?")) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, (int) Client.currentUser.getUserId());
            preparedStatement.setString(3, "%" + query + "%");


            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("conversation_id");
                System.out.print(userId);
                String displayName = rs.getString("DISPLAY_NAME");
                String content = rs.getString("content");
                String type = rs.getString("type_name");
                int msgId = rs.getInt("msg_id");
                MessageInConversation conversation = new MessageInConversation(msgId, userId, type, null , displayName, false, null, content);
                list.add(conversation);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static ArrayList<MessageInConversation> getMatchedMessages(int conversationId, String query) {
        //Return Users!
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<MessageInConversation> list = new ArrayList<>();
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
                        "\tCO_TYPE.TYPE_NAME,\n" +
                        "\tME.MSG_ID\n" +
                        "FROM\n" +
                        "\tPUBLIC.ENROLLMENT EN\n" +
                        "\tJOIN PUBLIC.CONVERSATION CO ON EN.CONVERSATION_ID = CO.CONVERSATION_ID\n" +
                        "\tJOIN PUBLIC.CONVERSATION_TYPE CO_TYPE ON CO.TYPE_ID = CO_TYPE.TYPE_ID\n" +
                        "\tJOIN PUBLIC.MESSAGE ME ON EN.CONVERSATION_ID = ME.CONVERSATION_ID\n" +
                        "WHERE\n" +
                        "\tEN.USER_ID = ?\n" +
                        "\tAND ME.CONTENT ILIKE ?\n" +
                        "\tAND CO.CONVERSATION_ID = ?"
        )) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, (int) Client.currentUser.getUserId());
            preparedStatement.setString(3, "%" + query + "%");
            preparedStatement.setInt(4, conversationId);

            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("conversation_id");
                System.out.print(userId);
                String displayName = rs.getString("DISPLAY_NAME");
                String content = rs.getString("content");
                String type = rs.getString("type_name");
                int msgId = rs.getInt("msg_id");
                MessageInConversation conversation = new MessageInConversation(msgId, userId, type, null , displayName, false, null, content);
                list.add(conversation);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("GET MSG IN CONV " + list.size());
        return list;
    }

    public static void deleteMessage(int msgId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "delete from public.message where msg_id = ?")) {

            preparedStatement.setInt(1, msgId);
            preparedStatement.executeUpdate();

            System.out.println("Delete message " + msgId);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        deleteMessage(212);
    }
}
