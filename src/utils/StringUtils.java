package utils;

import java.util.regex.Pattern;

/**
 * StringUtils - Utility methods for string operations
 */
public class StringUtils {
    // Common regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,15}$");
    
    /**
     * Check if a string is null or empty
     * 
     * @param str The string to check
     * @return true if the string is null or empty, false otherwise
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Check if a string is not null and not empty
     * 
     * @param str The string to check
     * @return true if the string is not null and not empty, false otherwise
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * Validate email format
     * 
     * @param email The email to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number format
     * 
     * @param phone The phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Mask a sensitive string (e.g., credit card number)
     * 
     * @param input The string to mask
     * @param visibleChars Number of characters to keep visible at the end
     * @return Masked string
     */
    public static String maskString(String input, int visibleChars) {
        if (isEmpty(input) || input.length() <= visibleChars) {
            return input;
        }
        
        return "*".repeat(input.length() - visibleChars) + 
               input.substring(input.length() - visibleChars);
    }
    
    /**
     * Format currency amount
     * 
     * @param amount The amount to format
     * @return Formatted string with LE currency
     */
    public static String formatCurrency(double amount) {
        return String.format("%.2f LE", amount);
    }
    
    /**
     * Convert a string to title case
     * 
     * @param input The string to convert
     * @return Title cased string
     */
    public static String toTitleCase(String input) {
        if (isEmpty(input)) {
            return input;
        }
        
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;
        
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            } else {
                c = Character.toLowerCase(c);
            }
            titleCase.append(c);
        }
        
        return titleCase.toString();
    }
}