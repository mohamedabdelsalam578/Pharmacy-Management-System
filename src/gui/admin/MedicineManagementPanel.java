package gui.admin;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.RoundedBorder;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeSizes;
import models.Medicine;
import services.PharmacyService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing medicines in the pharmacy system
 */
public class MedicineManagementPanel extends BasePanel {
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private StyledButton addButton;
    private StyledButton editButton;
    private StyledButton deleteButton;
    private StyledButton backButton;
    
    public MedicineManagementPanel(MainFrame mainFrame) {
        super(mainFrame);
        setBackground(ThemeColors.BACKGROUND);
    }
    
    @Override
    protected void initializeComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new CompoundBorder(
            new RoundedBorder(ThemeSizes.CARD_BORDER_RADIUS_LARGE, ThemeColors.BORDER_LIGHT),
            new EmptyBorder(ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING)
        ));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Create footer panel with actions
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        
        // Load medicines data
        loadMedicinesData();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new RoundedBorder(ThemeSizes.CARD_BORDER_RADIUS_LARGE, ThemeColors.BORDER_LIGHT),
            new EmptyBorder(ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Medicine Management");
        titleLabel.setFont(ThemeFonts.BOLD_TITLE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setIcon(ThemeIcons.MEDICINE);
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(ThemeColors.SURFACE);
        
        searchField = new JTextField(20);
        searchField.setFont(ThemeFonts.REGULAR_MEDIUM);
        searchField.setBorder(new CompoundBorder(
            new RoundedBorder(ThemeSizes.BORDER_RADIUS_SMALL, ThemeColors.BORDER_LIGHT),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        StyledButton searchButton = new StyledButton("Search", ThemeIcons.SEARCH);
        searchButton.addActionListener(e -> searchMedicines(searchField.getText()));
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Back button
        backButton = new StyledButton("Back to Dashboard", ThemeIcons.BACK);
        backButton.addActionListener(e -> mainFrame.navigateTo("DASHBOARD"));
        
        // Add components to header
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new RoundedBorder(ThemeSizes.CARD_BORDER_RADIUS_LARGE, ThemeColors.BORDER_LIGHT),
            new EmptyBorder(ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING)
        ));
        
        // Create table model
        String[] columns = {"ID", "Name", "Category", "Price", "Stock", "Prescription"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Boolean.class; // Prescription column
                if (columnIndex == 3) return Double.class;  // Price column
                if (columnIndex == 4) return Integer.class; // Stock column
                return String.class;
            }
        };
        
        // Create table
        medicineTable = new JTable(tableModel);
        medicineTable.setFont(ThemeFonts.REGULAR_MEDIUM);
        medicineTable.setRowHeight(30);
        medicineTable.setShowGrid(true);
        medicineTable.setGridColor(ThemeColors.BORDER_LIGHT);
        medicineTable.setSelectionBackground(ThemeColors.PRIMARY_LIGHT);
        medicineTable.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        medicineTable.getTableHeader().setFont(ThemeFonts.BOLD_MEDIUM);
        medicineTable.getTableHeader().setBackground(ThemeColors.SURFACE_VARIANT);
        medicineTable.getTableHeader().setForeground(ThemeColors.TEXT_PRIMARY);
        
        // Create cell renderer for stock column to highlight low stock
        medicineTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                int stock = (int) value;
                if (stock <= 0) {
                    c.setForeground(ThemeColors.DANGER);
                } else if (stock < 10) {
                    c.setForeground(ThemeColors.WARNING);
                } else {
                    c.setForeground(ThemeColors.SUCCESS);
                }
                
                return c;
            }
        });
        
        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(medicineTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new RoundedBorder(ThemeSizes.CARD_BORDER_RADIUS_LARGE, ThemeColors.BORDER_LIGHT),
            new EmptyBorder(ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING)
        ));
        
        // Add medicine button
        addButton = new StyledButton("Add Medicine", ThemeIcons.ADD);
        addButton.addActionListener(e -> addMedicine());
        
        // Edit medicine button
        editButton = new StyledButton("Edit Medicine", ThemeIcons.EDIT);
        editButton.addActionListener(e -> editMedicine());
        
        // Delete medicine button
        deleteButton = new StyledButton("Delete Medicine", ThemeIcons.DELETE);
        deleteButton.addActionListener(e -> deleteMedicine());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        
        return panel;
    }
    
    private void loadMedicinesData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get medicines from pharmacy service
        PharmacyService service = mainFrame.getPharmacyService();
        if (service != null) {
            List<Medicine> medicines = service.getMedicines();
            for (Medicine medicine : medicines) {
                Object[] row = {
                    medicine.getId(),
                    medicine.getName(),
                    medicine.getCategory(),
                    medicine.getPrice(),
                    medicine.getStock(),
                    medicine.isPrescription()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void searchMedicines(String query) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // If search query is empty, load all medicines
        if (query.trim().isEmpty()) {
            loadMedicinesData();
            return;
        }
        
        // Search by name, category or description
        PharmacyService service = mainFrame.getPharmacyService();
        if (service != null) {
            List<Medicine> medicines = service.getMedicines();
            String lowercaseQuery = query.toLowerCase();
            
            for (Medicine medicine : medicines) {
                if (medicine.getName().toLowerCase().contains(lowercaseQuery) || 
                    medicine.getCategory().toLowerCase().contains(lowercaseQuery) ||
                    medicine.getDescription().toLowerCase().contains(lowercaseQuery)) {
                    
                    Object[] row = {
                        medicine.getId(),
                        medicine.getName(),
                        medicine.getCategory(),
                        medicine.getPrice(),
                        medicine.getStock(),
                        medicine.isPrescription()
                    };
                    tableModel.addRow(row);
                }
            }
        }
    }
    
    private void addMedicine() {
        // Placeholder for medicine addition functionality
        JOptionPane.showMessageDialog(
            this,
            "Add Medicine functionality will be implemented here",
            "Add Medicine",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void editMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a medicine to edit",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Placeholder for medicine editing functionality
        int medicineId = (int) tableModel.getValueAt(selectedRow, 0);
        String medicineName = (String) tableModel.getValueAt(selectedRow, 1);
        
        JOptionPane.showMessageDialog(
            this,
            "Edit Medicine functionality for " + medicineName + " will be implemented here",
            "Edit Medicine",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void deleteMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a medicine to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Placeholder for medicine deletion functionality
        int medicineId = (int) tableModel.getValueAt(selectedRow, 0);
        String medicineName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete " + medicineName + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                this,
                "Delete Medicine functionality for " + medicineName + " will be implemented here",
                "Delete Medicine",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
} 