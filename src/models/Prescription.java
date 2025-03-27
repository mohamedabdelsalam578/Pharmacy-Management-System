package models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 💊 Prescription - The bridge between doctors, patients, and pharmacists 💊
 * 
 * This class is a critical connector in our healthcare workflow! It represents a real-world
 * prescription that travels from doctors to pharmacists to patients, demonstrating how
 * object-oriented design mirrors real-world interactions.
 * 
 * 🔑 Key Features and OOP Concepts:
 * - Composition: Uses a HashMap to store Medicine objects (Has-A relationship)
 * - Data validation: Ensures quantities are positive before adding medicines
 * - Encapsulation: Private data with controlled access via methods
 * - Real-world modeling: Tracks lifecycle states (Pending → Filled → Expired)
 * 
 * 📋 Workflow Representation:
 * 1. Doctor creates a Prescription for a Patient (issueDate, expiryDate)
 * 2. Doctor adds medicines with dosage instructions
 * 3. Prescription is sent to Pharmacy (status changes)
 * 4. Pharmacist processes the Prescription (more status changes)
 * 5. Patient receives the filled Prescription
 * 
 * 💡 This class demonstrates applying software design to real-world processes!
 */
public class Prescription {
    private int id;
    private int patientId;
    private int doctorId;
    private int pharmacyId; // ID of the pharmacy assigned to fill this prescription
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status; // Pending, Sent to Pharmacy, Filled, Expired
    private String instructions;
    private Map<Medicine, Integer> medicines; // Medicine and quantity

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
                      LocalDate expiryDate, String status, String instructions) {
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
                      LocalDate expiryDate, String status, String instructions) {
        this(id, patientId, doctorId, 0, issueDate, expiryDate, status, instructions);
    }
    
    /**
     * Constructor for Prescription class with default status "Pending" and without pharmacyId
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
        this(id, patientId, doctorId, 0, issueDate, expiryDate, "Pending", instructions);
    }

    /**
     * 🆔 getId - Retrieves the unique identifier for this prescription
     * 
     * This method returns the unique ID assigned to the prescription when created.
     * This ID is used throughout the pharmacy system for tracking, lookups,
     * and referencing this prescription in the healthcare workflow.
     * 
     * @return Unique prescription ID number
     */
    public int getId() {
        return id;
    }

    /**
     * 🧑‍⚕️ getPatientId - Identifies the patient receiving this medication
     * 
     * This method returns the ID of the patient for whom the prescription was
     * issued. It's a critical reference point in the healthcare workflow that
     * links the prescription to the correct patient record in the system.
     * 
     * @return ID of the Egyptian patient for whom the prescription is issued
     */
    public int getPatientId() {
        return patientId;
    }
    
    /**
     * 👨‍⚕️ getDoctorId - Identifies the physician who authorized this medication
     * 
     * This method returns the ID of the doctor who created and signed the
     * prescription. It's an important part of the medical audit trail and
     * ensures that all prescriptions can be traced back to the authorizing
     * physician as required by healthcare regulations.
     * 
     * @return ID of the Egyptian doctor who issued the prescription
     */
    public int getDoctorId() {
        return doctorId;
    }
    
    /**
     * 🏥 getPharmacyId - Identifies the dispensing location for this prescription
     * 
     * This method returns the ID of the pharmacy assigned to fill the prescription.
     * In the EL-TA3BAN workflow, prescriptions are typically routed to specific 
     * pharmacies based on location, availability of medications, or patient preference.
     * 
     * @return ID of the Egyptian pharmacy assigned to fill this prescription
     */
    public int getPharmacyId() {
        return pharmacyId;
    }
    
    /**
     * 🔄 setPharmacyId - Updates the dispensing location assignment
     * 
     * This method allows changing which pharmacy will handle the prescription.
     * It's used when routing prescriptions in the healthcare workflow, such as
     * during the initial assignment or when a pharmacy cannot fulfill the 
     * prescription and it needs to be redirected to another location.
     * 
     * @param pharmacyId ID of the pharmacy newly assigned to fill this prescription
     */
    public void setPharmacyId(int pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    /**
     * 📅 getIssueDate - Retrieves when this prescription was created
     * 
     * This method returns the date when the doctor created and authorized 
     * the prescription. This date is the starting point for prescription 
     * validity and is used to calculate if a prescription has expired.
     * 
     * @return LocalDate object representing when the prescription was issued
     */
    public LocalDate getIssueDate() {
        return issueDate;
    }

    /**
     * ⏳ getExpiryDate - Determines when this prescription becomes invalid
     * 
     * This method returns the date after which the prescription can no longer
     * be filled by a pharmacy. Egyptian regulations typically limit prescription
     * validity periods based on medication type and patient condition.
     * The system uses this date to automatically flag expired prescriptions.
     * 
     * @return LocalDate object representing when the prescription expires
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * 🔄 getStatus - Tracks the current state in the prescription lifecycle
     * 
     * This method returns the current status of the prescription as it moves
     * through the healthcare workflow. The status changes as the prescription
     * progresses through various stages from creation to fulfillment:
     * - "Pending": Initial state when created by doctor
     * - "Sent to Pharmacy": Prescription has been routed to a pharmacy
     * - "Filled": Medication has been dispensed to the patient
     * - "Expired": Prescription can no longer be filled (determined by expiry date)
     * 
     * @return String representing the current status in the prescription lifecycle
     */
    public String getStatus() {
        return status;
    }

    /**
     * 🔀 setStatus - Updates the prescription's position in the workflow
     * 
     * This method allows changing the status as the prescription moves through
     * the healthcare workflow. Status transitions typically follow this pattern:
     * 
     * Pending → Sent to Pharmacy → Filled
     *       ↘                     ↗
     *         → Expired →→→→→→→→→→
     *
     * Status changes can only be performed by authorized users according to
     * their role in the system (doctors, pharmacists).
     * 
     * @param status New status value for the prescription
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 📝 getInstructions - Provides medication usage guidelines for the patient
     * 
     * This method returns the doctor's instructions on how the prescribed 
     * medications should be taken. These instructions typically include:
     * - Dosage information (how much to take)
     * - Frequency (how often to take)
     * - Special considerations (with/without food, time of day)
     * - Duration of treatment
     * - Warnings or side effects to monitor
     * 
     * Clear instructions are critical for patient safety and treatment efficacy.
     * 
     * @return String containing detailed instructions for medication usage
     */
    public String getInstructions() {
        return instructions;
    }
    
    /**
     * 📝✏️ setInstructions - Updates the medication usage guidelines
     * 
     * This method allows doctors to modify the instructions for taking the
     * prescribed medications. Clear and accurate instructions are essential
     * for patient safety and treatment success.
     * 
     * 💡 OOP Concept: Encapsulation
     * This setter method demonstrates encapsulation by providing controlled
     * access to modify the internal state of the object. While instructions
     * can be read by any user via getInstructions(), they can only be
     * modified by authorized users through this method.
     * 
     * @param instructions New detailed instructions for medicine usage
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * 💊 getMedicines - Retrieves all medications in this prescription
     * 
     * This method returns a map containing all prescribed medications along
     * with their quantities. This data structure allows efficient lookup by
     * medicine object and tracks the prescribed amount of each medication.
     * 
     * 💡 Data Structure: Map
     * The use of Map<Medicine, Integer> demonstrates an appropriate data structure
     * where:
     * - Key: Medicine object (allows access to name, dosage, price, etc.)
     * - Value: Integer quantity (number of units prescribed)
     * 
     * This structure enables operations like calculating total cost and
     * generating detailed prescription information.
     * 
     * @return Map linking each Medicine object to its prescribed quantity
     */
    public Map<Medicine, Integer> getMedicines() {
        return medicines;
    }

    /**
     * ➕ addMedicine - Includes a medication in this prescription
     * 
     * This method adds a specific medicine with a specified quantity to the
     * prescription. It performs validation to ensure that the quantity is
     * positive before adding the medicine to the internal collection.
     * 
     * 💡 OOP Concept: Validation
     * This method demonstrates proper validation of method parameters:
     * 1. Checks that quantity is positive (> 0)
     * 2. Returns a boolean indicating success/failure
     * 3. Provides feedback to the user
     * 
     * The validation ensures data integrity and prevents invalid prescriptions.
     * 
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
     * ➖ removeMedicine - Removes a medication from this prescription
     * 
     * This method allows removing a medicine from the prescription by its ID.
     * It first locates the medicine using the Stream API, then removes it if found.
     * 
     * 💡 Modern Java Features Used:
     * - Stream API: Filters the medicine collection to find a match by ID
     * - Lambda expression: Provides the comparison logic
     * - Optional handling: Uses orElse(null) to handle the case when no match is found
     * 
     * This showcases how modern Java's functional programming features
     * make searching collections more expressive and less error-prone
     * than traditional looping constructs.
     * 
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
     * 💡 Modern Java Features Used:
     * - Stream API: Processes medicine collection declaratively
     * - Lambda expressions: Anonymous function for price calculation
     * - Method chaining: Creates a clean data processing pipeline
     * 
     * This method demonstrates how modern Java features make code more:
     * 1. Readable - Expresses intent clearly in a functional style
     * 2. Concise - Accomplishes in 3 lines what would take many lines in traditional loops
     * 3. Maintainable - Each operation is isolated and easier to modify
     * 
     * The calculation takes each medicine, multiplies its price by quantity,
     * and sums all products to get the total cost in Egyptian Pounds (LE).
     * 
     * @return Total cost of the medicines in the prescription in LE
     */
    public double calculateTotalCost() {
        return medicines.entrySet().stream()
                      .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                      .sum();
    }
    
    /**
     * 🔍 containsMedicine - Verifies if a specific medication is prescribed
     * 
     * This method checks if a particular medicine (identified by ID) is present
     * in the prescription. This is useful for avoiding duplicate medicines and
     * for checking if a specific medicine needs to be removed or updated.
     * 
     * 💡 Modern Java Features Used:
     * - Stream API: Processes the collection of medicines efficiently
     * - anyMatch method: Returns true if any element matches the predicate
     * - Lambda expression: Concisely defines the matching logic
     * 
     * The method shows how modern Java features can make common operations
     * like searching a collection more readable and maintainable.
     * 
     * @param medicineId ID of the medicine to look for in the prescription
     * @return true if the medicine is in the prescription, false otherwise
     */
    public boolean containsMedicine(int medicineId) {
        return medicines.keySet().stream()
                       .anyMatch(medicine -> medicine.getId() == medicineId);
    }

    /**
     * ⌛ isExpired - Determines if this prescription is still valid
     * 
     * This method checks if the current date is after the prescription's expiry date.
     * Expired prescriptions cannot be filled by pharmacies and should be 
     * flagged in the system to prevent misuse.
     * 
     * 💡 Java Date/Time API:
     * This method demonstrates proper usage of Java's modern date/time API:
     * - LocalDate.now(): Gets the current date
     * - isAfter(): Compares two dates in a readable way
     * 
     * The date/time API introduced in Java 8 provides more reliable and
     * intuitive date handling than older Java date libraries.
     * 
     * @return true if the prescription has expired, false if still valid
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    /**
     * 📊 displayInfo - Outputs formatted prescription details to console
     * 
     * This method prints a comprehensive summary of the prescription information
     * in a well-formatted, easy-to-read layout. It includes:
     * - Basic prescription metadata (IDs, dates, status)
     * - Patient instructions
     * - Financial summary (total cost in LE)
     * - Detailed medicine list with quantities and prices
     * 
     * 💡 Console Formatting Techniques:
     * - Separator lines and headers for visual organization
     * - Conditional output for optional fields (pharmacyId)
     * - Printf formatting for aligned tabular data
     * - Currency formatting with consistent decimal places
     * 
     * This demonstrates how to create readable console output for complex
     * data structures that helps users quickly understand the information.
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
}