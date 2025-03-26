package services;

import models.Admin;
import models.Medicine;
import models.Order;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AdminService provides methods for admin operations in the pharmacy
 */
public class AdminService {
    private List<Medicine> medicines;
    private List<Order> orders;

    /**
     * Constructor to initialize AdminService
     * 
     * @param medicines List of medicines in the pharmacy
     * @param orders List of orders placed by patients
     */
    public AdminService(List<Medicine> medicines, List<Order> orders) {
        this.medicines = medicines;
        this.orders = orders;
    }

    /**
     * Add a new medicine to the pharmacy
     * 
     * @param medicine The medicine to add
     * @return true if medicine was added successfully, false if it already exists
     */
    public boolean addMedicine(Medicine medicine) {
        // Check if medicine with the same ID already exists
        if (medicines.stream().anyMatch(m -> m.getId() == medicine.getId())) {
            System.out.println("Medicine with ID " + medicine.getId() + " already exists.");
            return false;
        }
        
        medicines.add(medicine);
        System.out.println("Medicine added successfully: " + medicine.getName());
        return true;
    }

    /**
     * Remove a medicine from the pharmacy
     * 
     * @param medicineId The ID of the medicine to remove
     * @return true if medicine was removed successfully, false if it doesn't exist
     */
    public boolean removeMedicine(int medicineId) {
        Medicine medicineToRemove = findMedicineById(medicineId);
        
        if (medicineToRemove == null) {
            System.out.println("Medicine with ID " + medicineId + " not found.");
            return false;
        }
        
        medicines.remove(medicineToRemove);
        System.out.println("Medicine removed successfully: " + medicineToRemove.getName());
        return true;
    }

    /**
     * Update the details of a medicine
     * 
     * @param medicineId The ID of the medicine to update
     * @param name Updated name
     * @param description Updated description
     * @param manufacturer Updated manufacturer
     * @param price Updated price
     * @param quantity Updated quantity
     * @param category Updated category
     * @param requiresPrescription Updated prescription requirement
     * @return true if medicine was updated successfully, false if it doesn't exist
     */
    public boolean updateMedicine(int medicineId, String name, String description, String manufacturer, 
                                 double price, int quantity, String category, boolean requiresPrescription) {
        Medicine medicineToUpdate = findMedicineById(medicineId);
        
        if (medicineToUpdate == null) {
            System.out.println("Medicine with ID " + medicineId + " not found.");
            return false;
        }
        
        medicineToUpdate.setName(name);
        medicineToUpdate.setDescription(description);
        medicineToUpdate.setManufacturer(manufacturer);
        medicineToUpdate.setPrice(price);
        medicineToUpdate.setQuantity(quantity);
        medicineToUpdate.setCategory(category);
        medicineToUpdate.setRequiresPrescription(requiresPrescription);
        
        System.out.println("Medicine updated successfully: " + medicineToUpdate.getName());
        return true;
    }

    /**
     * Generate a report for all medicines in the pharmacy
     */
    public void generateMedicineReport() {
        System.out.println("\n===== MEDICINE INVENTORY REPORT =====");
        System.out.println("Total number of medicines: " + medicines.size());
        
        double totalValue = 0.0;
        
        System.out.println("\nMedicine Details:");
        System.out.println("----------------------------------------------------------------");
        System.out.printf("%-5s %-20s %-10s %-10s %-15s\n", "ID", "Name", "Price", "Quantity", "Total Value");
        System.out.println("----------------------------------------------------------------");
        
        for (Medicine medicine : medicines) {
            double medicineValue = medicine.getPrice() * medicine.getQuantity();
            totalValue += medicineValue;
            
            System.out.printf("%-5d %-20s %-9.2f LE %-10d %-14.2f LE\n", 
                medicine.getId(), 
                medicine.getName(), 
                medicine.getPrice(), 
                medicine.getQuantity(),
                medicineValue);
        }
        
        System.out.println("----------------------------------------------------------------");
        System.out.printf("Total Inventory Value: %.2f LE\n", totalValue);
        System.out.println("================================================================");
    }

    /**
     * Generate a report for a specific medicine
     * 
     * @param medicineId The ID of the medicine to generate a report for
     */
    public void generateMedicineReport(int medicineId) {
        Medicine medicine = findMedicineById(medicineId);
        
        if (medicine == null) {
            System.out.println("Medicine with ID " + medicineId + " not found.");
            return;
        }
        
        System.out.println("\n===== SPECIFIC MEDICINE REPORT =====");
        System.out.println("Medicine ID: " + medicine.getId());
        System.out.println("Name: " + medicine.getName());
        System.out.println("Description: " + medicine.getDescription());
        System.out.println("Manufacturer: " + medicine.getManufacturer());
        System.out.println("Category: " + medicine.getCategory());
        System.out.println("Requires Prescription: " + (medicine.isRequiresPrescription() ? "Yes" : "No"));
        System.out.println("Price: " + String.format("%.2f", medicine.getPrice()) + " LE");
        System.out.println("Current Stock: " + medicine.getQuantity());
        System.out.println("Total Stock Value: " + String.format("%.2f", medicine.getPrice() * medicine.getQuantity()) + " LE");
        
        // Calculate sales information
        int totalSold = 0;
        double totalRevenue = 0.0;
        
        for (Order order : orders) {
            if (order.getStatus().equals("Completed")) {
                for (Map.Entry<Medicine, Integer> entry : order.getMedicines().entrySet()) {
                    if (entry.getKey().getId() == medicine.getId()) {
                        int quantity = entry.getValue();
                        totalSold += quantity;
                        totalRevenue += quantity * medicine.getPrice();
                    }
                }
            }
        }
        
        System.out.println("\nSales Information:");
        System.out.println("Total Units Sold: " + totalSold);
        System.out.println("Total Revenue: " + String.format("%.2f", totalRevenue) + " LE");
        System.out.println("================================================================");
    }

