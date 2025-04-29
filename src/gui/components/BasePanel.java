package gui.components;

import gui.MainFrame;
import gui.theme.ThemeColors;
import gui.theme.ThemeSizes;
import services.PharmacyService;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public abstract class BasePanel extends JPanel {
    protected MainFrame mainFrame;
    protected PharmacyService service;
    
    public BasePanel() {
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
            ThemeSizes.PADDING_MEDIUM,
            ThemeSizes.PADDING_MEDIUM,
            ThemeSizes.PADDING_MEDIUM,
            ThemeSizes.PADDING_MEDIUM
        ));
    }
    
    public BasePanel(MainFrame mainFrame) {
        this();
        this.mainFrame = mainFrame;
        this.service = mainFrame != null ? mainFrame.getService() : null;
        initializeComponents();
    }
    
    protected Border createBorder() {
        return BorderFactory.createEmptyBorder(
            ThemeSizes.PADDING_MEDIUM,
            ThemeSizes.PADDING_MEDIUM,
            ThemeSizes.PADDING_MEDIUM,
            ThemeSizes.PADDING_MEDIUM
        );
    }
    
    protected abstract void initializeComponents();
    
    public void refresh() {
        removeAll();
        initializeComponents();
        revalidate();
        repaint();
    }
    
    protected void addComponent(JComponent component, int position) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = position;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(
            ThemeSizes.PADDING_SMALL,
            ThemeSizes.PADDING_SMALL,
            ThemeSizes.PADDING_SMALL,
            ThemeSizes.PADDING_SMALL
        );
        
        if (getLayout() instanceof GridBagLayout) {
            add(component, gbc);
        } else {
            add(component);
        }
    }
    
    protected void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    protected void showWarning(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Warning",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    protected boolean showConfirm(String message) {
        int result = JOptionPane.showConfirmDialog(
            this,
            message,
            "Confirm",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    protected void addPaddedComponent(Component component) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setBackground(getBackground());
        wrapper.setBorder(BorderFactory.createEmptyBorder(
            ThemeSizes.PADDING_SMALL,
            ThemeSizes.PADDING_SMALL,
            ThemeSizes.PADDING_SMALL,
            ThemeSizes.PADDING_SMALL
        ));
        wrapper.add(component, BorderLayout.CENTER);
        add(wrapper);
    }
    
    /**
     * Adds a logout button to the panel
     * 
     * @param panel The panel to add the logout button to
     * @param constraints The layout constraints for the button (if using layouts like GridBagLayout)
     * @return The created LogoutButton instance
     */
    protected LogoutButton addLogoutButton(JPanel panel, Object constraints) {
        LogoutButton logoutButton = new LogoutButton(e -> {
            if (mainFrame != null) {
                boolean confirmed = showConfirm("Are you sure you want to log out?");
                if (confirmed) {
                    mainFrame.logout();
                }
            }
        });
        
        if (constraints != null) {
            panel.add(logoutButton, constraints);
        } else {
            panel.add(logoutButton);
        }
        
        return logoutButton;
    }
    
    /**
     * Adds a logout button to this panel
     * 
     * @param constraints The layout constraints for the button (if using layouts like GridBagLayout)
     * @return The created LogoutButton instance
     */
    protected LogoutButton addLogoutButton(Object constraints) {
        return addLogoutButton(this, constraints);
    }
    
    /**
     * Adds a logout button to this panel with default constraints
     * 
     * @return The created LogoutButton instance
     */
    protected LogoutButton addLogoutButton() {
        return addLogoutButton(this, null);
    }
} 