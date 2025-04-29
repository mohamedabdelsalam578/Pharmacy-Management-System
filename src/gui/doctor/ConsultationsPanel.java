package gui.doctor;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.StyledTable;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import models.Consultation;
import models.Doctor;
import models.Message;
import models.Patient;
import models.Prescription;
import services.PharmacyService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for doctors to manage patient consultations
 */
public class ConsultationsPanel extends BasePanel {

    private Doctor currentDoctor;
    private JTable consultationsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Consultation> consultationList;
    private PharmacyService pharmacyService;

    public ConsultationsPanel(MainFrame mainFrame) {
        super(mainFrame);
        try {
        this.currentDoctor = (Doctor) mainFrame.getCurrentUser();
        this.pharmacyService = PharmacyService.getInstance();
        this.consultationList = new ArrayList<>();
        initializeComponents();
        loadConsultationData();
        } catch (Exception e) {
            e.printStackTrace();
            setLayout(new BorderLayout());
            JLabel errorLabel = new JLabel("Could not load Consultations. Please try again later.");
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

        // Create consultations table
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
        JLabel titleLabel = new JLabel("Patient Consultations");
        titleLabel.setFont(ThemeFonts.BOLD_XXLARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(ThemeColors.SURFACE);

        searchField = new JTextField(20);
        JButton searchButton = new StyledButton("Search", null);
        searchButton.addActionListener(e -> filterConsultations());

        searchPanel.add(new JLabel("Search Consultations: "));
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
        String[] columnNames = {"ID", "Patient", "Date/Time", "Status", "Messages", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells not editable
            }
        };

        // Create and configure table
        consultationsTable = new StyledTable(tableModel);
        consultationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        consultationsTable.getTableHeader().setReorderingAllowed(false);
        consultationsTable.setRowHeight(30);

        // Column widths
        consultationsTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        consultationsTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Patient
        consultationsTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Date/Time
        consultationsTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Status
        consultationsTable.getColumnModel().getColumn(4).setPreferredWidth(80);   // Messages
        consultationsTable.getColumnModel().getColumn(5).setPreferredWidth(300);  // Notes

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(consultationsTable);
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
        JButton viewButton = new StyledButton("View Consultation", null);
        JButton replyButton = new StyledButton("Reply to Patient", null);
        JButton updateButton = new StyledButton("Update Status", null);
        JButton prescribeButton = new StyledButton("Create Prescription", null);
        JButton backButton = new StyledButton("Back to Dashboard", null);

        // Add action listeners
        viewButton.addActionListener(e -> viewConsultation());
        replyButton.addActionListener(e -> replyToPatient());
        updateButton.addActionListener(e -> updateConsultationStatus());
        prescribeButton.addActionListener(e -> createPrescriptionFromConsultation());
        backButton.addActionListener(e -> mainFrame.navigateTo("DASHBOARD"));

        // Add buttons to panel
        panel.add(viewButton);
        panel.add(replyButton);
        panel.add(updateButton);
        panel.add(prescribeButton);
        panel.add(backButton);

        return panel;
    }

    private void loadConsultationData() {
        // Clear existing data
        tableModel.setRowCount(0);
        consultationList.clear();

        // Get consultations for this doctor
        consultationList.addAll(currentDoctor.getConsultations());
        
        // Add consultations to the table
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Consultation consultation : consultationList) {
            // Find patient name
            Patient patient = findPatientById(consultation.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            Object[] rowData = {
                consultation.getId(),
                patientName,
                consultation.getDateTime().format(formatter),
                consultation.getStatus(),
                consultation.getMessages().size(),
                truncateText(consultation.getNotes(), 50)
            };
            tableModel.addRow(rowData);
        }
        
        // If no consultations, show a message
        if (consultationList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No consultations found for this doctor.",
                    "Consultation List",
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

    private void filterConsultations() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        
        if (searchTerm.isEmpty()) {
            loadConsultationData(); // If search is empty, reload all data
            return;
        }
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Format for date display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        // Filter consultations based on search term
        for (Consultation consultation : consultationList) {
            // Find patient name
            Patient patient = findPatientById(consultation.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            // Check if any of the consultation's data contains the search term
            if (String.valueOf(consultation.getId()).contains(searchTerm) ||
                    patientName.toLowerCase().contains(searchTerm) ||
                    consultation.getDateTime().format(formatter).toLowerCase().contains(searchTerm) ||
                    consultation.getStatus().toLowerCase().contains(searchTerm) ||
                    consultation.getNotes().toLowerCase().contains(searchTerm)) {
                
                Object[] rowData = {
                    consultation.getId(),
                    patientName,
                    consultation.getDateTime().format(formatter),
                    consultation.getStatus(),
                    consultation.getMessages().size(),
                    truncateText(consultation.getNotes(), 50)
                };
                tableModel.addRow(rowData);
            }
        }
        
        // If no results found
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No consultations found matching '" + searchTerm + "'",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewConsultation() {
        int selectedRow = consultationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a consultation from the list.",
                    "View Consultation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected consultation ID
        int consultationId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find the consultation in the list
        Consultation selectedConsultation = findConsultationById(consultationId);
        
        if (selectedConsultation != null) {
            displayConsultationDetails(selectedConsultation);
        }
    }
    
    private Consultation findConsultationById(int consultationId) {
        for (Consultation consultation : consultationList) {
            if (consultation.getId() == consultationId) {
                return consultation;
            }
        }
        return null;
    }
    
    private void displayConsultationDetails(Consultation consultation) {
        // Find patient name
        Patient patient = findPatientById(consultation.getPatientId());
        String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
        
        // Create consultation details panel
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        
        // Header with consultation info
        JPanel headerPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        headerPanel.add(new JLabel("Consultation ID:"));
        headerPanel.add(new JLabel(String.valueOf(consultation.getId())));
        headerPanel.add(new JLabel("Patient:"));
        headerPanel.add(new JLabel(patientName));
        headerPanel.add(new JLabel("Date/Time:"));
        headerPanel.add(new JLabel(consultation.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        headerPanel.add(new JLabel("Status:"));
        headerPanel.add(new JLabel(consultation.getStatus()));
        
        // Notes section
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        JLabel notesLabel = new JLabel("Consultation Notes:");
        notesLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        
        JTextArea notesArea = new JTextArea(consultation.getNotes());
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setEditable(false);
        notesArea.setRows(4);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        
        notesPanel.add(notesLabel, BorderLayout.NORTH);
        notesPanel.add(notesScroll, BorderLayout.CENTER);
        
        // Messages section
        JPanel messagesPanel = new JPanel(new BorderLayout());
        JLabel messagesLabel = new JLabel("Conversation:");
        messagesLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        
        // Create message list
        DefaultListModel<String> messageListModel = new DefaultListModel<>();
        
        if (consultation.getMessages().isEmpty()) {
            messageListModel.addElement("No messages in this consultation yet.");
        } else {
            DateTimeFormatter msgFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (Message message : consultation.getMessages()) {
                String sender;
                if (message.getSenderId() == consultation.getDoctorId()) {
                    sender = "Doctor";
                } else if (message.getSenderId() == consultation.getPatientId()) {
                    sender = patientName;
                } else {
                    sender = "Unknown";
                }
                
                messageListModel.addElement(
                    "[" + message.getTimestamp().format(msgFormatter) + "] " + 
                    sender + ": " + message.getContent()
                );
            }
        }
        
        JList<String> messageList = new JList<>(messageListModel);
        messageList.setFont(ThemeFonts.REGULAR_MEDIUM);
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane messageScroll = new JScrollPane(messageList);
        messageScroll.setPreferredSize(new Dimension(450, 200));
        
        messagesPanel.add(messagesLabel, BorderLayout.NORTH);
        messagesPanel.add(messageScroll, BorderLayout.CENTER);
        
        // Add all sections to the main panel
        detailsPanel.add(headerPanel, BorderLayout.NORTH);
        detailsPanel.add(notesPanel, BorderLayout.CENTER);
        detailsPanel.add(messagesPanel, BorderLayout.SOUTH);
        
        // Show in dialog
        JOptionPane.showMessageDialog(
            this,
            detailsPanel,
            "Consultation Details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void replyToPatient() {
        int selectedRow = consultationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a consultation from the list.",
                    "Reply to Patient",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected consultation ID
        int consultationId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find the consultation in the list
        Consultation selectedConsultation = findConsultationById(consultationId);
        
        if (selectedConsultation != null) {
            // Find patient
            Patient patient = findPatientById(selectedConsultation.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            
            // Create reply dialog
            JPanel replyPanel = new JPanel(new BorderLayout(5, 5));
            
            JLabel instructionLabel = new JLabel("Send message to " + patientName + ":");
            
            JTextArea messageArea = new JTextArea(5, 30);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            JScrollPane messageScroll = new JScrollPane(messageArea);
            
            replyPanel.add(instructionLabel, BorderLayout.NORTH);
            replyPanel.add(messageScroll, BorderLayout.CENTER);
            
            int result = JOptionPane.showConfirmDialog(
                this,
                replyPanel,
                "Reply to Patient",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String messageContent = messageArea.getText().trim();
                
                if (messageContent.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Message cannot be empty",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                
                // Create a new message
                int messageId = selectedConsultation.getMessages().size() + 1;
                Message message = new Message(
                    messageId,
                    currentDoctor.getId(),
                    selectedConsultation.getPatientId(),
                    messageContent,
                    LocalDateTime.now()
                );
                
                // Add message using service
                if (pharmacyService.addMessageToConsultation(consultationId, message)) {
                    // Update table
                    tableModel.setValueAt(selectedConsultation.getMessages().size() + 1, selectedRow, 4);
                    
                    // Update local object
                selectedConsultation.addMessage(message);
                
                JOptionPane.showMessageDialog(
                    this,
                    "Message sent successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Failed to send message",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    private void updateConsultationStatus() {
        int selectedRow = consultationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a consultation from the list.",
                    "Update Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected consultation ID
        int consultationId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find the consultation in the list
        Consultation selectedConsultation = findConsultationById(consultationId);
        
        if (selectedConsultation != null) {
            String[] statuses = {"Pending", "In Progress", "Completed", "Cancelled"};
            JComboBox<String> statusComboBox = new JComboBox<>(statuses);
            statusComboBox.setSelectedItem(selectedConsultation.getStatus());
            
            int result = JOptionPane.showConfirmDialog(
                this,
                statusComboBox,
                "Update Consultation Status",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String newStatus = (String) statusComboBox.getSelectedItem();
                
                // Update using service instead of direct model change
                if (pharmacyService.updateConsultationStatus(consultationId, newStatus)) {
                // Update table
                tableModel.setValueAt(newStatus, selectedRow, 3);
                    
                    // Update local consultation object
                    selectedConsultation.setStatus(newStatus);
                
                JOptionPane.showMessageDialog(
                    this,
                    "Consultation status updated successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Failed to update consultation status",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    private void createPrescriptionFromConsultation() {
        int selectedRow = consultationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a consultation from the list.",
                    "Create Prescription",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected consultation ID
        int consultationId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find the consultation in the list
        Consultation selectedConsultation = findConsultationById(consultationId);
        
        if (selectedConsultation != null) {
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
                "Create Prescription from Consultation",
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
                
                // Find max prescription ID to generate a new one
                int prescriptionId = 1;
                for (Prescription p : currentDoctor.getIssuedPrescriptions()) {
                    if (p.getId() >= prescriptionId) {
                        prescriptionId = p.getId() + 1;
                    }
                }
                
                // Create prescription from consultation
                Prescription prescription = selectedConsultation.generatePrescription(prescriptionId, instructions);
                
                if (prescription != null) {
                    // Use PharmacyService to save prescription properly
                    if (pharmacyService.savePrescription(prescription)) {
                    JOptionPane.showMessageDialog(
                        this,
                            "Prescription created successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                        // Open the prescriptions panel so the doctor can add medicines
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
        }
    }
} 