package com.erp.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for secure password hashing and verification using BCrypt.
 */
@Service
public class PasswordService {

    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordService() {
        // BCrypt with strength 10 (default) - good balance of security and performance
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Hash a plain text password.
     * 
     * @param plainPassword The plain text password
     * @return The BCrypt hashed password
     */
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Verify a plain text password against a hashed password.
     * Also supports legacy plain text passwords for migration.
     * 
     * @param plainPassword  The plain text password to verify
     * @param hashedPassword The stored (potentially hashed) password
     * @return true if the password matches
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        // Check if the stored password is BCrypt hashed (starts with $2a$, $2b$, or
        // $2y$)
        if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$")
                || hashedPassword.startsWith("$2y$")) {
            // It's a BCrypt hash - verify properly
            return passwordEncoder.matches(plainPassword, hashedPassword);
        } else {
            // Legacy plain text password - direct comparison
            // This allows existing users to log in while we migrate
            return plainPassword.equals(hashedPassword);
        }
    }

    /**
     * Check if a password is already hashed with BCrypt.
     * 
     * @param password The password to check
     * @return true if the password is BCrypt hashed
     */
    public boolean isHashed(String password) {
        if (password == null) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }
}
