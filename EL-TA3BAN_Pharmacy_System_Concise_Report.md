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

By digitalizing pharmacy processes, the system reduces errors, improves patient care, and enhances operational efficiency. The significance of this project lies in its ability to create a cohesive healthcare ecosystem where information flows seamlessly between stakeholders, ensuring better patient outcomes.

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
1. **Patients**: Account management, prescription viewing, consultations, digital wallet
2. **Doctors**: Digital prescriptions, medical records, patient consultations
3. **Pharmacists**: Inventory management, prescription fulfillment, order processing
4. **Administrators**: System management, reporting, configuration

#### Main Features:
- **Prescription Management**: Creation, transmission, filling, and tracking
- **Inventory Control**: Stock levels, expiration dates, and reordering
- **Patient Records**: Medical history and prescription archive
- **Consultation System**: Secure messaging between doctors and patients
- **Payment Processing**: Integrated digital wallet with transaction history
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

### 4.2 Class Relationships

#### User Hierarchy Relationships
- **User (Abstract Parent Class)**:
    - **Admin (1:1)**: System management capabilities
    - **Patient (1:1)**: Healthcare service recipient 
    - **Doctor (1:1)**: Medical consultation provider
    - **Pharmacist (1:1)**: Medication management

#### Key Relationship Types

| Relationship | Relationship Type | Notes |
| --- | --- | --- |
| User → Admin/Patient/Doctor/Pharmacist | Inheritance | "is-a" relationship where subclasses inherit from User abstract class |
| Patient → Wallet | Composition | Wallet is part of Patient and destroyed when Patient is deleted |
| Pharmacy → Pharmacist | Aggregation | Pharmacy contains pharmacists, but pharmacists can exist independently |
| Consultation → Message | Composition | Messages are integral parts of a Consultation |
| All Model Classes → Serializable | Implementation | Interface implementation for data persistence |

## 5. Implementation Details

### 5.1 Java Collections Used

| Collection | Used In | Purpose |
| --- | --- | --- |
| `ArrayList<T>` | User classes, Service classes | Storing lists of objects (medicines, orders, prescriptions, users) |
| `Map<K,V>` | Prescription class | Storing medicine-quantity pairs in prescriptions |
| `HashMap<K,V>` | Prescription, Pharmacy classes | Implementing medicine-quantity associations |

### 5.2 Object-Oriented Programming Features

| Feature | Used In | Purpose |
| --- | --- | --- |
| Inheritance | User hierarchy | Creating specialized user types |
| Polymorphism | displayInfo() | Different display for different objects |
| Encapsulation | All model classes | Private fields with public getters/setters |
| Abstraction | User as abstract class | Forcing subclasses to implement methods |

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
```

## 7. Data Persistence Strategy

The system implements data persistence using Java's serialization mechanism:

- All model classes implement the `Serializable` interface
- The `FileHandler` utility class manages file operations
- Data is stored in separate `.dat` files for different entity types
- Files are automatically created on first run if they don't exist
- Serialization allows for complete object graphs to be persisted

## 8. Testing and Validation

### 8.1 Testing Approach
- Functional testing of all user workflows
- Security testing of authentication mechanisms
- Data persistence validation
- Edge case handling
- User interface validation

### 8.2 Test Scenarios Implemented
1. **Patient Registration and Authentication**
2. **Prescription Workflow Testing**
3. **Consultation System Testing**
4. **Wallet Transaction Testing**

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
- Complete implementation of all required functionality
- Robust object-oriented design with clear separation of concerns
- Enhanced security features including password hashing
- Comprehensive Egyptian localization of user names, medicines, and currency (LE)
- Full healthcare workflow between doctors, pharmacists, and patients
- Local wallet-based payment system for seamless transactions
- Enhanced console interface with colors and intuitive navigation

### 10.2 Future Enhancements
- Graphical user interface implementation
- Database integration for improved scalability
- Mobile application for patient access
- Advanced reporting and analytics
- Electronic prescription signature verification

## 11. References

1. Java Documentation - [https://docs.oracle.com/en/java/](https://docs.oracle.com/en/java/)
2. Object-Oriented Programming Concepts - [https://www.oracle.com/java/technologies/java-object-oriented-design.html](https://www.oracle.com/java/technologies/java-object-oriented-design.html)

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