package models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a transaction in the pharmacy system
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * Represents the types of transactions
     */
    public enum Type {
        DEPOSIT("Deposit"),
        WITHDRAWAL("Withdrawal"),
        PAYMENT("Payment"),
        REFUND("Refund");
        
        private final String displayName;
        
        Type(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String id;
    private int patientId;
    private Type type;
    private double amount;
    private String description;
    private double balanceAfter;
    private Date timestamp;
    
    /**
     * Constructor for a transaction
     * 
     * @param patientId The ID of the patient
     * @param type The type of transaction
     * @param amount The amount of the transaction
     * @param description The description of the transaction
     * @param balanceAfter The balance after the transaction
     */
    public Transaction(int patientId, Type type, double amount, String description, double balanceAfter) {
        this.id = UUID.randomUUID().toString();
        this.patientId = patientId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.timestamp = new Date();
    }
    
    /**
     * Get the transaction ID
     * 
     * @return The ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get the patient ID
     * 
     * @return The patient ID
     */
    public int getPatientId() {
        return patientId;
    }
    
    /**
     * Get the transaction type
     * 
     * @return The type
     */
    public Type getType() {
        return type;
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
     * Get the transaction description
     * 
     * @return The description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the balance after the transaction
     * 
     * @return The balance after
     */
    public double getBalanceAfter() {
        return balanceAfter;
    }
    
    /**
     * Get the transaction timestamp
     * 
     * @return The timestamp
     */
    public Date getTimestampDate() {
        return timestamp;
    }
    
    /**
     * Get the formatted timestamp
     * 
     * @return The formatted timestamp
     */
    public String getTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(timestamp);
    }
    
    /**
     * Get a string representation of this transaction
     * 
     * @return The string representation
     */
    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f LE - %s", getTimestamp(), type.getDisplayName(), amount, description);
    }
}