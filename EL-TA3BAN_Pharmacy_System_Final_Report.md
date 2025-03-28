<div align="center">

# 🏥 EL-TA3BAN Pharmacy Management System 🐍
### Final Project Report

![Pharmacy Logo](elta3ban-logo.png)

*A Comprehensive Healthcare Management Solution*

---

**British University in Egypt (BUE)**  
Faculty of Informatics and Computer Science  
Programming in Java (24CSCI04C)

March 28, 2025

</div>

---

## Table of Contents
1. [Project Information](#1-project-information)
2. [Executive Summary](#2-executive-summary)
3. [System Architecture](#3-system-architecture)
4. [Implementation Details](#4-implementation-details)
5. [User Interface](#5-user-interface)
6. [Data Persistence](#6-data-persistence)
7. [Testing and Validation](#7-testing-and-validation)
8. [Key Achievements](#8-key-achievements)
9. [Team Contributions](#9-team-contributions)
10. [Conclusion](#10-conclusion)

---

## 1. Project Information

### 1.1 Project Details

| Field | Details |
|-------|---------|
| Project Title | EL-TA3BAN Pharmacy Management System |
| Course | Programming in Java |
| Course Code | 24CSCI04C |
| Institution | British University in Egypt (BUE) |
| Department | Faculty of Informatics and Computer Science |
| Academic Year | 2024/2025 |
| Submission Date | March 28, 2025 |

### 1.2 Project Timeline

| Phase | Task Description |
|-------|-----------------|
| Phase 1 | Requirement Analysis |
| Phase 2 | System Design and Class Diagram |
| Phase 3 | Core Model Implementation |
| Phase 4 | Service Layer Implementation |
| Phase 5 | User Interface Development |
| Phase 6 | Testing & Debugging |
| Phase 7 | Documentation & Report |

### 1.3 Access Information

For testing and demonstration purposes, the following credentials are available:

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Patient | amr | alice123 |
| Doctor | drmohamed | doctor123 |
| Pharmacist | fatima | pharm123 |

## 2. Executive Summary

### 2.1 Introduction

The EL-TA3BAN Pharmacy Management System is a comprehensive Java-based application that streamlines medical documentation and collaboration through intelligent workflow management. Our implementation provides a complete healthcare workflow between doctors, pharmacists, and patients with Egyptian localization of all sample data, including names, medicines, and currency (LE).

By digitalizing pharmacy processes, the system reduces errors, improves patient care, and enhances operational efficiency. The significance of this project lies in its ability to create a cohesive healthcare ecosystem where information flows seamlessly between all stakeholders.

### 2.2 Key Features

- **✓ User authentication with role-based access control**
- **✓ Complete prescription lifecycle management**
- **✓ Medical inventory tracking and management**
- **✓ Digital wallet payment system with transaction history**
- **✓ Patient medical records and history**
- **✓ Doctor-patient consultation system with messaging**
- **✓ Enhanced console user interface with emojis and color**
- **✓ Comprehensive reporting and analytics**
- **✓ File-based data persistence using serialization**

### 2.3 User Roles

#### 2.3.1 Patients
- **✓ Create and manage personal accounts**
- **✓ View medical history and prescriptions**
- **✓ Request and participate in doctor consultations**
- **✓ Order medicines from the pharmacy**
- **✓ Manage digital wallet for secure payments**

#### 2.3.2 Doctors
- **✓ Create and manage digital prescriptions**
- **✓ Maintain comprehensive patient medical records**
- **✓ Participate in consultations with patients**
- **✓ View patient medication history**
- **✓ Send prescriptions directly to pharmacy**

#### 2.3.3 Pharmacists
- **✓ Manage medicine inventory with expiration tracking**
- **✓ Process and fill doctor prescriptions**
- **✓ Handle patient medicine orders**
- **✓ Update medicine information and stock levels**

#### 2.3.4 Administrators
- **✓ Manage system users and access rights**
- **✓ Generate comprehensive system reports**
- **✓ Configure system settings and parameters**
- **✓ Monitor system performance and usage**

### 2.4 Technologies Used

- **✓ Programming Language**: Java
- **✓ Data Persistence**: Java Serialization (Object streams)
- **✓ Project Structure**: Object-Oriented Architecture
- **✓ Security Features**: Password hashing and validation
- **✓ IDE**: NetBeans
- **✓ Documentation**: JavaDoc, Markdown

## 3. System Architecture

### 3.1 Architectural Overview

The EL-TA3BAN Pharmacy Management System implements a modern service-oriented architecture with clear separation of concerns and layers. This design approach ensures that the system is maintainable, extensible, and follows established software engineering principles.

Our architecture consists of four distinct layers:

1. **✓ Model Layer**: Core data entities that represent the business domain
   - User hierarchy (Admin, Patient, Doctor, Pharmacist)
   - Healthcare entities (Medicine, Prescription, Order, etc.)
   - Financial components (Wallet, Transaction)

2. **✓ Service Layer**: Business logic implementation
   - PharmacyService: Central workflow coordination
   - AuthenticationService: Security and user management
   - Role-specific services (AdminService, DoctorService, etc.)

3. **✓ Utility Layer**: Helper functions and tools
   - FileHandler: Data persistence operations
   - ConsoleUI: User interface components
   - SystemTools: Common utilities and validations

4. **✓ Persistence Layer**: Data storage and retrieval
   - Text file storage for basic entities
   - Serialization for complex objects like wallets
   - In-memory caching for performance

### 3.2 Class Hierarchy and Structure

![Class Diagram - EL-TA3BAN Pharmacy System](https://raw.githubusercontent.com/your-repo/el-ta3ban-pharmacy/main/docs/class-diagram.png)

The above class diagram illustrates the complete structure of our pharmacy management system, showing all classes and their relationships. The diagram follows standard UML notation:
- Solid lines with arrows represent inheritance
- Dotted lines represent implementation
- Lines with diamonds represent composition/aggregation
- Simple lines represent associations

#### 3.2.1 Core Class Hierarchy

- **✓ User (Abstract Parent Class)**
    - **✓ Admin**: System administration and oversight
    - **✓ Patient**: Healthcare service recipients 
    - **✓ Doctor**: Medical providers and prescription creators
    - **✓ Pharmacist**: Medicine dispensers and inventory managers
- **✓ Medicine**: Pharmaceutical products with metadata
- **✓ Prescription**: Medical orders created by doctors
- **✓ Order**: Patient requests for medicines
- **✓ Pharmacy**: Physical or virtual medicine dispensary
- **✓ Consultation**: Doctor-patient communication channel
- **✓ Message**: Individual communications within consultations
- **✓ MedicalReport**: Clinical documentation and history
- **✓ Wallet**: Digital payment system for patients
    - **✓ Transaction**: Inner class for financial records
    - **✓ Card**: Inner class for payment methods

### 3.3 Key Relationships and Associations

#### 3.3.1 Primary Class Relationships

| Class Relationship | Type | Cardinality | Description |
|-------------------|------|-------------|-------------|
| User → Roles | Inheritance | 1 : 4 | One abstract User class inherited by all role classes |
| Patient ↔ Prescription | Association | 1 : * | Patient receives multiple prescriptions |
| Doctor ↔ Prescription | Association | 1 : * | Doctor creates multiple prescriptions |
| Pharmacist ↔ Pharmacy | Association | * : 1 | Multiple pharmacists staff one pharmacy |
| Prescription ↔ Medicine | Association | * : * | Many prescriptions contain many medicines |
| Patient ↔ Wallet | Composition | 1 : 1 | Each patient has one dedicated wallet |
| Wallet ↔ Transaction | Composition | 1 : * | Wallet contains multiple transaction records |
| Patient ↔ Consultation | Association | 1 : * | Patient can participate in multiple consultations |
| Doctor ↔ Consultation | Association | 1 : * | Doctor can conduct multiple consultations |

#### 3.3.2 Detailed Relationship Types

| Relationship | Type | Description |
| --- | --- | --- |
| **✓ User → Role Classes** | Inheritance | "is-a" relationship with shared base functionality |
| **✓ Patient → Wallet** | Composition | Strong ownership where wallet cannot exist without patient |
| **✓ Wallet → Transaction** | Composition | Transactions belong to and exist only within a wallet context |
| **✓ Wallet → Card** | Composition | Cards are owned by and managed within a wallet |
| **✓ Patient ↔ Order** | Association | Bidirectional relationship without lifecycle dependency |
| **✓ Doctor ↔ Prescription** | Association | Creation relationship without strict ownership |
| **✓ Prescription ↔ Medicine** | Many-to-Many | Medicines can be in multiple prescriptions with quantities |
| **✓ Consultation ↔ Message** | Composition | Messages belong to and cannot exist outside consultations |
| **✓ All Models → Serializable** | Implementation | Interface implementation for data persistence |

### 3.4 Design Patterns Implemented

The system implements several design patterns to improve code quality and maintainability:

1. **✓ Singleton Pattern**: Used in service classes to ensure single instances
2. **✓ Factory Method**: Implemented in user creation processes
3. **✓ Observer Pattern**: Used in notification systems
4. **✓ Composite Pattern**: Implemented in menu structures
5. **✓ Strategy Pattern**: Used in payment processing systems

## 4. Implementation Details

### 4.1 Core Features Implementation

The EL-TA3BAN Pharmacy Management System incorporates modern Java features and object-oriented programming principles to deliver a robust, scalable healthcare application.

#### 4.1.1 User Management System
- **Authentication**: Secure login with password hashing and salting
- **Session Management**: User role validation and permission control
- **Profile Management**: User profile creation, editing, and management
- **Security Features**: Login attempt limiting and security question recovery

#### 4.1.2 Prescription Management
- **Digital Prescription Creation**: Doctors create and manage prescriptions
- **Medication Integration**: Adding multiple medicines with dosage information
- **Workflow Management**: Tracking prescription status from creation to fulfillment
- **Prescription Validation**: Ensuring all required elements are present and valid

#### 4.1.3 Wallet Payment System
- **Balance Management**: Tracking and updating wallet balances
- **Transaction Records**: Comprehensive history with timestamps
- **Payment Processing**: Secure payment transactions for prescriptions
- **Card Management**: Adding and managing payment methods

### 4.2 Java Technology Utilization

#### 4.2.1 Java Collections Framework

| Collection Type | Implementation | Purpose |
| --- | --- | --- |
| **✓ `ArrayList<T>`** | User lists, Medicine catalogs | Dynamic collections of entities with efficient access |
| **✓ `Map<K,V>`** | Prescription medicine quantities | Key-value associations with O(1) lookup |
| **✓ `HashMap<K,V>`** | User authentication, Medicine lookup | Fast key-based retrieval of objects |
| **✓ `LinkedList<T>`** | Transaction records, Message queues | Efficient insertions and sequential access |
| **✓ `Set<T>`** | Unique medicine identifiers | Ensuring uniqueness of certain entity attributes |

#### 4.2.2 Object-Oriented Programming Features

| OOP Feature | Implementation | Benefits |
| --- | --- | --- |
| **✓ Inheritance** | User hierarchy, Service structure | Code reuse and polymorphic behavior |
| **✓ Polymorphism** | `displayInfo()`, `process()` methods | Dynamic behavior based on object type |
| **✓ Encapsulation** | Private fields with accessors | Data protection and validation |
| **✓ Abstraction** | Abstract User class, interfaces | Enforcing contracts and structure |
| **✓ Composition** | Wallet containing Transactions | Building complex objects from simpler ones |

#### 4.2.3 Functional Programming Elements

| Feature | Implementation | Purpose |
| --- | --- | --- |
| **✓ Lambda Expressions** | Event handling, collection processing | Concise code for operations on collections |
| **✓ Stream API** | Filtering users, aggregating data | Declarative data processing pipelines |
| **✓ Method References** | Callback implementations | Cleaner code for referring to methods |
| **✓ Functional Interfaces** | Custom callbacks and event handlers | Type-safe functional programming |

#### 4.2.4 Exception Handling and Input Validation

- **✓ Custom Exception Classes**: Domain-specific exceptions
- **✓ Try-with-resources**: For auto-closing file streams
- **✓ Multi-level Validation**: Input validation at UI, service, and model levels
- **✓ Graceful Error Recovery**: Comprehensive error handling with user feedback

## 5. User Interface Design

### 5.1 User Experience Principles

Our console-based user interface follows key UX design principles:

1. **✓ Clarity**: Clear instructions and feedback with intuitive navigation
2. **✓ Consistency**: Uniform styling and interaction patterns
3. **✓ Efficiency**: Minimal keystrokes for common operations
4. **✓ Feedback**: Immediate response to user actions
5. **✓ Error Prevention**: Input validation and confirmation for destructive operations

### 5.2 UI Features

- **✓ Color-Coded Feedback**
  - Success messages in green
  - Warnings in yellow
  - Errors in red
  - Headers in blue

- **✓ Visual Design Elements**
  - Unicode box drawing for menus and tables
  - Emojis as visual indicators for different functions
  - Progress indicators for long operations
  - Status symbols for states (✓ for complete, ⚠ for warning)


### 5.3 Key Interface Screens

#### 5.3.1 Main Menu Interface

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

#### 5.3.2 Patient Dashboard

```
╔══════════════════════════════════════════════════╗
║         PATIENT DASHBOARD: Amr Hassan            ║
╚══════════════════════════════════════════════════╝
  Wallet Balance: 450.00 LE              📅 28/03/2025
──────────────────────────────────────────────────
  [1] 💊 View My Prescriptions (2 pending)
  [2] 🛒 Order Medicine
  [3] 📋 View My Orders (1 active)
  [4] 💬 Consultations with Doctors (1 new message)
  [5] 📱 Manage My Account
  [6] 💰 Wallet Management
  [7] ↩️ Logout
──────────────────────────────────────────────────
```
## 7. Testing and Validation

### 7.1 Testing Methodology

Our comprehensive testing approach included:

1. **✓ Unit Testing**: Testing individual components in isolation
2. **✓ Integration Testing**: Verifying module interactions
3. **✓ System Testing**: Testing the complete application
4. **✓ User Acceptance Testing**: Validation against requirements

### 7.2 Test Scenarios and Results

#### 7.2.1 User Authentication and Registration

| Test Case | Description | Result |
|-----------|-------------|--------|
| **✓ Valid Registration** | Creating new patient with proper information | PASSED |
| **✓ Duplicate Username** | Attempt to create account with existing username | PASSED (Prevented) |
| **✓ Password Validation** | Testing password strength requirements | PASSED |
| **✓ Valid Login** | Authentication with correct credentials | PASSED |
| **✓ Invalid Login** | Authentication attempts with incorrect credentials | PASSED (Prevented) |
| **✓ Login Limiting** | Block after multiple failed attempts | PASSED |

#### 7.2.2 Prescription Workflow

| Test Case | Description | Result |
|-----------|-------------|--------|
| **✓ Prescription Creation** | Doctor creates new prescription | PASSED |
| **✓ Medicine Addition** | Adding medicines to prescription | PASSED |
| **✓ Pharmacy Transmission** | Sending prescription to pharmacy | PASSED |
| **✓ Pharmacist Processing** | Pharmacist reviews and fills prescription | PASSED |
| **✓ Inventory Update** | Automatic stock adjustment | PASSED |
| **✓ Patient Notification** | Patient can view completed prescription | PASSED |

#### 7.2.3 Wallet and Payment Processing

| Test Case | Description | Result |
|-----------|-------------|--------|
| **✓ Wallet Creation** | New wallet creation for patient | PASSED |
| **✓ Deposit Processing** | Adding funds to wallet | PASSED |
| **✓ Payment Processing** | Making payment for order/prescription | PASSED |
| **✓ Insufficient Funds** | Handling payment with insufficient balance | PASSED |
| **✓ Transaction History** | Recording and retrieving transaction history | PASSED |
| **✓ Card Management** | Adding and using payment cards | PASSED |

## 8. Key Achievements

### 8.1 Project Accomplishments

The EL-TA3BAN Pharmacy Management System successfully implemented:

- **✓ Complete Healthcare Workflow**: Seamless interaction between doctors, patients and pharmacists
- **✓ Security and Compliance**: Password hashing, attempt limiting, and data protection
- **✓ Egyptian Localization**: Culturally relevant sample data and currency (LE)
- **✓ Enhanced User Experience**: Interactive console interface with visual enhancements
- **✓ Comprehensive Documentation**: Auto-generated system documentation

### 8.2 Innovation and Extensions

Beyond the core requirements, our implementation includes:

- **✓ Advanced Consultation System**: Built-in messaging between doctors and patients
- **✓ Medical Reports Framework**: Structured medical documentation
- **✓ Wallet-Based Payment**: Secure digital wallet with transaction history
- **✓ Enhanced Visualization**: Color-coded interface with intuitive navigation
- **✓ Prescription Analytics**: Tracking and reporting on prescription patterns

### 8.3 Future Expansion Possibilities

The system's modular design allows for future enhancement with:

- **◯ Mobile Application Interface**: Extending to smartphone access
- **◯ Web-Based Frontend**: Adding browser-based access
- **◯ Relational Database Integration**: Scaling up data storage
- **◯ Insurance Processing**: Adding medical insurance integration
- **◯ Advanced Analytics**: Business intelligence and reporting

## 9. Team Contributions

### 9.1 Development Team

| Team Member | ID | Section | Primary Contributions |
|-------------|----|---------|-----------------------|
| Mohamed Mohamed Abdelsalam | 245296 | A9 | **✓ System Architecture and Design** <br>**✓ Project Management and Integration** <br>**✓ Code Review and Quality Assurance** |
| Mazen Mohamed Masoud | 246994 | A8 | **✓ Doctor Module Implementation** <br>**✓ Prescription Workflow Development** <br>**✓ Consultation System Design** |
| Mariam Tamer Mostafa | 239279 | A7 | **✓ Patient Module Development** <br>**✓ Order Processing System** <br>**✓ UI Enhancements and Testing** |
| Nour Ahmed Ali | 241603 | A10 | **✓ Pharmacy Module Implementation** <br>**✓ Inventory Management System** <br>**✓ Data Persistence for Medicines** |
| Nouran Khaled Mohamed | 245309 | A10 | **✓ Prescription Class Development** <br>**✓ Medicine-Prescription Association** <br>**✓ Testing and Documentation** |
| Moaz Mohamed Saed | 242675 | A8 | **✓ Medicine Class Implementation** <br>**✓ Inventory System Development** <br>**✓ Expiration Date Handling** |

### 9.2 Team Workflow

Our collaborative approach included:
- **✓ Regular code reviews** for quality assurance
- **✓ Pair programming** for complex features
- **✓ Task board management** for coordinating work
- **✓ Version control** with Git for code collaboration
- **✓ Documentation standards** for consistent project documentation

## 10. Conclusion

### 10.1 Project Summary

The EL-TA3BAN Pharmacy Management System successfully implements a comprehensive healthcare solution that connects patients, doctors, and pharmacists in an integrated digital ecosystem. By employing object-oriented design principles and modern Java features, we created a system that is both robust and extensible.

### 10.2 Achievements and Outcomes

We have successfully delivered:

- **✓ A complete healthcare workflow system** with role-based access control
- **✓ Comprehensive medicine and prescription management**
- **✓ Secure user authentication and data handling**
- **✓ Digital wallet payment processing with transaction history**
- **✓ Enhanced console user interface with visual enhancements**
- **✓ Egyptian-localized implementation with appropriate terminology and currency**

### 10.3 Learning Outcomes

This project has strengthened our understanding of:

- **✓ Object-oriented design principles** and their practical application
- **✓ Software architecture** and system integration
- **✓ Data persistence strategies** for different types of information
- **✓ User experience design** even within console-based interfaces
- **✓ Security implementation** for user data and authentication
- **✓ Collaborative software development** using version control

### 10.4 Final Remarks

The EL-TA3BAN Pharmacy Management System stands as a testament to effective application of software engineering principles in creating practical solutions to real-world problems. By digitalizing pharmacy operations and connecting healthcare stakeholders, we've built a system that demonstrates the potential of technology to streamline processes and improve service delivery in the healthcare sector.