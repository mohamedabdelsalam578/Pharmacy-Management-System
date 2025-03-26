package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Pharmacy class representing a pharmacy in the system
 */
public class Pharmacy {
    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private List<Pharmacist> pharmacists;
    private List<Medicine> medicines;
    private List<Prescription> prescriptions;

    /**
     * Constructor for Pharmacy class
     * 
     * @param id Pharmacy ID
     * @param name Pharmacy name
     * @param address Pharmacy address
     * @param phoneNumber Pharmacy phone number
     * @param email Pharmacy email
     */
    public Pharmacy(int id, String name, String address, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.pharmacists = new ArrayList<>();
        this.medicines = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
    }

    /**
     * Get pharmacy ID
     * 
     * @return Pharmacy ID
     */
    public int getId() {
        return id;
    }

    /**
     * Get pharmacy name
     * 
     * @return Pharmacy name
     */
    public String getName() {
        return name;
    }

    /**
     * Set pharmacy name
     * 
     * @param name New pharmacy name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get pharmacy address
     * 
     * @return Pharmacy address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set pharmacy address
     * 
     * @param address New pharmacy address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get pharmacy phone number
     * 
     * @return Pharmacy phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set pharmacy phone number
     * 
     * @param phoneNumber New pharmacy phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get pharmacy email
     * 
     * @return Pharmacy email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set pharmacy email
     * 
     * @param email New pharmacy email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get pharmacists working at this pharmacy
     * 
     * @return List of pharmacists
     */
    public List<Pharmacist> getPharmacists() {
        return pharmacists;
    }

    /**
     * Get medicines available at this pharmacy
     * 
     * @return List of medicines
     */
    public List<Medicine> getMedicines() {
        return medicines;
    }

    /**
     * Get prescriptions available at this pharmacy
     * 
     * @return List of prescriptions
     */
    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    /**
     * Find a medicine by ID
     * 
     * @param medicineId ID of the medicine to find
     * @return Medicine if found, null otherwise
     */
    public Medicine findMedicineById(int medicineId) {
        return medicines.stream()
                      .filter(m -> m.getId() == medicineId)
                      .findFirst()
                      .orElse(null);
    }

    /**
     * Find a prescription by ID
     * 
     * @param prescriptionId ID of the prescription to find
     * @return Prescription if found, null otherwise
     */
    public Prescription findPrescriptionById(int prescriptionId) {
        return prescriptions.stream()
                          .filter(p -> p.getId() == prescriptionId)
                          .findFirst()
                          .orElse(null);
    }

    /**
     * Add a pharmacist to the pharmacy
     * 
     * @param pharmacist Pharmacist to add
     * @return true if pharmacist was added successfully, false otherwise
     */
    public boolean addPharmacist(Pharmacist pharmacist) {
        // Check if pharmacist already works at this pharmacy
        if (pharmacists.stream().anyMatch(p -> p.getId() == pharmacist.getId())) {
            System.out.println("Pharmacist already works at this pharmacy.");
            return false;
        }
        
        pharmacists.add(pharmacist);
        System.out.println("Pharmacist added to pharmacy successfully.");
        return true;
    }

    /**
     * Remove a pharmacist from the pharmacy
     * 
     * @param pharmacistId ID of the pharmacist to remove
     * @return true if pharmacist was removed successfully, false otherwise
     */
    public boolean removePharmacist(int pharmacistId) {
        // Check if pharmacist works at this pharmacy
        Pharmacist pharmacist = pharmacists.stream()
                                         .filter(p -> p.getId() == pharmacistId)
                                         .findFirst()
                                         .orElse(null);
        
        if (pharmacist == null) {
            System.out.println("Pharmacist with ID " + pharmacistId + " does not work at this pharmacy.");
            return false;
        }
        
        pharmacists.remove(pharmacist);
        System.out.println("Pharmacist removed from pharmacy successfully.");
        return true;
    }

    /**
     * Add a medicine to the pharmacy inventory
     * 
     * @param medicine Medicine to add
     * @return true if medicine was added successfully, false otherwise
     */
    public boolean addMedicine(Medicine medicine) {
        // Check if medicine already exists in the pharmacy
        Medicine existingMedicine = findMedicineById(medicine.getId());
        
        if (existingMedicine != null) {
            // Update quantity if medicine already exists
            existingMedicine.setQuantity(existingMedicine.getQuantity() + medicine.getQuantity());
            System.out.println("Medicine quantity updated in pharmacy.");
            return true;
        }
        
        // Add new medicine to pharmacy
        medicines.add(medicine);
        System.out.println("Medicine added to pharmacy successfully.");
        return true;
    }

