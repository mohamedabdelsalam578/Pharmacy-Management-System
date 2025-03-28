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
    private List<Order> orders;
    private List<Prescription> prescriptions;
    private List<Consultation> consultations;
    
    /**
     * Constructor for creating a new patient
     * Used by FileHandler and other services for creating patients with full name
     * 
     * @param id The unique identifier for this patient
     * @param name The full name of this patient
     * @param username The username for this patient
     * @param password The password for this patient
     * @param email The email of this patient
     * @param phoneNumber The phone number of this patient
     * @param address The address of this patient
     */
    public Patient(int id, String name, String username, String password, 
                 String email, String phoneNumber, String address) {
        super(id, username, password, "PATIENT");
        
        // Split the full name into first and last name
        String[] nameParts = name.split(" ", 2);
        this.firstName = nameParts.length > 0 ? nameParts[0] : "";
        this.lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        this.dateOfBirth = new Date(); // Default date of birth
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.gender = "";
        this.insuranceInfo = "";
        this.emergencyContact = "";
        this.medicalHistory = "";
        this.allergies = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        
        // Initialize wallet
        this.wallet = new Wallet(id);
    }
    
    /**
     * Constructor for creating a new patient with detailed information
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
        super(id, username, passwordHash, "PATIENT");
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
        this.orders = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        
        // Initialize wallet
        this.wallet = new Wallet(id);
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
     * Set the full name of this patient
     * 
     * @param name The full name
     */
    public void setName(String name) {
        String[] nameParts = name.split(" ", 2);
        this.firstName = nameParts.length > 0 ? nameParts[0] : "";
        this.lastName = nameParts.length > 1 ? nameParts[1] : "";
    }
    
    /**
     * Get the full name of this patient with an alternate method name
     * 
     * @return The full name
     */
    public String getFullName() {
        return getName();
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
     * Get the date/time of this patient's birth date
     * Used by FileHandler for persistence
     * 
     * @return The date/time
     */
    public Date getDateTime() {
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
     * Get the orders of this patient
     * 
     * @return The orders
     */
    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }
    
    /**
     * Add an order to this patient
     * 
     * @param order The order to add
     */
    public void addOrder(Order order) {
        if (order != null && !orders.contains(order)) {
            orders.add(order);
        }
    }
    
    /**
     * Remove an order from this patient
     * 
     * @param order The order to remove
     * @return true if removed, false if not found
     */
    public boolean removeOrder(Order order) {
        return orders.remove(order);
    }
    
    /**
     * Get the prescriptions of this patient
     * 
     * @return The prescriptions
     */
    public List<Prescription> getPrescriptions() {
        return new ArrayList<>(prescriptions);
    }
    
    /**
     * Add a prescription to this patient
     * 
     * @param prescription The prescription to add
     */
    public void addPrescription(Prescription prescription) {
        if (prescription != null && !prescriptions.contains(prescription)) {
            prescriptions.add(prescription);
        }
    }
    
    /**
     * Remove a prescription from this patient
     * 
     * @param prescription The prescription to remove
     * @return true if removed, false if not found
     */
    public boolean removePrescription(Prescription prescription) {
        return prescriptions.remove(prescription);
    }
    
    /**
     * Find a prescription by ID
     * 
     * @param prescriptionId The ID of the prescription to find
     * @return The prescription, or null if not found
     */
    public Prescription findPrescriptionById(int prescriptionId) {
        for (Prescription prescription : prescriptions) {
            if (prescription.getId() == prescriptionId) {
                return prescription;
            }
        }
        return null;
    }
    
    /**
     * Find an order by ID
     * 
     * @param orderId The ID of the order to find
     * @return The order, or null if not found
     */
    public Order findOrderById(int orderId) {
        for (Order order : orders) {
            if (order.getId() == orderId) {
                return order;
            }
        }
        return null;
    }
    
    /**
     * Display information about this patient
     */
    @Override
    public void displayInfo() {
        System.out.println("\n===== PATIENT INFORMATION =====");
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Email: " + email);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Address: " + address);
        System.out.println("Wallet Balance: " + (wallet != null ? wallet.getBalance() : 0.0) + " LE");
        System.out.println("Orders: " + orders.size());
        System.out.println("Prescriptions: " + prescriptions.size());
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
    
    /**
     * Get the consultations of this patient
     * 
     * @return The consultations
     */
    public List<Consultation> getConsultations() {
        if (consultations == null) {
            consultations = new ArrayList<>();
        }
        return new ArrayList<>(consultations);
    }
    
    /**
     * Add a consultation to this patient
     * 
     * @param consultation The consultation to add
     */
    public void addConsultation(Consultation consultation) {
        if (consultations == null) {
            consultations = new ArrayList<>();
        }
        
        if (consultation != null && !consultations.contains(consultation)) {
            consultations.add(consultation);
        }
    }
    
    /**
     * Place an order using a prescription
     * 
     * @param prescription The prescription to use
     * @param orderId The order ID to assign
     * @return The created order
     */
    public Order placeOrderWithPrescription(Prescription prescription, int orderId) {
        if (prescription == null) {
            return null;
        }
        
        Order order = new Order(orderId, getId(), new Date());
        order.setPrescriptionId(prescription.getId());
        
        // Add order to patient's order list
        addOrder(order);
        
        return order;
    }
}