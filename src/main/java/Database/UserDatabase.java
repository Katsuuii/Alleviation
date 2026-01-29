package Database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt; // Import BCrypt

public class UserDatabase {

    private final MongoCollection<Document> userCollection;

    public UserDatabase() {
        try {
            this.userCollection = DatabaseAllev
                    .getInstance()
                    .getDatabase()
                    .getCollection("User");
        } catch (Exception e) {
            System.err.println("FATAL: Could not initialize MongoDB collection. Check DatabaseAllev connection.");
            e.printStackTrace();
            throw new RuntimeException("Database connection failed during startup.", e);
        }
    }


    public boolean createUser(String firstname, String lastname, String email,
                              String Username, String HashedPassword) {

        Document user = new Document()
                .append("firstName", firstname)
                .append("lastName", lastname)
                .append("email", email)
                .append("username", Username)
                .append("password", HashedPassword) // Stores the hash!
                .append("role", "USER")
                .append("wishlist", new org.bson.types.BasicBSONList());

        try {
            userCollection.insertOne(user);
            System.out.println("User added successfully: " + Username);
            return true;
        } catch (MongoException e) {
            System.err.println("DATABASE WRITE ERROR for user: " + Username);
            e.printStackTrace();
            return false;
        }
    }


    public Document authenticateUser(String username, String password) {
        try {

            Document query = new Document("username", username);
            Document foundUser = userCollection.find(query).first();

            if (foundUser == null) {

                return null;
            }


            String storedHashedPassword = foundUser.getString("password");


            if (BCrypt.checkpw(password, storedHashedPassword)) {
                System.out.println("User authenticated: " + username);
                return foundUser;
            } else {
                System.out.println("Authentication failed for user: " + username);
                return null;
            }

        } catch (MongoException e) {
            System.err.println("DATABASE ERROR during authentication for user: " + username);
            e.printStackTrace();
            return null;
        }
    }

    // Check email exists
    public boolean emailExists(String email) {
        Document found = userCollection.find(new Document("email", email)).first();
        return found != null;
    }

    // Check username exists
    public boolean usernameExists(String username) {
        Document found = userCollection.find(new Document("username", username)).first();
        return found != null;
    }
}