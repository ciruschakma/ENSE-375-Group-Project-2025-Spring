package com.studyplanner;

import com.mongodb.client.*;
 import java.util.*;
 import java.time.LocalDate;
 import java.time.LocalDateTime;

import org.bson.Document;

public class TaskDB {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "StudyPlanner";
    private static final String COLLECTION_NAME = "tasks";
   

    public static void saveTaskToDB(Task t) {
        try (MongoClient client = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase db = client.getDatabase("StudyPlanner");
            MongoCollection<Document> collection = db.getCollection("tasks");
            Document doc = new Document("title", t.getTitle())
                    .append("date", t.getDate().toString())
                    .append("completed", t.isCompleted());
            collection.insertOne(doc);
        }
    } 
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



public static void updateTaskInDB(Task t) {
    try (MongoClient client = MongoClients.create("mongodb://localhost:27017")) {
        MongoDatabase db = client.getDatabase("StudyPlanner");
        MongoCollection<Document> collection = db.getCollection("tasks");
        Document filter = new Document("title", t.getTitle())
                .append("date", t.getDate().toString());
        Document update = new Document("$set",
                new Document("completed", t.isCompleted()));
        collection.updateOne(filter, update);
    }
}


    
}
