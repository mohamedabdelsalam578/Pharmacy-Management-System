package services;

import models.*;
import utils.FileHandler;
import utils.ConsoleUI;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

/**
 * PharmacyService is the main service class that integrates admin and patient services
 */
public class PharmacyService {
    // Singleton instance
    private static PharmacyService instance;
    
    private List<Admin> admins;
    private List<Doctor> doctors;
    private List<Patient> patients;
    private List<Medicine> medicines;
    private List<Order> orders;
    private List<Pharmacist> pharmacists;
    private List<Pharmacy> pharmacies;
    private List<Prescription> prescriptions;
    private List<Consultation> consultations;
    
    private AdminService adminService;
    private PatientService patientService;
    private DoctorService doctorService;
    private PharmacistService pharmacistService;
    private AuthenticationService authService;
    
    private int nextOrderId;
    private Scanner scanner;

    /**
     * Get the singleton instance of PharmacyService
     * 
     * @return The singleton instance
     */
    public static PharmacyService getInstance() {
        if (instance == null) {
            instance = new PharmacyService();
        }
        return instance;
    }

    /**
     * Constructor to initialize PharmacyService
     */
    private PharmacyService() {
        this.admins = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.medicines = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.pharmacists = new ArrayList<>();
        this.pharmacies = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        this.consultations = new ArrayList<>();
        
        this.nextOrderId = 1;
        this.scanner = new Scanner(System.in);
        
        // Initialize data files
        FileHandler.initializeFiles();
        
        // Load data from files
        loadDataFromFiles();
        
        // Find the next order ID
        calculateNextOrderId();
        
        // Ensure all users have unique IDs
        ensureUniqueUserIds();
        
        this.adminService = new AdminService(medicines, orders);
        this.patientService = new PatientService(patients, orders, medicines, nextOrderId);
        this.doctorService = new DoctorService(doctors, patients, prescriptions, medicines, consultations);
        this.pharmacistService = new PharmacistService(pharmacists, pharmacies, prescriptions, medicines);
        this.authService = new AuthenticationService(admins, patients, doctors, pharmacists);
    }
    
    /**
     * Calculate the next order ID based on existing orders
     */
    private void calculateNextOrderId() {
        if (orders.isEmpty()) {
            nextOrderId = 1;
        } else {
            // Find the maximum order ID and add 1
            int maxId = 0;
            for (Order order : orders) {
                if (order.getId() > maxId) {
                    maxId = order.getId();
                }
            }
            nextOrderId = maxId + 1;
        }
    }
    
    /**
     * Load data from files
     */
    private void loadDataFromFiles() {
        // Load data from files
        admins = FileHandler.loadAdmins();
        patients = FileHandler.loadPatients();
        doctors = FileHandler.loadDoctors();
        medicines = FileHandler.loadMedicines();
        pharmacists = FileHandler.loadPharmacists();
        
        // Orders need to be loaded after medicines since they reference medicines
        orders = FileHandler.loadOrders(medicines);
        
        // Ensure all patients have an initialized cartOrder (might be missing in saved files)
        for (Patient patient : patients) {
            if (patient.getCartOrder() == null) {
                patient.setCartOrder(new Order(0, patient.getId()));
            }
        }
        
        // Fix corrupted pharmacists data if needed
        fixPharmacistsData();
        
        // Fix corrupted doctors data if needed
        fixDoctorsData();
        
        // If no data in files, initialize with sample data
        if (admins.isEmpty() && patients.isEmpty() && medicines.isEmpty()) {
            System.out.println("No data found in files. Initializing with sample data.");
            initialize();
            saveDataToFiles();
        }
    }
    
    /**
     * Fix corrupted pharmacists data
     */
    private void fixPharmacistsData() {
        // If pharmacists data is empty but pharmacist file exists, try to load it again
        if (pharmacists.isEmpty()) {
            pharmacists = FileHandler.loadPharmacists();
            System.out.println("Reloaded pharmacists data, found " + pharmacists.size() + " records.");
        }
        
        // Check for duplicate IDs
        boolean hasDuplicates = false;
        Set<Integer> pharmacistIds = new HashSet<>();
        
        for (Pharmacist pharmacist : pharmacists) {
            if (!pharmacistIds.add(pharmacist.getId())) {
                hasDuplicates = true;
                break;
            }
        }
        
        if (hasDuplicates) {
            System.out.println("Fixing duplicate pharmacist IDs...");
            
            // Find the highest ID currently in use
            int maxId = pharmacists.stream()
                .mapToInt(User::getId)
                .max()
                .orElse(0);
            
            // Create a set of used IDs to track which ones are already assigned
            Set<Integer> usedIds = new HashSet<>();
            
            // Fix duplicates by assigning new IDs where needed
            for (Pharmacist pharmacist : pharmacists) {
                if (!usedIds.add(pharmacist.getId())) {
                    // This ID is already used, assign a new unique ID
                    maxId++;
                    System.out.println("Reassigning pharmacist " + pharmacist.getName() + 
                                     " from ID " + pharmacist.getId() + " to " + maxId);
                    pharmacist.setId(maxId);
                    usedIds.add(maxId);
                }
            }
            
            // Save the fixed data
            FileHandler.savePharmacists(pharmacists);
            System.out.println("Pharmacist IDs have been fixed and saved.");
        }
    }
    
