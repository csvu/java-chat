package mop.app.client.dao.user;

import mop.app.client.Client;
import mop.app.client.dao.UtilityDAO;
import mop.app.client.model.user.Conversation;
import mop.app.client.model.user.Message;
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
}
