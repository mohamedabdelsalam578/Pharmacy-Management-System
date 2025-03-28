package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a wallet in the pharmacy system
 */
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int patientId;
    private String patientUsername;
    private double balance;
    private Map<String, Card> cards;
    private List<Transaction> transactions;
    
    /**
     * Represents a transaction in the pharmacy system
     */
    public static class Transaction implements Serializable {
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
         * Get the date/time of this transaction
         * Used by FileHandler for persistence
         * 
         * @return The date/time
         */
        public Date getDateTime() {
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
    
    /**
     * Constructor for creating a new wallet with just a patient ID
     * 
     * @param patientId The ID of the patient who owns this wallet
     */
    public Wallet(int patientId) {
        this.patientId = patientId;
        this.patientUsername = "patient" + patientId; // Default username
        this.balance = 0.0;
        this.cards = new HashMap<>();
        this.transactions = new ArrayList<>();
    }
    
    /**
     * Constructor for creating a new wallet
     * 
     * @param patientId The ID of the patient who owns this wallet
     * @param patientUsername The username of the patient who owns this wallet
     */
    public Wallet(int patientId, String patientUsername) {
        this.patientId = patientId;
        this.patientUsername = patientUsername;
        this.balance = 0.0;
        this.cards = new HashMap<>();
        this.transactions = new ArrayList<>();
    }
    
    /**
     * Constructor for creating a new wallet with an initial balance
     * 
     * @param patientId The ID of the patient who owns this wallet
     * @param patientUsername The username of the patient who owns this wallet
     * @param initialBalance The initial balance
     */
    public Wallet(int patientId, String patientUsername, double initialBalance) {
        this(patientId, patientUsername);
        
        if (initialBalance > 0) {
            this.balance = initialBalance;
            
            // Record the initial deposit
            Transaction initialDeposit = new Transaction(
                    this.patientId, 
                    Transaction.Type.DEPOSIT, 
                    initialBalance, 
                    "Initial Balance", 
                    this.balance);
            
            this.transactions.add(initialDeposit);
        }
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
     * Get the patient username
     * 
     * @return The patient username
     */
    public String getPatientUsername() {
        return patientUsername;
    }
    
    /**
     * Get the balance
     * 
     * @return The balance
     */
    public double getBalance() {
        return balance;
    }
    
    /**
     * Get a formatted string representation of the balance
     * 
     * @return The formatted balance
     */
    public String getFormattedBalance() {
        return String.format("%.2f LE", balance);
    }
    
    /**
     * Deposit money into the wallet
     * 
     * @param amount The amount to deposit
     * @param description The description for this deposit
     * @return true if the deposit was successful
     * @throws IllegalArgumentException If the amount is negative
     */
    public boolean deposit(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        balance += amount;
        
        Transaction transaction = new Transaction(
                patientId, 
                Transaction.Type.DEPOSIT, 
                amount, 
                description, 
                balance);
        
        transactions.add(transaction);
        
        return true;
    }
    
    /**
     * Withdraw money from the wallet
     * 
     * @param amount The amount to withdraw
     * @param description The description for this withdrawal
     * @return true if successful, false if insufficient funds
     * @throws IllegalArgumentException If the amount is negative
     */
    public boolean withdraw(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (balance < amount) {
            return false;
        }
        
        balance -= amount;
        
        Transaction transaction = new Transaction(
                patientId, 
                Transaction.Type.WITHDRAWAL, 
                amount, 
                description, 
                balance);
        
        transactions.add(transaction);
        
        return true;
    }
    
    /**
     * Make a payment from the wallet
     * 
     * @param amount The amount to pay
     * @param description The description for this payment
     * @return true if successful, false if insufficient funds
     */
    public boolean makePayment(double amount, String description) {
        return withdraw(amount, "Payment: " + description);
    }
    
    /**
     * Process a refund to the wallet
     * 
     * @param amount The amount to refund
     * @param description The description for this refund
     * @return true if the refund was successful
     */
    public boolean processRefund(double amount, String description) {
        return deposit(amount, "Refund: " + description);
    }
    
    /**
     * Add a card to the wallet
     * 
     * @param cardNumber The card number
     * @param holderName The card holder name
     * @param expiryDate The card expiry date
     * @return true if the card was added, false if it already exists
     */
    public boolean addCard(String cardNumber, String holderName, String expiryDate) {
        return addCard(cardNumber, holderName, expiryDate, "Credit");
    }
    
    /**
     * Add a card to this wallet with card type
     * 
     * @param cardNumber The card number
     * @param holderName The holder name
     * @param expiryDate The expiry date
     * @param cardType The card type
     * @return true if added, false otherwise
     */
    public boolean addCard(String cardNumber, String holderName, String expiryDate, String cardType) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }
        
        String lastFourDigits = getLastFourDigits(cardNumber);
        
        if (cards.containsKey(lastFourDigits)) {
            return false;
        }
        
        Card card = new Card(cardNumber, holderName, expiryDate);
        cards.put(lastFourDigits, card);
        
        return true;
    }
    
    /**
     * Remove a card from the wallet
     * 
     * @param lastFourDigits The last four digits of the card to remove
     * @return true if removed, false if not found
     */
    public boolean removeCard(String lastFourDigits) {
        if (lastFourDigits == null || lastFourDigits.trim().isEmpty()) {
            return false;
        }
        
        if (!cards.containsKey(lastFourDigits)) {
            return false;
        }
        
        cards.remove(lastFourDigits);
        return true;
    }
    
    /**
     * Get a card by its last four digits
     * 
     * @param lastFourDigits The last four digits
     * @return The card, or null if not found
     */
    public Card getCard(String lastFourDigits) {
        return cards.get(lastFourDigits);
    }
    
    /**
     * Check if a card with the given number exists in the wallet
     * 
     * @param cardNumber The card number
     * @return true if the card exists, false otherwise
     */
    public boolean hasCard(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }
        
        String lastFourDigits = getLastFourDigits(cardNumber);
        return cards.containsKey(lastFourDigits);
    }
    
    /**
     * Get the last four digits of a card number
     * 
     * @param cardNumber The card number
     * @return The last four digits
     */
    private String getLastFourDigits(String cardNumber) {
        if (cardNumber.length() < 4) {
            return cardNumber;
        }
        
        return cardNumber.substring(cardNumber.length() - 4);
    }
    
    /**
     * Get all cards in the wallet
     * 
     * @return The cards
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards.values());
    }
    
    /**
     * Get the number of cards in the wallet
     * 
     * @return The number of cards
     */
    public int getCardCount() {
        return cards.size();
    }
    
    /**
     * Get all transactions in the wallet
     * 
     * @return The transactions
     */
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    /**
     * Get the number of transactions in the wallet
     * 
     * @return The number of transactions
     */
    public int getTransactionCount() {
        return transactions.size();
    }
    
    /**
     * Display all transactions in the wallet
     */
    public void displayTransactions() {
        System.out.println("\n🧾 ===== TRANSACTION HISTORY ===== 🧾");
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        for (Transaction transaction : transactions) {
            System.out.println("- " + transaction.toString());
        }
    }
    
    /**
     * Display wallet information including balance, cards, and recent transactions
     */
    public void displayInfo() {
        System.out.println("\n💰 ===== WALLET INFO ===== 💰");
        System.out.println("Patient ID: " + patientId);
        System.out.println("Patient Username: " + patientUsername);
        System.out.println("Balance: " + getFormattedBalance());
        System.out.println("Cards: " + getCardCount());
        
        if (getCardCount() > 0) {
            System.out.println("\n💳 ===== CARDS ===== 💳");
            for (Card card : getCards()) {
                System.out.println("- " + card);
            }
        }
        
        int transactionLimit = 5;
        List<Transaction> recentTransactions = getTransactions();
        
        if (recentTransactions.size() > transactionLimit) {
            recentTransactions = recentTransactions.subList(
                    recentTransactions.size() - transactionLimit, 
                    recentTransactions.size());
        }
        
        if (!recentTransactions.isEmpty()) {
            System.out.println("\n🧾 ===== RECENT TRANSACTIONS ===== 🧾");
            
            for (Transaction transaction : recentTransactions) {
                System.out.println("- " + transaction.toString());
            }
        }
    }
    
    /**
     * Get a string representation of this wallet
     * 
     * @return The string representation
     */
    @Override
    public String toString() {
        return String.format("Wallet [PatientID: %d, Username: %s, Balance: %s, Cards: %d, Transactions: %d]", 
                patientId, patientUsername, getFormattedBalance(), getCardCount(), getTransactionCount());
    }
    
    /**
     * Represents a credit card stored in a wallet
     */
    public static class Card implements Serializable {
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
}