package gui.doctor;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeManager;
import models.Doctor;
import models.Patient;
import models.Prescription;
import models.Consultation;
import gui.components.RoundedBorder;
import gui.theme.ThemeSizes;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Dashboard panel for Doctor users
 * Displays patient list, prescriptions, and consultations
 */
public class DoctorDashboard extends BasePanel {

    private Doctor currentDoctor;
    private JPanel summaryPanel;
    private JPanel statsPanel;
    
    public DoctorDashboard(MainFrame mainFrame) {
        super(mainFrame);
        try {
        this.currentDoctor = (Doctor) mainFrame.getCurrentUser();
        initializeComponents();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error initializing doctor dashboard: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            
            // Create a simple fallback panel
            setLayout(new BorderLayout());
            JLabel errorLabel = new JLabel("Could not load Doctor Dashboard. Please log out and try again.");
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            add(errorLabel, BorderLayout.CENTER);
        }
    }
    
    @Override
    protected void initializeComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Initialize panels first
        createSummaryPanel();
        createStatsPanel();
        
        // Apply card styling to summary and stats panels
        ThemeManager.applyCardStyling(summaryPanel);
        ThemeManager.applyCardStyling(statsPanel);
        
        // Create main content with dashboard items
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Welcome message
        String welcomeText = "Welcome, Dr. ";
        try {
            if (currentDoctor.getName() != null) {
                String name = currentDoctor.getName();
                if (name.startsWith("Dr. ")) {
                    welcomeText += name.substring(4);
                } else {
                    welcomeText += name;
                }
            } else if (currentDoctor.getUsername() != null) {
                welcomeText += currentDoctor.getUsername();
            } else {
                welcomeText += "Doctor";
            }
        } catch (Exception e) {
            welcomeText += "Doctor";
            System.out.println("Error getting doctor name: " + e.getMessage());
        }
        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setFont(ThemeFonts.BOLD_XXLARGE);
        welcomeLabel.setForeground(ThemeColors.PRIMARY);
        welcomeLabel.setIcon(ThemeIcons.DOCTOR);
        
        // Refresh button
        StyledButton refreshButton = new StyledButton("Refresh", ThemeIcons.REFRESH);
        refreshButton.addActionListener(e -> refreshData());
        
        // Logout button
        StyledButton logoutButton = new StyledButton("Logout", ThemeIcons.LOGOUT);
        logoutButton.addActionListener(e -> mainFrame.logout());
        
        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(ThemeColors.SURFACE);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        
        panel.add(welcomeLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // Add summary and stats panels side by side
        panel.add(summaryPanel);
        panel.add(statsPanel);
        
        return panel;
    }
    
    private void createSummaryPanel() {
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BorderLayout(0, 10));
        
        // Panel title
        JLabel titleLabel = new JLabel("Doctor Actions");
        titleLabel.setFont(ThemeFonts.BOLD_LARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setIcon(ThemeIcons.SETTINGS);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Action buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        buttonsPanel.setBackground(ThemeColors.SURFACE);
        
        // Patient list button
        StyledButton patientListBtn = new StyledButton("Patient List", ThemeIcons.USERS);
        patientListBtn.addActionListener(e -> {
            mainFrame.navigateTo("PATIENT_LIST");
        });
        
        // Prescriptions button
        StyledButton prescriptionsBtn = new StyledButton("Prescriptions", ThemeIcons.PRESCRIPTION);
        prescriptionsBtn.addActionListener(e -> {
            mainFrame.navigateTo("PRESCRIPTIONS");
        });
        
        // Consultations button
        StyledButton consultationsBtn = new StyledButton("Consultations", ThemeIcons.CONSULTATION);
        consultationsBtn.addActionListener(e -> {
            mainFrame.navigateTo("CONSULTATIONS");
        });
        
        // Medical Records button
        StyledButton medicalRecordsBtn = new StyledButton("Medical Records", ThemeIcons.REPORT);
        medicalRecordsBtn.addActionListener(e -> {
            mainFrame.navigateTo("MEDICAL_RECORDS");
        });
        
        // Add buttons to panel
        buttonsPanel.add(patientListBtn);
        buttonsPanel.add(prescriptionsBtn);
        buttonsPanel.add(consultationsBtn);
        buttonsPanel.add(medicalRecordsBtn);
        
        // Add to summary panel
        summaryPanel.add(titleLabel, BorderLayout.NORTH);
        summaryPanel.add(buttonsPanel, BorderLayout.CENTER);
    }
    
