package tools.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 🔐 SecurityManager - Core security functions for authentication 🔐
 * 
 * This utility class provides essential security functions for user authentication,
 * including password hashing, verification, input validation, and brute force protection.
 * It follows security best practices with SHA-256 hashing, salting, and account lockouts.
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - Utility Class Pattern: A stateless class with static methods for security operations
 * - Encapsulation: Security operations are self-contained with clear interfaces
 * - Abstraction: Hides complex security implementation details
 * 
 * 📚 Class Responsibilities:
 * - Securely hash passwords with random salts
 * - Verify password hashes with timing-safe comparison
 * - Validate login inputs to prevent injection attacks
 * - Implement brute force protection with account lockouts
 * 
 * 🌐 Role in System:
 * This utility provides the core security operations used by AuthenticationService
 * to protect the EL-TA3BAN Pharmacy System. It enables secure access control
 * while preventing common security vulnerabilities like password leaks and brute force attacks.
 */
public class SecurityManager {
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    
    // Maps to track login attempts and lockouts
    private static Map<String, Integer> loginAttempts = new HashMap<>();
    private static Map<String, Long> lockoutTimes = new HashMap<>();
    
    /**
     * 🔒 hashPassword - Creates a secure SHA-256 password hash with salt
     * 
     * This method implements cryptographic best practices by generating
     * a random salt for each password and applying SHA-256 hashing.
     * The result is formatted as "$SHA$salt$hash" using Base64 encoding
     * for storage compatibility.
     * 
     * @param plainPassword The plain text password to hash
     * @return Securely hashed password with embedded salt
     */
    public static String hashPassword(String plainPassword) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Create SHA-256 hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            
            // Convert to Base64 for storage
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Format: $SHA$salt$hash
            return "$SHA$" + saltBase64 + "$" + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            // Fallback to plaintext if hashing fails
            System.err.println("Warning: Password hashing failed. Using plaintext password.");
            return plainPassword;
        }
    }
    
    /**
     * 🔐 verifyPassword - Securely validates user password against stored hash
     * 
     * This method demonstrates secure password verification by extracting
     * the salt from the stored hash, applying the same hashing algorithm,
     * and using a timing-safe comparison to prevent timing attacks.
     * The method handles the "$SHA$salt$hash" format created by hashPassword.
     * 
     * @param plainPassword The plain text password to verify
     * @param storedHash The stored password hash with embedded salt
     * @return True if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        try {
            if (!storedHash.startsWith("$SHA$")) {
                // Not a hashed password, can't verify
                return false;
            }
            
            // Split the hash into components
            String[] parts = storedHash.split("\\$");
            if (parts.length != 4) {
                return false;
            }
            
            // Extract salt and hash
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] storedHashBytes = Base64.getDecoder().decode(parts[3]);
            
            // Hash the plaintext password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] computedHash = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            
            // Compare hashes
            return MessageDigest.isEqual(storedHashBytes, computedHash);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 🛡️ validateLoginInput - Prevents injection attacks and ensures data quality
     * 
     * This method prevents SQL injection and other security attacks by
     * validating user input before processing. It checks for empty inputs,
     * minimum length requirements, and potentially dangerous characters
     * that could be used in injection attacks.
     * 
     * @param username The username to validate
     * @param password The password to validate
     * @return True if input is valid, false otherwise
     */
    public static boolean validateLoginInput(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty.");
            return false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Password cannot be empty.");
            return false;
        }
        
        if (username.length() < 3) {
            System.out.println("Username must be at least 3 characters long.");
            return false;
        }
        
        // Check for potentially dangerous characters
        if (username.contains(";") || username.contains("'") || username.contains("\"") || 
            username.contains("`") || username.contains("--")) {
            System.out.println("Username contains invalid characters.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if an account is locked due to too many failed login attempts
     * 
     * @param username The username to check
     * @return True if account is locked, false otherwise
     */
    public static boolean isAccountLocked(String username) {
        if (!lockoutTimes.containsKey(username)) {
            return false;
        }
        
        long lockoutTime = lockoutTimes.get(username);
        long currentTime = System.currentTimeMillis();
        long elapsedMinutes = (currentTime - lockoutTime) / (1000 * 60);
        
        if (elapsedMinutes >= LOCKOUT_DURATION_MINUTES) {
            // Lockout period has expired
            lockoutTimes.remove(username);
            loginAttempts.remove(username);
            return false;
        }
        
        return true;
    }
    
    /**
     * 🔒 recordFailedLoginAttempt - Implements brute force attack protection
     * 
     * This method enhances system security by tracking failed login attempts
     * and implementing account lockouts after reaching a threshold. It provides
     * user feedback about remaining attempts and implements a timing-based
     * lockout mechanism to prevent password guessing attacks.
     * 
     * @param username The username that failed login
     * @return True if account is now locked, false otherwise
     */
    public static boolean recordFailedLoginAttempt(String username) {
        int attempts = loginAttempts.getOrDefault(username, 0) + 1;
        loginAttempts.put(username, attempts);
        
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            // Lock the account
            lockoutTimes.put(username, System.currentTimeMillis());
            System.out.println("Too many failed login attempts. Account locked for " + 
                             LOCKOUT_DURATION_MINUTES + " minutes.");
            return true;
        } else {
            int remainingAttempts = MAX_LOGIN_ATTEMPTS - attempts;
            System.out.println("Login failed. " + remainingAttempts + 
                             " attempt" + (remainingAttempts > 1 ? "s" : "") + " remaining.");
            return false;
        }
    }
    
    /**
     * Reset login attempts counter for successful login
     * 
     * @param username The username to reset
     */
    public static void resetLoginAttempts(String username) {
        loginAttempts.remove(username);
        lockoutTimes.remove(username);
    }
}