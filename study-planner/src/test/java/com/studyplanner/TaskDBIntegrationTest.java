package com.studyplanner;

import com.mongodb.client.*;
import org.bson.Document;
import org.junit.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class TaskDBIntegrationTest {
    private MongoClient mongoClient;
    private MongoCollection<Document> taskCollection;

    @Before
    public void setUp() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db = mongoClient.getDatabase("StudyPlanner");
        taskCollection = db.getCollection("tasks");
        taskCollection.deleteMany(new Document()); // Clean up before each test
    }

    @After
    public void tearDown() {
        taskCollection.deleteMany(new Document());
        mongoClient.close();
    }

    @Test
    public void testSaveTaskToDB() {
        Task t = new Task("TDD Save Test", LocalDate.of(2025, 7, 26));

        TaskDB.saveTaskToDB(t); 

        Document found = taskCollection.find(new Document("title", "TDD Save Test")).first();
        assertNotNull("Task should be saved in MongoDB", found);
    }

    
}
