package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;

/**
 * 🛠️ SystemTools - Essential utility functions for the EL-TA3BAN Pharmacy System 🛠️
 * 
 * This class provides frequently used helper functions in a simple, organized way.
 * It includes formatting, validation, security, and generation tools used across the system.
 * 
 * Functions are grouped by category for easy reference:
 * - 🔒 SECURITY: Password hashing, verification, and secure tokens
 * - 📅 DATES: Date and time formatting for display and storage
 * - 🧮 FORMATS: Currency, text, and general formatting utilities
 * - 🎲 GENERATORS: ID and reference number generators
 * - ✅ VALIDATORS: Input validation for common data types
 */
public class SystemTools {
    // =============== CONSTANTS ===============
    
    /** System name */
    public static final String SYSTEM_NAME = "EL-TA3BAN PHARMACY SYSTEM";
    
    /** Egyptian currency symbol */
    public static final String CURRENCY = "LE";
    
    /** Standard date formatter */
    public static final DateTimeFormatter DATE_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /** Standard date-time formatter */
    public static final DateTimeFormatter DATETIME_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // =============== 🔒 SECURITY FUNCTIONS ===============
    
    /**
     * Create a secure password hash with salt
     * 
     * @param plainPassword Password to hash
     * @return Secure hash with embedded salt
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
     * Verify a password against a stored hash
     * 
     * @param plainPassword Password to verify
     * @param storedHash Stored hash to check against
     * @return True if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        try {
            if (!storedHash.startsWith("$SHA$")) {
                // Not a hashed password, do direct comparison (for legacy passwords)
                return storedHash.equals(plainPassword);
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
     * Generate a secure random token
     * 
     * @param length Token length in bytes
     * @return Base64-encoded secure token
     */
    public static String generateSecureToken(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    // =============== 📅 DATE FUNCTIONS ===============
    
    /**
     * Format a date for display
     * 
     * @param date Date to format
     * @return Formatted date string
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_FORMAT);
    }
    
    /**
     * Format a date-time for display
     * 
     * @param dateTime Date-time to format
     * @return Formatted date-time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATETIME_FORMAT);
    }
    
    /**
     * Parse a date string into a LocalDate
     * 
     * @param dateStr Date string to parse
     * @return Parsed LocalDate or null if invalid
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Check if a date is within a valid range
     * 
     * @param date Date to check
     * @param minDate Minimum allowed date
     * @param maxDate Maximum allowed date
     * @return True if within range, false otherwise
     */
    public static boolean isDateInRange(LocalDate date, LocalDate minDate, LocalDate maxDate) {
        if (date == null) return false;
        
        boolean afterMin = minDate == null || !date.isBefore(minDate);
        boolean beforeMax = maxDate == null || !date.isAfter(maxDate);
        
        return afterMin && beforeMax;
    }
    
    // =============== 🧮 FORMAT FUNCTIONS ===============
    
    /**
     * Format currency amount with Egyptian LE symbol
     * 
     * @param amount Amount to format
     * @return Formatted currency string
     */
    public static String formatCurrency(double amount) {
        return String.format("%.2f %s", amount, CURRENCY);
    }
    
    /**
     * Truncate a string if it exceeds a maximum length
     * 
     * @param text Text to truncate
     * @param maxLength Maximum allowed length
     * @return Truncated text with ellipsis if needed
     */
    public static String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Mask sensitive data (like credit card numbers)
     * 
     * @param data Data to mask
     * @param visibleChars Number of characters to show at the end
     * @return Masked string
     */
    public static String maskSensitiveData(String data, int visibleChars) {
        if (data == null || data.length() <= visibleChars) {
            return data;
        }
        
        int maskLength = data.length() - visibleChars;
        return "*".repeat(maskLength) + data.substring(maskLength);
    }
    
    // =============== 🎲 GENERATOR FUNCTIONS ===============
    
    /**
     * Generate a random ID based on current time
     * 
     * @return Generated ID
     */
    public static int generateId() {
        return (int) (System.currentTimeMillis() % 10000);
    }
    
    /**
     * Generate a reference number for orders or prescriptions
     * 
     * @param prefix Reference prefix (e.g., "ORD", "RX")
     * @return Generated reference number
     */
    public static String generateReference(String prefix) {
        Random rand = new Random();
        int year = LocalDate.now().getYear();
        int random = rand.nextInt(100000);
        
        return String.format("%s-%d-%05d", prefix, year, random);
    }
    
    // =============== ✅ VALIDATION FUNCTIONS ===============
    
    /**
     * Validate email format
     * 
     * @param email Email to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email validation
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Validate Egyptian phone number format
     * 
     * @param phone Phone number to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidEgyptianPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // Egyptian mobile numbers validation (01X XXXXXXXX)
        return phone.matches("^(01)[0-9]{9}$");
    }
    
    /**
     * Validate text length
     * 
     * @param text Text to validate
     * @param minLength Minimum allowed length
     * @param maxLength Maximum allowed length
     * @return True if valid, false otherwise
     */
    public static boolean isValidLength(String text, int minLength, int maxLength) {
        if (text == null) {
            return false;
        }
        
        int length = text.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Check if text contains potentially unsafe characters
     * 
     * @param text Text to check
     * @return True if contains unsafe characters, false otherwise
     */
    public static boolean hasUnsafeCharacters(String text) {
        if (text == null) {
            return false;
        }
        
        return text.contains(";") || 
               text.contains("'") || 
               text.contains("\"") || 
               text.contains("--");
    }
}