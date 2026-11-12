package com.spendtrack.dao;

import com.spendtrack.model.Category;
import com.spendtrack.model.Expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-based implementation of ExpenseDao for SQLite.
 * Demonstrates Polymorphism and Interface Realization.
 */
public class JdbcExpenseDao implements ExpenseDao {

    @Override
    public List<Expense> findAll() {
        String sql = "SELECT id, title, amount, category, date FROM expenses ORDER BY date DESC, id DESC";
        return queryExpenses(sql);
    }

    @Override
    public List<Expense> findByCategory(Category category) {
        String sql = "SELECT id, title, amount, category, date FROM expenses WHERE category = ? ORDER BY date DESC, id DESC";
        return queryExpenses(sql, category.name());
    }

    @Override
    public List<Expense> search(String query) {
        String sql = "SELECT id, title, amount, category, date FROM expenses WHERE LOWER(title) LIKE ? ORDER BY date DESC, id DESC";
        return queryExpenses(sql, "%" + query.toLowerCase().trim() + "%");
    }

    @Override
    public void save(Expense expense) {
        String sql = "INSERT INTO expenses (title, amount, category, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, expense.title());
            stmt.setDouble(2, expense.amount());
            stmt.setString(3, expense.category().name());
            stmt.setString(4, expense.date().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving expense: " + expense.title(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting expense with id: " + id, e);
        }
    }

    @Override
    public double getTotalAmount() {
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM expenses";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating total expenses", e);
        }
    }

    @Override
    public int getTransactionCount() {
        String sql = "SELECT COUNT(*) FROM expenses";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting transactions", e);
        }
    }

    @Override
    public Category getTopCategory() {
        String sql = "SELECT category FROM expenses GROUP BY category ORDER BY SUM(amount) DESC LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Category.valueOf(rs.getString("category"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating top category", e);
        }
    }

    private List<Expense> queryExpenses(String sql, String... params) {
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(new Expense(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getDouble("amount"),
                        Category.valueOf(rs.getString("category")),
                        LocalDate.parse(rs.getString("date"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying expenses", e);
        }
        return expenses;
    }
}
