package com.spendtrack.ui;

import com.spendtrack.model.Category;
import com.spendtrack.model.Expense;

import javax.swing.table.AbstractTableModel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Custom table model binding a List of Expenses to a JTable.
 * Demonstrates Inheritance (extending AbstractTableModel) and the Adapter/MVC pattern.
 */
public class ExpenseTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"Date", "Description", "Category", "Amount"};
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    private List<Expense> expenses = new ArrayList<>();

    public void setExpenses(List<Expense> expenses) {
        this.expenses = new ArrayList<>(expenses);
        fireTableDataChanged();
    }

    public Expense getExpenseAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < expenses.size()) {
            return expenses.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return expenses.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 2 -> Category.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Expense expense = expenses.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> expense.date().toString();
            case 1 -> expense.title();
            case 2 -> expense.category();
            case 3 -> CURRENCY_FORMAT.format(expense.amount());
            default -> null;
        };
    }
}
