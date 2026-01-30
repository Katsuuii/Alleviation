package Database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import OrderReceiptLogic.Order;
import OrderReceiptLogic.OrderLine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves receipts to MongoDB collection: "Receipt"
 *
 * To match your ERD table, we store ONE receipt document per order line:
 * Receipt_ID, User_ID, Order_Number, Quantity, Price, Product_Name, Email, First Name, Last Name, Order_Date
 */
public class ReceiptDatabase {

    private final MongoCollection<Document> receiptCollection;
    private final ProductDatabase productDb = new ProductDatabase();

    public ReceiptDatabase() {
        // Must match your Compass collection name exactly
        this.receiptCollection = DatabaseAllev.getInstance()
                .getDatabase()
                .getCollection("Receipt");
    }

    /**
     * Inserts receipt rows for every OrderLine of the order.
     */
    public void addReceiptForOrder(Order order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");

        String orderDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Document> docs = new ArrayList<>();

        int idx = 1;
        for (OrderLine line : order.getOrderLines()) {

            // Look up product name from Product collection
            String productName = productDb.getProductNameById(line.getProductId());
            if (productName == null) productName = line.getProductId(); // fallback

            // If gift card, append denomination into product name (optional but useful)
            if (line.getGiftAmount() != null) {
                productName = productName + " ($" + line.getGiftAmount() + ")";
            }

            // Receipt_ID strategy: orderNumber * 100 + idx (unique per line)
            int receiptId = order.getOrderNumber() * 100 + idx;
            idx++;

            Document receiptDoc = new Document()
                    .append("_id", receiptId)                    // Receipt_ID (PK)
                    .append("userId", order.getUserId())         // User_ID (FK)
                    .append("orderNumber", order.getOrderNumber())// Order_Number (FK)
                    .append("quantity", line.getQuantity())      // Quantity
                    .append("price", line.getPriceAtOrder())     // Price (unit price at order time)
                    .append("productName", productName)          // Product_Name
                    .append("email", order.getEmail())           // Email
                    .append("firstName", order.getFirstName())   // First Name
                    .append("lastName", order.getLastName())     // Last Name
                    .append("orderDate", orderDate);             // Order_Date (yyyy-MM-dd)

            docs.add(receiptDoc);
        }

        if (!docs.isEmpty()) {
            receiptCollection.insertMany(docs);
        }
    }
}
