import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBUtil {
    private static final String CONNECTION_STRING =
            "mongodb+srv://Yeshua:Yeshua1@alleviation.48lfkfh.mongodb.net/?retryWrites=true&w=majority&ssl=true";
    private static final String DATABASE_NAME = "Alleviation";

    private static MongoClient mongoClient;

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(new ConnectionString(CONNECTION_STRING));
        }
        return mongoClient.getDatabase(DATABASE_NAME);
    }
}
