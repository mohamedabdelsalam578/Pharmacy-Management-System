package models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


public class Prescription {
    private int id;
    private int patientId;
    private int doctorId;
    private int pharmacyId; // ID of the pharmacy assigned to fill this prescription
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private PrescriptionStatus status;
    private String instructions;
    private Map<Medicine, Integer> medicines; // Medicine and quantity
    private String rejectionReason;

    /**
     * Constructor for Prescription class with status and pharmacyId
     * 
     * @param id Prescription ID
     * @param patientId ID of the patient for whom the prescription is issued
     * @param doctorId ID of the doctor who issued the prescription
     * @param pharmacyId ID of the pharmacy assigned to fill this prescription
     * @param issueDate Date when the prescription was issued
     * @param expiryDate Date when the prescription will expire
     * @param status Initial status of the prescription
     * @param instructions Special instructions for taking the medicines
     */
    public Prescription(int id, int patientId, int doctorId, int pharmacyId, LocalDate issueDate, 
                      LocalDate expiryDate, PrescriptionStatus status, String instructions) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.pharmacyId = pharmacyId;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.instructions = instructions;
        this.medicines = new HashMap<>();
    }
    
    /**
     * Constructor for Prescription class with status but without pharmacyId
     * 
     * @param id Prescription ID
     * @param patientId ID of the patient for whom the prescription is issued
     * @param doctorId ID of the doctor who issued the prescription
     * @param issueDate Date when the prescription was issued
     * @param expiryDate Date when the prescription will expire
     * @param status Initial status of the prescription
     * @param instructions Special instructions for taking the medicines
     */
    public Prescription(int id, int patientId, int doctorId, LocalDate issueDate, 
                      LocalDate expiryDate, PrescriptionStatus status, String instructions) {
        this(id, patientId, doctorId, 0, issueDate, expiryDate, status, instructions);
    }
    
    /**
     * Constructor for Prescription class with default status PENDING and without pharmacyId
     * 
     * @param id Prescription ID
     * @param patientId ID of the patient for whom the prescription is issued
     * @param doctorId ID of the doctor who issued the prescription
     * @param issueDate Date when the prescription was issued
     * @param expiryDate Date when the prescription will expire
     * @param instructions Special instructions for taking the medicines
     */
    public Prescription(int id, int patientId, int doctorId, LocalDate issueDate, 
                      LocalDate expiryDate, String instructions) {
        this(id, patientId, doctorId, 0, issueDate, expiryDate, PrescriptionStatus.PENDING, instructions);
    }

    /**
     * getId - Retrieves the unique identifier for this prescription
     * 
     * @return Unique prescription ID number
     */
    public int getId() {
        return id;
    }

    /**
     * getPatientId - Identifies the patient receiving this medication
     * 
     * 
     * @return ID of the Egyptian patient for whom the prescription is issued
     */
    public int getPatientId() {
        return patientId;
    }
    
    /**
     *  getDoctorId - Identifies the physician who authorized this medication
    
     * @return ID of the Egyptian doctor who issued the prescription
     */
    public int getDoctorId() {
        return doctorId;
    }
    
    /**
     *  getPharmacyId - Identifies the dispensing location for this prescription
     * 
     * @return ID of the Egyptian pharmacy assigned to fill this prescription
     */
    public int getPharmacyId() {
        return pharmacyId;
    }
    
    /**
     *setPharmacyId - Updates the dispensing location assignment
     * 
     * 
     * @param pharmacyId ID of the pharmacy newly assigned to fill this prescription
     */
    public void setPharmacyId(int pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    /**
     * getIssueDate - Retrieves when this prescription was created
     * 
     * @return LocalDate object representing when the prescription was issued
     */
    public LocalDate getIssueDate() {
        return issueDate;
    }

    /**
     * getExpiryDate - Determines when this prescription becomes invalid
     * 
     * The system uses this date to automatically flag expired prescriptions.
     * 
     * @return LocalDate object representing when the prescription expires
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     *  getStatus - Tracks the current state in the prescription lifecycle
     * 
     * @return String representing the current status in the prescription lifecycle
     */
    public PrescriptionStatus getStatus() {
        return status;
    }

    /**
     *  setStatus - Updates the prescription's position in the workflow
     * 
     * @param status New status value for the prescription
     */
    public void setStatus(PrescriptionStatus status) {
        this.status = status;
    }

    /**
     * getInstructions - Provides medication usage guidelines for the patient
     * 
     * @return String containing detailed instructions for medication usage
     */
    public String getInstructions() {
        return instructions;
    }
    
    /**
     * setInstructions - Updates the medication usage guidelines
     *
     * @param instructions New detailed instructions for medicine usage
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * getMedicines - Retrieves all medications in this prescription
     *
     * @return Map linking each Medicine object to its prescribed quantity
     */
    public Map<Medicine, Integer> getMedicines() {
        return medicines;
    }

    /**
     * addMedicine - Includes a medication in this prescription
     * @param medicine The Medicine object to add to this prescription
     * @param quantity Number of units of this medicine to prescribe
     * @return true if successfully added, false if validation failed
     */
    public boolean addMedicine(Medicine medicine, int quantity) {
        // Check if quantity is valid
        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return false;
        }
        
        // Add medicine to prescription
        medicines.put(medicine, quantity);
        System.out.println("Medicine added to prescription successfully.");
        return true;
    }

    /**
     * removeMedicine - Removes a medication from this prescription
     * @param medicineId ID of the medicine to remove from the prescription
     * @return true if found and removed successfully, false if not found
     */
    public boolean removeMedicine(int medicineId) {
        // Find medicine in the prescription
        Medicine medicine = medicines.keySet().stream()
                                  .filter(m -> m.getId() == medicineId)
                                  .findFirst()
                                  .orElse(null);
        
        if (medicine == null) {
            System.out.println("Medicine with ID " + medicineId + " not found in prescription.");
            return false;
        }
        
        // Remove medicine from prescription
        medicines.remove(medicine);
        System.out.println("Medicine removed from prescription successfully.");
        return true;
    }

    /**
     * Calculate the total cost of the prescription
     * 
     * @return Total cost of the medicines in the prescription in LE
     */
    public double calculateTotalCost() {
        return medicines.entrySet().stream()
                      .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                      .sum();
    }
    
    /**
     * containsMedicine - Verifies if a specific medication is prescribed
 
     * @param medicineId ID of the medicine to look for in the prescription
     * @return true if the medicine is in the prescription, false otherwise
     */
    public boolean containsMedicine(int medicineId) {
        return medicines.keySet().stream()
                       .anyMatch(medicine -> medicine.getId() == medicineId);
    }

    /**
     *isExpired - Determines if this prescription is still valid
     * @return true if the prescription has expired, false if still valid
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    /**
     *displayInfo - Outputs formatted prescription details to console
     * 
     */
    public void displayInfo() {
        System.out.println("\n===== PRESCRIPTION INFORMATION =====");
        System.out.println("ID: " + id);
        System.out.println("Patient ID: " + patientId);
        System.out.println("Doctor ID: " + doctorId);
        if (pharmacyId > 0) {
            System.out.println("Pharmacy ID: " + pharmacyId);
        }
        System.out.println("Issue Date: " + issueDate);
        System.out.println("Expiry Date: " + expiryDate);
        System.out.println("Status: " + status);
        System.out.println("Instructions: " + instructions);
        System.out.println("Total Medicines: " + medicines.size());
        System.out.println("Total Cost: " + String.format("%.2f", calculateTotalCost()) + " LE");
        
        if (!medicines.isEmpty()) {
            System.out.println("\nMedicines:");
            System.out.printf("%-5s %-20s %-30s %-10s %-10s\n", "ID", "Name", "Description", "Price", "Quantity");
            System.out.println("--------------------------------------------------------------------------------");
            
            for (Map.Entry<Medicine, Integer> entry : medicines.entrySet()) {
                Medicine medicine = entry.getKey();
                int quantity = entry.getValue();
                
                System.out.printf("%-5d %-20s %-30s %-9.2f LE %-10d\n",
                                medicine.getId(),
                                medicine.getName(),
                                medicine.getDescription(),
                                medicine.getPrice(),
                                quantity);
            }
        }
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String reason) {
        this.rejectionReason = reason;
    }
}