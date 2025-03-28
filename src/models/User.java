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
 * 
 * 📚 Class Responsibilities:
 * - Provides common attributes for all user types (id, name, credentials, etc.)
 * - Enforces a consistent interface across different user roles
 * - Centralizes authentication-related data for the whole system
 * 
 * This is a great example of how we apply the "is-a" relationship in OOP.
 * For example, a Doctor "is-a" User with additional doctor-specific properties.
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
     * 
     * @param id User's unique identifier
     * @param name User's full name
     * @param username User's username for login
     * @param password User's password for login
     * @param email User's email address
     * @param phoneNumber User's phone number
     */
    public User(int id, String name, String username, String password, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    /**
     * 🔄 Abstract method to display information about a user (Polymorphism in action!)
     * 
     * This is a perfect example of polymorphism - one of the four pillars of OOP.
     * Each subclass (Admin, Patient, Doctor, Pharmacist) will override this method
     * to display role-specific information, but they can all be called through a
     * common User interface.
     * 
     * When we call displayInfo() on different User objects, the program automatically
     * knows which version to call based on the actual object type at runtime. That's
     * dynamic polymorphism!
     * 
     * For example:
     * - Admin will display administrative privileges and system access level
     * - Patient will display medical history and current prescriptions
     * - Doctor will display specialization and patient count
     * - Pharmacist will display their pharmacy branch and qualification
     */
    public abstract void displayInfo();
}