package com.spendtrack.model;

import java.awt.Color;

/**
 * Represents expense categories with embedded visual styling and iconography.
 * Demonstrates Type-Safety and Encapsulation.
 */
public enum Category {
    FOOD("Food & Dining", "🍔", new Color(245, 158, 11), new Color(245, 158, 11, 40)),
    TRANSPORT("Transport", "🚗", new Color(56, 189, 248), new Color(56, 189, 248, 40)),
    HOUSING("Housing & Bills", "🏠", new Color(129, 140, 248), new Color(129, 140, 248, 40)),
    ENTERTAINMENT("Entertainment", "🎬", new Color(244, 63, 94), new Color(244, 63, 94, 40)),
    SHOPPING("Shopping", "🛍️", new Color(52, 211, 153), new Color(52, 211, 153, 40)),
    HEALTH("Health & Medical", "💊", new Color(239, 68, 68), new Color(239, 68, 68, 40)),
    OTHER("Other", "📦", new Color(148, 163, 184), new Color(148, 163, 184, 40));

    private final String displayName;
    private final String icon;
    private final Color foregroundColor;
    private final Color backgroundColor;

    Category(String displayName, String icon, Color foregroundColor, Color backgroundColor) {
        this.displayName = displayName;
        this.icon = icon;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public String toString() {
        return icon + " " + displayName;
    }
}
