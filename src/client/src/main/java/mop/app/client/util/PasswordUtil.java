package mop.app.client.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.Random;

public class PasswordUtil {
    public static String hash(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verify(String password, String hashPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashPassword).verified;
    }

    public static String generateRandomPassword() {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "@$!%*?&_-";

        StringBuilder password = new StringBuilder();
        Random random = new Random(Double.doubleToLongBits(Math.random()));

        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        String allChars = uppercase + lowercase + digits + specialChars;
        for (int i = password.length(); i < 16; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the characters to ensure random order
        String passwordStr = password.toString();
        char[] passwordArray = passwordStr.toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    public static boolean isStrongPassword(String password) {
        // Password must be at least 8 characters long, contain at least 1 uppercase, 1 lowercase, 1 number, and 1 special character
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*?&_-]{8,}$";
        return password.matches(regex);
    }

//    public static void main(String[] args) {
//        String password = "123456789";
//        String hashPassword = hash(password);
//        System.out.println("Hashed password: " + hashPassword);
//        System.out.println("Verify password: " + verify(password, hashPassword));
//
//        System.out.println("Random password: " + generateRandomPassword());
//    }
}