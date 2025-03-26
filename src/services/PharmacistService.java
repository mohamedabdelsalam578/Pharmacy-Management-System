package services;

import models.Medicine;
import models.Pharmacist;
import models.Pharmacy;
import models.Prescription;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * PharmacistService class handles pharmacist-specific operations
 */
public class PharmacistService {
    private List<Pharmacist> pharmacists;
    private List<Pharmacy> pharmacies;
    private List<Prescription> prescriptions;
    private List<Medicine> medicines;
    
    private Scanner scanner;
    
    /**
     * Constructor to initialize PharmacistService
     * 
     * @param pharmacists List of pharmacists
     * @param pharmacies List of pharmacies
     * @param prescriptions List of prescriptions
     * @param medicines List of medicines
     */
    public PharmacistService(List<Pharmacist> pharmacists, List<Pharmacy> pharmacies, 
                          List<Prescription> prescriptions, List<Medicine> medicines) {
        this.pharmacists = pharmacists;
        this.pharmacies = pharmacies;
        this.prescriptions = prescriptions;
        this.medicines = medicines;
        
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Show pharmacist menu
     * 
     * @param pharmacist The logged-in pharmacist
     * @return True if the pharmacist logs out
     */
    public boolean showPharmacistMenu(Pharmacist pharmacist) {
        boolean logout = false;
        
        // Find the pharmacy where this pharmacist works
        Pharmacy pharmacy = findPharmacyById(pharmacist.getPharmacyId());
        
        if (pharmacy == null) {
            System.out.println("Error: Pharmacy not found for this pharmacist.");
            return true; // Force logout
        }
        
        while (!logout) {
            System.out.println("\n👩‍⚕️ ===== PHARMACIST MENU ===== 👩‍⚕️");
            System.out.println("Pharmacy: " + pharmacy.getName() + " (" + pharmacy.getAddress() + ")");
            System.out.println("1. 📝 View Pending Prescriptions");
            System.out.println("2. 📋 Fill Prescription");
            System.out.println("3. 💊 Manage Pharmacy Inventory");
            System.out.println("4. 👤 Update My Profile");
            System.out.println("5. 🚪 Logout");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    viewPendingPrescriptions(pharmacy);
                    break;
                case 2:
                    fillPrescription(pharmacist, pharmacy);
                    break;
                case 3:
                    manageInventory(pharmacy);
                    break;
                case 4:
                    updateProfile(pharmacist);
                    break;
                case 5:
                    logout = true;
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        return logout;
    }
    
    /**
     * Find pharmacy by ID
     * 
     * @param pharmacyId ID of the pharmacy to find
     * @return Pharmacy object if found, null otherwise
     */
    private Pharmacy findPharmacyById(int pharmacyId) {
        return pharmacies.stream()
                       .filter(p -> p.getId() == pharmacyId)
                       .findFirst()
                       .orElse(null);
    }
    
    /**
     * View all pending prescriptions for a pharmacy
     * 
     * @param pharmacy The pharmacy to view prescriptions for
     */
    private void viewPendingPrescriptions(Pharmacy pharmacy) {
        System.out.println("\n📝 ===== PENDING PRESCRIPTIONS ===== 📝");
        
        List<Prescription> pendingPrescriptions = pharmacy.getPrescriptions();
        
        if (pendingPrescriptions.isEmpty()) {
            System.out.println("No pending prescriptions for this pharmacy.");
            return;
        }
        
        for (Prescription prescription : pendingPrescriptions) {
            if (prescription.getStatus().equals("Sent to Pharmacy")) {
                System.out.println("\nPrescription ID: " + prescription.getId());
                System.out.println("Patient ID: " + prescription.getPatientId());
                System.out.println("Doctor ID: " + prescription.getDoctorId());
                System.out.println("Issue Date: " + prescription.getIssueDate());
                System.out.println("Expiry Date: " + prescription.getExpiryDate());
                System.out.println("Status: " + prescription.getStatus());
                System.out.println("Instructions: " + prescription.getInstructions());
                
                System.out.println("Medicines:");
                double totalPrice = 0.0;
                for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
                    Medicine medicine = entry.getKey();
                    int quantity = entry.getValue();
                    double medicinePrice = medicine.getPrice() * quantity;
                    totalPrice += medicinePrice;
                    System.out.println("- " + medicine.getName() + " (" + quantity + "): " + medicinePrice + " LE");
                }
                System.out.println("Total Price: " + totalPrice + " LE");
            }
        }
    }
    
    /**
     * Fill a prescription
     * 
     * @param pharmacist The pharmacist filling the prescription
     * @param pharmacy The pharmacy where the prescription is being filled
     */
    private void fillPrescription(Pharmacist pharmacist, Pharmacy pharmacy) {
        System.out.println("\n📋 ===== FILL PRESCRIPTION ===== 📋");
        
        List<Prescription> pendingPrescriptions = pharmacy.getPrescriptions();
        
        if (pendingPrescriptions.isEmpty()) {
            System.out.println("No pending prescriptions for this pharmacy.");
            return;
        }
        
        // Display pending prescriptions
        System.out.println("Pending Prescriptions:");
        int pendingCount = 0;
        for (int i = 0; i < pendingPrescriptions.size(); i++) {
            Prescription prescription = pendingPrescriptions.get(i);
            if (prescription.getStatus().equals("Sent to Pharmacy")) {
                System.out.println((i + 1) + ". Prescription ID: " + prescription.getId() + 
                                 ", Patient ID: " + prescription.getPatientId() + 
                                 ", Issue Date: " + prescription.getIssueDate());
                pendingCount++;
            }
        }
        
        if (pendingCount == 0) {
            System.out.println("No prescriptions are pending to be filled.");
            return;
        }
        
        System.out.print("Select prescription number to fill: ");
        int prescriptionIndex = getIntInput() - 1;
        
        if (prescriptionIndex < 0 || prescriptionIndex >= pendingPrescriptions.size()) {
            System.out.println("Invalid prescription selection.");
            return;
        }
        
        Prescription selectedPrescription = pendingPrescriptions.get(prescriptionIndex);
        
        if (!selectedPrescription.getStatus().equals("Sent to Pharmacy")) {
            System.out.println("This prescription is not pending to be filled.");
            return;
        }
        
        // Check if the pharmacy has all the medicines in stock
        boolean canFill = true;
        StringBuilder missingMedicines = new StringBuilder();
        
        for (Map.Entry<Medicine, Integer> entry : selectedPrescription.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            
            // Find medicine in pharmacy inventory
            Medicine pharmacyMedicine = pharmacy.findMedicineById(medicine.getId());
            
            if (pharmacyMedicine == null) {
                canFill = false;
                missingMedicines.append("- ").append(medicine.getName()).append(" (not in inventory)\n");
            } else if (pharmacyMedicine.getQuantity() < quantity) {
                canFill = false;
                missingMedicines.append("- ").append(medicine.getName())
                             .append(" (only ").append(pharmacyMedicine.getQuantity())
                             .append(" available, need ").append(quantity).append(")\n");
            }
        }
        
        if (!canFill) {
            System.out.println("Cannot fill prescription due to insufficient stock:");
            System.out.println(missingMedicines.toString());
            return;
        }
        
        // Update inventory and fill prescription
        for (Map.Entry<Medicine, Integer> entry : selectedPrescription.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            
            // Find and update medicine in pharmacy inventory
            Medicine pharmacyMedicine = pharmacy.findMedicineById(medicine.getId());
            pharmacyMedicine.setQuantity(pharmacyMedicine.getQuantity() - quantity);
            
            System.out.println("Dispensed " + quantity + " of " + medicine.getName());
        }
        
        // Update prescription status
        selectedPrescription.setStatus("Filled");
        
        // Add to pharmacist's filled prescriptions
        pharmacist.getFilledPrescriptions().add(selectedPrescription);
        
        System.out.println("Prescription #" + selectedPrescription.getId() + " filled successfully.");
    }
    
    /**
     * Manage pharmacy inventory
     * 
     * @param pharmacy The pharmacy whose inventory to manage
     */
    private void manageInventory(Pharmacy pharmacy) {
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n💊 ===== PHARMACY INVENTORY ===== 💊");
            System.out.println("1. 📋 View Inventory");
            System.out.println("2. ➕ Add Stock");
            System.out.println("3. ➖ Remove Stock");
            System.out.println("4. 🔄 Add New Medicine");
            System.out.println("5. 🚪 Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    viewInventory(pharmacy);
                    break;
                case 2:
                    addStock(pharmacy);
                    break;
                case 3:
                    removeStock(pharmacy);
                    break;
                case 4:
                    addNewMedicine(pharmacy);
                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * View pharmacy inventory
     * 
     * @param pharmacy The pharmacy whose inventory to view
     */
    private void viewInventory(Pharmacy pharmacy) {
        System.out.println("\n📋 ===== PHARMACY INVENTORY ===== 📋");
        
        List<Medicine> inventory = pharmacy.getMedicines();
        
        if (inventory.isEmpty()) {
            System.out.println("No medicines in pharmacy inventory.");
            return;
        }
        
        // Display inventory with categories
        Map<String, List<Medicine>> categorizedMedicines = pharmacy.getCategorizedMedicines();
        
        for (Map.Entry<String, List<Medicine>> entry : categorizedMedicines.entrySet()) {
            String category = entry.getKey();
            List<Medicine> categoryMedicines = entry.getValue();
            
            System.out.println("\n=== " + category + " ===");
            
            for (Medicine medicine : categoryMedicines) {
                System.out.println("ID: " + medicine.getId() + 
                                 ", Name: " + medicine.getName() + 
                                 ", Price: " + medicine.getPrice() + " LE" + 
                                 ", Quantity: " + medicine.getQuantity() +
                                 (medicine.isRequiresPrescription() ? " (Prescription Required)" : ""));
            }
        }
    }
    
    /**
     * Add stock to existing medicine in pharmacy inventory
     * 
     * @param pharmacy The pharmacy whose inventory to update
     */
    private void addStock(Pharmacy pharmacy) {
        System.out.println("\n➕ ===== ADD STOCK ===== ➕");
        
        List<Medicine> inventory = pharmacy.getMedicines();
        
        if (inventory.isEmpty()) {
            System.out.println("No medicines in pharmacy inventory.");
            return;
        }
        
        // Display all medicines
        System.out.println("Available Medicines:");
        for (int i = 0; i < inventory.size(); i++) {
            Medicine medicine = inventory.get(i);
            System.out.println((i + 1) + ". " + medicine.getName() + " (Current Stock: " + medicine.getQuantity() + ")");
        }
        
        System.out.print("Select medicine number: ");
        int medicineIndex = getIntInput() - 1;
        
        if (medicineIndex < 0 || medicineIndex >= inventory.size()) {
            System.out.println("Invalid medicine selection.");
            return;
        }
        
        Medicine selectedMedicine = inventory.get(medicineIndex);
        
        System.out.print("Enter quantity to add: ");
        int quantity = getIntInput();
        
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }
        
        // Update quantity
        selectedMedicine.setQuantity(selectedMedicine.getQuantity() + quantity);
        
        System.out.println("Added " + quantity + " units of " + selectedMedicine.getName() + 
                         ". New stock level: " + selectedMedicine.getQuantity());
    }
    
    /**
     * Remove stock from existing medicine in pharmacy inventory
     * 
     * @param pharmacy The pharmacy whose inventory to update
     */
    private void removeStock(Pharmacy pharmacy) {
        System.out.println("\n➖ ===== REMOVE STOCK ===== ➖");
        
        List<Medicine> inventory = pharmacy.getMedicines();
        
        if (inventory.isEmpty()) {
            System.out.println("No medicines in pharmacy inventory.");
            return;
        }
        
        // Display all medicines
        System.out.println("Available Medicines:");
        for (int i = 0; i < inventory.size(); i++) {
            Medicine medicine = inventory.get(i);
            System.out.println((i + 1) + ". " + medicine.getName() + " (Current Stock: " + medicine.getQuantity() + ")");
        }
        
        System.out.print("Select medicine number: ");
        int medicineIndex = getIntInput() - 1;
        
        if (medicineIndex < 0 || medicineIndex >= inventory.size()) {
            System.out.println("Invalid medicine selection.");
            return;
        }
        
        Medicine selectedMedicine = inventory.get(medicineIndex);
        
        System.out.print("Enter quantity to remove: ");
        int quantity = getIntInput();
        
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }
        
        if (quantity > selectedMedicine.getQuantity()) {
            System.out.println("Cannot remove more than current stock.");
            return;
        }
        
        // Update quantity
        selectedMedicine.setQuantity(selectedMedicine.getQuantity() - quantity);
        
        System.out.println("Removed " + quantity + " units of " + selectedMedicine.getName() + 
                         ". New stock level: " + selectedMedicine.getQuantity());
    }
    
    /**
     * Add a new medicine to pharmacy inventory
     * 
     * @param pharmacy The pharmacy whose inventory to update
     */
    private void addNewMedicine(Pharmacy pharmacy) {
        System.out.println("\n🔄 ===== ADD NEW MEDICINE ===== 🔄");
        
        // Display all available medicines in the main system
        System.out.println("Available Medicines in System:");
        
        List<Medicine> pharmacyMedicines = pharmacy.getMedicines();
        
        for (int i = 0; i < medicines.size(); i++) {
            Medicine medicine = medicines.get(i);
            
            // Check if medicine is already in pharmacy inventory
            boolean alreadyInPharmacy = pharmacyMedicines.stream()
                .anyMatch(m -> m.getId() == medicine.getId());
            
            if (!alreadyInPharmacy) {
                System.out.println((i + 1) + ". " + medicine.getName() + 
                                 " - " + medicine.getDescription() + 
                                 " (Price: " + medicine.getPrice() + " LE)");
            }
        }
        
        System.out.print("Select medicine number to add to pharmacy (0 to cancel): ");
        int medicineIndex = getIntInput() - 1;
        
        if (medicineIndex == -1) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        if (medicineIndex < 0 || medicineIndex >= medicines.size()) {
            System.out.println("Invalid medicine selection.");
            return;
        }
        
        Medicine selectedMedicine = medicines.get(medicineIndex);
        
        // Check if medicine is already in pharmacy inventory
        boolean alreadyInPharmacy = pharmacyMedicines.stream()
            .anyMatch(m -> m.getId() == selectedMedicine.getId());
        
        if (alreadyInPharmacy) {
            System.out.println("This medicine is already in the pharmacy inventory.");
            return;
        }
        
        System.out.print("Enter initial quantity: ");
        int quantity = getIntInput();
        
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }
        
        // Create a copy of the medicine for the pharmacy
        Medicine pharmacyMedicine = new Medicine(
            selectedMedicine.getId(),
            selectedMedicine.getName(),
            selectedMedicine.getDescription(),
            selectedMedicine.getManufacturer(),
            selectedMedicine.getPrice(),
            quantity,
            selectedMedicine.getCategory(),
            selectedMedicine.isRequiresPrescription()
        );
        
        // Add medicine to pharmacy inventory
        pharmacy.addMedicine(pharmacyMedicine);
        
        System.out.println("Added " + pharmacyMedicine.getName() + " to pharmacy inventory with quantity " + quantity + ".");
    }
    
