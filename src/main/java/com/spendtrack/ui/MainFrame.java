package com.spendtrack.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.spendtrack.dao.ExpenseDao;
import com.spendtrack.model.Category;
import com.spendtrack.model.Expense;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Main application window for SpendTrack.
 * Encapsulates the UI components, Event Listeners, and visual layout.
 * Applies Modern Swing UI/UX design principles.
 */
public class MainFrame extends JFrame {
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    private final ExpenseDao expenseDao;
    private final ExpenseTableModel tableModel = new ExpenseTableModel();
    private final JTable expenseTable = new JTable(tableModel);

    private final JLabel totalAmountLabel = new JLabel("$0.00");
    private final JComboBox<String> filterComboBox = new JComboBox<>();

    private final JTextField titleField = new JTextField(15);
    private final JTextField amountField = new JTextField(8);
    private final JComboBox<Category> categoryComboBox = new JComboBox<>(Category.values());
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);

    public MainFrame(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;

        setTitle("SpendTrack — Modern Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 620);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);

        initUi();
        refreshData();
    }

    private void initUi() {
        JPanel contentPane = new JPanel(new BorderLayout(16, 16));
        contentPane.setBorder(new EmptyBorder(18, 20, 18, 20));
        setContentPane(contentPane);

        // 1. Top Section: Header & Summary Card
        contentPane.add(createTopPanel(), BorderLayout.NORTH);

        // 2. Center Section: Filter Toolbar + Table
        contentPane.add(createCenterPanel(), BorderLayout.CENTER);

        // 3. Bottom Section: Quick-Add Expense Form
        contentPane.add(createFormPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());

        // App Branding
        JPanel brandPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        JLabel title = new JLabel("SpendTrack");
        title.putClientProperty(FlatClientProperties.STYLE, "font: 160% bold;");
        JLabel subtitle = new JLabel("Modern OOP Expense Tracking System");
        subtitle.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground;");
        brandPanel.add(title);
        brandPanel.add(subtitle);
        topPanel.add(brandPanel, BorderLayout.WEST);

        // KPI Summary Card: Total Expenses
        JPanel statCard = new JPanel(new GridLayout(2, 1, 0, 2));
        statCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true),
            new EmptyBorder(8, 18, 8, 18)
        ));
        statCard.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: $Panel.background;");

        JLabel statLabel = new JLabel("TOTAL EXPENDITURE");
        statLabel.putClientProperty(FlatClientProperties.STYLE, "font: 75% bold; foreground: $Label.disabledForeground;");

        totalAmountLabel.putClientProperty(FlatClientProperties.STYLE, "font: 160% bold; foreground: #e06c75;");
        totalAmountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        statCard.add(statLabel);
        statCard.add(totalAmountLabel);
        topPanel.add(statCard, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));

        // Filter Bar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.add(new JLabel("Category Filter:"));

        filterComboBox.addItem("All Categories");
        for (Category cat : Category.values()) {
            filterComboBox.addItem(cat.getDisplayName());
        }
        filterComboBox.addActionListener(e -> applyFilter());
        toolbar.add(filterComboBox);

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        deleteBtn.addActionListener(e -> deleteSelectedExpense());

        JPanel tableActionWrapper = new JPanel(new BorderLayout());
        tableActionWrapper.add(toolbar, BorderLayout.WEST);
        tableActionWrapper.add(deleteBtn, BorderLayout.EAST);
        centerPanel.add(tableActionWrapper, BorderLayout.NORTH);

        // Table Styling
        expenseTable.setRowHeight(28);
        expenseTable.setShowGrid(false);
        expenseTable.setIntercellSpacing(new Dimension(0, 0));
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Right-align the Amount column
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        expenseTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        // Format Date column to center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        expenseTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        expenseTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(10, 8));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Quick Add Expense"),
            new EmptyBorder(6, 12, 10, 12)
        ));

        // Placeholders & Client Properties
        titleField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Description (e.g. Groceries)");
        amountField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Amount (e.g. 24.50)");
        dateField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "YYYY-MM-DD");

        JPanel inputsGrid = new JPanel(new GridLayout(1, 4, 12, 0));

        JPanel p1 = new JPanel(new BorderLayout(0, 3));
        p1.add(new JLabel("Description"), BorderLayout.NORTH);
        p1.add(titleField, BorderLayout.CENTER);

        JPanel p2 = new JPanel(new BorderLayout(0, 3));
        p2.add(new JLabel("Amount ($)"), BorderLayout.NORTH);
        p2.add(amountField, BorderLayout.CENTER);

        JPanel p3 = new JPanel(new BorderLayout(0, 3));
        p3.add(new JLabel("Category"), BorderLayout.NORTH);
        p3.add(categoryComboBox, BorderLayout.CENTER);

        JPanel p4 = new JPanel(new BorderLayout(0, 3));
        p4.add(new JLabel("Date"), BorderLayout.NORTH);
        p4.add(dateField, BorderLayout.CENTER);

        inputsGrid.add(p1);
        inputsGrid.add(p2);
        inputsGrid.add(p3);
        inputsGrid.add(p4);

        JButton addBtn = new JButton("+ Add Expense");
        addBtn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        addBtn.addActionListener(e -> addExpense());

        // Keyboard Shortcut: Pressing enter in amount field submits form
        amountField.addActionListener(e -> addExpense());
        titleField.addActionListener(e -> addExpense());

        formPanel.add(inputsGrid, BorderLayout.CENTER);
        formPanel.add(addBtn, BorderLayout.EAST);

        return formPanel;
    }

    private void addExpense() {
        String title = titleField.getText().trim();
        String amountText = amountField.getText().trim();
        String dateText = dateField.getText().trim();
        Category category = (Category) categoryComboBox.getSelectedItem();

        if (title.isBlank() || amountText.isBlank() || dateText.isBlank() || category == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            LocalDate date = LocalDate.parse(dateText);

            Expense expense = new Expense(title, amount, category, date);
            expenseDao.save(expense);

            // Reset inputs
            titleField.setText("");
            amountField.setText("");
            dateField.setText(LocalDate.now().toString());
            titleField.requestFocus();

            refreshData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number (e.g. 19.99).", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense from the table to delete.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Expense expense = tableModel.getExpenseAt(selectedRow);
        if (expense != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete '" + expense.title() + "' (" + CURRENCY_FORMAT.format(expense.amount()) + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                expenseDao.delete(expense.id());
                refreshData();
            }
        }
    }

    private void applyFilter() {
        int selectedIndex = filterComboBox.getSelectedIndex();
        if (selectedIndex <= 0) {
            tableModel.setExpenses(expenseDao.findAll());
        } else {
            Category selectedCategory = Category.values()[selectedIndex - 1];
            tableModel.setExpenses(expenseDao.findByCategory(selectedCategory));
        }
    }

    private void refreshData() {
        applyFilter();
        double total = expenseDao.getTotalAmount();
        totalAmountLabel.setText(CURRENCY_FORMAT.format(total));
    }
}

