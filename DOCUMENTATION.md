# Pharmacy Management System Documentation

## Table of Contents
1. [System Overview](#system-overview)
2. [Model Classes](#model-classes)
3. [Service Classes](#service-classes)
   - [Admin Service](#4-admin-service-functions)
   - [Doctor Service](#5-doctor-service-functions)
   - [Pharmacist Service](#6-pharmacist-service-functions)
   - [Patient Service](#7-patient-service-functions)
   - [Authentication Service](#8-authentication-service-functions)
   - [Pharmacy Service](#9-pharmacyservice-functions)
4. [Utility Classes](#utility-classes)
   - [FileHandler](#10-filehandler-functions)
5. [Main Application](#main-application)
6. [Payment System](#payment-system)
   - [Wallet](#1-wallet-class-functions)
   - [CreditCard](#2-creditcard-class-functions)
   - [Transaction](#3-transaction-class-functions)
7. [Prescription Workflow](#prescription-workflow)
   - [Workflow Stages](#prescription-workflow-stages)
   - [Key Classes](#key-classes-in-prescription-workflow)
8. [Data Flow Diagram](#data-flow-diagram)

## System Overview

This documentation provides details about all classes and functions in the EL-TA3BAN Pharmacy Management System. The system follows object-oriented design principles to create a comprehensive healthcare management system connecting patients, doctors, and pharmacists. The system includes a complete healthcare workflow with integrated payment processing through a secure wallet system, prescription management, and inventory tracking.

The system now runs in extended mode by default, providing access to all features without requiring any command line arguments. This includes the complete healthcare workflow between doctors, pharmacists, and patients, with Egyptian localization of all sample data (names, medicines, and currency in LE).

## Model Classes

### User Class Hierarchy

| Class | Description | Key Functions |
|-------|-------------|---------------|
| `User` | Abstract base class for all users | `displayInfo()`, getters/setters for user attributes |
| `Admin` | System administrator with elevated privileges | `displayInfo()`, `generateReport()` |
| `Patient` | Represents patients in the healthcare system | `displayInfo()`, `addOrder()`, `getOrders()`, `getPrescriptions()`, `getWallet()`, `addCreditCard()` |
| `Doctor` | Medical professionals who create prescriptions | `displayInfo()`, `createPrescription()`, `getSpecialization()`, `getIssuedPrescriptions()` |
| `Pharmacist` | Pharmacy staff who process prescriptions | `displayInfo()`, `getPharmacy()`, `processOrder()`, `getFilledPrescriptions()` |

### Healthcare Entities

| Class | Description | Key Functions |
|-------|-------------|---------------|
| `Medicine` | Pharmaceutical products available in the system | `displayInfo()`, `isExpired()`, `updateStock()`, getters/setters, `getCategory()`, `isRequiresPrescription()` |
| `Prescription` | Medical orders created by doctors for patients | `addMedicine()`, `removeMedicine()`, `calculateTotalCost()`, `isExpired()`, `displayInfo()`, `getPharmacyId()`, `setPharmacyId()` |
| `Order` | Patient requests for medicines | `addMedicine()`, `cancelOrder()`, `getTotalCost()`, `getStatus()`, `displayInfo()`, `getPaymentMethod()`, `setPaymentMethod()` |
| `MedicalReport` | Patient health information and diagnosis | `displayInfo()`, `addPrescription()`, `getNotes()`, getters/setters |
| `Consultation` | Doctor-patient interactions and appointments | `displayInfo()`, `addMessage()`, `getMessages()`, `getStatus()` |
| `Message` | Communication between doctors and patients | `displayInfo()`, `getSender()`, `getContent()`, `getTimestamp()` |
| `Pharmacy` | Represents physical pharmacy location | `addPharmacist()`, `addMedicine()`, `processPrescription()`, `updateInventory()`, `findMedicineById()`, `getCategorizedMedicines()` |
| `Wallet` | Patient's digital wallet for payments | `getBalance()`, `deposit()`, `withdraw()`, `getTransactionHistory()`, `addCreditCard()`, `removeCreditCard()` |
| `CreditCard` | Credit card information for payments | `getCardNumber()`, `getMaskedCardNumber()`, `getCardHolderName()`, `getExpiryDate()`, `isValid()` |
| `Transaction` | Records of wallet financial operations | `getAmount()`, `getTimestamp()`, `getType()`, `getDescription()`, `getBalance()` |

## Service Classes

| Class | Description | Key Functions |
|-------|-------------|---------------|
| `AdminService` | Handles admin operations | `addMedicine()`, `removeMedicine()`, `updateMedicine()`, `generateReport()`, `adminMenu()` |
| `PatientService` | Manages patient functionality | `createPatient()`, `loginPatient()`, `placeOrder()`, `viewOrders()`, `patientMenu()`, `depositToWallet()`, `withdrawFromWallet()`, `payWithWallet()`, `addCreditCard()`, `removeCreditCard()`, `payWithCreditCard()` |
| `DoctorService` | Manages doctor operations | `loginDoctor()`, `createPrescription()`, `viewDoctorPrescriptions()`, `addMedicineToPrescription()`, `sendPrescriptionToPharmacy()`, `createMedicalReport()`, `doctorMenu()` |
| `PharmacistService` | Handles pharmacist functionality | `loginPharmacist()`, `fillPrescription()`, `getPendingPrescriptions()`, `getFilledPrescriptions()`, `addMedicineToPharmacy()`, `manageInventory()`, `pharmacistMenu()` |
| `PharmacyService` | Core service integrating all functionality with complete healthcare workflow | `run()`, `loginUser()`, `initializeSystem()`, `mainMenu()` |
| `AuthenticationService` | Security service for user login and account management | `loginUser()`, `registerUser()`, `validatePassword()`, `hashPassword()`, `validateLoginAttempts()` |

## Utility Classes

| Class | Description | Key Functions |
|-------|-------------|---------------|
| `DataInitializer` | Creates test data for the system | `initializeAndTest()`, `initializeExtendedSystem()`, `createTestData()` |
| `FileHandler` | Manages data persistence | `initializeFiles()`, `loadUsers()`, `saveMedicines()`, `loadOrders()`, various load/save methods |

## Main Application

| Class | Description | Key Functions |
|-------|-------------|---------------|
| `PharmacyManagementSystem` | Application entry point | `main()` - Starts the system in extended mode by default (with full healthcare workflow and Egyptian localization); basic mode available with `--basic` flag |

## Detailed Function Documentation

### 1. User Class Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `User(int id, String name, String username, String password, String email, String phoneNumber)` | Constructor initializing user data | User attributes | - |
| `getId()` | Gets user ID | - | `int` |
| `getName()` | Gets user's name | - | `String` |
| `getUsername()` | Gets login username | - | `String` |
| `getPassword()` | Gets user password | - | `String` |
| `getEmail()` | Gets user email | - | `String` |
| `getPhoneNumber()` | Gets user phone number | - | `String` |
| `setId(int id)` | Updates user ID | New ID | `void` |
| `setName(String name)` | Updates user name | New name | `void` |
| `setUsername(String username)` | Updates username | New username | `void` |
| `setPassword(String password)` | Updates password | New password | `void` |
| `setEmail(String email)` | Updates email | New email | `void` |
| `setPhoneNumber(String phoneNumber)` | Updates phone number | New phone | `void` |
| `displayInfo()` | Abstract method to show user details | - | `void` |

### 2. Medicine Class Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `Medicine(int id, String name, String description, double price, int quantity, String expiryDate)` | Constructor initializing medicine | Medicine attributes | - |
| `getId()` | Gets medicine ID | - | `int` |
| `getName()` | Gets medicine name | - | `String` |
| `getDescription()` | Gets medicine description | - | `String` |
| `getPrice()` | Gets price in LE | - | `double` |
| `getQuantity()` | Gets available quantity | - | `int` |
| `getExpiryDate()` | Gets expiry date | - | `String` |
| `setPrice(double price)` | Updates price | New price | `void` |
| `setQuantity(int quantity)` | Updates stock quantity | New quantity | `void` |
| `updateStock(int quantity)` | Adds or removes from stock | Quantity change (+ or -) | `boolean` |
| `isExpired()` | Checks if medicine is expired | - | `boolean` |
| `displayInfo()` | Shows medicine details | - | `void` |

### 3. Prescription Class Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `Prescription(int id, int patientId, int doctorId, LocalDate issueDate, LocalDate expiryDate, String status, String instructions)` | Full constructor | Prescription attributes | - |
| `Prescription(int id, int patientId, int doctorId, LocalDate issueDate, LocalDate expiryDate, String instructions)` | Constructor with default "Pending" status | Prescription attributes | - |
| `getId()` | Gets prescription ID | - | `int` |
| `getPatientId()` | Gets patient ID | - | `int` |
| `getDoctorId()` | Gets doctor ID | - | `int` |
| `getIssueDate()` | Gets date issued | - | `LocalDate` |
| `getExpiryDate()` | Gets expiry date | - | `LocalDate` |
| `getStatus()` | Gets current status | - | `String` |
| `setStatus(String status)` | Updates status | New status | `void` |
| `getInstructions()` | Gets usage instructions | - | `String` |
| `setInstructions(String instructions)` | Updates instructions | New instructions | `void` |
| `getMedicines()` | Gets all medicines and quantities | - | `Map<Medicine, Integer>` |
| `addMedicine(Medicine medicine, int quantity)` | Adds medicine to prescription | Medicine, quantity | `boolean` |
| `removeMedicine(int medicineId)` | Removes medicine from prescription | Medicine ID | `boolean` |
| `calculateTotalCost()` | Calculates prescription cost | - | `double` |
| `containsMedicine(int medicineId)` | Checks if medicine is in prescription | Medicine ID | `boolean` |
| `isExpired()` | Checks if prescription is expired | - | `boolean` |
| `displayInfo()` | Shows prescription details | - | `void` |

### 4. Admin Service Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `AdminService(List<Admin> admins, List<Medicine> medicines)` | Constructor initializing service | Admin list, medicine list | - |
| `loginAdmin(String username, String password)` | Authenticates admin | Username, password | `Admin` |
| `addMedicine(Medicine medicine)` | Adds new medicine to inventory | Medicine object | `boolean` |
| `removeMedicine(int id)` | Removes medicine from inventory | Medicine ID | `boolean` |
| `updateMedicine(int id, double price, int quantity)` | Updates medicine details | ID, price, quantity | `boolean` |
| `generateMedicineReport()` | Creates inventory report | - | `void` |
| `generateRevenueReport(List<Order> orders)` | Creates sales report | Order list | `void` |
| `adminMenu(Admin admin, Scanner scanner)` | Interactive admin interface | Admin user, input scanner | `void` |

### 5. Doctor Service Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `DoctorService(List<Doctor> doctors, List<Patient> patients, List<Prescription> prescriptions, List<Medicine> medicines)` | Constructor initializing service | Service dependencies | - |
| `loginDoctor(String username, String password)` | Authenticates doctor | Login credentials | `Doctor` |
| `doctorMenu(Doctor doctor)` | Shows interactive menu | Doctor | `boolean` |
| `createPrescription(int doctorId, int patientId, String instructions)` | Creates a new prescription | Doctor ID, Patient ID, instructions | `Prescription` |
| `addMedicineToPrescription(int doctorId, int prescriptionId, int medicineId, int quantity)` | Adds medicine to a prescription | Doctor ID, Prescription ID, Medicine ID, quantity | `boolean` |
| `sendPrescriptionToPharmacy(int doctorId, int prescriptionId, int pharmacyId)` | Sends prescription to pharmacy | Doctor ID, Prescription ID, Pharmacy ID | `boolean` |
| `viewDoctorPrescriptions(int doctorId)` | Views prescriptions created by doctor | Doctor ID | `void` |
| `updateDoctorProfile(Doctor doctor)` | Updates doctor information | Doctor | `void` |
| `viewPatientsList()` | Shows all patients | - | `void` |
| `viewMedicinesList()` | Shows all available medicines | - | `void` |
| `createMedicalReport(int doctorId, int patientId, String diagnosis, String treatment)` | Creates medical report | Doctor ID, Patient ID, diagnosis, treatment | `boolean` |
| `viewPatientPrescriptions(int patientId)` | Views patient's prescriptions | Patient ID | `void` |

### 6. Pharmacist Service Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `PharmacistService(List<Pharmacist> pharmacists, List<Pharmacy> pharmacies, List<Prescription> prescriptions, List<Medicine> medicines)` | Constructor initializing service | Service dependencies | - |
| `loginPharmacist(String username, String password)` | Authenticates pharmacist | Login credentials | `Pharmacist` |
| `showPharmacistMenu(Pharmacist pharmacist)` | Shows interactive menu | Pharmacist | `boolean` |
| `fillPrescription(int pharmacistId, int prescriptionId)` | Fills a prescription | Pharmacist ID, Prescription ID | `boolean` |
| `addMedicineToPharmacy(int pharmacyId, int medicineId, int quantity)` | Adds medicine to inventory | Pharmacy ID, Medicine ID, quantity | `boolean` |
| `getPendingPrescriptions(int pharmacyId)` | Gets pending prescriptions | Pharmacy ID | `List<Prescription>` |
| `getFilledPrescriptions(int pharmacistId)` | Gets prescriptions filled by pharmacist | Pharmacist ID | `List<Prescription>` |
| `manageInventory(Pharmacy pharmacy)` | Shows inventory management menu | Pharmacy | `void` |
| `viewInventory(Pharmacy pharmacy)` | Shows pharmacy inventory | Pharmacy | `void` |
| `addStock(Pharmacy pharmacy)` | Adds stock of existing medicine | Pharmacy | `void` |
| `removeStock(Pharmacy pharmacy)` | Removes stock of existing medicine | Pharmacy | `void` |
| `addNewMedicine(Pharmacy pharmacy)` | Adds new medicine to inventory | Pharmacy | `void` |
| `updatePharmacistProfile(Pharmacist pharmacist)` | Updates pharmacist information | Pharmacist | `void` |

### 7. Patient Service Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `PatientService(List<Patient> patients, List<Medicine> medicines, List<Order> orders)` | Constructor initializing service | Service dependencies | - |
| `createPatient(String name, String username, String password, String email, String phoneNumber)` | Creates new patient account | Patient details | `Patient` |
| `loginPatient(String username, String password)` | Authenticates patient | Login credentials | `Patient` |
| `placeOrder(Patient patient, List<Medicine> medicines, List<Integer> quantities)` | Creates new order | Patient, medicines, quantities | `Order` |
| `viewOrders(Patient patient)` | Displays patient orders | Patient | `void` |
| `patientMenu(Patient patient)` | Shows interactive menu | Patient | `boolean` |
| `viewAllPrescriptions(Patient patient)` | Displays patient prescriptions | Patient | `void` |
| `depositToWallet(Patient patient, double amount)` | Adds funds to wallet | Patient, amount | `boolean` |
| `withdrawFromWallet(Patient patient, double amount)` | Removes funds from wallet | Patient, amount | `boolean` |
| `viewWallet(Patient patient)` | Displays wallet details | Patient | `void` |
| `viewTransactionHistory(Patient patient)` | Shows financial history | Patient | `void` |
| `addCreditCard(Patient patient, String cardNumber, String cardHolderName, String expiryDate, String cvv)` | Adds payment card | Patient, card details | `boolean` |
| `removeCreditCard(Patient patient, String cardNumber)` | Removes payment card | Patient, card number | `boolean` |
| `payWithWallet(Patient patient, double amount, String description)` | Processes wallet payment | Patient, amount, description | `boolean` |
| `payWithCreditCard(Patient patient, String cardNumber, double amount, String description)` | Processes card payment | Patient, card number, amount, description | `boolean` |

### 8. Authentication Service Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `AuthenticationService()` | Constructor initializing security service | - | - |
| `hashPassword(String password)` | Securely hashes password | Plain password | `String` |
| `validatePassword(String inputPassword, String storedHash)` | Verifies password | Input password, stored hash | `boolean` |
| `loginUser(String username, String password, List<? extends User> users)` | Generically authenticates users | Username, password, user list | `User` |
| `registerUser(User user, List<? extends User> users)` | Registers new user | User object, user list | `boolean` |
| `changePassword(User user, String oldPassword, String newPassword)` | Updates user password | User, old password, new password | `boolean` |
| `validateLoginAttempts(String username)` | Prevents brute force attacks | Username | `boolean` |

### 9. PharmacyService Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `PharmacyService()` | Constructor initializing pharmacy system | - | - |
| `run()` | Starts the interactive system | - | `void` |
| `mainMenu()` | Displays main menu options | - | `void` |
| `loginUser(int userType, Scanner scanner)` | Authenticates different user types | User type, input scanner | `boolean` |
| `createPatientAccount(Scanner scanner)` | Registers new patient | Input scanner | `boolean` |
| `initializeSystem()` | Sets up system with initial data | - | `void` |
| `getMedicines()` | Gets all medicines | - | `List<Medicine>` |
| `getPatients()` | Gets all patients | - | `List<Patient>` |
| `getDoctors()` | Gets all doctors | - | `List<Doctor>` |
| `getPharmacists()` | Gets all pharmacists | - | `List<Pharmacist>` |
| `getOrders()` | Gets all orders | - | `List<Order>` |
| `getPrescriptions()` | Gets all prescriptions | - | `List<Prescription>` |

### 10. FileHandler Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `initializeFiles()` | Creates data directory and files | - | `void` |
| `loadAdmins()` | Loads admin users from file | - | `List<Admin>` |
| `saveAdmins(List<Admin> admins)` | Saves admin data | Admin list | `void` |
| `loadPatients()` | Loads patient users from file | - | `List<Patient>` |
| `savePatients(List<Patient> patients)` | Saves patient data | Patient list | `void` |
| `loadDoctors()` | Loads doctor users from file | - | `List<Doctor>` |
| `saveDoctors(List<Doctor> doctors)` | Saves doctor data | Doctor list | `void` |
| `loadPharmacists()` | Loads pharmacist users from file | - | `List<Pharmacist>` |
| `savePharmacists(List<Pharmacist> pharmacists)` | Saves pharmacist data | Pharmacist list | `void` |
| `loadMedicines()` | Loads medicine inventory from file | - | `List<Medicine>` |
| `saveMedicines(List<Medicine> medicines)` | Saves medicine data | Medicine list | `void` |
| `loadOrders()` | Loads order data from file | - | `List<Order>` |
| `saveOrders(List<Order> orders)` | Saves order data | Order list | `void` |
| `loadPrescriptions()` | Loads prescription data from file | - | `List<Prescription>` |
| `savePrescriptions(List<Prescription> prescriptions)` | Saves prescription data | Prescription list | `void` |
| `loadMedicalReports()` | Loads medical report data from file | - | `List<MedicalReport>` |
| `saveMedicalReports(List<MedicalReport> reports)` | Saves medical report data | Report list | `void` |
| `loadConsultations()` | Loads consultation data from file | - | `List<Consultation>` |
| `saveConsultations(List<Consultation> consultations)` | Saves consultation data | Consultation list | `void` |
| `loadMessages()` | Loads message data from file | - | `List<Message>` |
| `saveMessages(List<Message> messages)` | Saves message data | Message list | `void` |

## Payment System

The Pharmacy Management System includes a comprehensive internal payment system using a digital wallet, allowing patients to make payments without relying on external payment gateways.

### 1. Wallet Class Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `Wallet()` | Constructor initializing wallet with zero balance | - | - |
| `getBalance()` | Gets current wallet balance | - | `double` |
| `deposit(double amount, String description)` | Adds funds to wallet | Amount, transaction description | `boolean` |
| `withdraw(double amount, String description)` | Removes funds from wallet | Amount, transaction description | `boolean` |
| `getTransactionHistory()` | Gets all wallet transactions | - | `List<Transaction>` |
| `addCreditCard(CreditCard card)` | Adds a credit card to wallet | Credit card object | `boolean` |
| `removeCreditCard(String cardNumber)` | Removes a card from wallet | Card number to remove | `boolean` |
| `getCreditCards()` | Gets all registered cards | - | `List<CreditCard>` |
| `findCardByNumber(String cardNumber)` | Finds card by number | Card number to find | `CreditCard` |
| `toString()` | String representation of wallet | - | `String` |

### 2. CreditCard Class Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `CreditCard(String cardNumber, String cardHolderName, String expiryDate, String cvv)` | Constructor initializing card | Card details | - |
| `getCardNumber()` | Gets full card number | - | `String` |
| `getMaskedCardNumber()` | Gets masked card number (only last 4 digits visible) | - | `String` |
| `getCardHolderName()` | Gets card holder name | - | `String` |
| `getExpiryDate()` | Gets card expiry date | - | `String` |
| `getCvv()` | Gets card CVV code | - | `String` |
| `isValid()` | Validates card details format | - | `boolean` |
| `toString()` | String representation with masked details | - | `String` |

### 3. Transaction Class Functions

| Function | Description | Parameters | Return Type |
|----------|-------------|------------|-------------|
| `Transaction(double amount, String type, String description, double finalBalance, LocalDateTime timestamp)` | Constructor initializing transaction | Transaction details | - |
| `getAmount()` | Gets transaction amount | - | `double` |
| `getType()` | Gets transaction type (deposit, withdraw, payment) | - | `String` |
| `getDescription()` | Gets transaction description | - | `String` |
| `getBalance()` | Gets balance after transaction | - | `double` |
| `getTimestamp()` | Gets when transaction occurred | - | `LocalDateTime` |
| `toString()` | String representation of transaction | - | `String` |

## Prescription Workflow

The Pharmacy Management System includes a complete prescription workflow from doctor to pharmacist to patient.

### Prescription Workflow Stages

1. **Creation**: Doctor creates a prescription for a patient
2. **Medication Addition**: Doctor adds medicines to the prescription
3. **Sending to Pharmacy**: Doctor sends the prescription to a selected pharmacy
4. **Pharmacist Review**: Pharmacist reviews pending prescriptions
5. **Filling Prescription**: Pharmacist checks inventory and fills the prescription
6. **Inventory Update**: Pharmacy inventory is automatically updated
7. **Status Update**: Prescription status changes to "Filled"
8. **Patient Notification**: Patient can view filled prescriptions
9. **Payment**: Patient pays for the prescription using wallet or credit card

### Key Classes in Prescription Workflow

- **Doctor**: Creates and manages prescriptions
- **Prescription**: Contains medicines, dosage, and instructions
- **Pharmacist**: Reviews and fills prescriptions
- **Pharmacy**: Contains inventory of medicines
- **Patient**: Views and pays for prescriptions
- **Medicine**: Pharmaceutical products in the prescription

## Data Flow Diagram

```
┌───────────┐       ┌───────────┐       ┌───────────┐
│   Doctor  │───────│Prescription│───────│ Pharmacist│
└───────────┘       └───────────┘       └───────────┘
      │                   │                   │
      │                   │                   │
      ▼                   ▼                   ▼
┌───────────┐       ┌───────────┐       ┌───────────┐
│MedicalReport│      │  Medicine │       │  Pharmacy │
└───────────┘       └───────────┘       └───────────┘
      │                   ▲                   │
      │                   │                   │
      ▼                   │                   ▼
┌───────────┐       ┌───────────┐       ┌───────────┐
│  Patient  │───────│   Order   │───────│   Admin   │
└───┬───────┘       └───────────┘       └───────────┘
    │                                         │
    │                                         │
    ▼                                         ▼
┌───────────┐       ┌───────────┐       ┌───────────┐
│Consultation│       │   Wallet  │       │   Report  │
└───────────┘       └─────┬─────┘       └───────────┘
                          │
                          ▼
                    ┌───────────┐
                    │CreditCard │
                    └───────────┘
```