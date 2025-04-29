package gui.admin;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.StyledTextField;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeSizes;
import models.Admin;
import models.Doctor;
import models.Patient;
import models.Pharmacist;
import models.User;
import models.UserRole;
import services.PharmacyService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Panel for adding a new user with a modern UI similar to the registration screen
 */
public class AddUserPanel extends BasePanel {
    private UserManagementPanel parentPanel;
    private JDialog dialog;
    
    // User type selection
    private JComboBox<String> userTypeComboBox;
    
    // Common fields
    private StyledTextField nameField;
    private StyledTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private StyledTextField emailField;
    private StyledTextField phoneField;
    
    // Specific fields
    private StyledTextField addressField;          // For patients
    private StyledTextField specialtyField;        // For doctors
    private StyledTextField licenseField;          // For doctors and pharmacists
    private StyledTextField departmentField;       // For pharmacists and admins
    private StyledTextField positionField;         // For admins

    // Buttons
    private StyledButton saveButton;
    private StyledButton cancelButton;
    
    // Panels for different user types
    private JPanel patientFields;
    private JPanel doctorFields;
    private JPanel pharmacistFields;
    private JPanel adminFields;
    
    /**
     * Constructor for AddUserPanel
     * 
     * @param mainFrame The main frame
     * @param parentPanel The parent panel that will be refreshed after adding a user
     * @param dialog The dialog that contains this panel
     */
    public AddUserPanel(MainFrame mainFrame, UserManagementPanel parentPanel, JDialog dialog) {
        super(mainFrame);
        this.parentPanel = parentPanel;
        this.dialog = dialog;
        
        setBackground(ThemeColors.SURFACE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initializeComponents();
    }
    
    @Override
    protected void initializeComponents() {
        setLayout(new BorderLayout(15, 15));
        
        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ThemeColors.SURFACE);
        
        // User type selection
        JPanel typePanel = createUserTypePanel();
        mainPanel.add(typePanel);
        
        // Fields panel
        JPanel fieldsPanel = createFieldsPanel();
        mainPanel.add(fieldsPanel);
        
        // Buttons panel
        JPanel buttonsPanel = createButtonsPanel();
        
        // Add to main panel
        add(mainPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
        
        // Update visible fields based on initial selection
        updateVisibleFields();
    }
    
    private JPanel createUserTypePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(ThemeColors.SURFACE);
        
        JLabel typeLabel = new JLabel("User Type:");
        typeLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        typeLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        
        userTypeComboBox = new JComboBox<>(new String[]{"Patient", "Doctor", "Pharmacist", "Admin"});
        userTypeComboBox.setFont(ThemeFonts.REGULAR_MEDIUM);
        userTypeComboBox.setBackground(ThemeColors.SURFACE);
        userTypeComboBox.addActionListener(e -> updateVisibleFields());
        
        panel.add(typeLabel);
        panel.add(userTypeComboBox);
        
        return panel;
    }
    
    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new CardLayout());
        panel.setBackground(ThemeColors.SURFACE);
        
        // Create fields
        nameField = createTextField("Full Name");
        usernameField = createTextField("Username");
        
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, ThemeSizes.TEXT_FIELD_HEIGHT));
        passwordField.setFont(ThemeFonts.REGULAR_MEDIUM);
        passwordField.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(250, ThemeSizes.TEXT_FIELD_HEIGHT));
        confirmPasswordField.setFont(ThemeFonts.REGULAR_MEDIUM);
        confirmPasswordField.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        emailField = createTextField("Email");
        phoneField = createTextField("Phone Number");
        addressField = createTextField("Address");
        specialtyField = createTextField("Specialty");
        licenseField = createTextField("License Number");
        departmentField = createTextField("Department");
        positionField = createTextField("Position");
        
        // Create field panels for each user type
        JPanel commonFields = new JPanel();
        commonFields.setLayout(new GridLayout(0, 2, 10, 10));
        commonFields.setBackground(ThemeColors.SURFACE);
        
        commonFields.add(new JLabel("Name:"));
        commonFields.add(nameField);
        commonFields.add(new JLabel("Username:"));
        commonFields.add(usernameField);
        commonFields.add(new JLabel("Password:"));
        commonFields.add(passwordField);
        commonFields.add(new JLabel("Confirm Password:"));
        commonFields.add(confirmPasswordField);
        commonFields.add(new JLabel("Email:"));
        commonFields.add(emailField);
        commonFields.add(new JLabel("Phone:"));
        commonFields.add(phoneField);
        
        // Create patient fields
        patientFields = new JPanel(new GridLayout(0, 2, 10, 10));
        patientFields.setBackground(ThemeColors.SURFACE);
        patientFields.add(new JLabel("Address:"));
        patientFields.add(addressField);
        
        // Create doctor fields
        doctorFields = new JPanel(new GridLayout(0, 2, 10, 10));
        doctorFields.setBackground(ThemeColors.SURFACE);
        doctorFields.add(new JLabel("License Number:"));
        doctorFields.add(licenseField);
        doctorFields.add(new JLabel("Specialty:"));
        doctorFields.add(specialtyField);
        
        // Create pharmacist fields
        pharmacistFields = new JPanel(new GridLayout(0, 2, 10, 10));
        pharmacistFields.setBackground(ThemeColors.SURFACE);
        pharmacistFields.add(new JLabel("License Number:"));
        pharmacistFields.add(licenseField);
        pharmacistFields.add(new JLabel("Department:"));
        pharmacistFields.add(departmentField);
        
        // Create admin fields
        adminFields = new JPanel(new GridLayout(0, 2, 10, 10));
        adminFields.setBackground(ThemeColors.SURFACE);
        adminFields.add(new JLabel("Position:"));
        adminFields.add(positionField);
        adminFields.add(new JLabel("Department:"));
        adminFields.add(departmentField);
        
        // Create main panel with all fields
        JPanel allFieldsPanel = new JPanel();
        allFieldsPanel.setLayout(new BoxLayout(allFieldsPanel, BoxLayout.Y_AXIS));
        allFieldsPanel.setBackground(ThemeColors.SURFACE);
        
        // Add common fields
        JPanel commonFieldsContainer = new JPanel(new BorderLayout());
        commonFieldsContainer.setBackground(ThemeColors.SURFACE);
        commonFieldsContainer.setBorder(BorderFactory.createTitledBorder("User Information"));
        commonFieldsContainer.add(commonFields, BorderLayout.CENTER);
        allFieldsPanel.add(commonFieldsContainer);
        
        // Add specific fields containers
        JPanel specificFieldsContainer = new JPanel(new CardLayout());
        specificFieldsContainer.setBackground(ThemeColors.SURFACE);
        specificFieldsContainer.setBorder(BorderFactory.createTitledBorder("Additional Information"));
        
        specificFieldsContainer.add(patientFields, "Patient");
        specificFieldsContainer.add(doctorFields, "Doctor");
        specificFieldsContainer.add(pharmacistFields, "Pharmacist");
        specificFieldsContainer.add(adminFields, "Admin");
        
        allFieldsPanel.add(specificFieldsContainer);
        
        panel.add(allFieldsPanel);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(ThemeColors.SURFACE);
        
        saveButton = new StyledButton("Save", ThemeIcons.SAVE);
        saveButton.addActionListener(e -> saveUser());
        
        cancelButton = new StyledButton("Cancel", ThemeIcons.CANCEL);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        panel.add(cancelButton);
        panel.add(saveButton);
        
        return panel;
    }
    
    private StyledTextField createTextField(String placeholder) {
        StyledTextField field = new StyledTextField(placeholder);
        field.setFont(ThemeFonts.REGULAR_MEDIUM);
        return field;
    }
    
    public void updateVisibleFields() {
        String selectedType = (String) userTypeComboBox.getSelectedItem();
        CardLayout cardLayout = (CardLayout) ((JPanel) patientFields.getParent()).getLayout();
        cardLayout.show(patientFields.getParent(), selectedType);
    }
    
    private void saveUser() {
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        try {
            PharmacyService service = mainFrame.getPharmacyService();
            String selectedType = (String) userTypeComboBox.getSelectedItem();
            
            // Get the next available ID
            int nextId = getNextUserId();
            
            // Create the user based on type
            User newUser = null;
            boolean success = false;
            
            switch (selectedType) {
                case "Patient":
                    Patient patient = new Patient(
                        nextId,
                        nameField.getText(),
                        usernameField.getText(),
                        new String(passwordField.getPassword()),
                        emailField.getText(),
                        phoneField.getText(),
                        addressField.getText()
                    );
                    success = service.addPatient(patient);
                    break;
                case "Doctor":
                    // We're using the available constructor from Patient.java as reference
                    // This constructor should be: id, name, username, password, email, phone, license, specialty
                    Doctor doctor = new Doctor(
                        nextId,
                        nameField.getText(),
                        usernameField.getText(),
                        new String(passwordField.getPassword()),
                        emailField.getText(),
                        phoneField.getText(),
                        licenseField.getText(),
                        specialtyField.getText()
                    );
                    success = service.addDoctor(doctor);
                    break;
                case "Pharmacist":
                    // We're using the constructor from Pharmacist.java
                    // Constructor: id, name, username, password, email, phone, license, qualification
                    Pharmacist pharmacist = new Pharmacist(
                        nextId,
                        nameField.getText(),
                        usernameField.getText(),
                        new String(passwordField.getPassword()),
                        emailField.getText(),
                        phoneField.getText(),
                        licenseField.getText(),
                        departmentField.getText() // Using department as qualification
                    );
                    success = service.addPharmacist(pharmacist);
                    break;
                case "Admin":
                    Admin admin = new Admin(
                        nextId,
                        nameField.getText(),
                        usernameField.getText(),
                        new String(passwordField.getPassword()),
                        emailField.getText(),
                        phoneField.getText(),
                        positionField.getText(),
                        departmentField.getText()
                    );
                    success = service.addAdmin(admin);
                    break;
            }
            
            if (success) {
                // Show success message
                JOptionPane.showMessageDialog(
                    dialog,
                    selectedType + " added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE,
                    ThemeIcons.SUCCESS
                );
                
                // Refresh the user management panel
                parentPanel.loadUserData();
                
                // Close the dialog
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Failed to add user. Username may already exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE,
                    ThemeIcons.ERROR
                );
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                dialog,
                "Error adding user: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE,
                ThemeIcons.ERROR
            );
        }
    }
    
    private boolean validateInput() {
        // Check for empty fields
        if (nameField.getText().trim().isEmpty()) {
            showErrorMessage("Name is required");
            return false;
        }
        
        if (usernameField.getText().trim().isEmpty()) {
            showErrorMessage("Username is required");
            return false;
        }
        
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (password.trim().isEmpty()) {
            showErrorMessage("Password is required");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showErrorMessage("Passwords do not match");
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showErrorMessage("Email is required");
            return false;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            showErrorMessage("Phone number is required");
            return false;
        }
        
        // Validate specific fields based on user type
        String selectedType = (String) userTypeComboBox.getSelectedItem();
        
        if ("Patient".equals(selectedType) && addressField.getText().trim().isEmpty()) {
            showErrorMessage("Address is required for patients");
            return false;
        }
        
        if ("Doctor".equals(selectedType)) {
            if (licenseField.getText().trim().isEmpty()) {
                showErrorMessage("License number is required for doctors");
                return false;
            }
            if (specialtyField.getText().trim().isEmpty()) {
                showErrorMessage("Specialty is required for doctors");
                return false;
            }
        }
        
        if ("Pharmacist".equals(selectedType)) {
            if (licenseField.getText().trim().isEmpty()) {
                showErrorMessage("License number is required for pharmacists");
                return false;
            }
            if (departmentField.getText().trim().isEmpty()) {
                showErrorMessage("Department is required for pharmacists");
                return false;
            }
        }
        
        if ("Admin".equals(selectedType)) {
            if (positionField.getText().trim().isEmpty()) {
                showErrorMessage("Position is required for admins");
                return false;
            }
            if (departmentField.getText().trim().isEmpty()) {
                showErrorMessage("Department is required for admins");
                return false;
            }
        }
        
        return true;
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            dialog,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE,
            ThemeIcons.ERROR
        );
    }
    
    private int getNextUserId() {
        PharmacyService service = mainFrame.getPharmacyService();
        int maxId = 0;
        
        // Check all user types to find the highest ID
        for (Admin admin : service.getAdmins()) {
            maxId = Math.max(maxId, admin.getId());
        }
        
        for (Doctor doctor : service.getDoctors()) {
            maxId = Math.max(maxId, doctor.getId());
        }
        
        for (Patient patient : service.getPatients()) {
            maxId = Math.max(maxId, patient.getId());
        }
        
        for (Pharmacist pharmacist : service.getPharmacists()) {
            maxId = Math.max(maxId, pharmacist.getId());
        }
        
        return maxId + 1;
    }
} 