    /**
     * Update pharmacist's profile
     * 
     * @param pharmacist The pharmacist to update
     */
    private void updateProfile(Pharmacist pharmacist) {
        System.out.println("\n👤 ===== UPDATE PROFILE ===== 👤");
        
        System.out.println("Current Profile:");
        System.out.println("Name: " + pharmacist.getName());
        System.out.println("Email: " + pharmacist.getEmail());
        System.out.println("Phone: " + pharmacist.getPhoneNumber());
        System.out.println("Position: " + pharmacist.getPosition());
        System.out.println("License Number: " + pharmacist.getLicenseNumber());
        
        System.out.println("\nEnter new details (leave blank to keep current value):");
        
        System.out.print("Name [" + pharmacist.getName() + "]: ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            pharmacist.setName(name);
        }
        
        System.out.print("Email [" + pharmacist.getEmail() + "]: ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) {
            pharmacist.setEmail(email);
        }
        
        System.out.print("Phone [" + pharmacist.getPhoneNumber() + "]: ");
        String phone = scanner.nextLine();
        if (!phone.isEmpty()) {
            pharmacist.setPhoneNumber(phone);
        }
        
        System.out.print("Position [" + pharmacist.getPosition() + "]: ");
        String position = scanner.nextLine();
        if (!position.isEmpty()) {
            pharmacist.setPosition(position);
        }
        
        System.out.print("License Number [" + pharmacist.getLicenseNumber() + "]: ");
        String licenseNumber = scanner.nextLine();
        if (!licenseNumber.isEmpty()) {
            pharmacist.setLicenseNumber(licenseNumber);
        }
        
        System.out.print("New Password (leave blank to keep current): ");
        String password = scanner.nextLine();
        if (!password.isEmpty()) {
            pharmacist.setPassword(password);
        }
        
        System.out.println("Profile updated successfully.");
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
     * Public method to fill a prescription by IDs
     * 
     * @param pharmacistId ID of the pharmacist filling the prescription
     * @param prescriptionId ID of the prescription to fill
     * @return True if prescription was filled successfully, false otherwise
     */
    public boolean fillPrescription(int pharmacistId, int prescriptionId) {
        // Find the pharmacist
        Pharmacist pharmacist = pharmacists.stream()
            .filter(p -> p.getId() == pharmacistId)
            .findFirst()
            .orElse(null);
            
        if (pharmacist == null) {
            System.out.println("Pharmacist with ID " + pharmacistId + " not found.");
            return false;
        }
        
        // Find the pharmacy
        Pharmacy pharmacy = pharmacies.stream()
            .filter(p -> p.getId() == pharmacist.getPharmacyId())
            .findFirst()
            .orElse(null);
            
        if (pharmacy == null) {
            System.out.println("Pharmacy not found for this pharmacist.");
            return false;
        }
        
        // Find the prescription
        Prescription prescription = prescriptions.stream()
            .filter(p -> p.getId() == prescriptionId)
            .findFirst()
            .orElse(null);
            
        if (prescription == null) {
            System.out.println("Prescription with ID " + prescriptionId + " not found.");
            return false;
        }
        
        // Check if prescription is sent to this pharmacy
        if (prescription.getPharmacyId() != pharmacy.getId()) {
            System.out.println("This prescription is not assigned to your pharmacy.");
            return false;
        }
        
        // Check prescription status
        if (!prescription.getStatus().equals("Sent to Pharmacy")) {
            System.out.println("This prescription is not pending to be filled. Current status: " + prescription.getStatus());
            return false;
        }
        
        // Check if the pharmacy has all the medicines in stock
        boolean canFill = true;
        StringBuilder missingMedicines = new StringBuilder();
        
        for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            
            // Find medicine in pharmacy inventory
            Medicine pharmacyMedicine = pharmacy.findMedicineById(medicine.getId());
            
            if (pharmacyMedicine == null) {
                canFill = false;
                missingMedicines.append("- ").append(medicine.getName()).append(" (not in inventory)\n");
            } else if (pharmacyMedicine.getQuantity() < quantity) {
                canFill = false;
                missingMedicines.append("- ").append(medicine.getName())
                             .append(" (only ").append(pharmacyMedicine.getQuantity())
                             .append(" available, need ").append(quantity).append(")\n");
            }
        }
        
        if (!canFill) {
            System.out.println("Cannot fill prescription due to insufficient stock:");
            System.out.println(missingMedicines.toString());
            return false;
        }
        
        // Update inventory and fill prescription
        for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            
            // Find and update medicine in pharmacy inventory
            Medicine pharmacyMedicine = pharmacy.findMedicineById(medicine.getId());
            pharmacyMedicine.setQuantity(pharmacyMedicine.getQuantity() - quantity);
            
            System.out.println("Dispensed " + quantity + " of " + medicine.getName());
        }
        
