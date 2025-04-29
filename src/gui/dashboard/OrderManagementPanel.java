package gui.dashboard;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import models.Order;
import models.OrderItem;
import models.Patient;
import services.PharmacyService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel for managing orders in the pharmacy system
 */
public class OrderManagementPanel extends BasePanel {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterComboBox;
    private StyledButton viewButton;
    private StyledButton processButton;
    private StyledButton deleteButton;
    private StyledButton backButton;
    
    public OrderManagementPanel(MainFrame mainFrame) {
        super(mainFrame);
        setBackground(ThemeColors.BACKGROUND);
    }
    
    @Override
    protected void initializeComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Create footer panel with actions
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        
        // Load orders data
        loadOrdersData();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Order Management");
        titleLabel.setFont(ThemeFonts.BOLD_XXLARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setIcon(ThemeIcons.ORDER);
        
        // Search and filter panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        controlsPanel.setBackground(ThemeColors.SURFACE);
        
        // Status filter
        JLabel statusLabel = new JLabel("Filter by status:");
        statusLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
        
        statusFilterComboBox = new JComboBox<>(new String[] {
            "All Orders", "Pending", "Processing", "Completed", "Cancelled"
        });
        statusFilterComboBox.setFont(ThemeFonts.REGULAR_MEDIUM);
        statusFilterComboBox.addActionListener(e -> filterOrders());
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(ThemeFonts.REGULAR_MEDIUM);
        searchField.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        StyledButton searchButton = new StyledButton("Search", ThemeIcons.SEARCH);
        searchButton.addActionListener(e -> searchOrders(searchField.getText()));
        
        controlsPanel.add(statusLabel);
        controlsPanel.add(statusFilterComboBox);
        controlsPanel.add(searchField);
        controlsPanel.add(searchButton);
        
        // Back button
        backButton = new StyledButton("Back to Dashboard", ThemeIcons.BACK);
        backButton.addActionListener(e -> mainFrame.navigateTo("DASHBOARD"));
        
        // Add components to header
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(controlsPanel, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Create table model
        String[] columns = {"ID", "Patient", "Date", "Total Amount", "Status", "Items"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Double.class;  // Total Amount column
                if (columnIndex == 5) return Integer.class;  // Items column
                return String.class;
            }
        };
        
        // Create table
        orderTable = new JTable(tableModel);
        orderTable.setFont(ThemeFonts.REGULAR_MEDIUM);
        orderTable.setRowHeight(30);
        orderTable.setShowGrid(true);
        orderTable.setGridColor(ThemeColors.BORDER_LIGHT);
        orderTable.setSelectionBackground(ThemeColors.PRIMARY_LIGHT);
        orderTable.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        orderTable.getTableHeader().setFont(ThemeFonts.BOLD_MEDIUM);
        orderTable.getTableHeader().setBackground(ThemeColors.SURFACE_VARIANT);
        orderTable.getTableHeader().setForeground(ThemeColors.TEXT_PRIMARY);
        
        // Create cell renderer for status column to color code statuses
        orderTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = (String) value;
                if ("Completed".equals(status)) {
                    c.setForeground(ThemeColors.SUCCESS);
                } else if ("Pending".equals(status)) {
                    c.setForeground(ThemeColors.WARNING);
                } else if ("Processing".equals(status)) {
                    c.setForeground(ThemeColors.PRIMARY);
                } else if ("Cancelled".equals(status)) {
                    c.setForeground(ThemeColors.DANGER);
                } else {
                    c.setForeground(ThemeColors.TEXT_PRIMARY);
                }
                
                return c;
            }
        });
        
        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // View order details button
        viewButton = new StyledButton("View Order Details", ThemeIcons.INFO);
        viewButton.addActionListener(e -> viewOrderDetails());
        
        // Process order button
        processButton = new StyledButton("Process Order", ThemeIcons.REFRESH);
        processButton.addActionListener(e -> processOrder());
        
        // Delete order button
        deleteButton = new StyledButton("Cancel Order", ThemeIcons.DELETE);
        deleteButton.addActionListener(e -> cancelOrder());
        
        panel.add(viewButton);
        panel.add(processButton);
        panel.add(deleteButton);
        
        return panel;
    }
    
    private void loadOrdersData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get orders from pharmacy service
        PharmacyService service = mainFrame.getPharmacyService();
        if (service != null) {
            List<Order> orders = service.getOrders();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            for (Order order : orders) {
                // Apply status filter if needed
                String selectedStatus = (String) statusFilterComboBox.getSelectedItem();
                if (!"All Orders".equals(selectedStatus) && !order.getStatus().equals(selectedStatus)) {
                    continue;
                }
                
                // Get patient name
                Patient patient = null;
                String patientName = "Unknown";
                
                // Look for patient in the list of patients
                for (Patient p : service.getPatients()) {
                    if (p.getId() == order.getPatientId()) {
                        patient = p;
                        patientName = p.getName();
                        break;
                    }
                }
                
                // Calculate total
                double total = order.getTotalAmount();
                
                Object[] row = {
                    order.getId(),
                    patientName,
                    dateFormat.format(order.getOrderDate()),
                    total,
                    order.getStatus(),
                    order.getItems().size()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void filterOrders() {
        loadOrdersData();  // This will apply the selected filter
    }
    
    private void searchOrders(String query) {
        // If search query is empty, just load all orders with filter
        if (query.trim().isEmpty()) {
            loadOrdersData();
            return;
        }
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get orders from pharmacy service
        PharmacyService service = mainFrame.getPharmacyService();
        if (service != null) {
            List<Order> orders = service.getOrders();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String lowercaseQuery = query.toLowerCase();
            
            for (Order order : orders) {
                // Apply status filter if needed
                String selectedStatus = (String) statusFilterComboBox.getSelectedItem();
                if (!"All Orders".equals(selectedStatus) && !order.getStatus().equals(selectedStatus)) {
                    continue;
                }
                
                // Get patient
                Patient patient = null;
                String patientName = "Unknown";
                
                // Look for patient in the list of patients
                for (Patient p : service.getPatients()) {
                    if (p.getId() == order.getPatientId()) {
                        patient = p;
                        patientName = p.getName();
                        break;
                    }
                }
                
                // Search by ID or patient name
                if (String.valueOf(order.getId()).contains(query) || 
                    patientName.toLowerCase().contains(lowercaseQuery)) {
                    
                    // Calculate total
                    double total = order.getTotalAmount();
                    
                    Object[] row = {
                        order.getId(),
                        patientName,
                        dateFormat.format(order.getOrderDate()),
                        total,
                        order.getStatus(),
                        order.getItems().size()
                    };
                    tableModel.addRow(row);
                }
            }
        }
    }
    
    private void viewOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            int orderId = (int) tableModel.getValueAt(selectedRow, 0);
            PharmacyService service = mainFrame.getPharmacyService();
            Order order = null;
            
            // Find the order in the list of orders
            for (Order o : service.getOrders()) {
                if (o.getId() == orderId) {
                    order = o;
                    break;
                }
            }
            
            if (order != null) {
                mainFrame.navigateTo("ORDER_DETAILS", order);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Unable to retrieve order details.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an order to view details.", 
                "Selection Required", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void processOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            int orderId = (int) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 4);
            
            if ("Pending".equals(status)) {
                PharmacyService service = mainFrame.getPharmacyService();
                Order order = null;
                
                // Find the order in the list of orders
                for (Order o : service.getOrders()) {
                    if (o.getId() == orderId) {
                        order = o;
                        break;
                    }
                }
                
                if (order != null) {
                    // Update order status to processing
                    order.setStatus("Processing");
                    
                    // Refresh the table
                    loadOrdersData();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Order status updated to Processing.", 
                        "Status Updated", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Only Pending orders can be processed.", 
                    "Action Not Allowed", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an order to process.", 
                "Selection Required", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void cancelOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            int orderId = (int) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 4);
            
            if ("Completed".equals(status)) {
                JOptionPane.showMessageDialog(this, 
                    "Completed orders cannot be cancelled.", 
                    "Action Not Allowed", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this order?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                PharmacyService service = mainFrame.getPharmacyService();
                Order order = null;
                
                // Find the order in the list of orders
                for (Order o : service.getOrders()) {
                    if (o.getId() == orderId) {
                        order = o;
                        break;
                    }
                }
                
                if (order != null) {
                    // Update order status to cancelled
                    order.setStatus("Cancelled");
                    
                    // Refresh the table
                    loadOrdersData();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Order has been cancelled.", 
                        "Order Cancelled", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an order to cancel.", 
                "Selection Required", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 