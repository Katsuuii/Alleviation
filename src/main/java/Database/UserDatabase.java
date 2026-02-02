package Database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt; // Import BCrypt
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.List;

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


    // Wishlist (embedded in User)
    // ----------------------------

    /** Adds productId to the user's wishlist (no duplicates). */
    public boolean addToWishlist(String username, String productId) {
        try {
            return userCollection.updateOne(
                    Filters.eq("username", username),
                    Updates.addToSet("wishlist", productId)
            ).getModifiedCount() > 0;
        } catch (MongoException e) {
            System.err.println("DATABASE ERROR adding to wishlist for user: " + username);
            e.printStackTrace();
            return false;
        }
    }

    /** Removes productId from the user's wishlist. */
    public boolean removeFromWishlist(String username, String productId) {
        try {
            return userCollection.updateOne(
                    Filters.eq("username", username),
                    Updates.pull("wishlist", productId)
            ).getModifiedCount() > 0;
        } catch (MongoException e) {
            System.err.println("DATABASE ERROR removing from wishlist for user: " + username);
            e.printStackTrace();
            return false;
        }
    }

    /** Returns true if productId exists in the user's wishlist. */
    public boolean isWishlisted(String username, String productId) {
        try {
            Document user = userCollection.find(
                    Filters.and(
                            Filters.eq("username", username),
                            Filters.in("wishlist", productId)
                    )
            ).first();
            return user != null;
        } catch (MongoException e) {
            System.err.println("DATABASE ERROR checking wishlist for user: " + username);
            e.printStackTrace();
            return false;
        }
    }

    /** Gets the user's wishlist product IDs. Always returns a list (never null). */
    public List<String> getWishlist(String username) {
        try {
            Document user = userCollection.find(Filters.eq("username", username)).first();
            if (user == null) return new ArrayList<>();

            List<String> wishlist = user.getList("wishlist", String.class);
            return wishlist != null ? wishlist : new ArrayList<>();
        } catch (MongoException e) {
            System.err.println("DATABASE ERROR reading wishlist for user: " + username);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
