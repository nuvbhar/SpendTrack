package com.spendtrack.model;

/**
 * Represents expense categories.
 * Demonstrates Type-Safety and Encapsulation.
 */
public enum Category {
    FOOD("Food & Dining"),
    TRANSPORT("Transport"),
    HOUSING("Housing & Utilities"),
    ENTERTAINMENT("Entertainment"),
    SHOPPING("Shopping"),
    HEALTH("Health & Medical"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

