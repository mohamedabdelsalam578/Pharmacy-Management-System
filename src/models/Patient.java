package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a patient in the pharmacy system
 * Extends the User class with patient-specific properties
 */
public class Patient extends User {
    private static final long serialVersionUID = 1L;
    
    private String fullName;
    private String address;
    private String phoneNumber;
    private String email;
    private String gender;
    private int age;
    private Wallet wallet;
    private List<Order> orders;
    
    /**
     * Constructor for creating a new Patient
     * 
     * @param id The unique identifier for this patient
     * @param username The username for this patient
     * @param password The password for this patient
     * @param fullName The full name of this patient
     * @param address The address of this patient
     * @param phoneNumber The phone number of this patient
     * @param email The email address of this patient
     * @param gender The gender of this patient
     * @param age The age of this patient
     */
    public Patient(int id, String username, String password, String fullName, String address, 
                   String phoneNumber, String email, String gender, int age) {
        super(id, username, password, "PATIENT");
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.orders = new ArrayList<>();
    }
    
    /**
     * Constructor for creating a new Patient with minimal information
     * 
     * @param id The unique identifier for this patient
     * @param username The username for this patient
     * @param password The password for this patient
     * @param fullName The full name of this patient
     */
    public Patient(int id, String username, String password, String fullName) {
        this(id, username, password, fullName, "", "", "", "", 0);
    }
    
    /**
     * Get the full name of this patient
     * 
     * @return The full name
     */
    public String getFullName() {
        return fullName;
    }
    
    /**
     * Set the full name of this patient
     * 
     * @param fullName The full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
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
     * Get the email address of this patient
     * 
     * @return The email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Set the email address of this patient
     * 
     * @param email The email address
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
     * Get the age of this patient
     * 
     * @return The age
     */
    public int getAge() {
        return age;
    }
    
    /**
     * Set the age of this patient
     * 
     * @param age The age
     */
    public void setAge(int age) {
        this.age = age;
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
     * Get all orders for this patient
     * 
     * @return The list of orders
     */
    public List<Order> getOrders() {
        return orders;
    }
    
    /**
     * Add an order to this patient
     * 
     * @param order The order to add
     */
    public void addOrder(Order order) {
        this.orders.add(order);
    }
    
    /**
     * Remove an order from this patient
     * 
     * @param order The order to remove
     * @return true if the order was removed, false if it wasn't found
     */
    public boolean removeOrder(Order order) {
        return this.orders.remove(order);
    }
    
    /**
     * Get a formatted string representation of this patient
     * 
     * @return The formatted string
     */
    @Override
    public String toString() {
        return String.format("Patient [ID: %d, Username: %s, Full Name: %s, Phone: %s, Email: %s]", 
                getId(), getUsername(), fullName, phoneNumber, email);
    }
}