package models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a transaction in a wallet
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
     * Constructor for creating a new wallet transaction
     * 
     * @param id The transaction ID
     * @param wallet The wallet associated with this transaction
     * @param amount The transaction amount
     * @param type The transaction type (DEPOSIT, WITHDRAWAL, PAYMENT, etc.)
     * @param description A description of the transaction
     * @param timestamp The timestamp when the transaction occurred
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
     * Constructor for creating a new wallet transaction with current timestamp
     * 
     * @param wallet The wallet associated with this transaction
     * @param amount The transaction amount
     * @param type The transaction type (DEPOSIT, WITHDRAWAL, PAYMENT, etc.)
     * @param description A description of the transaction
     */
    public WalletTransaction(Wallet wallet, double amount, String type, String description) {
        this(0, wallet, amount, type, description, new Date());
    }
    
    /**
     * Get the transaction ID
     * 
     * @return The transaction ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Set the transaction ID
     * 
     * @param id The transaction ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Get the wallet associated with this transaction
     * 
     * @return The wallet
     */
    public Wallet getWallet() {
        return wallet;
    }
    
    /**
     * Get the transaction amount
     * 
     * @return The transaction amount
     */
    public double getAmount() {
        return amount;
    }
    
    /**
     * Get the transaction type
     * 
     * @return The transaction type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Get the transaction description
     * 
     * @return The transaction description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the transaction timestamp
     * 
     * @return The transaction timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }
    
    /**
     * Get a formatted string representation of the transaction timestamp
     * 
     * @return The formatted timestamp
     */
    public String getFormattedTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(timestamp);
    }
    
    /**
     * Get a sign indicator for the transaction amount based on the transaction type
     * 
     * @return "+" for deposits and refunds, "-" for withdrawals and payments
     */
    public String getAmountSign() {
        return (type.equals(DEPOSIT) || type.equals(REFUND)) ? "+" : "-";
    }
    
    /**
     * Get a formatted string representation of the transaction
     * 
     * @return The formatted transaction
     */
    @Override
    public String toString() {
        String sign = getAmountSign();
        String amountStr = String.format("%.2f LE", Math.abs(amount));
        String formattedTimestamp = getFormattedTimestamp();
        
        return String.format("[%s] %s%s - %s (%s)", 
                formattedTimestamp, sign, amountStr, type, description);
    }
}