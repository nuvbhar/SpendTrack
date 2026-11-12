package com.spendtrack.ui;

import com.spendtrack.model.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Modern pill-shaped badge renderer for Expense Categories in JTable.
 * Extends DefaultTableCellRenderer for robust cell lifecycle management.
 */
public class CategoryBadgeRenderer extends DefaultTableCellRenderer {
    private Category currentCategory;

    public CategoryBadgeRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Category category) {
            this.currentCategory = category;
            setText(category.getIcon() + "  " + category.getDisplayName());
            setForeground(category.getForegroundColor());
            setFont(getFont().deriveFont(Font.BOLD, 12f));
        } else {
            this.currentCategory = null;
            setText(value != null ? value.toString() : "");
            setForeground(UIManager.getColor("Label.foreground"));
        }
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (currentCategory != null && getText() != null && !getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            FontMetrics fm = g2.getFontMetrics(getFont());
            int textWidth = fm.stringWidth(getText());
            int badgeWidth = Math.min(getWidth() - 12, textWidth + 24);
            int badgeHeight = 24;
            int x = (getWidth() - badgeWidth) / 2;
            int y = (getHeight() - badgeHeight) / 2;

            g2.setColor(currentCategory.getBackgroundColor());
            g2.fillRoundRect(x, y, badgeWidth, badgeHeight, 12, 12);

            // Subtle border around badge
            g2.setColor(new Color(currentCategory.getForegroundColor().getRed(),
                                  currentCategory.getForegroundColor().getGreen(),
                                  currentCategory.getForegroundColor().getBlue(), 70));
            g2.drawRoundRect(x, y, badgeWidth - 1, badgeHeight - 1, 12, 12);

            g2.dispose();
        }
        super.paintComponent(g);
    }
}
