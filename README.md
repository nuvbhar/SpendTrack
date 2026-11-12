# SpendTrack — Modern Java (Swing + JDBC) Expense Tracker

A sleek, Linear/Raycast-styled desktop expense tracking and financial analytics application built for Object-Oriented Programming (OOP) coursework. Built using **Java 21+**, **Java Swing** with **FlatMacDarkLaf**, and **JDBC with SQLite**. Fully localized for **Indian Rupees (₹)**.

---

## 🎨 Modern UI & UX Features

SpendTrack implements modern desktop design paradigms:

1. **Indian Rupee (₹) Currency Localization**:
   - Standard Indian numbering formatting (`₹ 1,23,456.78`).
2. **Linear / Raycast Dark Aesthetic**:
   - Deep slate backgrounds with subtle borders (`#222436` / `#1c1c1e`).
   - Native modern dark title bars and rounded window decorations via `FlatMacDarkLaf`.
3. **Multi-Card KPI Dashboard & Budget Warning Alert**:
   - **Total Expenditure & Budget Tracker**: Live balance in emerald (`#34D399`) with remaining monthly budget calculation and color-coded threshold alerts (🟢 Safe < 75%, 🟡 Caution 75-99%, 🔴 ⚠️ Alert >= 100% Exceeded!). Click card to edit target monthly budget.
   - **Transactions Count**: Counter of logged records in sky blue (`#38BDF8`).
   - **Top Category**: Highlights highest spending category in soft violet (`#A78BFA`).
4. **Category Breakdown & Analytics**:
   - Click `📊 Analytics` to open a breakdown modal showing spending percentages (%) and visual progress bars per category computed via Java Streams.
5. **1-Click CSV Export**:
   - Click `📥 Export CSV` to export all records to a clean spreadsheet file via Java NIO `Files` I/O.
6. **Full ACID CRUD (Create, Read, Update, Delete)**:
   - Double-click any table row or click `✏️ Edit` to update existing expenses.
   - Click `+ New Expense` for modal creation.
   - Click `🗑️ Delete` with confirmation safeguard.
7. **Pill-Shaped Category Badges & Live Search**:
   - Custom `CategoryBadgeRenderer` paints colorful rounded pill tags with icons for each category (e.g. 🍔 Food, 🚗 Transport, 🏠 Housing, 🎬 Entertainment).
   - Real-time `🔍 Search description...` filter as you type.

---

## 🎯 Course OOP Principles Mapping

Built under a **strict minimal code philosophy** (~450 lines total) while clearly demonstrating all core OOP pillars:

| OOP Pillar | Project Manifestation | Detailed Explanation |
| :--- | :--- | :--- |
| **Encapsulation** | [`Expense.java`](file:///src/main/java/com/spendtrack/model/Expense.java) & [`Category.java`](file:///src/main/java/com/spendtrack/model/Category.java) | Domain state is immutable using modern Java `record`. Business rules (non-blank title, strictly positive amount, non-null date) are strictly enforced in the compact constructor. `Category` encapsulates display names, icons, and theme colors. |
| **Abstraction** | [`ExpenseDao.java`](file:///src/main/java/com/spendtrack/dao/ExpenseDao.java) | An interface defining data access operations (`findAll`, `findByCategory`, `search`, `save`, `update`, `delete`, `getTotalAmount`, `getMonthlyBudget`, `exportToCsv`) without exposing SQL details. |
| **Polymorphism** | [`JdbcExpenseDao.java`](file:///src/main/java/com/spendtrack/dao/JdbcExpenseDao.java) | Implements `ExpenseDao`. UI and business logic depend on the abstraction, allowing swappable data sources. |
| **Inheritance & Reusability** | [`ExpenseTableModel.java`](file:///src/main/java/com/spendtrack/ui/ExpenseTableModel.java) & [`CategoryBadgeRenderer.java`](file:///src/main/java/com/spendtrack/ui/CategoryBadgeRenderer.java) | `ExpenseTableModel` extends `AbstractTableModel` to adapt entity data to `JTable`. `CategoryBadgeRenderer` extends `DefaultTableCellRenderer` to customize cell drawing. |
| **Separation of Concerns (MVC)** | Multi-tier Architecture | Cleanly separated into Model (`com.spendtrack.model`), Persistence (`com.spendtrack.dao`), and Presentation (`com.spendtrack.ui`). |
| **Modern Java Idioms** | Streams API & NIO Files | Analytics aggregation via `Collectors.groupingBy` and `summingDouble`. File export via Java NIO `Files.write`. |

---

## 📁 Project Architecture

```
SpendTrack/
├── pom.xml                                  # Maven build configuration
├── mvnw / mvnw.cmd                          # Maven Wrapper (zero install needed)
├── .mvn/wrapper/                            # Wrapper binaries
├── src/
│   ├── main/java/com/spendtrack/
│   │   ├── Main.java                        # Bootstrap & FlatMacDarkLaf initialization
│   │   ├── model/
│   │   │   ├── Category.java                # Type-safe Category enum with colors & icons
│   │   │   └── Expense.java                 # Immutable Java Record with validation
│   │   ├── dao/
│   │   │   ├── DatabaseManager.java         # SQLite connection & schema (expenses + settings)
│   │   │   ├── ExpenseDao.java              # DAO abstraction interface
│   │   │   └── JdbcExpenseDao.java          # JDBC prepared-statement implementation
│   │   └── ui/
│   │       ├── AddExpenseDialog.java        # Modal dialog for creating and editing expenses
│   │       ├── AnalyticsDialog.java         # Category breakdown modal powered by Java Streams
│   │       ├── CategoryBadgeRenderer.java   # Custom pill-badge renderer for table categories
│   │       ├── ExpenseTableModel.java       # AbstractTableModel adapter for JTable
│   │       └── MainFrame.java               # Dashboard, KPI cards, search, and tables
│   └── test/java/com/spendtrack/
│       └── ExpenseDaoTest.java              # Unit tests for domain, CRUD, budget, & export
└── README.md
```

---

## 🚀 Getting Started

### 1. Run the Application
```powershell
# Windows (PowerShell / Command Prompt)
.\mvnw.cmd compile exec:java

# Linux / macOS
./mvnw compile exec:java
```

### 2. Run Automated Tests
```powershell
.\mvnw.cmd test
```

### 3. Build Executable JAR
```powershell
.\mvnw.cmd clean package
```
Generates `target/spendtrack-1.0.0.jar`.

---

## 🗄️ Database Schema

Automatically initialized in `spendtrack.db`:

```sql
CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    amount REAL NOT NULL CHECK(amount > 0),
    category TEXT NOT NULL,
    date TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS settings (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL
);
```
