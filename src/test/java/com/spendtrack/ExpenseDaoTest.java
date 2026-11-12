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
        // Assert invalid title throws
        assertThrows(IllegalArgumentException.class, () ->
            new Expense("   ", 10.0, Category.FOOD, LocalDate.now())
        );

        // Assert non-positive amount throws
        assertThrows(IllegalArgumentException.class, () ->
            new Expense("Coffee", -5.0, Category.FOOD, LocalDate.now())
        );
    }

    @Test
    void testCrudOperations() {
        int initialCount = expenseDao.findAll().size();
        double initialTotal = expenseDao.getTotalAmount();

        // 1. Create & Save
        Expense testExpense = new Expense("Unit Test Coffee", 5.50, Category.FOOD, LocalDate.now());
        expenseDao.save(testExpense);

        List<Expense> expenses = expenseDao.findAll();
        assertEquals(initialCount + 1, expenses.size());
        assertEquals(initialTotal + 5.50, expenseDao.getTotalAmount(), 0.001);

        Expense saved = expenses.stream()
            .filter(e -> "Unit Test Coffee".equals(e.title()))
            .findFirst()
            .orElseThrow();

        assertEquals(5.50, saved.amount());
        assertEquals(Category.FOOD, saved.category());

        // 2. Filter by Category
        List<Expense> foodExpenses = expenseDao.findByCategory(Category.FOOD);
        assertTrue(foodExpenses.stream().anyMatch(e -> e.id().equals(saved.id())));

        // 3. Delete
        expenseDao.delete(saved.id());
        List<Expense> afterDelete = expenseDao.findAll();
        assertEquals(initialCount, afterDelete.size());
        assertEquals(initialTotal, expenseDao.getTotalAmount(), 0.001);
    }
}

