package gui.components;

import gui.theme.ThemeColors;
import gui.theme.ThemeIcons;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Standard logout button for use in all panels
 */
public class LogoutButton extends StyledButton {
    
    /**
     * Create a standard logout button
     */
    public LogoutButton() {
        super("Logout");
        initialize();
    }
    
    /**
     * Create a standard logout button with a custom action listener
     * 
     * @param actionListener The action listener to be notified when the button is clicked
     */
    public LogoutButton(ActionListener actionListener) {
        super("Logout");
        initialize();
        addActionListener(actionListener);
    }
    
    private void initialize() {
        // Set standard icon and colors
        setIcon(ThemeIcons.LOGOUT);
        setBackground(ThemeColors.DANGER);
        setForeground(ThemeColors.TEXT_LIGHT);
        
        // Add default tooltip
        setToolTipText("Log out of the application");
    }
} 