package com.medicare.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for hashing and verifying user passwords securely using SHA-256 with salt.
 */
public class PasswordUtil {

    private static final int SALT_LENGTH = 16;

    /**
     * Generate a random salt byte array
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash password with given salt using SHA-256
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes());
            byte[] hashedBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return password;
        }
    }

    /**
     * Verify raw password against stored salted hash format (format: "salt$hash")
     */
    public static boolean verifyPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null || rawPassword == null) {
            return false;
        }
        
        // Handle legacy plaintext passwords
        if (!storedPassword.contains("$")) {
            return rawPassword.equals(storedPassword);
        }

        String[] parts = storedPassword.split("\\$", 2);
        if (parts.length != 2) {
            return rawPassword.equals(storedPassword);
        }

        String salt = parts[0];
        String expectedHash = parts[1];
        String actualHash = hashPassword(rawPassword, salt);
        return expectedHash.equals(actualHash);
    }

    /**
     * Creates a salted password string formatted as "salt$hash"
     */
    public static String createSaltedHash(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return salt + "$" + hash;
    }
}
