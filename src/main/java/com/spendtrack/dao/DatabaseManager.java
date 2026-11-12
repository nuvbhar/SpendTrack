package com.spendtrack.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connectivity and schema initialization.
 * Follows Single Responsibility Principle (SRP).
 */
public final class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:spendtrack.db";

    private DatabaseManager() {
        // Prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initDatabase() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS expenses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                amount REAL NOT NULL CHECK(amount > 0),
                category TEXT NOT NULL,
                date TEXT NOT NULL
            );
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}

