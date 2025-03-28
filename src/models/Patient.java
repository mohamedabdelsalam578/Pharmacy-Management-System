
package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Patient - Represents a pharmacy patient with orders and prescriptions
 * 
 * This class extends the User class and adds patient-specific attributes and behaviors,
 * demonstrating inheritance and specialized functionality.
 * 
 * OOP Concepts Demonstrated:
 * - Inheritance: Patient inherits from User
 * - Encapsulation: Private fields with public getters/setters
 * - Composition: Patient owns Orders, Prescriptions, and a Wallet
 * 
 * Class Responsibilities:
 * - Manages patient orders and prescriptions
 * - Handles patient consultations
 * - Processes payments through wallet
 */
public class Patient extends User {
    private String address;
    private List<Order> orders;
    private List<Prescription> prescriptions;
    private List<Consultation> consultations;
    private boolean isActive;
    private Wallet wallet; // Added wallet for payment functionality

    public Patient(int id, String name, String username, String password, 
                  String email, String phoneNumber, String address) {
        super(id, name, username, password, email, phoneNumber);
        this.address = address;
        this.orders = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        this.consultations = new ArrayList<>();
        this.isActive = true;
        this.wallet = new Wallet(this); // Composition - pass this Patient instance to Wallet
    }

    // Getters
    public String getAddress() { return address; }
    public List<Order> getOrders() { return orders; }
    public List<Prescription> getPrescriptions() { return prescriptions; }
    public List<Consultation> getConsultations() { return consultations; }
    public boolean isActive() { return isActive; }
    public Wallet getWallet() { return wallet; } // Add getter for wallet

    // Setters
    public void setAddress(String address) { this.address = address; }
    public void setActive(boolean active) { this.isActive = active; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; } // Add setter for wallet

    // Business methods
    public void addOrder(Order order) { orders.add(order); }
    public void addPrescription(Prescription prescription) { prescriptions.add(prescription); }
    public void addConsultation(Consultation consultation) { consultations.add(consultation); }
    public void removeOrder(Order order) { orders.remove(order); }
    public void removePrescription(Prescription prescription) { prescriptions.remove(prescription); }
    public void removeConsultation(Consultation consultation) { consultations.remove(consultation); }
    
    /**
     * Find a prescription by its ID
     * 
     * @param prescriptionId The ID of the prescription to find
     * @return The prescription with the given ID, or null if not found
     */
    public Prescription findPrescriptionById(int prescriptionId) {
        return prescriptions.stream()
                .filter(p -> p.getId() == prescriptionId)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Place an order using a prescription
     * 
     * @param prescription The prescription to use for the order
     * @param orderId The ID to assign to the new order
     * @return The created order, or null if the prescription is not valid
     */
    public Order placeOrderWithPrescription(Prescription prescription, int orderId) {
        // Verify the prescription is valid, not filled, and not expired
        if (prescription == null || prescription.getStatus().equals("Filled") || prescription.isExpired()) {
            return null;
        }
        
        // Create a new order for the prescription
        Order order = new Order(orderId, getId());
        
        // Add medicines from the prescription to the order
        for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            order.addMedicine(medicine, quantity);
        }
        
        // Update prescription status
        prescription.setStatus("Filled");
        
        // Add the order to the patient's orders
        addOrder(order);
        
        // Print receipt for the prescription order
        System.out.println("Prescription order placed successfully. Order ID: " + order.getId());
        order.printReceipt(getName());
        
        return order;
    }

    @Override
    public void displayInfo() {
        System.out.println("\n👤 ===== PATIENT INFORMATION ===== 👤");
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Username: " + getUsername());
        System.out.println("Email: " + getEmail());
        System.out.println("Phone Number: " + getPhoneNumber());
        System.out.println("Address: " + address);
        System.out.println("Number of Orders: " + orders.size());
        System.out.println("Number of Prescriptions: " + prescriptions.size());
        System.out.println("Number of Consultations: " + consultations.size());
        System.out.println("Status: " + (isActive ? "Active" : "Inactive"));
        System.out.println("Wallet Balance: " + String.format("%.2f LE", wallet.getBalance()));
        System.out.println("Total Transactions: " + wallet.getTransactions().size());
    }
}
