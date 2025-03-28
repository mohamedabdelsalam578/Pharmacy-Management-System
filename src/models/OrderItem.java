package models;

import java.io.Serializable;

/**
 * Represents an item in an order
 */
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int medicineId;
    private String medicineName;
    private int quantity;
    private double unitPrice;
    private boolean isPrescription;
    
    /**
     * Constructor for creating a new order item
     * 
     * @param medicineId The ID of the medicine
     * @param medicineName The name of the medicine
     * @param quantity The quantity of this item
     * @param unitPrice The unit price of this item
     */
    public OrderItem(int medicineId, String medicineName, int quantity, double unitPrice) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.isPrescription = false;
    }
    
    /**
     * Constructor for creating a new order item from a medicine
     * 
     * @param medicine The medicine
     * @param quantity The quantity of this item
     */
    public OrderItem(Medicine medicine, int quantity) {
        this.medicineId = medicine.getId();
        this.medicineName = medicine.getName();
        this.quantity = quantity;
        this.unitPrice = medicine.getPrice();
        this.isPrescription = medicine.isPrescription();
    }
    
    /**
     * Get the medicine ID of this item
     * 
     * @return The medicine ID
     */
    public int getMedicineId() {
        return medicineId;
    }
    
    /**
     * Set the medicine ID of this item
     * 
     * @param medicineId The medicine ID
     */
    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }
    
    /**
     * Get the medicine name of this item
     * 
     * @return The medicine name
     */
    public String getMedicineName() {
        return medicineName;
    }
    
    /**
     * Set the medicine name of this item
     * 
     * @param medicineName The medicine name
     */
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    
    /**
     * Get the quantity of this item
     * 
     * @return The quantity
     */
    public int getQuantity() {
        return quantity;
    }
    
    /**
     * Set the quantity of this item
     * 
     * @param quantity The quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    /**
     * Get the unit price of this item
     * 
     * @return The unit price
     */
    public double getUnitPrice() {
        return unitPrice;
    }
    
    /**
     * Set the unit price of this item
     * 
     * @param unitPrice The unit price
     */
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    /**
     * Check if this item requires a prescription
     * 
     * @return true if prescription required, false otherwise
     */
    public boolean isPrescription() {
        return isPrescription;
    }
    
    /**
     * Set whether this item requires a prescription
     * 
     * @param isPrescription Whether prescription is required
     */
    public void setPrescription(boolean isPrescription) {
        this.isPrescription = isPrescription;
    }
    
    /**
     * Get the total price of this item (unit price * quantity)
     * 
     * @return The total price
     */
    public double getTotalPrice() {
        return unitPrice * quantity;
    }
    
    /**
     * Get a formatted string representation of the unit price
     * 
     * @return The formatted unit price
     */
    public String getFormattedUnitPrice() {
        return String.format("%.2f LE", unitPrice);
    }
    
    /**
     * Get a formatted string representation of the total price
     * 
     * @return The formatted total price
     */
    public String getFormattedTotalPrice() {
        return String.format("%.2f LE", getTotalPrice());
    }
    
    /**
     * Get a formatted string representation of this item
     * 
     * @return The formatted string
     */
    @Override
    public String toString() {
        return String.format("%s x%d (%s) = %s", 
                medicineName, quantity, getFormattedUnitPrice(), getFormattedTotalPrice());
    }
}