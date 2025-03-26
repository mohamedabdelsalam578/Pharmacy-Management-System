#!/bin/bash
# Documentation Updater Script for EL-TA3BAN Pharmacy Management System
# This script helps standardize comments across the entire codebase
# It provides templates and guidance for updating class and method documentation

echo "=== EL-TA3BAN Pharmacy Documentation Standardizer ==="
echo ""
echo "This tool will help you apply standardized documentation to Java classes."
echo ""

# Create tools directory if it doesn't exist
mkdir -p templates

# Create a template file for standard class documentation
cat > templates/class_template.txt << 'EOF'
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
EOF

# Create a template file for standard method documentation
cat > templates/method_template.txt << 'EOF'
/**
 * 🔄 [METHOD_NAME] - [ONE_LINE_DESCRIPTION]
 * 
 * [DETAILED_DESCRIPTION_IF_NEEDED]
 * 
 * @param [PARAM_NAME] [PARAM_DESCRIPTION]
 * @return [RETURN_DESCRIPTION]
 * @throws [EXCEPTION] [EXCEPTION_DESCRIPTION]
 */
EOF

# Create emoji reference guide
cat > templates/emoji_guide.txt << 'EOF'
Emoji Usage Guide:

👤 User-related classes and methods
💊 Medicine-related classes and methods
🏥 Healthcare facility-related classes and methods 
📋 Prescription-related classes and methods
🛒 Order-related classes and methods
👨‍⚕️ Doctor-related classes and methods
👩‍⚕️ Pharmacist-related classes and methods
🔐 Authentication and security features
💰 Payment and wallet features
📊 Reporting functions
⚠️ Warning messages
✅ Success indicators
❌ Error messages
🔄 Process/workflow related functions
🧾 Transaction-related functions
📋 List or collection handling
💻 UI/Console-related functions
EOF

# Create a list of model classes to update
cat > templates/classes_to_update.txt << 'EOF'
Package: models
- User.java (already standardized)
- Admin.java
- Patient.java
- Doctor.java
- Pharmacist.java
- Medicine.java
- Order.java
- Prescription.java
- MedicalReport.java
- Consultation.java
- Message.java
- Wallet.java (already standardized)
- CreditCard.java
- Pharmacy.java

Package: services
- AdminService.java
- PatientService.java
- DoctorService.java
- PharmacistService.java
- PharmacyService.java
- AuthenticationService.java
- WalletService.java

Package: utils
- FileHandler.java
- ConsoleUI.java
- DataInitializer.java

Main Class:
- PharmacyManagementSystem.java
EOF

# Show usage instructions
echo "Documentation templates created in templates/ directory."
echo ""
echo "To standardize a class:"
echo "1. Look at the example in src/models/User.java and src/models/Wallet.java"
echo "2. Copy the appropriate template from templates/"
echo "3. Edit it for your specific class"
echo "4. Replace the existing class comment with your standardized version"
echo ""

echo "Classes that need documentation updates:"
cat templates/classes_to_update.txt

echo ""
echo "Happy standardizing!"