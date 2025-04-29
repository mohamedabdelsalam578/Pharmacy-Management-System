package gui.screens;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.StyledTextField;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeManager;
import gui.theme.ThemeSizes;
import models.User;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends BasePanel {
    private JComboBox<String> userTypeComboBox;
    private StyledTextField usernameField;
    private JPasswordField passwordField;
    private StyledButton loginButton;
    private StyledButton registerButton;
    
    public LoginScreen(MainFrame mainFrame) {
        super(mainFrame);
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BACKGROUND);
        initializeComponents();
        
        // Add a border with debug color
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    }
    
    @Override
    protected void initializeComponents() {
        // Create login panel with vertical box layout
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(ThemeColors.SURFACE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER, 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40) // Increased padding
        ));
        
        // Add title with centering panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(ThemeColors.SURFACE);
        JLabel titleLabel = new JLabel("EL-TA3BAN Pharmacy System");
        titleLabel.setFont(ThemeFonts.FUTURISTIC_HEADER); // Using new futuristic font
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        loginPanel.add(titlePanel);
        
        // Add subtitle for futuristic feel
        JPanel subtitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        subtitlePanel.setBackground(ThemeColors.SURFACE);
        JLabel subtitleLabel = new JLabel("Next-Generation Healthcare");
        subtitleLabel.setFont(ThemeFonts.ITALIC_MEDIUM);
        subtitleLabel.setForeground(ThemeColors.PRIMARY_LIGHT);
        subtitlePanel.add(subtitleLabel);
        loginPanel.add(subtitlePanel);
        
        // Add padding
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Increased spacing
        
        // Add role label
        JPanel roleHeaderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        roleHeaderPanel.setBackground(ThemeColors.SURFACE);
        JLabel roleLabel = new JLabel("Select Your Role");
        roleLabel.setFont(ThemeFonts.BOLD_LARGE); // Larger font
        roleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        roleHeaderPanel.add(roleLabel);
        loginPanel.add(roleHeaderPanel);
        
        // Add user type combo box with centering panel
        JPanel comboBoxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        comboBoxPanel.setBackground(ThemeColors.SURFACE);
        userTypeComboBox = new JComboBox<>(new String[]{"Admin", "Patient", "Doctor", "Pharmacist"});
        
        // Create role icon and color maps
        Map<String, Icon> roleIconMap = new HashMap<>();
        roleIconMap.put("Admin", ThemeIcons.USERS);
        roleIconMap.put("Patient", ThemeIcons.NOTIFICATION);
        roleIconMap.put("Doctor", ThemeIcons.PRESCRIPTION);
        roleIconMap.put("Pharmacist", ThemeIcons.MEDICINE);
        
        Map<String, Color> roleColorMap = new HashMap<>();
        roleColorMap.put("Admin", ThemeColors.TEXT_PRIMARY);
        roleColorMap.put("Patient", ThemeColors.INFO);
        roleColorMap.put("Doctor", ThemeColors.SUCCESS);
        roleColorMap.put("Pharmacist", ThemeColors.WARNING);
        
        // Apply modern styling with role-based icons and colors
        ThemeManager.applyRoleBasedStyling(userTypeComboBox, roleIconMap, roleColorMap);
        
        // Set preferred size for consistent sizing
        userTypeComboBox.setPreferredSize(new Dimension(280, ThemeSizes.COMBO_BOX_HEIGHT)); // Wider for futuristic look
        
        // Add tooltip for the combo box
        userTypeComboBox.setToolTipText("Select your role to log in");
        
        comboBoxPanel.add(userTypeComboBox);
        loginPanel.add(comboBoxPanel);
        
        // Add padding
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Increased spacing
        
        // Add username field with centering panel
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        usernamePanel.setBackground(ThemeColors.SURFACE);
        usernameField = new StyledTextField("Username");
        usernameField.setPreferredSize(new Dimension(280, ThemeSizes.TEXT_FIELD_HEIGHT)); // Wider for futuristic look
        usernamePanel.add(usernameField);
        loginPanel.add(usernamePanel);
        
        // Add padding
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Increased spacing
        
        // Add password field with centering panel
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordPanel.setBackground(ThemeColors.SURFACE);
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(280, ThemeSizes.TEXT_FIELD_HEIGHT)); // Wider for futuristic look
        passwordField.setFont(ThemeFonts.REGULAR_MEDIUM);
        passwordField.setForeground(ThemeColors.TEXT_PRIMARY);
        passwordField.setBackground(ThemeColors.SURFACE);
        passwordField.setCaretColor(ThemeColors.PRIMARY);
        passwordField.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(ThemeSizes.PADDING_SMALL, ThemeSizes.PADDING_MEDIUM, 
                          ThemeSizes.PADDING_SMALL, ThemeSizes.PADDING_MEDIUM)
        ));
        passwordPanel.add(passwordField);
        loginPanel.add(passwordPanel);
        
        // Add padding
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Increased spacing
        
        // Add buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0)); // Added horizontal gap
        buttonsPanel.setBackground(ThemeColors.SURFACE);
        
        // Add login button
        loginButton = new StyledButton("Login", ThemeIcons.LOGIN);
        loginButton.addActionListener(e -> processLogin());
        buttonsPanel.add(loginButton);
        
        // Add register button
        registerButton = new StyledButton("Register", ThemeIcons.ADD);
        registerButton.addActionListener(e -> {
            mainFrame.showRegistrationScreen();
        });
        buttonsPanel.add(registerButton);
        
        loginPanel.add(buttonsPanel);
        
        // Add login panel to center of the screen
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setBackground(ThemeColors.BACKGROUND);
        centeringPanel.add(loginPanel);
        add(centeringPanel, BorderLayout.CENTER);
    }
    
    private void processLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String userType = (String) userTypeComboBox.getSelectedItem();
        
        if (userType != null) {
            userType = userType.toUpperCase();
        }
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            User user = null;
            switch (userType) {
                case "ADMIN":
                    user = mainFrame.getService().authenticateAdmin(username, password);
                    break;
                case "PATIENT":
                    user = mainFrame.getService().authenticatePatient(username, password);
                    break;
                case "DOCTOR":
                    user = mainFrame.getService().authenticateDoctor(username, password);
                    break;
                case "PHARMACIST":
                    user = mainFrame.getService().authenticatePharmacist(username, password);
                    break;
            }
            
            if (user != null) {
                mainFrame.setCurrentUser(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            // Print detailed error for debugging
            e.printStackTrace();
            
            // Show detailed error to user
            JOptionPane.showMessageDialog(this, 
                "Login error: " + e.getMessage() + "\nPlease contact administrator.",
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 