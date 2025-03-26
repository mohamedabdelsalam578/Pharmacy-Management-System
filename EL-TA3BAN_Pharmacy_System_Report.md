# EL-TA3BAN PHARMACY MANAGEMENT SYSTEM
## Final Project Report

### Team Members
| ID      | Name                     | Section |
|---------|--------------------------|---------|
| 245296  | Mohamed Mohamed Abdelsalam| A9      |
| 246994  | Mazen Mohamed Masoud     | A8      |
| 239279  | Mariam Tamer Mostafa     | A7      |
| 241603  | Nour Ahmed Ali           | A10     |
| 245309  | Nouran Khaled Mohamed    | A10     |
| 242675  | Moaz Mohamed Saed        | A8      |

## Table of Contents
1. Introduction
2. System Requirements
3. System Architecture
4. Class Relationships
5. Implementation Details
6. User Interface Design
7. Data Persistence Strategy
8. Testing and Validation
9. Conclusion
10. References
11. Appendices

---

## 1. Introduction
The EL-TA3BAN Pharmacy Management System is a comprehensive Java-based application that streamlines medical documentation and collaboration through intelligent workflow management. The system implements a complete healthcare workflow between doctors, pharmacists, and patients with Egyptian localization of all sample data, including names, medicines, and currency (LE).

### Project Objectives
- Develop a user-friendly pharmacy management system based on object-oriented principles
- Implement a comprehensive healthcare workflow between doctors, pharmacists, and patients
- Ensure data persistence using serialized objects stored in text files
- Create a secure and intuitive interface for multiple user types
- Incorporate Egyptian localization throughout the system

### Key Features
- Multi-user authentication with role-based access control
- Complete prescription management lifecycle
- Inventory tracking and management
- Digital wallet system for secure in-app payments
- Patient medical records and history
- Doctor-patient consultation system
- Enhanced console interface with colors and visual elements

---

## 2. System Requirements

### Functional Requirements
- User authentication and authorization for four user types (Admin, Patient, Doctor, Pharmacist)
- Patient account management and wallet functionality
- Prescription creation, transmission, and fulfillment
- Medicine inventory management
- Order processing and tracking
- Medical consultation and messaging
- Report generation and system documentation

### Non-Functional Requirements
- Java-based implementation compatible with NetBeans IDE
- Object-oriented design principles (inheritance, polymorphism, encapsulation)
- Data persistence through file serialization
- Security features including password hashing
- Egyptian localization of all sample data
- Enhanced console user interface

---

## 3. System Architecture

The EL-TA3BAN Pharmacy Management System follows a service-oriented architecture with clear separation between:

1. **Model Layer**: Core data entities representing real-world objects
2. **Service Layer**: Business logic implementing operations
3. **Utility Layer**: Helper functions and tools for common operations
4. **Persistence Layer**: Data storage and retrieval mechanisms

```
[SYSTEM ARCHITECTURE DIAGRAM]

┌─────────────────────────────────────────────────────────┐
│                  Main Application                        │
│                                                         │
│  ┌─────────┐  ┌─────────────┐  ┌──────────────────┐    │
│  │  Models │  │  Services   │  │     Utils        │    │
│  │         │  │             │  │                  │    │
│  │ User    │  │ PharmacyService│  │ FileHandler    │    │
│  │ Admin   │  │ AdminService │  │ ConsoleUI       │    │
│  │ Patient │  │ PatientService│  │ SystemTools     │    │
│  │ Doctor  │  │ DoctorService│  │ DataInitializer │    │
│  │ Pharmacist│  │ PharmacistService│                  │    │
│  │ Medicine│  │ AuthenticationService│                  │    │
│  │ Order   │  │             │  │                  │    │
│  │ Prescription│             │  │                  │    │
│  │ Wallet  │  │             │  │                  │    │
│  │         │  │             │  │                  │    │
│  └─────────┘  └─────────────┘  └──────────────────┘    │
│                                                         │
│  ┌─────────────────────────┐                           │
│  │   Data Files (*.dat)    │                           │
│  └─────────────────────────┘                           │
└─────────────────────────────────────────────────────────┘
```

---

## 4. Class Relationships

