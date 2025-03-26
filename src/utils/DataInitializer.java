package utils;

import models.*;
import services.PharmacyService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * DataInitializer provides methods to initialize the pharmacy with test data
 * and to test various functionalities
 */
public class DataInitializer {
    
    /**
     * Initialize pharmacy with test data and run test scenarios
     * 
     * @param pharmacyService The pharmacy service to initialize
     */
    public static void initializeAndTest(PharmacyService pharmacyService) {
        // Initialize base data
        pharmacyService.initialize();
        
        System.out.println("===== PHARMACY MANAGEMENT SYSTEM INITIALIZATION =====");
        System.out.println("Pharmacy initialized with test data.");
        
        System.out.println("\n===== RUNNING TEST SCENARIOS =====");
        
        // Test Admin functionalities
        testAdminFunctionalities(pharmacyService);
        
        // Test Patient functionalities
        testPatientFunctionalities(pharmacyService);
        
        System.out.println("\n===== TEST SCENARIOS COMPLETED =====");
        System.out.println("The system is now ready for interactive use.");
    }
    
    /**
     * Test admin functionalities
     * 
     * @param pharmacyService The pharmacy service to test
     */
    private static void testAdminFunctionalities(PharmacyService pharmacyService) {
        System.out.println("\n----- Testing Admin Functionalities -----");
        
        // Get the admin service through a test admin
        Admin testAdmin = pharmacyService.getAdmins().get(0);
        System.out.println("Test Admin: " + testAdmin.getName() + " (Egyptian Pharmacy Manager)");
        
        // Test adding a new medicine
        Medicine newMedicine = new Medicine(
            6, "Histazine", "Allergy medication", "Amoun Pharmaceuticals", 
            28.50, 60, "Allergy", false
        );
        
        pharmacyService.getMedicines().add(newMedicine);
        System.out.println("Added new medicine: " + newMedicine.getName());
        
        // Test updating a medicine
        Medicine medicineToUpdate = pharmacyService.getMedicines().get(0);
        medicineToUpdate.setPrice(22.75);
        medicineToUpdate.setQuantity(120);
        System.out.println("Updated medicine: " + medicineToUpdate.getName() + 
                           " - New price: " + medicineToUpdate.getPrice() + " LE" + 
                           ", New quantity: " + medicineToUpdate.getQuantity());
        
        // Test removing a medicine
        Medicine medicineToRemove = pharmacyService.getMedicines().get(pharmacyService.getMedicines().size() - 1);
        pharmacyService.getMedicines().remove(medicineToRemove);
        System.out.println("Removed medicine: " + medicineToRemove.getName());
        
        System.out.println("Admin functionalities tested successfully.");
    }
    
    /**
     * Test patient functionalities
     * 
     * @param pharmacyService The pharmacy service to test
     */
    private static void testPatientFunctionalities(PharmacyService pharmacyService) {
        // Test patient-specific functionalities like ordering medicines and managing account
        System.out.println("\n----- Testing Patient Functionalities -----");
        
        // Get the patient service through a test patient
        Patient testPatient = pharmacyService.getPatients().get(0);
        System.out.println("Test Patient: " + testPatient.getName());
        
        // Initialize wallet for testing if needed
        if (testPatient.getWallet().getBalance() == 0) {
            testPatient.getWallet().deposit(500.0, "Initial deposit for testing");
            System.out.println("Added 500.0 LE to " + testPatient.getName() + "'s wallet for testing");
        }
        
        // Test placing an order
        Order newOrder = new Order(1, testPatient.getId());
        
        // Add medicines to order
        Medicine medicine1 = pharmacyService.getMedicines().get(0);
        Medicine medicine2 = pharmacyService.getMedicines().get(1);
        
        newOrder.addMedicine(medicine1, 2);
        newOrder.addMedicine(medicine2, 1);
        
        // Update stock
        medicine1.updateStock(2);
        medicine2.updateStock(1);
        
        // Add order to patient and order list
        testPatient.addOrder(newOrder);
        pharmacyService.getOrders().add(newOrder);
        
        System.out.println("Created new order for " + testPatient.getName() + 
                           " with " + newOrder.getMedicines().size() + " medicines.");
        
        // Test wallet payment for order
        boolean paymentResult = newOrder.processPaymentFromWallet(testPatient);
        if (paymentResult) {
            System.out.println("Successfully paid for order #" + newOrder.getId() + " using wallet");
            System.out.println("Remaining wallet balance: " + String.format("%.2f", testPatient.getWallet().getBalance()) + " LE");
        } else {
            System.out.println("Failed to pay for order using wallet");
        }
        
        // Test creating a new order for credit card payment
        Order creditCardOrder = new Order(2, testPatient.getId());
        
        // Add medicines to order
        creditCardOrder.addMedicine(medicine1, 1);
        
        // Update stock
        medicine1.updateStock(1);
        
        // Add order to patient and order list
        testPatient.addOrder(creditCardOrder);
        pharmacyService.getOrders().add(creditCardOrder);
        
        // Test credit card payment
        boolean ccPaymentResult = creditCardOrder.processPaymentWithCard(testPatient, "1234567890123456");
        if (ccPaymentResult) {
            System.out.println("Successfully paid for order #" + creditCardOrder.getId() + " using credit card");
        } else {
            System.out.println("Failed to pay for order using credit card");
        }
        
        // Display wallet transaction history
        System.out.println("\nWallet transaction history for " + testPatient.getName() + ":");
        testPatient.getWallet().displayTransactions();
        
        // Test creating a new patient account
        Patient newPatient = new Patient(
            3, "Khaled Mahmoud", "khaled_patient", "password", 
            "khaled@gmail.com", "01011223344", "14 El Nasr St, Alexandria"
        );
        
        // Add initial funds to new patient's wallet
        newPatient.getWallet().deposit(200.0, "Initial deposit");
        System.out.println("Created new patient account: " + newPatient.getName() + 
                           " with wallet balance: " + String.format("%.2f", newPatient.getWallet().getBalance()) + " LE");
        
        pharmacyService.getPatients().add(newPatient);
        
        // Test updating patient information
        testPatient.setEmail("amr_updated@gmail.com");
        testPatient.setPhoneNumber("01112233445");
        System.out.println("Updated patient information for: " + testPatient.getName() + 
                           " - New email: " + testPatient.getEmail());
        
        System.out.println("Patient functionalities tested successfully.");
    }
    
