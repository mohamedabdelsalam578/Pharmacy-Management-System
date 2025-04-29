package gui.dashboard;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.StyledTable;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeManager;
import models.Medicine;
import models.Order;
import models.OrderItem;
import models.Patient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Shopping cart panel showing items the patient intends to purchase
 */
public class ShoppingCartPanel extends BasePanel {

    private StyledTable<OrderItem> cartTable;
    private JLabel totalLabel;
    private Order cartOrder;

    public ShoppingCartPanel(MainFrame mainFrame) {
        super(mainFrame);
        // cartOrder will be set in initializeComponents based on current patient
    }

    @Override
    protected void initializeComponents() {
        // Ensure cart exists (defensive)
        Patient patientRef = (Patient) mainFrame.getCurrentUser();
        if (cartOrder == null && patientRef != null) {
            cartOrder = patientRef.getCartOrder();
        }
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeColors.BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Card container for table & actions
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        ThemeManager.applyCardStyling(cardPanel);

        // Table
        String[] cols = {"Medicine", "Unit Price", "Quantity", "Subtotal"};
        cartTable = new StyledTable<>(cols, this::mapOrderItem);
       
        try {
            cartTable.setData(cartOrder.getItems());
        } catch (Exception e) {
            // If we get a NullPointerException, create an empty table
            cartTable.setData(new ArrayList<>());
            System.err.println("Error loading cart items: " + e.getMessage());
        }
        cardPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Bottom actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);

        // Safe total calculation
        String totalText = "Total: L.E 0.00";
        try {
            if (cartOrder != null) {
                totalText = "Total: L.E " + String.format("%.2f", cartOrder.calculateTotal());
            }
        } catch (Exception e) {
            System.err.println("Error calculating total: " + e.getMessage());
        }
        totalLabel = new JLabel(totalText);
        totalLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        totalLabel.setForeground(ThemeColors.PRIMARY);

        StyledButton removeBtn = new StyledButton("Remove Item", ThemeIcons.DELETE);
        removeBtn.addActionListener(e -> removeSelected());

        StyledButton checkoutBtn = new StyledButton("Checkout", ThemeIcons.ORDER);
        checkoutBtn.addActionListener(e -> checkout());

        bottomPanel.add(totalLabel);
        bottomPanel.add(removeBtn);
        bottomPanel.add(checkoutBtn);

        cardPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(cardPanel, BorderLayout.CENTER);
    }

    private Object[] mapOrderItem(OrderItem item) {
        Medicine med = mainFrame.getService().findMedicineById(item.getMedicineId());
        String name = med != null ? med.getName() : "" + item.getMedicineId();
        double price = item.getUnitPrice();
        int qty = item.getQuantity();
        double subtotal = price * qty;
        return new Object[]{name, String.format("%.2f", price), qty, String.format("%.2f", subtotal)};
    }

    private void removeSelected() {
        OrderItem item = cartTable.getSelectedItem();
        if (item == null) return;
        cartOrder.removeItem(item.getMedicineId());
        cartTable.setData(cartOrder.getItems());
        refreshTotal();
    }

    private void refreshTotal() {
        try {
            if (cartOrder != null) {
                totalLabel.setText("Total: L.E " + String.format("%.2f", cartOrder.calculateTotal()));
            } else {
                totalLabel.setText("Total: L.E 0.00");
            }
        } catch (Exception e) {
            totalLabel.setText("Total: L.E 0.00");
            System.err.println("Error refreshing total: " + e.getMessage());
        }
    }

    private void checkout() {
        if (cartOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        Patient patient = (Patient) mainFrame.getCurrentUser();
        int confirm = JOptionPane.showConfirmDialog(this, "Confirm checkout using wallet?", "Checkout", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // First, finalize the cart and receive the completed order instance
        Order completedOrder = patient.checkoutCart();
        if (completedOrder == null) {
            JOptionPane.showMessageDialog(this, "Checkout failed. Your cart may be empty or invalid.", "Checkout Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Attempt payment on the completed order
        boolean paid = completedOrder.processPaymentFromWallet(patient);
        if (!paid) {
            JOptionPane.showMessageDialog(this, "Payment failed. Please ensure sufficient balance.", "Payment Failed", JOptionPane.ERROR_MESSAGE);
            // If payment failed, put the order back into the cart for retry
            patient.setCartOrder(completedOrder);
            this.cartOrder = completedOrder;
            refreshTotal();
            return;
        }

        // Persist globally
        var service = mainFrame.getService();
        if (service != null) {
            if (!service.getOrders().contains(completedOrder)) {
                service.getOrders().add(completedOrder);
            }
            service.saveDataToFiles();
        }

        JOptionPane.showMessageDialog(this, "Order placed successfully!");

        // Refresh UI with a new empty cart
        this.cartOrder = patient.getCartOrder();
        cartTable.setData(this.cartOrder.getItems());
        refreshTotal();

        // Notify dashboard to refresh (re-navigate to dashboard panel)
        if (mainFrame != null) {
            mainFrame.navigateTo("DASHBOARD");
        }
    }

    public Order getCartOrder() {
        return cartOrder;
    }

    // Public method to refresh table & total when cart changes
    public void refreshData() {
        cartTable.setData(cartOrder.getItems());
        refreshTotal();
    }
} 