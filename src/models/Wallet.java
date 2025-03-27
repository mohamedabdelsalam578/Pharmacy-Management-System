
package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private final Patient owner; // Composition - wallet belongs to one patient
    private double balance;
    private List<Transaction> transactions;
    private List<CreditCard> creditCards;
    
    public Wallet(Patient owner) {
        this.owner = owner;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        this.creditCards = new ArrayList<>();
    }
    
    public Patient getOwner() { return owner; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return transactions; }
    
    public boolean deposit(double amount, String description) {
        if (amount <= 0) return false;
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
    
    public boolean withdraw(double amount, String description) {
        if (amount <= 0 || amount > balance) return false;
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
    
    public boolean makePayment(double amount, int orderId) {
        if (amount <= 0 || amount > balance) return false;
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
    
    public void displayInfo() {
        System.out.println("Patient: " + owner.getName());
        System.out.println("Current Balance: " + String.format("%.2f LE", balance));
        System.out.println("Total Transactions: " + transactions.size());
    }
    
    public void displayTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        System.out.println("\n🧾 ===== TRANSACTION HISTORY ===== 🧾");
        System.out.printf("%-5s %-12s %-20s %-10s %-25s\n", 
                         "ID", "Type", "Description", "Amount", "Date/Time");
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
    
    public List<CreditCard> getSavedCards() {
        return creditCards;
    }
    
    public boolean addCard(String cardNumber, String cardHolderName, String expiryDate, String cardType) {
        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        
        for (CreditCard card : creditCards) {
            if (card.getLastFourDigits().equals(lastFourDigits)) {
                return false;
            }
        }
        
        creditCards.add(new CreditCard(cardNumber, cardHolderName, expiryDate, cardType));
        return true;
    }
    
    public boolean removeCard(String lastFourDigits) {
        return creditCards.removeIf(card -> card.getLastFourDigits().equals(lastFourDigits));
    }
    
    public void displaySavedCards() {
        if (creditCards.isEmpty()) {
            System.out.println("No saved cards found.");
            return;
        }
        
        System.out.println("\n💳 ===== SAVED CARDS ===== 💳");
        for (int i = 0; i < creditCards.size(); i++) {
            CreditCard card = creditCards.get(i);
            System.out.println("\nCard #" + (i + 1));
            card.displayInfo();
        }
    }
    
    public static class Transaction {
        private int id;
        private double amount;
        private String type;
        private String description;
        private LocalDateTime dateTime;
        
        public Transaction(int id, double amount, String type, String description, LocalDateTime dateTime) {
            this.id = id;
            this.amount = amount;
            this.type = type;
            this.description = description;
            this.dateTime = dateTime;
        }
        
        public int getId() { return id; }
        public double getAmount() { return amount; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public LocalDateTime getDateTime() { return dateTime; }
    }
}
