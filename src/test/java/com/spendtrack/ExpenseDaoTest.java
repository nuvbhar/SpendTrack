package com.spendtrack;

import com.spendtrack.dao.DatabaseManager;
import com.spendtrack.dao.ExpenseDao;
import com.spendtrack.dao.JdbcExpenseDao;
import com.spendtrack.model.Category;
import com.spendtrack.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    void testFullCrudAndFeatures() throws IOException {
        int initialCount = expenseDao.getTransactionCount();
        double initialTotal = expenseDao.getTotalAmount();

        // 1. Create & Save
        Expense testExpense = new Expense("Unit Test Biryani", 350.00, Category.FOOD, LocalDate.now());
        expenseDao.save(testExpense);

        assertEquals(initialCount + 1, expenseDao.getTransactionCount());
        assertEquals(initialTotal + 350.00, expenseDao.getTotalAmount(), 0.001);

        List<Expense> searchResults = expenseDao.search("Biryani");
        assertFalse(searchResults.isEmpty());

        Expense saved = searchResults.stream()
            .filter(e -> "Unit Test Biryani".equals(e.title()))
            .findFirst()
            .orElseThrow();

        assertEquals(350.00, saved.amount());
        assertEquals(Category.FOOD, saved.category());

        // 2. Update (Full CRUD)
        Expense updated = new Expense(saved.id(), "Unit Test Biryani Large", 450.00, Category.FOOD, saved.date());
        expenseDao.update(updated);

        Expense reFetched = expenseDao.search("Biryani Large").getFirst();
        assertEquals(450.00, reFetched.amount());
        assertEquals("Unit Test Biryani Large", reFetched.title());

        // 3. Monthly Budget persistence
        expenseDao.setMonthlyBudget(30000.0);
        assertEquals(30000.0, expenseDao.getMonthlyBudget(), 0.001);

        // 4. CSV Export
        Path tempCsv = Files.createTempFile("spendtrack_test", ".csv");
        expenseDao.exportToCsv(tempCsv);
        assertTrue(Files.exists(tempCsv));
        assertTrue(Files.size(tempCsv) > 0);
        Files.deleteIfExists(tempCsv);

        // 5. Delete
        expenseDao.delete(saved.id());
        assertEquals(initialCount, expenseDao.getTransactionCount());
        assertEquals(initialTotal, expenseDao.getTotalAmount(), 0.001);
    }
}
