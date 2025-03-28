package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a patient in the pharmacy system
 */
public class Patient extends User {
    private static final long serialVersionUID = 1L;
    
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String phoneNumber;
    private String address;
    private String email;
    private String gender;
    private String insuranceInfo;
    private String emergencyContact;
    private String medicalHistory;
    private List<String> allergies;
    private Wallet wallet;
    
    /**
     * Constructor for creating a new patient
     * 
     * @param id The unique identifier for this patient
     * @param username The username for this patient
     * @param passwordHash The password hash for this patient
     * @param firstName The first name of this patient
     * @param lastName The last name of this patient
     * @param dateOfBirth The date of birth of this patient
     * @param phoneNumber The phone number of this patient
     * @param address The address of this patient
     * @param email The email of this patient
     */
    public Patient(int id, String username, String passwordHash, String firstName, String lastName,
                 Date dateOfBirth, String phoneNumber, String address, String email) {
        super(id, username, passwordHash, UserType.PATIENT);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.gender = "";
        this.insuranceInfo = "";
        this.emergencyContact = "";
        this.medicalHistory = "";
        this.allergies = new ArrayList<>();
        this.wallet = null; // Will be initialized separately
    }
    
    /**
     * Constructor for creating a new patient with minimal information
     * 
     * @param id The unique identifier for this patient
     * @param username The username for this patient
     * @param passwordHash The password hash for this patient
     */
    public Patient(int id, String username, String passwordHash) {
        this(id, username, passwordHash, "", "", new Date(), "", "", "");
    }
    
    /**
     * Get the first name of this patient
     * 
     * @return The first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Set the first name of this patient
     * 
     * @param firstName The first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Get the last name of this patient
     * 
     * @return The last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Set the last name of this patient
     * 
     * @param lastName The last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Get the full name of this patient
     * 
     * @return The full name
     */
    public String getName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Get the date of birth of this patient
     * 
     * @return The date of birth
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    /**
     * Set the date of birth of this patient
     * 
     * @param dateOfBirth The date of birth
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    /**
     * Get the phone number of this patient
     * 
     * @return The phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    /**
     * Set the phone number of this patient
     * 
     * @param phoneNumber The phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    /**
     * Get the address of this patient
     * 
     * @return The address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Set the address of this patient
     * 
     * @param address The address
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Get the email of this patient
     * 
     * @return The email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Set the email of this patient
     * 
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Get the gender of this patient
     * 
     * @return The gender
     */
    public String getGender() {
        return gender;
    }
    
    /**
     * Set the gender of this patient
     * 
     * @param gender The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    /**
     * Get the insurance information of this patient
     * 
     * @return The insurance information
     */
    public String getInsuranceInfo() {
        return insuranceInfo;
    }
    
    /**
     * Set the insurance information of this patient
     * 
     * @param insuranceInfo The insurance information
     */
    public void setInsuranceInfo(String insuranceInfo) {
        this.insuranceInfo = insuranceInfo;
    }
    
    /**
     * Get the emergency contact of this patient
     * 
     * @return The emergency contact
     */
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    /**
     * Set the emergency contact of this patient
     * 
     * @param emergencyContact The emergency contact
     */
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    /**
     * Get the medical history of this patient
     * 
     * @return The medical history
     */
    public String getMedicalHistory() {
        return medicalHistory;
    }
    
    /**
     * Set the medical history of this patient
     * 
     * @param medicalHistory The medical history
     */
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
    
    /**
     * Get the allergies of this patient
     * 
     * @return The allergies
     */
    public List<String> getAllergies() {
        return new ArrayList<>(allergies);
    }
    
    /**
     * Set the allergies of this patient
     * 
     * @param allergies The allergies
     */
    public void setAllergies(List<String> allergies) {
        this.allergies = new ArrayList<>(allergies);
    }
    
    /**
     * Add an allergy to this patient
     * 
     * @param allergy The allergy to add
     */
    public void addAllergy(String allergy) {
        if (!allergies.contains(allergy)) {
            allergies.add(allergy);
        }
    }
    
    /**
     * Remove an allergy from this patient
     * 
     * @param allergy The allergy to remove
     * @return true if removed, false if not found
     */
    public boolean removeAllergy(String allergy) {
        return allergies.remove(allergy);
    }
    
    /**
     * Check if this patient has an allergy
     * 
     * @param allergy The allergy to check
     * @return true if the patient has the allergy, false otherwise
     */
    public boolean hasAllergy(String allergy) {
        return allergies.contains(allergy);
    }
    
    /**
     * Get the wallet of this patient
     * 
     * @return The wallet
     */
    public Wallet getWallet() {
        return wallet;
    }
    
    /**
     * Set the wallet of this patient
     * 
     * @param wallet The wallet
     */
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
    
    /**
     * Check if this patient has a wallet
     * 
     * @return true if the patient has a wallet, false otherwise
     */
    public boolean hasWallet() {
        return wallet != null;
    }
    
    /**
     * Get a formatted string representation of this patient
     * 
     * @return The formatted string
     */
    @Override
    public String toString() {
        return String.format("Patient [ID: %d, Name: %s %s, Email: %s, Phone: %s]", 
                getId(), firstName, lastName, email, phoneNumber);
    }
}