# SpendTrack — Modern Java (Swing + JDBC) Expense Tracker

A sleek, Linear/Raycast-styled desktop expense tracking and financial analytics application built for Object-Oriented Programming (OOP) coursework. Built using **Java 21+**, **Java Swing** with **FlatMacDarkLaf**, and **JDBC with SQLite**.

---

## 🎨 Modern UI & UX Features

SpendTrack breaks away from dated legacy Swing layouts by implementing modern desktop design paradigms:

1. **Linear / Raycast Dark Aesthetic**:
   - Deep slate backgrounds with subtle borders (`#222436` / `#1c1c1e`).
   - Native modern dark title bars and rounded window decorations via `FlatMacDarkLaf`.
2. **Multi-Card KPI Dashboard**:
   - **Total Expenditure**: Live formatted currency balance card (`#34D399` Emerald).
   - **Transactions Count**: Real-time counter of logged expenses (`#38BDF8` Sky Blue).
   - **Top Category**: Highlights highest spending category (`#A78BFA` Violet).
3. **Pill-Shaped Category Badges**:
   - Custom `CategoryBadgeRenderer` paints colorful rounded pill tags with icons for each category (e.g. 🍔 Food, 🚗 Transport, 🏠 Housing, 🎬 Entertainment).
4. **Live Search & Filtering**:
   - Dynamic search field with clear button (`🔍 Search description...`) filtering rows instantly as you type.
   - Category dropdown filter.
5. **Modal Slide-in Dialog**:
   - Clean, focused `AddExpenseDialog` modal with rounded inputs, placeholder hints, `Enter` to submit, and `Escape` to cancel.

---

## 🎯 Course OOP Principles Mapping

Built under a **strict minimal code philosophy** (~400 lines total) while clearly demonstrating all core OOP pillars:

| OOP Pillar | Project Manifestation | Detailed Explanation |
| :--- | :--- | :--- |
| **Encapsulation** | [`Expense.java`](file:///src/main/java/com/spendtrack/model/Expense.java) & [`Category.java`](file:///src/main/java/com/spendtrack/model/Category.java) | Domain state is immutable using modern Java `record`. Business rules (non-blank title, strictly positive amount, non-null date) are strictly enforced in the compact constructor. `Category` encapsulates display names, icons, and theme colors. |
| **Abstraction** | [`ExpenseDao.java`](file:///src/main/java/com/spendtrack/dao/ExpenseDao.java) | An interface defining data access operations (`findAll`, `findByCategory`, `search`, `save`, `delete`, `getTotalAmount`, `getTransactionCount`, `getTopCategory`) without exposing SQL details. |
| **Polymorphism** | [`JdbcExpenseDao.java`](file:///src/main/java/com/spendtrack/dao/JdbcExpenseDao.java) | Implements `ExpenseDao`. UI and business logic depend on the abstraction, allowing swappable data sources. |
| **Inheritance & Reusability** | [`ExpenseTableModel.java`](file:///src/main/java/com/spendtrack/ui/ExpenseTableModel.java) & [`CategoryBadgeRenderer.java`](file:///src/main/java/com/spendtrack/ui/CategoryBadgeRenderer.java) | `ExpenseTableModel` extends `AbstractTableModel` to adapt entity data to `JTable`. `CategoryBadgeRenderer` extends `JPanel` and implements `TableCellRenderer`. |
| **Separation of Concerns (MVC)** | Multi-tier Architecture | Cleanly separated into Model (`com.spendtrack.model`), Persistence (`com.spendtrack.dao`), and Presentation (`com.spendtrack.ui`). |

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
│   │   │   ├── DatabaseManager.java         # SQLite connection & table schema
│   │   │   ├── ExpenseDao.java              # DAO abstraction interface
│   │   │   └── JdbcExpenseDao.java          # JDBC prepared-statement implementation
│   │   └── ui/
│   │       ├── AddExpenseDialog.java        # Modern modal dialog for creating expenses
│   │       ├── CategoryBadgeRenderer.java   # Custom pill-badge renderer for table categories
│   │       ├── ExpenseTableModel.java       # AbstractTableModel adapter for JTable
│   │       └── MainFrame.java               # Dashboard, KPI cards, search, and tables
│   └── test/java/com/spendtrack/
│       └── ExpenseDaoTest.java              # Unit tests for domain & JDBC operations
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
```
