package mop.app.client.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    private String dbms;
    private String serverName;
    private int port;
    private String dbName;
    private String user;
    private String password;

    public DatabaseUtil() {
        try {
            Map<String, Object> config =
                (Map<String, Object>) YamlConfigLoader.loadConfig("config.yml").get("database");
            logger.info("Loaded database configuration: {}", config);

            // Safe access for nested maps
            Map<String, Object> serverConfig = (Map<String, Object>) config.get("server");
            if (serverConfig != null) {
                dbms = (String) config.get("dbms");
                serverName = (String) serverConfig.get("name");
                port = (serverConfig.get("port") != null) ? (int) serverConfig.get("port") : 5432; // Default value
                dbName = (String) serverConfig.get("dbname");
            } else {
                logger.error("Server configuration is missing from config.yml.");
            }

            Map<String, Object> connectionConfig = (Map<String, Object>) config.get("connection");
            if (connectionConfig != null) {
                user = (String) connectionConfig.get("username");
                password = (String) connectionConfig.get("password");
            } else {
                logger.error("Connection configuration is missing from config.yml.");
            }

            logger.info("Database configuration loaded successfully");
        } catch (Exception e) {
            logger.error("Failed to load configuration file: {}", e.getMessage());
        }
    }

    public Connection getConnection() {
        Connection conn;
        Properties connectionProps = new Properties();
        connectionProps.put("user", user);
        connectionProps.put("password", password);

        try {
            String connString = "jdbc:" + dbms + "://" + serverName +
                ":" + this.port + "/" + dbName;
            conn = DriverManager.getConnection(connString, connectionProps);
//            conn.setCatalog(dbName);

            logger.info("Connected to database: {}", conn.getCatalog());
        } catch (SQLException e) {
            logger.error("Failed to connect to database: {}", e.getMessage());
            conn = null;
        }
        logger.info("Connection established successfully: {}", conn);
        return conn;
    }

//    public static void main(String[] args) {
//        DatabaseUtil dbUtil = new DatabaseUtil();
//        dbUtil.getConnection();
//    }
}
