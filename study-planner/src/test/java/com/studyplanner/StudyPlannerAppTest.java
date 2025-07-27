package com.studyplanner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class StudyPlannerAppTest {

    @Before
    public void setUp() {
        StudyPlannerApp.tasksByDate.clear();
    }

    @Test
    public void testAddTaskToDate() {
        LocalDate date = LocalDate.of(2025, 7, 28);
        Task t = new Task("Meeting", date);
        StudyPlannerApp.tasksByDate.put(date, new ArrayList<>(List.of(t)));

        assertTrue(StudyPlannerApp.tasksByDate.containsKey(date));
        assertEquals("Meeting", StudyPlannerApp.tasksByDate.get(date).get(0).getTitle());
    }

    @Test
    public void testGetAllOngoingAndCompletedTasks() {
        LocalDate today = LocalDate.now();
        Task t1 = new Task("A", today);
        Task t2 = new Task("B", today);
        t2.setCompleted(true);

        StudyPlannerApp.tasksByDate.put(today, new ArrayList<>(Arrays.asList(t1, t2)));

        List<Task> ongoing = StudyPlannerApp.getAllOngoingTasks();
        List<Task> completed = StudyPlannerApp.getAllCompletedTasks();

        assertEquals(1, ongoing.size());
        assertEquals(t1, ongoing.get(0));
        assertEquals(1, completed.size());
        assertEquals(t2, completed.get(0));
    }


     // --- Decision Table Tests ---

    @Test
    public void dtValidInputsShouldAdd() {
        LocalDate date = LocalDate.now();
        boolean added = StudyPlannerApp.addTask("Do homework", date, "High", 5);
        assertTrue("Expected valid task to be added", added);
        assertTrue(StudyPlannerApp.tasksByDate.containsKey(date));
    }

    @Test
    public void dtEmptyTitleShouldReject() {
        LocalDate date = LocalDate.now();
        boolean added = StudyPlannerApp.addTask("", date, "Medium", 3);
        assertFalse("Empty title should not be added", added);
        assertFalse(StudyPlannerApp.tasksByDate.containsKey(date));
    }

    @Test
    public void dtPastDateShouldReject() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        boolean added = StudyPlannerApp.addTask("Past task", yesterday, "Low", 2);
        assertFalse("Past dates should not be added", added);
    }

    @Test
    public void dtInvalidPriorityShouldReject() {
        LocalDate date = LocalDate.now();
        boolean added = StudyPlannerApp.addTask("Weird priority", date, "Urgent", 4);
        assertFalse("Invalid priority should not be added", added);
    }

     // --- STATE TRANSITION TESTS ---

    @Test
    public void stMarkOngoingToCompleted() {
        LocalDate date = LocalDate.now();
        StudyPlannerApp.addTask("Finish lab", date, "Medium", 5);

        // grab the newly added task
        Task t = StudyPlannerApp.tasksByDate.get(date).get(0);
        assertFalse(t.isCompleted());

        // transition
        t.setCompleted(true);

        // verify via your accessors
        List<Task> ongoing   = StudyPlannerApp.getAllOngoingTasks();
        List<Task> completed = StudyPlannerApp.getAllCompletedTasks();

        assertFalse(ongoing.contains(t));
        assertTrue(completed.contains(t));
    }

    @Test
    public void stCannotRevertCompletedToOngoing() {
        LocalDate date = LocalDate.now();
        StudyPlannerApp.addTask("Done", date, "Low", 1);
        Task t = StudyPlannerApp.tasksByDate.get(date).get(0);

        // mark completed, then (invalidly) back to ongoing
        t.setCompleted(true);
        t.setCompleted(false);

        // we treat “once completed” as sticky
        assertTrue(StudyPlannerApp.getAllCompletedTasks().contains(t));
        assertFalse(StudyPlannerApp.getAllOngoingTasks().contains(t));
    }


    // --- USE CASE TESTS ---

    @Test
    public void ucAddFilterAndCompleteTask() {
        LocalDate date = LocalDate.of(2025, 7, 31);

        // 1) Add
        assertTrue(StudyPlannerApp.addTask("Grant proposal", date, "High", 7));

        // 2) Calendar data
        assertTrue(StudyPlannerApp.tasksByDate.containsKey(date));

        // 3) Ongoing list
        List<Task> ongoing = StudyPlannerApp.getAllOngoingTasks();
        assertEquals(1, ongoing.size());
        assertEquals("Grant proposal", ongoing.get(0).getTitle());

        // 4) Complete it
        Task t = ongoing.get(0);
        t.setCompleted(true);

        // 5) Final checks
        assertTrue(StudyPlannerApp.getAllCompletedTasks().contains(t));
        assertTrue(StudyPlannerApp.getAllOngoingTasks().isEmpty());
    }
}