        // Update prescription status
        prescription.setStatus("Filled");
        
        // Add to pharmacist's filled prescriptions
        pharmacist.getFilledPrescriptions().add(prescription);
        
        System.out.println("Prescription #" + prescription.getId() + " filled successfully.");
        return true;
    }
    
    /**
     * Add medicine to pharmacy inventory
     * 
     * @param pharmacyId ID of the pharmacy to add medicine to
     * @param medicineId ID of the medicine to add
     * @param quantity Quantity to add
     * @return True if medicine was added successfully, false otherwise
     */
    public boolean addMedicineToPharmacy(int pharmacyId, int medicineId, int quantity) {
        // Find the pharmacy
        Pharmacy pharmacy = pharmacies.stream()
            .filter(p -> p.getId() == pharmacyId)
            .findFirst()
            .orElse(null);
            
        if (pharmacy == null) {
            System.out.println("Pharmacy with ID " + pharmacyId + " not found.");
            return false;
        }
        
        // Find the medicine in the global list
        Medicine globalMedicine = medicines.stream()
            .filter(m -> m.getId() == medicineId)
            .findFirst()
            .orElse(null);
            
        if (globalMedicine == null) {
            System.out.println("Medicine with ID " + medicineId + " not found.");
            return false;
        }
        
        // Validate quantity
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return false;
        }
        
        // Check if the medicine already exists in the pharmacy
        Medicine pharmacyMedicine = pharmacy.findMedicineById(medicineId);
        
        if (pharmacyMedicine != null) {
            // Update existing medicine quantity
            pharmacyMedicine.setQuantity(pharmacyMedicine.getQuantity() + quantity);
            System.out.println("Added " + quantity + " units of " + pharmacyMedicine.getName() + 
                             ". New stock level: " + pharmacyMedicine.getQuantity());
        } else {
            // Create a new instance of the medicine for the pharmacy
            Medicine newPharmacyMedicine = new Medicine(
                globalMedicine.getId(),
                globalMedicine.getName(),
                globalMedicine.getDescription(),
                globalMedicine.getManufacturer(),
                globalMedicine.getPrice(),
                quantity,
                globalMedicine.getCategory(),
                globalMedicine.isRequiresPrescription()
            );
            
            // Add to pharmacy inventory
            pharmacy.addMedicine(newPharmacyMedicine);
            
            System.out.println("Added " + quantity + " units of " + newPharmacyMedicine.getName() + 
                             " to pharmacy inventory.");
        }
        
        return true;
    }
    
    /**
     * Get pending prescriptions for a specific pharmacy
     * 
     * @param pharmacyId ID of the pharmacy
     * @return List of pending prescriptions
     */
    public List<Prescription> getPendingPrescriptions(int pharmacyId) {
        // Find the pharmacy
        Pharmacy pharmacy = pharmacies.stream()
            .filter(p -> p.getId() == pharmacyId)
            .findFirst()
            .orElse(null);
            
        if (pharmacy == null) {
            System.out.println("Pharmacy with ID " + pharmacyId + " not found.");
            return List.of(); // Return empty list
        }
        
        // Filter pending prescriptions
        return prescriptions.stream()
            .filter(p -> p.getPharmacyId() == pharmacyId && p.getStatus().equals("Sent to Pharmacy"))
            .toList();
    }
    
    /**
     * Get filled prescriptions by a specific pharmacist
     * 
     * @param pharmacistId ID of the pharmacist
     * @return List of filled prescriptions
     */
    public List<Prescription> getFilledPrescriptions(int pharmacistId) {
        // Find the pharmacist
        Pharmacist pharmacist = pharmacists.stream()
            .filter(p -> p.getId() == pharmacistId)
            .findFirst()
            .orElse(null);
            
        if (pharmacist == null) {
            System.out.println("Pharmacist with ID " + pharmacistId + " not found.");
            return List.of(); // Return empty list
        }
        
        return pharmacist.getFilledPrescriptions();
    }
}