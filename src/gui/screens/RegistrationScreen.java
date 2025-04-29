package gui.screens;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.StyledTextField;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeSizes;
import gui.theme.ThemeManager;
import models.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Registration screen for new users
 */
public class RegistrationScreen extends BasePanel {
    // UI Components
    private JComboBox<String> userTypeComboBox;
    private StyledTextField nameField;
    private StyledTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private StyledTextField emailField;
    private StyledTextField phoneField;
    private StyledTextField addressField;
    private StyledTextField doctorLicenseField;
    private StyledTextField specialtyField;
    private StyledTextField pharmacistLicenseField;
    private StyledTextField qualificationField;
    private StyledButton registerButton;
    private StyledButton backButton;
    private StyledButton continueButton;
    
    // Panels for different user types
    private JPanel patientFields;
    private JPanel doctorFields;
    private JPanel pharmacistFields;
    private JPanel step1Panel;
    private JPanel step2Panel;
    private boolean isStep1 = true;
    
    public RegistrationScreen(MainFrame mainFrame) {
        super(mainFrame);
        setBackground(ThemeColors.BACKGROUND);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initializeComponents();
    }
    
    @Override
    protected void initializeComponents() {
        // Create main panel with CardLayout for steps
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout());
        mainPanel.setBackground(ThemeColors.SURFACE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Create both step panels
        step1Panel = createStep1Panel();
        step2Panel = createStep2Panel();
        
        // Add panels to CardLayout with specific names
        mainPanel.add(step1Panel, "STEP1");
        mainPanel.add(step2Panel, "STEP2");
        
        // Show step 1 initially
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        cardLayout.show(mainPanel, "STEP1");
        
        // Debug the panel structure
        System.out.println("Registration panels added to CardLayout:");
        System.out.println("step1Panel parent: " + step1Panel.getParent());
        System.out.println("step2Panel parent: " + step2Panel.getParent());
        
        // Center the main panel
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setBackground(ThemeColors.BACKGROUND);
        centeringPanel.add(mainPanel);
        add(centeringPanel, BorderLayout.CENTER);
    }
    