    /**
     * Initialize and test the extended pharmacy management system with healthcare functionality
     * 
     * @param pharmacyService The updated pharmacy service to initialize
     */
    public static void initializeExtendedSystem(PharmacyService pharmacyService) {
        System.out.println("===== EXTENDED PHARMACY MANAGEMENT SYSTEM INITIALIZATION =====");
        System.out.println("Initializing with healthcare workflow functionality...");
        
        // Initialize test data for the extended system
        initializeExtendedTestData(pharmacyService);
        
        System.out.println("Extended pharmacy system initialized with test data.");
        
        System.out.println("\n===== RUNNING EXTENDED TEST SCENARIOS =====");
        
        // Test Doctor functionalities
        testDoctorFunctionalities(pharmacyService);
        
        // Test Pharmacist functionalities
        testPharmacistFunctionalities(pharmacyService);
        
        // Test Patient functionalities with prescriptions
        testPatientWithPrescriptions(pharmacyService);
        
        System.out.println("\n===== EXTENDED TEST SCENARIOS COMPLETED =====");
        System.out.println("The extended system is now ready for interactive use.");
    }
    
    /**
     * Initialize test data for the extended pharmacy system
     * 
     * @param pharmacyService The updated pharmacy service to initialize
     */
    private static void initializeExtendedTestData(PharmacyService pharmacyService) {
        // This method would be implemented to create test data for doctors, pharmacists,
        // pharmacies, prescriptions, etc. For now, we'll just create placeholder data.
        
        // Create test admin
        Admin admin = new Admin(1, "Ahmed Nader", "admin", "admin123", "ahmed@pharmacy.com", 
                             "01012345678", "Head Pharmacist", "Management");
        pharmacyService.getAdmins().add(admin);
        
        // Create test medicines with prescription requirements
        Medicine medicine1 = new Medicine(1, "Aspirin", "Pain reliever", "Al-Kahira Pharm", 
                                   20.50, 100, "Pain Relief", false);
        Medicine medicine2 = new Medicine(2, "Paracetamol", "Fever reducer", "Pharco", 
                                   15.75, 150, "Fever Relief", false);
        Medicine medicine3 = new Medicine(3, "Amoxicillin", "Antibiotic", "EIPICO", 
                                   45.00, 80, "Antibiotics", true);
        Medicine medicine4 = new Medicine(4, "Lisinopril", "Blood pressure medication", "Pfizer Egypt", 
                                   35.25, 60, "Blood Pressure", true);
        Medicine medicine5 = new Medicine(5, "Omeprazole", "Acid reflux medication", "GlaxoSmithKline", 
                                   25.00, 70, "Stomach", false);
        
        pharmacyService.getMedicines().add(medicine1);
        pharmacyService.getMedicines().add(medicine2);
        pharmacyService.getMedicines().add(medicine3);
        pharmacyService.getMedicines().add(medicine4);
        pharmacyService.getMedicines().add(medicine5);
        
        // Create test patient
        Patient patient = new Patient(1, "Amr Hassan", "amr_patient", "amr123", 
                                 "amr@gmail.com", "01123456789", "123 El Geish St, Cairo");
        // Initialize wallet with funds for testing
        patient.getWallet().deposit(500.0, "Initial test deposit");
        System.out.println("Added 500.0 LE to " + patient.getName() + "'s wallet for testing");
        pharmacyService.getPatients().add(patient);
        
        // Create test doctor
        Doctor doctor = new Doctor(1, "Dr. Mohamed Saleh", "dr_mohamed", "dr123", 
                           "dr.mohamed@hospital.com", "01234567890", "Medical License 12345", "General Medicine");
        pharmacyService.getDoctors().add(doctor);
        
        // Create test pharmacy
        Pharmacy pharmacy = new Pharmacy(1, "Elt3ban Pharmacy", "45 El Tahrir St, Cairo", 
                                 "02-16999", "info@elt3banpharmacy.com");
        pharmacyService.getPharmacies().add(pharmacy);
        
        // Create test pharmacist
        Pharmacist pharmacist = new Pharmacist(1, "Fatima Ahmed", "fatima_pharm", "fatima123", 
                                     "fatima@pharmacy.com", "01056789012", "Pharm License 54321", 
                                     "Clinical Pharmacist", pharmacy.getId());
        pharmacyService.getPharmacists().add(pharmacist);
        pharmacy.addPharmacist(pharmacist);
        
        // Add medicines to pharmacy inventory
        for (Medicine medicine : pharmacyService.getMedicines()) {
            // Create copies of the medicines for the pharmacy
            Medicine pharmacyMedicine = new Medicine(
                medicine.getId(), 
                medicine.getName(), 
                medicine.getDescription(), 
                medicine.getManufacturer(), 
                medicine.getPrice(), 
                medicine.getQuantity(),
                medicine.getCategory(),
                medicine.isRequiresPrescription()
            );
            pharmacy.addMedicine(pharmacyMedicine);
        }
        
        System.out.println("Extended test data initialized successfully.");
    }
    
