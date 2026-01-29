package Database;
import static com.mongodb.client.model.Filters.eq;
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
    public String getProductIdByName(String productName) {
        Document doc = productCollection.find(eq("name", productName)).first();
        if (doc == null) return null;
        return doc.getString("_id");
    }
    public double getPriceByProductId(String productId) {
        Document doc = productCollection.find(eq("_id", productId)).first();
        if (doc == null) throw new IllegalArgumentException("Product not found: " + productId);
        Object priceObj = doc.get("price");
        return ((Number) priceObj).doubleValue();
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
    public void recordSale(String productId, int amount) {
        Document query = new Document("_id", productId);

        Bson updates = Updates.combine(
                Updates.inc("quantity", -amount),
                Updates.inc("sales", amount)
        );

        productCollection.updateOne(query, updates);
    }
    public int getStock(String productId) {
        Document doc = productCollection.find(eq("_id", productId)).first();
        if (doc == null) throw new IllegalArgumentException("Product not found: " + productId);
        Object qtyObj = doc.get("quantity");
        return ((Number) qtyObj).intValue();
    }

}