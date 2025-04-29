package gui.doctor;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.StyledTable;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import models.Doctor;
import models.MedicalReport;
import models.Patient;
import services.PharmacyService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for managing medical records by doctors
 */
public class MedicalRecordsPanel extends BasePanel {

    private Doctor currentDoctor;
    private JTable recordsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<MedicalReport> reportList;
    private PharmacyService pharmacyService;

    public MedicalRecordsPanel(MainFrame mainFrame) {
        super(mainFrame);
        try {
        this.currentDoctor = (Doctor) mainFrame.getCurrentUser();
        this.pharmacyService = PharmacyService.getInstance();
        this.reportList = new ArrayList<>();
        initializeComponents();
        loadReportData();
        } catch (Exception e) {
            e.printStackTrace();
            setLayout(new BorderLayout());
            JLabel errorLabel = new JLabel("Could not load Medical Records. Please try again later.");
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

        // Create medical records table
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
        JLabel titleLabel = new JLabel("Medical Records Management");
        titleLabel.setFont(ThemeFonts.BOLD_XXLARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(ThemeColors.SURFACE);

        searchField = new JTextField(20);
        JButton searchButton = new StyledButton("Search", null);
        searchButton.addActionListener(e -> filterRecords());

        searchPanel.add(new JLabel("Search Records: "));
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
        String[] columnNames = {"ID", "Patient", "Date", "Type", "Summary"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells not editable
            }
        };

        // Create and configure table
        recordsTable = new StyledTable(tableModel);
        recordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recordsTable.getTableHeader().setReorderingAllowed(false);
        recordsTable.setRowHeight(30);

        // Column widths
        recordsTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        recordsTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Patient
        recordsTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Date
        recordsTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Type
        recordsTable.getColumnModel().getColumn(4).setPreferredWidth(300);  // Summary

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(recordsTable);
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
        JButton viewButton = new StyledButton("View Record", null);
        JButton createButton = new StyledButton("Create New Record", null);
        JButton updateButton = new StyledButton("Update Record", null);
        JButton backButton = new StyledButton("Back to Dashboard", null);

        // Add action listeners
        viewButton.addActionListener(e -> viewRecord());
        createButton.addActionListener(e -> createNewRecord());
        updateButton.addActionListener(e -> updateRecord());
        backButton.addActionListener(e -> mainFrame.navigateTo("DASHBOARD"));

        // Add buttons to panel
        panel.add(viewButton);
        panel.add(createButton);
        panel.add(updateButton);
        panel.add(backButton);

        return panel;
    }

    private void loadReportData() {
        // Clear existing data
        tableModel.setRowCount(0);
        reportList.clear();

        // Get medical records issued by this doctor
        reportList.addAll(currentDoctor.getIssuedReports());
        
        // Add reports to the table
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (MedicalReport report : reportList) {
            // Find patient name
            Patient patient = findPatientById(report.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            Object[] rowData = {
                report.getId(),
                patientName,
                report.getDate().format(formatter),
                report.getType(),
                truncateText(report.getSummary(), 50)
            };
            tableModel.addRow(rowData);
        }
        
        // If no reports, show a message
        if (reportList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No medical records found for this doctor.",
                    "Medical Records",
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

    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    private void filterRecords() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        
        if (searchTerm.isEmpty()) {
            loadReportData(); // If search is empty, reload all data
            return;
        }
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Format for date display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Filter records based on search term
        for (MedicalReport report : reportList) {
            // Find patient name
            Patient patient = findPatientById(report.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            // Check if any of the report's data contains the search term
            if (String.valueOf(report.getId()).contains(searchTerm) ||
                    patientName.toLowerCase().contains(searchTerm) ||
                    report.getDate().format(formatter).contains(searchTerm) ||
                    report.getType().toLowerCase().contains(searchTerm) ||
                    report.getSummary().toLowerCase().contains(searchTerm)) {
                
                Object[] rowData = {
                    report.getId(),
                    patientName,
                    report.getDate().format(formatter),
                    report.getType(),
                    truncateText(report.getSummary(), 50)
                };
                tableModel.addRow(rowData);
            }
        }
        
        // If no results found
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No medical records found matching '" + searchTerm + "'",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewRecord() {
        int selectedRow = recordsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a medical record from the list.",
                    "View Record",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected record ID
        int recordId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find the record in the list
        MedicalReport selectedReport = null;
        for (MedicalReport report : reportList) {
            if (report.getId() == recordId) {
                selectedReport = report;
                break;
            }
        }
        
        if (selectedReport != null) {
            // Find patient
            Patient patient = findPatientById(selectedReport.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            // Display record details in a dialog
            JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
            
            // Header info
            JPanel headerPanel = new JPanel(new GridLayout(0, 2, 5, 5));
            headerPanel.add(new JLabel("Record ID:"));
            headerPanel.add(new JLabel(String.valueOf(selectedReport.getId())));
            headerPanel.add(new JLabel("Patient:"));
            headerPanel.add(new JLabel(patientName));
            headerPanel.add(new JLabel("Date:"));
            headerPanel.add(new JLabel(selectedReport.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            headerPanel.add(new JLabel("Type:"));
            headerPanel.add(new JLabel(selectedReport.getType()));
            
            // Summary section
            JPanel summaryPanel = new JPanel(new BorderLayout(5, 5));
            summaryPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
            JLabel summaryLabel = new JLabel("Summary:");
            summaryLabel.setFont(ThemeFonts.BOLD_MEDIUM);
            
            JTextArea summaryArea = new JTextArea(selectedReport.getSummary());
            summaryArea.setLineWrap(true);
            summaryArea.setWrapStyleWord(true);
            summaryArea.setEditable(false);
            summaryArea.setRows(4);
            JScrollPane summaryScroll = new JScrollPane(summaryArea);
            
            summaryPanel.add(summaryLabel, BorderLayout.NORTH);
            summaryPanel.add(summaryScroll, BorderLayout.CENTER);
            
            // Content section
            JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
            JLabel contentLabel = new JLabel("Detailed Content:");
            contentLabel.setFont(ThemeFonts.BOLD_MEDIUM);
            
            JTextArea contentArea = new JTextArea(selectedReport.getContent());
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            contentArea.setEditable(false);
            contentArea.setRows(10);
            JScrollPane contentScroll = new JScrollPane(contentArea);
            
            contentPanel.add(contentLabel, BorderLayout.NORTH);
            contentPanel.add(contentScroll, BorderLayout.CENTER);
            
            // Add all sections to the main panel
            detailsPanel.add(headerPanel, BorderLayout.NORTH);
            detailsPanel.add(summaryPanel, BorderLayout.CENTER);
            detailsPanel.add(contentPanel, BorderLayout.SOUTH);
            
            // Show in dialog
            JOptionPane.showMessageDialog(
                this,
                detailsPanel,
                "Medical Record Details",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void createNewRecord() {
        // Patient selection
        JComboBox<String> patientComboBox = new JComboBox<>();
        List<Patient> patients = pharmacyService.getAllPatients();
        
        for (Patient patient : patients) {
            patientComboBox.addItem(patient.getName() + " (ID: " + patient.getId() + ")");
        }
        
        // Record type selection
        String[] recordTypes = {"General Check-up", "Diagnosis", "Lab Results", "Treatment Plan", "Follow-up", "Specialist Referral"};
        JComboBox<String> typeComboBox = new JComboBox<>(recordTypes);
        
        // Form for creating a new medical record
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        formPanel.add(new JLabel("Patient:"));
        formPanel.add(patientComboBox);
        
        formPanel.add(new JLabel("Date:"));
        JTextField dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        formPanel.add(dateField);
        
        formPanel.add(new JLabel("Record Type:"));
        formPanel.add(typeComboBox);
        
        formPanel.add(new JLabel("Summary:"));
        JTextField summaryField = new JTextField();
        formPanel.add(summaryField);
        
        formPanel.add(new JLabel("Detailed Content:"));
        JTextArea contentArea = new JTextArea(10, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        
        // Add the scroll pane in a separate panel to span both columns
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(contentScroll, BorderLayout.CENTER);
        
        // Create a panel with empty elements for the grid
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(formPanel, BorderLayout.NORTH);
        combinedPanel.add(contentPanel, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            combinedPanel,
            "Create New Medical Record",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            // Extract patient ID from selection (format: "Name (ID: X)")
            String patientSelection = (String) patientComboBox.getSelectedItem();
            int patientId = Integer.parseInt(patientSelection.substring(
                patientSelection.lastIndexOf("ID: ") + 4,
                patientSelection.length() - 1
            ));
            
            // Parse date
            LocalDate date;
            try {
                date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid date format. Please use YYYY-MM-DD.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            String type = (String) typeComboBox.getSelectedItem();
            String summary = summaryField.getText().trim();
            String content = contentArea.getText().trim();
            
            // Validate inputs
            if (summary.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Summary and content cannot be empty.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Generate new record ID
            int newId = 1;
            if (!reportList.isEmpty()) {
                newId = reportList.stream().mapToInt(MedicalReport::getId).max().orElse(0) + 1;
            }
            
            // Create new medical record
            MedicalReport newReport = new MedicalReport(
                newId,
                patientId,
                currentDoctor.getId(),
                date,
                type,
                summary,
                content
            );
            
            // Add to doctor's records
            currentDoctor.addReport(newReport);
            
            // Update table
            reportList.add(newReport);
            Object[] rowData = {
                newReport.getId(),
                findPatientById(patientId).getName(),
                newReport.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                newReport.getType(),
                truncateText(newReport.getSummary(), 50)
            };
            tableModel.addRow(rowData);
            
            JOptionPane.showMessageDialog(
                this,
                "Medical record created successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void updateRecord() {
        int selectedRow = recordsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a medical record from the list.",
                    "Update Record",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected record ID
        int recordId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find the record in the list
        MedicalReport selectedReport = null;
        for (MedicalReport report : reportList) {
            if (report.getId() == recordId) {
                selectedReport = report;
                break;
            }
        }
        
        if (selectedReport != null) {
            // Record type selection
            String[] recordTypes = {"General Check-up", "Diagnosis", "Lab Results", "Treatment Plan", "Follow-up", "Specialist Referral"};
            JComboBox<String> typeComboBox = new JComboBox<>(recordTypes);
            typeComboBox.setSelectedItem(selectedReport.getType());
            
            // Form for updating the medical record
            JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
            
            // Find patient name
            Patient patient = findPatientById(selectedReport.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            formPanel.add(new JLabel("Patient:"));
            formPanel.add(new JLabel(patientName));
            
            formPanel.add(new JLabel("Date:"));
            JTextField dateField = new JTextField(
                selectedReport.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            formPanel.add(dateField);
            
            formPanel.add(new JLabel("Record Type:"));
            formPanel.add(typeComboBox);
            
            formPanel.add(new JLabel("Summary:"));
            JTextField summaryField = new JTextField(selectedReport.getSummary());
            formPanel.add(summaryField);
            
            formPanel.add(new JLabel("Detailed Content:"));
            JTextArea contentArea = new JTextArea(10, 30);
            contentArea.setText(selectedReport.getContent());
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            JScrollPane contentScroll = new JScrollPane(contentArea);
            
            // Add the scroll pane in a separate panel to span both columns
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(contentScroll, BorderLayout.CENTER);
            
            // Create a panel with empty elements for the grid
            JPanel combinedPanel = new JPanel(new BorderLayout());
            combinedPanel.add(formPanel, BorderLayout.NORTH);
            combinedPanel.add(contentPanel, BorderLayout.CENTER);
            
            int result = JOptionPane.showConfirmDialog(
                this,
                combinedPanel,
                "Update Medical Record",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                // Parse date
                LocalDate date;
                try {
                    date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid date format. Please use YYYY-MM-DD.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                
                String type = (String) typeComboBox.getSelectedItem();
                String summary = summaryField.getText().trim();
                String content = contentArea.getText().trim();
                
                // Validate inputs
                if (summary.isEmpty() || content.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Summary and content cannot be empty.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                
                // Update the medical record
                selectedReport.setDate(date);
                selectedReport.setType(type);
                selectedReport.setSummary(summary);
                selectedReport.setContent(content);
                
                // Update table
                tableModel.setValueAt(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), selectedRow, 2);
                tableModel.setValueAt(type, selectedRow, 3);
                tableModel.setValueAt(truncateText(summary, 50), selectedRow, 4);
                
                JOptionPane.showMessageDialog(
                    this,
                    "Medical record updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }
} 