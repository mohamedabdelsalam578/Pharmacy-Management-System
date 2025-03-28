package models.Wallet;

import java.io.Serializable;

/**
 * Represents a credit card stored in a wallet
 */
public class Card implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String number;
    private String holderName;
    private String expiryDate;
    
    /**
     * Constructor for a credit card
     * 
     * @param number The card number
     * @param holderName The cardholder name
     * @param expiryDate The expiry date (MM/YY)
     */
    public Card(String number, String holderName, String expiryDate) {
        this.number = number;
        this.holderName = holderName;
        this.expiryDate = expiryDate;
    }
    
    /**
     * Get the card number
     * 
     * @return The card number
     */
    public String getNumber() {
        return number;
    }
    
    /**
     * Get the cardholder name
     * 
     * @return The cardholder name
     */
    public String getHolderName() {
        return holderName;
    }
    
    /**
     * Get the expiry date
     * 
     * @return The expiry date
     */
    public String getExpiryDate() {
        return expiryDate;
    }
    
    /**
     * Get the last four digits of the card number
     * 
     * @return The last four digits
     */
    public String getLastFourDigits() {
        if (number.length() < 4) {
            return number;
        }
        return number.substring(number.length() - 4);
    }
    
    /**
     * Get a masked version of the card number
     * 
     * @return The masked card number (e.g., **** **** **** 1234)
     */
    public String getMaskedNumber() {
        if (number.length() < 4) {
            return number;
        }
        
        String lastFour = getLastFourDigits();
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < number.length() - 4; i++) {
            if (i > 0 && i % 4 == 0) {
                masked.append(" ");
            }
            masked.append("*");
        }
        
        return masked + " " + lastFour;
    }
    
    /**
     * Get a string representation of this card
     * 
     * @return The string representation
     */
    @Override
    public String toString() {
        return String.format("%s - %s (Expires: %s)", getMaskedNumber(), holderName, expiryDate);
    }
}