package com.studyplanner;

import com.mongodb.client.*;
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
}
