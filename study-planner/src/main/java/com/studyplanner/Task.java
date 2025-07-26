package com.studyplanner;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private String title;
    private LocalDate date;
    private boolean completed;

    // --- New fields ---
    private String priority; // "High", "Medium", "Low"
    private int complexity;  // 1-10 scale
    private String category;
    private String notes;
    private LocalDateTime createdAt;

    public Task(String title, LocalDate date) {
        this.title = title;
        this.date = date;
        this.completed = false;
        this.priority = "Medium";
        this.complexity = 5;
        this.category = "General";
        this.notes = "";
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---

    public String getTitle() { return title; }
    public LocalDate getDate() { return date; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public int getComplexity() { return complexity; }
    public void setComplexity(int complexity) { this.complexity = complexity; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // --- For DB loading ---
    public Task(String title, LocalDate date, boolean completed, String priority, int complexity,
                String category, String notes, LocalDateTime createdAt) {
        this.title = title;
        this.date = date;
        this.completed = completed;
        this.priority = priority;
        this.complexity = complexity;
        this.category = category;
        this.notes = notes;
        this.createdAt = createdAt;
    }
}
