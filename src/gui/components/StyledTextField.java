package gui.components;

import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeSizes;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StyledTextField extends JTextField {
    private String placeholder;
    private boolean isPlaceholder;
    private boolean suppressEvents = false;
    
    public StyledTextField() {
        this("");
    }
    
    public StyledTextField(String placeholder) {
        super();
        this.placeholder = placeholder;
        this.isPlaceholder = placeholder != null && !placeholder.isEmpty();
        
        // Set text field properties
        setFont(ThemeFonts.REGULAR_MEDIUM);
        setForeground(ThemeColors.TEXT_PRIMARY);
        setBackground(ThemeColors.SURFACE);
        setCaretColor(ThemeColors.PRIMARY);
        setBorder(createBorder());
        setPreferredSize(new Dimension(
            200,
            ThemeSizes.TEXT_FIELD_HEIGHT
        ));
        
        // Named inner class for focus events
        class PlaceholderFocusListener implements FocusListener {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholder) {
                    clearPlaceholder();
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (StyledTextField.super.getText().isEmpty()) {
                    showPlaceholder();
                }
            }
        }
        // Listen for focus events
        addFocusListener(new PlaceholderFocusListener());
        
        // Named inner class for mouse events
        class PlaceholderMouseListener extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isPlaceholder) {
                    clearPlaceholder();
                    requestFocusInWindow();
                }
            }
        }
        // Listen for clicks
        addMouseListener(new PlaceholderMouseListener());
        
        // Named inner class for document events
        class PlaceholderDocumentListener implements DocumentListener {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (isPlaceholder && !suppressEvents) {
                    SwingUtilities.invokeLater(() -> clearPlaceholder());
                }
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {}
            
            @Override
            public void changedUpdate(DocumentEvent e) {}
        }
        // Listen for text changes
        getDocument().addDocumentListener(new PlaceholderDocumentListener());
        
        // Initialize with placeholder if provided
        if (isPlaceholder) {
            showPlaceholder();
        }
    }
    
    private void clearPlaceholder() {
        suppressEvents = true;
        isPlaceholder = false;
        super.setText("");
        setForeground(ThemeColors.TEXT_PRIMARY);
        suppressEvents = false;
    }
    
    private void showPlaceholder() {
        suppressEvents = true;
        isPlaceholder = true;
        super.setText(placeholder);
        setForeground(ThemeColors.TEXT_SECONDARY);
        suppressEvents = false;
    }
    
    private Border createBorder() {
        return new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(
                ThemeSizes.PADDING_SMALL,
                ThemeSizes.PADDING_MEDIUM,
                ThemeSizes.PADDING_SMALL,
                ThemeSizes.PADDING_MEDIUM
            )
        );
    }
    
    @Override
    public String getText() {
        if (isPlaceholder) {
            return "";
        }
        return super.getText();
    }
    
    @Override
    public void setText(String text) {
        if (text == null || text.isEmpty()) {
            if (placeholder != null && !placeholder.isEmpty()) {
                showPlaceholder();
            } else {
                suppressEvents = true;
                isPlaceholder = false;
                super.setText("");
                setForeground(ThemeColors.TEXT_PRIMARY);
                suppressEvents = false;
            }
        } else {
            suppressEvents = true;
            isPlaceholder = false;
            super.setText(text);
            setForeground(ThemeColors.TEXT_PRIMARY);
            suppressEvents = false;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (isPlaceholder && !hasFocus()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );
            
            g2.setColor(ThemeColors.TEXT_SECONDARY);
            FontMetrics fm = g2.getFontMetrics();
            int x = ThemeSizes.PADDING_MEDIUM;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, x, y);
            
            g2.dispose();
        }
    }
} 