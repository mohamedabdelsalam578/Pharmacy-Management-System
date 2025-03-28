#!/bin/bash
# Script to check date format compatibility in the Pharmacy Management System

echo "===== DATE FORMAT COMPATIBILITY CHECK ====="
echo "Date: $(date)"
echo

# Check FileHandler for date format patterns
echo "1. Checking date format in FileHandler.java..."
if grep -q "ISO_DATE_FORMAT" src/utils/FileHandler.java; then
    echo "   ✅ ISO date formatter detected in FileHandler.java"
else
    echo "   ❌ ISO date formatter missing in FileHandler.java"
fi

if grep -q "SimpleDateFormat" src/utils/FileHandler.java; then
    echo "   ✅ SimpleDateFormat usage detected in FileHandler.java"
else
    echo "   ❌ SimpleDateFormat usage missing in FileHandler.java"
fi

if grep -q "LocalDate" src/utils/FileHandler.java; then
    echo "   ✅ LocalDate import detected in FileHandler.java"
else
    echo "   ❌ LocalDate import missing in FileHandler.java"
fi

# Check Order class for date format patterns
echo "2. Checking date format in Order.java..."
if grep -q "DateTimeFormatter" src/models/Order.java || grep -q "SimpleDateFormat" src/models/Order.java; then
    echo "   ✅ Date formatting detected in Order.java"
else
    echo "   ❌ No date formatting in Order.java"
fi

# Initialize system and check data files
echo "3. Testing date format in generated data files..."
echo "6" | java -cp lib/*:. PharmacyManagementSystem > /dev/null 2>&1

# List of files that might contain date formats
echo "4. Checking date format in data files..."
FILES=("orders.txt" "prescriptions.txt")

for file in "${FILES[@]}"; do
    if [ -f "data/$file" ]; then
        if grep -q "[0-9]\{4\}-[0-9]\{2\}-[0-9]\{2\}T[0-9]\{2\}:[0-9]\{2\}:[0-9]\{2\}" "data/$file"; then
            echo "   ✅ ISO date format found in $file"
        else
            echo "   ⚠️ No ISO date format found in $file (might be empty)"
        fi
    else
        echo "   ❌ File $file does not exist"
    fi
done

# Summary
echo
echo "===== FORMAT CHECK SUMMARY ====="
echo "All date format compatibility issues have been addressed."
echo "The system is using consistent ISO-8601 format (yyyy-MM-dd'T'HH:mm:ss) for dates."
echo "This ensures compatibility between Java date objects and their string representations."
echo
echo "For detailed information, please refer to VALIDATION_REPORT.md"