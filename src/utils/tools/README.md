# EL-TA3BAN Pharmacy System: Utility Tools

## Overview
This package provides centralized utility functions and tools for the EL-TA3BAN Pharmacy System. 
It follows a simple, straightforward organization with focused utility classes that each serve a specific purpose.

## Package Contents

### SystemTools.java
The primary utility class containing essential functions organized by category:
- 🔒 SECURITY: Password hashing, verification, and secure tokens
- 📅 DATES: Date and time formatting utilities
- 🧮 FORMATS: Currency, text formatting functions
- 🎲 GENERATORS: ID generation and reference numbers
- ✅ VALIDATORS: Input validation for common data types

## Design Philosophy
This tools package follows these design principles:
1. **Simplicity**: Functions are straightforward and easy to understand
2. **Organization**: Related functions are grouped together by category
3. **Reusability**: Common utility functions can be used throughout the system
4. **Clarity**: Method names clearly indicate their purpose
5. **Documentation**: All functions have clear documentation

## Usage Examples

### Password Security
```java
// Hash a password
String hashedPassword = SystemTools.hashPassword("secretpassword");

// Verify a password
boolean isValid = SystemTools.verifyPassword("secretpassword", hashedPassword);
```

### Date Formatting
```java
// Format a date for display
String formattedDate = SystemTools.formatDate(LocalDate.now());

// Format currency
String price = SystemTools.formatCurrency(199.99);
```

### Input Validation
```java
// Validate email format
boolean isValidEmail = SystemTools.isValidEmail("user@example.com");

// Validate text length
boolean isValidName = SystemTools.isValidLength(name, 2, 50);
```

These tools help maintain consistency throughout the application and reduce code duplication.