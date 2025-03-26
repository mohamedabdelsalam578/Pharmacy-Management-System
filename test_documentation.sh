#!/bin/bash
# Script to test documentation generation

# Compile the Java files
javac -d . src/*.java src/*/*.java

# Run the application, select option 6 for documentation, then enter to continue, then option 7 to exit
(echo "6"; sleep 1; echo ""; sleep 1; echo "7") | java PharmacyManagementSystem

# Check if documentation was generated
if [ -f "docs/generated_documentation.md" ]; then
    echo "Documentation successfully generated!"
    echo "First 20 lines of generated documentation:"
    head -n 20 docs/generated_documentation.md
else
    echo "Documentation generation failed!"
fi