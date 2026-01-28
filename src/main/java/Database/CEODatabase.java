package Database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class CEODatabase {

    private final MongoCollection<Document> CEOCollection;

    public CEODatabase() {
        try {
            this.CEOCollection = DatabaseAllev
                    .getInstance()
                    .getDatabase()
                    .getCollection("CEO");
        } catch (Exception e) {
            System.err.println("FATAL: Could not initialize MongoDB collection. Check DatabaseAllev connection.");
            e.printStackTrace();
            throw new RuntimeException("Database connection failed during startup.", e);
        }
    }

    public boolean createCEO(String firstName, String lastName) {

        Document CEO = new Document()
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("role", "CEO");

        try {
            CEOCollection.insertOne(CEO);
            System.out.println("CEO added successfully: " + firstName + " " + lastName);
            return true;
        } catch (MongoException e) {
            System.err.println("DATABASE WRITE ERROR for CEO: " + firstName + " " + lastName);
            e.printStackTrace();
            return false;
        }
    }

    public Document findCEO(String lastName) {
        try {
            return CEOCollection.find(new Document("lastName", lastName)).first();
        } catch (MongoException e) {
            System.err.println("DATABASE ERROR while finding CEO with lastName: " + lastName);
            e.printStackTrace();
            return null;
        }
    }
    public Document findCEOByName(String firstName, String lastName) {
        Document query = new Document()
                .append("firstName", firstName)
                .append("lastName", lastName);

        return CEOCollection.find(query).first();
    }

}
