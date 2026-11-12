package com.spendtrack.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.spendtrack.model.Category;
import com.spendtrack.model.Expense;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Modern analytics dialog displaying spending distribution by category.
 * Demonstrates Java Streams (groupingBy, summingDouble), Lambdas, and custom UI components.
 */
public class AnalyticsDialog extends JDialog {

    public AnalyticsDialog(JFrame parent, List<Expense> expenses) {
        super(parent, "Category Spending Analytics", true);
        setSize(480, 520);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUi(expenses);
    }

    private void initUi(List<Expense> expenses) {
        JPanel contentPane = new JPanel(new BorderLayout(0, 16));
        contentPane.setBorder(new EmptyBorder(22, 26, 22, 26));
        setContentPane(contentPane);

        double totalSpend = expenses.stream().mapToDouble(Expense::amount).sum();

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        JLabel titleLabel = new JLabel("📊 Category Spending Analytics");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: 140% bold;");
        JLabel subtitleLabel = new JLabel("Total Analyzed: " + ExpenseTableModel.formatRupees(totalSpend) + " across " + expenses.size() + " transactions");
        subtitleLabel.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground;");
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Group & sort using modern Java Streams API
        Map<Category, Double> categoryTotals = expenses.stream()
            .collect(Collectors.groupingBy(Expense::category, Collectors.summingDouble(Expense::amount)));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        if (expenses.isEmpty()) {
            JLabel emptyLabel = new JLabel("No transactions logged yet.");
            emptyLabel.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground;");
            listPanel.add(emptyLabel);
        } else {
            categoryTotals.entrySet().stream()
                .sorted(Map.Entry.<Category, Double>comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> {
                    Category cat = entry.getKey();
                    double amount = entry.getValue();
                    double percentage = totalSpend > 0 ? (amount / totalSpend) * 100 : 0.0;
                    listPanel.add(createCategoryRow(cat, amount, percentage));
                    listPanel.add(Box.createVerticalStrut(10));
                });
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Close Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton closeBtn = new JButton("Close");
        closeBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 6,18,6,18;");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private JPanel createCategoryRow(Category category, double amount, double percentage) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 25), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        row.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");

        // Top info: Name + Amount & Percentage
        JPanel topText = new JPanel(new BorderLayout());
        JLabel catLabel = new JLabel(category.getIcon() + " " + category.getDisplayName());
        catLabel.setFont(catLabel.getFont().deriveFont(Font.BOLD));
        catLabel.setForeground(category.getForegroundColor());

        JLabel amountLabel = new JLabel(String.format("%s (%.1f%%)", ExpenseTableModel.formatRupees(amount), percentage));
        amountLabel.setFont(amountLabel.getFont().deriveFont(Font.BOLD));

        topText.add(catLabel, BorderLayout.WEST);
        topText.add(amountLabel, BorderLayout.EAST);

        // Progress bar visual representation
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) Math.round(percentage));
        progressBar.setForeground(category.getForegroundColor());
        progressBar.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        progressBar.setPreferredSize(new Dimension(100, 6));

        row.add(topText, BorderLayout.NORTH);
        row.add(progressBar, BorderLayout.SOUTH);

        return row;
    }
}

