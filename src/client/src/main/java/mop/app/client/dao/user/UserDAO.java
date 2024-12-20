package mop.app.client.dao.user;

import mop.app.client.Client;
import mop.app.client.dao.UtilityDAO;
import mop.app.client.model.user.Relationship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDAO {
    public static ArrayList<Relationship> getMatched(String query) {
        //Return Users!
        UtilityDAO utilityDAO = new UtilityDAO();
        ArrayList<Relationship> list = new ArrayList<>();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return list;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                """
                        SELECT
                        	R1.USER_ID AS "id",
                        	R1.DISPLAY_NAME AS DISPLAY_NAME,
                        	COALESCE(R3.TYPE_NAME, 'N/A') AS "type"
                        FROM
                        	(
                        		SELECT
                        			USER_ID,
                        			DISPLAY_NAME,
                        			AVATAR
                        		FROM
                        			PUBLIC."user"
                        		WHERE
                        			(
                        				USERNAME ILIKE ?
                        				OR DISPLAY_NAME ILIKE ?
                        			)
                        			AND USER_ID <> ?
                        	) AS R1
                        	LEFT JOIN PUBLIC.RELATIONSHIP AS R2 ON R1.USER_ID = R2.USER_ID2
                        	AND R2.USER_ID1 = ?
                        	LEFT JOIN PUBLIC.RELATIONSHIP_TYPE AS R3 ON R2.STATUS = R3.TYPE_ID
                        WHERE
                        	(
                        		R3.TYPE_NAME = 'PENDING'
                        		OR R3.TYPE_NAME IS NULL
                        	)
                        	AND NOT EXISTS (
                        		SELECT
                        			*
                        		FROM
                        			PUBLIC.RELATIONSHIP REL
                        			JOIN PUBLIC.RELATIONSHIP_TYPE REL_TYPE ON REL.STATUS = REL_TYPE.TYPE_ID
                        		WHERE
                        			REL.USER_ID1 = R1.USER_ID
                        			AND REL.USER_ID2 = ?
                        			AND REL_TYPE.TYPE_NAME = 'BLOCK'
                        	)
                        """)) {
            preparedStatement.setString(1, "%" + query + "%");
            preparedStatement.setString(2, "%" + query + "%");
            preparedStatement.setInt(3, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(4, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(5, (int) Client.currentUser.getUserId());




            System.out.println("SHI");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("id");
                String displayName = rs.getString("DISPLAY_NAME");
                String type = rs.getString("type");
                Relationship re = new Relationship(userId, displayName, type);
                list.add(re);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static void report(int userId) {
        UtilityDAO utilityDAO = new UtilityDAO();
        Connection conn = utilityDAO.getConnection();
        if(conn == null) {
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "INSERT INTO PUBLIC.REPORT (USER_ID1, USER_ID2, CREATED_AT) VALUES (?, ?, NOW())")) {
            preparedStatement.setInt(1, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
