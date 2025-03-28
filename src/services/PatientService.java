package services;

import models.Patient;
import models.Medicine;
import models.Order;
import models.Wallet;
import models.CreditCard;

import java.util.List;
import java.util.Map;

/**
 * 👨‍👩‍👧 PatientService - Provides methods for patient operations in the pharmacy 👨‍👩‍👧
 * 
 * This service class handles all patient-related operations including:
 * - Account management
 * - Order placement and tracking
 * - Wallet management and payments
 * - Patient information updates
 */
public class PatientService {
    private List<Patient> patients;
    private List<Order> orders;
    private List<Medicine> medicines;
    private int nextOrderId;

    /**
     * Constructor to initialize PatientService
     * 
     * @param patients List of patients
     * @param orders List of orders
     * @param medicines List of medicines
     * @param nextOrderId Next available order ID
     */
    public PatientService(List<Patient> patients, List<Order> orders, List<Medicine> medicines, int nextOrderId) {
        this.patients = patients;
        this.orders = orders;
        this.medicines = medicines;
        this.nextOrderId = nextOrderId;
    }

    /**
     * Create a new patient account
     * 
     * @param patient The new patient to add
     * @return true if patient was added successfully, false if the patient with the same ID already exists
     */
    public boolean createAccount(Patient patient) {
        // Check if patient with the same ID already exists
        if (patients.stream().anyMatch(p -> p.getId() == patient.getId())) {
            System.out.println("Patient with ID " + patient.getId() + " already exists.");
            return false;
        }
        
        patients.add(patient);
        System.out.println("Patient account created successfully for: " + patient.getName());
        return true;
    }

