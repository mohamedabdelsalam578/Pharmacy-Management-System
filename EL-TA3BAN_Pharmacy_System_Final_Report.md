<div align="center">

#  EL-TA3BAN Pharmacy Management System 
###  Phase 1 Report

![Pharmacy Logo](elta3ban-logo.png)

*Your Health, Our Priority in Digital Pharmacy Solutions*

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

| Field              | Details                                  |
|--------------------|------------------------------------------|
| Title              | EL-TA3BAN Pharmacy Management System     |
| Course             | Programming in Java                      |
| Code               | 24CSCI04C                                |
| University         | British University in Egypt (BUE)        |
| Faculty            | Informatics and Computer Science         |
| Year               | 2024/2025                                |
| Due Date           | March 28, 2025                           |

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

The EL-TA3BAN Pharmacy Management System is a Java application designed to streamline medical documentation and improve collaboration among doctors, pharmacists, and patients. It implements full healthcare workflow.

By digitalizing pharmacy operations, the system enhances patient care, operational efficiency, and reduces the possibility of errors. It facilitates a cohesive healthcare environment by enabling seamless information exchange among all parties.

### 2.2 Key Features

- **✓ Role-based authentication**
- **✓ Prescription management**
- **✓ Inventory control system**
- **✓ Digital wallet & transactions**
- **✓ Medical records management**
- **✓ Doctor-patient messaging**
- **✓ Interactive console UI**
- **✓ Reporting & analytics**
- **✓ Serialization for data storage**

### 2.3 User Roles

#### 2.3.1 Patients
- **✓ Manage accounts**
- **✓ Access medical history**
- **✓ Request consultations**
- **✓ Order medicines**
- **✓ Use a digital wallet**

#### 2.3.2 Doctors
- **✓ Digital prescriptions**
- **✓ Patient records**
- **✓ Consult with patients**
- **✓ Access medication history**
- **✓ Send prescriptions**

#### 2.3.3 Pharmacists
- **✓ Inventory management**
- **✓ Fill prescriptions**
- **✓ Process orders**
- **✓ Update stock**

#### 2.3.4 Administrators
- **✓ Manage users & access**
- **✓ Generate reports**
- **✓ System settings**
- **✓ Monitor performance**

### 2.4 Technologies Used

- **✓ Java**
- **✓ Serialization**
- **✓ OOP Architecture**
- **✓ Security:** Hashing
- **✓ IDE:** NetBeans
- **✓ Documentation:** JavaDoc

## 3. System Architecture

### 3.1 Architectural Overview

The system employs a service-oriented architecture, ensuring maintainability and extensibility through four layers:

1. **Model Layer**: Defines core data entities like User, Medicine, and Financial components.

2. **Service Layer**: Implements business logic via services such as PharmacyService and AuthenticationService.

3. **Utility Layer**: Offers helper functions, including FileHandler for data operations and ConsoleUI for user interface.

4. **Persistence Layer**: Manages data storage with text files, serialization, and in-memory caching.

### 3.2 Class Hierarchy and Structure

![Class Diagram - EL-TA3BAN Pharmacy System](https://raw.githubusercontent.com/your-repo/el-ta3ban-pharmacy/main/docs/class-diagram.png)

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




## 4. Implementation Details

### 4.1 Java Technology Utilization

#### 4.1.1 Java Collections Framework

| Collection Type | Implementation | Purpose |
| --- | --- | --- |
| **✓ `ArrayList<T>`** | User lists, Medicine catalogs | Dynamic collections of entities with efficient access |
| **✓ `Map<K,V>`** | Prescription medicine quantities | Key-value associations with O(1) lookup |
| **✓ `HashMap<K,V>`** | User authentication, Medicine lookup | Fast key-based retrieval of objects |
| **✓ `LinkedList<T>`** | Transaction records, Message queues | Efficient insertions and sequential access |
| **✓ `Set<T>`** | Unique medicine identifiers | Ensuring uniqueness of certain entity attributes |

#### 4.1.2 Object-Oriented Programming Features

| OOP Feature | Implementation | Benefits |
| --- | --- | --- |
| **✓ Inheritance** | User hierarchy, Service structure | Code reuse and polymorphic behavior |
| **✓ Polymorphism** | `displayInfo()`, `process()` methods | Dynamic behavior based on object type |
| **✓ Encapsulation** | Private fields with accessors | Data protection and validation |
| **✓ Abstraction** | Abstract User class, interfaces | Enforcing contracts and structure |
| **✓ Composition** | Wallet containing Transactions | Building complex objects from simpler ones |

#### 4.1.3 Functional Programming Elements

| Feature | Implementation | Purpose |
| --- | --- | --- |
| **✓ Lambda Expressions** | Event handling, collection processing | Concise code for operations on collections |
| **✓ Stream API** | Filtering users, aggregating data | Declarative data processing pipelines |
| **✓ Method References** | Callback implementations | Cleaner code for referring to methods |
| **✓ Functional Interfaces** | Custom callbacks and event handlers | Type-safe functional programming |

#### 4.1.4 Exception Handling and Input Validation

- **✓ Custom Exception Classes**: Domain-specific exceptions
- **✓ Try-with-resources**: For auto-closing file streams
- **✓ Multi-level Validation**: Input validation at UI, service, and model levels
- **✓ Graceful Error Recovery**: Comprehensive error handling with user feedback

## 5. User Interface Design



### 5.1 UI Features

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


### 5.2 Key Interface Screens

#### 5.2.1 Main Menu Interface

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

#### 5.2.2 Patient Dashboard

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


## 6. Team Contributions

| Team Member | ID | Section | Primary Contributions |
|-------------|----|---------|-----------------------|
| Mohamed Mohamed Abdelsalam | 245296 | A9 | **✓ Contributed to every aspect of the project** |
| Mazen Mohamed Masoud | 246994 | A8 | **✓ Pharmacy Class Implementation** <br> **✓ Class Diagram Contributions** |
| Mariam Tamer Mostafa | 239279 | A7 | **✓ Admin Class Implementation** <br>**✓ Class Diagram Contributions** |
| Nour Ahmed Ali | 241603 | A10 | **✓ Doctor Class Implementation**<br>**✓ Class Diagram Contributions** |
| Nouran Khaled Mohamed | 245309 | A10 | **✓ Prescription Class Implementation** <br>**✓ Class Diagram Contributions** |
| Moaz Mohamed Saed | 242675 | A8 | **✓ Large sections of the class diagram** <br>**✓ Medicine Class Implementation** <br>**✓ Pharmacist Class Implementation** |

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

