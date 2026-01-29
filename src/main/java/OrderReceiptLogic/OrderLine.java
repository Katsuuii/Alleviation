// ===================== OrderLine.java (FULL) =====================
package OrderReceiptLogic;

public class OrderLine {
    private final String productId;
    private final int quantity;
    private final double priceAtOrder;

    // ✅ NEW: only used for gift cards (nullable)
    private final Integer giftAmount;

    // Normal product
    public OrderLine(String productId, int quantity, double priceAtOrder) {
        this(productId, quantity, priceAtOrder, null);
    }

    // Gift card product (store chosen amount)
    public OrderLine(String productId, int quantity, double priceAtOrder, Integer giftAmount) {
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.giftAmount = giftAmount;
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPriceAtOrder() { return priceAtOrder; }
    public Integer getGiftAmount() { return giftAmount; }
}
