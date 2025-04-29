package gui.dashboard;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import models.Order;
import models.OrderItem;
import models.Medicine;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class OrderDetailsPanel extends BasePanel {
    private final Order order;
    private JPanel mainContent;

    public OrderDetailsPanel(MainFrame mainFrame, Order order) {
        super(mainFrame);
        this.order = order;
        initializeComponents();
    }

    @Override
    protected void initializeComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content
        mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBackground(ThemeColors.SURFACE);
        mainContent.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Order details
        JPanel detailsPanel = createDetailsPanel();
        mainContent.add(detailsPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonsPanel = createButtonsPanel();
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Order Details");
        titleLabel.setFont(ThemeFonts.BOLD_XXLARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setIcon(ThemeIcons.ORDER);
        panel.add(titleLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeColors.SURFACE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Basic information
        addDetailRow(panel, gbc, 0, "Order ID:", String.valueOf(order.getId()));
        addDetailRow(panel, gbc, 1, "Order Date:", order.getOrderDate().toString());
        addDetailRow(panel, gbc, 2, "Status:", order.getStatus());
        addDetailRow(panel, gbc, 3, "Total Amount:", String.format("L.E %.2f", order.calculateTotal()));

        // Order items table
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        JLabel itemsLabel = new JLabel("Order Items:");
        itemsLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        panel.add(itemsLabel, gbc);

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(createItemsTable(), gbc);

        return panel;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(ThemeFonts.BOLD_MEDIUM);
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        panel.add(new JLabel(value), gbc);
    }

    private JScrollPane createItemsTable() {
        String[] columns = {"Medicine", "Quantity", "Unit Price", "Total"};
        List<OrderItem> items = order.getItems();
        Object[][] data = new Object[items.size()][4];
        
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            Medicine medicine = mainFrame.getService().getMedicines().stream()
                .filter(m -> m.getId() == item.getMedicineId())
                .findFirst()
                .orElse(null);
            
            data[i][0] = medicine != null ? medicine.getName() : "Unknown Medicine";
            data[i][1] = item.getQuantity();
            data[i][2] = String.format("L.E %.2f", item.getUnitPrice());
            data[i][3] = String.format("L.E %.2f", item.getQuantity() * item.getUnitPrice());
        }

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setBackground(ThemeColors.BACKGROUND);
        table.getTableHeader().setBackground(ThemeColors.SURFACE);
        table.getTableHeader().setFont(ThemeFonts.BOLD_SMALL);
        table.setFont(ThemeFonts.REGULAR_SMALL);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Right-align numeric columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        return scrollPane;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Reorder button (only for completed orders)
        if (order.getStatus().equals("COMPLETED")) {
            StyledButton reorderButton = new StyledButton("Reorder", ThemeIcons.REFRESH);
            reorderButton.addActionListener(e -> reorderItems());
            panel.add(reorderButton);
        }

        // Back button
        StyledButton backButton = new StyledButton("Back", ThemeIcons.BACK);
        backButton.addActionListener(e -> mainFrame.navigateTo("DASHBOARD"));
        panel.add(backButton);

        return panel;
    }

    private void reorderItems() {
        List<OrderItem> items = order.getItems();
        StringBuilder message = new StringBuilder();
        message.append("Added to cart:\n");

        for (OrderItem item : items) {
            Medicine medicine = mainFrame.getService().getMedicines().stream()
                .filter(m -> m.getId() == item.getMedicineId())
                .findFirst()
                .orElse(null);
            
            if (medicine != null) {
                mainFrame.getCurrentUser().getCartOrder().addItem(medicine, item.getQuantity());
                message.append("- ").append(medicine.getName())
                       .append(" (Qty: ").append(item.getQuantity()).append(")\n");
            }
        }

        JOptionPane.showMessageDialog(this,
            message.toString(),
            "Added to Cart",
            JOptionPane.INFORMATION_MESSAGE);
    }
} 