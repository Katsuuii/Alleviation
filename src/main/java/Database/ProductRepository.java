package Database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class ProductRepository {

    private final MongoCollection<Document> col;

    public ProductRepository() {
        this.col = DatabaseAllev.getInstance().getDatabase().getCollection("Product");
    }

    public List<Document> findAll() {
        List<Document> docs = new ArrayList<>();
        col.find().into(docs);
        return docs;
    }

    public void restock(String productId, int amount) {
        col.updateOne(eq("_id", productId), Updates.inc("quantity", amount));
    }

    public void deleteById(String productId) {
        col.deleteOne(eq("_id", productId));
    }
    public boolean nameExistsIgnoreCase(String name) {
        if (name == null) return false;
        String clean = name.trim();

        // simple exact match (case sensitive) — easiest
        // return col.find(Filters.eq("name", clean)).first() != null;

        // better: case-insensitive exact match
        return col.find(Filters.regex("name", "^" + java.util.regex.Pattern.quote(clean) + "$",
                "i")).first() != null;
    }

    public void renameProduct(String productId, String newName) {
        col.updateOne(eq("_id", productId), Updates.set("name", newName.trim()));
    }

}
