import services.PharmacyService;
import utils.DataInitializer;
import utils.FileHandler;
import utils.DatabaseUtil;
import utils.ConsoleUI;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;

/**
 * 🏥 Welcome to the Pharmacy Management System! 🏥
 * Test credentials:
 *    - Admin: username "admin", password "admin123"
 *    - Patient: username "amr", password "alice123"
 *    - Doctor: username "drmohamed", password "doctor123"
 *    - Pharmacist: username "fatima", password "pharm123"
 */
public class PharmacyManagementSystem {
    private static final Logger LOGGER = Logger.getLogger(PharmacyManagementSystem.class.getName());
    
    /**
     * Main method to start the application
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Pharmacy Management System...");
        ConsoleUI.printColoredText("EL-TA3BAN PHARMACY MANAGEMENT SYSTEM", ConsoleUI.CYAN);
        ConsoleUI.printColoredText("----------------------------------------", ConsoleUI.CYAN);
        
        // Initialize file system for data storage
        FileHandler.initializeFiles();
        
        // Initialize database connection
        try {
            DatabaseUtil.initialize();
            if (DatabaseUtil.isDatabaseAvailable()) {
                ConsoleUI.printColoredText("✅ Database connection established successfully", ConsoleUI.GREEN);
                System.out.println("Wallet transactions will be stored in the database");
                
                // Show detailed database status in verbose mode
                if (args.length > 0 && args[0].equalsIgnoreCase("--verbose")) {
                    DatabaseUtil.printDatabaseStatus();
                }
            } else {
                ConsoleUI.printColoredText("⚠️ Database connection not available", ConsoleUI.YELLOW);
                System.out.println("Falling back to file-based storage for wallet transactions");
                
                // In verbose mode, show detailed connection problem information
                if (args.length > 0 && args[0].equalsIgnoreCase("--verbose")) {
                    System.out.println("\nCheck that your database environment variables are correctly set:");
                    DatabaseUtil.printDatabaseStatus();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing database: {0}", e.getMessage());
            ConsoleUI.printColoredText("❌ Error connecting to database: " + e.getMessage(), ConsoleUI.RED);
            System.out.println("The system will use file-based storage instead");
        }
        
        // Parse command line arguments
        boolean extendedMode = true;
        boolean verboseMode = false;
        
        // Process all command line arguments
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--basic")) {
                extendedMode = false;
            } else if (arg.equalsIgnoreCase("--verbose")) {
                verboseMode = true;
            } else if (arg.equalsIgnoreCase("--help")) {
                printHelpMessage();
                return; // Exit after showing help
            } else if (arg.equalsIgnoreCase("--init-db")) {
                // Initialize database and exit
                initializeDatabase();
                return;
            }
        }
        
        // Print mode information
        if (extendedMode) {
            System.out.println("Starting with extended healthcare functionality...");
        } else {
            System.out.println("Starting in basic mode (Admin and Patient only)");
        }
        
        if (verboseMode) {
            System.out.println("Running in verbose mode with additional debug information");
        }
        
        // Create the pharmacy service
        PharmacyService pharmacyService = new PharmacyService();
        
        // Initialize with appropriate test data
        if (extendedMode) {
            DataInitializer.initializeExtendedSystem(pharmacyService);
        } else {
            DataInitializer.initializeBasicSystem(pharmacyService);
        }
        
        // Start the system
        pharmacyService.run(extendedMode);
    }
    
    /**
     * Print help message showing command line options
     */
    private static void printHelpMessage() {
        ConsoleUI.printColoredText("EL-TA3BAN PHARMACY MANAGEMENT SYSTEM", ConsoleUI.CYAN);
        ConsoleUI.printColoredText("----------------------------------------", ConsoleUI.CYAN);
        System.out.println("\nUSAGE:");
        System.out.println("  java PharmacyManagementSystem [options]");
        
        System.out.println("\nOPTIONS:");
        System.out.println("  --help              Display this help message");
        System.out.println("  --basic             Run in basic mode (Admin and Patient only)");
        System.out.println("  --verbose           Show detailed debugging information");
        System.out.println("  --init-db           Initialize database and exit");
        
        System.out.println("\nDATABASE CONFIGURATION:");
        System.out.println("  The system will use PostgreSQL database if available");
        System.out.println("  Set the following environment variables to configure database connection:");
        System.out.println("    DATABASE_URL      Database connection URL");
        System.out.println("    PGHOST           Database host");
        System.out.println("    PGPORT           Database port");
        System.out.println("    PGDATABASE       Database name");
        System.out.println("    PGUSER           Database username");
        System.out.println("    PGPASSWORD       Database password");
        
        System.out.println("\nEXAMPLES:");
        System.out.println("  java PharmacyManagementSystem");
        System.out.println("    Run in extended mode with all user roles");
        
        System.out.println("  java PharmacyManagementSystem --basic");
        System.out.println("    Run in basic mode with Admin and Patient roles only");
        
        System.out.println("  java PharmacyManagementSystem --verbose");
        System.out.println("    Run in extended mode with detailed debug information");
        
        System.out.println("  java PharmacyManagementSystem --basic --verbose");
        System.out.println("    Run in basic mode with detailed debug information");
        
        System.out.println("  java PharmacyManagementSystem --init-db");
        System.out.println("    Initialize database tables and verify connection");
        
        System.out.println("\nTEST CREDENTIALS:");
        System.out.println("  Admin:       username \"admin\", password \"admin123\"");
        System.out.println("  Patient:     username \"amr\", password \"alice123\"");
        System.out.println("  Doctor:      username \"drmohamed\", password \"doctor123\"");
        System.out.println("  Pharmacist:  username \"fatima\", password \"pharm123\"");
    }
    
    /**
     * Test database connection and initialize tables
     * This method can be called to explicitly initialize the database
     */
    private static void initializeDatabase() {
        try {
            System.out.println("Testing database connection...");
            DatabaseUtil.initialize();
            
            if (DatabaseUtil.isDatabaseAvailable()) {
                System.out.println("Database connection successful");
                
                // Get database connection and close it immediately
                try (Connection conn = DatabaseUtil.getConnection()) {
                    System.out.println("Connected to database: " + conn.getMetaData().getURL());
                }
                
                // Print complete database status
                DatabaseUtil.printDatabaseStatus();
            } else {
                System.out.println("Failed to connect to database");
                System.out.println("Check that your database environment variables are set correctly");
            }
        } catch (Exception e) {
            System.out.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
