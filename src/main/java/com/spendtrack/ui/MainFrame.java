package com.spendtrack.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.spendtrack.dao.ExpenseDao;
import com.spendtrack.model.Category;
import com.spendtrack.model.Expense;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Modern Linear/Raycast-styled desktop interface for SpendTrack.
 * Features a multi-card KPI dashboard, live search, category badges, and modal dialogs.
 */
public class MainFrame extends JFrame {
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    private final ExpenseDao expenseDao;
    private final ExpenseTableModel tableModel = new ExpenseTableModel();
    private final JTable expenseTable = new JTable(tableModel);

    // KPI Metric Labels
    private final JLabel totalAmountLabel = new JLabel("$0.00");
    private final JLabel transactionCountLabel = new JLabel("0 Entries");
    private final JLabel topCategoryLabel = new JLabel("None");

    // Controls
    private final JTextField searchField = new JTextField(16);
    private final JComboBox<String> filterComboBox = new JComboBox<>();

    public MainFrame(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;

        setTitle("SpendTrack — Modern Financial Analytics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 680);
        setMinimumSize(new Dimension(800, 520));
        setLocationRelativeTo(null);

        initUi();
        refreshData();
    }

    private void initUi() {
        JPanel contentPane = new JPanel(new BorderLayout(0, 18));
        contentPane.setBorder(new EmptyBorder(22, 26, 22, 26));
        setContentPane(contentPane);

        // 1. Top Section: Header & Multi-Card KPI Dashboard
        JPanel northWrapper = new JPanel(new BorderLayout(0, 16));
        northWrapper.add(createHeaderBar(), BorderLayout.NORTH);
        northWrapper.add(createKpiDashboard(), BorderLayout.CENTER);
        contentPane.add(northWrapper, BorderLayout.NORTH);

        // 2. Center Section: Toolbar + Table
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 12));
        centerWrapper.add(createToolbar(), BorderLayout.NORTH);
        centerWrapper.add(createTableSection(), BorderLayout.CENTER);
        contentPane.add(centerWrapper, BorderLayout.CENTER);
    }

    private JPanel createHeaderBar() {
        JPanel headerBar = new JPanel(new BorderLayout());

        // App Branding
        JPanel brandGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        JLabel title = new JLabel("⚡ SpendTrack");
        title.putClientProperty(FlatClientProperties.STYLE, "font: 165% bold;");
        JLabel subtitle = new JLabel("Personal Finance & Real-time Expense Tracking");
        subtitle.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground;");
        brandGroup.add(title);
        brandGroup.add(subtitle);
        headerBar.add(brandGroup, BorderLayout.WEST);

        // Primary Call to Action Button: + New Expense
        JButton newExpenseBtn = new JButton("+ New Expense");
        newExpenseBtn.putClientProperty(FlatClientProperties.STYLE,
            "arc: 12; background: #6366F1; foreground: #ffffff; font: bold 13; margin: 8,18,8,18;");
        newExpenseBtn.addActionListener(e -> openAddExpenseDialog());
        headerBar.add(newExpenseBtn, BorderLayout.EAST);

        return headerBar;
    }

    private JPanel createKpiDashboard() {
        JPanel dashboardGrid = new JPanel(new GridLayout(1, 3, 16, 0));

        totalAmountLabel.putClientProperty(FlatClientProperties.STYLE, "font: 175% bold; foreground: #34D399;");
        transactionCountLabel.putClientProperty(FlatClientProperties.STYLE, "font: 175% bold; foreground: #38BDF8;");
        topCategoryLabel.putClientProperty(FlatClientProperties.STYLE, "font: 155% bold; foreground: #A78BFA;");

        dashboardGrid.add(createKpiCard("TOTAL EXPENDITURE", totalAmountLabel, "Cumulative all-time spend"));
        dashboardGrid.add(createKpiCard("TRANSACTIONS", transactionCountLabel, "Total logged payments"));
        dashboardGrid.add(createKpiCard("TOP CATEGORY", topCategoryLabel, "Highest outflow category"));

        return dashboardGrid;
    }

    private JPanel createKpiCard(String labelText, JLabel valueLabel, String subtext) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true),
            new EmptyBorder(14, 18, 14, 18)
        ));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 14;");

        JLabel label = new JLabel(labelText);
        label.putClientProperty(FlatClientProperties.STYLE, "font: 75% bold; foreground: $Label.disabledForeground;");

        JLabel sub = new JLabel(subtext);
        sub.putClientProperty(FlatClientProperties.STYLE, "font: 80%; foreground: $Label.disabledForeground;");

        card.add(label, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(sub, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));

        // Left Filters: Search field + Category Dropdown
        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🔍 Search description...");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 4,10,4,10;");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });
        leftGroup.add(searchField);

        filterComboBox.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        filterComboBox.addItem("All Categories");
        for (Category cat : Category.values()) {
            filterComboBox.addItem(cat.getIcon() + " " + cat.getDisplayName());
        }
        filterComboBox.addActionListener(e -> applyFilter());
        leftGroup.add(filterComboBox);

        toolbar.add(leftGroup, BorderLayout.WEST);

        // Right Actions: Delete Selected
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; foreground: #F87171;");
        deleteBtn.addActionListener(e -> deleteSelectedExpense());
        toolbar.add(deleteBtn, BorderLayout.EAST);

        return toolbar;
    }

    private JComponent createTableSection() {
        expenseTable.setRowHeight(38);
        expenseTable.setShowGrid(false);
        expenseTable.setIntercellSpacing(new Dimension(0, 0));
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Date Column: Centered & subtle
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        expenseTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        // Description Column: Bold left-aligned
        DefaultTableCellRenderer descRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                setBorder(new EmptyBorder(0, 12, 0, 0));
                return c;
            }
        };
        expenseTable.getColumnModel().getColumn(1).setCellRenderer(descRenderer);

        // Category Column: Custom Pill Badge
        expenseTable.getColumnModel().getColumn(2).setCellRenderer(new CategoryBadgeRenderer());

        // Amount Column: Right-aligned bold red/rose
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(new EmptyBorder(0, 0, 0, 16));
                c.setFont(c.getFont().deriveFont(Font.BOLD, 13f));
                c.setForeground(new Color(248, 113, 113));
                return c;
            }
        };
        expenseTable.getColumnModel().getColumn(3).setCellRenderer(amountRenderer);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 25), 1, true));
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        return scrollPane;
    }

    private void openAddExpenseDialog() {
        AddExpenseDialog dialog = new AddExpenseDialog(this, expenseDao, this::refreshData);
        dialog.setVisible(true);
    }

    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense row to delete.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Expense expense = tableModel.getExpenseAt(selectedRow);
        if (expense != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete '" + expense.title() + "' (" + CURRENCY_FORMAT.format(expense.amount()) + ")?",
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
        String query = searchField.getText().trim();
        int categoryIndex = filterComboBox.getSelectedIndex();

        List<Expense> results;
        if (!query.isBlank()) {
            results = expenseDao.search(query);
            if (categoryIndex > 0) {
                Category selectedCategory = Category.values()[categoryIndex - 1];
                results = results.stream().filter(e -> e.category() == selectedCategory).toList();
            }
        } else if (categoryIndex > 0) {
            Category selectedCategory = Category.values()[categoryIndex - 1];
            results = expenseDao.findByCategory(selectedCategory);
        } else {
            results = expenseDao.findAll();
        }

        tableModel.setExpenses(results);
    }

    private void refreshData() {
        applyFilter();

        // Update KPIs
        double total = expenseDao.getTotalAmount();
        int count = expenseDao.getTransactionCount();
        Category topCat = expenseDao.getTopCategory();

        totalAmountLabel.setText(CURRENCY_FORMAT.format(total));
        transactionCountLabel.setText(count + (count == 1 ? " Entry" : " Entries"));
        topCategoryLabel.setText(topCat != null ? topCat.getIcon() + " " + topCat.getDisplayName() : "None yet");
    }
}
