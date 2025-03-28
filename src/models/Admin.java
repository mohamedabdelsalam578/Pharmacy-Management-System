package models;

public class Admin extends User {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String email;
    private String phoneNumber;
    private String position;
    private String department;

    /**
     * Constructor for creating a new admin
     * 
     * @param id The unique identifier for this admin
     * @param name The name of this admin
     * @param username The username for this admin
     * @param password The password for this admin
     * @param email The email of this admin
     * @param phoneNumber The phone number of this admin
     * @param position The position/role of this admin
     * @param department The department of this admin
     */
    public Admin(int id, String name, String username, String password, String email, 
                String phoneNumber, String position, String department) {
        super(id, username, password, "ADMIN");
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.position = position;
        this.department = department;
    }

    /**
     * Get the name of this admin
     * 
     * @return The name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the name of this admin
     * 
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get the email of this admin
     * 
     * @return The email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Set the email of this admin
     * 
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Get the phone number of this admin
     * 
     * @return The phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    /**
     * Set the phone number of this admin
     * 
     * @param phoneNumber The phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    /**
     * Get the position of this admin
     * 
     * @return The position
     */
    public String getPosition() {
        return position;
    }
    
    /**
     * Set the position of this admin
     * 
     * @param position The position
     */
    public void setPosition(String position) {
        this.position = position;
    }
    
    /**
     * Get the department of this admin
     * 
     * @return The department
     */
    public String getDepartment() {
        return department;
    }
    
    /**
     * Set the department of this admin
     * 
     * @param department The department
     */
    public void setDepartment(String department) {
        this.department = department;
    }
    
    /**
     * Display information about this admin
     */
    public void displayInfo() {
        System.out.println("Admin: " + getName() + " (" + position + " - " + department + ")");
    }
    
    /**
     * Get a formatted string representation of this admin
     * 
     * @return The formatted string
     */
    @Override
    public String toString() {
        return String.format("Admin [ID: %d, Name: %s, Email: %s, Position: %s, Department: %s]", 
                getId(), name, email, position, department);
    }
}