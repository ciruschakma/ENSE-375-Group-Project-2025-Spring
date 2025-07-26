package com.studyplanner;

import com.mongodb.client.*;
 import java.util.*;
 import java.time.LocalDate;

import org.bson.Document;

public class TaskDB {
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
    try (MongoClient client = MongoClients.create("mongodb://localhost:27017")) {
        MongoDatabase db = client.getDatabase("StudyPlanner");
        MongoCollection<Document> collection = db.getCollection("tasks");
        for (Document doc : collection.find()) {
            String title = doc.getString("title");
            String dateStr = doc.getString("date");
            boolean completed = doc.getBoolean("completed", false);
            Task t = new Task(title, LocalDate.parse(dateStr));
            t.setCompleted(completed);
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
