package gui.doctor;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.StyledTable;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import models.Doctor;
import models.Patient;
import models.Prescription;
import services.PharmacyService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for displaying and managing the patients assigned to a doctor
 */
public class PatientListPanel extends BasePanel {

    private Doctor currentDoctor;
    private JTable patientsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Patient> patientList;
    private PharmacyService pharmacyService;

    public PatientListPanel(MainFrame mainFrame) {
        super(mainFrame);
        try {
        this.currentDoctor = (Doctor) mainFrame.getCurrentUser();
        this.pharmacyService = PharmacyService.getInstance();
        this.patientList = new ArrayList<>();
        initializeComponents();
        loadPatientData();
        } catch (Exception e) {
            e.printStackTrace();
            setLayout(new BorderLayout());
            JLabel errorLabel = new JLabel("Could not load Patient List. Please try again later.");
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

        // Create patient table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Create bottom panel with buttons
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(ThemeColors.BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Add title
        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(ThemeFonts.BOLD_XXLARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(ThemeColors.SURFACE);

        searchField = new JTextField(20);
        JButton searchButton = new StyledButton("Search", null);
        searchButton.addActionListener(e -> filterPatients());

        searchPanel.add(new JLabel("Search Patients: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(ThemeColors.BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Create table model
        String[] columnNames = {"ID", "Name", "Age", "Gender", "Contact", "Medical History"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells not editable
            }
        };

        // Create and configure table
        patientsTable = new StyledTable(tableModel);
        patientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientsTable.getTableHeader().setReorderingAllowed(false);
        patientsTable.setRowHeight(30);

        // Column widths
        patientsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        patientsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        patientsTable.getColumnModel().getColumn(2).setPreferredWidth(50);  // Age
        patientsTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Gender
        patientsTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Contact
        patientsTable.getColumnModel().getColumn(5).setPreferredWidth(300); // Medical History

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(patientsTable);
        scrollPane.setBorder(null);
        scrollPane.setBackground(ThemeColors.SURFACE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(ThemeColors.BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Buttons for actions
        JButton viewButton = new StyledButton("View Patient Details", null);
        JButton prescribeButton = new StyledButton("Create Prescription", null);
        JButton consultButton = new StyledButton("Schedule Consultation", null);
        JButton backButton = new StyledButton("Back to Dashboard", null);

        // Add action listeners
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPatientDetails();
            }
        });

        prescribeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPrescription();
            }
        });

        consultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scheduleConsultation();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.navigateTo("DASHBOARD");
            }
        });

        // Add buttons to panel
        panel.add(viewButton);
        panel.add(prescribeButton);
        panel.add(consultButton);
        panel.add(backButton);

        return panel;
    }

    private void loadPatientData() {
        // Clear existing data
        tableModel.setRowCount(0);
        patientList.clear();

        // Simulate fetching patients from the service
        // In a real application, this would get patients assigned to the doctor
        List<Patient> allPatients = pharmacyService.getAllPatients();
        
        // For demonstration, we'll assume all patients are accessible to this doctor
        // In a real app, you'd filter by doctor assignments
        patientList.addAll(allPatients);
        
        // Add patients to the table
        for (Patient patient : patientList) {
            Object[] rowData = {
                patient.getId(),
                patient.getName(),
                patient.getAge(),
                patient.getGender(),
                patient.getContactInfo(),
                patient.getMedicalHistory().isEmpty() ? "None" : patient.getMedicalHistory()
            };
            tableModel.addRow(rowData);
        }
        
        // If no patients, show a message
        if (patientList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No patients found for this doctor.",
                    "Patient List",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterPatients() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        
        if (searchTerm.isEmpty()) {
            loadPatientData(); // If search is empty, reload all data
            return;
        }
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Filter patients based on search term
        for (Patient patient : patientList) {
            // Check if any of the patient's data contains the search term
            if (String.valueOf(patient.getId()).contains(searchTerm) ||
                    patient.getName().toLowerCase().contains(searchTerm) ||
                    String.valueOf(patient.getAge()).contains(searchTerm) ||
                    patient.getGender().toLowerCase().contains(searchTerm) ||
                    patient.getContactInfo().toLowerCase().contains(searchTerm) ||
                    patient.getMedicalHistory().toLowerCase().contains(searchTerm)) {
                
                Object[] rowData = {
                    patient.getId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getGender(),
                    patient.getContactInfo(),
                    patient.getMedicalHistory().isEmpty() ? "None" : patient.getMedicalHistory()
                };
                tableModel.addRow(rowData);
            }
        }
        
        // If no results found
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No patients found matching '" + searchTerm + "'",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewPatientDetails() {
        int selectedRow = patientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient from the list.",
                    "View Patient",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected patient ID
        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find the patient in the list
        Patient selectedPatient = patientList.stream()
                .filter(p -> p.getId() == patientId)
                .findFirst()
                .orElse(null);
        
        if (selectedPatient != null) {
            // Display patient details in a dialog
            StringBuilder details = new StringBuilder();
            details.append("Patient ID: ").append(selectedPatient.getId()).append("\n");
            details.append("Name: ").append(selectedPatient.getName()).append("\n");
            details.append("Age: ").append(selectedPatient.getAge()).append("\n");
            details.append("Gender: ").append(selectedPatient.getGender()).append("\n");
            details.append("Contact: ").append(selectedPatient.getContactInfo()).append("\n");
            details.append("Medical History: ").append(
                    selectedPatient.getMedicalHistory().isEmpty() ? "None" : selectedPatient.getMedicalHistory()
            );
            
            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(ThemeFonts.REGULAR_MEDIUM);
            textArea.setBackground(ThemeColors.SURFACE);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this,
                    scrollPane,
                    "Patient Details: " + selectedPatient.getName(),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createPrescription() {
        int selectedRow = patientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient from the list.",
                    "Create Prescription",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected patient ID
        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Create form for prescription details
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        JLabel instructionsLabel = new JLabel("Prescription Instructions:");
        JTextArea instructionsArea = new JTextArea(5, 30);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        JScrollPane instructionsScroll = new JScrollPane(instructionsArea);
        
        formPanel.add(instructionsLabel);
        formPanel.add(instructionsScroll);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            formPanel,
            "Create Prescription for " + patientName,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String instructions = instructionsArea.getText().trim();
            
            if (instructions.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Instructions cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Generate a new prescription ID
            int newId = 1;
            List<Prescription> existingPrescriptions = currentDoctor.getIssuedPrescriptions();
            if (!existingPrescriptions.isEmpty()) {
                newId = existingPrescriptions.stream().mapToInt(Prescription::getId).max().orElse(0) + 1;
            }
            
            // Create the prescription
            Prescription newPrescription = currentDoctor.createPrescription(
                patientId,
                newId,
                instructions
            );
            
            if (newPrescription != null) {
                // Save to pharmacy service to ensure it's in the central list
                pharmacyService.savePrescription(newPrescription);
                
                JOptionPane.showMessageDialog(
                    this,
                    "Prescription created successfully.\nYou can now add medicines from the Prescriptions panel.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                // Navigate to the Prescriptions panel
                mainFrame.navigateTo("PRESCRIPTIONS");
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to create prescription",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void scheduleConsultation() {
        int selectedRow = patientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient from the list.",
                    "Schedule Consultation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected patient ID
        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Simple dialog to schedule consultation
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JTextField dateField = new JTextField(10);
        JTextField timeField = new JTextField(10);
        JTextField reasonField = new JTextField(20);
        
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Time (HH:MM):"));
        panel.add(timeField);
        panel.add(new JLabel("Reason:"));
        panel.add(reasonField);
        
        int result = JOptionPane.showConfirmDialog(this, 
                panel, 
                "Schedule Consultation for " + patientName, 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            String reason = reasonField.getText().trim();
            
            if (date.isEmpty() || time.isEmpty() || reason.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "All fields are required to schedule a consultation.",
                        "Incomplete Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // In a real application, you would create a Consultation object
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(date + " " + time, formatter);

                // Generate new consultation ID
                int newId = 1;
                java.util.List<models.Consultation> existing = pharmacyService.getConsultations();
                if (!existing.isEmpty()) {
                    newId = existing.stream().mapToInt(models.Consultation::getId).max().orElse(0) + 1;
                }

                // Create consultation object
                models.Consultation consultation = new models.Consultation(
                        newId,
                        currentDoctor.getId(),
                        patientId,
                        dateTime,
                        reason,
                        "Pending"
                );

                // Save via service (links to doctor and patient and persists)
                if (pharmacyService.saveDoctorConsultation(consultation)) {
            JOptionPane.showMessageDialog(this,
                    "Consultation scheduled for " + patientName + " on " + date + " at " + time,
                    "Consultation Scheduled",
                    JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to save consultation. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date/time format. Please use YYYY-MM-DD for date and HH:MM for time.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 