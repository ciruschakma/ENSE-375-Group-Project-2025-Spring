package com.studyplanner;

import org.junit.Test;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class TaskDBIntegrationTest {

    @Test
    public void testAddAndRetrieveTaskIntegration() {
        LocalDate today = LocalDate.now();

        Task task = new Task(
            "Integration test", today, false,
            "Medium", 2, "Integration", "integration test notes",
            LocalDateTime.now(), LocalDateTime.now(),
            Duration.ofMinutes(45), true
        );

        TaskDB.addTask(task);

        List<Task> tasks = TaskDB.getTasksForDate(today);
        boolean found = tasks.stream().anyMatch(
            t -> t.getTitle().equals("Integration test") &&
                 t.getCategory().equals("Integration")
        );
        assertTrue("Added task should be retrievable from DB", found);
    }

    @Test
    public void testUpdateTaskIntegration() {
        LocalDate date = LocalDate.now().plusDays(2);

        // Add a task
        Task task = new Task(
            "Integration update", date, false,
            "Low", 1, "Integration", "will update",
            LocalDateTime.now(), LocalDateTime.now(),
            Duration.ofMinutes(60), false
        );
        TaskDB.addTask(task);

        // Simulate user completes task and changes notes
        Task updatedTask = new Task(
            "Integration update", date, true,
            "Low", 1, "Integration", "updated notes",
            task.getCreatedAt(), task.getStartTime(),
            task.getDuration(), true
        );
        TaskDB.updateTaskInDB(updatedTask);

        List<Task> tasks = TaskDB.getTasksForDate(date);
        boolean found = tasks.stream().anyMatch(
            t -> t.getTitle().equals("Integration update") &&
                 t.isCompleted() &&
                 t.getNotes().equals("updated notes")
        );
        assertTrue("Updated task should reflect changes in DB", found);
    }

    @Test
    public void testMultipleTasksSameDateIntegration() {
        LocalDate date = LocalDate.now().plusDays(3);

        Task task1 = new Task(
            "Integration Multi1", date, false,
            "High", 3, "Integration", "task 1",
            LocalDateTime.now(), LocalDateTime.now(),
            Duration.ofMinutes(10), false
        );
        Task task2 = new Task(
            "Integration Multi2", date, false,
            "Medium", 2, "Integration", "task 2",
            LocalDateTime.now(), LocalDateTime.now(),
            Duration.ofMinutes(20), false
        );

        TaskDB.addTask(task1);
        TaskDB.addTask(task2);

        List<Task> tasks = TaskDB.getTasksForDate(date);
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Integration Multi1")));
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Integration Multi2")));
    }
}
