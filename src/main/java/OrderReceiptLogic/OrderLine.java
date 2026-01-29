package OrderReceiptLogic;

public class OrderLine {
    private final String productId;     // MongoDB _id e.g. "PROD-AB123"
    private final int quantity;
    private final double priceAtOrder;

    public OrderLine(String productId, int quantity, double priceAtOrder) {
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPriceAtOrder() { return priceAtOrder; }
}
