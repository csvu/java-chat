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
                     """
                             insert into
                             public.conversation("name", icon, type_id)
                             values (NULL, NULL, 1)""", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement enroll = conn.prepareStatement(
                     """
                             insert into
                             public.enrollment
                             values (?, ?, 2), (?, ?, 2)""");

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
                """
                        select us.user_id, us.display_name as sender, me.sent_at, me.content, me.msg_id
                        from public.message as me join public."user" as us on me.user_id = us.user_id
                        left join public.hidden_message hm on me.msg_id = hm.msg_id and hm.user_id = ?
                        where me.conversation_id = ? and me.msg_id < ? and hm.msg_id is NULL
                        order by me.msg_id desc limit 10""")) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, conversationId);
            preparedStatement.setInt(3, msgId);
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
                """
                        SELECT
                        CASE
                        WHEN CO_TYPE.TYPE_NAME = 'PAIR' THEN (
                        SELECT
                        PARTNER.DISPLAY_NAME
                        FROM
                        PUBLIC.ENROLLMENT AS EN
                        JOIN PUBLIC."user" AS PARTNER ON EN.USER_ID = PARTNER.USER_ID
                        WHERE
                        EN.USER_ID <> ?
                        AND CONVERSATION_ID = CO.CONVERSATION_ID
                        LIMIT
                        1
                        )
                        ELSE CO.NAME
                        END AS DISPLAY_NAME,
                        CO.CONVERSATION_ID,
                        ME.CONTENT,
                        CO_TYPE.TYPE_NAME,
                        ME.MSG_ID
                        FROM
                        PUBLIC.ENROLLMENT EN
                        JOIN PUBLIC.CONVERSATION CO ON EN.CONVERSATION_ID = CO.CONVERSATION_ID
                        JOIN PUBLIC.CONVERSATION_TYPE CO_TYPE ON CO.TYPE_ID = CO_TYPE.TYPE_ID
                        JOIN PUBLIC.MESSAGE ME ON EN.CONVERSATION_ID = ME.CONVERSATION_ID
                        LEFT JOIN PUBLIC.HIDDEN_MESSAGE HM ON ME.MSG_ID = HM.MSG_ID AND HM.USER_ID = ?
                        WHERE
                        EN.USER_ID = ?
                        AND HM.USER_ID IS NULL
                        AND ME.CONTENT ILIKE ?""")) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(3, (int) Client.currentUser.getUserId());
            preparedStatement.setString(4, "%" + query + "%");


            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("conversation_id");
                System.out.print(userId);
                String displayName = rs.getString("DISPLAY_NAME");
                String content = rs.getString("content");
                String type = rs.getString("type_name");
                int msgId = rs.getInt("msg_id");
                MessageInConversation conversation = new MessageInConversation(msgId, userId, type, null , displayName, true, null, content);
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
                """
                        SELECT
                        CASE
                        WHEN CO_TYPE.TYPE_NAME = 'PAIR' THEN (
                        SELECT
                        PARTNER.DISPLAY_NAME
                        FROM
                        PUBLIC.ENROLLMENT AS EN
                        JOIN PUBLIC."user" AS PARTNER ON EN.USER_ID = PARTNER.USER_ID
                        WHERE
                        EN.USER_ID <> ?
                        AND CONVERSATION_ID = CO.CONVERSATION_ID
                        LIMIT
                        1
                        )
                        ELSE CO.NAME
                        END AS DISPLAY_NAME,
                        CO.CONVERSATION_ID,
                        ME.CONTENT,
                        CO_TYPE.TYPE_NAME,
                        ME.MSG_ID
                        FROM
                        PUBLIC.ENROLLMENT EN
                        JOIN PUBLIC.CONVERSATION CO ON EN.CONVERSATION_ID = CO.CONVERSATION_ID
                        JOIN PUBLIC.CONVERSATION_TYPE CO_TYPE ON CO.TYPE_ID = CO_TYPE.TYPE_ID
                        JOIN PUBLIC.MESSAGE ME ON EN.CONVERSATION_ID = ME.CONVERSATION_ID
                        LEFT JOIN PUBLIC.HIDDEN_MESSAGE HM ON ME.MSG_ID = HM.MSG_ID AND HM.USER_ID = ?
                        WHERE
                        EN.USER_ID = ?
                        AND HM.USER_ID IS NULL
                        AND ME.CONTENT ILIKE ?
                        AND CO.CONVERSATION_ID = ?"""
        )) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(3, (int) Client.currentUser.getUserId());
            preparedStatement.setString(4, "%" + query + "%");
            preparedStatement.setInt(5, conversationId);

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

    public static void hideMessage(int msgId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "insert into hidden_message values(?, ?)")) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, msgId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void hideAllMessages(int conversationId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        insert into hidden_message
                        select ? as user_id, msg_id
                        from public.message
                        where conversation_id = ?
                        except
                        select *
                        from hidden_message
                        where user_id = ?
                        """)) {

            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, conversationId);
            preparedStatement.setInt(3, (int) Client.currentUser.getUserId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }






    public static void main(String[] args) {
        deleteMessage(212);
    }
}
