package models;

/**
 * 💳 CreditCard - Secure payment method storage and handling 💳
 * 
 * This class implements a secure approach to handling sensitive payment 
 * information within the EL-TA3BAN pharmacy system by masking card numbers
 * and storing only essential information needed for payment processing.
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - Encapsulation: All card data is private with controlled access
 * - Information Hiding: Implements card masking for security
 * - Immutability: Card data cannot be modified after creation
 * 
 * 📚 Class Responsibilities:
 * - Securely store payment card details
 * - Mask sensitive card information
 * - Provide formatted display of card details
 * - Support identification by last 4 digits
 * 
 * 🌐 Role in System:
 * This class works closely with the Wallet class to provide secure
 * payment options for patients, allowing them to save payment methods
 * for future transactions without compromising financial security.
 */
public class CreditCard {
    private String cardNumber;      // Masked card number for security
    private String cardHolderName;  // Name on the card
    private String expiryDate;      // Expiry date in MM/YY format
    private String cardType;        // Visa, Mastercard, etc.
    
    /**
     * 🔐 CreditCard Constructor - Creates a new secure payment method
     * 
     * Initializes a new credit card with automatic security masking.
     * This constructor demonstrates secure handling of financial data
     * by immediately masking the card number upon creation.
     * 
     * @param cardNumber Card number (will be masked except last 4 digits)
     * @param cardHolderName Name of Egyptian card holder
     * @param expiryDate Expiry date in MM/YY format
     * @param cardType Type of card (Visa, Mastercard, Meeza, etc.)
     */
    public CreditCard(String cardNumber, String cardHolderName, String expiryDate, String cardType) {
        // Mask all but last 4 digits of card number for security
        this.cardNumber = maskCardNumber(cardNumber);
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cardType = cardType;
    }
    
    /**
     * 🔒 getCardNumber - Retrieves the securely masked card number
     * 
     * This method provides access to the payment card number while 
     * maintaining security by returning only the masked version with
     * asterisks replacing most digits for PCI compliance.
     * 
     * @return Masked card number with only last 4 digits visible
     */
    public String getCardNumber() {
        return cardNumber;
    }
    
    /**
     * 👤 getCardHolderName - Retrieves the name associated with the card
     * 
     * This method returns the cardholder's name as stored in the system.
     * This information is used for display purposes and transaction verification
     * but doesn't require the same level of masking as the card number.
     * 
     * @return Full name of the Egyptian card holder
     */
    public String getCardHolderName() {
        return cardHolderName;
    }
    
    /**
     * 📅 getExpiryDate - Retrieves the card's validity period
     * 
     * This method returns the expiration date of the stored payment card.
     * The date is stored in MM/YY format following standard payment industry
     * conventions for card expiration representation.
     * 
     * @return Card expiration date in MM/YY format
     */
    public String getExpiryDate() {
        return expiryDate;
    }
    
    /**
     * 💳 getCardType - Identifies the payment network of the card
     * 
     * This method returns the card issuer or payment network information
     * such as Visa, Mastercard, or Meeza (Egyptian national payment system).
     * This detail is important for both display purposes and transaction processing.
     * 
     * @return Payment network or card issuer name
     */
    public String getCardType() {
        return cardType;
    }
    
    /**
     * 🔢 getLastFourDigits - Provides unique card identifier
     * 
     * This method extracts and returns only the last 4 digits of the card number,
     * which serve as a unique identifier for the card without exposing sensitive 
     * information. It's used for card selection interfaces and transaction logs
     * where the complete masked number would be too verbose.
     * 
     * @return Last 4 digits of the card number for identification
     */
    public String getLastFourDigits() {
        return cardNumber.substring(cardNumber.length() - 4);
    }
    
    /**
     * 🔒 maskCardNumber - Implements financial data security best practices
     * 
     * This method masks the card number for data security and PCI compliance.
     * It demonstrates string manipulation techniques by cleaning the input,
     * masking digits, and reformatting the output for improved readability.
     * 
     * @param cardNumber Full credit card number to be masked
     * @return Formatted card number with only last 4 digits visible
     */
    private String maskCardNumber(String cardNumber) {
        // Remove any spaces or dashes
        String cleanNumber = cardNumber.replaceAll("[ -]", "");
        
        // Keep only last 4 digits visible
        int visible = 4;
        int length = cleanNumber.length();
        
        if (length <= visible) {
            return cleanNumber;
        }
        
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < length - visible; i++) {
            masked.append('*');
        }
        
        masked.append(cleanNumber.substring(length - visible));
        
        // Format with spaces for readability
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < masked.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(' ');
            }
            formatted.append(masked.charAt(i));
        }
        
        return formatted.toString();
    }
    
    /**
     * 📊 displayInfo - Formats and presents card details to the user
     * 
     * This method provides a formatted output of the card information
     * while maintaining security by showing only the masked card number.
     * It demonstrates proper information display with security in mind.
     */
    public void displayInfo() {
        System.out.println(cardType + ": " + cardNumber);
        System.out.println("Card Holder: " + cardHolderName);
        System.out.println("Expires: " + expiryDate);
    }
}