package com.studyplanner;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TaskDB {
    private static final String DB_URL = "jdbc:sqlite:tasks.db";

    static {
        // Ensure table exists on startup
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
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tasks table", e);
        }
    }

    // Save a single task
    public static void saveTaskToDB(Task t) {
        String sql = """
            INSERT INTO tasks (title, date, completed, priority, complexity, category, notes, createdAt)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
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
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save task", e);
        }
    }

    // Update an existing task
    public static void updateTaskInDB(Task t) {
        String sql = """
            UPDATE tasks
            SET completed=?, priority=?, complexity=?, category=?, notes=?, createdAt=?
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
            stmt.setString(7, t.getTitle());
            stmt.setString(8, t.getDate().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update task", e);
        }
    }

    // Load all tasks from the database
    public static List<Task> loadAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String title = rs.getString("title");
                String dateStr = rs.getString("date");
                boolean completed = rs.getInt("completed") != 0;
                String priority = rs.getString("priority");
                int complexity = rs.getInt("complexity");
                String category = rs.getString("category");
                String notes = rs.getString("notes");
                String createdAtStr = rs.getString("createdAt");
                LocalDate date = LocalDate.parse(dateStr);
                LocalDateTime createdAt = createdAtStr != null
                        ? LocalDateTime.parse(createdAtStr)
                        : LocalDateTime.now();

                Task t = new Task(
                        title,
                        date,
                        completed,
                        priority != null ? priority : "Medium",
                        complexity,
                        category != null ? category : "General",
                        notes != null ? notes : "",
                        createdAt
                );
                tasks.add(t);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load tasks", e);
        }
        return tasks;
    }
}
