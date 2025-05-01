package gui.doctor;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.StyledTable;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import models.Doctor;
import models.Medicine;
import models.Patient;
import models.Prescription;
import models.PrescriptionStatus;
import services.PharmacyService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Panel for managing prescriptions issued by the doctor
 */
public class PrescriptionsPanel extends BasePanel {

    private Doctor currentDoctor;
    private JTable prescriptionsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Prescription> prescriptionList;
    private PharmacyService pharmacyService;

    public PrescriptionsPanel(MainFrame mainFrame) {
        super(mainFrame);
        try {
        this.currentDoctor = (Doctor) mainFrame.getCurrentUser();
        this.pharmacyService = PharmacyService.getInstance();
        this.prescriptionList = new ArrayList<>();
        initializeComponents();
        loadPrescriptionData();
        } catch (Exception e) {
            e.printStackTrace();
            setLayout(new BorderLayout());
            JLabel errorLabel = new JLabel("Could not load Prescriptions. Please try again later.");
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

        // Create prescriptions table
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
        JLabel titleLabel = new JLabel("Prescription Management");
        titleLabel.setFont(ThemeFonts.BOLD_XXLARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(ThemeColors.SURFACE);

        searchField = new JTextField(20);
        JButton searchButton = new StyledButton("Search", null);
        searchButton.addActionListener(e -> filterPrescriptions());

        searchPanel.add(new JLabel("Search Prescriptions: "));
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
        String[] columnNames = {"ID", "Patient", "Date Issued", "Expiry Date", "Status", "Instructions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells not editable
            }
        };

        // Create and configure table
        prescriptionsTable = new StyledTable(tableModel);
        prescriptionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prescriptionsTable.getTableHeader().setReorderingAllowed(false);
        prescriptionsTable.setRowHeight(30);

        // Column widths
        prescriptionsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        prescriptionsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Patient
        prescriptionsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Date Issued
        prescriptionsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Expiry Date
        prescriptionsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        prescriptionsTable.getColumnModel().getColumn(5).setPreferredWidth(300); // Instructions

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(prescriptionsTable);
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
        JButton viewButton = new StyledButton("View Details", null);
        JButton newButton = new StyledButton("Create New Prescription", null);
        JButton updateButton = new StyledButton("Update Status", null);
        JButton editMedsButton = new StyledButton("Edit Medicines", null);
        JButton backButton = new StyledButton("Back to Dashboard", null);

        // Add action listeners
        viewButton.addActionListener(e -> viewPrescriptionDetails());
        newButton.addActionListener(e -> createNewPrescription());
        updateButton.addActionListener(e -> updatePrescriptionStatus());
        editMedsButton.addActionListener(e -> editMedicinesInPrescription());
        backButton.addActionListener(e -> mainFrame.navigateTo("DASHBOARD"));

        // Add buttons to panel
        panel.add(viewButton);
        panel.add(newButton);
        panel.add(editMedsButton);
        panel.add(updateButton);
        panel.add(backButton);

        return panel;
    }

    private void loadPrescriptionData() {
        // Clear existing data
        tableModel.setRowCount(0);
        prescriptionList.clear();

        // Get prescriptions issued by this doctor
        prescriptionList.addAll(currentDoctor.getIssuedPrescriptions());
        
        // Add prescriptions to the table
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Prescription prescription : prescriptionList) {
            // Find patient name
            Patient patient = findPatientById(prescription.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            Object[] rowData = {
                prescription.getId(),
                patientName,
                prescription.getIssueDate().format(formatter),
                prescription.getExpiryDate().format(formatter),
                prescription.getStatus(),
                prescription.getInstructions()
            };
            tableModel.addRow(rowData);
        }
        
        // If no prescriptions, show a message
        if (prescriptionList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No prescriptions found for this doctor.",
                    "Prescription List",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private Patient findPatientById(int patientId) {
        List<Patient> patients = pharmacyService.getAllPatients();
        for (Patient patient : patients) {
            if (patient.getId() == patientId) {
                return patient;
            }
        }
        return null;
    }

    private void filterPrescriptions() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        
        if (searchTerm.isEmpty()) {
            loadPrescriptionData(); // If search is empty, reload all data
            return;
        }
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Format for date display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Filter prescriptions based on search term
        for (Prescription prescription : prescriptionList) {
            // Find patient name
            Patient patient = findPatientById(prescription.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            // Check if any of the prescription's data contains the search term
            if (String.valueOf(prescription.getId()).contains(searchTerm) ||
                    patientName.toLowerCase().contains(searchTerm) ||
                    prescription.getIssueDate().format(formatter).contains(searchTerm) ||
                    prescription.getExpiryDate().format(formatter).contains(searchTerm) ||
                    prescription.getStatus().toLowerCase().contains(searchTerm) ||
                    prescription.getInstructions().toLowerCase().contains(searchTerm)) {
                
                Object[] rowData = {
                    prescription.getId(),
                    patientName,
                    prescription.getIssueDate().format(formatter),
                    prescription.getExpiryDate().format(formatter),
                    prescription.getStatus(),
                    prescription.getInstructions()
                };
                tableModel.addRow(rowData);
            }
        }
        
        // If no results found
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No prescriptions found matching '" + searchTerm + "'",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewPrescriptionDetails() {
        int selectedRow = prescriptionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a prescription from the list.",
                    "View Prescription",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected prescription ID
        int prescriptionId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find the prescription in the list
        Prescription selectedPrescription = null;
        for (Prescription prescription : prescriptionList) {
            if (prescription.getId() == prescriptionId) {
                selectedPrescription = prescription;
                break;
            }
        }
        
        if (selectedPrescription != null) {
            // Find patient 
            Patient patient = findPatientById(selectedPrescription.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            // Display prescription details in a dialog
            StringBuilder details = new StringBuilder();
            details.append("Prescription ID: ").append(selectedPrescription.getId()).append("\n");
            details.append("Patient: ").append(patientName).append("\n");
            details.append("Issue Date: ").append(selectedPrescription.getIssueDate()).append("\n");
            details.append("Expiry Date: ").append(selectedPrescription.getExpiryDate()).append("\n");
            details.append("Status: ").append(selectedPrescription.getStatus()).append("\n");
            details.append("Instructions: ").append(selectedPrescription.getInstructions()).append("\n\n");
            
            details.append("Medications:\n");
            if (selectedPrescription.getMedicines().isEmpty()) {
                details.append("No medications added to this prescription yet.");
            } else {
                // Get the map of medicines and quantities
                Map<Medicine, Integer> medicinesMap = selectedPrescription.getMedicines();
                
                // Iterate through the map entries
                for (Map.Entry<Medicine, Integer> entry : medicinesMap.entrySet()) {
                    Medicine medicine = entry.getKey();
                    Integer quantity = entry.getValue();
                    details.append("- ").append(medicine.getName())
                           .append(", Quantity: ").append(quantity)
                           .append("\n");
                }
            }
            
            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(ThemeFonts.REGULAR_MEDIUM);
            textArea.setBackground(ThemeColors.SURFACE);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this,
                    scrollPane,
                    "Prescription Details",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createNewPrescription() {
        // Open patient selection dialog
        JComboBox<String> patientComboBox = new JComboBox<>();
        List<Patient> patients = pharmacyService.getAllPatients();
        Map<String, Integer> patientIdMap = new HashMap<>();
        
        for (Patient patient : patients) {
            String displayName = patient.getName() + " (ID: " + patient.getId() + ")";
            patientComboBox.addItem(displayName);
            patientIdMap.put(displayName, patient.getId());
        }
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Select Patient:"));
        formPanel.add(patientComboBox);
        
        JTextArea instructionsArea = new JTextArea(5, 20);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        JScrollPane instructionsScroll = new JScrollPane(instructionsArea);
        
        formPanel.add(new JLabel("Instructions:"));
        formPanel.add(instructionsScroll);
        
        int result = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Create New Prescription",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String selectedPatient = (String) patientComboBox.getSelectedItem();
            int patientId = patientIdMap.get(selectedPatient);
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
            if (!prescriptionList.isEmpty()) {
                newId = prescriptionList.stream().mapToInt(Prescription::getId).max().orElse(0) + 1;
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
                        "Prescription created successfully.\nYou can now add medicines to it.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                
                // Refresh the table
                loadPrescriptionData();
                
                // Open medication selection dialog
                addMedicationsToPrescription(newPrescription);
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
    
    private void addMedicationsToPrescription(Prescription prescription) {
        List<Medicine> medicines = pharmacyService.getMedicines();
        if (medicines == null || medicines.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                    "No medicines available in the system.",
                    "No Medicines",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean adding = true;
        while (adding) {
            // Build an array of medicine names for the combo box
            String[] medOptions = medicines.stream()
                    .map(m -> String.format("%d - %s (%.2f LE, In stock: %d)%s", 
                            m.getId(),
                            m.getName(),
                            m.getPrice(),
                            m.getQuantity(),
                            m.isRequiresPrescription() ? "*" : ""))
                    .toArray(String[]::new);

            JComboBox<String> medCombo = new JComboBox<>(medOptions);
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

            JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
            panel.add(new JLabel("Select Medicine:"));
            panel.add(medCombo);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantitySpinner);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Add Medicine to Prescription",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                break;
            }

            int selectedIdx = medCombo.getSelectedIndex();
            Medicine selectedMed = medicines.get(selectedIdx);
            int qty = (Integer) quantitySpinner.getValue();

            if (qty <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Quantity must be positive.",
                        "Invalid Quantity",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            // Check stock
            if (qty > selectedMed.getQuantity()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Not enough stock for this medicine.",
                        "Insufficient Stock",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            // Add medicine via doctor's helper to set prescription flag if needed
            if (currentDoctor.addMedicineToPrescription(prescription, selectedMed, qty)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Added " + qty + " x " + selectedMed.getName() + " to prescription.",
                        "Medicine Added",
                        JOptionPane.INFORMATION_MESSAGE);
                // Optionally deduct stock here
                selectedMed.setQuantity(selectedMed.getQuantity() - qty);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to add medicine.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            // Ask if the user wants to add another medicine
            int cont = JOptionPane.showConfirmDialog(this,
                    "Add another medicine?",
                    "Continue",
                    JOptionPane.YES_NO_OPTION);
            adding = (cont == JOptionPane.YES_OPTION);
        }

        // Save changes to the prescription & system
        pharmacyService.savePrescription(prescription);

        // Refresh table to reflect any status change
        loadPrescriptionData();
    }

    private void updatePrescriptionStatus() {
        int selectedRow = prescriptionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a prescription from the list.",
                    "Update Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected prescription ID
        int prescriptionId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find the prescription in the list
        Prescription selectedPrescription = null;
        for (Prescription prescription : prescriptionList) {
            if (prescription.getId() == prescriptionId) {
                selectedPrescription = prescription;
                break;
            }
        }
        
        if (selectedPrescription != null) {
            String[] statuses = {
                PrescriptionStatus.PENDING.getDisplayName(),
                PrescriptionStatus.VALIDATED.getDisplayName(),
                PrescriptionStatus.REJECTED.getDisplayName(),
                PrescriptionStatus.COMPLETED.getDisplayName(),
                PrescriptionStatus.CANCELLED.getDisplayName()
            };
            JComboBox<String> statusComboBox = new JComboBox<>(statuses);
            statusComboBox.setSelectedItem(selectedPrescription.getStatus().getDisplayName());
            
            int result = JOptionPane.showConfirmDialog(
                    this,
                    statusComboBox,
                    "Update Prescription Status",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String newStatusStr = (String) statusComboBox.getSelectedItem();
                PrescriptionStatus newStatus = PrescriptionStatus.fromString(newStatusStr);
                selectedPrescription.setStatus(newStatus);
                
                // Save the updated prescription to ensure changes are persisted
                pharmacyService.savePrescription(selectedPrescription);
                
                JOptionPane.showMessageDialog(
                        this,
                        "Prescription status updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                
                // Refresh the table
                loadPrescriptionData();
            }
        }
    }

    private void editMedicinesInPrescription() {
        int selectedRow = prescriptionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a prescription first.",
                    "Edit Medicines",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int prescriptionId = (int) tableModel.getValueAt(selectedRow, 0);
        Prescription selectedPrescription = null;
        for (Prescription p : prescriptionList) {
            if (p.getId() == prescriptionId) {
                selectedPrescription = p;
                break;
            }
        }

        if (selectedPrescription != null) {
            addMedicationsToPrescription(selectedPrescription);
        }
    }

    private void updatePrescriptionStatus(int prescriptionId, PrescriptionStatus newStatus) {
        PharmacyService service = mainFrame.getPharmacyService();
        if (service != null) {
            Doctor doctor = (Doctor) mainFrame.getCurrentUser();
            if (doctor != null) {
                if (newStatus == PrescriptionStatus.VALIDATED) {
                    doctor.validatePrescription(prescriptionId);
                } else if (newStatus == PrescriptionStatus.REJECTED) {
                    doctor.rejectPrescription(prescriptionId);
                }
                loadPrescriptionData();
            }
        }
    }
} 