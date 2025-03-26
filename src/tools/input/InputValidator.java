package tools.input;

import java.util.regex.Pattern;

/**
 * ✅ InputValidator - General input validation utilities ✅
 * 
 * This utility class provides standardized methods for validating different types
 * of input across the pharmacy system. It ensures data quality and security by
 * validating common inputs like emails, phone numbers, and text inputs.
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - Utility Class Pattern: Stateless class with static methods
 * - Encapsulation: Self-contained validation logic
 * - Reusability: Common validation functions shared across the system
 * 
 * 📚 Class Responsibilities:
 * - Validate email addresses with proper format
 * - Validate Egyptian phone numbers
 * - Validate text inputs for length and character content
 * - Validate numeric inputs (price, quantity, etc.)
 * - Prevent injection attacks with input sanitization
 * 
 * 🌐 Role in System:
 * This utility ensures data quality throughout the EL-TA3BAN Pharmacy System
 * by providing consistent validation for all user and system inputs.
 */
public class InputValidator {
    
    // Common validation patterns
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern EGYPTIAN_PHONE_PATTERN = 
        Pattern.compile("^(01)[0-9]{9}$");
    
    private static final Pattern NUMERIC_PATTERN = 
        Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
    
    /**
     * Validate email address format
     * 
     * @param email The email to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate Egyptian phone number format
     * 
     * @param phone The phone number to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidEgyptianPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return EGYPTIAN_PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validate text input for minimum and maximum length
     * 
     * @param text The text to validate
     * @param minLength Minimum allowed length
     * @param maxLength Maximum allowed length
     * @return True if valid, false otherwise
     */
    public static boolean isValidTextLength(String text, int minLength, int maxLength) {
        if (text == null) {
            return false;
        }
        int length = text.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Validate numeric input (positive numbers)
     * 
     * @param numberStr The number string to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidPositiveNumber(String numberStr) {
        if (numberStr == null || numberStr.trim().isEmpty()) {
            return false;
        }
        
        if (!NUMERIC_PATTERN.matcher(numberStr).matches()) {
            return false;
        }
        
        try {
            double number = Double.parseDouble(numberStr);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if text contains potentially dangerous SQL injection characters
     * 
     * @param text The text to check
     * @return True if contains dangerous characters, false if safe
     */
    public static boolean containsDangerousCharacters(String text) {
        if (text == null) {
            return false;
        }
        
        // Check for SQL injection patterns
        return text.contains("'") || 
               text.contains("\"") || 
               text.contains(";") || 
               text.contains("--") || 
               text.contains("/*") || 
               text.contains("*/") ||
               text.contains("=");
    }
    
    /**
     * Sanitize input text by removing potentially dangerous characters
     * 
     * @param text The text to sanitize
     * @return Sanitized text
     */
    public static String sanitizeText(String text) {
        if (text == null) {
            return "";
        }
        
        // Remove common SQL injection characters
        return text.replaceAll("[;'\"=]", "")
                  .replaceAll("--", "")
                  .replaceAll("/\\*", "")
                  .replaceAll("\\*/", "");
    }
    
    /**
     * Validate a name (no numbers or special characters)
     * 
     * @param name The name to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        // Allow letters, spaces, and common name punctuation
        return name.matches("^[\\p{L} .'-]+$");
    }
}