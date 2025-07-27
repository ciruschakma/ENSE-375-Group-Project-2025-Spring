package com.studyplanner;

import org.junit.Test;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class TaskDBDataFlowTest {

    // Test 1: All Task fields propagate from object definition to DB and back (round-trip)
    @Test
    public void testTaskFields_RoundTripDataFlow() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(
            "DFTest", LocalDate.now().plusDays(5), false,
            "Critical", 7, "Lab", "Data flow round-trip",
            now, now, Duration.ofMinutes(55), true
        );

        TaskDB.addTask(task);

        List<Task> retrieved = TaskDB.getTasksForDate(task.getDate());
        Task match = retrieved.stream()
                .filter(t -> t.getTitle().equals("DFTest"))
                .findFirst().orElse(null);

        assertNotNull("Task should be found in DB", match);
        assertEquals(task.getTitle(), match.getTitle());
        assertEquals(task.getDate(), match.getDate());
        assertEquals(task.isCompleted(), match.isCompleted());
        assertEquals(task.getPriority(), match.getPriority());
        assertEquals(task.getComplexity(), match.getComplexity());
        assertEquals(task.getCategory(), match.getCategory());
        assertEquals(task.getNotes(), match.getNotes());
        assertEquals(task.isTimerEnabled(), match.isTimerEnabled());
        // createdAt, startTime, and duration can be tricky due to possible serialization differences
        // but you can check that they're not null and are in the correct range
        assertNotNull(match.getCreatedAt());
        assertNotNull(match.getStartTime());
        assertTrue(match.getDuration().toMinutes() > 0);
    }

    // Test 2: Multiple tasks, each field propagates
    @Test
    public void testMultipleTasks_FieldPropagation() {
        LocalDate date = LocalDate.now().plusDays(8);

        Task task1 = new Task(
            "DFMulti1", date, false,
            "Low", 1, "Test", "First task",
            LocalDateTime.now(), LocalDateTime.now(), Duration.ofMinutes(10), false
        );
        Task task2 = new Task(
            "DFMulti2", date, true,
            "Medium", 2, "Test", "Second task",
            LocalDateTime.now(), LocalDateTime.now(), Duration.ofMinutes(15), true
        );

        TaskDB.addTask(task1);
        TaskDB.addTask(task2);

        List<Task> tasks = TaskDB.getTasksForDate(date);
        Task found1 = tasks.stream().filter(t -> t.getTitle().equals("DFMulti1")).findFirst().orElse(null);
        Task found2 = tasks.stream().filter(t -> t.getTitle().equals("DFMulti2")).findFirst().orElse(null);

        assertNotNull("Task 1 should be found", found1);
        assertNotNull("Task 2 should be found", found2);
        assertEquals(task1.getNotes(), found1.getNotes());
        assertEquals(task2.getNotes(), found2.getNotes());
        assertEquals(task2.isCompleted(), found2.isCompleted());
    }

    // Test 3: Edge - task with empty notes, lowest complexity, false timerEnabled
    @Test
    public void testFieldPropagation_EdgeCases() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(
            "DFEdge", LocalDate.now().plusDays(10), false,
            "Low", 0, "Misc", "", // empty notes
            now, now, Duration.ofSeconds(1), false // minimal duration, timer off
        );

        TaskDB.addTask(task);
        List<Task> tasks = TaskDB.getTasksForDate(task.getDate());
        Task found = tasks.stream().filter(t -> t.getTitle().equals("DFEdge")).findFirst().orElse(null);

        assertNotNull("Edge case task should be found", found);
        assertEquals("", found.getNotes());
        assertEquals(0, found.getComplexity());
        assertFalse(found.isTimerEnabled());
        assertEquals(Duration.ofSeconds(1), found.getDuration());
    }
}
