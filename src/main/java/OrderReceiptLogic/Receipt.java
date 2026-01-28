//package OrderReceiptLogic;
//
//import java.time.format.DateTimeFormatter;
//import java.util.Locale;
//
///**
// * Receipt generator for printing or displaying an order summary.
// */
//public class Receipt {
//    private final Order order;
//    private final String paymentMethod;
//    private final String referenceNumber;
//
//    public Receipt(Order order, String paymentMethod) {
//        if (order == null) {
//            throw new IllegalArgumentException("Order cannot be null");
//        }
//
//        this.order = order;
//        this.paymentMethod = (paymentMethod == null) ? "Unknown" : paymentMethod;
//
//        // Unique printable reference
//        this.referenceNumber =
//                "R-" + java.util.UUID.randomUUID().toString().substring(0, 8)
//                        + "-" + order.getID();
//    }
//
//    public String getPaymentMethod() {
//        return paymentMethod;
//    }
//
//    public String getReferenceNumber() {
//        return referenceNumber;
//    }
//
//    public String toPrintableText() {
//        StringBuilder sb = new StringBuilder();
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        sb.append("===== RECEIPT =====\n");
//        sb.append("Ref: ").append(getReferenceNumber()).append("\n");
//        sb.append("Order #: ").append(order.getOrderNumber()).append("\n");
//
//        sb.append("Customer: ")
//                .append(order.getFirstName()).append(" ")
//                .append(order.getLastName()).append("\n")
//                .append("Email: ").append(order.getEmail()).append("\n");
//
//
//        sb.append("--------------------\n");
//
//        for (OrderLine line : order.getOrderLines()) {
//            sb.append(String.format(
//                    Locale.US,
//                    "Product %d x%d  %.2f\n",
//                    line.getProductId(),
//                    line.getQuantity()
//            ));
//        }
//
//        sb.append("--------------------\n");
//        sb.append(String.format(Locale.US, "TOTAL: %.2f\n", order.getTotalAmount()));
//        sb.append("Payment: ").append(getPaymentMethod()).append("\n");
//        sb.append("====================\n");
//
//        return sb.toString();
//    }
//
//    @Override
//    public String toString() {
//        return toPrintableText();
//    }
//}
