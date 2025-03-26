# Pharmacy Management System – Final Report

## 1. Project Identification
- **Project Title**: EL-TA3BAN Pharmacy Management System
- **Course Name**: Programming in Java (24CSCI04C)
- **University**: British University in Egypt (BUE)
- **Student Names & IDs**:
  - Mohamed Mohamed Abdelsalam (245296) - Section A9
  - Mazen Mohamed Masoud (246994) - Section A8
  - Mariam Tamer Mostafa (239279) - Section A7
  - Nour Ahmed Ali (241603) - Section A10
  - Nouran Khaled Mohamed (245309) - Section A10
  - Moaz Mohamed Saed (242675) - Section A8
- **Supervisor**: Dr. Khaled Alsheshtawi
- **Date**: March 26, 2025

## 2. Overview

### 2.1 Introduction
The EL-TA3BAN Pharmacy Management System is a comprehensive Java-based application that streamlines medical documentation and collaboration through intelligent workflow management. **Our implementation provides a complete healthcare workflow between doctors, pharmacists, and patients** with Egyptian localization of all sample data, including names, medicines, and currency (LE).

By digitalizing pharmacy processes, the system reduces errors, improves patient care, and enhances operational efficiency. The significance of this project lies in its ability to create a cohesive healthcare ecosystem where information flows seamlessly between stakeholders.

### 2.2 Project Scope

#### Key Functionalities Implemented:
- **✓ User authentication with role-based access control**
- **✓ Complete prescription lifecycle management**
- **✓ Medical inventory tracking and management**
- **✓ Digital wallet payment system**
- **✓ Patient medical records and history**
- **✓ Doctor-patient consultation system with messaging**
- **✓ Enhanced console user interface**

#### User Roles:
1. **Patients**: 
   - **✓ Create and manage accounts**
   - **✓ View medical history and prescriptions**
   - **✓ Request doctor consultations**
   - **✓ Order medicines**
   - **✓ Manage digital wallet for payments**

2. **Doctors**:
   - **✓ Create digital prescriptions**
   - **✓ Maintain patient medical records**
   - **✓ Participate in consultations with patients**
   - **✓ View medication history**

3. **Pharmacists**:
   - **✓ Manage medicine inventory**
   - **✓ Process and fill prescriptions**
   - **✓ Handle patient orders**
   - **✓ Update medicine information**

4. **Administrators**:
   - **✓ Manage system users**
   - **✓ Generate system reports**
   - **✓ Configure system settings**

### 2.3 Technologies Used
- **✓ Programming Language**: Java
- **✓ Data Persistence**: Java Serialization (Object streams)
- **✓ Project Structure**: Object-Oriented Architecture
- **✓ Security Features**: Password hashing and validation
- **✓ IDE**: NetBeans
- **✓ Documentation**: JavaDoc, Markdown

## 3. Timeline

| Phase | Task Description | Start Date | End Date |
|-------|-----------------|------------|----------|
| Phase 1 | Requirement Analysis | March 1, 2025 | March 5, 2025 |
| Phase 2 | System Design and Class Diagram | March 6, 2025 | March 10, 2025 |
| Phase 3 | Core Model Implementation | March 11, 2025 | March 15, 2025 |
| Phase 4 | Service Layer Implementation | March 16, 2025 | March 19, 2025 |
| Phase 5 | User Interface Development | March 20, 2025 | March 22, 2025 |
| Phase 6 | Testing & Debugging | March 23, 2025 | March 24, 2025 |
| Phase 7 | Documentation & Report | March 25, 2025 | March 26, 2025 |

## 4. System Architecture

### 4.1 Architectural Overview
**We implemented a service-oriented architecture with clear separation between**:

1. **✓ Model Layer**: Core data entities (User, Patient, Doctor, Medicine, etc.)
2. **✓ Service Layer**: Business logic (PharmacyService, AuthenticationService, etc.)
3. **✓ Utility Layer**: Helper functions (FileHandler, ConsoleUI, etc.)
4. **✓ Persistence Layer**: Data storage and retrieval (Serialization)

### 4.2 Class Relationships

#### Complete Class Hierarchy
- **✓ User (Abstract Parent Class)**: Base class for all system users
    - **✓ Admin**: System administrators with full access rights
    - **✓ Patient**: Healthcare recipients with prescriptions and wallet
    - **✓ Doctor**: Medical providers who create prescriptions and reports
    - **✓ Pharmacist**: Medicine dispensers who process prescriptions
- **✓ Medicine**: Pharmaceutical products with name, price, and description
- **✓ Prescription**: Doctor's orders for patient medications
- **✓ Order**: Patient requests for specific medicines
- **✓ Pharmacy**: Physical or virtual establishment for filling prescriptions
- **✓ Consultation**: Communication session between doctor and patient
- **✓ Message**: Individual communications within consultations
- **✓ MedicalReport**: Patient health documentation
- **✓ Wallet**: Patient's payment system with balance and transaction history
- **✓ Transaction**: Record of financial exchanges in the system

#### Key Relationship Types

