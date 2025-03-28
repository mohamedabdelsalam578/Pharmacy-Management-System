package services;

import models.*;
import utils.FileHandler;
import utils.ConsoleUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;

/**
 * PharmacyService is the main service class that integrates admin and patient services
 */
public class PharmacyService {
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
     * Constructor to initialize PharmacyService
     */
    public PharmacyService() {
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
        
        // Orders need to be loaded after medicines since they reference medicines
        orders = FileHandler.loadOrders(medicines);
        
        // If no data in files, initialize with sample data
        if (admins.isEmpty() && patients.isEmpty() && medicines.isEmpty()) {
            System.out.println("No data found in files. Initializing with sample data.");
            initialize();
            saveDataToFiles();
        }
    }
    
    /**
     * Save all data to files and database
     */
    public void saveDataToFiles() {
        // Save data to files
        FileHandler.saveAdmins(admins);
        FileHandler.savePatients(patients);
        FileHandler.saveDoctors(doctors);
        FileHandler.saveMedicines(medicines);
        FileHandler.saveOrders(orders);
        
        // Save wallet data to database if available
        saveWalletsToDatabase();
        
        System.out.println("All data saved to persistent storage.");
    }
    
    /**
     * Save all wallet data to database
     * This only has an effect if database connection is available
     */
    private void saveWalletsToDatabase() {
        try {
            // Initialize WalletService for database operations
            WalletService walletService = new WalletService();
            
            // Only proceed if database is available
            if (!walletService.isDatabaseAvailable()) {
                return;
            }
            
            int savedCount = 0;
            
            // Save each patient's wallet to database
            for (Patient patient : patients) {
                Wallet wallet = patient.getWallet();
                if (wallet != null) {
                    boolean saved = walletService.saveWallet(wallet);
                    if (saved) {
                        // Also save any credit cards
                        walletService.saveCreditCards(wallet);
                        savedCount++;
                    }
                }
            }
            
            if (savedCount > 0) {
                System.out.println("Saved " + savedCount + " wallets to database.");
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not save wallet data to database: " + e.getMessage());
            // Log the exception but continue execution
            e.printStackTrace();
        }
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
        doctors.add(new Doctor(1, "Dr. Ahmed Mahmoud", "dr_ahmed", "password", "ahmed@elta3ban.com", "01212345678", 
                              "Cardiology", "EGP12345"));
        doctors.add(new Doctor(2, "Dr. Nour El-Din", "dr_nour", "password", "nour@elta3ban.com", "01512345678", 
                              "Neurology", "EGP67890"));
    }

    /**
     * Initialize sample patients with Egyptian names
     */
    private void initializePatients() {
        patients.add(new Patient(1, "Amr Khaled", "alice", "alice123", "amr@gmail.com", "01112345679", 
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
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                System.out.print("Enter your choice: ");
            }
        }
    }

    /**
     * Login as admin and display admin menu
     */
    private void loginAsAdmin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
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
    private Admin authenticateAdmin(String username, String password) {
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
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
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
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
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
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
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
        System.out.print("\nSend a message? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("y") || choice.equals("yes")) {
            System.out.print("Enter your message: ");
            String messageContent = scanner.nextLine().trim();
            
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
        System.out.print("Send an initial message? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("y") || choice.equals("yes")) {
            System.out.print("Enter your message: ");
            String messageContent = scanner.nextLine().trim();
            
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
        System.out.print("Enter your message: ");
        String messageContent = scanner.nextLine().trim();
        
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
        
        System.out.print("Enter deposit description (optional): ");
        String description = scanner.nextLine();
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
        double input = 0;
        boolean validInput = false;
        
        while (!validInput) {
            try {
                input = Double.parseDouble(scanner.nextLine());
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
        
        return input;
    }

    /**
     * Authenticate doctor credentials
     * 
     * @param username Doctor username
     * @param password Doctor password
     * @return Doctor object if authentication successful, null otherwise
     */
    private Doctor authenticateDoctor(String username, String password) {
        return authService.authenticateDoctor(username, password);
    }
    
    /**
     * Authenticate pharmacist credentials
     * 
     * @param username Pharmacist username
     * @param password Pharmacist password
     * @return Pharmacist object if authentication successful, null otherwise
     */
    private Pharmacist authenticatePharmacist(String username, String password) {
        return authService.authenticatePharmacist(username, password);
    }
    
    /**
     * Authenticate patient credentials
     * 
     * @param username Patient username
     * @param password Patient password
     * @return Patient object if authentication successful, null otherwise
     */
    private Patient authenticatePatient(String username, String password) {
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
            System.out.print("\nDo you want to pay for this order now? (y/n): ");
            String payNow = scanner.nextLine().trim().toLowerCase();
            
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
                System.out.print("Enter Credit Card Number: ");
                String cardNumber = scanner.nextLine().trim();
                
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
        
        System.out.print("Name [" + patient.getName() + "]: ");
        String name = scanner.nextLine();
        name = name.isEmpty() ? patient.getName() : name;
        
        System.out.print("Email [" + patient.getEmail() + "]: ");
        String email = scanner.nextLine();
        email = email.isEmpty() ? patient.getEmail() : email;
        
        System.out.print("Phone Number [" + patient.getPhoneNumber() + "]: ");
        String phoneNumber = scanner.nextLine();
        phoneNumber = phoneNumber.isEmpty() ? patient.getPhoneNumber() : phoneNumber;
        
        System.out.print("Address [" + patient.getAddress() + "]: ");
        String address = scanner.nextLine();
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
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        
        // Check if username already exists
        if (patients.stream().anyMatch(p -> p.getUsername().equals(username))) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine();
        
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        
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
}
