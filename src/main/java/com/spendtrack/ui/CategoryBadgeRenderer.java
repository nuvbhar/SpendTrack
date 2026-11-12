package com.spendtrack.ui;

import com.spendtrack.model.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Modern pill-shaped badge renderer for Expense Categories in JTable.
 * Supports row selection highlighting with frosted white aesthetics.
 */
public class CategoryBadgeRenderer extends DefaultTableCellRenderer {
    private Category currentCategory;
    private boolean isRowSelected;

    public CategoryBadgeRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        this.isRowSelected = isSelected;

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        }

        if (value instanceof Category category) {
            this.currentCategory = category;
            setText(category.getIcon() + "  " + category.getDisplayName());
            setFont(getFont().deriveFont(Font.BOLD, 12f));

            if (isSelected) {
                setForeground(Color.WHITE);
            } else {
                setForeground(category.getForegroundColor());
            }
        } else {
            this.currentCategory = null;
            setText(value != null ? value.toString() : "");
            setForeground(isSelected ? Color.WHITE : UIManager.getColor("Label.foreground"));
        }
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Paint row selection background across the entire cell
        if (isRowSelected) {
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Paint Pill Badge
        if (currentCategory != null && getText() != null && !getText().isEmpty()) {
            FontMetrics fm = g2.getFontMetrics(getFont());
            int textWidth = fm.stringWidth(getText());
            int badgeWidth = Math.min(getWidth() - 12, textWidth + 24);
            int badgeHeight = 24;
            int x = (getWidth() - badgeWidth) / 2;
            int y = (getHeight() - badgeHeight) / 2;

            if (isRowSelected) {
                Color base = currentCategory.getForegroundColor();
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 90));
            } else {
                g2.setColor(currentCategory.getBackgroundColor());
            }
            g2.fillRoundRect(x, y, badgeWidth, badgeHeight, 12, 12);

            // Badge border
            if (isRowSelected) {
                g2.setColor(new Color(255, 255, 255, 180));
            } else {
                Color base = currentCategory.getForegroundColor();
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 70));
            }
            g2.drawRoundRect(x, y, badgeWidth - 1, badgeHeight - 1, 12, 12);
        }

        g2.dispose();

        // 3. Paint text via super (ensure opaque is false so background isn't overwritten)
        setOpaque(false);
        super.paintComponent(g);
    }
}
