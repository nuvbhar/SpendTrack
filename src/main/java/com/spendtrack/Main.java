package com.spendtrack;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.spendtrack.dao.DatabaseManager;
import com.spendtrack.dao.ExpenseDao;
import com.spendtrack.dao.JdbcExpenseDao;
import com.spendtrack.ui.MainFrame;

import javax.swing.*;

/**
 * Main application bootstrap.
 * Configures the FlatMacDarkLaf modern theme, prepares the database, and schedules GUI launch on EDT.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Enable modern window decorations and FlatMacDark theme (Linear/Raycast aesthetic)
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        FlatMacDarkLaf.setup();

        // 2. Initialize the database schema
        DatabaseManager.initDatabase();

        // 3. Launch UI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            ExpenseDao expenseDao = new JdbcExpenseDao();
            MainFrame mainFrame = new MainFrame(expenseDao);
            mainFrame.setVisible(true);
        });
    }
}
