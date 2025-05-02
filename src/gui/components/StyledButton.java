package gui.components;

import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeSizes;
// import gui.components.RoundedBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StyledButton extends JButton {
    private Color backgroundColor;
    private Color hoverColor;
    private Color textColor;
    private boolean isActive;
    
    public StyledButton(String text) {
        super(text);
        initialize();
    }
    
    public StyledButton(String text, Icon icon) {
        super(text, icon);
        initialize();
    }
    
    // Define a named inner class instead of an anonymous class
    private class ButtonMouseListener extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            if (isEnabled()) {
                setBackground(ThemeColors.PRIMARY_HOVER);
            }
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            if (isEnabled()) {
                setBackground(isActive ? ThemeColors.PRIMARY_DARK : ThemeColors.PRIMARY);
            }
        }
    }
    
    private void initialize() {
        setFont(ThemeFonts.BOLD_SECTION);
        setForeground(Color.WHITE);
        setBackground(ThemeColors.PRIMARY);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Set icon text gap
        setIconTextGap(10);
        
        // Add hover effects using the named inner class
        addMouseListener(new ButtonMouseListener());
        
        setBorder(new RoundedBorder(15));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        
        // Draw background
        g2.setColor(getBackground());
        g2.fillRoundRect(
            0, 0, getWidth(), getHeight(),
            ThemeSizes.BORDER_RADIUS_MEDIUM,
            ThemeSizes.BORDER_RADIUS_MEDIUM
        );
        
        // Draw text
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), x, y);
        
        g2.dispose();
    }
    
    public void setActive(boolean active) {
        isActive = active;
        setBackground(active ? ThemeColors.PRIMARY_DARK : ThemeColors.PRIMARY);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setBackground(isActive ? ThemeColors.PRIMARY_DARK : ThemeColors.PRIMARY);
            setForeground(ThemeColors.TEXT_PRIMARY);
        } else {
            setBackground(ThemeColors.DISABLED);
            setForeground(ThemeColors.TEXT_DISABLED);
        }
    }
} 