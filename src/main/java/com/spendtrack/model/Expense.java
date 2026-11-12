package com.spendtrack.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain entity representing a single expense record.
 * Uses modern Java record for immutability and concise encapsulation.
 */
public record Expense(Long id, String title, double amount, Category category, LocalDate date) {

    public Expense {
        Objects.requireNonNull(title, "Title cannot be null");
        if (title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        Objects.requireNonNull(category, "Category cannot be null");
        Objects.requireNonNull(date, "Date cannot be null");
    }

    /**
     * Overloaded constructor for newly created expenses prior to persistence.
     */
    public Expense(String title, double amount, Category category, LocalDate date) {
        this(null, title, amount, category, date);
    }
}

