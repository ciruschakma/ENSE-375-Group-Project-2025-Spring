package com.studyplanner;

import org.junit.Test;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class TaskDBPathTest {

    // --- PATHS FOR addTask(Task) ---

    @Test
    public void testAddTask_validTask_shouldInsertSuccessfully() {
        Task task = new Task(
            "Path test", LocalDate.now(), false,
            "High", 1, "General", "unit test",
            LocalDateTime.now(), LocalDateTime.now(),
            Duration.ofMinutes(30), false
        );
        TaskDB.addTask(task);

        List<Task> tasks = TaskDB.getTasksForDate(task.getDate());
        boolean found = tasks.stream().anyMatch(t -> t.getTitle().equals("Path test"));
        assertTrue("Task should be added", found);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddTask_nullTask_shouldThrowException() {
        TaskDB.addTask(null);
    }

    // --- PATHS FOR getTasksForDate(LocalDate) ---

    @Test
    public void testGetTasksForDate_withExistingTasks() {
        LocalDate date = LocalDate.now().plusDays(1);
        Task task = new Task(
            "Future test", date, false,
            "Medium", 3, "General", "future task",
            LocalDateTime.now(), LocalDateTime.now(),
            Duration.ofMinutes(15), false
        );
        TaskDB.addTask(task);

        List<Task> tasks = TaskDB.getTasksForDate(date);
        assertFalse("Should retrieve at least one task for date", tasks.isEmpty());
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Future test")));
    }

    @Test
    public void testGetTasksForDate_noTasksReturnsEmptyList() {
        LocalDate date = LocalDate.of(2099, 1, 1); // Presume no tasks on this date
        List<Task> tasks = TaskDB.getTasksForDate(date);
        assertTrue("Should return empty list for no tasks", tasks.isEmpty());
    }
}
