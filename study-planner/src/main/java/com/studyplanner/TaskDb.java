package com.studyplanner;

import com.mongodb.client.*;
import org.bson.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TaskDB {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "studyplannerapp";
    private static final String COLLECTION_NAME = "tasks";

    // Save a single task (used when adding a new one)
    public static void saveTaskToDB(Task t) {
        try (MongoClient client = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase db = client.getDatabase(DB_NAME);
            MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
            Document doc = new Document("title", t.getTitle())
                    .append("date", t.getDate().toString())
                    .append("completed", t.isCompleted())
                    .append("priority", t.getPriority())
                    .append("complexity", t.getComplexity())
                    .append("category", t.getCategory())
                    .append("notes", t.getNotes())
                    .append("createdAt", t.getCreatedAt().toString());
            collection.insertOne(doc);
        }
    }

    public static void updateTaskInDB(Task t) {
        try (MongoClient client = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase db = client.getDatabase(DB_NAME);
            MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
            Document filter = new Document("title", t.getTitle())
                    .append("date", t.getDate().toString());
            Document update = new Document("$set",
                    new Document("completed", t.isCompleted())
                            .append("priority", t.getPriority())
                            .append("complexity", t.getComplexity())
                            .append("category", t.getCategory())
                            .append("notes", t.getNotes())
                            .append("createdAt", t.getCreatedAt().toString())
            );
            collection.updateOne(filter, update);
        }
    }

    // Load all tasks from the database
    public static List<Task> loadAllTasks() {
        List<Task> tasks = new ArrayList<>();
        try (MongoClient client = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase db = client.getDatabase(DB_NAME);
            MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);

            for (Document doc : collection.find()) {
                String title = doc.getString("title");
                String dateStr = doc.getString("date");
                boolean completed = doc.getBoolean("completed", false);
                String priority = doc.getString("priority");
                int complexity = doc.containsKey("complexity") ? doc.getInteger("complexity") : 5;
                String category = doc.getString("category");
                String notes = doc.getString("notes");
                String createdAtStr = doc.getString("createdAt");
                LocalDate date = LocalDate.parse(dateStr);
                LocalDateTime createdAt = createdAtStr != null
                        ? LocalDateTime.parse(createdAtStr)
                        : LocalDateTime.now();

                Task t = new Task(
                        title,
                        date,
                        completed,
                        priority != null ? priority : "Medium",
                        complexity,
                        category != null ? category : "General",
                        notes != null ? notes : "",
                        createdAt
                );
                tasks.add(t);
            }
        }
        return tasks;
    }
}
