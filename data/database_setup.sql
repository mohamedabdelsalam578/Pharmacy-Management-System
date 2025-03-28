-- EL-TA3BAN Pharmacy Management System Database Script
-- This script creates all the necessary tables for the pharmacy management system.
-- Execute this script to initialize your PostgreSQL database manually.

-- Note: The system will automatically create these tables when it starts if they don't exist.
-- This script is provided for reference and manual database setup if needed.

-- Create wallet table
CREATE TABLE IF NOT EXISTS wallets (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(patient_id)
);

-- Create wallet transactions table
CREATE TABLE IF NOT EXISTS wallet_transactions (
    id SERIAL PRIMARY KEY,
    wallet_id INTEGER NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);

-- Index for faster lookups by patient_id in wallets table
CREATE INDEX IF NOT EXISTS idx_wallets_patient_id ON wallets(patient_id);

-- Index for faster lookups by wallet_id in wallet_transactions table
CREATE INDEX IF NOT EXISTS idx_wallet_transactions_wallet_id ON wallet_transactions(wallet_id);

-- Example query to view all wallets
-- SELECT * FROM wallets;

-- Example query to view all wallet transactions
-- SELECT * FROM wallet_transactions;

-- Example query to view a patient's wallet balance and transactions
-- SELECT w.id as wallet_id, w.patient_id, w.balance, 
--        wt.id as transaction_id, wt.amount, wt.transaction_type, wt.description, wt.timestamp
-- FROM wallets w
-- LEFT JOIN wallet_transactions wt ON w.id = wt.wallet_id
-- WHERE w.patient_id = [PATIENT_ID]
-- ORDER BY wt.timestamp DESC;

-- Example query to add funds to a wallet
-- UPDATE wallets SET balance = balance + [AMOUNT], updated_at = CURRENT_TIMESTAMP WHERE id = [WALLET_ID];
-- INSERT INTO wallet_transactions (wallet_id, amount, transaction_type, description)
-- VALUES ([WALLET_ID], [AMOUNT], 'DEPOSIT', 'Manual fund addition');

-- Example query to get transaction summary by type
-- SELECT transaction_type, SUM(amount) as total_amount, COUNT(*) as transaction_count
-- FROM wallet_transactions
-- GROUP BY transaction_type
-- ORDER BY total_amount DESC;

-- Example command to backup your database
-- pg_dump -h [HOSTNAME] -p [PORT] -U [USERNAME] -W -F c -b -v -f [FILENAME] [DATABASE]

-- Example command to restore your database from backup
-- pg_restore -h [HOSTNAME] -p [PORT] -U [USERNAME] -W -d [DATABASE] -v [FILENAME]