package gui.pharmacist;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.StyledTable;
import gui.components.StyledTextField;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeSizes;
import models.Prescription;
import models.Patient;
import models.Doctor;
import models.Medicine;
import models.PrescriptionStatus;
import services.PharmacyService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrescriptionManagementPanel extends BasePanel {
    private StyledTable<Prescription> prescriptionsTable;
    private StyledTextField searchField;
    private JComboBox<String> statusFilterComboBox;
    private List<Prescription> prescriptionList;

    public PrescriptionManagementPanel(MainFrame mainFrame) {
        super(mainFrame);
        initializeComponents();
    }

    @Override
    protected void initializeComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(ThemeColors.BACKGROUND);

        // Create header panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Create main content panel
        add(createContentPanel(), BorderLayout.CENTER);

        // Create footer panel with actions
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Load initial data
        loadPrescriptionData();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Title
        JLabel titleLabel = new JLabel("Prescription Management");
        titleLabel.setFont(ThemeFonts.BOLD_LARGE);
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);

        // Controls panel (search and filter)
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlsPanel.setBackground(ThemeColors.SURFACE);

        // Search field
        searchField = new StyledTextField("Search prescriptions...");
        searchField.setPreferredSize(new Dimension(200, ThemeSizes.TEXT_FIELD_HEIGHT));
        searchField.addActionListener(e -> searchPrescriptions(searchField.getText()));

        // Status filter
        statusFilterComboBox = new JComboBox<>(new String[]{
            "All Prescriptions",
            "Pending",
            "Validated",
            "Filled",
            "Rejected"
        });
        statusFilterComboBox.setPreferredSize(new Dimension(150, ThemeSizes.COMBO_BOX_HEIGHT));
        statusFilterComboBox.addActionListener(e -> filterPrescriptions());

        controlsPanel.add(searchField);
        controlsPanel.add(statusFilterComboBox);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(controlsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Create table
        String[] columns = {
            "ID", "Patient", "Doctor", "Date", "Status", "Medicines"
        };

        prescriptionsTable = new StyledTable<>(columns, prescription -> {
            Object[] row = new Object[6];
            row[0] = prescription.getId();
            
            // Get patient name
            Patient patient = mainFrame.getService().getPatients().stream()
                .filter(p -> p.getId() == prescription.getPatientId())
                .findFirst()
                .orElse(null);
            row[1] = patient != null ? patient.getName() : "Unknown Patient";
            
            // Get doctor name
            Doctor doctor = mainFrame.getService().getDoctors().stream()
                .filter(d -> d.getId() == prescription.getDoctorId())
                .findFirst()
                .orElse(null);
            row[2] = doctor != null ? doctor.getName() : "Unknown Doctor";
            
            row[3] = prescription.getIssueDate().toString();
            row[4] = prescription.getStatus();
            row[5] = prescription.getMedicines().size() + " items";
            
            return row;
        });

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(prescriptionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Create action buttons
        StyledButton viewButton = new StyledButton("View Details", ThemeIcons.VIEW);
        viewButton.addActionListener(e -> viewPrescriptionDetails());

        StyledButton validateButton = new StyledButton("Validate", ThemeIcons.VALIDATE);
        validateButton.addActionListener(e -> validatePrescription());

        StyledButton fillButton = new StyledButton("Fill Prescription", ThemeIcons.MEDICINE);
        fillButton.addActionListener(e -> fillPrescription());

        StyledButton exportButton = new StyledButton("Export", ThemeIcons.EXPORT);
        exportButton.addActionListener(e -> exportPrescriptionReport());

        // Add buttons to panel
        panel.add(viewButton);
        panel.add(validateButton);
        panel.add(fillButton);
        panel.add(exportButton);

        return panel;
    }

    private void loadPrescriptionData() {
        PharmacyService service = mainFrame.getPharmacyService();
        if (service != null) {
            prescriptionList = service.getPrescriptions();
            prescriptionsTable.setData(prescriptionList);
        }
    }

    private void searchPrescriptions(String query) {
        if (query.trim().isEmpty()) {
            prescriptionsTable.setData(prescriptionList);
            return;
        }

        String lowercaseQuery = query.toLowerCase();
        List<Prescription> filteredList = prescriptionList.stream()
            .filter(prescription -> {
                // Get patient and doctor names
                Patient patient = mainFrame.getService().getPatients().stream()
                    .filter(p -> p.getId() == prescription.getPatientId())
                    .findFirst()
                    .orElse(null);
                Doctor doctor = mainFrame.getService().getDoctors().stream()
                    .filter(d -> d.getId() == prescription.getDoctorId())
                    .findFirst()
                    .orElse(null);

                String patientName = patient != null ? patient.getName().toLowerCase() : "";
                String doctorName = doctor != null ? doctor.getName().toLowerCase() : "";

                return String.valueOf(prescription.getId()).contains(query) ||
                       patientName.contains(lowercaseQuery) ||
                       doctorName.contains(lowercaseQuery);
            })
            .collect(Collectors.toList());

        prescriptionsTable.setData(filteredList);
    }

    private void filterPrescriptions() {
        String selectedStatus = (String) statusFilterComboBox.getSelectedItem();
        if ("All Prescriptions".equals(selectedStatus)) {
            prescriptionsTable.setData(prescriptionList);
            return;
        }

        List<Prescription> filteredList = prescriptionList.stream()
            .filter(prescription -> prescription.getStatus().equals(selectedStatus))
            .collect(Collectors.toList());

        prescriptionsTable.setData(filteredList);
    }

    private void viewPrescriptionDetails() {
        Prescription selected = prescriptionsTable.getSelectedItem();
        if (selected != null) {
            mainFrame.navigateTo("PRESCRIPTION_DETAILS", selected);
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a prescription to view.",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void validatePrescription() {
        Prescription selected = prescriptionsTable.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a prescription to validate.",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Check if prescription can be validated
        if (selected.getStatus() != PrescriptionStatus.PENDING) {
            JOptionPane.showMessageDialog(this,
                "Only pending prescriptions can be validated.",
                "Invalid Status",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate medicines availability
        boolean allMedicinesAvailable = true;
        for (Map.Entry<Medicine, Integer> entry : selected.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            Medicine med = mainFrame.getService().getMedicines().stream()
                .filter(m -> m.getId() == medicine.getId())
                .findFirst()
                .orElse(null);
            if (med == null || med.getStock() < quantity) {
                allMedicinesAvailable = false;
                break;
            }
        }

        if (!allMedicinesAvailable) {
            JOptionPane.showMessageDialog(this,
                "Cannot validate prescription. Some medicines are out of stock.",
                "Inventory Issue",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm validation
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to validate this prescription?",
            "Confirm Validation",
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            selected.setStatus(PrescriptionStatus.VALIDATED);
            mainFrame.getService().saveDataToFiles();
            loadPrescriptionData();
            JOptionPane.showMessageDialog(this,
                "Prescription has been validated successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void fillPrescription() {
        Prescription selected = prescriptionsTable.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a prescription to fill.",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Check if prescription can be filled
        if (selected.getStatus() != PrescriptionStatus.VALIDATED) {
            JOptionPane.showMessageDialog(this,
                "Only validated prescriptions can be filled.",
                "Invalid Status",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm filling
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to fill this prescription? This will update the inventory.",
            "Confirm Fill",
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            // Update medicine inventory
            boolean success = true;
            for (Map.Entry<Medicine, Integer> entry : selected.getMedicines().entrySet()) {
                Medicine prescribedMed = entry.getKey();
                int quantity = entry.getValue();
                Medicine med = mainFrame.getService().getMedicines().stream()
                    .filter(m -> m.getId() == prescribedMed.getId())
                    .findFirst()
                    .orElse(null);
                if (med != null && med.getStock() >= quantity) {
                    med.updateStock(-quantity);
                } else {
                    success = false;
                    break;
                }
            }

            if (success) {
                selected.setStatus(PrescriptionStatus.FILLED);
                mainFrame.getService().saveDataToFiles();
                loadPrescriptionData();
                JOptionPane.showMessageDialog(this,
                    "Prescription has been filled successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error filling prescription. Please check inventory.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportPrescriptionReport() {
        StringBuilder content = new StringBuilder();
        content.append("PRESCRIPTION REPORT\n");
        content.append("==================\n\n");
        content.append("Total Prescriptions: ").append(prescriptionList.size()).append("\n\n");

        // Add status counts
        long pendingCount = prescriptionList.stream()
            .filter(p -> p.getStatus().equals(PrescriptionStatus.PENDING.toString()))
            .count();
        long validatedCount = prescriptionList.stream()
            .filter(p -> p.getStatus().equals(PrescriptionStatus.VALIDATED.toString()))
            .count();
        long filledCount = prescriptionList.stream()
            .filter(p -> p.getStatus().equals(PrescriptionStatus.FILLED.toString()))
            .count();

        content.append("Status Breakdown:\n");
        content.append("- Pending: ").append(pendingCount).append("\n");
        content.append("- Validated: ").append(validatedCount).append("\n");
        content.append("- Filled: ").append(filledCount).append("\n\n");

        content.append("Detailed Prescriptions:\n");
        content.append("=====================\n\n");

        for (Prescription prescription : prescriptionList) {
            content.append("Prescription #").append(prescription.getId()).append("\n");
            
            // Get patient name
            Patient patient = mainFrame.getService().getPatients().stream()
                .filter(p -> p.getId() == prescription.getPatientId())
                .findFirst()
                .orElse(null);
            content.append("Patient: ").append(patient != null ? patient.getName() : "Unknown").append("\n");
            
            // Get doctor name
            Doctor doctor = mainFrame.getService().getDoctors().stream()
                .filter(d -> d.getId() == prescription.getDoctorId())
                .findFirst()
                .orElse(null);
            content.append("Doctor: ").append(doctor != null ? doctor.getName() : "Unknown").append("\n");
            
            content.append("Date: ").append(prescription.getIssueDate()).append("\n");
            content.append("Status: ").append(prescription.getStatus()).append("\n");
            content.append("Medicines: ").append(prescription.getMedicines().size()).append(" items\n");
            content.append("------------------------\n");
        }

        utils.PDFGenerator.generatePDF("Prescription Report", content.toString(), this);
    }
} 