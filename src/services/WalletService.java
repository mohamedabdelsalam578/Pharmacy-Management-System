package services;

import models.Patient;
import models.Order;
import models.Wallet;
import models.Wallet.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing patient wallets and payments
 * Handles wallet operations like deposits, withdrawals, and payments
 */
public class WalletService {
    private List<Wallet> wallets;
    
    /**
     * Constructor to initialize wallet service
     */
    public WalletService() {
        this.wallets = new ArrayList<>();
    }
    
    /**
     * Set the wallets list
     * 
     * @param wallets List of wallets to set
     */
    public void setWallets(List<Wallet> wallets) {
        this.wallets = wallets;
    }
    
    /**
     * Get all wallets
     * 
     * @return List of all wallets
     */
    public List<Wallet> getWallets() {
        return wallets;
    }
    
    /**
     * Find a wallet by patient ID
     * 
     * @param patientId Patient ID to find wallet for
     * @return Wallet for the patient, or null if not found
     */
    public Wallet getWalletByPatientId(int patientId) {
        return wallets.stream()
                     .filter(w -> w.getOwner().getId() == patientId)
                     .findFirst()
                     .orElse(null);
    }
    
    /**
     * Create a new wallet for a patient
     * 
     * @param patient Patient to create wallet for
     * @return Created wallet
     */
    public Wallet createWallet(Patient patient) {
        // Create new wallet
        Wallet wallet = new Wallet(patient);
        
        // Add to wallets list
        wallets.add(wallet);
        
        // Set patient's wallet
        patient.setWallet(wallet);
        
        return wallet;
    }
    
    /**
     * Deposit money into a patient's wallet
     * 
     * @param patientId Patient ID to deposit money for
     * @param amount Amount to deposit
     * @param description Description of deposit
     * @return True if deposit successful, false otherwise
     */
    public boolean deposit(int patientId, double amount, String description) {
        Wallet wallet = getWalletByPatientId(patientId);
        
        if (wallet == null) {
            System.out.println("Wallet not found for patient ID: " + patientId);
            return false;
        }
        
        return wallet.deposit(amount, description);
    }
    
    /**
     * Withdraw money from a patient's wallet
     * 
     * @param patientId Patient ID to withdraw money from
     * @param amount Amount to withdraw
     * @param description Description of withdrawal
     * @return True if withdrawal successful, false otherwise
     */
    public boolean withdraw(int patientId, double amount, String description) {
        Wallet wallet = getWalletByPatientId(patientId);
        
        if (wallet == null) {
            System.out.println("Wallet not found for patient ID: " + patientId);
            return false;
        }
        
        return wallet.withdraw(amount, description);
    }
    
    /**
     * Pay for an order using the patient's wallet
     * 
     * @param order Order to pay for
     * @return True if payment successful, false otherwise
     */
    public boolean payOrder(Order order) {
        // Check if order is already paid
        if (order.isPaid()) {
            System.out.println("Order #" + order.getId() + " is already paid.");
            return false;
        }
        
        // Get wallet for the patient
        Wallet wallet = getWalletByPatientId(order.getPatientId());
        
        if (wallet == null) {
            System.out.println("Wallet not found for patient ID: " + order.getPatientId());
            return false;
        }
        
        // Check if wallet has enough balance
        double orderTotal = order.calculateTotal();
        
        if (wallet.getBalance() < orderTotal) {
            System.out.println("Insufficient balance in wallet.");
            System.out.println("Current balance: " + String.format("%.2f LE", wallet.getBalance()));
            System.out.println("Required amount: " + String.format("%.2f LE", orderTotal));
            return false;
        }
        
        // Make payment
        boolean paid = wallet.makePayment(orderTotal, order.getId());
        
        if (paid) {
            // Update order status
            order.setPaid(true);
            System.out.println("Payment successful! Order #" + order.getId() + " is now paid.");
            System.out.println("New wallet balance: " + String.format("%.2f LE", wallet.getBalance()));
        }
        
        return paid;
    }
    
    /**
     * Display wallet information for a patient
     * 
     * @param patientId Patient ID to display wallet for
     */
    public void displayWalletInfo(int patientId) {
        Wallet wallet = getWalletByPatientId(patientId);
        
        if (wallet == null) {
            System.out.println("Wallet not found for patient ID: " + patientId);
            return;
        }
        
        System.out.println("\n💰 ===== WALLET INFORMATION ===== 💰");
        wallet.displayInfo();
    }
    
    /**
     * Display transaction history for a patient
     * 
     * @param patientId Patient ID to display transactions for
     */
    public void displayTransactionHistory(int patientId) {
        Wallet wallet = getWalletByPatientId(patientId);
        
        if (wallet == null) {
            System.out.println("Wallet not found for patient ID: " + patientId);
            return;
        }
        
        wallet.displayTransactions();
    }
}