// src/main/java/com/studyplanner/Task.java
package com.studyplanner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private String title;
    private LocalDate date;
    private boolean completed;

    // New timer fields
    private LocalDateTime startTime;
    private Duration duration;
    private boolean timerEnabled;

    // Existing fields
    private String priority;
    private int complexity;
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

        // Timer defaults
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ofMinutes(30);
        this.timerEnabled = false;
    }

    public Task(String title, LocalDate date, boolean completed, String priority, int complexity,
                String category, String notes, LocalDateTime createdAt,
                LocalDateTime startTime, Duration duration, boolean timerEnabled) {
        this.title = title;
        this.date = date;
        this.completed = completed;
        this.priority = priority;
        this.complexity = complexity;
        this.category = category;
        this.notes = notes;
        this.createdAt = createdAt;
        this.startTime = startTime;
        this.duration = duration;
        this.timerEnabled = timerEnabled;
    }

    // Getters/setters for all fields...

    public String getTitle() { return title; }
    public LocalDate getDate() { return date; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { if (!this.completed && completed) this.completed = true; }

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

    // Timer getters/setters
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public Duration getDuration() { return duration; }
    public void setDuration(Duration duration) { this.duration = duration; }

    public boolean isTimerEnabled() { return timerEnabled; }
    public void setTimerEnabled(boolean timerEnabled) { this.timerEnabled = timerEnabled; }
}
