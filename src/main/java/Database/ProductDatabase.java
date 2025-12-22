package Database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import ProductStuff.Product;

public class ProductDatabase {
    private final MongoCollection<Document> productCollection;

    public ProductDatabase() {
        // Matches your Compass collection name exactly
        this.productCollection = DatabaseAllev.getInstance().getDatabase().getCollection("Product");
    }

    // Initial Save
    public void addProduct(Product product) {
        Document doc = new Document("_id", product.getId())
                .append("name", product.getName())
                .append("price", product.getPrice())
                .append("quantity", product.getQuantity())
                .append("sales", product.getSales());

        productCollection.insertOne(doc);
    }

    /**
     * DYNAMIC LOGIC: This method performs the "Sale".
     * It updates the database in real-time.
     */
    public void recordSale(String productId) {
        Document query = new Document("_id", productId);

        // Atomically decrease quantity and increase sales
        Bson updates = Updates.combine(
                Updates.inc("quantity", -1),
                Updates.inc("sales", 1)
        );

        productCollection.updateOne(query, updates);
    }
}