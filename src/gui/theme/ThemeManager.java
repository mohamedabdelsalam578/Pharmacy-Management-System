package gui.theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

// Import the RoundedBorder class
import gui.components.RoundedBorder;

public class ThemeManager {
    private static ThemeManager instance;
    private String currentTheme;
    private Map<String, Theme> themes;
    private static final int BORDER_RADIUS = 6;
    
    private ThemeManager() {
        themes = new HashMap<>();
        initializeThemes();
        currentTheme = "default";
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    private void initializeThemes() {
        // Default Theme
        Theme defaultTheme = new Theme(
            ThemeColors.PRIMARY,
            ThemeColors.BACKGROUND,
            ThemeColors.TEXT_PRIMARY,
            ThemeColors.SURFACE
        );
        themes.put("default", defaultTheme);
        
        // Dark Theme
        Theme darkTheme = new Theme(
            new Color(0, 150, 255),
            new Color(33, 33, 33),
            new Color(255, 255, 255),
            new Color(48, 48, 48)
        );
        themes.put("dark", darkTheme);
        
        // Light Theme
        Theme lightTheme = new Theme(
            new Color(0, 120, 212),
            new Color(250, 250, 250),
            new Color(33, 33, 33),
            new Color(255, 255, 255)
        );
        themes.put("light", lightTheme);
    }
    
    public void setTheme(String themeName) {
        if (themes.containsKey(themeName)) {
            currentTheme = themeName;
            Theme theme = themes.get(themeName);
            applyTheme(theme);
        }
    }
    
    public String getCurrentTheme() {
        return currentTheme;
    }
    
    private void applyTheme(Theme theme) {
        // Update UIManager defaults
        UIManager.put("Button.background", theme.getPrimaryColor());
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", ThemeFonts.REGULAR_MEDIUM);
        
        UIManager.put("Label.foreground", theme.getTextColor());
        UIManager.put("Label.font", ThemeFonts.REGULAR_MEDIUM);
        
        UIManager.put("TextField.background", theme.getSurfaceColor());
        UIManager.put("TextField.foreground", theme.getTextColor());
        UIManager.put("TextField.font", ThemeFonts.REGULAR_MEDIUM);
        
        UIManager.put("ComboBox.background", theme.getSurfaceColor());
        UIManager.put("ComboBox.foreground", theme.getTextColor());
        UIManager.put("ComboBox.font", ThemeFonts.REGULAR_MEDIUM);
        
        UIManager.put("Table.background", theme.getSurfaceColor());
        UIManager.put("Table.foreground", theme.getTextColor());
        UIManager.put("Table.font", ThemeFonts.REGULAR_MEDIUM);
        
        UIManager.put("Panel.background", theme.getBackgroundColor());
        
        // Update all existing components
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
    }
    
    /**
     * Apply default styling to a text input component
     * 
     * @param component The text component to style
     */
    public static void applyTextComponentStyling(JTextComponent component) {
        component.setFont(ThemeFonts.REGULAR_MEDIUM);
        component.setForeground(ThemeColors.TEXT_PRIMARY);
        component.setBackground(ThemeColors.SURFACE);
        component.setCaretColor(ThemeColors.PRIMARY);
        component.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(
                ThemeSizes.PADDING_SMALL,
                ThemeSizes.PADDING_MEDIUM,
                ThemeSizes.PADDING_SMALL,
                ThemeSizes.PADDING_MEDIUM
            )
        ));
    }
    
    /**
     * Applies card-like styling to a JPanel (background, rounded border, padding).
     * Use this for dashboard cards or main content panels.
     *
     * @param panel The JPanel to style.
     */
    public static void applyCardStyling(JPanel panel) {
        panel.setOpaque(true); // Ensure background color is painted
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new RoundedBorder(ThemeSizes.CARD_BORDER_RADIUS, ThemeColors.BORDER_LIGHT), // Use existing RoundedBorder
            new EmptyBorder(ThemeSizes.PADDING_MEDIUM, ThemeSizes.PADDING_MEDIUM, ThemeSizes.PADDING_MEDIUM, ThemeSizes.PADDING_MEDIUM) // Add padding
        ));
    }
    
    // Define named inner classes for mouse and focus listeners
    private static class ComboBoxMouseListener extends MouseAdapter {
        private final boolean[] isMouseOver;
        private final JComboBox<?> comboBox;
        
        public ComboBoxMouseListener(boolean[] isMouseOver, JComboBox<?> comboBox) {
            this.isMouseOver = isMouseOver;
            this.comboBox = comboBox;
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {
            isMouseOver[0] = true;
            comboBox.repaint();
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            isMouseOver[0] = false;
            comboBox.repaint();
        }
    }
    
    private static class ComboBoxFocusListener extends FocusAdapter {
        private final boolean[] hasFocus;
        private final JComboBox<?> comboBox;
        
        public ComboBoxFocusListener(boolean[] hasFocus, JComboBox<?> comboBox) {
            this.hasFocus = hasFocus;
            this.comboBox = comboBox;
        }
        
        @Override
        public void focusGained(FocusEvent e) {
            hasFocus[0] = true;
            comboBox.repaint();
        }
        
        @Override
        public void focusLost(FocusEvent e) {
            hasFocus[0] = false;
            comboBox.repaint();
        }
    }
    
    private static class ModernListCellRenderer extends DefaultListCellRenderer {
        private final int VERTICAL_PADDING = 8;
        private final int HORIZONTAL_PADDING = 12;
        private final int ICON_TEXT_GAP = 10;
        
        public ModernListCellRenderer() {
            // Initialize with modern styling
            setOpaque(true);
            setIconTextGap(ICON_TEXT_GAP);
            setBorder(new EmptyBorder(VERTICAL_PADDING, HORIZONTAL_PADDING, 
                                    VERTICAL_PADDING, HORIZONTAL_PADDING));
        }
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            
            if (isSelected) {
                label.setBackground(ThemeColors.PRIMARY_LIGHT);
                label.setForeground(ThemeColors.TEXT_PRIMARY);
            } else {
                label.setBackground(ThemeColors.SURFACE);
                label.setForeground(ThemeColors.TEXT_PRIMARY);
            }
            
            // Add spacing and style
            label.setBorder(new EmptyBorder(VERTICAL_PADDING, HORIZONTAL_PADDING, 
                                        VERTICAL_PADDING, HORIZONTAL_PADDING));
            return label;
        }
    }

    /**
     * Apply modern styling to a combo box with custom rendering and behavior
     * 
     * @param comboBox The combo box to style
     */
    public static void applyModernComboBoxStyling(JComboBox<?> comboBox) {
        // Set modern UI properties
        comboBox.setBackground(ThemeColors.SURFACE);
        comboBox.setForeground(ThemeColors.TEXT_PRIMARY);
        comboBox.setFont(ThemeFonts.REGULAR_MEDIUM);
        
        // Apply modern UI with custom rounded corners and flat style
        comboBox.setUI(new ModernComboBoxUI());
        
        // Add hover effect
        final boolean[] isMouseOver = {false};
        final boolean[] hasFocus = {false};
        
        // Use named inner classes instead of anonymous classes
        comboBox.addMouseListener(new ComboBoxMouseListener(isMouseOver, comboBox));
        comboBox.addFocusListener(new ComboBoxFocusListener(hasFocus, comboBox));
        
        // Set modern renderer with improved visuals using named inner class
        comboBox.setRenderer(new ModernListCellRenderer());
    }
    
    /**
     * Apply default styling to a combo box
     * 
     * @param comboBox The combo box to style
     */
    public static void applyComboBoxStyling(JComboBox<?> comboBox) {
        // Use the modern styling by default
        applyModernComboBoxStyling(comboBox);
    }
    
    /**
     * Modern UI for ComboBox
     */
    private static class ModernComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            return new ArrowButton();
        }
        
        @Override
        protected ComboPopup createPopup() {
            return new ModernComboPopup(comboBox);
        }
        
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint background with rounded corners
            Color bgColor = comboBox.isEnabled() ? comboBox.getBackground() : ThemeColors.DISABLED;
            
            // Draw background with subtle gradient
            GradientPaint gradient = new GradientPaint(
                0, 0, 
                bgColor, 
                0, bounds.height, 
                new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 245)
            );
            
            g2.setPaint(gradient);
            g2.fill(new RoundRectangle2D.Double(bounds.x, bounds.y, bounds.width, bounds.height, 
                                             BORDER_RADIUS, BORDER_RADIUS));
            
            // Draw border
            Color borderColor = hasFocus ? ThemeColors.PRIMARY : ThemeColors.BORDER;
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(new RoundRectangle2D.Double(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 
                                             BORDER_RADIUS, BORDER_RADIUS));
            
            g2.dispose();
        }
        
        private class ModernComboPopup extends BasicComboPopup {
            public ModernComboPopup(JComboBox<Object> combo) {
                super(combo);
                setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER, 1));
                setBackground(ThemeColors.SURFACE);
            }
            
            @Override
            protected void configureList() {
                super.configureList();
                list.setSelectionBackground(ThemeColors.PRIMARY);
                list.setSelectionForeground(Color.WHITE);
                list.setBackground(ThemeColors.SURFACE);
            }
            
            @Override
            protected void configureScroller() {
                super.configureScroller();
                scroller.setBorder(BorderFactory.createEmptyBorder());
                // Add modern scrollbar styling if needed
            }
        }
        
        // Custom arrow button with a cleaner look
        private class ArrowButton extends JButton {
            public ArrowButton() {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(20, 20);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Draw the arrow in a subtle color
                Color arrowColor = isEnabled() ? ThemeColors.TEXT_SECONDARY : ThemeColors.DISABLED;
                g2.setColor(arrowColor);
                
                // Draw a clean minimal arrow
                int size = 8;
                int x = (width - size) / 2;
                int y = (height - size / 2) / 2;
                
                int[] xPoints = {x, x + size / 2, x + size};
                int[] yPoints = {y, y + size / 2, y};
                
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawPolyline(xPoints, yPoints, 3);
                
                g2.dispose();
            }
        }
    }
    
    /**
     * Apply modern styling with flat icons to a combo box renderer
     * 
     * @param comboBox The combo box to style
     * @param roleIconsMap Map of role names to icons
     * @param roleColorsMap Map of role names to colors
     */
    public static void applyRoleBasedStyling(JComboBox<String> comboBox, 
                                          Map<String, Icon> roleIconsMap,
                                          Map<String, Color> roleColorsMap) {
        applyModernComboBoxStyling(comboBox);
        
        comboBox.setRenderer(new DefaultListCellRenderer() {
            private final int VERTICAL_PADDING = 6;
            private final int HORIZONTAL_PADDING = 12;
            private final int ICON_TEXT_GAP = 8;
            
            {
                setOpaque(false);
                setIconTextGap(ICON_TEXT_GAP);
                setBorder(new EmptyBorder(VERTICAL_PADDING, HORIZONTAL_PADDING, 
                                         VERTICAL_PADDING, HORIZONTAL_PADDING));
            }
            
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                
                // Apply modern styling
                label.setOpaque(false);
                label.setFont(ThemeFonts.REGULAR_MEDIUM);
                label.setIconTextGap(ICON_TEXT_GAP);
                label.setBorder(new EmptyBorder(VERTICAL_PADDING, HORIZONTAL_PADDING, 
                                               VERTICAL_PADDING, HORIZONTAL_PADDING));
                
                // Set role-specific icon and color
                String role = (String) value;
                if (roleIconsMap.containsKey(role)) {
                    label.setIcon(roleIconsMap.get(role));
                }
                
                // Set colors based on selection state and role
                if (isSelected) {
                    label.setForeground(Color.WHITE);
                    label.setBackground(ThemeColors.PRIMARY);
                } else if (roleColorsMap.containsKey(role)) {
                    label.setForeground(roleColorsMap.get(role));
                    label.setBackground(ThemeColors.SURFACE);
                } else {
                    label.setForeground(ThemeColors.TEXT_PRIMARY);
                    label.setBackground(ThemeColors.SURFACE);
                }
                
                return label;
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint background with rounded corners if selected
                if (!isOpaque() && getBackground() != null) {
                    int width = getWidth();
                    int height = getHeight();
                    
                    g2.setColor(getBackground());
                    if (getBackground().equals(ThemeColors.PRIMARY)) {
                        // Selected item with rounded corners
                        g2.fill(new RoundRectangle2D.Double(0, 0, width, height, BORDER_RADIUS, BORDER_RADIUS));
                    } else {
                        g2.fillRect(0, 0, width, height);
                    }
                }
                
                g2.dispose();
                
                super.paintComponent(g);
            }
        });
    }
    
    /**
     * Create a flat, modern SVG-style icon for role selection
     * 
     * @param baseIcon Base icon to start with
     * @param color Color to apply to the icon
     * @return Styled icon
     */
    public static Icon createFlatRoleIcon(Icon baseIcon, Color color) {
        // If we have the base icon, return it as is
        if (baseIcon != null) {
            return baseIcon;
        }
        
        // Try to load from appropriate role icon based on color
        String iconName = getIconNameFromColor(color);
        if (iconName != null) {
            try {
                String path = "icons/" + iconName;
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    // Resize if necessary
                    if (icon.getIconWidth() != 16 || icon.getIconHeight() != 16) {
                        Image scaledImage = icon.getImage().getScaledInstance(
                            16, 16, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                    return icon;
                }
            } catch (Exception e) {
                // Fallback to colored circle if loading fails
                System.err.println("Error loading role icon: " + e.getMessage());
            }
        }
        
        // Otherwise create a simple colored icon as fallback
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(color);
                g2.fillOval(x + 2, y + 2, getIconWidth() - 4, getIconHeight() - 4);
                
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return 16;
            }
            
            @Override
            public int getIconHeight() {
                return 16;
            }
        };
    }
    
    /**
     * Get appropriate icon name based on role color
     * 
     * @param color The role color
     * @return The icon filename, or null if no match
     */
    private static String getIconNameFromColor(Color color) {
        if (color.equals(ThemeColors.PRIMARY)) return "dashboard.png";
        if (color.equals(ThemeColors.SUCCESS)) return "medicine.png";
        if (color.equals(ThemeColors.DANGER)) return "logout.png";
        if (color.equals(ThemeColors.WARNING)) return "prescription.png";
        if (color.equals(ThemeColors.INFO)) return "report.png";
        return null;
    }
    
    private static class Theme {
        private final Color primaryColor;
        private final Color backgroundColor;
        private final Color textColor;
        private final Color surfaceColor;
        
        public Theme(Color primaryColor, Color backgroundColor, Color textColor, Color surfaceColor) {
            this.primaryColor = primaryColor;
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
            this.surfaceColor = surfaceColor;
        }
        
        public Color getPrimaryColor() {
            return primaryColor;
        }
        
        public Color getBackgroundColor() {
            return backgroundColor;
        }
        
        public Color getTextColor() {
            return textColor;
        }
        
        public Color getSurfaceColor() {
            return surfaceColor;
        }
    }
} 