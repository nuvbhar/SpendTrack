# SpendTrack — Modern Java (Swing + JDBC) Expense Tracker

A lightweight, modern desktop expense tracker designed for Object-Oriented Programming (OOP) coursework. Built using **Java 21+**, **Java Swing** with **FlatLaf**, and **JDBC with SQLite**.

---

## 🎯 Course OOP Principles Mapping

This project was built under a **strict minimal code philosophy** (~350 lines total) while demonstrating all core tenets of Object-Oriented Programming:

| OOP Pillar | Project Manifestation | Detailed Explanation |
| :--- | :--- | :--- |
| **Encapsulation** | [`Expense.java`](file:///src/main/java/com/spendtrack/model/Expense.java) & [`Category.java`](file:///src/main/java/com/spendtrack/model/Category.java) | Domain state is immutable using modern Java `record`. Business rules (non-blank title, strictly positive amount, non-null date) are strictly enforced in the compact constructor. |
| **Abstraction** | [`ExpenseDao.java`](file:///src/main/java/com/spendtrack/dao/ExpenseDao.java) | An interface defining data access operations (`findAll`, `findByCategory`, `save`, `delete`, `getTotalAmount`) without exposing any database or SQL details to the UI layer. |
| **Polymorphism** | [`JdbcExpenseDao.java`](file:///src/main/java/com/spendtrack/dao/JdbcExpenseDao.java) | Implements `ExpenseDao`. The UI components depend on the `ExpenseDao` abstraction, enabling seamless substitution with in-memory or alternative database implementations. |
| **Inheritance & Reusability** | [`ExpenseTableModel.java`](file:///src/main/java/com/spendtrack/ui/ExpenseTableModel.java) & [`MainFrame.java`](file:///src/main/java/com/spendtrack/ui/MainFrame.java) | `ExpenseTableModel` extends Swing's `AbstractTableModel` to adapt entity data to `JTable`. `MainFrame` extends `JFrame` to encapsulate window lifecycle. |
| **Separation of Concerns (MVC)** | Multi-tier Architecture | Cleanly separated into Model (`com.spendtrack.model`), Persistence (`com.spendtrack.dao`), and Presentation (`com.spendtrack.ui`). |

---

## 🎨 UI/UX Design Principles

- **FlatLaf Look & Feel**: Modern, flat UI with system font scaling and native window styling.
- **Visual Hierarchy**: Prominent **Total Expenditure** KPI card at the top right with auto-updating metrics.
- **High Usability Grid**: Clean table formatting with centered dates, badge-style category names, and right-aligned currency.
- **Frictionless Quick Add**: Bottom bar form with placeholder hints, auto-defaulted date, and `Enter`-key submission.
- **Defensive Deletion**: Confirmation dialog protects against accidental data loss.

---

## 📁 Project Architecture

```
SpendTrack/
├── pom.xml                                  # Maven build configuration
├── mvnw / mvnw.cmd                          # Maven Wrapper (zero install needed)
├── .mvn/wrapper/                            # Wrapper binaries
├── src/
│   ├── main/java/com/spendtrack/
│   │   ├── Main.java                        # Bootstrap & FlatLaf initialization
│   │   ├── model/
│   │   │   ├── Category.java                # Type-safe Category enum
│   │   │   └── Expense.java                 # Immutable Java Record with validation
│   │   ├── dao/
│   │   │   ├── DatabaseManager.java         # SQLite connection & table schema
│   │   │   ├── ExpenseDao.java              # DAO abstraction interface
│   │   │   └── JdbcExpenseDao.java          # JDBC prepared-statement implementation
│   │   └── ui/
│   │       ├── ExpenseTableModel.java       # AbstractTableModel adapter for JTable
│   │       └── MainFrame.java               # Dashboard, tables, and quick-add form
│   └── test/java/com/spendtrack/
│       └── ExpenseDaoTest.java              # Unit tests for domain & JDBC operations
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK 21 or higher)**
- No local database server or external Maven installation is required.

### 1. Run the Application
Using the included Maven Wrapper:

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
The packaged JAR will be generated in `target/spendtrack-1.0.0.jar`.

---

## 🗄️ Database Details

The application uses an embedded **SQLite** database (`spendtrack.db`) created automatically on first run in the root project folder:

```sql
CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    amount REAL NOT NULL CHECK(amount > 0),
    category TEXT NOT NULL,
    date TEXT NOT NULL
);
```
