import services.PharmacyService;
import utils.DataInitializer;
import utils.FileHandler;
import utils.ConsoleUI;

import java.util.logging.Level;
import java.util.logging.Logger;

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
        
        ConsoleUI.printColoredText("✅ Using file-based storage for all data", ConsoleUI.GREEN);
        
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
        
        System.out.println("\nDATA STORAGE:");
        System.out.println("  The system uses text files for all data storage");
        
        System.out.println("\nEXAMPLES:");
        System.out.println("  java PharmacyManagementSystem");
        System.out.println("    Run in extended mode with all user roles");
        
        System.out.println("  java PharmacyManagementSystem --basic");
        System.out.println("    Run in basic mode with Admin and Patient roles only");
        
        System.out.println("  java PharmacyManagementSystem --verbose");
        System.out.println("    Run in extended mode with detailed debug information");
        
        System.out.println("  java PharmacyManagementSystem --basic --verbose");
        System.out.println("    Run in basic mode with detailed debug information");
        
        System.out.println("\nTEST CREDENTIALS:");
        System.out.println("  Admin:       username \"admin\", password \"admin123\"");
        System.out.println("  Patient:     username \"amr\", password \"alice123\"");
        System.out.println("  Doctor:      username \"drmohamed\", password \"doctor123\"");
        System.out.println("  Pharmacist:  username \"fatima\", password \"pharm123\"");
    }

}
