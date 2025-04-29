package gui.components;

import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeSizes;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MessageDialog {
    public static void showError(Component parent, String message) {
        showDialog(parent, "Error", message, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showSuccess(Component parent, String message) {
        showDialog(parent, "Success", message, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void showWarning(Component parent, String message) {
        showDialog(parent, "Warning", message, JOptionPane.WARNING_MESSAGE);
    }
    
    public static void showInfo(Component parent, String message) {
        showDialog(parent, "Information", message, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static boolean showConfirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            createMessagePanel(message),
            "Confirm",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    private static void showDialog(Component parent, String title, String message, int messageType) {
        JOptionPane.showMessageDialog(
            parent,
            createMessagePanel(message),
            title,
            messageType
        );
    }
    
    private static JPanel createMessagePanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(createBorder());
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
        messageLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        messageLabel.setBorder(new EmptyBorder(
            ThemeSizes.PADDING_MEDIUM,
            ThemeSizes.PADDING_MEDIUM,
            ThemeSizes.PADDING_MEDIUM,
            ThemeSizes.PADDING_MEDIUM
        ));
        
        panel.add(messageLabel, BorderLayout.CENTER);
        return panel;
    }
    
    private static Border createBorder() {
        return new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(
                ThemeSizes.PADDING_MEDIUM,
                ThemeSizes.PADDING_MEDIUM,
                ThemeSizes.PADDING_MEDIUM,
                ThemeSizes.PADDING_MEDIUM
            )
        );
    }
    
    // Customize JOptionPane
    static {
        UIManager.put("OptionPane.background", ThemeColors.SURFACE);
        UIManager.put("Panel.background", ThemeColors.SURFACE);
        UIManager.put("OptionPane.messageFont", ThemeFonts.REGULAR_MEDIUM);
        UIManager.put("OptionPane.buttonFont", ThemeFonts.REGULAR_MEDIUM);
        UIManager.put("OptionPane.messageForeground", ThemeColors.TEXT_PRIMARY);
        UIManager.put("OptionPane.buttonBackground", ThemeColors.PRIMARY);
        UIManager.put("OptionPane.buttonForeground", Color.WHITE);
    }
} 