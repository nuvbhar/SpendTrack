package com.spendtrack;

import com.spendtrack.dao.DatabaseManager;
import com.spendtrack.dao.ExpenseDao;
import com.spendtrack.dao.JdbcExpenseDao;
import com.spendtrack.model.Category;
import com.spendtrack.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseDaoTest {
    private ExpenseDao expenseDao;

    @BeforeEach
    void setUp() {
        DatabaseManager.initDatabase();
        expenseDao = new JdbcExpenseDao();
    }

    @Test
    void testExpenseModelValidation() {
        assertThrows(IllegalArgumentException.class, () ->
            new Expense("   ", 10.0, Category.FOOD, LocalDate.now())
        );

        assertThrows(IllegalArgumentException.class, () ->
            new Expense("Coffee", -5.0, Category.FOOD, LocalDate.now())
        );
    }

    @Test
    void testCrudOperations() {
        int initialCount = expenseDao.getTransactionCount();
        double initialTotal = expenseDao.getTotalAmount();

        // 1. Create & Save
        Expense testExpense = new Expense("Unit Test Coffee", 5.50, Category.FOOD, LocalDate.now());
        expenseDao.save(testExpense);

        assertEquals(initialCount + 1, expenseDao.getTransactionCount());
        assertEquals(initialTotal + 5.50, expenseDao.getTotalAmount(), 0.001);

        List<Expense> searchResults = expenseDao.search("Coffee");
        assertFalse(searchResults.isEmpty());

        Expense saved = searchResults.stream()
            .filter(e -> "Unit Test Coffee".equals(e.title()))
            .findFirst()
            .orElseThrow();

        assertEquals(5.50, saved.amount());
        assertEquals(Category.FOOD, saved.category());

        // 2. Filter by Category
        List<Expense> foodExpenses = expenseDao.findByCategory(Category.FOOD);
        assertTrue(foodExpenses.stream().anyMatch(e -> e.id().equals(saved.id())));

        // 3. Top Category calculation
        Category topCat = expenseDao.getTopCategory();
        assertNotNull(topCat);

        // 4. Delete
        expenseDao.delete(saved.id());
        assertEquals(initialCount, expenseDao.getTransactionCount());
        assertEquals(initialTotal, expenseDao.getTotalAmount(), 0.001);
    }
}