| Relationship | Relationship Type | Notes |
| --- | --- | --- |
| **✓ User → Admin/Patient/Doctor/Pharmacist** | Inheritance | "is-a" relationship with User parent class |
| **✓ Patient → Wallet** | Composition | Wallet is part of Patient and cannot exist independently |
| **✓ Patient → Order** | Association | Patients create orders but orders can exist independently |
| **✓ Patient → Prescription** | Association | Prescriptions persist independently after creation |
| **✓ Patient → MedicalReport** | Association | Medical reports persist independently for record-keeping |
| **✓ Patient → Consultation** | Association | Consultations persist independently in the system |
| **✓ Doctor → Prescription** | Association | Doctors create prescriptions but no lifecycle dependency |
| **✓ Doctor → MedicalReport** | Association | Reports persist independently for medical records |
| **✓ Doctor → Consultation** | Association | No lifecycle dependency between doctor and consultations |
| **✓ Pharmacist → Pharmacy** | Association | Employment relationship without ownership |
| **✓ Pharmacy → Pharmacist** | Aggregation | Pharmacy contains pharmacists, but pharmacists can exist independently |
| **✓ Pharmacy → Medicine** | Association | Inventory relationship without lifecycle dependency |
| **✓ Prescription → Medicine** | Association | Many-to-many relationship with quantities |
| **✓ Order → Medicine** | Association | Many-to-many relationship with quantities |
| **✓ Consultation → Message** | Composition | Messages are integral parts of a Consultation |
| **✓ Wallet → Transaction** | Composition | Transactions belong to Wallet and cannot exist independently |
| **✓ All Model Classes → Serializable** | Implementation | Interface implementation for data persistence |

## 5. Implementation Details

### 5.1 Java Collections Used

| Collection | Used In | Purpose |
| --- | --- | --- |
| **✓ `ArrayList<T>`** | User classes, Service classes | Storing lists of objects |
| **✓ `Map<K,V>`** | Prescription class | Storing medicine-quantity pairs |
| **✓ `HashMap<K,V>`** | Prescription, Pharmacy classes | Implementing medicine associations |

### 5.2 Object-Oriented Programming Features

| Feature | Used In | Purpose |
| --- | --- | --- |
| **✓ Inheritance** | User hierarchy | Creating specialized user types |
| **✓ Polymorphism** | displayInfo() | Different display for different objects |
| **✓ Encapsulation** | All model classes | Private fields with public getters/setters |
| **✓ Abstraction** | User class | Forcing subclasses to implement methods |

## 6. User Interface Design

**We implemented an enhanced console user interface with the following features**:

### 6.1 UI Enhancement Features
- **✓ Color-coded output** for better visual hierarchy
- **✓ Unicode box-drawing characters** for menu boundaries
- **✓ Emojis as visual indicators** for different functions
- **✓ Loading animations** for processing operations
- **✓ Clear success/error messaging** with distinctive formatting

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
```

## 7. Data Persistence Implementation

**We implemented data persistence using Java's serialization mechanism**:

- **✓ All model classes implement** the `Serializable` interface
- **✓ FileHandler utility class** manages file operations
- **✓ Data stored in separate files** for different entity types
- **✓ Error handling** ensures data integrity during I/O operations

## 8. Testing and Validation

### 8.1 Test Scenarios Implemented
1. **✓ Patient Registration and Authentication**
   - Testing user creation with valid/invalid data
   - Testing login with correct/incorrect credentials

2. **✓ Prescription Workflow Testing**
   - Doctor creates prescription for patient
   - Prescription is sent to pharmacy
   - Pharmacist processes prescription

3. **✓ Consultation System Testing**
   - Patient requests consultation with doctor
   - Doctor accepts consultation
   - Messages are exchanged

4. **✓ Wallet Transaction Testing**
   - Funds are added to patient wallet
   - Payments are processed for prescriptions
   - Transaction history is maintained

## 9. Unique Contributions & Achievements

### 9.1 Key Achievements
- **✓ Complete healthcare workflow** with doctor-pharmacist-patient interaction
- **✓ Implemented security features** including password hashing and login attempt limiting
- **✓ Full chat functionality** between doctors and patients
- **✓ Egyptian-localized test data** with proper names, medicines, and currency
- **✓ Local wallet payment system** without relying on external services
- **✓ Enhanced console interface** with colors, animations, and emojis

### 9.2 Additional Features Beyond Requirements
- **✓ Advanced messaging system** between doctors and patients
- **✓ Medical reports generation**
- **✓ Digital wallet system** with transaction history
- **✓ Direct integration** between prescriptions and consultations

## 10. Team Member Contributions

| Team Member | ID | Section | Primary Contributions |
|-------------|----|---------|-----------------------|
| Mohamed Mohamed Abdelsalam | 245296 | A9 | **✓ Overall architecture and implementation** <br>**✓ Project coordination and integration** <br>**✓ System design and code review** |
| Mazen Mohamed Masoud | 246994 | A8 | **✓ Doctor class and DoctorService implementation** |
| Mariam Tamer Mostafa | 239279 | A7 | **✓ Patient class and PatientService implementation** |
| Nour Ahmed Ali | 241603 | A10 | **✓ Pharmacy class and PharmacyService implementation** |
| Nouran Khaled Mohamed | 245309 | A10 | **✓ Prescription class and related functionality** |
| Moaz Mohamed Saed | 242675 | A8 | **✓ Medicine class and inventory management** |

## 11. Conclusion

The EL-TA3BAN Pharmacy Management System successfully implements a comprehensive pharmacy solution with extended healthcare workflow functionality. By following object-oriented design principles, the system provides a robust, maintainable, and extensible platform for pharmacy operations.

**We have met and exceeded the project requirements by implementing:**
- **✓ All required admin functions** (add/remove/update medicines, generate reports)
- **✓ All required client functions** (create account, order medicines, cancel/update orders)
- **✓ Proper use of collections** for all required entities
- **✓ Complete class hierarchy** with inheritance, interfaces, and polymorphism
- **✓ Advanced features** beyond the basic requirements