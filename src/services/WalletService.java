package services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Patient;
import models.Wallet;
import models.Wallet.Transaction;
import models.Wallet.TransactionType;
import repositories.WalletRepository;
import utils.ConsoleUI;
import utils.DatabaseUtil;
import utils.FileHandler;

/**
 * Service class for wallet operations
 * This class provides methods for managing wallets and their transactions
 */
public class WalletService {
    private static final String WALLETS_DIR = "data/wallet";
    private static final String WALLET_FILE_FORMAT = WALLETS_DIR + "/wallet_%d.dat";
    
    // Map of wallets by patient ID
    private Map<Integer, Wallet> walletMap;
    
    // Database repository
    private WalletRepository repository;
    
    /**
     * Constructor
     * Initializes the wallet map and repository
     */
    public WalletService() {
        this.walletMap = new HashMap<>();
        this.repository = new WalletRepository();
        
        // Create the wallets directory if it doesn't exist
        File dir = new File(WALLETS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Get a patient's wallet
     * Creates a new wallet if one doesn't exist
     * 
     * @param patient The patient
     * @return The patient's wallet
     */
    public Wallet getWallet(Patient patient) {
        // Check if wallet is in memory
        Wallet wallet = walletMap.get(patient.getId());
        
        if (wallet == null) {
            // Not in memory, try to get from database if available
            if (DatabaseUtil.isDatabaseAvailable()) {
                try {
                    wallet = repository.getWalletByPatient(patient);
                    
                    if (wallet != null) {
                        // Wallet found in database, add to memory
                        walletMap.put(patient.getId(), wallet);
                        
                        // Load transactions
                        List<Transaction> transactions = repository.getTransactionsByWallet(wallet);
                        wallet.setTransactions(transactions);
                        
                        return wallet;
                    }
                } catch (SQLException e) {
                    System.err.println("Error retrieving wallet from database: " + e.getMessage());
                    // Fall back to file-based storage
                }
            }
            
            // Try to load from file
            wallet = loadWalletFromFile(patient);
            
            // If still null, create a new wallet
            if (wallet == null) {
                try {
                    if (DatabaseUtil.isDatabaseAvailable()) {
                        // Create in database
                        wallet = repository.createWallet(patient);
                    } else {
                        // Create in memory only
                        wallet = new Wallet(patient);
                    }
                    
                    // Add to memory map
                    walletMap.put(patient.getId(), wallet);
                    
                    // Save to file as backup
                    saveWalletToFile(wallet);
                } catch (SQLException e) {
                    System.err.println("Error creating wallet in database: " + e.getMessage());
                    // Fall back to memory-only wallet
                    wallet = new Wallet(patient);
                    walletMap.put(patient.getId(), wallet);
                }
            }
        }
        
        return wallet;
    }
    
    /**
     * Load a wallet from file
     * 
     * @param patient The patient
     * @return The wallet or null if not found
     */
    private Wallet loadWalletFromFile(Patient patient) {
        String filename = String.format(WALLET_FILE_FORMAT, patient.getId());
        File file = new File(filename);
        
        if (!file.exists()) {
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Wallet wallet = (Wallet) ois.readObject();
            walletMap.put(patient.getId(), wallet);
            return wallet;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading wallet from file: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Save a wallet to file
     * 
     * @param wallet The wallet to save
     * @return true if saved successfully, false otherwise
     */
    private boolean saveWalletToFile(Wallet wallet) {
        String filename = String.format(WALLET_FILE_FORMAT, wallet.getPatient().getId());
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(wallet);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving wallet to file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deposit money into a wallet
     * 
     * @param wallet The wallet to deposit to
     * @param amount The amount to deposit
     * @param description The description for this deposit
     * @return true if the deposit was successful, false otherwise
     */
    public boolean deposit(Wallet wallet, double amount, String description) {
        try {
            // Create transaction in wallet object
            Transaction transaction = wallet.deposit(amount, description);
            
            // Save to database if available
            if (DatabaseUtil.isDatabaseAvailable()) {
                // Update wallet balance
                repository.updateWallet(wallet);
                
                // Add transaction
                repository.addTransaction(wallet, amount, TransactionType.DEPOSIT, description);
            }
            
            // Save to file as backup
            saveWalletToFile(wallet);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error processing deposit: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Withdraw money from a wallet
     * 
     * @param wallet The wallet to withdraw from
     * @param amount The amount to withdraw
     * @param description The description for this withdrawal
     * @return true if the withdrawal was successful, false otherwise
     */
    public boolean withdraw(Wallet wallet, double amount, String description) {
        try {
            // Check if there are sufficient funds
            if (!wallet.hasSufficientFunds(amount)) {
                return false;
            }
            
            // Create transaction in wallet object
            Transaction transaction = wallet.withdraw(amount, description);
            
            // Save to database if available
            if (DatabaseUtil.isDatabaseAvailable()) {
                // Update wallet balance
                repository.updateWallet(wallet);
                
                // Add transaction
                repository.addTransaction(wallet, amount, TransactionType.WITHDRAWAL, description);
            }
            
            // Save to file as backup
            saveWalletToFile(wallet);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error processing withdrawal: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Make a payment from a wallet
     * 
     * @param wallet The wallet to pay from
     * @param amount The amount to pay
     * @param description The description for this payment
     * @return true if the payment was successful, false otherwise
     */
    public boolean makePayment(Wallet wallet, double amount, String description) {
        try {
            // Check if there are sufficient funds
            if (!wallet.hasSufficientFunds(amount)) {
                return false;
            }
            
            // Create transaction in wallet object
            Transaction transaction = wallet.makePayment(amount, description);
            
            // Save to database if available
            if (DatabaseUtil.isDatabaseAvailable()) {
                // Update wallet balance
                repository.updateWallet(wallet);
                
                // Add transaction
                repository.addTransaction(wallet, amount, TransactionType.PAYMENT, description);
            }
            
            // Save to file as backup
            saveWalletToFile(wallet);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Save wallet data to file
     * This should be called periodically to ensure data persistence
     * 
     * @param wallet The wallet to save
     * @return true if saved successfully, false otherwise
     */
    public boolean saveWallet(Wallet wallet) {
        return saveWalletToFile(wallet);
    }
    
    /**
     * Save credit card information for a wallet
     * 
     * @param wallet The wallet to save credit cards for
     * @return true if saved successfully, false otherwise
     */
    public boolean saveCreditCards(Wallet wallet) {
        return saveWalletToFile(wallet);
    }
    
    /**
     * Get transaction history for a wallet
     * 
     * @param wallet The wallet to get history for
     * @param limit The maximum number of transactions to return (0 for all)
     * @return The list of transactions, sorted by date (most recent first)
     */
    public List<Transaction> getTransactionHistory(Wallet wallet, int limit) {
        try {
            // Try to get from database if available
            if (DatabaseUtil.isDatabaseAvailable()) {
                List<Transaction> transactions = repository.getTransactionsByWallet(wallet);
                wallet.setTransactions(transactions);
            }
            
            // Return from wallet object
            return wallet.getTransactionHistory(limit);
        } catch (SQLException e) {
            System.err.println("Error retrieving transaction history: " + e.getMessage());
            // Fall back to in-memory transactions
            return wallet.getTransactionHistory(limit);
        }
    }
    
    /**
     * Process a refund to a wallet
     * 
     * @param wallet The wallet to refund to
     * @param amount The amount to refund
     * @param description The description for this refund
     * @return true if the refund was successful, false otherwise
     */
    public boolean processRefund(Wallet wallet, double amount, String description) {
        try {
            // Create transaction in wallet object
            Transaction transaction = wallet.processRefund(amount, description);
            
            // Save to database if available
            if (DatabaseUtil.isDatabaseAvailable()) {
                // Update wallet balance
                repository.updateWallet(wallet);
                
                // Add transaction
                repository.addTransaction(wallet, amount, TransactionType.REFUND, description);
            }
            
            // Save to file as backup
            saveWalletToFile(wallet);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error processing refund: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Display wallet menu for a patient
     * 
     * @param patient The patient
     * @return true if the user wants to return to the previous menu, false if they want to exit
     */
    public boolean showWalletMenu(Patient patient) {
        Wallet wallet = getWallet(patient);
        
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printColoredText("🏦 WALLET MANAGEMENT", ConsoleUI.CYAN);
            System.out.println("--------------------");
            System.out.println("Welcome, " + patient.getFullName());
            System.out.println("Current balance: " + wallet.getFormattedBalance());
            System.out.println("--------------------");
            System.out.println("1. 💰 Deposit money");
            System.out.println("2. 💸 Withdraw money");
            System.out.println("3. 📋 View transaction history");
            System.out.println("4. 💳 Manage credit cards");
            System.out.println("5. ↩️ Return to previous menu");
            System.out.println("0. ❌ Exit");
            
            int choice = ConsoleUI.readIntInput("Enter your choice: ", 0, 5);
            
            switch (choice) {
                case 1:
                    handleDeposit(wallet);
                    break;
                
                case 2:
                    handleWithdrawal(wallet);
                    break;
                
                case 3:
                    showTransactionHistory(wallet);
                    break;
                
                case 4:
                    manageCreditCards(wallet);
                    break;
                    
                case 5:
                    return true;
                
                case 0:
                    return false;
            }
        }
    }
    
    /**
     * Handle the deposit process
     * 
     * @param wallet The wallet to deposit to
     */
    private void handleDeposit(Wallet wallet) {
        ConsoleUI.clearScreen();
        ConsoleUI.printColoredText("💰 DEPOSIT MONEY", ConsoleUI.CYAN);
        System.out.println("--------------------");
        System.out.println("Current balance: " + wallet.getFormattedBalance());
        
        double amount = ConsoleUI.readDoubleInput("Enter amount to deposit (LE): ", 0.01, 10000);
        String description = ConsoleUI.readStringInput("Enter description (optional): ");
        
        if (description.trim().isEmpty()) {
            description = "Deposit";
        }
        
        boolean success = deposit(wallet, amount, description);
        
        if (success) {
            ConsoleUI.printColoredText("✅ Deposit successful! New balance: " + wallet.getFormattedBalance(), ConsoleUI.GREEN);
        } else {
            ConsoleUI.printColoredText("❌ Failed to process deposit. Please try again.", ConsoleUI.RED);
        }
        
        ConsoleUI.pressEnterToContinue();
    }
    
    /**
     * Handle the withdrawal process
     * 
     * @param wallet The wallet to withdraw from
     */
    private void handleWithdrawal(Wallet wallet) {
        ConsoleUI.clearScreen();
        ConsoleUI.printColoredText("💸 WITHDRAW MONEY", ConsoleUI.CYAN);
        System.out.println("--------------------");
        System.out.println("Current balance: " + wallet.getFormattedBalance());
        
        double maxAmount = wallet.getBalance();
        
        if (maxAmount < 0.01) {
            ConsoleUI.printColoredText("❌ Insufficient funds for withdrawal.", ConsoleUI.RED);
            ConsoleUI.pressEnterToContinue();
            return;
        }
        
        double amount = ConsoleUI.readDoubleInput("Enter amount to withdraw (LE): ", 0.01, maxAmount);
        String description = ConsoleUI.readStringInput("Enter description (optional): ");
        
        if (description.trim().isEmpty()) {
            description = "Withdrawal";
        }
        
        boolean success = withdraw(wallet, amount, description);
        
        if (success) {
            ConsoleUI.printColoredText("✅ Withdrawal successful! New balance: " + wallet.getFormattedBalance(), ConsoleUI.GREEN);
        } else {
            ConsoleUI.printColoredText("❌ Failed to process withdrawal. Please try again.", ConsoleUI.RED);
        }
        
        ConsoleUI.pressEnterToContinue();
    }
    
    /**
     * Show transaction history for a wallet
     * 
     * @param wallet The wallet to show history for
     */
    private void showTransactionHistory(Wallet wallet) {
        ConsoleUI.clearScreen();
        ConsoleUI.printColoredText("📋 TRANSACTION HISTORY", ConsoleUI.CYAN);
        System.out.println("--------------------");
        System.out.println("Current balance: " + wallet.getFormattedBalance());
        System.out.println("--------------------");
        
        List<Transaction> transactions = getTransactionHistory(wallet, 20);
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (int i = 0; i < transactions.size(); i++) {
                Transaction transaction = transactions.get(i);
                String amountStr = String.format("%.2f LE", transaction.getAmount());
                String sign = transaction.getAmountSign();
                TransactionType type = transaction.getType();
                String description = transaction.getDescription();
                String timestamp = transaction.getFormattedTimestamp();
                
                String color = ConsoleUI.RESET;
                if (type == TransactionType.DEPOSIT || type == TransactionType.REFUND) {
                    color = ConsoleUI.GREEN;
                } else if (type == TransactionType.WITHDRAWAL || type == TransactionType.PAYMENT) {
                    color = ConsoleUI.RED;
                }
                
                System.out.print((i + 1) + ". [" + timestamp + "] ");
                ConsoleUI.printColoredText(sign + amountStr, color);
                System.out.println(" - " + type + " (" + description + ")");
            }
        }
        
        ConsoleUI.pressEnterToContinue();
    }
    
    /**
     * Manage credit cards for a wallet
     * 
     * @param wallet The wallet to manage credit cards for
     */
    private void manageCreditCards(Wallet wallet) {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printColoredText("💳 MANAGE CREDIT CARDS", ConsoleUI.CYAN);
            System.out.println("--------------------");
            
            List<String> creditCards = wallet.getCreditCards();
            
            if (creditCards.isEmpty()) {
                System.out.println("No credit cards added yet.");
            } else {
                System.out.println("Your credit cards:");
                for (int i = 0; i < creditCards.size(); i++) {
                    String card = creditCards.get(i);
                    String maskedCard = maskCardNumber(card);
                    System.out.println((i + 1) + ". " + maskedCard);
                }
            }
            
            System.out.println("--------------------");
            System.out.println("1. 💳 Add new credit card");
            System.out.println("2. ❌ Remove credit card");
            System.out.println("3. ↩️ Return to wallet menu");
            
            int choice = ConsoleUI.readIntInput("Enter your choice: ", 1, 3);
            
            switch (choice) {
                case 1:
                    addCreditCard(wallet);
                    break;
                
                case 2:
                    removeCreditCard(wallet);
                    break;
                
                case 3:
                    return;
            }
        }
    }
    
    /**
     * Add a credit card to a wallet
     * 
     * @param wallet The wallet to add a credit card to
     */
    private void addCreditCard(Wallet wallet) {
        ConsoleUI.clearScreen();
        ConsoleUI.printColoredText("💳 ADD CREDIT CARD", ConsoleUI.CYAN);
        System.out.println("--------------------");
        
        String cardNumber = ConsoleUI.readStringInput("Enter card number (16 digits): ");
        
        boolean success = wallet.addCreditCard(cardNumber);
        
        if (success) {
            ConsoleUI.printColoredText("✅ Credit card added successfully!", ConsoleUI.GREEN);
            
            // Save the updated wallet
            saveCreditCards(wallet);
        } else {
            ConsoleUI.printColoredText("❌ Invalid card number or card already exists.", ConsoleUI.RED);
        }
        
        ConsoleUI.pressEnterToContinue();
    }
    
    /**
     * Remove a credit card from a wallet
     * 
     * @param wallet The wallet to remove a credit card from
     */
    private void removeCreditCard(Wallet wallet) {
        ConsoleUI.clearScreen();
        ConsoleUI.printColoredText("❌ REMOVE CREDIT CARD", ConsoleUI.CYAN);
        System.out.println("--------------------");
        
        List<String> creditCards = wallet.getCreditCards();
        
        if (creditCards.isEmpty()) {
            System.out.println("No credit cards to remove.");
            ConsoleUI.pressEnterToContinue();
            return;
        }
        
        System.out.println("Select a credit card to remove:");
        for (int i = 0; i < creditCards.size(); i++) {
            String card = creditCards.get(i);
            String maskedCard = maskCardNumber(card);
            System.out.println((i + 1) + ". " + maskedCard);
        }
        
        int choice = ConsoleUI.readIntInput("Enter your choice (0 to cancel): ", 0, creditCards.size());
        
        if (choice == 0) {
            return;
        }
        
        String cardToRemove = creditCards.get(choice - 1);
        boolean success = wallet.removeCreditCard(cardToRemove);
        
        if (success) {
            ConsoleUI.printColoredText("✅ Credit card removed successfully!", ConsoleUI.GREEN);
            
            // Save the updated wallet
            saveCreditCards(wallet);
        } else {
            ConsoleUI.printColoredText("❌ Failed to remove credit card.", ConsoleUI.RED);
        }
        
        ConsoleUI.pressEnterToContinue();
    }
    
    /**
     * Mask a credit card number for display
     * Shows only the last 4 digits, replaces the rest with *
     * 
     * @param cardNumber The credit card number to mask
     * @return The masked credit card number
     */
    private String maskCardNumber(String cardNumber) {
        // Remove spaces and dashes
        String cleanedNumber = cardNumber.replaceAll("[\\s-]", "");
        
        if (cleanedNumber.length() < 4) {
            return cleanedNumber;
        }
        
        // Get the last 4 digits
        String lastFour = cleanedNumber.substring(cleanedNumber.length() - 4);
        
        // Create a string of asterisks for the rest of the card number
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < cleanedNumber.length() - 4; i++) {
            masked.append("*");
        }
        
        // Format the masked card number
        return masked + lastFour;
    }
    
    /**
     * Save all wallets to file
     * This should be called periodically to ensure data persistence
     */
    public void saveAllWallets() {
        for (Wallet wallet : walletMap.values()) {
            saveWalletToFile(wallet);
        }
    }
}