    /**
     * Test doctor functionalities
     * 
     * @param pharmacyService The updated pharmacy service to test
     */
    private static void testDoctorFunctionalities(PharmacyService pharmacyService) {
        System.out.println("\n----- Testing Doctor Functionalities -----");
        
        // Get test doctor and patient
        Doctor doctor = pharmacyService.getDoctors().get(0);
        Patient patient = pharmacyService.getPatients().get(0);
        
        System.out.println("Test Doctor: " + doctor.getName());
        System.out.println("Creating prescription for patient: " + patient.getName());
        
        // Create a prescription
        Prescription prescription = new Prescription(
            1, 
            patient.getId(), 
            doctor.getId(), 
            LocalDate.now(), 
            LocalDate.now().plusDays(30), 
            "Take as directed after meals"
        );
        
        // Add medicines to prescription
        Medicine medicine1 = pharmacyService.getMedicines().get(2); // Amoxicillin (requires prescription)
        Medicine medicine2 = pharmacyService.getMedicines().get(3); // Lisinopril (requires prescription)
        
        prescription.addMedicine(medicine1, 1);
        prescription.addMedicine(medicine2, 1);
        
        // Add prescription to doctor's issued prescriptions
        doctor.getIssuedPrescriptions().add(prescription);
        
        // Add prescription to patient's prescriptions
        patient.addPrescription(prescription);
        
        // Add prescription to system
        pharmacyService.getPrescriptions().add(prescription);
        
        System.out.println("Created prescription with ID: " + prescription.getId() + 
                         " and " + prescription.getMedicines().size() + " medicines");
        
        // Send prescription to pharmacy
        Pharmacy pharmacy = pharmacyService.getPharmacies().get(0);
        prescription.setStatus("Sent to Pharmacy");
        pharmacy.addPrescription(prescription);
        
        System.out.println("Sent prescription to pharmacy: " + pharmacy.getName());
        
        // Create a test consultation between doctor and patient
        Consultation consultation = new Consultation(
            1, 
            doctor.getId(), 
            patient.getId(), 
            "Initial consultation for headache and fever"
        );
        
        // Add the consultation to both doctor and patient
        doctor.addConsultation(consultation);
        patient.addConsultation(consultation);
        
        // Add a few test messages to the consultation
        Message doctorMessage1 = new Message(
            1, 
            doctor.getId(), 
            patient.getId(), 
            "Hello Amr, what symptoms are you experiencing?"
        );
        
        Message patientMessage1 = new Message(
            2, 
            patient.getId(), 
            doctor.getId(), 
            "Hi Doctor, I've been having headaches and fever for the past two days."
        );
        
        Message doctorMessage2 = new Message(
            3, 
            doctor.getId(), 
            patient.getId(), 
            "I'll prescribe some medication for you. Please take it as directed and let me know if the symptoms persist."
        );
        
        // Add messages to the consultation
        consultation.addMessage(doctorMessage1);
        consultation.addMessage(patientMessage1);
        consultation.addMessage(doctorMessage2);
        
        // Add consultation to pharmacy service
        pharmacyService.getConsultations().add(consultation);
        
        System.out.println("Created test consultation between Dr. " + doctor.getName() + " and " + patient.getName());
        System.out.println("Doctor functionalities tested successfully.");
    }
    
