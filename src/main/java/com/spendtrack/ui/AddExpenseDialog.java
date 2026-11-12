package com.spendtrack.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.spendtrack.dao.ExpenseDao;
import com.spendtrack.model.Category;
import com.spendtrack.model.Expense;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

/**
 * Modern modal dialog for creating a new expense.
 * Encapsulates input validation, keyboard shortcuts, and clean visual styling.
 */
public class AddExpenseDialog extends JDialog {
    private final ExpenseDao expenseDao;
    private final Runnable onSuccessCallback;

    private final JTextField titleField = new JTextField(20);
    private final JTextField amountField = new JTextField(20);
    private final JComboBox<Category> categoryComboBox = new JComboBox<>(Category.values());
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 20);

    public AddExpenseDialog(JFrame parent, ExpenseDao expenseDao, Runnable onSuccessCallback) {
        super(parent, "New Expense", true);
        this.expenseDao = expenseDao;
        this.onSuccessCallback = onSuccessCallback;

        setSize(450, 490);
        setResizable(false);
        setLocationRelativeTo(parent);

        initUi();

        // Auto-focus title field when dialog opens
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                titleField.requestFocusInWindow();
            }
        });
    }

    private void initUi() {
        JPanel contentPane = new JPanel(new BorderLayout(0, 16));
        contentPane.setBorder(new EmptyBorder(24, 28, 24, 28));
        setContentPane(contentPane);

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        JLabel titleLabel = new JLabel("Add New Expense");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: 140% bold;");
        JLabel subtitleLabel = new JLabel("Record a purchase or payment into your tracker.");
        subtitleLabel.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground;");
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Form Fields
        JPanel formGrid = new JPanel(new GridLayout(4, 1, 0, 12));

        titleField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. Grocery store, Swiggy, Metro pass");
        titleField.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");

        amountField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0.00");
        amountField.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");

        categoryComboBox.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");

        dateField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "YYYY-MM-DD");
        dateField.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");

        formGrid.add(createFieldGroup("Description", titleField));
        formGrid.add(createFieldGroup("Amount (₹)", amountField));
        formGrid.add(createFieldGroup("Category", categoryComboBox));
        formGrid.add(createFieldGroup("Date", dateField));

        contentPane.add(formGrid, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Save Expense");
        saveBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #6366F1; foreground: #ffffff; font: bold;");
        saveBtn.addActionListener(e -> saveExpense());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // Keyboard Actions: Enter to submit, ESC to cancel
        titleField.addActionListener(e -> saveExpense());
        amountField.addActionListener(e -> saveExpense());
        dateField.addActionListener(e -> saveExpense());

        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private JPanel createFieldGroup(String labelText, JComponent component) {
        JPanel group = new JPanel(new BorderLayout(0, 4));
        JLabel label = new JLabel(labelText);
        label.putClientProperty(FlatClientProperties.STYLE, "font: 90% bold; foreground: $Label.disabledForeground;");
        group.add(label, BorderLayout.NORTH);
        group.add(component, BorderLayout.CENTER);
        return group;
    }

    private void saveExpense() {
        String title = titleField.getText().trim();
        String amountText = amountField.getText().trim();
        String dateText = dateField.getText().trim();
        Category category = (Category) categoryComboBox.getSelectedItem();

        if (title.isBlank() || amountText.isBlank() || dateText.isBlank() || category == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            LocalDate date = LocalDate.parse(dateText);

            Expense expense = new Expense(title, amount, category, date);
            expenseDao.save(expense);

            dispose();
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number in Rupees (e.g. 350.50).", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
        }
    }
}
