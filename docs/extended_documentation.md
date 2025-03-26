# Extended Pharmacy Management System Documentation

*Generated on: 2025-03-25*

## Table of Contents

1. [System Overview](#system-overview)
2. [Classes](#classes)
3. [Services](#services)
4. [Healthcare Workflow](#healthcare-workflow)
5. [Data Flow](#data-flow)
6. [System Statistics](#system-statistics)

## System Overview

The Extended Pharmacy Management System is a comprehensive healthcare application that integrates pharmacy operations with doctor-patient interactions. It follows object-oriented design principles and provides functionality for different user roles including administrators, patients, doctors, and pharmacists. The system features a complete healthcare workflow from prescription creation to medicine dispensing.

## Classes

### User Class Hierarchy

| Class | Description |
|-------|-------------|
| `User` | Abstract base class for all users |
| `Admin` | System administrator with elevated privileges |
| `Patient` | Represents patients in the healthcare system |
| `Doctor` | Medical professionals who create prescriptions |
| `Pharmacist` | Pharmacy staff who process prescriptions |

### Healthcare Entities

| Class | Description |
|-------|-------------|
| `Medicine` | Pharmaceutical products available in the system |
| `Prescription` | Medical orders created by doctors for patients |
| `Order` | Patient requests for medicines |
| `MedicalReport` | Patient health information and diagnosis |
| `Pharmacy` | Represents physical pharmacy locations |
| `Consultation` | Doctor-patient interaction records |
| `Message` | Communication between healthcare providers |

## Services

| Service | Description |
|---------|-------------|
| `AdminService` | Handles admin operations and system management |
| `PatientService` | Manages patient functionality including orders |
| `DoctorService` | Manages doctor operations, prescriptions, and medical reports |
| `PharmacistService` | Handles prescription processing and inventory |
| `UpdatedPharmacyService` | Core service integrating all functionality |

## Healthcare Workflow

The system supports a complete healthcare workflow:

1. **Patient Registration**: Patients create accounts or are registered by admin
2. **Doctor Consultation**: Doctors create medical reports and prescriptions for patients
3. **Prescription Processing**: Prescriptions are sent to pharmacies
4. **Pharmacist Review**: Pharmacists validate and process prescriptions
5. **Medicine Dispensing**: Pharmacists prepare and dispense medicines
6. **Patient Collection**: Patients collect their medicines from pharmacy

### Pharmacy Operations

The system supports multiple pharmacy locations, each with its own:

- Inventory management
- Staff (pharmacists) assignments
- Prescription processing queue
- Contact information and location details

## Data Flow

```
Doctor ──────────────> Prescription ──────────────> Pharmacist
  │         creates          │      processes          │
  │                          │                         │
  ▼                          ▼                         ▼
MedicalReport         Medicine Order             Pharmacy
  │                          ▲                         │
  │                          │                         │
  └────> Patient <──────────┘           Admin <────────┘
         consults         places       manages
```

## System Statistics

- **Users:** 4
  - Administrators: 1
  - Patients: 1
  - Doctors: 1
  - Pharmacists: 1
- **Pharmacies:** 1
- **Medicines:** 5
- **Orders:** 0
- **Prescriptions:** 1

### System Features

- User authentication and role-based access control
- Prescription management with digital validation
- Inventory tracking with automatic updates
- Medical record keeping with patient history
- Multi-pharmacy support with location management
- Order processing with prescription validation
- Reporting capabilities for inventory and revenue