    /**
     * Test pharmacist functionalities
     * 
     * @param pharmacyService The updated pharmacy service to test
     */
    private static void testPharmacistFunctionalities(PharmacyService pharmacyService) {
        System.out.println("\n----- Testing Pharmacist Functionalities -----");
        
        // Get test pharmacist and pharmacy
        Pharmacist pharmacist = pharmacyService.getPharmacists().get(0);
        Pharmacy pharmacy = pharmacyService.getPharmacies().get(0);
        
        System.out.println("Test Pharmacist: " + pharmacist.getName());
        System.out.println("Working at pharmacy: " + pharmacy.getName());
        
        // Get prescription from pharmacy
        if (!pharmacy.getPrescriptions().isEmpty()) {
            Prescription prescription = pharmacy.getPrescriptions().get(0);
            
            System.out.println("Processing prescription with ID: " + prescription.getId());
            
            // Fill prescription
            prescription.setStatus("Filled");
            pharmacist.getFilledPrescriptions().add(prescription);
            
            System.out.println("Filled prescription successfully.");
            
            // Update medicine inventory in pharmacy
            for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
                Medicine medicine = entry.getKey();
                int quantity = entry.getValue();
                
                // Find the medicine in the pharmacy inventory
                Medicine pharmacyMedicine = pharmacy.findMedicineById(medicine.getId());
                if (pharmacyMedicine != null) {
                    // Update quantity
                    pharmacyMedicine.setQuantity(pharmacyMedicine.getQuantity() - quantity);
                    System.out.println("Updated inventory for medicine: " + pharmacyMedicine.getName() + 
                                     " - New quantity: " + pharmacyMedicine.getQuantity());
                }
            }
        } else {
            System.out.println("No prescriptions to process in pharmacy.");
        }
        
        // Test adding new medicine to pharmacy inventory
        Medicine newMedicine = new Medicine(
            6, "Insulin", "Diabetes medication", "Alexandria Co.", 
            120.00, 30, "Diabetes", true
        );
        
        pharmacy.addMedicine(newMedicine);
        System.out.println("Added new medicine to pharmacy inventory: " + newMedicine.getName());
        
        System.out.println("Pharmacist functionalities tested successfully.");
    }
    
    /**
     * Test patient functionalities with prescriptions
     * 
     * @param pharmacyService The updated pharmacy service to test
     */
    private static void testPatientWithPrescriptions(PharmacyService pharmacyService) {
        System.out.println("\n----- Testing Patient Prescription Functionalities -----");
        
        // Get test patient
        Patient patient = pharmacyService.getPatients().get(0);
        
        System.out.println("Test Patient: " + patient.getName());
        
        // Check if patient has prescriptions
        if (!patient.getPrescriptions().isEmpty()) {
            Prescription prescription = patient.getPrescriptions().get(0);
            
            System.out.println("Patient has prescription with ID: " + prescription.getId());
            System.out.println("Prescription status: " + prescription.getStatus());
            
            // If prescription is filled, create an order with it
            if (prescription.getStatus().equals("Filled")) {
                // Create an order with the filled prescription
                int nextOrderId = pharmacyService.getOrders().stream()
                                                .mapToInt(Order::getId)
                                                .max()
                                                .orElse(0) + 1;
                
                Order order = patient.placeOrderWithPrescription(prescription, nextOrderId);
                
                if (order != null) {
                    pharmacyService.getOrders().add(order);
                    
                    // Process payment using wallet
                    boolean paymentSuccess = order.processPaymentFromWallet(patient);
                    if (paymentSuccess) {
                        System.out.println("Paid for prescription order #" + order.getId() + " using wallet");
                        System.out.println("Remaining wallet balance: " + String.format("%.2f", patient.getWallet().getBalance()) + " LE");
                    } else {
                        System.out.println("Failed to pay for prescription order using wallet - insufficient funds");
                    }
                    System.out.println("Created order with prescription - Order ID: " + order.getId());
                }
            } else {
                System.out.println("Prescription is not yet filled and cannot be used for ordering.");
            }
        } else {
            System.out.println("Patient has no prescriptions.");
        }
        
        // Test updating patient account information
        patient.setPhoneNumber("01087654321");
        patient.setAddress("45 Salah Salem St, Cairo");
        
        System.out.println("Updated patient information - New phone: " + patient.getPhoneNumber() + 
                         ", New address: " + patient.getAddress());
        
        System.out.println("Patient prescription functionalities tested successfully.");
    }
}
