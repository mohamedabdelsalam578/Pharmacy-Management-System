package gui.components;

import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A styled button component with icon, title and description
 * Used for dashboard action buttons and similar UI elements
 */
public class ActionButton extends JPanel {
    private final JLabel iconLabel;
    private final JLabel titleLabel;
    private final JLabel descriptionLabel;
    private ActionListener actionListener;

    /**
     * Create a new action button
     * @param title The button title
     * @param description The button description
     * @param icon The button icon
     */
    public ActionButton(String title, String description, Icon icon) {
        setLayout(new BorderLayout(10, 0));
        setBackground(ThemeColors.SURFACE);
        setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon on the left
        iconLabel = new JLabel(icon);
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 5));
        
        // Text panel on the right
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(ThemeColors.SURFACE);
        
        titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        
        descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(ThemeFonts.REGULAR_SMALL);
        descriptionLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        
        textPanel.add(titleLabel);
        textPanel.add(descriptionLabel);
        
        add(iconLabel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
        
        // Add hover effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(ThemeColors.PRIMARY_LIGHT);
                textPanel.setBackground(ThemeColors.PRIMARY_LIGHT);
                titleLabel.setForeground(ThemeColors.PRIMARY);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(ThemeColors.SURFACE);
                textPanel.setBackground(ThemeColors.SURFACE);
                titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(ActionButton.this, ActionEvent.ACTION_PERFORMED, "clicked"));
                }
            }
        });
    }
    
    /**
     * Add an action listener to the button
     * @param listener The action listener
     */
    public void addActionListener(ActionListener listener) {
        this.actionListener = listener;
    }
    
    /**
     * ActionListener interface for button events
     */
    public interface ActionListener {
        void actionPerformed(ActionEvent e);
    }
} 