import services.PharmacyService;
import utils.DataInitializer;
import utils.FileHandler;

/**
 * 🏥 Welcome to the Pharmacy Management System! 🏥
 * Test credentials:
 *    - Admin: username "admin", password "admin123"
 *    - Patient: username "amr", password "alice123"
 *    - Doctor: username "drmohamed", password "doctor123"
 *    - Pharmacist: username "fatima", password "pharm123"
 */
public class PharmacyManagementSystem {
    
    /**
     * Main method to start the application
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Pharmacy Management System...");
        
        // Initialize file system for data storage
        FileHandler.initializeFiles();
        
        // Create the pharmacy service
        PharmacyService pharmacyService = new PharmacyService();
        
        System.out.println("Starting with extended healthcare functionality...");
        // Initialize with extended test data
        DataInitializer.initializeExtendedSystem(pharmacyService);
        pharmacyService.run(true);
    }
}