    /**
     * Update medicine quantity in the pharmacy inventory
     * 
     * @param medicineId ID of the medicine to update
     * @param newQuantity New quantity for the medicine
     * @return true if medicine quantity was updated successfully, false otherwise
     */
    public boolean updateMedicineQuantity(int medicineId, int newQuantity) {
        Medicine medicine = findMedicineById(medicineId);
        
        if (medicine == null) {
            System.out.println("Medicine with ID " + medicineId + " not found in pharmacy.");
            return false;
        }
        
        medicine.setQuantity(newQuantity);
        System.out.println("Medicine quantity updated successfully.");
        return true;
    }

    /**
     * Check if the pharmacy has a medicine in the required quantity
     * 
     * @param medicineId ID of the medicine to check
     * @param requiredQuantity Required quantity of the medicine
     * @return true if the pharmacy has the medicine in the required quantity, false otherwise
     */
    public boolean hasMedicine(int medicineId, int requiredQuantity) {
        Medicine medicine = findMedicineById(medicineId);
        
        if (medicine == null) {
            return false;
        }
        
        return medicine.getQuantity() >= requiredQuantity;
    }

    /**
     * Dispense a medicine from the pharmacy inventory
     * 
     * @param medicineId ID of the medicine to dispense
     * @param quantity Quantity of the medicine to dispense
     * @return true if medicine was dispensed successfully, false otherwise
     */
    public boolean dispenseMedicine(int medicineId, int quantity) {
        Medicine medicine = findMedicineById(medicineId);
        
        if (medicine == null) {
            System.out.println("Medicine with ID " + medicineId + " not found in pharmacy.");
            return false;
        }
        
        if (medicine.getQuantity() < quantity) {
            System.out.println("Not enough stock for medicine: " + medicine.getName());
            return false;
        }
        
        medicine.setQuantity(medicine.getQuantity() - quantity);
        return true;
    }

    /**
     * Add a prescription to the pharmacy
     * 
     * @param prescription Prescription to add
     * @return true if prescription was added successfully, false otherwise
     */
    public boolean addPrescription(Prescription prescription) {
        // Check if prescription already exists in the pharmacy
        if (prescriptions.stream().anyMatch(p -> p.getId() == prescription.getId())) {
            System.out.println("Prescription already exists in pharmacy.");
            return false;
        }
        
        prescriptions.add(prescription);
        System.out.println("Prescription added to pharmacy successfully.");
        return true;
    }

    /**
     * Check if the pharmacy has a prescription
     * 
     * @param prescriptionId ID of the prescription to check
     * @return true if the pharmacy has the prescription, false otherwise
     */
    public boolean hasPrescription(int prescriptionId) {
        return prescriptions.stream().anyMatch(p -> p.getId() == prescriptionId);
    }

    /**
     * Calculate the total value of the pharmacy inventory
     * 
     * @return Total value of the pharmacy inventory in LE
     */
    public double calculateInventoryValue() {
        return medicines.stream()
                      .mapToDouble(m -> m.getPrice() * m.getQuantity())
                      .sum();
    }
    
    /**
     * Get medicines categorized by their category
     * 
     * @return Map of categories to lists of medicines
     */
    public Map<String, List<Medicine>> getCategorizedMedicines() {
        return medicines.stream()
                       .collect(Collectors.groupingBy(
                           medicine -> medicine.getCategory() != null ? 
                                      medicine.getCategory() : "Uncategorized"
                       ));
    }

    /**
     * Display pharmacy information
     */
    public void displayInfo() {
        System.out.println("\n===== PHARMACY INFORMATION =====");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Address: " + address);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Email: " + email);
        System.out.println("Number of Pharmacists: " + pharmacists.size());
        System.out.println("Number of Medicines: " + medicines.size());
        System.out.println("Number of Prescriptions: " + prescriptions.size());
        System.out.println("Inventory Value: " + String.format("%.2f", calculateInventoryValue()) + " LE");
    }
}