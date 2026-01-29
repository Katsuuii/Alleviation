package Database;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import Database.DatabaseAllev;
public class DatabaseAllev {
    protected final String DB_URI =
            "mongodb+srv://Katsuu:1Brx9AEizY9oGn7X@alleviation.48lfkfh.mongodb.net/?appName=Alleviation";

    protected final String DB_NAME = "Alleviation";
    private static final DatabaseAllev instance = new DatabaseAllev();

    private MongoDatabase database;

    private MongoClient mongoClient;

    private DatabaseAllev() {
    }

    public static DatabaseAllev getInstance() {
        return instance;
    }

    public MongoClient getDatabaseClient() {
        if (this.mongoClient == null) {
            this.mongoClient = MongoClients.create(DB_URI);
        }

        return this.mongoClient;
    }
    public MongoDatabase getDatabase() {
        if(this.database == null){
            this.database = this.getDatabaseClient().getDatabase(DB_NAME);
        }

        return this.database;
    }
    private MongoCollection<Document> getMongoCollection() {
        return DatabaseAllev.getInstance()
                .getDatabase()
                .getCollection("orders"); // your orders collection
    }

}