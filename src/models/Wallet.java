package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a wallet in the pharmacy system
 */
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * Represents a payment card in the wallet
     */
    public static class Card implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String number;
        private String holderName;
        private String expiryDate;
        private String cardType;
        private Date addedDate;
        
        /**
         * Constructor for creating a new card
         * 
         * @param number The card number
         * @param holderName The card holder name
         * @param expiryDate The card expiry date
         * @param cardType The card type
         */
        public Card(String number, String holderName, String expiryDate, String cardType) {
            this.number = number;
            this.holderName = holderName;
            this.expiryDate = expiryDate;
            this.cardType = cardType;
            this.addedDate = new Date();
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
         * Get the card holder name
         * 
         * @return The card holder name
         */
        public String getHolderName() {
            return holderName;
        }
        
        /**
         * Get the card expiry date
         * 
         * @return The card expiry date
         */
        public String getExpiryDate() {
            return expiryDate;
        }
        
        /**
         * Get the card type
         * 
         * @return The card type
         */
        public String getCardType() {
            return cardType;
        }
        
        /**
         * Get the date this card was added to the wallet
         * 
         * @return The added date
         */
        public Date getAddedDate() {
            return addedDate;
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
         * @return The masked card number
         */
        public String getMaskedNumber() {
            if (number.length() <= 4) {
                return number;
            }
            
            return "**** **** **** " + getLastFourDigits();
        }
        
        /**
         * Check if this card is valid (not expired)
         * 
         * @return true if valid, false otherwise
         */
        public boolean isValid() {
            // Simple validation - just check if the expiry date is in the format MM/YY
            if (!expiryDate.matches("\\d{2}/\\d{2}")) {
                return false;
            }
            
            // In a real system, we would check if the card is expired
            // For now, we assume all cards are valid
            return true;
        }
        
        /**
         * Get a formatted string representation of this card
         * 
         * @return The formatted string
         */
        @Override
        public String toString() {
            return String.format("%s (**** **** **** %s) [%s]", 
                    cardType, getLastFourDigits(), expiryDate);
        }
    }
    
    private int patientId;
    private String patientUsername;
    private double balance;
    private Map<String, Card> cards;
    private List<WalletTransaction> transactions;
    
    /**
     * Constructor for creating a new wallet
     * 
     * @param patient The patient who owns this wallet
     */
    public Wallet(Patient patient) {
        this.patientId = patient.getId();
        this.patientUsername = patient.getUsername();
        this.balance = 0.0;
        this.cards = new HashMap<>();
        this.transactions = new ArrayList<>();
    }
    
    /**
     * Constructor for creating a new wallet with an initial balance
     * 
     * @param patient The patient who owns this wallet
     * @param initialBalance The initial balance
     */
    public Wallet(Patient patient, double initialBalance) {
        this(patient);
        
        if (initialBalance > 0) {
            this.balance = initialBalance;
            
            // Record the initial deposit
            WalletTransaction initialDeposit = new WalletTransaction(
                    this.patientId, 
                    WalletTransaction.Type.DEPOSIT, 
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
     * @param source The source of the deposit
     * @return The transaction representing this deposit
     * @throws IllegalArgumentException If the amount is negative
     */
    public WalletTransaction deposit(double amount, String source) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        balance += amount;
        
        WalletTransaction transaction = new WalletTransaction(
                patientId, 
                WalletTransaction.Type.DEPOSIT, 
                amount, 
                source, 
                balance);
        
        transactions.add(transaction);
        
        return transaction;
    }
    
    /**
     * Withdraw money from the wallet
     * 
     * @param amount The amount to withdraw
     * @param reason The reason for the withdrawal
     * @return true if successful, false if insufficient funds
     * @throws IllegalArgumentException If the amount is negative
     */
    public boolean withdraw(double amount, String reason) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (balance < amount) {
            return false;
        }
        
        balance -= amount;
        
        WalletTransaction transaction = new WalletTransaction(
                patientId, 
                WalletTransaction.Type.WITHDRAWAL, 
                amount, 
                reason, 
                balance);
        
        transactions.add(transaction);
        
        return true;
    }
    
    /**
     * Add a card to the wallet
     * 
     * @param cardNumber The card number
     * @param cardHolderName The card holder name
     * @param expiryDate The card expiry date
     * @param cardType The card type
     * @return true if the card was added, false if it already exists
     */
    public boolean addCard(String cardNumber, String cardHolderName, String expiryDate, String cardType) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }
        
        String lastFourDigits = getLastFourDigits(cardNumber);
        
        if (cards.containsKey(lastFourDigits)) {
            return false;
        }
        
        Card card = new Card(cardNumber, cardHolderName, expiryDate, cardType);
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
    public List<WalletTransaction> getTransactions() {
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
        List<WalletTransaction> recentTransactions = getTransactions();
        
        if (recentTransactions.size() > transactionLimit) {
            recentTransactions = recentTransactions.subList(
                    recentTransactions.size() - transactionLimit, 
                    recentTransactions.size());
        }
        
        if (!recentTransactions.isEmpty()) {
            System.out.println("\n🧾 ===== RECENT TRANSACTIONS ===== 🧾");
            
            for (WalletTransaction transaction : recentTransactions) {
                System.out.println("- " + transaction.getFormattedDescription());
            }
        }
        
        System.out.println();
    }
    
    /**
     * Get a formatted string representation of this wallet
     * 
     * @return The formatted string
     */
    @Override
    public String toString() {
        return String.format("Wallet [PatientID: %d, Username: %s, Balance: %s, Cards: %d, Transactions: %d]", 
                patientId, patientUsername, getFormattedBalance(), getCardCount(), getTransactionCount());
    }
}