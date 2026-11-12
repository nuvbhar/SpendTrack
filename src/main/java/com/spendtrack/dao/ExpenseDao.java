package com.spendtrack.dao;

import com.spendtrack.model.Category;
import com.spendtrack.model.Expense;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Data Access Object interface for Expense persistence.
 * Demonstrates Abstraction and Dependency Inversion.
 */
public interface ExpenseDao {
    List<Expense> findAll();
    List<Expense> findByCategory(Category category);
    List<Expense> search(String query);
    void save(Expense expense);
    void update(Expense expense);
    void delete(long id);
    double getTotalAmount();
    int getTransactionCount();
    Category getTopCategory();
    double getMonthlyBudget();
    void setMonthlyBudget(double budget);
    void exportToCsv(Path destination) throws IOException;
}
