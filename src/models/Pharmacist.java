package models;

import java.util.ArrayList;
import java.util.List;

public class Pharmacist extends User {
    private String name;
    private String email;
    private String phoneNumber;
    private String licenseNumber;
    private String qualification;
    private int pharmacyId;
    private Pharmacy pharmacy;
    private List<Prescription> filledPrescriptions;
    private String position; // Job position in the pharmacy

    public Pharmacist(int id, String name, String username, String password, String email, 
                     String phoneNumber, String licenseNumber, String qualification) {
        super(id, username, password, "PHARMACIST");
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.qualification = qualification;
        this.filledPrescriptions = new ArrayList<>();
        this.position = "Staff Pharmacist"; // Default position
    }
    
    public Pharmacist(int id, String name, String username, String password, String email, 
                     String phoneNumber, String licenseNumber, String qualification, int pharmacyId) {
        this(id, name, username, password, email, phoneNumber, licenseNumber, qualification);
        this.pharmacyId = pharmacyId;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getQualification() { return qualification; }
    public int getPharmacyId() { return pharmacyId; }
    public Pharmacy getPharmacy() { return pharmacy; }
    public List<Prescription> getFilledPrescriptions() { return filledPrescriptions; }
    public String getPosition() { return position; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public void setPharmacyId(int pharmacyId) { this.pharmacyId = pharmacyId; }
    public void setPosition(String position) { this.position = position; }
    public void setPharmacy(Pharmacy pharmacy) { 
        this.pharmacy = pharmacy;
        if (pharmacy != null) {
            this.pharmacyId = pharmacy.getId();
        }
    }

    /**
     * Fill a prescription at the pharmacy
     * 
     * @param prescription The prescription to fill
     * @param pharmacy The pharmacy where the prescription is filled
     * @return true if the prescription was successfully filled, false otherwise
     */
    public boolean fillPrescription(Prescription prescription, Pharmacy pharmacy) {
        if (prescription == null || pharmacy == null) {
            return false;
        }
        
        // Check if the pharmacy has all required medicines
        for (Medicine medicine : prescription.getMedicines().keySet()) {
            int quantity = prescription.getMedicines().get(medicine);
            if (!pharmacy.hasMedicine(medicine.getId(), quantity)) {
                return false;
            }
        }
        
        // Dispense medicines
        for (Medicine medicine : prescription.getMedicines().keySet()) {
            int quantity = prescription.getMedicines().get(medicine);
            pharmacy.dispenseMedicine(medicine.getId(), quantity);
        }
        
        // Update prescription status
        prescription.setStatus("Filled");
        
        // Add to filled prescriptions
        filledPrescriptions.add(prescription);
        
        return true;
    }

    @Override
    public void displayInfo() {
        System.out.println("Pharmacist: " + getName() + " (License: " + licenseNumber + ")");
        System.out.println("Qualification: " + qualification);
        System.out.println("Pharmacy ID: " + pharmacyId);
        System.out.println("Filled Prescriptions: " + filledPrescriptions.size());
    }
}