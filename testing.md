# 📋 testing.md – Smart Study‑Session Planner

This document outlines the test strategy, techniques, and coverage for the Smart Study‑Session Planner developed for ENSE 375. The application is tested following **Test-Driven Development (TDD)** using **JUnit 5**, ensuring reliability of logic, UI, and data persistence mechanisms.

---

## ✅ Test Strategy

We applied the following testing techniques:
- **Unit Testing**: For core logic and model classes (`Task`, `TaskDB`)
- **Integration Testing**: For database operations and UI workflow
- **Data Flow Testing**: Ensuring task attributes flow correctly through storage and retrieval
- **Validation Testing**: Using black-box testing techniques
- **Automated Testing**: Executed via Maven and JUnit

---

## 🔍 Unit Tests

### Task.java
- Tests include all getter and setter methods
- Verifies data encapsulation and time-related attributes
- Ensures valid construction and manipulation of task attributes

### TaskDB.java
- Focused on interaction with SQLite
- Methods tested:
  - `addTask()`
  - `getTasksForDate()`
  - `updateTaskInDB()`
  - `deleteTaskById()`

---

## 🔗 Integration Tests

### TaskDBIntegrationTest.java
- Tests full data persistence pipeline from app to SQLite DB
- Asserts correctness of insert, update, retrieve, and delete
- Checks real-world user scenarios (task creation → DB → retrieval)

---

## 🔄 Data Flow Testing

### TaskDBDataFlowTest.java

Ensures that **all fields from the `Task` object** persist and recover correctly through DB transactions.

#### ✔️ Test Cases:

1. **Round-Trip Data Test**
   - Creates a task with all fields
   - Inserts to DB → Retrieves by date → Compares field-by-field
   - Confirms consistency of title, date, completion status, priority, complexity, notes, timer flags, and durations

2. **Multiple Tasks Propagation**
   - Adds multiple tasks for same date
   - Ensures each record retains distinct field values
   - Checks correct separation and retrievability of each object

3. **Edge Case Propagation**
   - Minimal values: empty notes, complexity = 0, duration = 1 sec
   - Ensures no corruption in lower-bound values
   - Verifies that optional fields and minimal durations are handled

> 🔁 **Reminder**: For accurate results, delete the existing `tasks.db` before running these tests:
```bash
del tasks.db      # Windows
rm tasks.db       # Mac/Linux
```

---

## 🧪 Validation Testing Techniques

| Technique                | Description                                | Example |
|--------------------------|--------------------------------------------|---------|
| **Boundary Value**       | Tests limits of priority/complexity ranges | Complexity: 0 (min), 10 (max) |
| **Equivalence Class**    | Groups valid/invalid values for attributes | Title: valid (non-empty), invalid (null) |
| **Decision Table**       | Covers rules for marking tasks as complete | If `MarkComplete` clicked → move to completed table |
| **State Transition**     | Tests status changes and resulting actions | Task: `incomplete → complete` triggers update |
| **Use Case**             | Validates user workflows from UI           | Add task → Mark done → Verify in completed table |

---

## 🧪 Sample Use Case Test

**Scenario**: User adds a task and marks it complete  
**Steps**:
1. Add task titled "Final Lab"
2. Click to mark it complete
3. Verify it's removed from ongoing list
4. Confirm it's shown in completed task list

**Expected Result**: Task appears only in the correct final state table

---

## 🛠️ Tools Used

- Java 17
- Maven 3.8+
- JavaFX 17
- JUnit 5
- SQLite JDBC Driver

---

## 📦 Running Tests

```bash
mvn clean test
```

Make sure the application is not running simultaneously, and clear the DB if testing UI-dependent logic.

---

## ✅ Test Coverage Summary

| Category          | Modules             | Notes                             |
|------------------|---------------------|-----------------------------------|
| Unit Tests        | Task, TaskDB        | Field-level correctness           |
| Integration Tests | DB + UI logic       | Add/Update/Delete flow            |
| Data Flow Tests   | Full task roundtrip | Verifies object-field persistence |
| Validation Tests  | All task scenarios  | Black-box techniques applied      |

---

> 🧠 Following TDD helped us preempt bugs by writing failing tests before implementing the corresponding features.
