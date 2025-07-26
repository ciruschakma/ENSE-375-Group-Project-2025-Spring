package com.studyplanner;

import org.junit.*;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.*;

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
}

