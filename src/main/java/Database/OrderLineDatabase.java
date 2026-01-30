package Database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import OrderReceiptLogic.Order;
import OrderReceiptLogic.OrderLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Saves order line rows to MongoDB collection: "Orderline"
 *
 * ERD fields:
 * PK  OrderLine_Number (int)
 * FK  Order_Number     (int)
 * FK  Product_ID       (string in your project, e.g. "PROD-12EE1")
 *     Quantity         (int)
 *     Price            (double)
 */
public class OrderLineDatabase {

    private final MongoCollection<Document> orderLineCollection;

    public OrderLineDatabase() {
        // Must match Compass collection name exactly (you have "Orderline" in Compass)
        this.orderLineCollection = DatabaseAllev.getInstance()
                .getDatabase()
                .getCollection("Orderline");
    }

    /**
     * Inserts one row per OrderLine for the given Order.
     */
    public void addOrderLinesForOrder(Order order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");

        List<Document> docs = new ArrayList<>();

        int idx = 1;
        for (OrderLine line : order.getOrderLines()) {

            // PK strategy: orderNumber * 100 + idx (unique per line)
            int orderLineNumber = order.getOrderNumber() * 100 + idx;
            idx++;

            Document doc = new Document()
                    .append("_id", orderLineNumber)                 // OrderLine_Number (PK)
                    .append("orderNumber", order.getOrderNumber())  // Order_Number (FK)
                    .append("productId", line.getProductId())       // Product_ID (FK)
                    .append("quantity", line.getQuantity())         // Quantity
                    .append("price", line.getPriceAtOrder());       // Price (unit price at order time)

            docs.add(doc);
        }

        if (!docs.isEmpty()) {
            orderLineCollection.insertMany(docs);
        }
    }
}