    /**
     * Generate a revenue report for the pharmacy
     */
    public void generateRevenueReport() {
        System.out.println("\n===== PHARMACY REVENUE REPORT =====");
        
        double totalRevenue = 0.0;
        int totalOrdersCompleted = 0;
        
        for (Order order : orders) {
            if (order.getStatus().equals("Completed")) {
                totalRevenue += order.getTotalAmount();
                totalOrdersCompleted++;
            }
        }
        
        System.out.println("Total Completed Orders: " + totalOrdersCompleted);
        System.out.println("Total Revenue: " + String.format("%.2f", totalRevenue) + " LE");
        
        // Revenue by medicine category
        Map<String, Double> revenueByCategory = calculateRevenueByCategory();
        
        System.out.println("\nRevenue by Medicine Category:");
        System.out.println("----------------------------------------------------------------");
        System.out.printf("%-20s %-15s\n", "Category", "Revenue");
        System.out.println("----------------------------------------------------------------");
        
        for (Map.Entry<String, Double> entry : revenueByCategory.entrySet()) {
            System.out.printf("%-20s %-15.2f LE\n", entry.getKey(), entry.getValue());
        }
        
        System.out.println("================================================================");
    }

    /**
     * Calculate revenue by medicine category
     * 
     * @return Map of category names to revenue amounts
     */
    private Map<String, Double> calculateRevenueByCategory() {
        Map<String, Double> revenueByCategory = new java.util.HashMap<>();
        
        for (Order order : orders) {
            if (order.getStatus().equals("Completed")) {
                for (Map.Entry<Medicine, Integer> entry : order.getMedicines().entrySet()) {
                    Medicine medicine = entry.getKey();
                    int quantity = entry.getValue();
                    double revenue = medicine.getPrice() * quantity;
                    
                    String category = medicine.getCategory();
                    revenueByCategory.put(category, revenueByCategory.getOrDefault(category, 0.0) + revenue);
                }
            }
        }
        
        return revenueByCategory;
    }

    /**
     * Find a medicine by its ID
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
     * Generate an inventory report with detailed analytics
     */
    public void generateInventoryReport() {
        System.out.println("\n===== INVENTORY REPORT =====");
        System.out.println("Total medicines: " + medicines.size());
        
        // Calculate total inventory value
        double totalValue = medicines.stream()
                          .mapToDouble(m -> m.getPrice() * m.getQuantity())
                          .sum();
        
        System.out.println("Total inventory value: " + String.format("%.2f", totalValue) + " LE");
        
        // Group medicines by category
        Map<String, List<Medicine>> medicinesByCategory = new HashMap<>();
        for (Medicine medicine : medicines) {
            medicinesByCategory.computeIfAbsent(medicine.getCategory(), k -> new ArrayList<>())
                             .add(medicine);
        }
        
        // Display medicines by category
        System.out.println("\nInventory by Category:");
        for (Map.Entry<String, List<Medicine>> entry : medicinesByCategory.entrySet()) {
            String category = entry.getKey();
            List<Medicine> categoryMedicines = entry.getValue();
            
            // Calculate category total value
            double categoryValue = categoryMedicines.stream()
                                .mapToDouble(m -> m.getPrice() * m.getQuantity())
                                .sum();
            
            System.out.println("----- " + category + " -----");
            System.out.println("Total medicines: " + categoryMedicines.size());
            System.out.println("Category value: " + String.format("%.2f", categoryValue) + " LE");
            
            // Display low stock items
            List<Medicine> lowStock = categoryMedicines.stream()
                                   .filter(m -> m.getQuantity() < 10)
                                   .collect(Collectors.toList());
            
            if (!lowStock.isEmpty()) {
                System.out.println("Low stock items:");
                for (Medicine medicine : lowStock) {
                    System.out.println("  - " + medicine.getName() + ": " + medicine.getQuantity() + " units");
                }
            }
        }
        
        // Display medicines that require prescriptions
        List<Medicine> prescriptionMedicines = medicines.stream()
                                           .filter(Medicine::isRequiresPrescription)
                                           .collect(Collectors.toList());
        
        System.out.println("\nMedicines Requiring Prescription: " + prescriptionMedicines.size());
        
        // Display out of stock items
        List<Medicine> outOfStock = medicines.stream()
                                 .filter(m -> m.getQuantity() == 0)
                                 .collect(Collectors.toList());
        
        if (!outOfStock.isEmpty()) {
            System.out.println("\nOut of Stock Items:");
            for (Medicine medicine : outOfStock) {
                System.out.println("  - " + medicine.getName() + " (" + medicine.getCategory() + ")");
            }
        }
        
        System.out.println("================================================================");
    }
}
