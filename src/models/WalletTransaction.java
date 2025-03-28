package models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a transaction in a patient's wallet
 * This class is used to track deposits, withdrawals, and payments
 */
public class WalletTransaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Transaction types
    public static final String DEPOSIT = "DEPOSIT";
    public static final String WITHDRAWAL = "WITHDRAWAL";
    public static final String PAYMENT = "PAYMENT";
    public static final String REFUND = "REFUND";
    
    private int id;
    private Wallet wallet;
    private double amount;
    private String type;
    private String description;
    private Date timestamp;
    
    /**
     * Constructor for creating a new transaction
     * 
     * @param wallet The wallet this transaction belongs to
     * @param amount The transaction amount
     * @param type The transaction type (use the constants)
     * @param description The transaction description
     */
    public WalletTransaction(Wallet wallet, double amount, String type, String description) {
        this.id = 0; // Will be set by the database
        this.wallet = wallet;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.timestamp = new Date();
    }
    
    /**
     * Constructor for creating a new transaction with a specific ID and timestamp
     * This is mainly used when loading from the database
     * 
     * @param id The transaction ID
     * @param wallet The wallet this transaction belongs to
     * @param amount The transaction amount
     * @param type The transaction type
     * @param description The transaction description
     * @param timestamp The transaction timestamp
     */
    public WalletTransaction(int id, Wallet wallet, double amount, String type, String description, Date timestamp) {
        this.id = id;
        this.wallet = wallet;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
    }
    
    /**
     * Get the transaction ID
     * 
     * @return The ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Set the transaction ID
     * 
     * @param id The ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Get the wallet this transaction belongs to
     * 
     * @return The wallet
     */
    public Wallet getWallet() {
        return wallet;
    }
    
    /**
     * Get the transaction amount
     * 
     * @return The amount
     */
    public double getAmount() {
        return amount;
    }
    
    /**
     * Get the transaction type
     * 
     * @return The type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Get the transaction description
     * 
     * @return The description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the transaction timestamp
     * 
     * @return The timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }
    
    /**
     * Get a formatted string representation of the timestamp
     * 
     * @return The formatted timestamp
     */
    public String getFormattedTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(timestamp);
    }
    
    /**
     * Get a formatted string representation of the transaction amount
     * This includes the sign (+ for deposits/refunds, - for withdrawals/payments)
     * 
     * @return The formatted amount
     */
    public String getFormattedAmount() {
        String sign = "";
        
        if (type.equals(DEPOSIT) || type.equals(REFUND)) {
            sign = "+";
        } else if (type.equals(WITHDRAWAL) || type.equals(PAYMENT)) {
            sign = "-";
        }
        
        return String.format("%s%.2f LE", sign, amount);
    }
    
    /**
     * Get a formatted string representation of this transaction
     * 
     * @return The formatted transaction
     */
    @Override
    public String toString() {
        return String.format("%s: %s - %s (%s)", 
                getFormattedTimestamp(), type, getFormattedAmount(), description);
    }
    
    /**
     * Get a detailed string representation of this transaction
     * 
     * @return The detailed string
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction Details:\n");
        sb.append("  ID: ").append(id).append("\n");
        sb.append("  Type: ").append(type).append("\n");
        sb.append("  Amount: ").append(getFormattedAmount()).append("\n");
        sb.append("  Date: ").append(getFormattedTimestamp()).append("\n");
        sb.append("  Description: ").append(description).append("\n");
        sb.append("  Wallet ID: ").append(wallet.getId()).append("\n");
        sb.append("  Patient: ").append(wallet.getPatient().getUsername()).append("\n");
        return sb.toString();
    }
}