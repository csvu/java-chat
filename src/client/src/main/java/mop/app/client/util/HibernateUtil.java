package mop.app.client.util;

import java.util.Map;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

    @Getter
    private static SessionFactory sessionFactory;

    static {
        try {
            Map<String, Object> config =
                (Map<String, Object>) YamlConfigLoader.loadConfig("config.yml").get("database");
            logger.info("Loaded database configuration: {}", config);

            // Safe access for nested maps
            Map<String, Object> serverConfig = (Map<String, Object>) config.get("server");
            Map<String, Object> connectionConfig = (Map<String, Object>) config.get("connection");

            Configuration configuration = new Configuration();

            configuration.setProperty("hibernate.connection.url", (String) connectionConfig.get("url"));
            configuration.setProperty("hibernate.connection.username", (String) connectionConfig.get("username"));
            configuration.setProperty("hibernate.connection.password", (String) connectionConfig.get("password"));

            configuration.setProperty("hibernate.hbm2ddl.auto", (String) config.get("hbm2ddl_auto"));
            configuration.setProperty("hibernate.show_sql", String.valueOf(config.get("show_sql")));
            configuration.setProperty("hibernate.format_sql", String.valueOf(config.get("format_sql")));
            configuration.setProperty("hibernate.highlight_sql", String.valueOf(config.get("highlight_sql")));
            configuration.setProperty("hibernate.connection.pool_size", String.valueOf(config.get("pool_size")));
            configuration.setProperty("hibernate.current_session_context_class", (String) config.get("current_session_context_class"));
            configuration.setProperty("hibernate.connection.autocommit", String.valueOf(config.get("autocommit")));

            configuration.addAnnotatedClass(mop.app.client.dto.UserDTO.class);
            configuration.addAnnotatedClass(mop.app.client.dto.RoleDTO.class);
            configuration.addAnnotatedClass(mop.app.client.dto.ConversationDTO.class);
            configuration.addAnnotatedClass(mop.app.client.dto.ConversationTypeDTO.class);
            configuration.addAnnotatedClass(mop.app.client.dto.EnrollmentRoleDTO.class);
            configuration.addAnnotatedClass(mop.app.client.dto.EnrollmentDTO.class);
            configuration.addAnnotatedClass(mop.app.client.dto.LoginTimeDTO.class);
            configuration.addAnnotatedClass(mop.app.client.dto.OpenTimeDTO.class);
            configuration.addAnnotatedClass(mop.app.client.dto.ReportDTO.class);
            configuration.addAnnotatedClass(mop.app.client.dto.RelationshipDTO.class);
            configuration.addAnnotatedClass(mop.app.client.dto.RelationshipTypeDTO.class);
            configuration.addAnnotatedClass(mop.app.client.dto.MessageDTO.class);

            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
                logger.info("SessionFactory closed successfully");
            } catch (Exception e) {
                logger.error("Error closing SessionFactory", e);
            }
        }
    }
}