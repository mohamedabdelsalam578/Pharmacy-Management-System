# EL-TA3BAN Pharmacy Management System - Code Documentation Standards

This document outlines the standardized approach to code documentation used throughout the EL-TA3BAN Pharmacy Management System. Following these standards ensures consistent, educational, and informative code documentation that aligns with the project's goals.

## Purpose

The documentation standards aim to:
1. Create a codebase that serves as an educational resource for first-year programming students
2. Demonstrate OOP concepts clearly within the code itself
3. Maintain consistency across all files
4. Ensure code is both readable and self-explanatory
5. Provide cultural context with Egyptian localization

## Class Documentation Template

All classes must have a header comment following this format:

```java
/**
 * 📝 [CLASS_NAME] - [ONE_LINE_DESCRIPTION] 📝
 * 
 * [DETAILED_DESCRIPTION_2-3_LINES]
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - [OOP_CONCEPT_1]: [BRIEF_EXPLANATION]
 * - [OOP_CONCEPT_2]: [BRIEF_EXPLANATION]
 * - [OOP_CONCEPT_3]: [BRIEF_EXPLANATION]
 * 
 * 📚 Class Responsibilities:
 * - [RESPONSIBILITY_1]
 * - [RESPONSIBILITY_2]
 * - [RESPONSIBILITY_3]
 * 
 * 🌐 Role in System:
 * [EXPLANATION_OF_HOW_CLASS_FITS_IN_SYSTEM]
 */
```

## Method Documentation Template

All non-trivial methods must be documented using this format:

```java
/**
 * 🔄 [METHOD_NAME] - [ONE_LINE_DESCRIPTION]
 * 
 * [DETAILED_DESCRIPTION_IF_NEEDED]
 * 
 * @param [PARAM_NAME] [PARAM_DESCRIPTION]
 * @return [RETURN_DESCRIPTION]
 * @throws [EXCEPTION] [EXCEPTION_DESCRIPTION]
 */
```

## Emoji Usage Guide

Emojis should be used consistently to visually identify different parts of the codebase:

| Emoji | Usage Context |
|-------|---------------|
| 👤 | User-related classes and methods |
| 💊 | Medicine-related classes and methods |
| 🏥 | Healthcare facility-related classes and methods |
| 📋 | Prescription-related classes and methods |
| 🛒 | Order-related classes and methods |
| 👨‍⚕️ | Doctor-related classes and methods |
| 👩‍⚕️ | Pharmacist-related classes and methods |
| 🔐 | Authentication and security features |
| 💰 | Payment and wallet features |
| 📊 | Reporting functions |
| ⚠️ | Warning messages |
| ✅ | Success indicators |
| ❌ | Error messages |
| 🔄 | Process/workflow related functions |
| 🧾 | Transaction-related functions |
| 📋 | List or collection handling |
| 💻 | UI/Console-related functions |

## Example Documentation

### Class Documentation Example

```java
/**
 * 💰 Wallet - Financial management component for patient accounts 💰
 * 
 * The Wallet class represents a virtual financial account within the EL-TA3BAN
 * pharmacy system. It handles all money-related operations for patients including
 * deposits, withdrawals, and payment processing with credit cards.
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - Encapsulation: All financial data is protected with private fields
 * - Composition: Contains CreditCard objects and Transaction history
 * - Inner Classes: Uses a Transaction class to track financial operations
 * 
 * 📚 Class Responsibilities:
 * - Maintain accurate balance tracking for patients
 * - Process secure financial transactions
 * - Store and manage saved payment methods
 * - Provide a complete transaction history
 * 
 * 🌐 Role in System:
 * This class enables the payment functionality throughout the application,
 * allowing patients to pay for medications, consultations, and services
 * using either their wallet balance or saved payment cards.
 */
```

### Method Documentation Example

```java
/**
 * 💳 addCard - Securely stores a payment card in the patient's wallet
 * 
 * This method demonstrates secure handling of sensitive financial data
 * by implementing card masking and duplicate detection. The system only
 * stores partial card information for security best practices.
 * 
 * @param cardNumber Full credit card number (will be masked in storage)
 * @param cardHolderName Name of the Egyptian card holder
 * @param expiryDate Card expiration date in MM/YY format
 * @param cardType Type of card (Visa, Mastercard, Meeza, etc.)
 * @return True if card added successfully, false if duplicate detected
 */
```

## Implementation Checklist

- [ ] All model classes (User, Patient, Doctor, etc.)
- [ ] All service classes (AuthenticationService, PharmacyService, etc.)
- [ ] Utility classes (FileHandler, ConsoleUI, etc.)
- [ ] Main application class

## Documentation Update Process

1. Use the templates from `tools/documentation_updater.sh`
2. Apply to one file at a time, ensuring accuracy
3. Verify all parameters and return values are documented
4. Commit documentation updates separately from functional changes

## Maintenance

These documentation standards should be maintained as the project evolves. Any new classes or methods should follow these guidelines from the start.