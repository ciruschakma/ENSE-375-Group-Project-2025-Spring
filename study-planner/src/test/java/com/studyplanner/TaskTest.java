package com.studyplanner;

import org.junit.Test;
import java.time.LocalDate;
import static org.junit.Assert.*;

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
}
