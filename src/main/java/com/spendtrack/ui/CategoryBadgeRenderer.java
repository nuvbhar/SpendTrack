package com.spendtrack.ui;

import com.spendtrack.model.Category;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Modern pill-shaped badge renderer for Expense Categories in JTable.
 * Demonstrates Custom UI Component rendering, Polymorphism, and Encapsulation.
 */
public class CategoryBadgeRenderer extends JPanel implements TableCellRenderer {
    private final JLabel label = new JLabel();
    private Color badgeBackground = new Color(0, 0, 0, 0);

    public CategoryBadgeRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        setOpaque(false);
        label.setBorder(new EmptyBorder(3, 10, 3, 10));
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        add(label);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if (value instanceof Category category) {
            label.setText(category.getIcon() + "  " + category.getDisplayName());
            label.setForeground(category.getForegroundColor());
            badgeBackground = category.getBackgroundColor();
        } else if (value != null) {
            label.setText(value.toString());
            badgeBackground = new Color(255, 255, 255, 20);
        }
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle bounds = label.getBounds();
        g2.setColor(badgeBackground);
        g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);
        g2.dispose();
    }
}
