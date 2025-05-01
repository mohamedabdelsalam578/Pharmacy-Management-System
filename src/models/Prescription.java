package models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Prescription {
    private int id;
    private int patientId;
    private int doctorId;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String instructions;
    private PrescriptionStatus status;
    private Map<Medicine, Integer> medicines;
    private int pharmacyId;

    public Prescription(int id, int patientId, int doctorId, LocalDate issueDate, LocalDate expiryDate, String instructions, PrescriptionStatus status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.instructions = instructions;
        this.status = status;
        this.medicines = new HashMap<>();
    }

    // Getters
    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getInstructions() { return instructions; }
    public PrescriptionStatus getStatus() { return status; }
    public Map<Medicine, Integer> getMedicines() { return medicines; }
    public int getPharmacyId() { return pharmacyId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public void setStatus(PrescriptionStatus status) { this.status = status; }
    public void setPharmacyId(int pharmacyId) { this.pharmacyId = pharmacyId; }

    public void addMedicine(Medicine medicine, int quantity) {
        if (medicine != null && quantity > 0) {
            medicines.put(medicine, quantity);
        }
    }

    public void removeMedicine(Medicine medicine) {
        medicines.remove(medicine);
    }

    public boolean hasMedicine(Medicine medicine) {
        return medicines.containsKey(medicine);
    }

    public int getMedicineQuantity(Medicine medicine) {
        return medicines.getOrDefault(medicine, 0);
    }

    @Override
    public String toString() {
        return String.format("Prescription #%d [Patient: %d, Doctor: %d, Status: %s]",
                id, patientId, doctorId, status.getDisplayName());
    }
}