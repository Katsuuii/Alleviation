package Database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CartDatabase {

    private final MongoCollection<Document> cartCol;

    public CartDatabase() {
        MongoDatabase db = DatabaseAllev.getInstance().getDatabase();
        this.cartCol = db.getCollection("carts");
    }

    // Add item (if exists, qty++)
    public void addToCart(String username, String productId, String title, String displayPrice, String type) {
        if (username == null || username.isBlank()) return;
        if (productId == null || productId.isBlank()) return;

        Document existing = cartCol.find(Filters.and(
                Filters.eq("username", username),
                Filters.eq("productId", productId)
        )).first();

        if (existing == null) {
            Document doc = new Document("username", username)
                    .append("productId", productId)
                    .append("title", title)
                    .append("displayPrice", displayPrice) // "$59.99" or "$10–$50"
                    .append("type", type)                 // "GAME" or "GIFT_CARD"
                    .append("qty", 1)
                    .append("addedAt", System.currentTimeMillis());
            cartCol.insertOne(doc);
        } else {
            cartCol.updateOne(
                    Filters.eq("_id", existing.getObjectId("_id")),
                    Updates.inc("qty", 1)
            );
        }
    }

    public void removeItem(String username, String productId) {
        cartCol.deleteOne(Filters.and(
                Filters.eq("username", username),
                Filters.eq("productId", productId)
        ));
    }

    public void incrementQty(String username, String productId) {
        Document existing = cartCol.find(Filters.and(
                Filters.eq("username", username),
                Filters.eq("productId", productId)
        )).first();

        if (existing == null) return;

        cartCol.updateOne(
                Filters.eq("_id", existing.getObjectId("_id")),
                Updates.inc("qty", 1)
        );
    }

    public void decrementQty(String username, String productId) {
        Document existing = cartCol.find(Filters.and(
                Filters.eq("username", username),
                Filters.eq("productId", productId)
        )).first();

        if (existing == null) return;

        int qty = existing.getInteger("qty", 1);
        if (qty <= 1) {
            removeItem(username, productId);
        } else {
            cartCol.updateOne(
                    Filters.eq("_id", existing.getObjectId("_id")),
                    Updates.inc("qty", -1)
            );
        }
    }

    public List<Document> getCart(String username) {
        List<Document> items = new ArrayList<>();
        cartCol.find(Filters.eq("username", username)).into(items);

        // newest first
        items.sort(Comparator.comparingLong(d -> d.getLong("addedAt")));

        List<Document> reversed = new ArrayList<>();
        for (int i = items.size() - 1; i >= 0; i--) reversed.add(items.get(i));
        return reversed;
    }

    public void clearCart(String username) {
        cartCol.deleteMany(Filters.eq("username", username));
    }

    // ✅ NEW: store chosen denomination for gift cards
    public void setGiftCardAmount(String username, String productId, int unitAmount) {
        cartCol.updateOne(
                Filters.and(
                        Filters.eq("username", username),
                        Filters.eq("productId", productId)
                ),
                Updates.set("unitAmount", unitAmount)
        );
    }
}
