package repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Patient;
import models.Wallet;
import models.WalletTransaction;
import utils.DatabaseUtil;

/**
 * Repository class for handling wallet database operations
 * Implements the repository pattern for data access and persistence
 */
public class WalletRepository {

    /**
     * Create a new wallet for a patient in the database
     * 
     * @param patient The patient to create a wallet for
     * @return The created wallet
     * @throws SQLException If an error occurs while creating the wallet
     */
    public Wallet createWallet(Patient patient) throws SQLException {
        Wallet wallet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // Check if wallet already exists for this patient
            String checkQuery = "SELECT * FROM wallets WHERE patient_id = ?";
            stmt = conn.prepareStatement(checkQuery);
            stmt.setInt(1, patient.getId());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Wallet already exists, return it
                wallet = new Wallet(rs.getInt("id"), patient, rs.getDouble("balance"));
                System.out.println("Existing wallet found for patient " + patient.getUsername());
                return wallet;
            }
            
            // Close resources to prepare for insert
            DatabaseUtil.closeResources(rs, stmt);
            
            // Create a new wallet
            String insertQuery = "INSERT INTO wallets (patient_id, balance) VALUES (?, ?) RETURNING id";
            stmt = conn.prepareStatement(insertQuery);
            stmt.setInt(1, patient.getId());
            stmt.setDouble(2, 0.0); // Initial balance is zero
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int walletId = rs.getInt("id");
                wallet = new Wallet(walletId, patient, 0.0);
                System.out.println("Created new wallet for patient " + patient.getUsername());
            }
            
            return wallet;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }
    
    /**
     * Get the wallet for a patient from the database
     * 
     * @param patient The patient to get the wallet for
     * @return The patient's wallet or null if not found
     * @throws SQLException If an error occurs while retrieving the wallet
     */
    public Wallet getWalletByPatient(Patient patient) throws SQLException {
        Wallet wallet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String query = "SELECT * FROM wallets WHERE patient_id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, patient.getId());
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int walletId = rs.getInt("id");
                double balance = rs.getDouble("balance");
                wallet = new Wallet(walletId, patient, balance);
            }
            
            return wallet;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }
    
    /**
     * Update a wallet's balance in the database
     * 
     * @param wallet The wallet to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If an error occurs while updating the wallet
     */
    public boolean updateWallet(Wallet wallet) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String query = "UPDATE wallets SET balance = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setDouble(1, wallet.getBalance());
            stmt.setInt(2, wallet.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }
    
    /**
     * Add a transaction to a wallet
     * 
     * @param wallet The wallet to add the transaction to
     * @param amount The transaction amount
     * @param type The transaction type
     * @param description The transaction description
     * @return The created wallet transaction
     * @throws SQLException If an error occurs while adding the transaction
     */
    public WalletTransaction addTransaction(Wallet wallet, double amount, String type, String description) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String query = "INSERT INTO wallet_transactions (wallet_id, amount, transaction_type, description) " +
                           "VALUES (?, ?, ?, ?) RETURNING id, timestamp";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, wallet.getId());
            stmt.setDouble(2, amount);
            stmt.setString(3, type);
            stmt.setString(4, description);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int transactionId = rs.getInt("id");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                WalletTransaction transaction = new WalletTransaction(transactionId, wallet, amount, type, description, new Date(timestamp.getTime()));
                return transaction;
            }
            
            return null;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }
    
    /**
     * Get all transactions for a wallet
     * 
     * @param wallet The wallet to get transactions for
     * @return A list of wallet transactions
     * @throws SQLException If an error occurs while retrieving the transactions
     */
    public List<WalletTransaction> getTransactionsByWallet(Wallet wallet) throws SQLException {
        List<WalletTransaction> transactions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String query = "SELECT * FROM wallet_transactions WHERE wallet_id = ? ORDER BY timestamp DESC";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, wallet.getId());
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                double amount = rs.getDouble("amount");
                String type = rs.getString("transaction_type");
                String description = rs.getString("description");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                
                WalletTransaction transaction = new WalletTransaction(id, wallet, amount, type, description, new Date(timestamp.getTime()));
                transactions.add(transaction);
            }
            
            return transactions;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }
    
    /**
     * Delete all data for a wallet (including transactions)
     * This is mainly used for testing purposes
     * 
     * @param wallet The wallet to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If an error occurs while deleting the wallet
     */
    public boolean deleteWallet(Wallet wallet) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Delete all transactions for this wallet
            String deleteTransactionsQuery = "DELETE FROM wallet_transactions WHERE wallet_id = ?";
            stmt = conn.prepareStatement(deleteTransactionsQuery);
            stmt.setInt(1, wallet.getId());
            stmt.executeUpdate();
            
            // Close statement and prepare for next query
            stmt.close();
            
            // Delete the wallet
            String deleteWalletQuery = "DELETE FROM wallets WHERE id = ?";
            stmt = conn.prepareStatement(deleteWalletQuery);
            stmt.setInt(1, wallet.getId());
            int rowsAffected = stmt.executeUpdate();
            
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
            DatabaseUtil.closeResources(stmt, conn);
        }
    }
}