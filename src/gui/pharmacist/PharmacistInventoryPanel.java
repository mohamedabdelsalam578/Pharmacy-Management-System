package gui.pharmacist;

import gui.MainFrame;
import gui.admin.MedicineManagementPanel;
import gui.components.StyledButton;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeSizes;
import models.Medicine;
import models.Pharmacist;
import models.Pharmacy;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PharmacistInventoryPanel extends MedicineManagementPanel {
    private final Pharmacist currentPharmacist;
    private final Pharmacy currentPharmacy;
    private JLabel pharmacyInfoLabel;
    private StyledButton updateStockButton;
    private StyledButton lowStockAlertButton;
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JPanel footerPanel;

    public PharmacistInventoryPanel(MainFrame mainFrame) {
        super(mainFrame);
        this.currentPharmacist = (Pharmacist) mainFrame.getCurrentUser();
        this.currentPharmacy = findPharmacyById(currentPharmacist.getPharmacyId());
        initializeComponents();
    }

    private Pharmacy findPharmacyById(int pharmacyId) {
        return mainFrame.getPharmacyService().getPharmacies().stream()
                .filter(p -> p.getId() == pharmacyId)
                .findFirst()
                .orElse(null);
    }

    @Override
    protected void initializeComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(ThemeColors.BACKGROUND);
        
        try {
            System.out.println("PharmacistInventoryPanel: Starting initialization...");
            
            // Create components
            createTable();
            createFooterPanel();
            
            // Add pharmacy-specific information
            addPharmacyInfo();
            
            // Add pharmacist-specific buttons
            addPharmacistButtons();
            
            // Customize table for pharmacist view
            customizeTable();
            
            // Load pharmacy-specific inventory with error handling
            System.out.println("PharmacistInventoryPanel: Loading pharmacy inventory...");
            loadPharmacyInventory();
            System.out.println("PharmacistInventoryPanel: Inventory loaded successfully");
            
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR in PharmacistInventoryPanel: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error initializing inventory panel: " + e.getMessage() + "\n\nCheck console for details.",
                "Initialization Error", 
                JOptionPane.ERROR_MESSAGE);
            
            // Add a label with error information
            JLabel errorLabel = new JLabel("Failed to initialize inventory panel. Error: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setFont(ThemeFonts.BOLD_MEDIUM);
            
            // Clear everything and add the error message
            removeAll();
            setLayout(new BorderLayout());
            add(errorLabel, BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    private void createTable() {
        String[] columns = {"ID", "Name", "Category", "Price", "Stock", "Prescription"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Boolean.class;
                if (columnIndex == 3) return Double.class;
                if (columnIndex == 4) return Integer.class;
                return String.class;
            }
        };
        
        medicineTable = new gui.components.StyledTable<>(tableModel);
        medicineTable.setFont(ThemeFonts.REGULAR_MEDIUM);
        medicineTable.setRowHeight(30);
        medicineTable.setShowGrid(true);
        medicineTable.setGridColor(ThemeColors.BORDER_LIGHT);
        medicineTable.setSelectionBackground(ThemeColors.PRIMARY_LIGHT);
        medicineTable.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        
        JScrollPane scrollPane = new JScrollPane(medicineTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createFooterPanel() {
        footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footerPanel.setBackground(ThemeColors.SURFACE);
        footerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(footerPanel, BorderLayout.SOUTH);
    }

    protected JPanel getFooterPanel() {
        return footerPanel;
    }

    protected JTable getTable() {
        return medicineTable;
    }

    protected DefaultTableModel getTableModel() {
        return tableModel;
    }

    private void addPharmacyInfo() {
        if (currentPharmacy != null) {
            pharmacyInfoLabel = new JLabel(String.format("Pharmacy: %s | Location: %s", 
                currentPharmacy.getName(), currentPharmacy.getAddress()));
            pharmacyInfoLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
            pharmacyInfoLabel.setForeground(ThemeColors.TEXT_PRIMARY);
            
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setBackground(ThemeColors.SURFACE);
            infoPanel.add(pharmacyInfoLabel);
            
            add(infoPanel, BorderLayout.NORTH);
        }
    }

    private void addPharmacistButtons() {
        updateStockButton = new StyledButton("Update Stock", ThemeIcons.EDIT);
        updateStockButton.addActionListener(e -> updateStock());

        lowStockAlertButton = new StyledButton("Low Stock Alerts", ThemeIcons.WARNING);
        lowStockAlertButton.addActionListener(e -> showLowStockAlerts());

        footerPanel.add(updateStockButton);
        footerPanel.add(lowStockAlertButton);
    }

    private void customizeTable() {
        medicineTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                int stock = (int) value;
                if (stock <= 0) {
                    c.setForeground(ThemeColors.DANGER);
                    setToolTipText("Out of Stock!");
                } else if (stock < 10) {
                    c.setForeground(ThemeColors.WARNING);
                    setToolTipText("Low Stock!");
                } else {
                    c.setForeground(ThemeColors.SUCCESS);
                    setToolTipText("Stock Level OK");
                }
                
                return c;
            }
        });
    }

    private void loadPharmacyInventory() {
        System.out.println("Loading pharmacy inventory...");
        
        // Safety check for null pharmacy
        if (currentPharmacy == null) {
            System.err.println("ERROR: Current pharmacy is null!");
            JOptionPane.showMessageDialog(this, 
                "Cannot load inventory: No pharmacy assigned to this pharmacist", 
                "Pharmacy Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            System.out.println("Looking up medicines for pharmacy: " + currentPharmacy.getName());
            List<Medicine> pharmacyMedicines = currentPharmacy.getMedicines();
            
            if (pharmacyMedicines == null) {
                System.err.println("Pharmacy medicines list is null, this is unexpected");
                // The Pharmacy constructor initializes an empty list, so this shouldn't happen
                // Let's create an empty table instead of crashing
                pharmacyMedicines = new ArrayList<>();
            }
            
            System.out.println("Found " + pharmacyMedicines.size() + " medicines in pharmacy inventory");
            updateTableData(pharmacyMedicines);
            
        } catch (Exception e) {
            System.err.println("Error loading pharmacy inventory: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading pharmacy inventory: " + e.getMessage(),
                "Data Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStock() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a medicine to update stock.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Medicine medicine = getMedicineFromSelectedRow(selectedRow);
        if (medicine == null) return;

        String input = JOptionPane.showInputDialog(this,
            "Enter new stock quantity for " + medicine.getName() + ":",
            "Update Stock",
            JOptionPane.PLAIN_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int newStock = Integer.parseInt(input.trim());
                if (newStock < 0) {
                    JOptionPane.showMessageDialog(this,
                        "Stock quantity cannot be negative.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                medicine.setQuantity(newStock);
                mainFrame.getPharmacyService().saveDataToFiles();
                loadPharmacyInventory();
                
                JOptionPane.showMessageDialog(this,
                    "Stock updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showLowStockAlerts() {
        if (currentPharmacy == null) return;

        List<Medicine> lowStockMedicines = currentPharmacy.getMedicines().stream()
            .filter(m -> m.getQuantity() < 10)
            .collect(Collectors.toList());

        if (lowStockMedicines.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No medicines are currently low in stock.",
                "Stock Status",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("The following medicines are low in stock:\n\n");

        for (Medicine medicine : lowStockMedicines) {
            message.append(String.format("• %s - Current stock: %d\n",
                medicine.getName(), medicine.getQuantity()));
        }

        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        textArea.setFont(ThemeFonts.REGULAR_MEDIUM);
        textArea.setBackground(ThemeColors.SURFACE);
        textArea.setForeground(ThemeColors.TEXT_PRIMARY);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this,
            scrollPane,
            "Low Stock Alert",
            JOptionPane.WARNING_MESSAGE);
    }

    private Medicine getMedicineFromSelectedRow(int row) {
        int medicineId = (int) medicineTable.getValueAt(row, 0);
        return mainFrame.getPharmacyService().getMedicines().stream()
            .filter(m -> m.getId() == medicineId)
            .findFirst()
            .orElse(null);
    }

    @Override
    protected void searchMedicines(String query) {
        if (currentPharmacy == null) return;

        List<Medicine> filteredMedicines = currentPharmacy.getMedicines().stream()
            .filter(medicine -> 
                medicine.getName().toLowerCase().contains(query.toLowerCase()) ||
                medicine.getCategory().toLowerCase().contains(query.toLowerCase()) ||
                medicine.getDescription().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());

        updateTableData(filteredMedicines);
    }

    private void updateTableData(List<Medicine> medicines) {
        tableModel.setRowCount(0);
        for (Medicine medicine : medicines) {
            tableModel.addRow(new Object[]{
                medicine.getId(),
                medicine.getName(),
                medicine.getCategory(),
                medicine.getPrice(),
                medicine.getQuantity(),
                medicine.isRequiresPrescription()
            });
        }
    }
} 