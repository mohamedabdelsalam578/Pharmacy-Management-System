package gui.pharmacist;

import gui.MainFrame;
import gui.components.StyledButton;
import gui.components.StyledTable;
import gui.dashboard.PharmacistDashboardPanel;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeSizes;
import models.Medicine;
import models.Prescription;
import models.PrescriptionStatus;
import models.Patient;
import models.Doctor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class PharmacistPrescriptionPanel extends JPanel {
    private final MainFrame mainFrame;
    private StyledTable<Prescription> prescriptionTable;
    private JComboBox<String> statusFilter;
    private JTextField searchField;
    private JLabel countLabel;

    public PharmacistPrescriptionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeComponents();
        loadPrescriptions();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ThemeColors.BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Create top panel with filters
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Create prescription table
        createPrescriptionTable();
        JScrollPane scrollPane = new JScrollPane(prescriptionTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create bottom panel with actions
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(ThemeSizes.PADDING_MEDIUM, 0));
        panel.setBackground(ThemeColors.BACKGROUND);

        // Left side - Status filter
        statusFilter = new JComboBox<>(new String[]{"All", "Pending", "In Progress", "Completed", "Cancelled"});
        statusFilter.setFont(ThemeFonts.REGULAR_MEDIUM);
        statusFilter.setBackground(ThemeColors.SURFACE);
        statusFilter.setForeground(ThemeColors.TEXT_PRIMARY);
        statusFilter.addActionListener(e -> filterPrescriptions());

        // Center - Search field
        searchField = new JTextField(20);
        searchField.setFont(ThemeFonts.REGULAR_MEDIUM);
        searchField.setBackground(ThemeColors.SURFACE);
        searchField.setForeground(ThemeColors.TEXT_PRIMARY);
        searchField.putClientProperty("JTextField.placeholderText", "Search prescriptions...");
        searchField.addActionListener(e -> filterPrescriptions());

        // Right side - Count label
        countLabel = new JLabel("0 prescriptions");
        countLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
        countLabel.setForeground(ThemeColors.TEXT_PRIMARY);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, ThemeSizes.PADDING_MEDIUM, 0));
        filterPanel.setBackground(ThemeColors.BACKGROUND);
        
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
        statusLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createHorizontalStrut(ThemeSizes.PADDING_LARGE));
        filterPanel.add(searchField);

        panel.add(filterPanel, BorderLayout.WEST);
        panel.add(countLabel, BorderLayout.EAST);

        return panel;
    }

    private void createPrescriptionTable() {
        String[] columns = {"ID", "Patient", "Doctor", "Date", "Status", "Medicines", "Actions"};
        prescriptionTable = new StyledTable<>(columns, prescription -> {
            Patient patient = findPatient(prescription.getPatientId());
            Doctor doctor = findDoctor(prescription.getDoctorId());
            String medicineList = formatMedicineList(prescription.getMedicines());

            return new Object[]{
                prescription.getId(),
                patient != null ? patient.getName() : "Unknown",
                doctor != null ? doctor.getName() : "Unknown",
                prescription.getIssueDate().toString(),
                prescription.getStatus(),
                medicineList,
                new StyledButton("View Details", ThemeIcons.VIEW)
            };
        });

        // Set custom renderer for the Actions column
        prescriptionTable.getColumnModel().getColumn(6).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            if (value instanceof StyledButton) {
                StyledButton button = (StyledButton) value;
                button.setBackground(isSelected ? ThemeColors.HOVER : ThemeColors.SURFACE);
                return button;
            }
            return new JLabel(value.toString());
        });

        // Add mouse listener for button clicks
        prescriptionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = prescriptionTable.rowAtPoint(e.getPoint());
                int col = prescriptionTable.columnAtPoint(e.getPoint());
                
                if (col == 6 && row >= 0) {
                    Prescription prescription = prescriptionTable.getData().get(row);
                    showPrescriptionDetailsDialog(prescription);
                }
            }
        });
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeSizes.PADDING_MEDIUM, ThemeSizes.PADDING_MEDIUM));
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(ThemeSizes.PADDING_SMALL, 0, 0, 0));

        StyledButton exportButton = new StyledButton("Export Report", ThemeIcons.EXPORT);
        exportButton.addActionListener(e -> ((PharmacistDashboardPanel)SwingUtilities.getAncestorOfClass(
            PharmacistDashboardPanel.class, this)).exportPrescriptionReport());

        StyledButton processButton = new StyledButton("Process Selected", ThemeIcons.PROCESS);
        processButton.addActionListener(e -> processPrescription());

        StyledButton refreshButton = new StyledButton("Refresh", ThemeIcons.REFRESH);
        refreshButton.addActionListener(e -> loadPrescriptions());

        panel.add(exportButton);
        panel.add(Box.createHorizontalStrut(ThemeSizes.PADDING_SMALL));
        panel.add(refreshButton);
        panel.add(Box.createHorizontalStrut(ThemeSizes.PADDING_SMALL));
        panel.add(processButton);

        return panel;
    }

    private void loadPrescriptions() {
        List<Prescription> prescriptions = mainFrame.getService().getPrescriptions();
        prescriptionTable.setData(prescriptions);
        updatePrescriptionCount();
    }

    private void filterPrescriptions() {
        String status = (String) statusFilter.getSelectedItem();
        String search = searchField.getText().toLowerCase();

        List<Prescription> allPrescriptions = mainFrame.getService().getPrescriptions();
        List<Prescription> filtered = allPrescriptions.stream()
            .filter(p -> status.equals("All") || p.getStatus().equals(status))
            .filter(p -> {
                Patient patient = findPatient(p.getPatientId());
                return search.isEmpty() || 
                       (patient != null && patient.getName().toLowerCase().contains(search)) ||
                       String.valueOf(p.getId()).contains(search);
            })
            .toList();

        prescriptionTable.setData(filtered);
        updatePrescriptionCount();
    }

    private void updatePrescriptionCount() {
        int count = prescriptionTable.getRowCount();
        countLabel.setText(count + " prescription" + (count != 1 ? "s" : ""));
    }

    private void processPrescription() {
        Prescription selected = prescriptionTable.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a prescription to process.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!selected.getStatus().equals("Pending")) {
            JOptionPane.showMessageDialog(this,
                "Only pending prescriptions can be processed.",
                "Invalid Status",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check medicine availability
        StringBuilder unavailableMedicines = new StringBuilder();
        boolean canProcess = true;

        for (Map.Entry<Medicine, Integer> entry : selected.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int requiredQuantity = entry.getValue();

            if (medicine.getStock() < requiredQuantity) {
                canProcess = false;
                unavailableMedicines.append(String.format("• %s (Need: %d, Available: %d)\n",
                    medicine.getName(), requiredQuantity, medicine.getStock()));
            }
        }

        if (!canProcess) {
            JOptionPane.showMessageDialog(this,
                "Cannot process prescription due to insufficient stock:\n" + unavailableMedicines.toString(),
                "Insufficient Stock",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Process the prescription
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to process this prescription?\nThis will update the medicine stock.",
            "Confirm Processing",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Update medicine stock
            for (Map.Entry<Medicine, Integer> entry : selected.getMedicines().entrySet()) {
                Medicine medicine = entry.getKey();
                int quantity = entry.getValue();
                medicine.setQuantity(medicine.getStock() - quantity);
            }

            // Update prescription status
            selected.setStatus(PrescriptionStatus.COMPLETED);

            // Save changes
            utils.FileHandler.saveMedicines(mainFrame.getService().getMedicines());
            utils.FileHandler.savePrescriptions(mainFrame.getService().getPrescriptions());

            // Refresh display
            loadPrescriptions();

            JOptionPane.showMessageDialog(this,
                "Prescription processed successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showPrescriptionDetailsDialog(Prescription prescription) {
        // Create a styled dialog using our theme
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setTitle("Prescription Details");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout(ThemeSizes.PADDING_MEDIUM, ThemeSizes.PADDING_MEDIUM));
        dialog.setBackground(ThemeColors.BACKGROUND);

        // Create content panel with our styling
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ThemeColors.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            ThemeSizes.PADDING_LARGE, ThemeSizes.PADDING_LARGE, 
            ThemeSizes.PADDING_LARGE, ThemeSizes.PADDING_LARGE));

        // Add prescription details with our fonts
        Patient patient = findPatient(prescription.getPatientId());
        Doctor doctor = findDoctor(prescription.getDoctorId());

        addDetailRow(contentPanel, "Prescription ID:", String.valueOf(prescription.getId()));
        addDetailRow(contentPanel, "Patient:", patient != null ? patient.getName() : "Unknown");
        addDetailRow(contentPanel, "Doctor:", doctor != null ? doctor.getName() : "Unknown");
        addDetailRow(contentPanel, "Issue Date:", prescription.getIssueDate().toString());
        addDetailRow(contentPanel, "Status:", prescription.getStatus().toString());

        // Create medicines table with our styling
        String[] columns = {"Medicine", "Quantity", "Available Stock"};
        StyledTable<Map.Entry<Medicine, Integer>> medicinesTable = new StyledTable<>(columns, entry -> new Object[]{
            entry.getKey().getName(),
            entry.getValue(),
            entry.getKey().getStock()
        });
        medicinesTable.setData(new ArrayList<>(prescription.getMedicines().entrySet()));

        JScrollPane scrollPane = new JScrollPane(medicinesTable);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        contentPanel.add(Box.createVerticalStrut(ThemeSizes.PADDING_MEDIUM));
        
        JLabel medicinesLabel = new JLabel("Prescribed Medicines:");
        medicinesLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        contentPanel.add(medicinesLabel);
        contentPanel.add(Box.createVerticalStrut(ThemeSizes.PADDING_SMALL));
        contentPanel.add(scrollPane);

        // Add styled buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeColors.BACKGROUND);
        
        StyledButton closeButton = new StyledButton("Close", ThemeIcons.CLOSE);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBackground(ThemeColors.BACKGROUND);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(ThemeFonts.BOLD_MEDIUM);
        labelComponent.setPreferredSize(new Dimension(120, ThemeSizes.BUTTON_HEIGHT));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(ThemeFonts.REGULAR_MEDIUM);
        
        row.add(labelComponent);
        row.add(valueComponent);
        panel.add(row);
    }

    private Patient findPatient(int patientId) {
        return mainFrame.getService().getPatients().stream()
            .filter(p -> p.getId() == patientId)
            .findFirst()
            .orElse(null);
    }

    private Doctor findDoctor(int doctorId) {
        return mainFrame.getService().getDoctors().stream()
            .filter(d -> d.getId() == doctorId)
            .findFirst()
            .orElse(null);
    }

    private String formatMedicineList(Map<Medicine, Integer> medicines) {
        return medicines.entrySet().stream()
            .map(e -> String.format("%s (%d)", e.getKey().getName(), e.getValue()))
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }
} 