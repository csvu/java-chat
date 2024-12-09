package mop.app.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import mop.app.client.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreProcess {
    private static final Logger logger = LoggerFactory.getLogger(PreProcess.class);

    public PreProcess() {}

    public static boolean saveUserInformation(UserDTO user) {
        if (new File("user.bin").exists()) {
            logger.error("User information already exists");
            boolean isDelete = deleteUserInformation();
            if (!isDelete) {
                logger.error("Failed to delete existing user information");
                return false;
            }
        }

        try (FileOutputStream fileOut = new FileOutputStream("user.bin")) {
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(user);
            out.close();
            fileOut.close();
            logger.info("User information saved successfully");
            return true;
        } catch (Exception e) {
            logger.error("Failed to save user information: {}", e.getMessage());
            return false;
        }
    }

    public static UserDTO loadUserInformation() {
        try {
            UserDTO user;
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("user.bin"))) {
                user = (UserDTO) in.readObject();
            }
            logger.info("User information loaded successfully");
            return user;
        } catch (Exception e) {
            logger.error("Failed to load user information: {}", e.getMessage());
            return null;
        }
    }

    public static boolean deleteUserInformation() {
        try {
            if (new File("user.bin").delete()) {
                logger.info("User information deleted successfully");
                return true;
            } else {
                logger.error("Failed to delete user information");
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to delete user information: {}", e.getMessage());
            return false;
        }
    }

//    public static void main(String[] args) {
//        UserDTO user = UserDTO.builder()
//            .userId(1)
//            .username("user")
//            .password("password")
//            .build();
//
//        saveUserInformation(user);
//        UserDTO loadedUser = loadUserInformation();
//        logger.info("Loaded user: {}", loadedUser);
//    }
}
