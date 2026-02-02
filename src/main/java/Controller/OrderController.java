package Controller;

import Database.CartDatabase;
import Database.DatabaseAllev;
import Database.OrderDatabase;
import Database.OrderLineDatabase;
import Database.ProductDatabase;
import Database.ReceiptDatabase;
import OrderReceiptLogic.Order;
import OrderReceiptLogic.OrderLine;
import OrderReceiptLogic.Receipt;

import com.mongodb.client.MongoCollection;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.bson.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class OrderController implements Initializable {

    // --- UI (must match Order.fxml) ---
    @FXML private Label FirstandLast;
    @FXML private Label EmailAddress;

    @FXML private TableView<CheckoutRow> itemsTable;
    @FXML private TableColumn<CheckoutRow, String> colProduct;
    @FXML private TableColumn<CheckoutRow, Number> colQty;
    @FXML private TableColumn<CheckoutRow, String> colUnit;
    @FXML private TableColumn<CheckoutRow, String> colSubtotal;

    @FXML private TextArea notesArea;
    @FXML private Label totalLabel;
    @FXML private Button submitButton;

    // --- Session/state ---
    private int userId;
    private String username; // IMPORTANT for loading cart
    private String firstName;
    private String lastName;
    private String email;
    private boolean singleItemMode = false;
    private String singleProductName = null;
    private boolean singleGiftCardMode = false;

    // --- DBs ---
    private final CartDatabase cartDb = new CartDatabase();
    private final ProductDatabase productDb = new ProductDatabase();

    // --- model for table ---
    public static class CheckoutRow {
        private final SimpleStringProperty product;
        private final SimpleIntegerProperty qty;
        private final SimpleDoubleProperty unit;
        private final SimpleDoubleProperty subtotal;
        private final String productId;
        private final Integer giftAmount; // null if not giftcard
        private final String type; // "GAME" or "GIFT_CARD"

        public CheckoutRow(String productId, String product, int qty, double unit, Integer giftAmount, String type) {
            this.productId = productId;
            this.product = new SimpleStringProperty(product);
            this.qty = new SimpleIntegerProperty(qty);
            this.unit = new SimpleDoubleProperty(unit);
            this.subtotal = new SimpleDoubleProperty(unit * qty);
            this.giftAmount = giftAmount;
            this.type = type;
        }

        public String getProduct() { return product.get(); }
        public int getQty() { return qty.get(); }
        public double getUnit() { return unit.get(); }
        public double getSubtotal() { return subtotal.get(); }

        public String getProductId() { return productId; }
        public Integer getGiftAmount() { return giftAmount; }
        public String getType() { return type; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // table bindings
        colProduct.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduct()));
        colQty.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQty()));
        colUnit.setCellValueFactory(c -> new SimpleStringProperty(String.format(Locale.US, "$%.2f", c.getValue().getUnit())));
        colSubtotal.setCellValueFactory(c -> new SimpleStringProperty(String.format(Locale.US, "$%.2f", c.getValue().getSubtotal())));

        totalLabel.setText("$0.00");
    }

    // ✅ 4-arg version (old callers)
    public void setUserData(int userId, String firstName, String lastName, String email) {
        setUserData(userId, null, firstName, lastName, email);
    }

    // ✅ 5-arg version (cart checkout, giftcards controller, etc.)
    public void setUserData(int userId, String username, String firstName, String lastName, String email) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        // set labels
        String fn = firstName == null ? "" : firstName.trim();
        String ln = lastName == null ? "" : lastName.trim();
        String full = (fn + " " + ln).trim();
        if (full.isBlank()) full = "Guest";
        FirstandLast.setText(full);

        String em = (email == null || email.isBlank()) ? "—" : email.trim();
        EmailAddress.setText(em);

        // load cart if we have username
        refreshCheckoutFromCart();
    }

    private void refreshCheckoutFromCart() {
        if (username == null || username.isBlank()) {
            itemsTable.setItems(FXCollections.observableArrayList());
            totalLabel.setText("$0.00");
            return;
        }

        List<Document> cartItems = cartDb.getCart(username);
        var rows = FXCollections.<CheckoutRow>observableArrayList();

        double total = 0.0;

        for (Document d : cartItems) {
            String productId = d.getString("productId");
            String title = d.getString("title");
            String type = d.getString("type"); // "GAME" / "GIFT_CARD"
            int qty = d.getInteger("qty", 1);

            Integer giftAmount = null;
            double unitPrice = 0.0;

            if ("GIFT_CARD".equalsIgnoreCase(type)) {
                Integer amt = d.containsKey("unitAmount") ? d.getInteger("unitAmount") : null;

                // ✅ auto-default if missing
                if (amt == null || amt <= 0) {
                    int min = parseMinFromRange(d.getString("displayPrice")); // "$10–$50" -> 10, "$30" -> 30
                    cartDb.setGiftCardAmount(username, productId, min);
                    amt = min;
                }

                giftAmount = amt;
                unitPrice = amt.doubleValue();
            }
            else {
                // GAME: prefer DB price, fallback to parsing displayPrice
                try {
                    unitPrice = productDb.getPriceByProductId(productId);
                } catch (Exception ignored) {
                    unitPrice = parseSinglePrice(d.getString("displayPrice"));
                }
            }

            // Nice display name (gift amount appended)
            String displayName = title;
            if (giftAmount != null) displayName = title + " ($" + giftAmount + ")";

            CheckoutRow row = new CheckoutRow(productId, displayName, qty, unitPrice, giftAmount, type);
            rows.add(row);

            total += unitPrice * qty;
        }

        itemsTable.setItems(rows);
        totalLabel.setText(String.format(Locale.US, "$%.2f", total));
    }

    private double parseSinglePrice(String displayPrice) {
        try {
            if (displayPrice == null) return 0.0;
            String s = displayPrice.trim();
            if (s.contains("–") || s.contains("-")) return 0.0; // range => not a single price
            s = s.replace("$", "");
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // ✅ This fixes your FXML crash: onAction="#handleBack"
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Cart.fxml"));
            Parent root = loader.load();

            CartController cc = loader.getController();
            cc.setLoggedInUser(userId, username, firstName, lastName, email);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Cart");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSubmitOrder() {
        if (username == null || username.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user session (username missing).");
            return;
        }

        var rows = itemsTable.getItems();
        if (rows == null || rows.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Your cart is empty.");
            return;
        }



        for (CheckoutRow r : rows) {
            if ("GIFT_CARD".equalsIgnoreCase(r.getType())) {
                // Gift cards: sales only
                productDb.recordGiftCardSale(r.getProductId(), r.getQty());
            } else {
                // Games: stock down + sales up
                productDb.recordSale(r.getProductId(), r.getQty());
            }
        }




        String notes = (notesArea.getText() == null) ? "" : notesArea.getText().trim();

        try {
            // Build order lines from cart rows
            List<OrderLine> lines = new ArrayList<>();

            for (CheckoutRow r : rows) {
                String productId = r.getProductId();
                int qty = r.getQty();
                double priceAtOrder = r.getUnit();
                Integer giftAmount = r.getGiftAmount();

                // Stock check only for GAME
                if (!"GIFT_CARD".equalsIgnoreCase(r.getType())) {
                    int stock = productDb.getStock(productId);
                    if (qty > stock) {
                        showAlert(Alert.AlertType.ERROR, "Out of Stock",
                                r.getProduct() + ": only " + stock + " left.");
                        return;
                    }
                }

                lines.add(new OrderLine(productId, qty, priceAtOrder, giftAmount));
            }

            int id = generateOrderId();
            int orderNumber = generateOrderNumber();

            Order order = new Order(id, orderNumber, userId, lines, firstName, lastName, email);

            // Save order
            OrderDatabase orderDb = new OrderDatabase(getMongoCollection());
            orderDb.addOrder(order, notes);

            OrderLineDatabase orderLineDb = new OrderLineDatabase();
            orderLineDb.addOrderLinesForOrder(order);

            // Reduce stock for non-giftcards
            for (CheckoutRow r : rows) {
                if (!"GIFT_CARD".equalsIgnoreCase(r.getType())) {
                    productDb.recordSale(r.getProductId(), r.getQty());
                }
            }

            // Save receipt rows
            ReceiptDatabase receiptDb = new ReceiptDatabase();
            receiptDb.addReceiptForOrder(order);

            // Clear cart after successful order
            cartDb.clearCart(username);

            // Go to receipt screen
            Receipt receipt = new Receipt(order, "Unknown");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Receipt.fxml"));
            Parent receiptRoot = loader.load();

            ReceiptController rc = loader.getController();
            rc.setReceipt(receipt, notes);

            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setTitle("Receipt");
            stage.setScene(new Scene(receiptRoot));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not place order. Check console.");
        }
    }

    private MongoCollection<Document> getMongoCollection() {
        return DatabaseAllev.getInstance().getDatabase().getCollection("orders");
    }

    private int generateOrderId() {
        return (int) (Math.random() * 100000);
    }

    private int generateOrderNumber() {
        return (int) (Math.random() * 1000000);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // --- Compatibility: old controllers expect these ---
    public void prefillProduct(String productName) {
        this.singleItemMode = true;
        this.singleProductName = productName;
        this.singleGiftCardMode = false;
        refreshSingleItemTable();
    }

    public void enableGiftCardMode(String defaultCardName) {
        this.singleItemMode = true;
        this.singleGiftCardMode = true;
        this.singleProductName = defaultCardName;
        refreshSingleItemTable();
    }

    // Shows a 1-item checkout in the same table UI
    private void refreshSingleItemTable() {
        if (singleProductName == null || singleProductName.isBlank()) return;

        String productId = productDb.getProductIdByName(singleProductName);
        if (productId == null) return;

        int qty = 1;
        double unitPrice;
        Integer giftAmount = null;

        if (singleGiftCardMode) {
            giftAmount = 5;      // default chosen amount
            unitPrice = 5.0;
        } else {
            unitPrice = productDb.getPriceByProductId(productId);
        }

        String displayName = singleProductName;
        if (giftAmount != null) displayName = singleProductName + " ($" + giftAmount + ")";

        var rows = javafx.collections.FXCollections.<CheckoutRow>observableArrayList();
        rows.add(new CheckoutRow(productId, displayName, qty, unitPrice, giftAmount,
                singleGiftCardMode ? "GIFT_CARD" : "GAME"));

        itemsTable.setItems(rows);
        totalLabel.setText(String.format(java.util.Locale.US, "$%.2f", unitPrice * qty));
    }
    private int parseMinFromRange(String range) {
        int[] mm = parseMinMax(range);
        return mm[0];
    }

    private int[] parseMinMax(String range) {
        if (range == null) return new int[]{0, 0};
        String s = range.replace("$", "").trim();

        String[] parts;
        if (s.contains("–")) parts = s.split("–");
        else if (s.contains("-")) parts = s.split("-");
        else return new int[]{toIntSafe(s), toIntSafe(s)};

        return new int[]{toIntSafe(parts[0]), toIntSafe(parts[1])};
    }

    private int toIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

}
