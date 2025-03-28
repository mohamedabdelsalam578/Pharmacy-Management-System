package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a patient's wallet in the system
 * This class is responsible for managing a patient's balance and transactions
 */
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private Patient patient;
    private double balance;
    private List<WalletTransaction> transactions;
    private List<String> creditCards;
    
    /**
     * Constructor for creating a new wallet
     * 
     * @param id The wallet ID
     * @param patient The patient that owns this wallet
     * @param balance The initial balance
     */
    public Wallet(int id, Patient patient, double balance) {
        this.id = id;
        this.patient = patient;
        this.balance = balance;
        this.transactions = new ArrayList<>();
        this.creditCards = new ArrayList<>();
    }
    
    /**
     * Constructor for creating a new wallet with zero balance
     * 
     * @param patient The patient that owns this wallet
     */
    public Wallet(Patient patient) {
        this(0, patient, 0.0);
    }
    
    /**
     * Get the wallet ID
     * 
     * @return The wallet ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Set the wallet ID
     * 
     * @param id The wallet ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Get the patient that owns this wallet
     * 
     * @return The patient
     */
    public Patient getPatient() {
        return patient;
    }
    
    /**
     * Get the current balance
     * 
     * @return The balance
     */
    public double getBalance() {
        return balance;
    }
    
    /**
     * Set the balance
     * 
     * @param balance The new balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    /**
     * Get all transactions for this wallet
     * 
     * @return The list of transactions
     */
    public List<WalletTransaction> getTransactions() {
        return transactions;
    }
    
    /**
     * Set the transactions for this wallet
     * 
     * @param transactions The list of transactions
     */
    public void setTransactions(List<WalletTransaction> transactions) {
        this.transactions = transactions;
    }
    
    /**
     * Add a transaction to this wallet
     * 
     * @param transaction The transaction to add
     */
    public void addTransaction(WalletTransaction transaction) {
        this.transactions.add(transaction);
    }
    
    /**
     * Get all credit cards associated with this wallet
     * 
     * @return The list of credit cards
     */
    public List<String> getCreditCards() {
        return creditCards;
    }
    
    /**
     * Add a credit card to this wallet
     * 
     * @param cardNumber The credit card number to add
     * @return true if the card was added, false if it already exists
     */
    public boolean addCreditCard(String cardNumber) {
        // Validate the card number format (simple validation)
        if (!isValidCreditCardFormat(cardNumber)) {
            return false;
        }
        
        // Check if card already exists
        if (creditCards.contains(cardNumber)) {
            return false;
        }
        
        // Add the card
        creditCards.add(cardNumber);
        return true;
    }
    
    /**
     * Remove a credit card from this wallet
     * 
     * @param cardNumber The credit card number to remove
     * @return true if the card was removed, false if it wasn't found
     */
    public boolean removeCreditCard(String cardNumber) {
        return creditCards.remove(cardNumber);
    }
    
    /**
     * Deposit money into the wallet
     * 
     * @param amount The amount to deposit
     * @param description The description for this deposit
     * @return The created transaction
     */
    public WalletTransaction deposit(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        // Update balance
        balance += amount;
        
        // Create and return the transaction
        WalletTransaction transaction = new WalletTransaction(this, amount, WalletTransaction.DEPOSIT, description);
        transactions.add(transaction);
        return transaction;
    }
    
    /**
     * Withdraw money from the wallet
     * 
     * @param amount The amount to withdraw
     * @param description The description for this withdrawal
     * @return The created transaction
     * @throws IllegalArgumentException If the amount is negative or if there are insufficient funds
     */
    public WalletTransaction withdraw(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        
        // Update balance
        balance -= amount;
        
        // Create and return the transaction
        WalletTransaction transaction = new WalletTransaction(this, amount, WalletTransaction.WITHDRAWAL, description);
        transactions.add(transaction);
        return transaction;
    }
    
    /**
     * Make a payment from the wallet
     * 
     * @param amount The amount to pay
     * @param description The description for this payment
     * @return The created transaction
     * @throws IllegalArgumentException If the amount is negative or if there are insufficient funds
     */
    public WalletTransaction makePayment(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        
        // Update balance
        balance -= amount;
        
        // Create and return the transaction
        WalletTransaction transaction = new WalletTransaction(this, amount, WalletTransaction.PAYMENT, description);
        transactions.add(transaction);
        return transaction;
    }
    
    /**
     * Process a refund to the wallet
     * 
     * @param amount The amount to refund
     * @param description The description for this refund
     * @return The created transaction
     */
    public WalletTransaction processRefund(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }
        
        // Update balance
        balance += amount;
        
        // Create and return the transaction
        WalletTransaction transaction = new WalletTransaction(this, amount, WalletTransaction.REFUND, description);
        transactions.add(transaction);
        return transaction;
    }
    
    /**
     * Check if the wallet has sufficient funds for a payment
     * 
     * @param amount The payment amount
     * @return true if the wallet has sufficient funds, false otherwise
     */
    public boolean hasSufficientFunds(double amount) {
        return balance >= amount;
    }
    
    /**
     * Get the transaction history for this wallet
     * 
     * @param limit The maximum number of transactions to return (0 for all)
     * @return The list of transactions, sorted by date (most recent first)
     */
    public List<WalletTransaction> getTransactionHistory(int limit) {
        // Sort transactions by date (most recent first)
        transactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));
        
        // Return all transactions if limit is 0 or negative
        if (limit <= 0 || limit >= transactions.size()) {
            return new ArrayList<>(transactions);
        }
        
        // Return only the most recent transactions
        return new ArrayList<>(transactions.subList(0, limit));
    }
    
    /**
     * Get a formatted string representation of the wallet balance
     * 
     * @return The formatted balance
     */
    public String getFormattedBalance() {
        return String.format("%.2f LE", balance);
    }
    
    /**
     * Validate a credit card number format
     * This is a simple validation that checks if the card number has 16 digits
     * In a real application, this would include more sophisticated validation
     * 
     * @param cardNumber The credit card number to validate
     * @return true if the format is valid, false otherwise
     */
    private boolean isValidCreditCardFormat(String cardNumber) {
        // Remove spaces and dashes
        String cleanedNumber = cardNumber.replaceAll("[\\s-]", "");
        
        // Check if the number contains only digits and has a length of 16
        return cleanedNumber.matches("\\d{16}");
    }
    
    /**
     * Get a formatted string representation of the wallet
     * 
     * @return The formatted wallet
     */
    @Override
    public String toString() {
        return String.format("Wallet [ID: %d, Patient: %s, Balance: %s, Cards: %d, Transactions: %d]", 
                id, patient.getUsername(), getFormattedBalance(), creditCards.size(), transactions.size());
    }
}