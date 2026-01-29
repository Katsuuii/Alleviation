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
        this.productCollection = DatabaseAllev.getInstance().getDatabase().getCollection("Product");
    }
    public String getProductIdByName(String productName) {
        Document doc = productCollection.find(eq("name", productName)).first();
        if (doc == null) return null;
        return doc.getString("_id");
    }
    public String getProductNameById(String productId) {
        Document doc = productCollection.find(eq("_id", productId)).first();
        if (doc == null) return null;
        return doc.getString("name");
    }

    public double getPriceByProductId(String productId) {
        Document doc = productCollection.find(eq("_id", productId)).first();
        if (doc == null) throw new IllegalArgumentException("Product not found: " + productId);
        Object priceObj = doc.get("price");
        return ((Number) priceObj).doubleValue();
    }

    public void addProduct(Product product) {
        Document doc = new Document("_id", product.getId())
                .append("name", product.getName())
                .append("price", product.getPrice())
                .append("quantity", product.getQuantity())
                .append("sales", product.getSales());

        productCollection.insertOne(doc);
    }

    public void recordSale(String productId, int amount) {
        Document query = new Document("_id", productId);

        Bson updates = Updates.combine(
                Updates.inc("quantity", -amount),
                Updates.inc("sales", amount)
        );

        var result = productCollection.updateOne(query, updates);

        // No println, but still catches "it didn't update"
        if (result.getMatchedCount() == 0) {
            throw new IllegalStateException("recordSale failed: product not found for _id=" + productId);
        }
        if (result.getModifiedCount() == 0) {
            // Matched but didn't change (usually amount=0 or fields missing)
            throw new IllegalStateException("recordSale failed: matched but not modified for _id=" + productId);
        }
    }




    public int getStock(String productId) {
        Document doc = productCollection.find(eq("_id", productId)).first();
        if (doc == null) throw new IllegalArgumentException("Product not found: " + productId);
        Object qtyObj = doc.get("quantity");
        return ((Number) qtyObj).intValue();
    }
}