    /**
     * Reload pharmacist data from files
     * This public method can be called when pharmacist login fails to ensure data is properly loaded
     */
    public void reloadPharmacistData() {
        try {
            System.out.println("Reloading pharmacist data from files...");
            List<Pharmacist> reloadedPharmacists = FileHandler.loadPharmacists();
            
            // If we found data, replace the current list
            if (reloadedPharmacists != null && !reloadedPharmacists.isEmpty()) {
                System.out.println("Found " + reloadedPharmacists.size() + " pharmacists in data file");
                this.pharmacists = reloadedPharmacists;
                
                // Reinitialize authentication service with updated pharmacist list
                this.authService = new AuthenticationService(admins, patients, doctors, pharmacists);
                
                // Save the data to ensure it's properly persisted
                FileHandler.savePharmacists(pharmacists);
            } else {
                System.out.println("No pharmacist data found in file");
            }
        } catch (Exception e) {
            System.err.println("Error reloading pharmacist data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Fix corrupted doctors data
     */
    private void fixDoctorsData() {
        // Check if doctors are missing or have null names
        if (doctors.isEmpty() || doctors.stream().anyMatch(d -> d.getName() == null || "null".equals(d.getName()))) {
            System.out.println("Fixing corrupted doctors data...");
            // Clear current doctors list
            doctors.clear();
            
            // Initialize with fresh doctor data with unique IDs
            doctors.add(new Doctor(1, "Dr. Ahmed Mahmoud", "dr_ahmed", "1234", "ahmed@elta3ban.com", "01212345678", 
                              "Cardiology", "EGP12345"));
            doctors.add(new Doctor(2, "Dr. Nour El-Din", "dr_nour", "password", "nour@elta3ban.com", "01512345678", 
                              "Neurology", "EGP67890"));
            doctors.add(new Doctor(3, "Dr. Mohamed Hassan", "dr_mohamed", "dr123", "dr.mohamed@hospital.com", "01234567890", 
                              "General Medicine", "Medical License 12345"));
            
            // Save the fixed data
            FileHandler.saveDoctors(doctors);
            System.out.println("Doctors data has been fixed and saved.");
        }
        
        // Check for duplicate IDs and fix them
        Map<Integer, Long> idCounts = doctors.stream()
            .collect(Collectors.groupingBy(User::getId, Collectors.counting()));
        
        // If there are duplicate IDs, reassign them
        boolean hasDuplicates = idCounts.values().stream().anyMatch(count -> count > 1);
        if (hasDuplicates) {
            System.out.println("Fixing duplicate doctor IDs...");
            
            // Find the highest ID currently in use
            int maxId = doctors.stream()
                .mapToInt(User::getId)
                .max()
                .orElse(0);
            
            // Create a set of used IDs to track which ones are already assigned
            Set<Integer> usedIds = new HashSet<>();
            
            // Fix duplicates by assigning new IDs where needed
            for (Doctor doctor : doctors) {
                if (!usedIds.add(doctor.getId())) {
                    // This ID is already used, assign a new unique ID
                    maxId++;
                    System.out.println("Reassigning doctor " + doctor.getName() + 
                                     " from ID " + doctor.getId() + " to " + maxId);
                    doctor.setId(maxId);
                    usedIds.add(maxId);
                }
            }
            
            // Save the fixed data
            FileHandler.saveDoctors(doctors);
            System.out.println("Doctor IDs have been fixed and saved.");
        }
    }
    
    /**
     * Save all data to files
     */
    public void saveDataToFiles() {
        FileHandler.saveAdmins(admins);
        FileHandler.savePatients(patients);
        FileHandler.saveDoctors(doctors);
        FileHandler.saveMedicines(medicines);
        FileHandler.saveOrders(orders);
        FileHandler.savePharmacists(pharmacists);
        
        // Save prescriptions after medicines are saved
        if (prescriptions != null) {
            FileHandler.savePrescriptions(prescriptions);
        }
        
        System.out.println("All data saved to files.");
    }

    /**
     * Get pharmacies managed by this service
     * 
     * @return List of pharmacies
     */
    public List<Pharmacy> getPharmacies() {
        return pharmacies;
    }
    
    /**
     * Get pharmacists managed by this service
     * 
     * @return List of pharmacists
     */
    public List<Pharmacist> getPharmacists() {
        return pharmacists;
    }
    
    /**
     * Get prescriptions managed by this service
     * 
     * @return List of prescriptions
     */
    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }
    
    /**
     * Get consultations managed by this service
     * 
     * @return List of consultations
     */
    public List<Consultation> getConsultations() {
        return consultations;
    }
    
    /**
     * Initialize the pharmacy service with sample data
     */
    public void initialize() {
        // Initialize with some sample data for testing
        initializeAdmins();
        initializeDoctors();
        initializePatients();
        initializeMedicines();
    }

    /**
     * Initialize sample admins with Egyptian names
     */
    private void initializeAdmins() {
        admins.add(new Admin(1, "Mohamed Ahmed", "admin", "admin123", "mohamed@elta3ban.com", "01012345678", 
                            "Manager", "Management"));
        admins.add(new Admin(2, "Fatma Ibrahim", "fatma_admin", "password", "fatma@elta3ban.com", "01112345678", 
                            "Assistant Manager", "Management"));
    }

    /**
     * Initialize sample doctors with Egyptian names
     */
    private void initializeDoctors() {
        doctors.add(new Doctor(1, "Dr. Ahmed Mahmoud", "dr_ahmed", "1234", "ahmed@elta3ban.com", "01212345678", 
                              "Cardiology", "EGP12345"));
        doctors.add(new Doctor(2, "Dr. Nour El-Din", "dr_nour", "password", "nour@elta3ban.com", "01512345678", 
                              "Neurology", "EGP67890"));
    }

    /**
     * Initialize sample patients with Egyptian names
     */
    private void initializePatients() {
        patients.add(new Patient(1, "Amr Khaled", "Amr", "Amr123", "amr@gmail.com", "01112345679", 
                              "22 Tahrir St, Cairo"));
        patients.add(new Patient(2, "Laila Mostafa", "laila_patient", "password", "laila@gmail.com", "01012345670", 
                              "15 Pyramids St, Giza"));
    }

    /**
     * Initialize sample medicines with Egyptian brands and medicines
     */
    private void initializeMedicines() {
        medicines.add(new Medicine(1, "Paramol", "Pain reliever", "EIPICO", 20.50, 100, "Pain Relief", false));
        medicines.add(new Medicine(2, "Megamox", "Antibiotic", "Pharco", 35.75, 50, "Antibiotics", true));
        medicines.add(new Medicine(3, "Lopresor", "Blood pressure medication", "Novartis Egypt", 65.25, 75, "Blood Pressure", true));
        medicines.add(new Medicine(4, "Brufen", "Anti-inflammatory", "Kahira Pharmaceuticals", 18.50, 150, "Pain Relief", false));
        medicines.add(new Medicine(5, "Claritine", "Antihistamine", "Eva Pharma", 32.00, 80, "Allergy", false));
    }

    /**
     * Main method to run the console-based interface
     * 
     * @param extendedMode Whether to run in extended mode with full healthcare workflow
     */
    public void run(boolean extendedMode) {
        boolean exit = false;
        
        // Clear screen and display welcome animation
        ConsoleUI.clearScreen();
        ConsoleUI.typeText("Welcome to the EL-TA3BAN Pharmacy Management System", 20, ConsoleUI.CYAN);
        ConsoleUI.showLoadingSpinner("Loading system components", 2, ConsoleUI.BLUE);
        
        // Print mode information with color
        if (extendedMode) {
            ConsoleUI.printInfo("Running in Extended Mode with full healthcare workflow 🌟");
        } else {
            ConsoleUI.printInfo("Running in Basic Mode with standard pharmacy operations 💊");
        }
        
        while (!exit) {
            // Display stylized header
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader("EL-TA3BAN PHARMACY SYSTEM", 50, ConsoleUI.CYAN);
            ConsoleUI.printLine(50, ConsoleUI.BLUE);
            
            // Display menu options with colored boxes
            ConsoleUI.printMenuItem(1, "Login as Admin", "👨‍💼", ConsoleUI.GREEN);
            ConsoleUI.printMenuItem(2, "Login as Patient", "🧑‍⚕️", ConsoleUI.GREEN);
            
            // Only show these options in extended mode
            if (extendedMode) {
                ConsoleUI.printMenuItem(3, "Login as Doctor", "👨‍⚕️", ConsoleUI.GREEN);
                ConsoleUI.printMenuItem(4, "Login as Pharmacist", "👩‍⚕️", ConsoleUI.GREEN);
                ConsoleUI.printMenuItem(5, "Create Patient Account", "✏️", ConsoleUI.YELLOW);
                ConsoleUI.printMenuItem(6, "Exit", "🚪", ConsoleUI.RED);
            } else {
                ConsoleUI.printMenuItem(3, "Create Patient Account", "✏️", ConsoleUI.YELLOW);
                ConsoleUI.printMenuItem(4, "Exit", "🚪", ConsoleUI.RED);
            }
            
            ConsoleUI.printLine(50, ConsoleUI.BLUE);
            String input = ConsoleUI.promptInput("Enter your choice: ", ConsoleUI.CYAN);
            
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                ConsoleUI.printError("Invalid input. Please enter a number.");
                // Small delay for user to read the error message
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }
            
            if (extendedMode) {
                // Extended mode switch
                switch (choice) {
                    case 1:
                        loginAsAdmin();
                        break;
                    case 2:
                        loginAsPatient();
                        break;
                    case 3:
                        loginAsDoctor();
                        break;
                    case 4:
                        loginAsPharmacist();
                        break;
                    case 5:
                        createPatientAccount();
                        break;
                    case 6:
                        exit = true;
                        saveDataToFiles();
                        System.out.println("Thank you for using the EL-TA3BAN Pharmacy Management System!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                // Basic mode switch
                switch (choice) {
                    case 1:
                        loginAsAdmin();
                        break;
                    case 2:
                        loginAsPatient();
                        break;
                    case 3:
                        createPatientAccount();
                        break;
                    case 4:
                        exit = true;
                        saveDataToFiles();
                        System.out.println("Thank you for using the EL-TA3BAN Pharmacy Management System!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }


    
    /**
     * Get integer input from user
     * 
     * @return The integer entered by the user
     */
    private int getIntInput() {
        return ConsoleUI.readIntInput("", 0, Integer.MAX_VALUE);
    }

    /**
     * Login as admin and display admin menu
     */
    private void loginAsAdmin() {
        String username = ConsoleUI.readStringInput("Enter username: ");
        String password = ConsoleUI.readStringInput("Enter password: ");
        
        Admin admin = authenticateAdmin(username, password);
        
        if (admin == null) {
            System.out.println("Invalid username or password.");
            return;
        }
        
        System.out.println("Welcome, " + admin.getName() + "!");
        boolean logout = false;
        
        while (!logout) {
            System.out.println("\n👨‍💼 ===== ADMIN MENU ===== 👨‍💼");
            System.out.println("1. ➕ Add Medicine");
            System.out.println("2. ➖ Remove Medicine");
            System.out.println("3. 🔄 Update Medicine");
            System.out.println("4. 📊 Generate Medicine Report");
            System.out.println("5. 💰 Generate Revenue Report");
            System.out.println("6. 💊 View All Medicines");
            System.out.println("7. 🛒 View All Orders");
            System.out.println("8. 🚪 Logout");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    addMedicine();
                    break;
                case 2:
                    removeMedicine();
                    break;
                case 3:
                    updateMedicine();
                    break;
                case 4:
                    generateMedicineReport();
                    break;
                case 5:
                    adminService.generateRevenueReport();
                    break;
                case 6:
                    viewAllMedicines();
                    break;
                case 7:
                    viewAllOrders();
                    break;
                case 8:
                    logout = true;
                    saveDataToFiles();
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Authenticate admin credentials
     * 
     * @param username Admin username
     * @param password Admin password
     * @return Admin object if authentication successful, null otherwise
     */
    public Admin authenticateAdmin(String username, String password) {
        return authService.authenticateAdmin(username, password);
    }

    /**
     * Add a new medicine to the pharmacy
     */
    private void addMedicine() {
        System.out.println("\n===== ADD MEDICINE =====");
        
        System.out.print("Enter Medicine ID: ");
        int id = getIntInput();
        
        // Check if medicine with this ID already exists
        if (medicines.stream().anyMatch(m -> m.getId() == id)) {
            System.out.println("Medicine with ID " + id + " already exists.");
            return;
        }
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();
        
        System.out.print("Enter Manufacturer: ");
        String manufacturer = scanner.nextLine();
        
        System.out.print("Enter Price: ");
        double price = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Enter Quantity: ");
        int quantity = getIntInput();
        
        System.out.print("Enter Category: ");
        String category = scanner.nextLine();
        
        System.out.print("Requires Prescription (true/false): ");
        boolean requiresPrescription = Boolean.parseBoolean(scanner.nextLine());
        
        Medicine newMedicine = new Medicine(id, name, description, manufacturer, price, quantity, category, requiresPrescription);
        
        if (adminService.addMedicine(newMedicine)) {
            System.out.println("Medicine added successfully!");
            saveDataToFiles(); // Save data after adding medicine
        } else {
            System.out.println("Failed to add medicine.");
        }
    }

    /**
     * Remove a medicine from the pharmacy
     */
    private void removeMedicine() {
        System.out.println("\n===== REMOVE MEDICINE =====");
        
        System.out.print("Enter Medicine ID to remove: ");
        int id = getIntInput();
        
        if (adminService.removeMedicine(id)) {
            System.out.println("Medicine removed successfully!");
            saveDataToFiles(); // Save data after removing medicine
        } else {
            System.out.println("Failed to remove medicine.");
        }
    }

    /**
     * Update medicine details
     */
    private void updateMedicine() {
        System.out.println("\n===== UPDATE MEDICINE =====");
        
        System.out.print("Enter Medicine ID to update: ");
        int id = getIntInput();
        
        // Check if medicine exists
        Medicine medicine = medicines.stream()
                                    .filter(m -> m.getId() == id)
                                    .findFirst()
                                    .orElse(null);
        
        if (medicine == null) {
            System.out.println("Medicine with ID " + id + " not found.");
            return;
        }
        
        System.out.println("Current details for " + medicine.getName() + ":");
        medicine.displayInfo();
        
        System.out.println("\nEnter new details (leave blank to keep current value):");
        
        System.out.print("Name [" + medicine.getName() + "]: ");
        String name = scanner.nextLine();
        name = name.isEmpty() ? medicine.getName() : name;
        
        System.out.print("Description [" + medicine.getDescription() + "]: ");
        String description = scanner.nextLine();
        description = description.isEmpty() ? medicine.getDescription() : description;
        
        System.out.print("Manufacturer [" + medicine.getManufacturer() + "]: ");
        String manufacturer = scanner.nextLine();
        manufacturer = manufacturer.isEmpty() ? medicine.getManufacturer() : manufacturer;
        
        System.out.print("Price [" + medicine.getPrice() + "]: ");
        String priceStr = scanner.nextLine();
        double price = priceStr.isEmpty() ? medicine.getPrice() : Double.parseDouble(priceStr);
        
        System.out.print("Quantity [" + medicine.getQuantity() + "]: ");
        String quantityStr = scanner.nextLine();
        int quantity = quantityStr.isEmpty() ? medicine.getQuantity() : Integer.parseInt(quantityStr);
        
        System.out.print("Category [" + medicine.getCategory() + "]: ");
        String category = scanner.nextLine();
        category = category.isEmpty() ? medicine.getCategory() : category;
        
        System.out.print("Requires Prescription [" + medicine.isRequiresPrescription() + "]: ");
        String requiresPrescriptionStr = scanner.nextLine();
        boolean requiresPrescription = requiresPrescriptionStr.isEmpty() ? 
                                      medicine.isRequiresPrescription() : 
                                      Boolean.parseBoolean(requiresPrescriptionStr);
        
        if (adminService.updateMedicine(id, name, description, manufacturer, price, quantity, category, requiresPrescription)) {
            System.out.println("Medicine updated successfully!");
            saveDataToFiles(); // Save data after updating medicine
        } else {
            System.out.println("Failed to update medicine.");
        }
    }

    /**
     * Generate medicine reports
     */
    private void generateMedicineReport() {
        System.out.println("\n📊 ===== GENERATE MEDICINE REPORT ===== 📊");
        System.out.println("1. Report for all medicines");
        System.out.println("2. Report for specific medicine");
        System.out.print("Enter your choice: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                adminService.generateMedicineReport();
                break;
            case 2:
                System.out.print("Enter Medicine ID: ");
                int medicineId = getIntInput();
                adminService.generateMedicineReport(medicineId);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * View all medicines in the pharmacy
     */
    private void viewAllMedicines() {
        System.out.println("\n💊 ===== ALL MEDICINES ===== 💊");
        
        if (medicines.isEmpty()) {
            System.out.println("No medicines found in the pharmacy.");
            return;
        }
        
        for (Medicine medicine : medicines) {
            medicine.displayInfo();
            System.out.println("----------------------------------------------------------------");
        }
    }

    /**
     * View all orders in the pharmacy
     */
    private void viewAllOrders() {
        System.out.println("\n🛒 ===== ALL ORDERS ===== 🛒");
        
        if (orders.isEmpty()) {
            System.out.println("No orders found in the system.");
            return;
        }
        
        for (Order order : orders) {
            order.displayInfo();
            System.out.println("----------------------------------------------------------------");
        }
    }

    /**
     * Login as doctor and display doctor menu
     */
    private void loginAsDoctor() {
        System.out.println("\n👨‍⚕️ ===== DOCTOR LOGIN ===== 👨‍⚕️");
        String username = ConsoleUI.readStringInput("Enter username: ");
        String password = ConsoleUI.readStringInput("Enter password: ");
        
        Doctor doctor = authenticateDoctor(username, password);
        
        if (doctor == null) {
            System.out.println("Invalid username or password.");
            return;
        }
        
        System.out.println("Welcome, " + doctor.getName() + "!");
        
        // Use the doctor service to handle doctor menu
        boolean logout = doctorService.showDoctorMenu(doctor);
        
        if (logout) {
            saveDataToFiles();
        }
    }
    
    /**
     * Login as pharmacist and display pharmacist menu
     */
    private void loginAsPharmacist() {
        System.out.println("\n👩‍⚕️ ===== PHARMACIST LOGIN ===== 👩‍⚕️");
        String username = ConsoleUI.readStringInput("Enter username: ");
        String password = ConsoleUI.readStringInput("Enter password: ");
        
        Pharmacist pharmacist = authenticatePharmacist(username, password);
        
        if (pharmacist == null) {
            System.out.println("Invalid username or password.");
            return;
        }
        
        System.out.println("Welcome, " + pharmacist.getName() + "!");
        
        // Use the pharmacist service to handle pharmacist menu
        boolean logout = pharmacistService.showPharmacistMenu(pharmacist);
        
        if (logout) {
            saveDataToFiles();
        }
    }
    
    /**
     * Login as patient and display patient menu
     */
    private void loginAsPatient() {
        String username = ConsoleUI.readStringInput("Enter username: ");
        String password = ConsoleUI.readStringInput("Enter password: ");
        
        Patient patient = authenticatePatient(username, password);
        
        if (patient == null) {
            System.out.println("Invalid username or password.");
            return;
        }
        
        System.out.println("Welcome, " + patient.getName() + "!");
        boolean logout = false;
        
        while (!logout) {
            System.out.println("\n🧑‍⚕️ ===== PATIENT MENU ===== 🧑‍⚕️");
            System.out.println("1. 💊 View Available Medicines");
            System.out.println("2. 🛒 Place an Order");
            System.out.println("3. 📋 View My Orders");
            System.out.println("4. ❌ Cancel an Order");
            System.out.println("5. 💰 Manage Wallet");
            System.out.println("6. 💬 Doctor Consultations");
            System.out.println("7. 📝 Update My Account");
            System.out.println("8. 🚪 Logout");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    viewAvailableMedicines();
                    break;
                case 2:
                    placeOrder(patient);
                    break;
                case 3:
                    patientService.viewOrders(patient.getId());
                    break;
                case 4:
                    cancelOrder(patient);
                    break;
                case 5:
                    manageWallet(patient);
                    break;
                case 6:
                    manageConsultations(patient);
                    break;
                case 7:
                    updatePatientAccount(patient);
                    break;
                case 8:
                    logout = true;
                    saveDataToFiles();
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Manage consultations for a patient
     * 
     * @param patient The patient managing consultations
     */
    private void manageConsultations(Patient patient) {
        System.out.println("\n💬 ===== DOCTOR CONSULTATIONS ===== 💬");
        
        while (true) {
            System.out.println("\n1. View My Consultations");
            System.out.println("2. Request New Consultation");
            System.out.println("3. Send Message to Doctor");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    viewConsultations(patient);
                    break;
                case 2:
                    requestNewConsultation(patient);
                    break;
                case 3:
                    sendMessageToDoctor(patient);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * View all consultations for a patient
     * 
     * @param patient The patient viewing consultations
     */
    private void viewConsultations(Patient patient) {
        System.out.println("\n📋 ===== MY CONSULTATIONS ===== 📋");
        
        List<Consultation> consultations = patient.getConsultations();
        
        if (consultations.isEmpty()) {
            System.out.println("You don't have any consultations yet.");
            return;
        }
        
        for (int i = 0; i < consultations.size(); i++) {
            Consultation consultation = consultations.get(i);
            
            // Find the doctor name
            Doctor doctor = doctors.stream()
                .filter(d -> d.getId() == consultation.getDoctorId())
                .findFirst()
                .orElse(null);
            
            String doctorName = (doctor != null) ? doctor.getName() : "Unknown Doctor";
            
            System.out.println("\n" + (i + 1) + ". Consultation with Dr. " + doctorName);
            System.out.println("   Date: " + consultation.getDateTime());
            System.out.println("   Messages: " + consultation.getMessages().size());
        }
        
        System.out.print("\nSelect a consultation to view (0 to go back): ");
        int consultationIndex = getIntInput() - 1;
        
        if (consultationIndex < 0 || consultationIndex >= consultations.size()) {
            return;
        }
        
        viewConsultationDetails(consultations.get(consultationIndex));
    }
    
    /**
     * View details of a specific consultation
     * 
     * @param consultation The consultation to view
     */
    private void viewConsultationDetails(Consultation consultation) {
        // Find the doctor
        Doctor doctor = doctors.stream()
            .filter(d -> d.getId() == consultation.getDoctorId())
            .findFirst()
            .orElse(null);
        
        String doctorName = (doctor != null) ? doctor.getName() : "Unknown Doctor";
        
        // Find the patient
        Patient patient = patients.stream()
            .filter(p -> p.getId() == consultation.getPatientId())
            .findFirst()
            .orElse(null);
            
        String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
        
        System.out.println("\n💬 ===== CONSULTATION DETAILS ===== 💬");
        System.out.println("Doctor: Dr. " + doctorName);
        System.out.println("Patient: " + patientName);
        System.out.println("Date: " + consultation.getDateTime());
        System.out.println("Notes: " + consultation.getNotes());
        
        System.out.println("\nMessages:");
        if (consultation.getMessages().isEmpty()) {
            System.out.println("No messages yet.");
        } else {
            for (Message message : consultation.getMessages()) {
                String sender;
                if (message.getSenderId() == consultation.getDoctorId()) {
                    sender = "Dr. " + doctorName;
                } else if (message.getSenderId() == consultation.getPatientId()) {
                    sender = patientName;
                } else {
                    sender = "Unknown";
                }
                
                System.out.println("[" + message.getTimestamp() + "] " + sender + ": " + message.getContent());
            }
        }
        
        // Option to send a new message
        String choice = ConsoleUI.readStringInput("\nSend a message? (y/n): ").trim().toLowerCase();
        if (choice.equals("y") || choice.equals("yes")) {
            String messageContent = ConsoleUI.readStringInput("Enter your message: ").trim();
            
            if (!messageContent.isEmpty()) {
                int messageId = consultation.getMessages().size() + 1;
                Message message = new Message(messageId, consultation.getPatientId(), consultation.getDoctorId(), messageContent);
                consultation.addMessage(message);
                System.out.println("Message sent successfully.");
            }
        }
    }
    
    /**
     * Request a new consultation with a doctor
     * 
     * @param patient The patient requesting the consultation
     */
    private void requestNewConsultation(Patient patient) {
        System.out.println("\n➕ ===== REQUEST NEW CONSULTATION ===== ➕");
        
        // Show available doctors
        System.out.println("\nSelect a doctor:");
        for (int i = 0; i < doctors.size(); i++) {
            System.out.println((i + 1) + ". Dr. " + doctors.get(i).getName() + " (" + doctors.get(i).getSpecialization() + ")");
        }
        
        System.out.print("Enter doctor number: ");
        int doctorIndex = getIntInput() - 1;
        
        if (doctorIndex < 0 || doctorIndex >= doctors.size()) {
            System.out.println("Invalid doctor selection.");
            return;
        }
        
        Doctor doctor = doctors.get(doctorIndex);
        
        // Create a new consultation
        System.out.print("Enter reason for consultation: ");
        String notes = scanner.nextLine();
        
        int consultationId = 1;
        // Get the highest consultation ID currently in use
        for (Consultation consultation : patient.getConsultations()) {
            if (consultation.getId() >= consultationId) {
                consultationId = consultation.getId() + 1;
            }
        }
        
        Consultation consultation = new Consultation(consultationId, doctor.getId(), patient.getId(), notes);
        
        // Add consultation to doctor and patient
        doctor.addConsultation(consultation);
        patient.addConsultation(consultation);
        
        System.out.println("Consultation request sent successfully to Dr. " + doctor.getName() + ".");
        
        // Option to send an initial message
        String choice = ConsoleUI.readStringInput("Send an initial message? (y/n): ").trim().toLowerCase();
        if (choice.equals("y") || choice.equals("yes")) {
            String messageContent = ConsoleUI.readStringInput("Enter your message: ").trim();
            
            if (!messageContent.isEmpty()) {
                Message message = new Message(1, patient.getId(), doctor.getId(), messageContent);
                consultation.addMessage(message);
                System.out.println("Message sent successfully.");
            }
        }
    }
    
    /**
     * Send a message to a doctor in an existing consultation
     * 
     * @param patient The patient sending the message
     */
    private void sendMessageToDoctor(Patient patient) {
        System.out.println("\n✉️ ===== SEND MESSAGE ===== ✉️");
        
        List<Consultation> consultations = patient.getConsultations();
        
        if (consultations.isEmpty()) {
            System.out.println("You don't have any consultations yet. Request a new consultation first.");
            return;
        }
        
        // List all consultations
        for (int i = 0; i < consultations.size(); i++) {
            Consultation consultation = consultations.get(i);
            
            // Find the doctor name
            Doctor doctor = doctors.stream()
                .filter(d -> d.getId() == consultation.getDoctorId())
                .findFirst()
                .orElse(null);
            
            String doctorName = (doctor != null) ? doctor.getName() : "Unknown Doctor";
            
            System.out.println((i + 1) + ". Consultation with Dr. " + doctorName + " (" + consultation.getDateTime() + ")");
        }
        
        System.out.print("Select a consultation: ");
        int consultationIndex = getIntInput() - 1;
        
        if (consultationIndex < 0 || consultationIndex >= consultations.size()) {
            System.out.println("Invalid consultation selection.");
            return;
        }
        
        Consultation selectedConsultation = consultations.get(consultationIndex);
        
        // Get the doctor
        Doctor doctor = doctors.stream()
            .filter(d -> d.getId() == selectedConsultation.getDoctorId())
            .findFirst()
            .orElse(null);
        
        if (doctor == null) {
            System.out.println("Error: Doctor not found.");
            return;
        }
        
        System.out.println("Sending message to Dr. " + doctor.getName());
        String messageContent = ConsoleUI.readStringInput("Enter your message: ").trim();
        
        if (messageContent.isEmpty()) {
            System.out.println("Message cannot be empty.");
            return;
        }
        
        // Create and add the message
        int messageId = selectedConsultation.getMessages().size() + 1;
        Message message = new Message(messageId, patient.getId(), doctor.getId(), messageContent);
        selectedConsultation.addMessage(message);
        
        System.out.println("Message sent successfully to Dr. " + doctor.getName() + ".");
    }
    
    /**
     * Wallet management menu for patients
     * 
     * @param patient The patient whose wallet to manage
     */
    private void manageWallet(Patient patient) {
        boolean back = false;
        Wallet wallet = patient.getWallet();
        
        while (!back) {
            System.out.println("\n💰 ===== WALLET MANAGEMENT ===== 💰");
            System.out.println("Current Balance: " + String.format("%.2f LE", wallet.getBalance()));
            System.out.println("\n1. 💵 View Wallet Information");
            System.out.println("2. 📊 View Transaction History");
            System.out.println("3. 💸 Deposit Funds");
            System.out.println("4. 🔙 Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    wallet.displayInfo();
                    break;
                case 2:
                    wallet.displayTransactions();
                    break;
                case 3:
                    depositFunds(patient);
                    break;
                case 4:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Deposit funds into a patient's wallet
     * 
     * @param patient The patient whose wallet to deposit to
     */
    private void depositFunds(Patient patient) {
        System.out.println("\n💸 ===== DEPOSIT FUNDS ===== 💸");
        System.out.println("Current Balance: " + String.format("%.2f LE", patient.getWallet().getBalance()));
        
        System.out.print("Enter amount to deposit (LE): ");
        double amount = getDoubleInput();
        
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }
        
        String description = ConsoleUI.readStringInput("Enter deposit description (optional): ");
        if (description.isEmpty()) {
            description = "Manual deposit";
        }
        
        boolean deposited = patient.getWallet().deposit(amount, description);
        
        if (deposited) {
            System.out.println("\n✅ Deposit successful!");
            System.out.println("New Balance: " + String.format("%.2f LE", patient.getWallet().getBalance()));
            saveDataToFiles(); // Save after deposit
        } else {
            System.out.println("\n❌ Deposit failed. Please try again.");
        }
    }
    
    /**
     * Get double input from the user with validation
     * 
     * @return Valid double input
     */
    private double getDoubleInput() {
        return ConsoleUI.readDoubleInput("", 0, Double.MAX_VALUE);
    }

    /**
     * Authenticate doctor credentials
     * 
     * @param username Doctor username
     * @param password Doctor password
     * @return Doctor object if authentication successful, null otherwise
     */
    public Doctor authenticateDoctor(String username, String password) {
        return authService.authenticateDoctor(username, password);
    }
    
    /**
     * Authenticate pharmacist credentials
     * 
     * @param username Pharmacist username
     * @param password Pharmacist password
     * @return Pharmacist object if authentication successful, null otherwise
     */
    public Pharmacist authenticatePharmacist(String username, String password) {
        return authService.authenticatePharmacist(username, password);
    }
    
    /**
     * Authenticate patient credentials
     * 
     * @param username Patient username
     * @param password Patient password
     * @return Patient object if authentication successful, null otherwise
     */
    public Patient authenticatePatient(String username, String password) {
        return authService.authenticatePatient(username, password);
    }

    /**
     * View available medicines
     */
    private void viewAvailableMedicines() {
        System.out.println("\n💊 ===== AVAILABLE MEDICINES ===== 💊");
        
        if (medicines.isEmpty()) {
            System.out.println("No medicines found in the pharmacy.");
            return;
        }
        
        System.out.printf("%-5s %-20s %-30s %-10s %-10s\n", "ID", "Name", "Description", "Price", "In Stock");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (Medicine medicine : medicines) {
            if (medicine.getQuantity() > 0) {
                System.out.printf("%-5d %-20s %-30s %-9.2f LE %-10d\n",
                    medicine.getId(),
                    medicine.getName(),
                    medicine.getDescription(),
                    medicine.getPrice(),
                    medicine.getQuantity());
            }
        }
    }

    /**
     * Place an order for a patient
     * 
     * @param patient The patient placing the order
     */
    private void placeOrder(Patient patient) {
        System.out.println("\n🛒 ===== PLACE ORDER ===== 🛒");
        
        // Display available medicines
        viewAvailableMedicines();
        
        Map<Integer, Integer> medicineQuantities = new HashMap<>();
        boolean addMoreMedicines = true;
        
        while (addMoreMedicines) {
            System.out.print("Enter Medicine ID to order (0 to finish): ");
            int medicineId = getIntInput();
            
            if (medicineId == 0) {
                addMoreMedicines = false;
                continue;
            }
            
            // Check if medicine exists
            Medicine medicine = medicines.stream()
                                        .filter(m -> m.getId() == medicineId)
                                        .findFirst()
                                        .orElse(null);
            
            if (medicine == null) {
                System.out.println("Medicine with ID " + medicineId + " not found.");
                continue;
            }
            
            if (medicine.getQuantity() <= 0) {
                System.out.println("Medicine " + medicine.getName() + " is out of stock.");
                continue;
            }
            
            System.out.print("Enter quantity (max " + medicine.getQuantity() + "): ");
            int quantity = getIntInput();
            
            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                continue;
            }
            
            if (quantity > medicine.getQuantity()) {
                System.out.println("Not enough stock available. Maximum available: " + medicine.getQuantity());
                continue;
            }
            
            // Add to order
            medicineQuantities.put(medicineId, quantity);
            System.out.println(quantity + " units of " + medicine.getName() + " added to your order.");
        }
        
        if (medicineQuantities.isEmpty()) {
            System.out.println("Order canceled. No medicines selected.");
            return;
        }
        
        // Place the order
        Order order = patientService.placeOrder(patient.getId(), medicineQuantities);
        
        if (order != null) {
            System.out.println("Order placed successfully!");
            order.displayInfo();
            
            // Ask user if they want to pay now
            String payNow = ConsoleUI.readStringInput("\nDo you want to pay for this order now? (y/n): ").trim().toLowerCase();
            
            if (payNow.equals("y") || payNow.equals("yes")) {
                processOrderPayment(patient, order);
            } else {
                System.out.println("You can pay for this order later from the 'View My Orders' menu.");
            }
            
            saveDataToFiles(); // Save data after placing order
        } else {
            System.out.println("Failed to place order.");
        }
    }
    
    /**
     * Process payment for an order
     * 
     * @param patient The patient who is paying
     * @param order The order to pay for
     * @return True if payment was successful, false otherwise
     */
    private boolean processOrderPayment(Patient patient, Order order) {
        if (order.isPaid()) {
            System.out.println("This order is already paid.");
            return true;
        }
        
        System.out.println("\n💲 ===== PROCESS PAYMENT ===== 💲");
        System.out.println("Order Total: " + String.format("%.2f LE", order.calculateTotal()));
        System.out.println("Current Wallet Balance: " + String.format("%.2f LE", patient.getWallet().getBalance()));
        
        System.out.println("\nSelect Payment Method:");
        System.out.println("1. 💰 Pay from Wallet");
        System.out.println("2. 💳 Pay with Credit Card");
        System.out.println("3. ❌ Cancel Payment");
        System.out.print("Enter your choice: ");
        
        int choice = getIntInput();
        boolean paymentSuccess = false;
        
        switch (choice) {
            case 1:
                paymentSuccess = order.processPaymentFromWallet(patient);
                break;
            case 2:
                String cardNumber = ConsoleUI.readStringInput("Enter Credit Card Number: ").trim();
                
                // Simple validation for card number
                if (cardNumber.length() < 12 || !cardNumber.matches("\\d+")) {
                    System.out.println("Invalid card number. Payment canceled.");
                    return false;
                }
                
                paymentSuccess = order.processPaymentWithCard(patient, cardNumber);
                break;
            case 3:
                System.out.println("Payment canceled.");
                return false;
            default:
                System.out.println("Invalid choice. Payment canceled.");
                return false;
        }
        
        if (paymentSuccess) {
            System.out.println("Payment processed successfully!");
            System.out.println("Order status updated to: " + order.getStatus());
            return true;
        } else {
            System.out.println("Payment failed. You can try again later.");
            return false;
        }
    }

    /**
     * Cancel an order for a patient
     * 
     * @param patient The patient canceling the order
     */
    private void cancelOrder(Patient patient) {
        System.out.println("\n❌ ===== CANCEL ORDER ===== ❌");
        
        // Display patient's orders
        patientService.viewOrders(patient.getId());
        
        System.out.print("Enter Order ID to cancel (0 to cancel): ");
        int orderId = getIntInput();
        
        if (orderId == 0) {
            System.out.println("Operation canceled.");
            return;
        }
        
        if (patientService.cancelOrder(patient.getId(), orderId)) {
            System.out.println("Order canceled successfully!");
            saveDataToFiles(); // Save data after canceling order
        } else {
            System.out.println("Failed to cancel order.");
        }
    }

    /**
     * Update patient account information
     * 
     * @param patient The patient to update
     */
    private void updatePatientAccount(Patient patient) {
        System.out.println("\n📝 ===== UPDATE ACCOUNT ===== 📝");
        
        System.out.println("Current account information:");
        patient.displayInfo();
        
        System.out.println("\nEnter new details (leave blank to keep current value):");
        
        String name = ConsoleUI.readStringInput("Name [" + patient.getName() + "]: ");
        name = name.isEmpty() ? patient.getName() : name;
        
        String email = ConsoleUI.readStringInput("Email [" + patient.getEmail() + "]: ");
        email = email.isEmpty() ? patient.getEmail() : email;
        
        String phoneNumber = ConsoleUI.readStringInput("Phone Number [" + patient.getPhoneNumber() + "]: ");
        phoneNumber = phoneNumber.isEmpty() ? patient.getPhoneNumber() : phoneNumber;
        
        String address = ConsoleUI.readStringInput("Address [" + patient.getAddress() + "]: ");
        address = address.isEmpty() ? patient.getAddress() : address;
        
        if (patientService.updateAccount(patient.getId(), name, email, phoneNumber, address)) {
            System.out.println("Account updated successfully!");
            saveDataToFiles(); // Save data after updating account
        } else {
            System.out.println("Failed to update account.");
        }
    }

    /**
     * Create a new patient account
     */
    private void createPatientAccount() {
        System.out.println("\n👤 ===== CREATE PATIENT ACCOUNT ===== 👤");
        
        // Generate a new patient ID
        int nextPatientId = patients.stream().mapToInt(Patient::getId).max().orElse(0) + 1;
        
        String name = ConsoleUI.readStringInput("Enter Name: ");
        String username = ConsoleUI.readStringInput("Enter Username: ");
        
        // Check if username already exists
        if (patients.stream().anyMatch(p -> p.getUsername().equals(username))) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }
        
        String password = ConsoleUI.readStringInput("Enter Password: ");
        String email = ConsoleUI.readStringInput("Enter Email: ");
        String phoneNumber = ConsoleUI.readStringInput("Enter Phone Number: ");
        String address = ConsoleUI.readStringInput("Enter Address: ");
        
        Patient newPatient = new Patient(nextPatientId, name, username, password, email, phoneNumber, address);
        
        if (patientService.createAccount(newPatient)) {
            System.out.println("Account created successfully!");
            System.out.println("Your Patient ID is: " + nextPatientId);
            saveDataToFiles(); // Save data after creating account
        } else {
            System.out.println("Failed to create account.");
        }
    }

    /**
     * Get the list of admins
     * 
     * @return List of admin objects
     */
    public List<Admin> getAdmins() {
        return admins;
    }

    /**
     * Get the list of doctors
     * 
     * @return List of doctor objects
     */
    public List<Doctor> getDoctors() {
        return doctors;
    }

    /**
     * Get the list of patients
     * 
     * @return List of patient objects
     */
    public List<Patient> getPatients() {
        return patients;
    }

    /**
     * Get the list of medicines
     * 
     * @return List of medicine objects
     */
    public List<Medicine> getMedicines() {
        return medicines;
    }

    /**
     * Get the list of orders
     * 
     * @return List of order objects
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * Get the authentication service
     * 
     * @return The authentication service
     */
    public AuthenticationService getAuthService() {
        return authService;
    }

    /**
     * Get the PatientService instance
     * 
     * @return PatientService instance
     */
    public PatientService getPatientService() {
        return patientService;
    }

    /**
     * Get the DoctorService instance
     * 
     * @return DoctorService instance
     */
    public DoctorService getDoctorService() {
        return doctorService;
    }

    /**
     * Get the PharmacistService instance
     * 
     * @return PharmacistService instance
     */
    public PharmacistService getPharmacistService() {
        return pharmacistService;
    }
    
    /**
     * Check if the file system is ready for operations
     * 
     * @return true if file system is ready, false otherwise
     */
    public boolean isFileSystemReady() {
        return true; // For now, always return true as the file system is initialized in the constructor
    }

    /**
     * Add a new admin to the system
     * 
     * @param admin The admin to add
     * @return true if admin was added successfully
     */
    public boolean addAdmin(Admin admin) {
        // Check if username already exists
        if (admins.stream().anyMatch(a -> a.getUsername().equals(admin.getUsername()))) {
            return false;
        }
        admins.add(admin);
        saveDataToFiles();
        return true;
    }
    
    /**
     * Add a new doctor to the system
     * 
     * @param doctor The doctor to add
     * @return true if doctor was added successfully
     */
    public boolean addDoctor(Doctor doctor) {
        // Check if username already exists
        if (doctors.stream().anyMatch(d -> d.getUsername().equals(doctor.getUsername()))) {
            return false;
        }
        doctors.add(doctor);
        saveDataToFiles();
        return true;
    }
    
    /**
     * Add a new patient to the system
     * 
     * @param patient The patient to add
     * @return true if patient was added successfully
     */
    public boolean addPatient(Patient patient) {
        // Check if username already exists
        if (patients.stream().anyMatch(p -> p.getUsername().equals(patient.getUsername()))) {
            return false;
        }
        patients.add(patient);
        saveDataToFiles();
        return true;
    }
    
    /**
     * Add a new pharmacist to the system
     * 
     * @param pharmacist The pharmacist to add
     * @return true if pharmacist was added successfully
     */
    public boolean addPharmacist(Pharmacist pharmacist) {
        if (pharmacist == null) {
            System.err.println("Cannot add null pharmacist");
            return false;
        }
        
        // Validate pharmacist data
        if (pharmacist.getName() == null || pharmacist.getName().trim().isEmpty() ||
            pharmacist.getUsername() == null || pharmacist.getUsername().trim().isEmpty() ||
            pharmacist.getPassword() == null || pharmacist.getPassword().trim().isEmpty() ||
            pharmacist.getLicenseNumber() == null || pharmacist.getLicenseNumber().trim().isEmpty() ||
            pharmacist.getQualification() == null || pharmacist.getQualification().trim().isEmpty()) {
            System.err.println("Invalid pharmacist data: missing required fields");
            return false;
        }
        
        // Check for duplicate username
        boolean usernameExists = pharmacists.stream()
            .anyMatch(p -> p.getUsername().equals(pharmacist.getUsername()));
        
        if (usernameExists) {
            System.err.println("Username already exists: " + pharmacist.getUsername());
            return false;
        }
        
        // Add the pharmacist
        pharmacists.add(pharmacist);
        
        // Save to file immediately
        try {
            FileHandler.savePharmacists(pharmacists);
            System.out.println("Successfully added pharmacist: " + pharmacist.getName());
            
            // Reinitialize authentication service with updated pharmacist list
            this.authService = new AuthenticationService(admins, patients, doctors, pharmacists);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error saving pharmacist data: " + e.getMessage());
            pharmacists.remove(pharmacist); // Rollback the addition
            return false;
        }
    }
    
    /**
     * Delete an admin from the system
     * 
     * @param adminId The ID of the admin to delete
     * @return true if admin was deleted successfully, false if not found
     */
    public boolean deleteAdmin(int adminId) {
        boolean removed = admins.removeIf(a -> a.getId() == adminId);
        if (removed) {
            saveDataToFiles();
        }
        return removed;
    }
    
    /**
     * Delete a doctor from the system
     * 
     * @param doctorId The ID of the doctor to delete
     * @return true if doctor was deleted successfully, false if not found
     */
    public boolean deleteDoctor(int doctorId) {
        boolean removed = doctors.removeIf(d -> d.getId() == doctorId);
        if (removed) {
            saveDataToFiles();
        }
        return removed;
    }
    
    /**
     * Delete a patient from the system
     * 
     * @param patientId The ID of the patient to delete
     * @return true if patient was deleted successfully, false if not found
     */
    public boolean deletePatient(int patientId) {
        boolean removed = patients.removeIf(p -> p.getId() == patientId);
        if (removed) {
            saveDataToFiles();
        }
        return removed;
    }
    
    /**
     * Delete a pharmacist from the system
     * 
     * @param pharmacistId The ID of the pharmacist to delete
     * @return true if pharmacist was deleted successfully, false if not found
     */
    public boolean deletePharmacist(int pharmacistId) {
        boolean removed = pharmacists.removeIf(p -> p.getId() == pharmacistId);
        if (removed) {
            saveDataToFiles();
        }
        return removed;
    }

    /**
     * Ensure all users have unique IDs across all user types
     * Reassigns IDs sequentially by user type: first admins, then doctors, then patients, then pharmacists
     */
    private void ensureUniqueUserIds() {
        System.out.println("Organizing user IDs to be sequential across all user types...");
        boolean hasChanges = false;
        
        // Start with ID = 1
        int nextId = 1;
        
        // First assign IDs to admins
        System.out.println("Assigning IDs to admins starting with " + nextId);
        for (Admin admin : admins) {
            if (admin.getId() != nextId) {
                System.out.println("Reassigning admin " + admin.getName() + 
                                 " from ID " + admin.getId() + " to " + nextId);
                admin.setId(nextId);
                hasChanges = true;
            }
            nextId++;
        }
        
        // Then assign IDs to doctors
        System.out.println("Assigning IDs to doctors starting with " + nextId);
        for (Doctor doctor : doctors) {
            if (doctor.getId() != nextId) {
                System.out.println("Reassigning doctor " + doctor.getName() + 
                                 " from ID " + doctor.getId() + " to " + nextId);
                doctor.setId(nextId);
                hasChanges = true;
            }
            nextId++;
        }
        
        // Then assign IDs to patients
        System.out.println("Assigning IDs to patients starting with " + nextId);
        for (Patient patient : patients) {
            if (patient.getId() != nextId) {
                System.out.println("Reassigning patient " + patient.getName() + 
                                 " from ID " + patient.getId() + " to " + nextId);
                patient.setId(nextId);
                hasChanges = true;
            }
            nextId++;
        }
        
        // Finally assign IDs to pharmacists
        System.out.println("Assigning IDs to pharmacists starting with " + nextId);
        for (Pharmacist pharmacist : pharmacists) {
            if (pharmacist.getId() != nextId) {
                System.out.println("Reassigning pharmacist " + pharmacist.getName() + 
                                 " from ID " + pharmacist.getId() + " to " + nextId);
                pharmacist.setId(nextId);
                hasChanges = true;
            }
            nextId++;
        }
        
        // Save changes if any were made
        if (hasChanges) {
            System.out.println("Saving updated user IDs...");
            saveDataToFiles();
            System.out.println("All user IDs are now sequential and unique across all user types.");
        } else {
            System.out.println("All user IDs are already sequential - no changes needed.");
        }
    }

    /**
     * Get all patients from the system
     * 
     * @return List of all patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    /**
     * Saves a consultation and links it to doctor and patient.
     * This automatically saves data to files after making changes.
     * 
     * @param consultation The consultation to save
     * @return true if successful, false if failed
     */
    public boolean saveDoctorConsultation(Consultation consultation) {
        if (consultation == null) {
            return false;
        }
        
        // Get the doctor
        Doctor doctor = null;
        for (Doctor doc : doctors) {
            if (doc.getId() == consultation.getDoctorId()) {
                doctor = doc;
                break;
            }
        }
        
        if (doctor == null) {
            System.err.println("Doctor not found for consultation: " + consultation.getId());
            return false;
        }
        
        // Get the patient
        Patient patient = null;
        for (Patient pat : patients) {
            if (pat.getId() == consultation.getPatientId()) {
                patient = pat;
                break;
            }
        }
        
        if (patient == null) {
            System.err.println("Patient not found for consultation: " + consultation.getId());
            return false;
        }
        
        // Add consultation to doctor
        doctor.addConsultation(consultation);
        
        // Add consultation to patient
        patient.addConsultation(consultation);
        
        // Add consultation to list if not already present
        boolean found = false;
        for (int i = 0; i < consultations.size(); i++) {
            if (consultations.get(i).getId() == consultation.getId()) {
                consultations.set(i, consultation); // Update existing
                found = true;
                break;
            }
        }
        
        if (!found) {
            consultations.add(consultation);
        }
        
        // Save all data to files
        saveDataToFiles();
        
        return true;
    }

    /**
     * Updates a consultation's status and saves changes.
     * 
     * @param consultationId ID of the consultation to update
     * @param newStatus New status for the consultation
     * @return true if successful, false if failed
     */
    public boolean updateConsultationStatus(int consultationId, String newStatus) {
        // Find consultation in list
        Consultation consultation = null;
        for (Consultation c : consultations) {
            if (c.getId() == consultationId) {
                consultation = c;
                break;
            }
        }
        
        if (consultation == null) {
            return false;
        }
        
        // Update status
        consultation.setStatus(newStatus);
        
        // Save changes
        saveDataToFiles();
        
        return true;
    }

    /**
     * Add a message to a consultation and save changes.
     * 
     * @param consultationId ID of the consultation
     * @param message Message to add
     * @return true if successful, false if failed
     */
    public boolean addMessageToConsultation(int consultationId, Message message) {
        // Find consultation
        Consultation consultation = null;
        for (Consultation c : consultations) {
            if (c.getId() == consultationId) {
                consultation = c;
                break;
            }
        }
        
        if (consultation == null || message == null) {
            return false;
        }
        
        // Add message
        consultation.addMessage(message);
        
        // Save changes
        saveDataToFiles();
        
        return true;
    }

    /**
     * Saves a prescription and links it to doctor and patient.
     * Adds the prescription to the central list and associates it with the doctor and patient.
     * 
     * @param prescription The prescription to save
     * @return true if successful, false if failed
     */
    public boolean savePrescription(Prescription prescription) {
        if (prescription == null) {
            return false;
        }
        
        // Find the doctor
        Doctor doctor = null;
        for (Doctor doc : doctors) {
            if (doc.getId() == prescription.getDoctorId()) {
                doctor = doc;
                break;
            }
        }
        
        if (doctor == null) {
            System.err.println("Doctor not found for prescription: " + prescription.getId());
            return false;
        }
        
        // Find the patient
        Patient patient = null;
        for (Patient pat : patients) {
            if (pat.getId() == prescription.getPatientId()) {
                patient = pat;
                break;
            }
        }
        
        if (patient == null) {
            System.err.println("Patient not found for prescription: " + prescription.getId());
            return false;
        }
        
        // Add prescription to doctor
        if (doctor.findPrescriptionById(prescription.getId()) == null) {
            doctor.addPrescription(prescription);
        }
        
        // Add prescription to patient
        if (patient.findPrescriptionById(prescription.getId()) == null) {
            patient.addPrescription(prescription);
        }
        
        // Add prescription to central list if not already present
        boolean found = false;
        for (int i = 0; i < prescriptions.size(); i++) {
            if (prescriptions.get(i).getId() == prescription.getId()) {
                prescriptions.set(i, prescription); // Update existing
                found = true;
                break;
            }
        }
        
        if (!found) {
            prescriptions.add(prescription);
        }
        
        // Save all data to files
        saveDataToFiles();
        
        return true;
    }

    /**
     * Generate a unique order ID.
     * @return next available order id
     */
    public synchronized int generateOrderId() {
        // Ensure nextOrderId is always greater than current max
        int maxId = orders.stream().mapToInt(Order::getId).max().orElse(0);
        if (nextOrderId <= maxId) {
            nextOrderId = maxId + 1;
        }
        return nextOrderId++;
    }

    /**
     * Find a medicine object by its ID.
     * @param medicineId the id to search
     * @return Medicine object or null if not found
     */
    public Medicine findMedicineById(int medicineId) {
        return medicines.stream()
                .filter(m -> m.getId() == medicineId)
                .findFirst()
                .orElse(null);
    }

}