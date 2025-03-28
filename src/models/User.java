package models;

/**
 * 👤 User - The foundation of our system's class hierarchy 👤
 * 
 * This abstract class demonstrates the OOP concept of inheritance by serving
 * as the parent class for all user types in our pharmacy system.
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - Abstraction: User class cannot be instantiated directly (it's abstract!)
 * - Encapsulation: Private fields with public getters/setters protect our data
 * - Inheritance: Admin, Patient, Doctor, and Pharmacist all inherit from User
 * - Polymorphism: The displayInfo() method behaves differently for each user type
 */
public abstract class User {
    private int id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;

    /**
     * Constructor to initialize a User
     */
    public User(int id, String name, String username, String password, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    /**
     * Abstract method to display information about a user (Polymorphism in action!)
     * Each subclass will override this method to display role-specific information.
     */
    public abstract void displayInfo();
}