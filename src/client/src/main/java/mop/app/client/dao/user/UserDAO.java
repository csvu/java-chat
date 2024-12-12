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
                "SELECT\n" +
                        "\tR1.USER_ID AS \"id\",\n" +
                        "\tR1.DISPLAY_NAME AS DISPLAY_NAME,\n" +
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
            preparedStatement.setInt(3, (int) Client.currentUser.getUserId());
            preparedStatement.setInt(4, (int) Client.currentUser.getUserId());


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
}
