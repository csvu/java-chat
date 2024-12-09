package mop.app.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        try {
            Client.main(args);
        } catch (Exception e) {
            logger.error("Error starting client application: " + e.getMessage());
        }
    }
}