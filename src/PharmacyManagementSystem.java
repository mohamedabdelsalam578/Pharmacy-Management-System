import services.PharmacyService;
import utils.DataInitializer;
import utils.FileHandler;

/**
 * 🏥 Welcome to the Pharmacy Management System! 🏥
 * 
 * Hello TA and team members! This is a comprehensive pharmacy system I built using
 * Object-Oriented Programming principles we learned in class. Here's what's special about it:
 * 
 * ✅ Complete Healthcare Workflow:
 *    - Connects patients, doctors, and pharmacists in one integrated system
 *    - Features Egyptian localization with local names, medicines, and currency (LE)
 *    - Maintains data even when you exit the program (using file-based storage)
 * 
 * ✅ OOP Concepts Applied:
 *    - Inheritance hierarchy with User as parent class and specialized roles as child classes
 *    - Encapsulation to protect data with private fields and accessor methods
 *    - Polymorphism through role-specific behaviors in different user types
 *    - Abstraction with service layers that handle business logic separately from models
 * 
 * ✅ Main Features:
 *    - Admin functionalities: manage medicines, view reports, add/remove users
 *    - Patient functionalities: create account, order medicines, view prescriptions
 *    - Doctor functionalities: create prescriptions and medical reports for patients
 *    - Pharmacist functionalities: fill prescriptions, manage pharmacy inventory
 * 
 * The project is structured in packages to maintain clean separation of concerns:
 *    - models: Data structures representing real-world entities
 *    - services: Business logic for different system roles
 *    - utils: Helper classes for data handling and initialization
 * 
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
        
        // Start the interactive console interface with extended mode always true
        pharmacyService.run(true);
    }
}
