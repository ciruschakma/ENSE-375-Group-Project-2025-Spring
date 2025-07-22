package com.studyplanner;

import java.time.LocalDate;

public class Task {
    private final String title;
    private final LocalDate date;
    private boolean completed;

    public Task(String title, LocalDate date) {
        this.title = title;
        this.date = date;
        this.completed = false;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
