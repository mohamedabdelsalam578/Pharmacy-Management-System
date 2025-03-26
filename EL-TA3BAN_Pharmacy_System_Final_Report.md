# Pharmacy Management System – Final Report

## 1. Project Identification
- **Project Title**: EL-TA3BAN Pharmacy Management System
- **Course Name**: Object-Oriented Programming
- **University**: British University in Egypt (BUE)
- **Student Names & IDs**:
  - Mohamed Mohamed Abdelsalam (245296) - Section A9
  - Mazen Mohamed Masoud (246994) - Section A8
  - Mariam Tamer Mostafa (239279) - Section A7
  - Nour Ahmed Ali (241603) - Section A10
  - Nouran Khaled Mohamed (245309) - Section A10
  - Moaz Mohamed Saed (242675) - Section A8
- **Supervisor**: [Your Supervisor's Name]
- **Date**: March 26, 2023

## 2. Overview

### 2.1 Introduction
The EL-TA3BAN Pharmacy Management System is a comprehensive Java-based application designed to streamline pharmacy operations and enhance healthcare collaboration through an integrated workflow system. The project implements a complete digital infrastructure for connecting patients, doctors, and pharmacists in the Egyptian healthcare environment.

This system addresses the critical need for efficient medication management, prescription handling, and patient record-keeping in modern pharmacies. By digitalizing these processes, the system reduces errors, improves patient care, and enhances operational efficiency.

The significance of this project lies in its ability to create a cohesive healthcare ecosystem where information flows seamlessly between stakeholders, ensuring better patient outcomes and optimized pharmacy operations.

### 2.2 Project Scope

#### Key Functionalities:
- User authentication and role-based access control
- Complete prescription lifecycle management
- Medical inventory tracking and management
- Digital wallet payment system
- Patient medical records and history
- Doctor-patient consultation system
- Administrative system management

#### User Roles:
1. **Patients**:
   - Create and manage personal accounts
   - View medical history and prescriptions
   - Request doctor consultations
   - Order medicines
   - Manage digital wallet for payments

2. **Doctors**:
   - Create digital prescriptions
   - Maintain patient medical records
   - Participate in consultations with patients
   - View medication history

3. **Pharmacists**:
   - Manage medicine inventory
   - Process and fill prescriptions
   - Handle patient orders
   - Update medicine information

4. **Administrators**:
   - Manage system users
   - Generate system reports
   - Configure system settings
   - Override security features when necessary

#### Main Features:
- **Prescription Management**: Creation, transmission, filling, and tracking
- **Inventory Control**: Stock levels, expiration dates, and automated reordering
- **Patient Records**: Complete medical history and prescription archive
- **Consultation System**: Secure messaging between doctors and patients
- **Payment Processing**: Integrated digital wallet with transaction history
- **Reporting**: Comprehensive system analytics and operational reports
- **Enhanced UI**: Color-coded console interface with intuitive navigation

### 2.3 Technologies Used
- **Programming Language**: Java (JDK 11+)
- **Data Persistence**: Java Serialization (Object streams)
- **Project Structure**: Object-Oriented Architecture with Service Pattern
- **Security Features**: Password hashing, input validation, and access control
- **IDE**: NetBeans
- **Version Control**: Git/GitHub
- **Documentation**: JavaDoc, Markdown

## 3. Timeline

| Phase | Task Description | Start Date | End Date |
|-------|-----------------|------------|----------|
| Phase 1 | Requirement Analysis | February 1, 2023 | February 7, 2023 |
| Phase 2 | System Design | February 8, 2023 | February 15, 2023 |
| Phase 3 | Model Implementation | February 16, 2023 | February 28, 2023 |
| Phase 4 | Service Layer Implementation | March 1, 2023 | March 10, 2023 |
| Phase 5 | User Interface Development | March 11, 2023 | March 18, 2023 |
| Phase 6 | Testing & Debugging | March 19, 2023 | March 23, 2023 |
| Phase 7 | Documentation & Report | March 24, 2023 | March 26, 2023 |

## 4. System Architecture

### 4.1 Architectural Overview
The system follows a service-oriented architecture with clear separation between:

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

### 4.2 Class Relationships

#### User Hierarchy Relationships
- **User (Abstract Parent Class)**:
    - **Admin (1:1)**: System management capabilities
    - **Patient (1:1)**: Healthcare service recipient 
    - **Doctor (1:1)**: Medical consultation provider
    - **Pharmacist (1:1)**: Medication management

#### Key Relationship Types

| Relationship | Line Type | Relationship Type | Notes |
| --- | --- | --- | --- |
| User → Admin/Patient/Doctor/Pharmacist | Solid, hollow arrow | Inheritance | "is-a" relationship where subclasses inherit from User abstract class |
| Patient → Wallet | Solid, filled diamond | Composition | Wallet is part of Patient and destroyed when Patient is deleted |
| Pharmacy → Pharmacist | Solid, empty diamond | Aggregation | Pharmacy contains pharmacists, but pharmacists can exist independently |
| Consultation → Message | Solid, filled diamond | Composition | Messages are integral parts of a Consultation |
| All Model Classes → Serializable | Dashed, hollow arrow | Implementation | Interface implementation for data persistence |

## 5. Implementation Details

### 5.1 Java Collections Used

| Collection | Description | Used In | Purpose |
| --- | --- | --- | --- |
| `ArrayList<T>` | Resizable array implementation of List interface | User classes, Service classes | Storing lists of objects (medicines, orders, prescriptions, users) |
| `Map<K,V>` | Interface for key-value associations | Prescription class | Storing medicine-quantity pairs in prescriptions |
| `HashMap<K,V>` | Hash table implementation of Map | Prescription, Pharmacy classes | Implementing medicine-quantity associations |

### 5.2 Stream API Methods

| Method | Description | Used In | Purpose |
| --- | --- | --- | --- |
| `stream()` | Creates a sequential stream from a collection | Service classes | Starting stream operations for filtering data |
| `filter()` | Filters elements based on a predicate | All service classes | Finding objects by ID or other criteria |
| `findFirst()` | Returns first element of stream (Optional) | ID-based lookups | Finding a specific object by ID |

### 5.3 Object-Oriented Programming Features

| Feature | Description | Used In | Purpose |
| --- | --- | --- | --- |
| Inheritance | Extends class capabilities | User hierarchy | Creating specialized user types |
| Polymorphism | Method behaving differently | displayInfo() | Different display for different objects |
| Encapsulation | Data hiding | All model classes | Private fields with public getters/setters |
| Abstraction | Hiding implementation details | User as abstract class | Forcing subclasses to implement methods |

## 6. User Interface Design

The EL-TA3BAN Pharmacy Management System implements an enhanced console user interface with the following features:

### 6.1 UI Enhancement Features
- Color-coded output for better visual hierarchy
- Unicode box-drawing characters for menu boundaries
- Emojis as visual indicators for different functions
- Loading animations for processing operations
- Clear success/error messaging with distinctive formatting

### 6.2 Main Menu Interface
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

This menu interface is implemented with proper coloring and formatting through the ConsoleUI utility class. As seen in the running system, this menu is the starting point for all user interactions, providing clear navigation options with intuitive emoji indicators for each role.

## 7. Data Persistence Strategy

The system implements data persistence using Java's serialization mechanism:

### 7.1 Serialization Implementation
- All model classes implement the `Serializable` interface
- The `FileHandler` utility class manages file operations
- Data is stored in separate `.dat` files for different entity types
- Files are automatically created on first run if they don't exist

### 7.2 File Structure
```
/data/
  ├── users.dat        (All User objects)
  ├── medicines.dat    (All Medicine objects)
  ├── orders.dat       (All Order objects)
  ├── prescriptions.dat (All Prescription objects)
  ├── consultations.dat (All Consultation objects)
  └── reports.dat      (All MedicalReport objects)
```

### 7.3 Serialization Implementation Example
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

## 8. Testing and Validation

### 8.1 Testing Approach
- Functional testing of all user workflows
- Security testing of authentication mechanisms
- Data persistence validation
- Edge case handling
- User interface validation

### 8.2 Validation Techniques
- Input validation for all user-provided data
- Exception handling throughout the system
- Automatic test data generation for system evaluation
- Comprehensive testing of the healthcare workflow cycle

### 8.3 Test Scenarios Implemented
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

## 9. Challenges and Solutions

### 9.1 Technical Challenges
1. **Data Persistence**: 
   - **Challenge**: Ensuring all object relationships are properly maintained when saving/loading data
   - **Solution**: Implemented complete serialization of object graphs using Java's built-in serialization

2. **Security Implementation**:
   - **Challenge**: Creating a secure authentication system without external libraries
   - **Solution**: Developed custom password hashing and login attempt limiting

3. **Complex Workflow Management**:
   - **Challenge**: Coordinating the interaction between different user roles
   - **Solution**: Implemented service-oriented architecture with clear separation of concerns

### 9.2 Team Challenges
1. **Task Distribution**:
   - **Challenge**: Effective division of work among team members
   - **Solution**: Assigned roles based on strengths and established clear communication channels

2. **Code Integration**:
   - **Challenge**: Merging code from different team members
   - **Solution**: Implemented version control with Git and established coding standards

## 10. Conclusion

### 10.1 Key Achievements
- Complete implementation of all required functionality including prescription management, medical consultations, and order processing
- Robust object-oriented design with clear separation of concerns through service-oriented architecture
- Enhanced security features including password hashing and login attempt limiting
- Comprehensive Egyptian localization of user names, medicines, and currency (LE)
- Full healthcare workflow between doctors, pharmacists, and patients with consultation messaging
- Local wallet-based payment system for seamless transactions without external dependencies
- Enhanced console interface with colors, animations, and intuitive navigation

### 10.2 Future Enhancements
- Graphical user interface implementation
- Database integration for improved scalability
- Mobile application for patient access
- Advanced reporting and analytics
- Electronic prescription signature verification

## 11. References

1. Java Documentation - [https://docs.oracle.com/en/java/](https://docs.oracle.com/en/java/)
2. Object-Oriented Programming Concepts - [https://www.oracle.com/java/technologies/java-object-oriented-design.html](https://www.oracle.com/java/technologies/java-object-oriented-design.html)
3. Java Serialization API - [https://docs.oracle.com/javase/8/docs/technotes/guides/serialization/](https://docs.oracle.com/javase/8/docs/technotes/guides/serialization/)

## 12. Appendices

### Appendix A: Team Member Contributions

| Team Member | ID | Section | Primary Contributions |
|-------------|----|---------|-----------------------|
| Mohamed Mohamed Abdelsalam | 245296 | A9 | System architecture, Authentication service |
| Mazen Mohamed Masoud | 246994 | A8 | Doctor service, Consultation system |
| Mariam Tamer Mostafa | 239279 | A7 | Patient service, Wallet implementation |
| Nour Ahmed Ali | 241603 | A10 | Pharmacy service, Prescription management |
| Nouran Khaled Mohamed | 245309 | A10 | UI design, Documentation |
| Moaz Mohamed Saed | 242675 | A8 | Testing, Data initialization |

### Appendix B: System Screenshots

(Insert system screenshots here)

### Appendix C: Code Samples

(Insert representative code samples here)