### User Hierarchy Relationships
- **User (Abstract Parent Class)**:
    - **Admin (1:1)**: An Admin is a User with system management capabilities
    - **Patient (1:1)**: A Patient is a User who receives healthcare services
    - **Doctor (1:1)**: A Doctor is a User who provides medical consultations and prescriptions
    - **Pharmacist (1:1)**: A Pharmacist is a User who manages medications and fills prescriptions

### Key Relationship Types

| Relationship | Line Type | Relationship Type | Notes |
| --- | --- | --- | --- |
| User → Admin/Patient/Doctor/Pharmacist | Solid, hollow arrow | Inheritance | "is-a" relationship where subclasses inherit from User abstract class |
| Patient → Wallet | Solid, filled diamond | Composition | Wallet is part of Patient and destroyed when Patient is deleted |
| Pharmacy → Pharmacist | Solid, empty diamond | Aggregation | Pharmacy contains pharmacists, but pharmacists can exist independently |
| Consultation → Message | Solid, filled diamond | Composition | Messages are integral parts of a Consultation |
| All Model Classes → Serializable | Dashed, hollow arrow | Implementation | Interface implementation for data persistence |

(See Appendix A for the complete class relationship diagram)

---

## 5. Implementation Details

### Java Collections Used

| Collection | Description | Used In | Purpose |
| --- | --- | --- | --- |
| `ArrayList<T>` | Resizable array implementation of List interface | User classes, Service classes | Storing lists of objects (medicines, orders, prescriptions, users) |
| `Map<K,V>` | Interface for key-value associations | Prescription class | Storing medicine-quantity pairs in prescriptions |
| `HashMap<K,V>` | Hash table implementation of Map | Prescription, Pharmacy classes | Implementing medicine-quantity associations |

### Stream API Methods

| Method | Description | Used In | Purpose |
| --- | --- | --- | --- |
| `stream()` | Creates a sequential stream from a collection | Service classes | Starting stream operations for filtering data |
| `filter()` | Filters elements based on a predicate | All service classes | Finding objects by ID or other criteria |
| `findFirst()` | Returns first element of stream (Optional) | ID-based lookups | Finding a specific object by ID |

### Object-Oriented Programming Features

| Feature | Description | Used In | Purpose |
| --- | --- | --- | --- |
| Inheritance | Extends class capabilities | User hierarchy | Creating specialized user types |
| Polymorphism | Method behaving differently | displayInfo() | Different display for different objects |
| Encapsulation | Data hiding | All model classes | Private fields with public getters/setters |
| Abstraction | Hiding implementation details | User as abstract class | Forcing subclasses to implement methods |

(See Appendix B for complete method and collection usage details)

---

## 6. User Interface Design

The EL-TA3BAN Pharmacy Management System implements an enhanced console user interface with the following features:

### UI Enhancement Features
- Color-coded output for better visual hierarchy
- Unicode box-drawing characters for menu boundaries
- Emojis as visual indicators for different functions
- Loading animations for processing operations
- Clear success/error messaging with distinctive formatting

### Main Menu Interface
```
╔══════════════════════════════════════════════════╗
║            EL-TA3BAN PHARMACY SYSTEM             ║
╚══════════════════════════════════════════════════╝
──────────────────────────────────────────────────
  [1] 👨‍💼 Login as Admin
  [2] 🧑‍⚕️ Login as Patient
  [3] 👨‍⚕️ Login as Doctor
  [4] 👩‍⚕️ Login as Pharmacist
  [5] ✏️ Create Patient Account
  [6] 📑 Generate Documentation
  [7] 🚪 Exit
──────────────────────────────────────────────────
Enter your choice:
```

This menu interface is implemented in our system with proper coloring and formatting through the ConsoleUI utility class. As seen in the running system, this menu is the starting point for all user interactions, providing clear navigation options with intuitive emoji indicators for each role.

### User Experience Flow
1. Initial main menu for role selection
2. Role-based authentication 
3. Role-specific menu with available operations
4. Interactive workflows with guided prompts
5. Visual feedback for all operations

---

## 7. Data Persistence Strategy

The system implements data persistence using Java's serialization mechanism:

### Serialization Implementation
- All model classes implement the `Serializable` interface
- The `FileHandler` utility class manages file operations
- Data is stored in separate `.dat` files for different entity types
- Files are automatically created on first run if they don't exist

### File Structure
```
/data/
  ├── users.dat        (All User objects)
  ├── medicines.dat    (All Medicine objects)
  ├── orders.dat       (All Order objects)
  ├── prescriptions.dat (All Prescription objects)
  ├── consultations.dat (All Consultation objects)
  └── reports.dat      (All MedicalReport objects)
```

### Data Loading and Saving
- Data is loaded when the system starts through the DataInitializer class
- Changes are saved to disk when operations complete via the FileHandler's save methods
- Error handling with try-catch blocks ensures data integrity during I/O operations
- Serialization allows for complete object graphs to be persisted, maintaining all object relationships

#### Serialization Implementation Example
```java
// In model classes:
public class Patient extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    // Class implementation...
}

// In FileHandler:
public static void saveUsers(List<User> users) {
    try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream("data/users.dat"))) {
        oos.writeObject(users);
    } catch (IOException e) {
        System.err.println("Error saving users: " + e.getMessage());
    }
}

public static List<User> loadUsers() {
    List<User> users = new ArrayList<>();
    try (ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream("data/users.dat"))) {
        users = (List<User>) ois.readObject();
    } catch (FileNotFoundException e) {
        // First run, create new file
        saveUsers(users);
    } catch (IOException | ClassNotFoundException e) {
        System.err.println("Error loading users: " + e.getMessage());
    }
    return users;
}
```

---

## 8. Testing and Validation

### Testing Approach
- Functional testing of all user workflows
- Security testing of authentication mechanisms
- Data persistence validation
- Edge case handling
- User interface validation

### Validation Techniques
- Input validation for all user-provided data
- Exception handling throughout the system
- Automatic test data generation for system evaluation
- Comprehensive testing of the healthcare workflow cycle

### Test Scenarios Implemented
1. **Patient Registration and Authentication**
   - Testing user creation with valid/invalid data
   - Testing login with correct/incorrect credentials
   - Testing password hashing security

2. **Prescription Workflow Testing**
   - Doctor creates prescription for patient
   - Prescription is sent to pharmacy
   - Pharmacist processes prescription
   - Patient receives filled prescription

3. **Consultation System Testing**
   - Patient requests consultation with doctor
   - Doctor accepts consultation
   - Messages are exchanged
   - Consultation history is maintained

4. **Wallet Transaction Testing**
   - Funds are added to patient wallet
   - Payments are processed for prescriptions
   - Transaction history is maintained
   - Wallet balance is updated correctly

---

## 9. Conclusion

The EL-TA3BAN Pharmacy Management System successfully implements a comprehensive pharmacy solution with extended healthcare workflow functionality. By following object-oriented design principles, the system provides a robust, maintainable, and extensible platform for pharmacy operations.

### Key Achievements
- Complete implementation of all required functionality including prescription management, medical consultations, and order processing
- Robust object-oriented design with clear separation of concerns through service-oriented architecture
- Enhanced security features including password hashing and login attempt limiting
- Comprehensive Egyptian localization of user names, medicines, and currency (LE)
- Full healthcare workflow between doctors, pharmacists, and patients with consultation messaging
- Local wallet-based payment system for seamless transactions without external dependencies
- Enhanced console interface with colors, animations, and intuitive navigation

### Future Enhancements
- Graphical user interface implementation
- Database integration for improved scalability
- Mobile application for patient access
- Advanced reporting and analytics
- Electronic prescription signature verification

---

## 10. References

1. Java Documentation - [https://docs.oracle.com/en/java/](https://docs.oracle.com/en/java/)
2. Object-Oriented Programming Concepts - [https://www.oracle.com/java/technologies/java-object-oriented-design.html](https://www.oracle.com/java/technologies/java-object-oriented-design.html)

---

## 11. Appendices

### Appendix A: Complete Class Diagram

(Insert detailed class diagram here)

### Appendix B: Methods and Collections Used

(See separate document with comprehensive table of Java methods and collections)

### Appendix C: System Screenshots

(Insert system screenshots here)