    private void createStatsPanel() {
        if (currentDoctor == null) {
            statsPanel = new JPanel();
            statsPanel.add(new JLabel("Doctor data unavailable."));
            return;
        }
        statsPanel = new JPanel();
        statsPanel.setLayout(new BorderLayout(0, 10));
        
        // Panel title
        JLabel titleLabel = new JLabel("Doctor Statistics");
        titleLabel.setFont(ThemeFonts.BOLD_LARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setIcon(ThemeIcons.CHART);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Stats content
        JPanel statsContent = new JPanel(new GridLayout(4, 1, 0, 10));
        statsContent.setBackground(ThemeColors.SURFACE);
        
        // Get stats
        int totalPatients = getTotalPatients();
        int activePrescriptions = getActivePrescriptions();
        int pendingConsultations = getPendingConsultations();
        
        // Stats rows
        JPanel patientsRow = createStatRow("Total Patients", String.valueOf(totalPatients), ThemeColors.INFO);
        JPanel prescriptionsRow = createStatRow("Active Prescriptions", String.valueOf(activePrescriptions), ThemeColors.SUCCESS);
        JPanel consultationsRow = createStatRow("Pending Consultations", String.valueOf(pendingConsultations), ThemeColors.WARNING);
        // Fix for swapped specialization and license data
        String specialization = currentDoctor.getSpecialization();
        String licenseNumber = currentDoctor.getLicenseNumber();
        // Detect if they're swapped (license usually starts with letters, specialization is a medical field)
        if (specialization != null && specialization.startsWith("EGP") && licenseNumber != null && 
            !licenseNumber.startsWith("EGP") && !licenseNumber.startsWith("L")) {
            // They are swapped
            String temp = specialization;
            specialization = licenseNumber;
            licenseNumber = temp;
        }
        // Make title larger by 1.2
        titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getSize2D() * 1.2f));
        JPanel specialtyRow = createStatRow("Specialty", specialization, ThemeColors.PRIMARY);
        
        // Add rows to content
        statsContent.add(patientsRow);
        statsContent.add(prescriptionsRow);
        statsContent.add(consultationsRow);
        statsContent.add(specialtyRow);
        
        // Add to stats panel
        statsPanel.add(titleLabel, BorderLayout.NORTH);
        statsPanel.add(statsContent, BorderLayout.CENTER);
    }
    
    private JPanel createStatRow(String title, String value, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeFonts.BOLD_SECTION);
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        JLabel valueLabel = new JLabel(value, SwingConstants.RIGHT);
        valueLabel.setFont(ThemeFonts.FUTURISTIC_LARGE);
        valueLabel.setForeground(color);
        row.add(titleLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }
    
    private int getTotalPatients() {
        // In a real application, this would fetch the doctor's patients
        // For now, return a placeholder value
        return 15;
    }
    
    private int getActivePrescriptions() {
        // In a real application, this would fetch the doctor's active prescriptions
        // For now, return a placeholder value
        List<Prescription> prescriptions = currentDoctor.getIssuedPrescriptions();
        return prescriptions != null ? prescriptions.size() : 0;
    }
    
    private int getPendingConsultations() {
        // In a real application, this would fetch the doctor's pending consultations
        // For now, return a placeholder value
        List<Consultation> consultations = currentDoctor.getConsultations();
        int pending = 0;
        if (consultations != null) {
            for (Consultation consultation : consultations) {
                if ("Pending".equals(consultation.getStatus())) {
                    pending++;
                }
            }
        }
        return pending;
    }
    
    private void refreshData() {
        // Refresh data and update UI
        createSummaryPanel();
        createStatsPanel();
        revalidate();
        repaint();
        JOptionPane.showMessageDialog(this, 
            "Dashboard data refreshed", 
            "Refresh", 
            JOptionPane.INFORMATION_MESSAGE);
    }
} 