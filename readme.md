# Smart Study‑Session Planner

A lightweight JavaFX-based desktop application that helps students organize their academic workload by generating a personalized, optimized weekly study calendar. It integrates task scheduling, categorization, and deadline tracking through a user-friendly interface.

## 🛠️ Project Overview

This application allows students to:
- Add and manage tasks with deadlines, priorities, and complexity levels.
- View ongoing and completed tasks in separate tables.
- Store task data persistently using an embedded SQLite database.
- Automatically display study sessions based on upcoming deadlines.

This project is developed as part of ENSE 375 at the University of Regina, following Test-Driven Development (TDD) principles using JUnit.

## 📌 Current Status

- ✅ Problem definition submitted
- ✅ Design constraints and requirements documented
- ✅ Architecture and iterative solutions implemented
- ✅ Integration and validation testing complete
- 🚧 Final polish and documentation in progress

## 👥 Team

- **Linton Dsouza** – 200470698  
- **Maheen Siddique** – 200480228  
- **Cirus Chakma** – 200495194  

## 📂 Folder Structure

```
study-planner/
├── src/
│   ├── main/java/com/studyplanner/     # Source files (JavaFX App, DB, Task logic)
│   └── test/java/com/studyplanner/     # JUnit test classes
├── tasks.db                             # SQLite database (auto-created)
├── pom.xml                              # Maven project config
├── .gitignore                           # Git ignore rules
└── testing.md                           # Test plan and coverage
```

## 🚀 How to Run the Project

### Prerequisites

- Java 17+
- Maven 3.8+
- Git (to clone repo)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/ciruschakma/ENSE-375-Group-Project-2025-Spring.git
   cd ENSE-375-Group-Project-2025-Spring
   ```

2. **Build and Run the App**
   ```bash
   mvn clean javafx:run
   ```

3. **Run Tests**
   ```bash
   mvn test
   ```

> 💡 The app uses an SQLite file `tasks.db` for persistence. It will auto-create the DB if it doesn’t exist.

## 🧪 Testing Notes

We used the following testing strategies:
- **Unit Testing**: For classes like `Task`, `TaskDB`
- **Integration Testing**: Verifies DB interaction and UI flow
- **Validation Testing**: Includes:
  - Boundary Value Analysis
  - Equivalence Class Testing
  - Decision Table Testing
  - State Transition Testing
  - Use Case Testing

📄 See `testing.md` for complete test cases and design details.

## ⚠️ Important Dev Note

Due to the **growing number of tasks** in the `tasks.db` file (especially from automated testing), you may experience a **long startup delay** because of the timer logic checking deadlines.

👉 **To reset the database after testing**, delete the file:

```bash
del tasks.db      # On Windows
rm tasks.db       # On Mac/Linux
```

The app will regenerate a fresh empty DB on next launch.

---

## 📘 References

- ENSE 375 – Software Testing & Validation  
- JavaFX Documentation  
- SQLite JDBC Driver  
- Maven Lifecycle Reference  

---

> 📅 **Deadline:** July 31, 2025  
> 📬 Commit regularly. Submit GitHub link & commit hash on URCourses before the deadline.
