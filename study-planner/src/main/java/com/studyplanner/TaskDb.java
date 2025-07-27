package com.studyplanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDB {
    private static final String DB_URL = "jdbc:sqlite:tasks.db";

    static {
        // 1) Create table if missing (only original columns)
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = """
                CREATE TABLE IF NOT EXISTS tasks (
                    title TEXT,
                    date TEXT,
                    completed INTEGER,
                    priority TEXT,
                    complexity INTEGER,
                    category TEXT,
                    notes TEXT,
                    createdAt TEXT
                )
            """;
            conn.createStatement().execute(sql);

            // 2) Migrate: add new columns if they don't exist
            //    SQLite will error if the column is already there, so catch & ignore.
            try {
                conn.createStatement()
                    .execute("ALTER TABLE tasks ADD COLUMN startTime TEXT");
            } catch (SQLException e) { /* already exists? ignore */ }

            try {
                conn.createStatement()
                    .execute("ALTER TABLE tasks ADD COLUMN durationSeconds INTEGER");
            } catch (SQLException e) { /* already exists? ignore */ }

            try {
                conn.createStatement()
                    .execute("ALTER TABLE tasks ADD COLUMN timerEnabled INTEGER");
            } catch (SQLException e) { /* already exists? ignore */ }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize tasks table", e);
        }
    }

    public static void saveTaskToDB(Task t) {
        String sql = """
            INSERT INTO tasks (
              title, date, completed, priority, complexity,
              category, notes, createdAt,
              startTime, durationSeconds, timerEnabled
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getTitle());
            stmt.setString(2, t.getDate().toString());
            stmt.setInt(3, t.isCompleted() ? 1 : 0);
            stmt.setString(4, t.getPriority());
            stmt.setInt(5, t.getComplexity());
            stmt.setString(6, t.getCategory());
            stmt.setString(7, t.getNotes());
            stmt.setString(8, t.getCreatedAt().toString());
            stmt.setString(9, t.getStartTime().toString());
            stmt.setLong(10, t.getDuration().getSeconds());
            stmt.setInt(11, t.isTimerEnabled() ? 1 : 0);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save task", e);
        }
    }

    public static void updateTaskInDB(Task t) {
        String sql = """
            UPDATE tasks SET
              completed=?, priority=?, complexity=?, category=?, notes=?, createdAt=?,
              startTime=?, durationSeconds=?, timerEnabled=?
            WHERE title=? AND date=?
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, t.isCompleted() ? 1 : 0);
            stmt.setString(2, t.getPriority());
            stmt.setInt(3, t.getComplexity());
            stmt.setString(4, t.getCategory());
            stmt.setString(5, t.getNotes());
            stmt.setString(6, t.getCreatedAt().toString());
            stmt.setString(7, t.getStartTime().toString());
            stmt.setLong(8, t.getDuration().getSeconds());
            stmt.setInt(9, t.isTimerEnabled() ? 1 : 0);
            stmt.setString(10, t.getTitle());
            stmt.setString(11, t.getDate().toString());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update task", e);
        }
    }

    public static List<Task> loadAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String title = rs.getString("title");
                LocalDate date = LocalDate.parse(rs.getString("date"));
                boolean completed = rs.getInt("completed") != 0;
                String priority = rs.getString("priority");
                int complexity = rs.getInt("complexity");
                String category = rs.getString("category");
                String notes = rs.getString("notes");
                LocalDateTime createdAt = LocalDateTime.parse(rs.getString("createdAt"));

                // For older rows these columns may be NULL
                String startTimeStr = rs.getString("startTime");
                LocalDateTime startTime = (startTimeStr != null)
                    ? LocalDateTime.parse(startTimeStr)
                    : LocalDateTime.now();

                long durSecs = 0;
                try { durSecs = rs.getLong("durationSeconds"); }
                catch (SQLException ignore) { /* missing or null → 0 */ }
                Duration duration = Duration.ofSeconds(durSecs > 0 ? durSecs : 30 * 60);

                boolean timerEnabled = false;
                try { timerEnabled = rs.getInt("timerEnabled") != 0; }
                catch (SQLException ignore) { /* missing → false */ }

                Task t = new Task(
                    title, date, completed,
                    priority, complexity,
                    category, notes, createdAt,
                    startTime, duration, timerEnabled
                );
                tasks.add(t);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load tasks", e);
        }
        return tasks;
    }

public static void addTask(Task task) {
    if (task == null) throw new IllegalArgumentException("Task cannot be null");

    String sql = "INSERT INTO tasks (title, date, completed, priority, complexity, category, notes, createdAt, startTime, durationSeconds, timerEnabled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, task.getTitle());
        stmt.setString(2, task.getDate().toString());
        stmt.setInt(3, task.isCompleted() ? 1 : 0);
        stmt.setString(4, task.getPriority());
        stmt.setInt(5, task.getComplexity());
        stmt.setString(6, task.getCategory());
        stmt.setString(7, task.getNotes());
        stmt.setString(8, task.getCreatedAt().toString());
        stmt.setString(9, task.getStartTime().toString());
        stmt.setLong(10, task.getDuration().getSeconds());
        stmt.setInt(11, task.isTimerEnabled() ? 1 : 0);

        stmt.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("Failed to insert task into database", e);
    }
}

public static List<Task> getTasksForDate(LocalDate date) {
    List<Task> tasks = new ArrayList<>();
    String sql = "SELECT * FROM tasks WHERE date = ?";

    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, date.toString());
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String title = rs.getString("title");
            LocalDate taskDate = LocalDate.parse(rs.getString("date"));
            boolean completed = rs.getInt("completed") != 0;
            String priority = rs.getString("priority");
            int complexity = rs.getInt("complexity");
            String category = rs.getString("category");
            String notes = rs.getString("notes");
            LocalDateTime createdAt = LocalDateTime.parse(rs.getString("createdAt"));
            String startTimeStr = rs.getString("startTime");
            LocalDateTime startTime = (startTimeStr != null) ? LocalDateTime.parse(startTimeStr) : LocalDateTime.now();
            long durSecs = 0;
            try { durSecs = rs.getLong("durationSeconds"); } catch (SQLException ignore) {}
            Duration duration = Duration.ofSeconds(durSecs > 0 ? durSecs : 30 * 60);
            boolean timerEnabled = false;
            try { timerEnabled = rs.getInt("timerEnabled") != 0; } catch (SQLException ignore) {}

            Task t = new Task(
                title, taskDate, completed,
                priority, complexity, category, notes, createdAt,
                startTime, duration, timerEnabled
            );
            tasks.add(t);
        }
    } catch (SQLException e) {
        throw new RuntimeException("Failed to fetch tasks", e);
    }
    return tasks;
}
}
