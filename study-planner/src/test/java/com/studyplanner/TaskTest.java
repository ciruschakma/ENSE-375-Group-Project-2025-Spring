package com.studyplanner;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TaskTest {

    @Test
    public void canSetAndGetPriorityAndComplexity() {
        LocalDate date = LocalDate.of(2025, 7, 26);
        Task task = new Task("Priority Task", date);

        task.setPriority("High");
        assertEquals("High", task.getPriority());

        task.setComplexity(8);
        assertEquals(8, task.getComplexity());
    }

    // --- BOUNDARY VALUE TESTS ---

    @Test
    public void testComplexityLowerBoundary() {
        Task task = new Task("Boundary Test", LocalDate.now());
        task.setComplexity(1);  // lower bound
        assertEquals(1, task.getComplexity());
    }

    @Test
    public void testComplexityUpperBoundary() {
        Task task = new Task("Boundary Test", LocalDate.now());
        task.setComplexity(10);  // upper bound
        assertEquals(10, task.getComplexity());
    }

    @Test
    public void testComplexityJustBelowLower() {
        Task task = new Task("Boundary Test", LocalDate.now());
        task.setComplexity(0);  // just below lower
        assertEquals(0, task.getComplexity());  // No enforcement yet
    }

    @Test
    public void testComplexityJustAboveUpper() {
        Task task = new Task("Boundary Test", LocalDate.now());
        task.setComplexity(11);  // just above upper
        assertEquals(11, task.getComplexity());  // No enforcement yet
    }

    @Test
    public void testEmptyTitle() {
        Task task = new Task("", LocalDate.now());
        assertEquals("", task.getTitle());  // Currently allowed
    }

    @Test
    public void testMaxLengthTitle() {
        String longTitle = "A".repeat(255);
        Task task = new Task(longTitle, LocalDate.now());
        assertEquals(longTitle, task.getTitle());
    }

     // -----------------------
    // EQUIVALENCE CLASS TESTS
    // -----------------------

    // --- Priority: Valid Equivalence Classes ---

    @Test
    public void testValidPriorityHigh() {
        Task task = new Task("Valid Priority", LocalDate.now());
        task.setPriority("High");  // valid class
        assertEquals("High", task.getPriority());
    }

    @Test
    public void testValidPriorityMedium() {
        Task task = new Task("Valid Priority", LocalDate.now());
        task.setPriority("Medium");  // valid class
        assertEquals("Medium", task.getPriority());
    }

    @Test
    public void testValidPriorityLow() {
        Task task = new Task("Valid Priority", LocalDate.now());
        task.setPriority("Low");  // valid class
        assertEquals("Low", task.getPriority());
    }

    // --- Priority: Invalid Equivalence Classes ---

    @Test
    public void testInvalidPriorityString() {
        Task task = new Task("Invalid Priority", LocalDate.now());
        task.setPriority("Urgent");  // invalid class (not enforced)
        assertEquals("Urgent", task.getPriority());  // Current model allows it
    }

    @Test
    public void testEmptyPriority() {
        Task task = new Task("Invalid Priority", LocalDate.now());
        task.setPriority("");  // invalid class (empty string)
        assertEquals("", task.getPriority());  // Allowed for now
    }

    // --- Complexity: Valid Equivalence Class ---

    @Test
    public void testValidComplexityClass() {
        Task task = new Task("Valid Complexity", LocalDate.now());
        task.setComplexity(5);  // within 1–10
        assertEquals(5, task.getComplexity());
    }

    // --- Complexity: Invalid Equivalence Classes ---

    @Test
    public void testComplexityNegative() {
        Task task = new Task("Invalid Complexity", LocalDate.now());
        task.setComplexity(-1);  // invalid
        assertEquals(-1, task.getComplexity());  // No validation yet
    }

    @Test
    public void testComplexityAboveRange() {
        Task task = new Task("Invalid Complexity", LocalDate.now());
        task.setComplexity(11);  // invalid
        assertEquals(11, task.getComplexity());
    }

    // --- Title: Valid and Invalid Equivalence Classes ---

    @Test
    public void testValidTitle() {
        Task task = new Task("Study Session", LocalDate.now());
        assertEquals("Study Session", task.getTitle());
    }

    @Test
    public void testEmptyTitleInvalidClass() {
        Task task = new Task("", LocalDate.now());  // invalid (empty string)
        assertEquals("", task.getTitle());  // Still allowed
    }

    @Test
    public void testDateClasses() {
    Task past = new Task("Past", LocalDate.now().minusDays(1));
    assertEquals(LocalDate.now().minusDays(1), past.getDate());
    Task today = new Task("Today", LocalDate.now());
    assertEquals(LocalDate.now(), today.getDate());
    Task future = new Task("Future", LocalDate.of(2100,1,1));
    assertEquals(LocalDate.of(2100,1,1), future.getDate());
    }

    @Test
    public void testDefaultCategoryAndNotes() {
    Task t = new Task("X", LocalDate.now());
    assertEquals("General", t.getCategory());
    assertEquals("", t.getNotes());
    }

    @Test
    public void testSetCategoryAndNotes() {
    Task t = new Task("X", LocalDate.now());
    t.setCategory("Work");
    t.setNotes("Finish report");
    assertEquals("Work", t.getCategory());
    assertEquals("Finish report", t.getNotes());
    }


}
