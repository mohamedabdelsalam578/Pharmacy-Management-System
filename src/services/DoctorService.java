package services;

import models.Doctor;
import models.Medicine;
import models.Patient;
import models.Prescription;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * DoctorService class handles doctor-specific operations
 */
public class DoctorService {
    private List<Doctor> doctors;
    private List<Patient> patients;
    private List<Prescription> prescriptions;
    private List<Medicine> medicines;
    
    private Scanner scanner;
    
    /**
     * Constructor to initialize DoctorService
     * 
     * @param doctors List of doctors
     * @param patients List of patients
     * @param prescriptions List of prescriptions
     * @param medicines List of medicines
     */
    public DoctorService(List<Doctor> doctors, List<Patient> patients, 
                       List<Prescription> prescriptions, List<Medicine> medicines) {
        this.doctors = doctors;
        this.patients = patients;
        this.prescriptions = prescriptions;
        this.medicines = medicines;
        
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Show doctor menu
     * 
     * @param doctor The logged-in doctor
     * @return True if the doctor logs out
     */
    public boolean showDoctorMenu(Doctor doctor) {
        boolean logout = false;
        
        while (!logout) {
            System.out.println("\n👨‍⚕️ ===== DOCTOR MENU ===== 👨‍⚕️");
            System.out.println("1. 📝 Create Prescription");
            System.out.println("2. 📋 View My Patients");
            System.out.println("3. 📄 View My Prescriptions");
            System.out.println("4. 👤 Update My Profile");
            System.out.println("5. 🚪 Logout");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    createPrescription(doctor);
                    break;
                case 2:
                    viewMyPatients(doctor);
                    break;
                case 3:
                    viewMyPrescriptions(doctor);
                    break;
                case 4:
                    updateProfile(doctor);
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
     * Create a new prescription for a patient
     * 
     * @param doctor The doctor creating the prescription
     */
    private void createPrescription(Doctor doctor) {
        System.out.println("\n📝 ===== CREATE PRESCRIPTION ===== 📝");
        
        // Show available patients
        System.out.println("\nAvailable Patients:");
        for (int i = 0; i < patients.size(); i++) {
            System.out.println((i + 1) + ". " + patients.get(i).getName() + " (ID: " + patients.get(i).getId() + ")");
        }
        
        System.out.print("Select patient number: ");
        int patientIndex = getIntInput() - 1;
        
        if (patientIndex < 0 || patientIndex >= patients.size()) {
            System.out.println("Invalid patient selection.");
            return;
        }
        
        Patient patient = patients.get(patientIndex);
        
        // Create prescription
        int prescriptionId = prescriptions.size() + 1;
        
        System.out.println("\nCreating prescription for " + patient.getName());
        System.out.print("Enter prescription instructions: ");
        String notes = scanner.nextLine();
        
        LocalDate issueDate = LocalDate.now();
        LocalDate expiryDate = issueDate.plusDays(30); // Default 30-day validity
        
        Prescription prescription = new Prescription(prescriptionId, patient.getId(), doctor.getId(), 
                                                  issueDate, expiryDate, notes);
        
        // Add medicines to prescription
        boolean addMoreMedicines = true;
        
        while (addMoreMedicines) {
            // Show all prescription-requiring medicines
            System.out.println("\nAvailable Prescription Medicines:");
            int count = 0;
            for (int i = 0; i < medicines.size(); i++) {
                Medicine medicine = medicines.get(i);
                if (medicine.isRequiresPrescription()) {
                    System.out.println((i + 1) + ". " + medicine.getName() + " - " + medicine.getDescription() + 
                                     " (Price: " + medicine.getPrice() + " LE)");
                    count++;
                }
            }
            
            if (count == 0) {
                System.out.println("No prescription medicines available in the system.");
                break;
            }
            
            System.out.print("Select medicine number (0 to finish): ");
            int medicineIndex = getIntInput() - 1;
            
            if (medicineIndex == -1) {
                addMoreMedicines = false;
                continue;
            }
            
            if (medicineIndex < 0 || medicineIndex >= medicines.size()) {
                System.out.println("Invalid medicine selection.");
                continue;
            }
            
            Medicine selectedMedicine = medicines.get(medicineIndex);
            
            if (!selectedMedicine.isRequiresPrescription()) {
                System.out.println("This medicine does not require a prescription.");
                continue;
            }
            
            System.out.print("Enter quantity: ");
            int quantity = getIntInput();
            
            if (quantity <= 0) {
                System.out.println("Quantity must be greater than 0.");
                continue;
            }
            
            // Add medicine to prescription
            prescription.addMedicine(selectedMedicine, quantity);
            
            System.out.println("Added " + quantity + " of " + selectedMedicine.getName() + " to the prescription.");
            
            System.out.print("Add another medicine? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            addMoreMedicines = choice.equals("y") || choice.equals("yes");
        }
        
        // Finalize and save prescription if it has medicines
        if (prescription.getMedicines().isEmpty()) {
            System.out.println("Prescription cancelled - no medicines were added.");
        } else {
            // Add prescription to the system
            prescriptions.add(prescription);
            
            // Add to doctor's issued prescriptions
            doctor.getIssuedPrescriptions().add(prescription);
            
            // Add to patient's prescriptions
            patient.addPrescription(prescription);
            
            System.out.println("Prescription #" + prescription.getId() + " created successfully for " + patient.getName() + ".");
        }
    }
    
    /**
     * View all patients who have prescriptions from this doctor
     * 
     * @param doctor The doctor whose patients to display
     */
    private void viewMyPatients(Doctor doctor) {
        System.out.println("\n📋 ===== MY PATIENTS ===== 📋");
        
        boolean found = false;
        for (Patient patient : patients) {
            boolean isMyPatient = patient.getPrescriptions().stream()
                .anyMatch(p -> p.getDoctorId() == doctor.getId());
            
            if (isMyPatient) {
                System.out.println("\nPatient ID: " + patient.getId());
                System.out.println("Name: " + patient.getName());
                System.out.println("Email: " + patient.getEmail());
                System.out.println("Phone: " + patient.getPhoneNumber());
                System.out.println("Address: " + patient.getAddress());
                
                // Count prescriptions issued to this patient
                long prescriptionCount = patient.getPrescriptions().stream()
                    .filter(p -> p.getDoctorId() == doctor.getId())
                    .count();
                
                System.out.println("Prescriptions Issued: " + prescriptionCount);
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("You don't have any patients yet.");
        }
    }
    
    /**
     * View all prescriptions issued by this doctor
     * 
     * @param doctor The doctor whose prescriptions to display
     */
    private void viewMyPrescriptions(Doctor doctor) {
        System.out.println("\n📄 ===== MY PRESCRIPTIONS ===== 📄");
        
        List<Prescription> issuedPrescriptions = doctor.getIssuedPrescriptions();
        
        if (issuedPrescriptions.isEmpty()) {
            System.out.println("You haven't issued any prescriptions yet.");
            return;
        }
        
        for (Prescription prescription : issuedPrescriptions) {
            System.out.println("\nPrescription ID: " + prescription.getId());
            
            // Find the patient name
            Patient patient = patients.stream()
                .filter(p -> p.getId() == prescription.getPatientId())
                .findFirst()
                .orElse(null);
            
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            System.out.println("Patient: " + patientName);
            System.out.println("Issue Date: " + prescription.getIssueDate());
            System.out.println("Expiry Date: " + prescription.getExpiryDate());
            System.out.println("Status: " + prescription.getStatus());
            System.out.println("Instructions: " + prescription.getInstructions());
            
            System.out.println("Medicines:");
            for (Medicine medicine : prescription.getMedicines().keySet()) {
                int quantity = prescription.getMedicines().get(medicine);
                System.out.println("- " + medicine.getName() + " (" + quantity + "): " + 
                                 medicine.getPrice() * quantity + " LE");
            }
        }
    }
    
    /**
     * Update doctor's profile
     * 
     * @param doctor The doctor to update
     */
    private void updateProfile(Doctor doctor) {
        System.out.println("\n👤 ===== UPDATE PROFILE ===== 👤");
        
        System.out.println("Current Profile:");
        System.out.println("Name: " + doctor.getName());
        System.out.println("Email: " + doctor.getEmail());
        System.out.println("Phone: " + doctor.getPhoneNumber());
        System.out.println("Specialization: " + doctor.getSpecialization());
        System.out.println("License Number: " + doctor.getLicenseNumber());
        
        System.out.println("\nEnter new details (leave blank to keep current value):");
        
        System.out.print("Name [" + doctor.getName() + "]: ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            doctor.setName(name);
        }
        
        System.out.print("Email [" + doctor.getEmail() + "]: ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) {
            doctor.setEmail(email);
        }
        
        System.out.print("Phone [" + doctor.getPhoneNumber() + "]: ");
        String phone = scanner.nextLine();
        if (!phone.isEmpty()) {
            doctor.setPhoneNumber(phone);
        }
        
        System.out.print("Specialization [" + doctor.getSpecialization() + "]: ");
        String specialization = scanner.nextLine();
        if (!specialization.isEmpty()) {
            doctor.setSpecialization(specialization);
        }
        
        System.out.print("License Number [" + doctor.getLicenseNumber() + "]: ");
        String licenseNumber = scanner.nextLine();
        if (!licenseNumber.isEmpty()) {
            doctor.setLicenseNumber(licenseNumber);
        }
        
        System.out.print("New Password (leave blank to keep current): ");
        String password = scanner.nextLine();
        if (!password.isEmpty()) {
            doctor.setPassword(password);
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
     * View prescriptions by doctor ID
     * 
     * @param doctorId The ID of the doctor
     */
    public void viewDoctorPrescriptions(int doctorId) {
        System.out.println("\n📄 ===== DOCTOR'S PRESCRIPTIONS ===== 📄");
        
        // Find the doctor
        Doctor doctor = doctors.stream()
            .filter(d -> d.getId() == doctorId)
            .findFirst()
            .orElse(null);
            
        if (doctor == null) {
            System.out.println("Doctor with ID " + doctorId + " not found.");
            return;
        }
        
        List<Prescription> issuedPrescriptions = doctor.getIssuedPrescriptions();
        
        if (issuedPrescriptions.isEmpty()) {
            System.out.println("No prescriptions found for this doctor.");
            return;
        }
        
        for (Prescription prescription : issuedPrescriptions) {
            System.out.println("\nPrescription ID: " + prescription.getId());
            
            // Find the patient name
            Patient patient = patients.stream()
                .filter(p -> p.getId() == prescription.getPatientId())
                .findFirst()
                .orElse(null);
            
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            System.out.println("Patient: " + patientName);
            System.out.println("Issue Date: " + prescription.getIssueDate());
            System.out.println("Expiry Date: " + prescription.getExpiryDate());
            System.out.println("Status: " + prescription.getStatus());
            System.out.println("Instructions: " + prescription.getInstructions());
            
            System.out.println("Medicines:");
            for (Medicine medicine : prescription.getMedicines().keySet()) {
                int quantity = prescription.getMedicines().get(medicine);
                System.out.println("- " + medicine.getName() + " (" + quantity + "): " + 
                                 medicine.getPrice() * quantity + " LE");
            }
        }
    }
    
    /**
     * Create a prescription by doctor ID
     * 
     * @param doctorId The ID of the doctor creating the prescription
     * @param patientId The ID of the patient for the prescription
     * @param instructions Instructions for the prescription
     * @return The newly created prescription
     */
    public Prescription createPrescription(int doctorId, int patientId, String instructions) {
        // Find the doctor
        Doctor doctor = doctors.stream()
            .filter(d -> d.getId() == doctorId)
            .findFirst()
            .orElse(null);
            
        if (doctor == null) {
            System.out.println("Doctor with ID " + doctorId + " not found.");
            return null;
        }
        
        // Find the patient
        Patient patient = patients.stream()
            .filter(p -> p.getId() == patientId)
            .findFirst()
            .orElse(null);
            
        if (patient == null) {
            System.out.println("Patient with ID " + patientId + " not found.");
            return null;
        }
        
        // Create a new prescription
        int prescriptionId = prescriptions.size() + 1;
        LocalDate issueDate = LocalDate.now();
        LocalDate expiryDate = issueDate.plusDays(30); // Default 30-day validity
        
        Prescription prescription = new Prescription(prescriptionId, patientId, doctorId, 
                                                  issueDate, expiryDate, instructions);
        
        // Add prescription to the system
        prescriptions.add(prescription);
        
        // Add to doctor's issued prescriptions
        doctor.getIssuedPrescriptions().add(prescription);
        
        // Add to patient's prescriptions
        patient.addPrescription(prescription);
        
        System.out.println("Prescription #" + prescription.getId() + " created successfully for " + patient.getName() + ".");
        
        return prescription;
    }
    
    /**
     * Add a medicine to a prescription
     * 
     * @param doctorId The ID of the doctor
     * @param prescriptionId The ID of the prescription
     * @param medicineId The ID of the medicine to add
     * @param quantity The quantity of medicine to add
     * @return True if medicine was added successfully, false otherwise
     */
    public boolean addMedicineToPrescription(int doctorId, int prescriptionId, int medicineId, int quantity) {
        // Find the doctor
        Doctor doctor = doctors.stream()
            .filter(d -> d.getId() == doctorId)
            .findFirst()
            .orElse(null);
            
        if (doctor == null) {
            System.out.println("Doctor with ID " + doctorId + " not found.");
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
        
        // Verify prescription belongs to this doctor
        if (prescription.getDoctorId() != doctorId) {
            System.out.println("This prescription was not issued by you.");
            return false;
        }
        
        // Find the medicine
        Medicine medicine = medicines.stream()
            .filter(m -> m.getId() == medicineId)
            .findFirst()
            .orElse(null);
            
        if (medicine == null) {
            System.out.println("Medicine with ID " + medicineId + " not found.");
            return false;
        }
        
        // Validate quantity
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return false;
        }
        
        // Add medicine to prescription
        prescription.addMedicine(medicine, quantity);
        
        System.out.println("Added " + quantity + " of " + medicine.getName() + " to prescription #" + prescriptionId);
        return true;
    }
    
    /**
     * Send a prescription to a pharmacy
     * 
     * @param doctorId The ID of the doctor
     * @param prescriptionId The ID of the prescription
     * @param pharmacyId The ID of the pharmacy
     * @return True if sent successfully, false otherwise
     */
    public boolean sendPrescriptionToPharmacy(int doctorId, int prescriptionId, int pharmacyId) {
        // Find the doctor
        Doctor doctor = doctors.stream()
            .filter(d -> d.getId() == doctorId)
            .findFirst()
            .orElse(null);
            
        if (doctor == null) {
            System.out.println("Doctor with ID " + doctorId + " not found.");
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
        
        // Verify prescription belongs to this doctor
        if (prescription.getDoctorId() != doctorId) {
            System.out.println("This prescription was not issued by you.");
            return false;
        }
        
        // Update prescription status and pharmacy ID
        prescription.setStatus("Sent to Pharmacy");
        prescription.setPharmacyId(pharmacyId);
        
        System.out.println("Prescription #" + prescriptionId + " sent to pharmacy with ID " + pharmacyId);
        return true;
    }
}