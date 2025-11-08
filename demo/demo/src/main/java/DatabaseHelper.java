import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
public class DatabaseHelper {
    protected final String DB_URI = "mongodb+srv://Katsuu:Mark123@alleviation.48lfkfh.mongodb.net/?appName=Alleviation";
    protected final String DB_NAME = "tasks";
    private static final DatabaseHelper instance = new DatabaseHelper();

    private MongoDatabase database;

    private MongoClient mongoClient;

    private DatabaseHelper() {
    }

    public static DatabaseHelper getInstance() {
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


}