package com.spendtrack.dao;

import com.spendtrack.model.Category;
import com.spendtrack.model.Expense;

import java.util.List;

/**
 * Data Access Object interface for Expense persistence.
 * Demonstrates Abstraction and Dependency Inversion.
 */
public interface ExpenseDao {
    List<Expense> findAll();
    List<Expense> findByCategory(Category category);
    void save(Expense expense);
    void delete(long id);
    double getTotalAmount();
}
