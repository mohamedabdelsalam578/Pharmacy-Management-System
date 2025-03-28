package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a patient's wallet in the system
 * This class is responsible for managing a patient's balance and transactions
 */
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Credit card class for storing card information
    public static class CreditCard implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String cardNumber;
        private String cardHolderName;
        private String expiryDate;
        private String cardType;
        
        public CreditCard(String cardNumber, String cardHolderName, String expiryDate, String cardType) {
            this.cardNumber = cardNumber;
            this.cardHolderName = cardHolderName;
            this.expiryDate = expiryDate;
            this.cardType = cardType;
        }
        
        public String getCardNumber() {
            return cardNumber;
        }
        
        public String getMaskedCardNumber() {
            if (cardNumber.length() <= 4) {
                return cardNumber;
            }
            
            String lastFour = cardNumber.substring(cardNumber.length() - 4);
            StringBuilder masked = new StringBuilder();
            
            for (int i = 0; i < cardNumber.length() - 4; i++) {
                masked.append("*");
            }
            
            masked.append(lastFour);
            return masked.toString();
        }
        
        public String getLastFourDigits() {
            if (cardNumber.length() <= 4) {
                return cardNumber;
            }
            
            return cardNumber.substring(cardNumber.length() - 4);
        }
        
        public String getCardHolderName() {
            return cardHolderName;
        }
        
        public String getExpiryDate() {
            return expiryDate;
        }
        
        public String getCardType() {
            return cardType;
        }
        
        @Override
        public String toString() {
            return String.format("%s ending in %s (Expires: %s)", 
                    cardType, getLastFourDigits(), expiryDate);
        }
    }
    
    private int id;
    private Patient patient;
    private double balance;
    private List<WalletTransaction> transactions;
    private Map<String, CreditCard> creditCards;
    
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
        this.creditCards = new HashMap<>();
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
    public List<CreditCard> getSavedCards() {
        return new ArrayList<>(creditCards.values());
    }
    
    /**
     * Add a credit card to this wallet
     * 
     * @param cardNumber The credit card number
     * @param cardHolderName The card holder name
     * @param expiryDate The expiry date (MM/YY format)
     * @param cardType The card type (e.g., Visa, MasterCard)
     * @return true if the card was added, false if it already exists or is invalid
     */
    public boolean addCard(String cardNumber, String cardHolderName, String expiryDate, String cardType) {
        // Validate the card number format (simple validation)
        if (!isValidCreditCardFormat(cardNumber)) {
            return false;
        }
        
        // Check if card already exists
        if (creditCards.containsKey(cardNumber)) {
            return false;
        }
        
        // Create a new credit card
        CreditCard card = new CreditCard(cardNumber, cardHolderName, expiryDate, cardType);
        
        // Add the card
        creditCards.put(cardNumber, card);
        return true;
    }
    
    /**
     * Remove a credit card from this wallet by last four digits
     * 
     * @param lastFourDigits The last four digits of the card to remove
     * @return true if the card was removed, false if it wasn't found
     */
    public boolean removeCard(String lastFourDigits) {
        for (Map.Entry<String, CreditCard> entry : creditCards.entrySet()) {
            if (entry.getValue().getLastFourDigits().equals(lastFourDigits)) {
                creditCards.remove(entry.getKey());
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if this wallet has a card with the specified card number
     * 
     * @param cardNumber The credit card number to check
     * @return true if the wallet has the card, false otherwise
     */
    public boolean hasCard(String cardNumber) {
        return creditCards.containsKey(cardNumber);
    }
    
    /**
     * Deposit money into the wallet
     * 
     * @param amount The amount to deposit
     * @param description The description for this deposit
     * @return true if the deposit was successful, false otherwise
     */
    public boolean deposit(double amount, String description) {
        if (amount <= 0) {
            return false; // Invalid amount
        }
        
        try {
            // Update balance
            balance += amount;
            
            // Create and add the transaction
            WalletTransaction transaction = new WalletTransaction(this, amount, WalletTransaction.DEPOSIT, description);
            transactions.add(transaction);
            
            return true;
        } catch (Exception e) {
            // If an error occurs, revert the balance change
            balance -= amount;
            System.err.println("Error processing deposit: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Withdraw money from the wallet
     * 
     * @param amount The amount to withdraw
     * @param description The description for this withdrawal
     * @return true if the withdrawal was successful, false otherwise
     */
    public boolean withdraw(double amount, String description) {
        if (amount <= 0) {
            return false; // Invalid amount
        }
        
        if (amount > balance) {
            return false; // Insufficient funds
        }
        
        try {
            // Update balance
            balance -= amount;
            
            // Create and add the transaction
            WalletTransaction transaction = new WalletTransaction(this, amount, WalletTransaction.WITHDRAWAL, description);
            transactions.add(transaction);
            
            return true;
        } catch (Exception e) {
            // If an error occurs, revert the balance change
            balance += amount;
            System.err.println("Error processing withdrawal: " + e.getMessage());
            return false;
        }
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
     * Display detailed wallet information in the console
     */
    public void displayInfo() {
        System.out.println("================================================");
        System.out.println("               WALLET INFORMATION               ");
        System.out.println("================================================");
        System.out.println("Patient: " + patient.getFirstName() + " " + patient.getLastName());
        System.out.println("Current Balance: " + getFormattedBalance());
        System.out.println("Saved Cards: " + creditCards.size());
        System.out.println("Transaction History: " + transactions.size() + " transactions");
        System.out.println("================================================");
        
        // Display saved cards if any
        if (!creditCards.isEmpty()) {
            System.out.println("\nSaved Cards:");
            System.out.println("------------------------------------------------");
            
            int index = 1;
            for (CreditCard card : getSavedCards()) {
                System.out.printf("%d. %s (%s)\n", index++, card.getMaskedCardNumber(), card.getCardType());
            }
            
            System.out.println("------------------------------------------------");
        }
        
        // Display recent transactions if any
        if (!transactions.isEmpty()) {
            System.out.println("\nRecent Transactions:");
            System.out.println("------------------------------------------------");
            
            // Get the 5 most recent transactions
            List<WalletTransaction> recentTransactions = getTransactionHistory(5);
            
            for (WalletTransaction transaction : recentTransactions) {
                System.out.println(transaction.toString());
            }
            
            System.out.println("------------------------------------------------");
        }
    }
    
    /**
     * Display all wallet transactions in the console
     */
    public void displayTransactions() {
        System.out.println("================================================");
        System.out.println("              TRANSACTION HISTORY               ");
        System.out.println("================================================");
        System.out.println("Patient: " + patient.getFirstName() + " " + patient.getLastName());
        System.out.println("Current Balance: " + getFormattedBalance());
        System.out.println("Total Transactions: " + transactions.size());
        System.out.println("================================================");
        
        if (transactions.isEmpty()) {
            System.out.println("\nNo transactions found.");
            return;
        }
        
        // Sort transactions by date (most recent first)
        transactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));
        
        System.out.println("\nAll Transactions:");
        System.out.println("------------------------------------------------");
        
        for (WalletTransaction transaction : transactions) {
            System.out.println(transaction.toString());
        }
        
        System.out.println("------------------------------------------------");
    }
    
    /**
     * Display all saved cards in the console
     */
    public void displaySavedCards() {
        System.out.println("================================================");
        System.out.println("                  SAVED CARDS                   ");
        System.out.println("================================================");
        System.out.println("Patient: " + patient.getFirstName() + " " + patient.getLastName());
        System.out.println("Total Cards: " + creditCards.size());
        System.out.println("================================================");
        
        if (creditCards.isEmpty()) {
            System.out.println("\nNo cards saved yet.");
            return;
        }
        
        System.out.println("\nAll Saved Cards:");
        System.out.println("------------------------------------------------");
        
        int index = 1;
        for (CreditCard card : getSavedCards()) {
            System.out.printf("%d. %s\n", index++, card.toString());
        }
        
        System.out.println("------------------------------------------------");
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