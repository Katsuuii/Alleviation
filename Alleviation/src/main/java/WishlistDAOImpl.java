import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAOImpl implements WishlistDAO {
    private final MongoCollection<Document> collection;

    public WishlistDAOImpl() {
        MongoDatabase db = MongoDBUtil.getDatabase();
        this.collection = db.getCollection("wishlists");
    }

    @Override
    public void insert(Wishlist w) {
        Document doc = new Document("id", w.getId())
                .append("productName", w.getProductName())
                .append("description", w.getDescription());
        collection.insertOne(doc);
    }

    @Override
    public List<Wishlist> findAll() {
        List<Wishlist> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(new Wishlist(
                    doc.getString("id"),
                    doc.getString("productName"),
                    doc.getString("description")
            ));
        }
        return list;
    }

    @Override
    public void delete(String id) {
        collection.deleteOne(new Document("id", id));
    }
}