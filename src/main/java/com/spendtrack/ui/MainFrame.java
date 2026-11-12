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
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Modern Linear/Raycast-styled desktop interface for SpendTrack.
 * Features Indian Rupee (₹) currency, dynamic budget limit monitoring,
 * live search, category analytics, CSV export, and full CRUD.
 */
public class MainFrame extends JFrame {

    private final ExpenseDao expenseDao;
    private final ExpenseTableModel tableModel = new ExpenseTableModel();
    private final JTable expenseTable = new JTable(tableModel);

    // KPI Metric Labels
    private final JLabel totalAmountLabel = new JLabel("₹ 0.00");
    private final JLabel budgetSubLabel = new JLabel("Budget: ₹ 25,000.00 (0% used)");
    private final JLabel transactionCountLabel = new JLabel("0 Entries");
    private final JLabel topCategoryLabel = new JLabel("None");

    // Controls
    private final JTextField searchField = new JTextField(16);
    private final JComboBox<String> filterComboBox = new JComboBox<>();

    public MainFrame(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;

        setTitle("SpendTrack — Personal Finance & Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 700);
        setMinimumSize(new Dimension(860, 560));
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
        JLabel subtitle = new JLabel("Personal Financial Tracker & Outflow Analytics");
        subtitle.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground;");
        brandGroup.add(title);
        brandGroup.add(subtitle);
        headerBar.add(brandGroup, BorderLayout.WEST);

        // Right Actions: Analytics & + New Expense Button
        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton analyticsBtn = new JButton("📊 Analytics");
        analyticsBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; margin: 8,14,8,14;");
        analyticsBtn.addActionListener(e -> openAnalyticsDialog());
        actionGroup.add(analyticsBtn);

        JButton newExpenseBtn = new JButton("+ New Expense");
        newExpenseBtn.putClientProperty(FlatClientProperties.STYLE,
            "arc: 12; background: #6366F1; foreground: #ffffff; font: bold 13; margin: 8,18,8,18;");
        newExpenseBtn.addActionListener(e -> openAddExpenseDialog());
        actionGroup.add(newExpenseBtn);

        headerBar.add(actionGroup, BorderLayout.EAST);

        return headerBar;
    }

    private JPanel createKpiDashboard() {
        JPanel dashboardGrid = new JPanel(new GridLayout(1, 3, 16, 0));

        totalAmountLabel.putClientProperty(FlatClientProperties.STYLE, "font: 175% bold; foreground: #34D399;");
        transactionCountLabel.putClientProperty(FlatClientProperties.STYLE, "font: 175% bold; foreground: #38BDF8;");
        topCategoryLabel.putClientProperty(FlatClientProperties.STYLE, "font: 155% bold; foreground: #A78BFA;");

        // Card 1 with Budget Click to Edit
        JPanel budgetCard = createKpiCard("TOTAL EXPENDITURE", totalAmountLabel, budgetSubLabel);
        budgetCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        budgetCard.setToolTipText("Click to set monthly budget limit");
        budgetCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                promptSetBudget();
            }
        });

        dashboardGrid.add(budgetCard);
        dashboardGrid.add(createKpiCard("TRANSACTIONS", transactionCountLabel, new JLabel("Total logged payments")));
        dashboardGrid.add(createKpiCard("TOP CATEGORY", topCategoryLabel, new JLabel("Highest outflow category")));

        return dashboardGrid;
    }

    private JPanel createKpiCard(String labelText, JLabel valueLabel, JLabel subLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true),
            new EmptyBorder(14, 18, 14, 18)
        ));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 14;");

        JLabel label = new JLabel(labelText);
        label.putClientProperty(FlatClientProperties.STYLE, "font: 75% bold; foreground: $Label.disabledForeground;");

        subLabel.putClientProperty(FlatClientProperties.STYLE, "font: 80%;");

        card.add(label, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subLabel, BorderLayout.SOUTH);

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

        // Right Actions: Export CSV, Edit Selected, Delete Selected
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        JButton exportBtn = new JButton("📥 Export CSV");
        exportBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        exportBtn.addActionListener(e -> exportCsv());
        rightActions.add(exportBtn);

        JButton editBtn = new JButton("✏️ Edit");
        editBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        editBtn.addActionListener(e -> editSelectedExpense());
        rightActions.add(editBtn);

        JButton deleteBtn = new JButton("🗑️ Delete");
        deleteBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; foreground: #F87171;");
        deleteBtn.addActionListener(e -> deleteSelectedExpense());
        rightActions.add(deleteBtn);

        toolbar.add(rightActions, BorderLayout.EAST);

        return toolbar;
    }

    private JComponent createTableSection() {
        expenseTable.setRowHeight(38);
        expenseTable.setShowGrid(false);
        expenseTable.setIntercellSpacing(new Dimension(0, 0));
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Frosted White Row Selection Highlighting
        expenseTable.setSelectionBackground(new Color(255, 255, 255, 35));
        expenseTable.setSelectionForeground(Color.WHITE);

        // Double-click to Edit row
        expenseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedExpense();
                }
            }
        });

        // Configure Explicit Column Widths
        TableColumnModel colModel = expenseTable.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(110);
        colModel.getColumn(0).setMaxWidth(130);
        colModel.getColumn(1).setPreferredWidth(350);
        colModel.getColumn(2).setPreferredWidth(190);
        colModel.getColumn(3).setPreferredWidth(140);
        colModel.getColumn(3).setMaxWidth(160);

        // Date Column: Centered with White Selection Highlight
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    c.setForeground(Color.WHITE);
                    c.setBackground(table.getSelectionBackground());
                } else {
                    c.setForeground(UIManager.getColor("Label.disabledForeground"));
                }
                return c;
            }
        };
        colModel.getColumn(0).setCellRenderer(centerRenderer);

        // Description Column: Bold font with White Selection Highlight
        DefaultTableCellRenderer descRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                setBorder(new EmptyBorder(0, 12, 0, 0));
                if (isSelected) {
                    c.setForeground(Color.WHITE);
                    c.setBackground(table.getSelectionBackground());
                } else {
                    c.setForeground(UIManager.getColor("Label.foreground"));
                }
                return c;
            }
        };
        colModel.getColumn(1).setCellRenderer(descRenderer);

        // Category Column: Custom Pill Badge with Continuous Row Highlight
        colModel.getColumn(2).setCellRenderer(new CategoryBadgeRenderer());

        // Amount Column: Right-aligned bold currency with White Selection Highlight
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(new EmptyBorder(0, 0, 0, 16));
                c.setFont(c.getFont().deriveFont(Font.BOLD, 13f));
                if (isSelected) {
                    c.setForeground(Color.WHITE);
                    c.setBackground(table.getSelectionBackground());
                } else {
                    c.setForeground(new Color(248, 113, 113));
                }
                return c;
            }
        };
        colModel.getColumn(3).setCellRenderer(amountRenderer);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 25), 1, true));
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        return scrollPane;
    }

    private void openAddExpenseDialog() {
        AddExpenseDialog dialog = new AddExpenseDialog(this, expenseDao, this::refreshData);
        dialog.setVisible(true);
    }

    private void editSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense row to edit.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Expense expense = tableModel.getExpenseAt(selectedRow);
        if (expense != null) {
            AddExpenseDialog dialog = new AddExpenseDialog(this, expenseDao, expense, this::refreshData);
            dialog.setVisible(true);
        }
    }

    private void openAnalyticsDialog() {
        AnalyticsDialog dialog = new AnalyticsDialog(this, expenseDao.findAll());
        dialog.setVisible(true);
    }

    private void promptSetBudget() {
        double currentBudget = expenseDao.getMonthlyBudget();
        String input = JOptionPane.showInputDialog(
            this,
            "Enter your target monthly budget in ₹:",
            String.valueOf((int) currentBudget)
        );

        if (input != null && !input.isBlank()) {
            try {
                double newBudget = Double.parseDouble(input.trim());
                if (newBudget <= 0) throw new IllegalArgumentException("Budget must be positive");
                expenseDao.setMonthlyBudget(newBudget);
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive amount.", "Invalid Budget", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("SpendTrack_Expenses.csv"));
        int userChoice = fileChooser.showSaveDialog(this);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File targetFile = fileChooser.getSelectedFile();
            try {
                expenseDao.exportToCsv(targetFile.toPath());
                JOptionPane.showMessageDialog(
                    this,
                    "Successfully exported " + tableModel.getRowCount() + " expenses to:\n" + targetFile.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to export CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
                "Delete '" + expense.title() + "' (" + ExpenseTableModel.formatRupees(expense.amount()) + ")?",
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
        double budget = expenseDao.getMonthlyBudget();

        totalAmountLabel.setText(ExpenseTableModel.formatRupees(total));
        transactionCountLabel.setText(count + (count == 1 ? " Entry" : " Entries"));
        topCategoryLabel.setText(topCat != null ? topCat.getIcon() + " " + topCat.getDisplayName() : "None yet");

        // Budget Threshold Alert calculation
        double percentUsed = budget > 0 ? (total / budget) * 100 : 0.0;
        if (percentUsed >= 100.0) {
            budgetSubLabel.setText(String.format("⚠️ Budget: %s (%.0f%% - Exceeded!)", ExpenseTableModel.formatRupees(budget), percentUsed));
            budgetSubLabel.setForeground(new Color(239, 68, 68));
        } else if (percentUsed >= 75.0) {
            budgetSubLabel.setText(String.format("Budget: %s (%.0f%% used)", ExpenseTableModel.formatRupees(budget), percentUsed));
            budgetSubLabel.setForeground(new Color(245, 158, 11));
        } else {
            budgetSubLabel.setText(String.format("Budget: %s (%.0f%% used)", ExpenseTableModel.formatRupees(budget), percentUsed));
            budgetSubLabel.setForeground(new Color(52, 211, 153));
        }
    }
}