    /**
     * Update patient account information
     * 
     * @param patientId The ID of the patient to update
     * @param name Updated name
     * @param email Updated email
     * @param phoneNumber Updated phone number
     * @param address Updated address
     * @return true if patient was updated successfully, false if the patient doesn't exist
     */
    public boolean updateAccount(int patientId, String name, String email, String phoneNumber, String address) {
        Patient patientToUpdate = findPatientById(patientId);
        
        if (patientToUpdate == null) {
            System.out.println("Patient with ID " + patientId + " not found.");
            return false;
        }
        
        // Simple validation
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return false;
        }
        
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Email cannot be empty.");
            return false;
        }
        
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            System.out.println("Phone number cannot be empty.");
            return false;
        }
        
        // Update patient information
        patientToUpdate.setName(name);
        patientToUpdate.setEmail(email);
        patientToUpdate.setPhoneNumber(phoneNumber);
        patientToUpdate.setAddress(address);
        
        System.out.println("Patient account updated successfully for: " + patientToUpdate.getName());
        return true;
    }

    /**
     * Place a new order for a patient
     * 
     * @param patientId The ID of the patient placing the order
     * @param medicineQuantities Map of medicine IDs to quantities
     * @return The created order if successful, null otherwise
     */
    public Order placeOrder(int patientId, Map<Integer, Integer> medicineQuantities) {
        Patient patient = findPatientById(patientId);
        
        if (patient == null) {
            System.out.println("Patient with ID " + patientId + " not found.");
            return null;
        }
        
        Order newOrder = new Order(nextOrderId++, patientId);
        
        // Add medicines to the order
        boolean allMedicinesAvailable = true;
        for (Map.Entry<Integer, Integer> entry : medicineQuantities.entrySet()) {
            int medicineId = entry.getKey();
            int quantity = entry.getValue();
            
            Medicine medicine = findMedicineById(medicineId);
            
            if (medicine == null) {
                System.out.println("Medicine with ID " + medicineId + " not found.");
                allMedicinesAvailable = false;
                break;
            }
            
            if (medicine.getQuantity() < quantity) {
                System.out.println("Insufficient stock for medicine: " + medicine.getName() + 
                                   ". Available: " + medicine.getQuantity() + ", Requested: " + quantity);
                allMedicinesAvailable = false;
                break;
            }
            
            // Add medicine to the order
            newOrder.addMedicine(medicine, quantity);
            
            // Update stock
            medicine.updateStock(quantity);
        }
        
        if (!allMedicinesAvailable) {
            // Revert any stock changes if order fails
            for (Map.Entry<Medicine, Integer> entry : newOrder.getMedicines().entrySet()) {
                Medicine medicine = entry.getKey();
                int quantity = entry.getValue();
                medicine.restoreStock(quantity);
            }
            return null;
        }
        
        // Add order to the list of orders
        orders.add(newOrder);
        
        // Add order to the patient's list of orders
        patient.addOrder(newOrder);
        
        System.out.println("Order placed successfully. Order ID: " + newOrder.getId());
        
        // Print receipt for the order
        newOrder.printReceipt(patient.getName());
        
        return newOrder;
    }

    /**
     * Cancel an existing order
     * 
     * @param patientId The ID of the patient canceling the order
     * @param orderId The ID of the order to cancel
     * @return true if order was canceled successfully, false otherwise
     */
    public boolean cancelOrder(int patientId, int orderId) {
        Patient patient = findPatientById(patientId);
        
        if (patient == null) {
            System.out.println("Patient with ID " + patientId + " not found.");
            return false;
        }
        
        Order orderToCancel = findOrderById(orderId);
        
        if (orderToCancel == null) {
            System.out.println("Order with ID " + orderId + " not found.");
            return false;
        }
        
        if (orderToCancel.getPatientId() != patientId) {
            System.out.println("Order does not belong to this patient.");
            return false;
        }
        
        if (orderToCancel.getStatus() == Order.Status.CANCELLED) {
            System.out.println("Order is already canceled.");
            return false;
        }
        
        if (orderToCancel.getStatus() == Order.Status.COMPLETED) {
            System.out.println("Cannot cancel a completed order.");
            return false;
        }
        
        // Update order status
        orderToCancel.setStatus(Order.Status.CANCELLED);
        
        // Restore medicine stock
        for (Map.Entry<Medicine, Integer> entry : orderToCancel.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            medicine.restoreStock(quantity);
        }
        
        System.out.println("Order canceled successfully. Order ID: " + orderId);
        return true;
    }

    /**
     * Update an existing order
     * 
     * @param patientId The ID of the patient updating the order
     * @param orderId The ID of the order to update
     * @param medicineQuantities Map of medicine IDs to quantities
     * @return true if order was updated successfully, false otherwise
     */
    public boolean updateOrder(int patientId, int orderId, Map<Integer, Integer> medicineQuantities) {
        // Cancel the existing order
        if (!cancelOrder(patientId, orderId)) {
            return false;
        }
        
        // Place a new order
        Order newOrder = placeOrder(patientId, medicineQuantities);
        
        if (newOrder == null) {
            System.out.println("Failed to update order.");
            return false;
        }
        
        System.out.println("Order updated successfully. New Order ID: " + newOrder.getId());
        return true;
    }

    /**
     * Display all orders for a patient
     * 
     * @param patientId The ID of the patient
     */
    public void viewOrders(int patientId) {
        Patient patient = findPatientById(patientId);
        
        if (patient == null) {
            System.out.println("Patient with ID " + patientId + " not found.");
            return;
        }
        
        List<Order> patientOrders = patient.getOrders();
        
        if (patientOrders.isEmpty()) {
            System.out.println("No orders found for patient: " + patient.getName());
            return;
        }
        
        System.out.println("\n🛒 ===== ORDERS FOR PATIENT: " + patient.getName() + " ===== 🛒");
        
        for (Order order : patientOrders) {
            order.displayInfo();
            
            // If the order is not paid, ask if user wants to pay for it now
            if (!order.isPaid()) {
                System.out.print("\nWould you like to pay for this order now? (y/n): ");
                java.util.Scanner scanner = new java.util.Scanner(System.in);
                String response = scanner.nextLine().trim().toLowerCase();
                
                if (response.equals("y") || response.equals("yes")) {
                    // Display payment options
                    System.out.println("\n💲 ===== PAYMENT OPTIONS ===== 💲");
                    System.out.println("1. 💰 Pay from Wallet (Current Balance: " + 
                            String.format("%.2f LE", patient.getWallet().getBalance()) + ")");
                    System.out.println("2. 💳 Pay with Credit Card");
                    System.out.println("3. ❌ Cancel Payment");
                    System.out.print("Enter your choice: ");
                    
                    int choice = 0;
                    try {
                        choice = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Payment canceled.");
                        continue;
                    }
                    
                    boolean paymentSuccess = false;
                    
                    switch (choice) {
                        case 1:
                            paymentSuccess = order.processPaymentFromWallet(patient);
                            break;
                        case 2:
                            System.out.print("Enter Credit Card Number: ");
                            String cardNumber = scanner.nextLine().trim();
                            
                            // Simple validation for card number
                            if (cardNumber.length() < 12 || !cardNumber.matches("\\d+")) {
                                System.out.println("Invalid card number. Payment canceled.");
                                break;
                            }
                            
                            // Display masked card number for security
                            String lastFour = cardNumber.substring(cardNumber.length() - 4);
                            String masked = "*".repeat(cardNumber.length() - 4) + lastFour;
                            System.out.println("Processing payment with card: " + masked);
                            
                            paymentSuccess = order.processPaymentWithCard(patient, cardNumber);
                            break;
                        case 3:
                            System.out.println("Payment canceled.");
                            break;
                        default:
                            System.out.println("Invalid choice. Payment canceled.");
                            break;
                    }
                    
                    if (paymentSuccess) {
                        System.out.println("Payment processed successfully!");
                    }
                }
            }
            
            System.out.println("----------------------------------------------------------------");
        }
    }

    /**
     * Find a patient by ID
     * 
     * @param patientId The ID of the patient to find
     * @return The patient object if found, null otherwise
     */
    private Patient findPatientById(int patientId) {
        return patients.stream()
                      .filter(p -> p.getId() == patientId)
                      .findFirst()
                      .orElse(null);
    }

    /**
     * Find a medicine by ID
     * 
     * @param medicineId The ID of the medicine to find
     * @return The medicine object if found, null otherwise
     */
    private Medicine findMedicineById(int medicineId) {
        return medicines.stream()
                        .filter(m -> m.getId() == medicineId)
                        .findFirst()
                        .orElse(null);
    }

    /**
     * Find an order by ID
     * 
     * @param orderId The ID of the order to find
     * @return The order object if found, null otherwise
     */
    private Order findOrderById(int orderId) {
        return orders.stream()
                     .filter(o -> o.getId() == orderId)
                     .findFirst()
                     .orElse(null);
    }
    
    // Variables for wallet transaction management
    private int nextTransactionId = 1000;
    
    /**
     * Add funds to a patient's wallet
     * 
     * @param patientId The ID of the patient
     * @param amount The amount to add
     * @param source The source of the funds (e.g., "Credit Card", "Bank Transfer")
     * @return true if funds were added successfully, false otherwise
     */
    public boolean addFundsToWallet(int patientId, double amount, String source) {
        Patient patient = findPatientById(patientId);
        
        if (patient == null) {
            System.out.println("Patient with ID " + patientId + " not found.");
            return false;
        }
        
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return false;
        }
        
        // Get patient's wallet
        Wallet wallet = patient.getWallet();
        
        // Add funds to wallet
        boolean added = wallet.deposit(amount, source);
        
        if (added) {
            System.out.println("\n💰 Funds added successfully!");
            System.out.println("Amount: " + String.format("%.2f LE", amount));
            System.out.println("New Balance: " + String.format("%.2f LE", wallet.getBalance()));
            return true;
        } else {
            System.out.println("Failed to add funds to wallet.");
            return false;
        }
    }
    
    /**
     * Process a payment for an order using the patient's wallet
     * 
     * @param patientId The ID of the patient
     * @param orderId The ID of the order to pay for
     * @return true if payment was successful, false otherwise
     */
    public boolean payOrderFromWallet(int patientId, int orderId) {
        Patient patient = findPatientById(patientId);
        
        if (patient == null) {
            System.out.println("Patient with ID " + patientId + " not found.");
            return false;
        }
        
        Order order = findOrderById(orderId);
        
        if (order == null) {
            System.out.println("Order with ID " + orderId + " not found.");
            return false;
        }
        
        if (order.getPatientId() != patientId) {
            System.out.println("Order does not belong to this patient.");
            return false;
        }
        
        if (order.isPaid()) {
            System.out.println("Order is already paid.");
            return false;
        }
        
        // Process payment from wallet
        return order.processPaymentFromWallet(patient);
    }
    
    /**
     * Process a payment for an order using a credit card
     * 
     * @param patientId The ID of the patient
     * @param orderId The ID of the order to pay for
     * @param cardNumber The credit card number
     * @return true if payment was successful, false otherwise
     */
    public boolean payOrderWithCard(int patientId, int orderId, String cardNumber) {
        Patient patient = findPatientById(patientId);
        
        if (patient == null) {
            System.out.println("Patient with ID " + patientId + " not found.");
            return false;
        }
        
        Order order = findOrderById(orderId);
        
        if (order == null) {
            System.out.println("Order with ID " + orderId + " not found.");
            return false;
        }
        
        if (order.getPatientId() != patientId) {
            System.out.println("Order does not belong to this patient.");
            return false;
        }
        
        if (order.isPaid()) {
            System.out.println("Order is already paid.");
            return false;
        }
        
        // Process payment with card
        return order.processPaymentWithCard(patient, cardNumber);
    }
    
    /**
     * Add a credit card to a patient's wallet
     * 
     * @param patientId The ID of the patient
     * @param cardNumber The credit card number
     * @param cardHolderName The card holder name
     * @param expiryDate The card expiry date (MM/YY)
     * @param cardType The card type (e.g., Visa, Mastercard)
     * @return true if card was added successfully, false otherwise
     */
    public boolean addCardToWallet(int patientId, String cardNumber, String cardHolderName, String expiryDate, String cardType) {
        Patient patient = findPatientById(patientId);
        
        if (patient == null) {
            System.out.println("Patient with ID " + patientId + " not found.");
            return false;
        }
        
        // Simple validation for card number
        if (cardNumber.length() < 12 || !cardNumber.matches("\\d+")) {
            System.out.println("Invalid card number format.");
            return false;
        }
        
        // Simple validation for card holder name
        if (cardHolderName == null || cardHolderName.trim().length() < 2) {
            System.out.println("Invalid card holder name.");
            return false;
        }
        
        // Get patient's wallet
        Wallet wallet = patient.getWallet();
        
        // Display masked card number for security
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        String masked = "*".repeat(cardNumber.length() - 4) + lastFour;
        System.out.println("Adding card: " + masked);
        
        // Add card to wallet
        boolean added = wallet.addCard(cardNumber, cardHolderName, expiryDate, cardType);
        
        if (added) {
            System.out.println("\n💳 Card added successfully!");
            return true;
        } else {
            System.out.println("Failed to add card to wallet. Card may already exist.");
            return false;
        }
    }
    
    /**
     * Remove a credit card from a patient's wallet
     * 
     * @param patientId The ID of the patient
     * @param lastFourDigits The last 4 digits of the card to remove
     * @return true if card was removed successfully, false otherwise
     */
    public boolean removeCardFromWallet(int patientId, String lastFourDigits) {
        Patient patient = findPatientById(patientId);
        
        if (patient == null) {
            System.out.println("Patient with ID " + patientId + " not found.");
            return false;
        }
        
        // Get patient's wallet
        Wallet wallet = patient.getWallet();
        
        // Remove card from wallet
        boolean removed = wallet.removeCard(lastFourDigits);
        
        if (removed) {
            System.out.println("\n💳 Card removed successfully!");
            return true;
        } else {
            System.out.println("Failed to remove card from wallet. Card not found.");
            return false;
        }
    }
    
    /**
     * View wallet information for a patient
     * 
     * @param patientId The ID of the patient
     */
    public void viewWallet(int patientId) {
        Patient patient = findPatientById(patientId);
        
        if (patient == null) {
            System.out.println("Patient with ID " + patientId + " not found.");
            return;
        }
        
        // Get patient's wallet and display info
        Wallet wallet = patient.getWallet();
        wallet.displayInfo();
    }
}