package mop.app.client.dao;

import java.sql.*;
import java.util.Properties;

public class UtilityDAO{
    String dbms = "postgresql";
    String serverName = "localhost";
    int portNumber = 5432;
    String dbName = "chat";
    String user = "postgres";
    String password = "986532";

    public Connection getConnection(){
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", user);
        connectionProps.put("password", password);
        try {
            String connString = "jdbc:" + dbms + "://" + serverName +
                    ":" + this.portNumber + "/" + dbName;
            conn = DriverManager.getConnection(connString, connectionProps);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            conn = null;
        }
        return conn;

    }

}