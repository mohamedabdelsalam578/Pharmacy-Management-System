package utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for database operations
 * Provides methods for connecting to the database, initializing tables,
 * and performing common database operations
 */
public class DatabaseUtil {
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static String dbUrl = null;
    private static String dbUser = null;
    private static String dbPassword = null;
    private static boolean dbAvailable = false;
    
    // SQL script to create database tables
    private static final String CREATE_WALLET_TABLE = 
        "CREATE TABLE IF NOT EXISTS wallets (" +
        "id SERIAL PRIMARY KEY, " +
        "patient_id INTEGER NOT NULL, " +
        "balance DECIMAL(10, 2) NOT NULL DEFAULT 0, " +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "UNIQUE(patient_id)" +
        ")";
    
    private static final String CREATE_WALLET_TRANSACTIONS_TABLE =
        "CREATE TABLE IF NOT EXISTS wallet_transactions (" +
        "id SERIAL PRIMARY KEY, " +
        "wallet_id INTEGER NOT NULL, " +
        "amount DECIMAL(10, 2) NOT NULL, " +
        "transaction_type VARCHAR(50) NOT NULL, " +
        "description VARCHAR(255), " +
        "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "FOREIGN KEY (wallet_id) REFERENCES wallets(id)" +
        ")";
    
    /**
     * Static initializer to load the JDBC driver and set up database connection parameters
     */
    static {
        try {
            // Load the JDBC driver
            Class.forName(JDBC_DRIVER);
            System.out.println("JDBC driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load JDBC driver: " + e.getMessage());
        }
    }
    
    /**
     * Initialize the database connection parameters from environment variables
     * This method should be called before any database operations are performed
     */
    public static void initialize() {
        // Get database connection parameters from environment variables
        dbUrl = System.getenv("DATABASE_URL");
        
        // If DATABASE_URL is not provided, try to construct it from individual components
        if (dbUrl == null || dbUrl.isEmpty()) {
            String host = System.getenv("PGHOST");
            String port = System.getenv("PGPORT");
            String database = System.getenv("PGDATABASE");
            
            if (host != null && port != null && database != null) {
                dbUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
                System.out.println("Constructed database URL: " + dbUrl);
            }
        }
        
        dbUser = System.getenv("PGUSER");
        dbPassword = System.getenv("PGPASSWORD");
        
        // Test the database connection
        try (Connection conn = getConnection()) {
            if (conn != null) {
                dbAvailable = true;
                System.out.println("Database connection test successful");
                
                // Initialize tables
                initializeTables(conn);
            }
        } catch (SQLException e) {
            dbAvailable = false;
            System.err.println("Database connection test failed: " + e.getMessage());
        }
    }
    
    /**
     * Create the necessary database tables if they don't exist
     * 
     * @param conn The database connection
     * @throws SQLException If an error occurs while creating the tables
     */
    private static void initializeTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Create wallet table
            stmt.execute(CREATE_WALLET_TABLE);
            System.out.println("Wallet table created or verified");
            
            // Create wallet transactions table
            stmt.execute(CREATE_WALLET_TRANSACTIONS_TABLE);
            System.out.println("Wallet transactions table created or verified");
        }
    }
    
    /**
     * Get a connection to the database
     * 
     * @return A database connection
     * @throws SQLException If an error occurs while connecting to the database
     */
    public static Connection getConnection() throws SQLException {
        if (dbUrl == null || dbUser == null || dbPassword == null) {
            throw new SQLException("Database connection parameters not initialized");
        }
        
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
    
    /**
     * Check if the database is available
     * 
     * @return true if the database is available, false otherwise
     */
    public static boolean isDatabaseAvailable() {
        return dbAvailable;
    }
    
    /**
     * Print detailed information about the database connection and tables
     */
    public static void printDatabaseStatus() {
        try (Connection conn = getConnection()) {
            System.out.println("\n----- DATABASE STATUS -----");
            
            // Get database metadata
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("Database: " + metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
            System.out.println("JDBC Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());
            System.out.println("URL: " + metaData.getURL());
            
            // List all tables in the database
            System.out.println("\nDatabase Tables:");
            List<String> tables = listTables(conn);
            
            if (tables.isEmpty()) {
                System.out.println("No tables found in the database");
            } else {
                for (String table : tables) {
                    System.out.println("  - " + table);
                    
                    // Count rows in the table
                    int rowCount = countRows(conn, table);
                    System.out.println("    Rows: " + rowCount);
                    
                    // List columns in the table
                    List<String> columns = listColumns(conn, table);
                    System.out.println("    Columns: " + String.join(", ", columns));
                }
            }
            
            System.out.println("--------------------------\n");
        } catch (SQLException e) {
            System.err.println("Error getting database status: " + e.getMessage());
        }
    }
    
    /**
     * List all tables in the database
     * 
     * @param conn The database connection
     * @return A list of table names
     * @throws SQLException If an error occurs while retrieving the table list
     */
    private static List<String> listTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        
        try (ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }
        
        return tables;
    }
    
    /**
     * Count the number of rows in a table
     * 
     * @param conn The database connection
     * @param tableName The name of the table
     * @return The number of rows in the table
     * @throws SQLException If an error occurs while counting the rows
     */
    private static int countRows(Connection conn, String tableName) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName;
        
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    /**
     * List all columns in a table
     * 
     * @param conn The database connection
     * @param tableName The name of the table
     * @return A list of column names
     * @throws SQLException If an error occurs while retrieving the column list
     */
    private static List<String> listColumns(Connection conn, String tableName) throws SQLException {
        List<String> columns = new ArrayList<>();
        
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
        }
        
        return columns;
    }
    
    /**
     * Execute a SQL query that returns a result set
     * 
     * @param query The SQL query to execute
     * @return The result set
     * @throws SQLException If an error occurs while executing the query
     */
    public static ResultSet executeQuery(String query) throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }
    
    /**
     * Execute a SQL update query
     * 
     * @param query The SQL update query to execute
     * @return The number of rows affected
     * @throws SQLException If an error occurs while executing the query
     */
    public static int executeUpdate(String query) throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(query);
        }
    }
    
    /**
     * Execute a SQL update query with parameters
     * 
     * @param query The SQL update query to execute
     * @param params The parameters for the query
     * @return The number of rows affected
     * @throws SQLException If an error occurs while executing the query
     */
    public static int executeUpdate(String query, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Set the parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Close database resources safely
     * 
     * @param resources The database resources to close (Connection, Statement, ResultSet)
     */
    public static void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.err.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }
}