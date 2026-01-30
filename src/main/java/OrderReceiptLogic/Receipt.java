package OrderReceiptLogic;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Receipt {
    private final Order order;
    private final String paymentMethod;
    private final String referenceNumber;

    public Receipt(Order order, String paymentMethod) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");
        this.order = order;
        this.paymentMethod = (paymentMethod == null) ? "Unknown" : paymentMethod;

        this.referenceNumber =
                "R-" + java.util.UUID.randomUUID().toString().substring(0, 8)
                        + "-" + order.getID();
    }

    public Order getOrder() { return order; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getReferenceNumber() { return referenceNumber; }

    public String toPrintableText() {
        StringBuilder sb = new StringBuilder();

        sb.append("===== RECEIPT =====\n");
        sb.append("Ref: ").append(getReferenceNumber()).append("\n");
        sb.append("Order #: ").append(order.getOrderNumber()).append("\n");
        sb.append("Customer: ")
                .append(order.getFirstName()).append(" ")
                .append(order.getLastName()).append("\n");
        sb.append("Email: ").append(order.getEmail()).append("\n");

        sb.append("--------------------\n");

        double total = 0;
        for (OrderLine line : order.getOrderLines()) {
            double subtotal = line.getQuantity() * line.getPriceAtOrder();
            total += subtotal;

            String productDisplay = line.getProductId();
            if (line.getGiftAmount() != null) {
                productDisplay += " ($" + line.getGiftAmount() + ")";
            }

            sb.append(String.format(
                    Locale.US,
                    "%s  x%d   $%.2f   Sub: $%.2f\n",
                    productDisplay,
                    line.getQuantity(),
                    line.getPriceAtOrder(),
                    subtotal
            ));
        }

        sb.append("--------------------\n");
        sb.append(String.format(Locale.US, "TOTAL: $%.2f\n", total));
        sb.append("Payment: ").append(getPaymentMethod()).append("\n");
        sb.append("====================\n");

        return sb.toString();
    }

    @Override
    public String toString() {
        return toPrintableText();
    }
}
