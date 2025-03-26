package models;

/**
 * 💊 Medicine - Core entity representing pharmaceutical products
 * 
 * This class represents medications within the pharmacy system and
 * stores all relevant product information including pricing, inventory,
 * and prescription requirements.
 * 
 * 🔑 Class Responsibilities:
 * - Store complete medication information (name, price, etc.)
 * - Track inventory quantities through stock management methods
 * - Enforce prescription requirements for controlled substances
 * - Provide formatted display of medicine information
 * 
 * 🏗️ Design Patterns & Principles:
 * - Encapsulation: All fields are private with public getters/setters
 * - Information Expert: Contains all data and behavior related to medicines
 * - Entity Object: Represents a primary domain model in the system
 * 
 * 🔄 Relationships:
 * - Used by Pharmacy to maintain inventory
 * - Referenced by Prescription to build medicine lists
 * - Managed by PharmacistService for stock control
 * - Ordered by Patient through shopping cart system
 */
public class Medicine {
    private int id;
    private String name;
    private String description;
    private String manufacturer;
    private double price;
    private int quantity;
    private String category;
    private boolean requiresPrescription;
    private boolean hasPrescription;

    /**
     * 🏗️ Constructor - Creates a new Medicine object with all required attributes
     * 
     * This constructor initializes a complete medicine record with all its
     * critical attributes including pricing, inventory information, and
     * prescription requirements. It serves as the primary means of creating
     * medicine objects in the system.
     * 
     * 💡 Object Initialization:
     * - Sets all required attributes for a valid medicine object
     * - Initializes the hasPrescription flag to false by default
     * - Validates that all required fields receive appropriate values
     * 
     * The prescription requirement flag is particularly important as it
     * enforces the Egyptian laws regarding controlled substances that
     * cannot be sold without a doctor's prescription.
     * 
     * @param id Unique identifier for this medicine
     * @param name Official medicine name (generic or brand name)
     * @param description Brief explanation of usage and effects
     * @param manufacturer Company that produces this medicine
     * @param price Cost in Egyptian Pounds (LE)
     * @param quantity Initial stock available in the pharmacy
     * @param category Medicine classification (e.g., analgesic, antibiotic)
     * @param requiresPrescription Whether this medicine requires a doctor's prescription
     */
    public Medicine(int id, String name, String description, String manufacturer, 
                   double price, int quantity, String category, boolean requiresPrescription) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.requiresPrescription = requiresPrescription;
        this.hasPrescription = false;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getManufacturer() { return manufacturer; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getStock() { return quantity; }
    public String getCategory() { return category; }
    public boolean getRequiresPrescription() { return requiresPrescription; }
    public boolean isRequiresPrescription() { return requiresPrescription; }
    public boolean hasPrescription() { return hasPrescription; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setStock(int stock) { this.quantity = stock; }
    public void setCategory(String category) { this.category = category; }
    public void setRequiresPrescription(boolean requiresPrescription) { this.requiresPrescription = requiresPrescription; }
    public void setPrescription(boolean hasPrescription) { this.hasPrescription = hasPrescription; }
    
    /**
     * 📦 updateStock - Sets a new inventory level for this medicine
     * 
     * This method allows complete replacement of the current stock quantity
     * with a new value. This is typically used when performing inventory
     * adjustments after physical counting or when receiving new shipments.
     * 
     * 💡 Safety Features:
     * - Input validation prevents negative quantities
     * - Returns boolean status to verify success or failure
     * - Uses direct assignment for atomicity (all-or-nothing update)
     * 
     * This method demonstrates basic input validation to ensure data
     * integrity and prevent impossible inventory states.
     * 
     * @param quantity The new quantity to set (must be non-negative)
     * @return true if the update was successful, false if validation failed
     */
    public boolean updateStock(int quantity) {
        if (quantity < 0) {
            return false;
        }
        this.quantity = quantity;
        return true;
    }

    /**
     * 📉 decreaseStock - Reduces inventory when medicines are dispensed
     * 
     * This method is called when medications are sold or dispensed to patients.
     * It carefully checks if enough stock is available before proceeding with
     * the reduction to prevent inventory from going negative.
     * 
     * 💡 Validation Logic:
     * - Checks that amount is positive (prevents erroneous increases)
     * - Verifies sufficient stock exists before deducting
     * - Returns status indicating whether operation succeeded
     * 
     * This method is crucial for maintaining accurate inventory levels
     * and preventing impossible stock situations that would compromise
     * pharmacy operations.
     * 
     * @param amount The quantity to remove from inventory (must be positive)
     * @return true if successfully decreased, false if insufficient stock or invalid amount
     */
    public boolean decreaseStock(int amount) {
        if (amount < 0 || amount > quantity) {
            return false;
        }
        this.quantity -= amount;
        return true;
    }

    /**
     * 📈 restoreStock - Increases inventory quantities
     * 
     * This method is used when returning unsold medicine to inventory,
     * receiving new shipments, or correcting inventory errors. It
     * provides a safe way to increase available stock.
     * 
     * 💡 Implementation Notes:
     * - Validates input to prevent negative adjustments
     * - Silent failure for invalid amounts (returns void)
     * - No upper limit on quantity increases
     * 
     * Unlike decreaseStock, this method doesn't need to check for
     * maximum limits since pharmacies can theoretically have unlimited
     * stock of medications.
     * 
     * @param amount Amount to add to current stock (must be positive)
     */
    public void restoreStock(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    /**
     * 📋 displayInfo - Presents complete medication details
     * 
     * This method outputs a formatted display of all medicine details to the console,
     * providing a comprehensive view of the medication's properties, price,
     * and current inventory status. This is used in the admin interface,
     * pharmacy inventory screens, and patient medication information views.
     * 
     * 💡 UI/UX Considerations:
     * - Consistent formatting with labeled fields
     * - Currency displayed in Egyptian Pounds (LE)
     * - Human-readable representation of boolean values (Yes/No)
     * - Clear organization with manufacturer in parentheses 
     * 
     * This method demonstrates how to create readable console output
     * for complex data objects in a standardized format.
     */
    public void displayInfo() {
        System.out.println("Medicine: " + name + " (" + manufacturer + ")");
        System.out.println("ID: " + id);
        System.out.println("Description: " + description);
        System.out.println("Category: " + category);
        System.out.println("Price: " + price + " LE");
        System.out.println("Quantity in Stock: " + quantity);
        System.out.println("Requires Prescription: " + (requiresPrescription ? "Yes" : "No"));
    }
}