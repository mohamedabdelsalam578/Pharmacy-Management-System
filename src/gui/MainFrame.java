package gui;

import gui.components.BasePanel;
import gui.screens.LoginScreen;
import gui.screens.RegistrationScreen;
import gui.theme.ThemeColors;
import gui.theme.ThemeManager;
import gui.theme.ThemeSizes;
import models.User;
import models.UserRole;
import services.PharmacyService;
import gui.admin.AdminDashboardPanel;
import gui.admin.MedicineManagementPanel;
import gui.admin.UserManagementPanel;
import gui.admin.ReportsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private User currentUser;
    private PharmacyService service;
    private JPanel mainContent;
    
    public MainFrame() {
        super("EL-TA3BAN Pharmacy System");
        
        // Initialize services
        service = PharmacyService.getInstance();
        
        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ThemeSizes.WINDOW_WIDTH_DEFAULT, ThemeSizes.WINDOW_HEIGHT_DEFAULT);
        setMinimumSize(new Dimension(ThemeSizes.WINDOW_WIDTH_MIN, ThemeSizes.WINDOW_HEIGHT_MIN));
        setLocationRelativeTo(null);
        
        // Initialize components
        initializeComponents();
        
        // Add window listener to auto-save on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (service != null) {
                        service.saveDataToFiles();
                    }
                } catch (Exception ex) {
                    System.err.println("Auto-save on window closing failed: " + ex.getMessage());
                }
            }
        });
        
        // Set theme
        ThemeManager.getInstance().setTheme("default");
        
        // Show login screen
        showLoginScreen();
    }
    
    private void initializeComponents() {
        // Set layout
        setLayout(new BorderLayout());
        
        // Create main content panel - using CardLayout to prevent duplicate components
        mainContent = new JPanel(new CardLayout());
        mainContent.setBackground(ThemeColors.BACKGROUND);
        
        // Add components to frame (no top bar anymore)
        add(mainContent, BorderLayout.CENTER);
    }
    
    public void showLoginScreen() {
        // Clear the main content completely before adding login screen
        mainContent.removeAll();
        
        // Create and add login screen - explicitly using BorderLayout.CENTER
        LoginScreen loginScreen = new LoginScreen(this);
        mainContent.add(loginScreen, "LOGIN");
        
        // Show the login screen
        CardLayout cl = (CardLayout)(mainContent.getLayout());
        cl.show(mainContent, "LOGIN");
        
        // Repaint to ensure changes are visible
        mainContent.revalidate();
        mainContent.repaint();
    }
    
    public void showRegistrationScreen() {
        // Clear the main content completely before adding registration screen
        mainContent.removeAll();
        
        // Create and add registration screen
        RegistrationScreen registrationScreen = new RegistrationScreen(this);
        mainContent.add(registrationScreen, "REGISTRATION");
        
        // Show the registration screen
        CardLayout cl = (CardLayout)(mainContent.getLayout());
        cl.show(mainContent, "REGISTRATION");
        
        // Repaint to ensure changes are visible
        mainContent.revalidate();
        mainContent.repaint();
    }
    
    public void navigateTo(String destination) {
        navigateTo(destination, null);
    }
    
    public void navigateTo(String destination, Object data) {
        // Auto-persist any changes before switching screens
        try {
            if (service != null) {
                service.saveDataToFiles();
            }
        } catch (Exception ex) {
            System.err.println("Auto-save failed: " + ex.getMessage());
        }
        
        // Debug logging
        System.out.println("NAVIGATION: Requested to navigate to '" + destination + "'");
        
        if (destination.equals("LOGOUT")) {
            logout();
            return;
        }
        
        // Show appropriate panel based on destination
        mainContent.removeAll();
        BasePanel panel = null;
        
        switch (destination) {
            case "DASHBOARD":
                // Create and show appropriate dashboard based on user role
                if (currentUser != null) {
                    switch (currentUser.getRole()) {
                        case ADMIN:
                            System.out.println("NAVIGATION: Creating AdminDashboardPanel");
                            panel = new gui.admin.AdminDashboardPanel(this);
                            break;
                        case DOCTOR:
                            // Create doctor dashboard panel
                            panel = new gui.doctor.DoctorDashboard(this);
                            break;
                        case PHARMACIST:
                            panel = new gui.dashboard.PharmacistDashboardPanel(this);
                            break;
                        case PATIENT:
                            panel = new gui.dashboard.PatientDashboardPanel(this);
                            break;
                    }
                }
                break;
            case "MEDICINES":
                // Create and show medicines panel
                System.out.println("NAVIGATION: Creating MedicineManagementPanel via MEDICINES");
                panel = new gui.admin.MedicineManagementPanel(this);
                break;
            case "USERS":
                // Create and show users panel
                System.out.println("NAVIGATION: Creating UserManagementPanel via USERS");
                panel = new gui.admin.UserManagementPanel(this);
                break;
            case "REPORTS":
                // Create and show reports panel
                System.out.println("NAVIGATION: Creating ReportsPanel via REPORTS");
                panel = new gui.admin.ReportsPanel(this);
                break;
            case "MEDICINE_MANAGEMENT":
                // Create and show medicine management panel
                System.out.println("NAVIGATION: Creating MedicineManagementPanel via MEDICINE_MANAGEMENT");
                panel = new gui.admin.MedicineManagementPanel(this);
                break;
            case "USER_MANAGEMENT":
                // Create and show user management panel
                System.out.println("NAVIGATION: Creating UserManagementPanel via USER_MANAGEMENT");
                panel = new gui.admin.UserManagementPanel(this);
                break;
            case "INVENTORY_MANAGEMENT":
                // Create and show inventory management panel (alias for medicine management)
                System.out.println("NAVIGATION: Creating MedicineManagementPanel via INVENTORY_MANAGEMENT");
                panel = new gui.admin.MedicineManagementPanel(this);
                break;
            case "SYSTEM_SETTINGS":
                // Create and show reports panel
                System.out.println("NAVIGATION: Creating ReportsPanel via SYSTEM_SETTINGS");
                panel = new gui.admin.ReportsPanel(this);
                break;
            case "ORDERS":
                // Create and show orders panel
                panel = new gui.dashboard.OrderManagementPanel(this);
                break;
            case "PRESCRIPTIONS":
                if (currentUser != null) {
                    if (currentUser.getRole() == UserRole.DOCTOR) {
                        panel = new gui.doctor.PrescriptionsPanel(this);
                    } else if (currentUser.getRole() == UserRole.PHARMACIST) {
                        panel = new gui.pharmacist.PrescriptionManagementPanel(this);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Access denied. Only doctors and pharmacists can access prescriptions.", 
                            "Access Denied", 
                            JOptionPane.WARNING_MESSAGE);
                        navigateTo("DASHBOARD");
                        return;
                    }
                }
                break;
            case "PATIENT_LIST":
                if (currentUser != null && currentUser.getRole() == UserRole.DOCTOR) {
                    panel = new gui.doctor.PatientListPanel(this);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Patient list is only available for doctors.", 
                        "Access Denied", 
                        JOptionPane.WARNING_MESSAGE);
                    navigateTo("DASHBOARD");
                    return;
                }
                break;
            case "CONSULTATIONS":
                if (currentUser != null && currentUser.getRole() == UserRole.DOCTOR) {
                    panel = new gui.doctor.ConsultationsPanel(this);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Consultations panel will be implemented soon.", 
                        "Coming Soon", 
                        JOptionPane.INFORMATION_MESSAGE);
                    navigateTo("DASHBOARD");
                    return;
                }
                break;
            case "MEDICAL_RECORDS":
                if (currentUser != null && currentUser.getRole() == UserRole.DOCTOR) {
                    panel = new gui.doctor.MedicalRecordsPanel(this);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Medical records panel will be implemented soon.", 
                        "Coming Soon", 
                        JOptionPane.INFORMATION_MESSAGE);
                    navigateTo("DASHBOARD");
                    return;
                }
                break;
            default:
                System.out.println("NAVIGATION: Unknown destination '" + destination + "'");
                break;
        }
        
        if (panel != null) {
            System.out.println("NAVIGATION: Adding panel to mainContent: " + panel.getClass().getSimpleName());
            mainContent.add(panel, "CURRENT");
            CardLayout cl = (CardLayout)(mainContent.getLayout());
            cl.show(mainContent, "CURRENT");
        } else {
            System.err.println("NAVIGATION: Failed to create panel for destination '" + destination + "'");
        }
        
        mainContent.revalidate();
        mainContent.repaint();
    }
    
    /**
     * Logs out the current user and returns to the login screen
     */
    public void logout() {
        // Persist any runtime changes to disk
        try {
            getPharmacyService().saveDataToFiles();
            System.out.println("Data saved successfully on logout.");
        } catch (Exception ex) {
            System.err.println("Error saving data on logout: " + ex.getMessage());
        }
        currentUser = null;
        showLoginScreen();
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Debug logging removed
        if (user != null) {
            navigateTo("DASHBOARD");
        }
    }
    
    /**
     * Get the service instance
     * 
     * @return The pharmacy service instance
     */
    public PharmacyService getService() {
        return service;
    }
    
    /**
     * Get the pharmacy service instance
     * Alias for getService() for better readability
     * 
     * @return The pharmacy service instance
     */
    public PharmacyService getPharmacyService() {
        // Check if service is properly initialized
        if (service == null) {
            System.err.println("WARNING: PharmacyService is null in MainFrame - initializing new service");
            service = PharmacyService.getInstance();
        }
        
        // Ensure service has sample data
        if (service.getPatients() == null || service.getPatients().isEmpty() ||
            service.getDoctors() == null || service.getDoctors().isEmpty() ||
            service.getAdmins() == null || service.getAdmins().isEmpty()) {
            
            System.out.println("PharmacyService missing data - initializing with sample data");
            service.initialize();
        }
        
        // Debug
        System.out.println("PharmacyService patients: " + 
            (service.getPatients() != null ? service.getPatients().size() : "NULL"));
        System.out.println("PharmacyService doctors: " + 
            (service.getDoctors() != null ? service.getDoctors().size() : "NULL"));
        System.out.println("PharmacyService admins: " + 
            (service.getAdmins() != null ? service.getAdmins().size() : "NULL"));
            
        return service;
    }
    

} 