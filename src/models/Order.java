package models;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDate;


/**
 * 🛒 Order - Represents a patient purchase of medicines 🛒
 * 
 * This class manages the details of a patient's medicine order,
 * including what medicines were ordered, quantities, prices,
 * and the order's fulfillment status.
 * 
 * 📚 Class Responsibilities:
 * - Track medicines in an order and their quantities
 * - Calculate total order amount
 * - Manage order status (Pending, Completed, Cancelled)
 * - Generate receipts
 * - Process payments through patient wallet
 */
public class Order {
    private int id;
    private int patientId;
    private Map<Medicine, Integer> medicines;
    private double totalAmount;
    private Date orderDate;
    private String status;
    private String paymentMethod; // "WALLET", "CREDIT_CARD", "CASH"
    private String paymentStatus; // "PENDING", "PAID", "REFUNDED"
    
    // Inner class for order items
    public static class OrderItem {
        private Medicine medicine;
        private int quantity;
        
        public OrderItem(Medicine medicine, int quantity) {
            this.medicine = medicine;
            this.quantity = quantity;
        }
        
        public Medicine getMedicine() { return medicine; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    /**
     * 🏗️ Constructor - Creates a new basic order with default values
     * 
     * This constructor initializes an order with the minimum required fields
     * (id and patientId) and sets all other fields to sensible default values.
     * It's the primary constructor used for creating new orders in the system.
     * 
     * 💡 Default Initializations:
     * - Empty medicines map (no items added yet)
     * - Total amount set to 0.0 LE
     * - Order date set to current time
     * - Status set to "Pending"
     * - Payment method defaulted to "CASH"
     * - Payment status set to "PENDING"
     * 
     * This represents a newly created order that is ready to have
     * medicines added before being finalized and processed.
     * 
     * @param id Unique identifier for this order
     * @param patientId ID of the patient placing the order
     */
    public Order(int id, int patientId) {
        this.id = id;
        this.patientId = patientId;
        this.medicines = new HashMap<>();
        this.totalAmount = 0.0;
        this.orderDate = new Date();
        this.status = "Pending";
        this.paymentMethod = "CASH"; // Default payment method
        this.paymentStatus = "PENDING"; // Default payment status
    }
    
    // Additional constructor with LocalDate and status
    public Order(int id, int patientId, LocalDate orderDate, String status) {
        this.id = id;
        this.patientId = patientId;
        this.medicines = new HashMap<>();
        this.totalAmount = 0.0;
        // Convert LocalDate to Date
        this.orderDate = java.sql.Date.valueOf(orderDate);
        this.status = status;
        this.paymentMethod = "CASH"; // Default payment method
        this.paymentStatus = "PENDING"; // Default payment status
    }
    
    // Constructor that takes a date string
    public Order(int id, int patientId, String orderDateStr) {
        this.id = id;
        this.patientId = patientId;
        this.medicines = new HashMap<>();
        this.totalAmount = 0.0;
        
        // Parse the date string to create a Date object
        try {
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.orderDate = formatter.parse(orderDateStr);
        } catch (java.text.ParseException e) {
            // If parsing fails, use current date
            System.err.println("Error parsing date: " + e.getMessage());
            this.orderDate = new Date();
        }
        
        this.status = "Pending";
        this.paymentMethod = "CASH"; // Default payment method
        this.paymentStatus = "PENDING"; // Default payment status
    }
    
    // Constructor with payment information
    public Order(int id, int patientId, String paymentMethod, String paymentStatus) {
        this.id = id;
        this.patientId = patientId;
        this.medicines = new HashMap<>();
        this.totalAmount = 0.0;
        this.orderDate = new Date();
        this.status = "Pending";
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public Map<Medicine, Integer> getMedicines() { return medicines; }
    public double getTotalAmount() { return totalAmount; }
    public Date getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }

    public void setStatus(String status) { this.status = status; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    /**
     * 💲 isPaid - Checks if this order has been paid for
     * 
     * This method determines if the order has been successfully paid for
     * by checking the payment status. This is used throughout the system
     * to determine if medicines can be dispensed, if additional payment
     * is needed, or if refunds should be processed.
     * 
     * 💡 Implementation Note:
     * - Uses string comparison with constant "PAID" status
     * - Returns boolean for easy conditional logic
     * - Serves as a convenient abstraction over the status string
     * 
     * This method is used in the checkout flow, order management,
     * and reporting features of the pharmacy system.
     * 
     * @return true if payment has been completed, false otherwise
     */
    public boolean isPaid() {
        return paymentStatus.equals("PAID");
    }
    
    /**
     * 💰 setPaid - Updates the payment status of this order
     * 
     * This method is primarily used when processing payments through
     * external systems or cash payments at the pharmacy counter.
     * It updates both the payment status and the order status accordingly.
     * 
     * 💡 Side Effects:
     * - Changes payment status to "PAID" or "PENDING"
     * - When marking as paid, also updates order status to "Confirmed"
     * - When marking as unpaid, keeps current order status unchanged
     * 
     * This method is especially useful for cash or external payment systems
     * that aren't directly integrated with the wallet system.
     * 
     * @param paid true to mark as paid, false to mark as pending
     */
    public void setPaid(boolean paid) {
        this.paymentStatus = paid ? "PAID" : "PENDING";
        if (paid) {
            this.status = "Confirmed";
        }
    }


    /**
     * ➕ addMedicine - Adds a medicine to this order
     * 
     * This method attempts to add a specific quantity of medicine to the order.
     * It first checks if there is sufficient stock available in the pharmacy
     * before adding it, ensuring we don't promise medicines we can't deliver.
     * 
     * 💡 Inventory Management:
     * - Validates against pharmacy stock levels
     * - Returns status indicating success or failure
     * - Automatically recalculates the total order amount
     * 
     * This inventory check is a critical safeguard to prevent orders that
     * cannot be fulfilled due to insufficient stock.
     * 
     * @param medicine The medicine to add to the order
     * @param quantity The amount of this medicine to order
     * @return true if added successfully, false if insufficient stock
     */
    public boolean addMedicine(Medicine medicine, int quantity) {
        if (medicine.getQuantity() >= quantity) {
            medicines.put(medicine, quantity);
            calculateTotalAmount();
            return true;
        }
        return false;
    }

    /**
     * ➖ removeMedicine - Removes a medicine from this order
     * 
     * This method allows removing a medicine completely from the order,
     * regardless of its quantity. After removal, the total order amount
     * is automatically recalculated.
     * 
     * 💡 Implementation Notes:
     * - Uses Map.containsKey() to verify medicine exists in order
     * - Provides safe operation (no effect if medicine not in order)
     * - Maintains order total consistency by recalculating after removal
     * 
     * This method is essential for order modifications before payment,
     * allowing patients to change their minds about purchases.
     * 
     * @param medicine The medicine to remove from the order
     * @return true if removed successfully, false if not found
     */
    public boolean removeMedicine(Medicine medicine) {
        if (medicines.containsKey(medicine)) {
            medicines.remove(medicine);
            calculateTotalAmount();
            return true;
        }
        return false;
    }

    private void calculateTotalAmount() {
        totalAmount = 0.0;
        for (Map.Entry<Medicine, Integer> entry : medicines.entrySet()) {
            totalAmount += entry.getKey().getPrice() * entry.getValue();
        }
    }
    
    /**
     * 💹 calculateTotal - Computes total cost with Egyptian VAT
     * 
     * This method calculates the final amount to be paid by the patient,
     * including the standard Egyptian Value Added Tax (VAT) rate of 14%.
     * This is the amount that will be charged to the patient's wallet or
     * credit card during payment processing.
     * 
     * 💡 Financial Calculation:
     * - Applies current Egyptian tax rate (14%) to the subtotal
     * - Returns the complete amount including tax
     * - Used for all payment processing and receipt generation
     * 
     * Egyptian tax laws require pharmacies to collect VAT on medication
     * sales, with the current standard rate being 14% as of 2023.
     * 
     * @return The final amount to be paid in Egyptian Pounds (LE), including tax
     */
    public double calculateTotal() {
        return totalAmount * 1.14; // Add 14% tax to the total amount
    }

    public List<Medicine> getMedicinesList() {
        return new ArrayList<>(medicines.keySet());
    }

    public List<Integer> getQuantities() {
        List<Medicine> medicinesList = getMedicinesList();
        List<Integer> quantities = new ArrayList<>();
        for (Medicine medicine : medicinesList) {
            quantities.add(medicines.get(medicine));
        }
        return quantities;
    }
    
    /**
     * 📦 getOrderItems - Provides a structured list of order contents
     * 
     * This method converts the internal medicines map into a list of OrderItem
     * objects, which provide a more convenient way to access medicine and
     * quantity information together. This is particularly useful for UI
     * display, receipt generation, and order processing.
     * 
     * 💡 Data Structure Transformation:
     * - Converts from Map<Medicine, Integer> to List<OrderItem>
     * - Creates new OrderItem objects for each medicine/quantity pair
     * - Provides a more object-oriented view of order contents
     * 
     * This transformation is an example of adapting internal data structures
     * to provide a more convenient interface for other system components.
     * 
     * @return List of OrderItem objects containing medicine and quantity pairs
     */
    public List<OrderItem> getOrderItems() {
        List<OrderItem> items = new ArrayList<>();
        for (Map.Entry<Medicine, Integer> entry : medicines.entrySet()) {
            items.add(new OrderItem(entry.getKey(), entry.getValue()));
        }
        return items;
    }

    public void displayInfo() {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("\n🛒 ===== ORDER INFORMATION ===== 🛒");
        System.out.println("📋 Order ID: " + id);
        System.out.println("👤 Patient ID: " + patientId);
        System.out.println("📅 Order Date: " + formatter.format(orderDate));
        
        // Display status with appropriate emoji
        String statusEmoji = "⏳";
        if (status.equals("Confirmed")) statusEmoji = "✅";
        else if (status.equals("Cancelled")) statusEmoji = "❌";
        else if (status.equals("Delivered")) statusEmoji = "🚚";
        System.out.println(statusEmoji + " Status: " + status);
        
        // Display payment method with appropriate emoji
        String paymentEmoji = "💵";
        if (paymentMethod.equals("WALLET")) paymentEmoji = "💰";
        else if (paymentMethod.equals("CREDIT_CARD")) paymentEmoji = "💳";
        System.out.println(paymentEmoji + " Payment Method: " + paymentMethod);
        
        // Display payment status with appropriate emoji
        String paymentStatusEmoji = "⏳";
        if (paymentStatus.equals("PAID")) paymentStatusEmoji = "✅";
        else if (paymentStatus.equals("REFUNDED")) paymentStatusEmoji = "↩️";
        System.out.println(paymentStatusEmoji + " Payment Status: " + paymentStatus);
        
        System.out.println("\n💊 ----- MEDICINES ----- 💊");
        if (medicines.isEmpty()) {
            System.out.println("No medicines in this order.");
        } else {
            for (Map.Entry<Medicine, Integer> entry : medicines.entrySet()) {
                Medicine medicine = entry.getKey();
                int quantity = entry.getValue();
                double itemTotal = medicine.getPrice() * quantity;
                System.out.println("\t• " + medicine.getName() + " (Qty: " + quantity + ") - " +
                        String.format("%.2f", itemTotal) + " LE");
            }
        }
        
        System.out.println("\n💵 ----- TOTALS ----- 💵");
        System.out.println("Subtotal: " + String.format("%.2f", totalAmount) + " LE");
        System.out.println("Tax (14%): " + String.format("%.2f", totalAmount * 0.14) + " LE");
        System.out.println("-".repeat(30));
        System.out.println("Total Amount: " + String.format("%.2f", totalAmount * 1.14) + " LE");
    }
    
    /**
     * Process payment for the order using patient's wallet
     * 
     * @param patient The patient who is paying for the order
     * @return true if payment was successful, false otherwise
     */
    public boolean processPaymentFromWallet(Patient patient) {
        // Get the patient's wallet
        Wallet wallet = patient.getWallet();
        
        // Calculate the total amount including tax
        double totalWithTax = calculateTotal();
        
        // Attempt to make the payment from the wallet
        boolean paymentSuccess = wallet.makePayment(totalWithTax, id);
        
        if (paymentSuccess) {
            // Update the order payment information
            this.paymentMethod = "WALLET";
            this.paymentStatus = "PAID";
            this.status = "Confirmed";
            
            System.out.println("\n💰 Payment processed successfully!");
            System.out.println("Amount: " + String.format("%.2f", totalWithTax) + " LE");
            System.out.println("Method: Wallet");
            System.out.println("New Wallet Balance: " + String.format("%.2f", wallet.getBalance()) + " LE");
            
            return true;
        } else {
            System.out.println("\n❌ Payment failed!");
            System.out.println("Insufficient funds in wallet.");
            System.out.println("Current wallet balance: " + String.format("%.2f", wallet.getBalance()) + " LE");
            System.out.println("Required amount: " + String.format("%.2f", totalWithTax) + " LE");
            
            return false;
        }
    }
    
    /**
     * Process payment for the order using credit card
     * 
     * @param patient The patient who is paying for the order
     * @param cardNumber Credit card number
     * @return true if payment was successful, false otherwise
     */
    public boolean processPaymentWithCard(Patient patient, String cardNumber) {
        // Get the patient's wallet
        Wallet wallet = patient.getWallet();
        
        // Calculate the total amount including tax
        double totalWithTax = calculateTotal();
        
        // In a real system, we would integrate with a payment gateway
        // For now, we'll simulate a credit card payment by:
        // 1. Adding the amount to the wallet (simulating funds from card)
        // 2. Then making a payment from the wallet
        
        // First deposit the amount into wallet (simulating card transfer)
        if (!wallet.deposit(totalWithTax, "Credit card deposit - Last digits: " + cardNumber.substring(cardNumber.length() - 4))) {
            System.out.println("\n❌ Card payment processing failed!");
            return false;
        }
        
        // Then make payment from wallet
        boolean paymentSuccess = wallet.makePayment(totalWithTax, id);
        
        if (paymentSuccess) {
            // Update the order payment information
            this.paymentMethod = "CREDIT_CARD";
            this.paymentStatus = "PAID";
            this.status = "Confirmed";
            
            System.out.println("\n💳 Card payment processed successfully!");
            System.out.println("Card ending in: " + cardNumber.substring(cardNumber.length() - 4));
            System.out.println("Amount: " + String.format("%.2f", totalWithTax) + " LE");
            
            return true;
        } else {
            System.out.println("\n❌ Payment failed during processing!");
            return false;
        }
    }
    
    /**
     * Prints a formatted receipt for the order with detailed information
     * 
     * @param patientName the name of the patient who placed the order
     */
    public void printReceipt(String patientName) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // Print header with border
        System.out.println("\n" + "=".repeat(50));
        System.out.println(" ".repeat(15) + "ORDER RECEIPT" + " ".repeat(15));
        System.out.println("=".repeat(50));
        
        // Print receipt basic information
        System.out.println("🏥 Elt3ban Pharmacy");
        System.out.println("📅 Date: " + formatter.format(orderDate));
        System.out.println("🧾 Receipt #: " + id);
        System.out.println("-".repeat(50));
        
        // Print patient information
        System.out.println("👤 Patient: " + patientName);
        System.out.println("🆔 Patient ID: " + patientId);
        System.out.println("-".repeat(50));
        
        // Print table header
        System.out.printf("%-30s %-6s %-10s %-10s%n", "Medicine", "Qty", "Price(LE)", "Total(LE)");
        System.out.println("-".repeat(50));
        
        // Print medicines with their prices and quantities
        for (Map.Entry<Medicine, Integer> entry : medicines.entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            double price = medicine.getPrice();
            double itemTotal = price * quantity;
            
            System.out.printf("%-30s %-6d %-10.2f %-10.2f%n", 
                    medicine.getName(), quantity, price, itemTotal);
        }
        
        // Print footer with totals
        System.out.println("-".repeat(50));
        System.out.printf("%-37s %-12.2f%n", "Subtotal:", totalAmount);
        System.out.printf("%-37s %-12.2f%n", "Tax (14%):", totalAmount * 0.14);
        System.out.println("-".repeat(50));
        System.out.printf("%-37s %-12.2f%n", "TOTAL:", totalAmount * 1.14);
        
        // Print payment information if available
        if (paymentStatus.equals("PAID")) {
            System.out.println("-".repeat(50));
            String paymentIcon = paymentMethod.equals("WALLET") ? "💰" : "💳";
            System.out.println(paymentIcon + " Paid via: " + paymentMethod);
            System.out.println("Payment Status: " + paymentStatus);
        }
        
        System.out.println("=".repeat(50));
        
        // Print a thank you message and contact information
        System.out.println("Thank you for your purchase!");
        System.out.println("For inquiries, call: 16999");
        System.out.println("Visit us online: www.elt3banpharmacy.com");
        System.out.println("=".repeat(50) + "\n");
    }
}