    private JPanel createStep1Panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeColors.SURFACE);
        
        // Add title
        JLabel titleLabel = new JLabel("Register New Account - Step 1");
        titleLabel.setFont(ThemeFonts.REGULAR_XXLARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(ThemeColors.SURFACE);
        titlePanel.add(titleLabel);
        panel.add(titlePanel);
        
        // Add spacing
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Add role label
        JPanel roleHeaderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        roleHeaderPanel.setBackground(ThemeColors.SURFACE);
        JLabel roleLabel = new JLabel("Select Account Type");
        roleLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        roleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        roleHeaderPanel.add(roleLabel);
        panel.add(roleHeaderPanel);
        
        // Create user type selector
        userTypeComboBox = new JComboBox<>(new String[]{"Patient", "Doctor", "Pharmacist"});
        
        // Create role icon and color maps
        Map<String, Icon> roleIconMap = new HashMap<>();
        roleIconMap.put("Patient", ThemeIcons.NOTIFICATION);
        roleIconMap.put("Doctor", ThemeIcons.PRESCRIPTION);
        roleIconMap.put("Pharmacist", ThemeIcons.MEDICINE);
        
        Map<String, Color> roleColorMap = new HashMap<>();
        roleColorMap.put("Patient", ThemeColors.INFO);
        roleColorMap.put("Doctor", ThemeColors.SUCCESS);
        roleColorMap.put("Pharmacist", ThemeColors.WARNING);
        
        // Apply modern styling with role-based icons and colors
        ThemeManager.applyRoleBasedStyling(userTypeComboBox, roleIconMap, roleColorMap);
        
        // Set size constraints
        userTypeComboBox.setPreferredSize(new Dimension(200, ThemeSizes.COMBO_BOX_HEIGHT));
        userTypeComboBox.setMaximumSize(new Dimension(200, ThemeSizes.COMBO_BOX_HEIGHT));
        userTypeComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel selectorPanel = new JPanel();
        selectorPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        selectorPanel.setBackground(ThemeColors.SURFACE);
        selectorPanel.add(userTypeComboBox);
        panel.add(selectorPanel);
        
        // Add spacing
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Initialize common fields
        nameField = createTextField("Full Name");
        usernameField = createTextField("Username");
        
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, ThemeSizes.TEXT_FIELD_HEIGHT));
        passwordField.setFont(ThemeFonts.REGULAR_MEDIUM);
        passwordField.setForeground(ThemeColors.TEXT_PRIMARY);
        passwordField.setBackground(ThemeColors.SURFACE);
        passwordField.setCaretColor(ThemeColors.PRIMARY);
        passwordField.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(ThemeSizes.PADDING_SMALL, ThemeSizes.PADDING_MEDIUM, 
                          ThemeSizes.PADDING_SMALL, ThemeSizes.PADDING_MEDIUM)
        ));
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(250, ThemeSizes.TEXT_FIELD_HEIGHT));
        confirmPasswordField.setFont(ThemeFonts.REGULAR_MEDIUM);
        confirmPasswordField.setForeground(ThemeColors.TEXT_PRIMARY);
        confirmPasswordField.setBackground(ThemeColors.SURFACE);
        confirmPasswordField.setCaretColor(ThemeColors.PRIMARY);
        confirmPasswordField.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(ThemeSizes.PADDING_SMALL, ThemeSizes.PADDING_MEDIUM, 
                          ThemeSizes.PADDING_SMALL, ThemeSizes.PADDING_MEDIUM)
        ));
        
        emailField = createTextField("Email");
        phoneField = createTextField("Phone");
        
        // Common fields panel
        JPanel commonFields = new JPanel();
        commonFields.setLayout(new BoxLayout(commonFields, BoxLayout.Y_AXIS));
        commonFields.setBackground(ThemeColors.SURFACE);
        
        addFieldWithLabel(commonFields, "Name:", nameField);
        addFieldWithLabel(commonFields, "Username:", usernameField);
        addFieldWithLabel(commonFields, "Password:", passwordField);
        addFieldWithLabel(commonFields, "Confirm Password:", confirmPasswordField);
        addFieldWithLabel(commonFields, "Email:", emailField);
        addFieldWithLabel(commonFields, "Phone:", phoneField);
        
        panel.add(commonFields);
        
        // Add buttons
        continueButton = new StyledButton("Continue");
        continueButton.addActionListener(e -> goToStep2());
        
        backButton = new StyledButton("Back to Login");
        backButton.addActionListener(e -> mainFrame.showLoginScreen());
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setBackground(ThemeColors.SURFACE);
        buttonsPanel.add(backButton);
        buttonsPanel.add(continueButton);
        
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(buttonsPanel);
        
        return panel;
    }
    
    private JPanel createStep2Panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        
        // Add title
        JLabel titleLabel = new JLabel("Register New Account - Step 2");
        titleLabel.setFont(ThemeFonts.REGULAR_XXLARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Initialize type-specific fields
        addressField = createTextField("Address");
        doctorLicenseField = createTextField("License Number");
        specialtyField = createTextField("Specialty");
        pharmacistLicenseField = createTextField("License Number");
        qualificationField = createTextField("Qualification");
        
        // Create type-specific panels
        patientFields = new JPanel();
        patientFields.setLayout(new BoxLayout(patientFields, BoxLayout.Y_AXIS));
        patientFields.setBackground(ThemeColors.SURFACE);
        addFieldWithLabel(patientFields, "Address:", addressField);
        
        doctorFields = new JPanel();
        doctorFields.setLayout(new BoxLayout(doctorFields, BoxLayout.Y_AXIS));
        doctorFields.setBackground(ThemeColors.SURFACE);
        addFieldWithLabel(doctorFields, "License Number:", doctorLicenseField);
        addFieldWithLabel(doctorFields, "Specialty:", specialtyField);
        
        pharmacistFields = new JPanel();
        pharmacistFields.setLayout(new BoxLayout(pharmacistFields, BoxLayout.Y_AXIS));
        pharmacistFields.setBackground(ThemeColors.SURFACE);
        addFieldWithLabel(pharmacistFields, "License Number:", pharmacistLicenseField);
        addFieldWithLabel(pharmacistFields, "Qualification:", qualificationField);
        
        // Add a panel to hold the dynamic fields
        JPanel dynamicFieldsContainer = new JPanel();
        dynamicFieldsContainer.setLayout(new GridBagLayout());
        dynamicFieldsContainer.setBackground(ThemeColors.SURFACE);
        dynamicFieldsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create a panel that will contain all role-specific fields
        JPanel roleFieldsPanel = new JPanel(new CardLayout());
        roleFieldsPanel.setBackground(ThemeColors.SURFACE);
        
        // Add all type-specific panels to the card layout
        roleFieldsPanel.add(patientFields, "PATIENT");
        roleFieldsPanel.add(doctorFields, "DOCTOR");
        roleFieldsPanel.add(pharmacistFields, "PHARMACIST");
        
        // Add the role fields panel to the container
        dynamicFieldsContainer.add(roleFieldsPanel);
        
        panel.add(dynamicFieldsContainer, BorderLayout.CENTER);
        
        // Add buttons
        registerButton = new StyledButton("Register");
        registerButton.addActionListener(e -> register());
        
        JButton backToStep1Button = new StyledButton("Back");
        backToStep1Button.addActionListener(e -> goToStep1());
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setBackground(ThemeColors.SURFACE);
        buttonsPanel.add(backToStep1Button);
        buttonsPanel.add(registerButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void goToStep1() {
        System.out.println("Going back to Step 1");
        JPanel parent = (JPanel) step1Panel.getParent();
        CardLayout cardLayout = (CardLayout) parent.getLayout();
        cardLayout.show(parent, "STEP1");
        isStep1 = true;
        System.out.println("Now on Step 1");
    }
    
    private void goToStep2() {
        // Validate step 1 fields
        System.out.println("Attempting to go to Step 2");
        if (validateStep1()) {
            System.out.println("Step 1 validation passed");
            // Update fields visibility based on selected user type
            updateVisibleFields();
            
            System.out.println("Getting card layout");
            JPanel parent = (JPanel) step2Panel.getParent();
            CardLayout cardLayout = (CardLayout) parent.getLayout();
            System.out.println("Showing STEP2 card");
            cardLayout.show(parent, "STEP2");
            isStep1 = false;
            System.out.println("Now on Step 2");
        } else {
            System.out.println("Step 1 validation failed");
        }
    }
    
    private boolean validateStep1() {
        // Check if all common fields are filled
        if (nameField.getText().isEmpty() || 
            usernameField.getText().isEmpty() || 
            new String(passwordField.getPassword()).isEmpty() ||
            new String(confirmPasswordField.getPassword()).isEmpty() ||
            emailField.getText().isEmpty() ||
            phoneField.getText().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Check if passwords match
        if (!new String(passwordField.getPassword()).equals(new String(confirmPasswordField.getPassword()))) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Check email format
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!emailField.getText().matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid email address.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void updateVisibleFields() {
        String selectedType = (String) userTypeComboBox.getSelectedItem();
        System.out.println("Updating visible fields for: " + selectedType);
        
        // Get the card layout from the role fields panel
        JPanel dynamicFieldsContainer = (JPanel)patientFields.getParent();
        CardLayout cardLayout = (CardLayout) dynamicFieldsContainer.getLayout();
        
        // Show the appropriate card based on user type
        switch (selectedType) {
            case "Patient":
                System.out.println("Showing PATIENT card");
                cardLayout.show(dynamicFieldsContainer, "PATIENT");
                break;
            case "Doctor":
                System.out.println("Showing DOCTOR card");
                cardLayout.show(dynamicFieldsContainer, "DOCTOR");
                break;
            case "Pharmacist":
                System.out.println("Showing PHARMACIST card");
                cardLayout.show(dynamicFieldsContainer, "PHARMACIST");
                break;
        }
        
        revalidate();
        repaint();
    }
    
    private boolean validateInput() {
        // Step 1 validation was already done
        if (!validateStep1()) {
            goToStep1();
            return false;
        }
        
        // Validate type-specific fields
        String userType = (String) userTypeComboBox.getSelectedItem();
        switch (userType) {
            case "Patient":
                if (addressField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please provide an address.", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
                
            case "Doctor":
                if (doctorLicenseField.getText().isEmpty() || specialtyField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please fill in all required fields for doctor registration.", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
                
            case "Pharmacist":
                if (pharmacistLicenseField.getText().isEmpty() || qualificationField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please fill in all required fields for pharmacist registration.", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
        }
        
        return true;
    }
    
    private StyledTextField createTextField(String placeholder) {
        StyledTextField field = new StyledTextField(placeholder);
        field.setPreferredSize(new Dimension(250, ThemeSizes.TEXT_FIELD_HEIGHT));
        return field;
    }
    
    private void addFieldWithLabel(JPanel panel, String label, JComponent field) {
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fieldPanel.setBackground(ThemeColors.SURFACE);
        
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setPreferredSize(new Dimension(120, 25));
        
        fieldPanel.add(fieldLabel);
        fieldPanel.add(field);
        
        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    private void register() {
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        // Get user type
        String userType = (String) userTypeComboBox.getSelectedItem();
        
        try {
            User newUser = null;
            boolean success = false;
            
            // Common data
            String name = nameField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();
            String phone = phoneField.getText();
            
            switch (userType) {
                case "Patient":
                    String address = addressField.getText();
                    Patient patient = createPatient(name, username, password, email, phone, address);
                    success = patient != null;
                    newUser = patient;
                    break;
                    
                case "Doctor":
                    String licenseNumber = doctorLicenseField.getText();
                    String specialty = specialtyField.getText();
                    Doctor doctor = createDoctor(name, username, password, email, phone, licenseNumber, specialty);
                    success = doctor != null;
                    newUser = doctor;
                    break;
                    
                case "Pharmacist":
                    String pharmLicenseNumber = pharmacistLicenseField.getText();
                    String qualification = qualificationField.getText();
                    Pharmacist pharmacist = createPharmacist(name, username, password, email, phone, pharmLicenseNumber, qualification);
                    success = pharmacist != null;
                    newUser = pharmacist;
                    break;
            }
            
            if (success && newUser != null) {
                JOptionPane.showMessageDialog(this, 
                    "Registration successful!\nPlease login with your new credentials.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                mainFrame.showLoginScreen();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Registration failed. Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred during registration: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Patient createPatient(String name, String username, String password, String email, String phone, String address) {
        // Check if username already exists
        if (service.getPatients().stream().anyMatch(p -> p.getUsername().equals(username))) {
            JOptionPane.showMessageDialog(this, 
                "Username already exists. Please choose a different username.", 
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        // Generate a new patient ID
        int nextPatientId = service.getPatients().stream()
            .mapToInt(Patient::getId)
            .max()
            .orElse(0) + 1;
        
        // Create and register the patient
        Patient newPatient = new Patient(nextPatientId, name, username, password, email, phone, address);
        
        if (service.getPatientService().createAccount(newPatient)) {
            return newPatient;
        }
        
        return null;
    }
    
    private Doctor createDoctor(String name, String username, String password, String email, String phone, 
                             String licenseNumber, String specialty) {
        // Check if username already exists
        if (service.getDoctors().stream().anyMatch(d -> d.getUsername().equals(username))) {
            JOptionPane.showMessageDialog(this, 
                "Username already exists. Please choose a different username.", 
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        // Generate a new doctor ID
        int nextDoctorId = service.getDoctors().stream()
            .mapToInt(Doctor::getId)
            .max()
            .orElse(0) + 1;
        
        // Create and register the doctor
        Doctor newDoctor = new Doctor(nextDoctorId, name, username, password, email, phone, licenseNumber, specialty);
        
        if (service.getDoctorService().createAccount(newDoctor)) {
            return newDoctor;
        }
        
        return null;
    }
    
    private Pharmacist createPharmacist(String name, String username, String password, String email, String phone, 
                                     String licenseNumber, String qualification) {
        // Check if username already exists
        if (service.getPharmacists().stream().anyMatch(p -> p.getUsername().equals(username))) {
            JOptionPane.showMessageDialog(this, 
                "Username already exists. Please choose a different username.", 
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        // Generate a new pharmacist ID
        int nextPharmacistId = service.getPharmacists().stream()
            .mapToInt(Pharmacist::getId)
            .max()
            .orElse(0) + 1;
        
        // Create and register the pharmacist
        Pharmacist newPharmacist = new Pharmacist(nextPharmacistId, name, username, password, email, phone, 
                                              licenseNumber, qualification);
        
        if (service.getPharmacistService().createAccount(newPharmacist)) {
            return newPharmacist;
        }
        
        return null;
    }
    
    private void clearFields() {
        nameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        doctorLicenseField.setText("");
        specialtyField.setText("");
        pharmacistLicenseField.setText("");
        qualificationField.setText("");
    }
} 