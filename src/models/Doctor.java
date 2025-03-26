package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

public class Doctor extends User {
    private String licenseNumber;
    private String specialization;
    private List<Prescription> issuedPrescriptions;
    private List<MedicalReport> issuedReports;
    private List<Consultation> consultations;

    public Doctor(int id, String name, String username, String password, String email, 
                 String phoneNumber, String licenseNumber, String specialization) {
        super(id, name, username, password, email, phoneNumber);
        this.licenseNumber = licenseNumber;
        this.specialization = specialization;
        this.issuedPrescriptions = new ArrayList<>();
        this.issuedReports = new ArrayList<>();
        this.consultations = new ArrayList<>();
    }

    // Simpler constructor for backward compatibility
    public Doctor(int id, String name, String username, String password, String email, 
                 String phoneNumber, String specialization) {
        this(id, name, username, password, email, phoneNumber, "L" + id, specialization);
    }

    // Getters
    public String getLicenseNumber() { return licenseNumber; }
    public String getSpecialization() { return specialization; }
    public List<Prescription> getPrescriptions() { return issuedPrescriptions; }
    public List<Prescription> getIssuedPrescriptions() { return issuedPrescriptions; }
    public List<MedicalReport> getIssuedReports() { return issuedReports; }
    public List<Consultation> getConsultations() { return consultations; }

    // Setters
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    /**
     * Find a prescription in the doctor's issued prescriptions by ID
     * 
     * @param prescriptionId ID of the prescription to find
     * @return The prescription if found, null otherwise
     */
    public Prescription findPrescriptionById(int prescriptionId) {
        return issuedPrescriptions.stream()
                                 .filter(p -> p.getId() == prescriptionId)
                                 .findFirst()
                                 .orElse(null);
    }

    /**
     * Find a medical report in the doctor's issued reports by ID
     * 
     * @param reportId ID of the report to find
     * @return The report if found, null otherwise
     */
    public MedicalReport findReportById(int reportId) {
        return issuedReports.stream()
                           .filter(r -> r.getId() == reportId)
                           .findFirst()
                           .orElse(null);
    }

    /**
     * Add a prescription to the doctor's issued prescriptions
     * 
     * @param prescription The prescription to add
     */
    public void addPrescription(Prescription prescription) { 
        issuedPrescriptions.add(prescription); 
    }

    /**
     * Add a medical report to the doctor's issued reports
     * 
     * @param report The report to add
     */
    public void addReport(MedicalReport report) {
        issuedReports.add(report);
    }

    /**
     * Add a consultation to the doctor's consultations
     * 
     * @param consultation The consultation to add
     */
    public void addConsultation(Consultation consultation) { 
        consultations.add(consultation); 
    }

    /**
     * Create a new prescription for a patient
     * 
     * @param patientId ID of the patient
     * @param prescriptionId ID for the new prescription
     * @param instructions Instructions for the prescription
     * @return The created prescription
     */
    public Prescription createPrescription(int patientId, int prescriptionId, String instructions) {
        Prescription prescription = new Prescription(
            prescriptionId,
            patientId,
            this.getId(),
            LocalDate.now(),
            LocalDate.now().plusMonths(1),
            "Created",
            instructions
        );
        
        issuedPrescriptions.add(prescription);
        return prescription;
    }

    /**
     * Add a medicine to a prescription
     * 
     * @param prescription The prescription to add the medicine to
     * @param medicine The medicine to add
     * @param quantity The quantity of the medicine
     * @return true if the medicine was added successfully, false otherwise
     */
    public boolean addMedicineToPrescription(Prescription prescription, Medicine medicine, int quantity) {
        if (prescription == null || medicine == null || quantity <= 0) {
            return false;
        }
        
        // If the medicine requires a prescription, mark it as having a prescription
        if (medicine.isRequiresPrescription()) {
            medicine.setPrescription(true);
        }
        
        // Add the medicine to the prescription
        prescription.addMedicine(medicine, quantity);
        return true;
    }

    /**
     * Send a prescription to a pharmacy
     * 
     * @param prescription The prescription to send
     * @param pharmacy The pharmacy to send the prescription to
     * @return true if the prescription was sent successfully, false otherwise
     */
    public boolean sendPrescriptionToPharmacy(Prescription prescription, Pharmacy pharmacy) {
        if (prescription == null || pharmacy == null) {
            return false;
        }
        
        // Update prescription status
        prescription.setStatus("Sent to Pharmacy");
        
        // Add prescription to pharmacy
        return pharmacy.addPrescription(prescription);
    }

    @Override
    public void displayInfo() {
        System.out.println("\n===== DOCTOR INFORMATION =====");
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("License Number: " + licenseNumber);
        System.out.println("Specialization: " + specialization);
        System.out.println("Email: " + getEmail());
        System.out.println("Phone Number: " + getPhoneNumber());
        System.out.println("Issued Prescriptions: " + issuedPrescriptions.size());
        System.out.println("Issued Medical Reports: " + issuedReports.size());
        System.out.println("Consultations: " + consultations.size());
    }
}