package models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 💰 Wallet - Digital payment system for patients in EL-TA3BAN pharmacy 💰
 * 
 * This class implements a complete financial management system for patients,
 * allowing them to deposit funds, make payments, and track transaction history
 * without relying on external payment processors.
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - Encapsulation: Private fields with controlled access protect financial data
 * - Composition: Wallet contains Transaction objects and CreditCard objects
 * - Inner Classes: Transaction class is nested to show strong relationship
 * - Data Validation: All financial operations include validation checks
 * 
 * 📚 Class Responsibilities:
 * - Manages patient account balance with deposit/withdrawal operations
 * - Records and displays complete transaction history with Egyptian currency (LE)
 * - Handles secure storage of credit card information with masking
 * - Processes payments for pharmacy orders directly from wallet balance
 * 
 * 🌐 Role in System:
 * The Wallet serves as the financial backbone of the patient experience,
 * enabling a complete e-commerce workflow within the pharmacy system while
 * demonstrating secure financial transaction handling.
 */
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int patientId;
    private double balance;
    private List<Transaction> transactions;
    private List<CreditCard> savedCards;
    
    /**
     * Constructor to initialize wallet
     * 
     * @param id Wallet ID
     * @param patientId Patient ID associated with this wallet
     */
    public Wallet(int id, int patientId) {
        this.id = id;
        this.patientId = patientId;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        this.savedCards = new ArrayList<>();
    }
    
    /**
     * Get wallet ID
     * 
     * @return Wallet ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Get patient ID
     * 
     * @return Patient ID
     */
    public int getPatientId() {
        return patientId;
    }
    
    /**
     * Get current wallet balance
     * 
     * @return Wallet balance
     */
    public double getBalance() {
        return balance;
    }
    
    /**
     * Get list of wallet transactions
     * 
     * @return List of transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    /**
     * 💰 deposit - Adds funds to the patient's wallet balance
     * 
     * This method ensures proper validation of deposit amounts and records
     * a complete transaction history entry with timestamp for auditing.
     * 
     * @param amount Amount to deposit in Egyptian Pounds (LE)
     * @param description Description of deposit for transaction history
     * @return True if deposit successful, false if amount is invalid
     */
    public boolean deposit(double amount, String description) {
        if (amount <= 0) {
            return false;
        }
        
        balance += amount;
        transactions.add(new Transaction(
            transactions.size() + 1,
            amount,
            "DEPOSIT",
            description,
            LocalDateTime.now()
        ));
        
        return true;
    }
    
    /**
     * Withdraw money from wallet
     * 
     * @param amount Amount to withdraw
     * @param description Description of withdrawal
     * @return True if withdrawal successful, false otherwise
     */
    public boolean withdraw(double amount, String description) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        
        balance -= amount;
        transactions.add(new Transaction(
            transactions.size() + 1,
            amount,
            "WITHDRAWAL",
            description,
            LocalDateTime.now()
        ));
        
        return true;
    }
    
    /**
     * 🛒 makePayment - Processes a payment for a pharmacy order
     * 
     * This method implements the core payment functionality of the wallet system,
     * deducting funds from the patient's balance and creating a detailed
     * transaction record with order reference for tracking.
     * 
     * @param amount Amount to pay in Egyptian Pounds (LE)
     * @param orderId Order ID to link payment with specific order
     * @return True if payment successful, false if insufficient funds or invalid amount
     */
    public boolean makePayment(double amount, int orderId) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        
        balance -= amount;
        transactions.add(new Transaction(
            transactions.size() + 1,
            amount,
            "PAYMENT",
            "Payment for Order #" + orderId,
            LocalDateTime.now()
        ));
        
        return true;
    }
    
    /**
     * 📊 displayInfo - Shows the wallet's current status and balance
     * 
     * Formats the wallet information with Egyptian Pound (LE) currency
     * and provides a clear summary of the wallet's financial state.
     */
    public void displayInfo() {
        System.out.println("Wallet ID: " + id);
        System.out.println("Patient ID: " + patientId);
        System.out.println("Current Balance: " + String.format("%.2f LE", balance));
        System.out.println("Total Transactions: " + transactions.size());
    }
    
    /**
     * Display wallet transaction history
     */
    public void displayTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        System.out.println("\n🧾 ===== TRANSACTION HISTORY ===== 🧾");
        System.out.printf("%-5s %-12s %-20s %-10s %-25s\n", "ID", "Type", "Description", "Amount", "Date/Time");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (Transaction transaction : transactions) {
            System.out.printf("%-5d %-12s %-20s %-9.2f LE %-25s\n",
                transaction.getId(),
                transaction.getType(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getDateTime().toString().replace("T", " "));
        }
    }
    
    /**
     * Get list of saved credit cards
     * 
     * @return List of saved credit cards
     */
    public List<CreditCard> getSavedCards() {
        return savedCards;
    }
    
    /**
     * 💳 addCard - Securely stores a payment card in the patient's wallet
     * 
     * This method demonstrates secure handling of sensitive financial data
     * by implementing card masking and duplicate detection. The system only
     * stores partial card information for security best practices.
     * 
     * @param cardNumber Full credit card number (will be masked in storage)
     * @param cardHolderName Name of the Egyptian card holder
     * @param expiryDate Card expiration date in MM/YY format
     * @param cardType Type of card (Visa, Mastercard, Meeza, etc.)
     * @return True if card added successfully, false if duplicate detected
     */
    public boolean addCard(String cardNumber, String cardHolderName, String expiryDate, String cardType) {
        // Check if card already exists (by last 4 digits)
        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        
        for (CreditCard card : savedCards) {
            if (card.getLastFourDigits().equals(lastFourDigits)) {
                return false; // Card already exists
            }
        }
        
        // Add new card
        savedCards.add(new CreditCard(cardNumber, cardHolderName, expiryDate, cardType));
        return true;
    }
    
    /**
     * Remove a credit card from wallet
     * 
     * @param lastFourDigits Last 4 digits of card to remove
     * @return True if card removed successfully, false otherwise
     */
    public boolean removeCard(String lastFourDigits) {
        for (CreditCard card : savedCards) {
            if (card.getLastFourDigits().equals(lastFourDigits)) {
                savedCards.remove(card);
                return true;
            }
        }
        
        return false; // Card not found
    }
    
    /**
     * Display saved credit cards
     */
    public void displaySavedCards() {
        if (savedCards.isEmpty()) {
            System.out.println("No saved cards found.");
            return;
        }
        
        System.out.println("\n💳 ===== SAVED CARDS ===== 💳");
        for (int i = 0; i < savedCards.size(); i++) {
            CreditCard card = savedCards.get(i);
            System.out.println("\nCard #" + (i + 1));
            card.displayInfo();
        }
    }
    
    /**
     * 🧾 Transaction - Inner class for tracking financial operations
     * 
     * This nested class demonstrates the OOP concept of cohesion by keeping
     * strongly related functionality (financial transactions) encapsulated
     * within the Wallet class itself. It provides a complete audit trail
     * for all wallet operations.
     * 
     * 🔑 OOP Concepts Demonstrated:
     * - Encapsulation: All transaction details are private with controlled access
     * - Static Inner Class: Independent of specific Wallet instance but logically related
     * - Immutability: Transaction objects cannot be modified after creation (audit security)
     */
    public static class Transaction implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private int id;
        private double amount;
        private String type;  // DEPOSIT, WITHDRAWAL, PAYMENT
        private String description;
        private LocalDateTime dateTime;
        
        /**
         * Constructor to initialize transaction
         * 
         * @param id Transaction ID
         * @param amount Transaction amount
         * @param type Transaction type
         * @param description Transaction description
         * @param dateTime Transaction date/time
         */
        public Transaction(int id, double amount, String type, String description, LocalDateTime dateTime) {
            this.id = id;
            this.amount = amount;
            this.type = type;
            this.description = description;
            this.dateTime = dateTime;
        }
        
        /**
         * Get transaction ID
         * 
         * @return Transaction ID
         */
        public int getId() {
            return id;
        }
        
        /**
         * Get transaction amount
         * 
         * @return Transaction amount
         */
        public double getAmount() {
            return amount;
        }
        
        /**
         * Get transaction type
         * 
         * @return Transaction type
         */
        public String getType() {
            return type;
        }
        
        /**
         * Get transaction description
         * 
         * @return Transaction description
         */
        public String getDescription() {
            return description;
        }
        
        /**
         * Get transaction date/time
         * 
         * @return Transaction date/time
         */
        public LocalDateTime getDateTime() {
            return dateTime;
        }
    }
}