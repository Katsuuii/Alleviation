import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class OrderLineDAOImpl implements OrderLineDAO {
    private final MongoCollection<Document> collection;

    public OrderLineDAOImpl() {
        MongoDatabase db = MongoDBUtil.getDatabase();
        this.collection = db.getCollection("orderlines");
    }

    @Override
    public void insert(OrderLine o) {
        Document doc = new Document("id", o.getId())
                .append("productName", o.getProductName())
                .append("quantity", o.getQuantity())
                .append("price", o.getPrice());
        collection.insertOne(doc);
    }

    @Override
    public List<OrderLine> findAll() {
        List<OrderLine> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(new OrderLine(
                    doc.getString("id"),
                    doc.getString("productName"),
                    doc.getInteger("quantity"),
                    doc.getDouble("price")
            ));
        }
        return list;
    }

    @Override
    public void delete(String id) {
        collection.deleteOne(new Document("id", id));
    }
}