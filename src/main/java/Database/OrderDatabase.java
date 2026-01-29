package Database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import OrderReceiptLogic.Order;
import OrderReceiptLogic.OrderLine;

import java.util.ArrayList;
import java.util.List;

public class OrderDatabase {
    private final MongoCollection<Document> orderCollection;

    public OrderDatabase(MongoCollection<Document> orderCollection) {
        this.orderCollection = orderCollection;
    }

    /**
     * Inserts an order into MongoDB with all details including userId, OrderID, OrderNumber, product lines, and notes
     */
    public void addOrder(Order order, String notes) {
        List<Document> orderLineDocs = new ArrayList<>();
        for (OrderLine line : order.getOrderLines()) {
            Document lineDoc = new Document()
                    .append("productId", line.getProductId())
                    .append("quantity", line.getQuantity())
                    .append("priceAtOrder", line.getPriceAtOrder())
                    .append("giftAmount", line.getGiftAmount())
                    .append("subtotal", line.getQuantity() * line.getPriceAtOrder());

            orderLineDocs.add(lineDoc);
        }

        Document orderDoc = new Document()
                .append("_id", order.getID())
                .append("orderNumber", order.getOrderNumber())
                .append("userId", order.getUserId())
                .append("firstName", order.getFirstName())
                .append("lastName", order.getLastName())
                .append("email", order.getEmail())
                .append("notes", notes)
                .append("orderLines", orderLineDocs);

        try {
            orderCollection.insertOne(orderDoc);
            System.out.println("Order inserted successfully: " + orderDoc.toJson());
        } catch (Exception e) {
            System.err.println("Failed to insert order: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
