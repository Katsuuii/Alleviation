package Controller;

import Database.*;
import OrderReceiptLogic.Order;
import OrderReceiptLogic.OrderLine;
import OrderReceiptLogic.Receipt;

import com.mongodb.client.MongoCollection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderController implements Initializable {

    // Gift Card Picker UI
    @FXML private VBox giftCardBox;
    @FXML private ComboBox<String> giftCardTypeCombo;
    @FXML private Spinner<Integer> giftAmountSpinner;

    // Normal UI
    @FXML private Label FirstandLast;
    @FXML private Label EmailAddress;
    @FXML private Label Product;

    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private TextArea notesArea;
    @FXML private Button submitButton;

    // Session/state
    private int userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;

    private String selectedProduct;
    private boolean isGiftCard = false;

    private final ProductDatabase productDb = new ProductDatabase();

    // Must match DB names exactly
    private static final String ROBLOX = "Roblox Gift Card";
    private static final String MINECRAFT = "Minecraft Gift Card";
    private static final String STEAM = "Steam Gift Card";
    private static final String VISA = "Visa Gift Card";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        // Hide gift card UI by default
        giftCardBox.setVisible(false);
        giftCardBox.setManaged(false);

        // Default gift amount (will be replaced when gift mode is enabled)
        giftAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 5));
    }

    public void setUserData(int userId, String firstName, String lastName, String email) {
        this.userId = userId;
        this.userFirstName = firstName;
        this.userLastName = lastName;
        this.userEmail = email;

        String fn = firstName == null ? "" : firstName.trim();
        String ln = lastName == null ? "" : lastName.trim();
        String full = (fn + " " + ln).trim();
        if (full.isBlank()) full = "Guest";

        String em = (email == null || email.isBlank()) ? "—" : email.trim();

        FirstandLast.setText(full);
        EmailAddress.setText(em);
    }

    public void prefillProduct(String productName) {
        this.selectedProduct = productName;
        Product.setText(productName);

        if (!isGiftCard) {
            giftCardBox.setVisible(false);
            giftCardBox.setManaged(false);
        }
    }

    public void enableGiftCardMode(String defaultCardName) {
        isGiftCard = true;

        giftCardBox.setVisible(true);
        giftCardBox.setManaged(true);

        giftCardTypeCombo.getItems().setAll(ROBLOX, MINECRAFT, STEAM, VISA);

        giftCardTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            selectedProduct = newVal;
            Product.setText(newVal);
            applyGiftRangeFor(newVal);
        });

        if (defaultCardName != null && giftCardTypeCombo.getItems().contains(defaultCardName)) {
            giftCardTypeCombo.setValue(defaultCardName);
        } else {
            giftCardTypeCombo.setValue(ROBLOX);
        }

        applyGiftRangeFor(giftCardTypeCombo.getValue());
    }

    private void applyGiftRangeFor(String cardName) {
        int min, max, step;

        switch (cardName) {
            case ROBLOX -> { min = 5; max = 50; step = 5; }
            case STEAM  -> { min = 5; max = 20; step = 5; }
            case VISA   -> { min = 5; max = 50; step = 5; }
            case MINECRAFT -> { min = 30; max = 30; step = 1; }
            default -> { min = 5; max = 50; step = 5; }
        }

        giftAmountSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, min, step)
        );
    }

    @FXML
    private void handleSubmitOrder() {
        int quantity = quantitySpinner.getValue();
        String notes = notesArea.getText() == null ? "" : notesArea.getText().trim();

        if (selectedProduct == null || selectedProduct.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No product selected.");
            return;
        }

        try {
            String productId = productDb.getProductIdByName(selectedProduct);
            if (productId == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Product not found in database.");
                return;
            }

            int stock = productDb.getStock(productId);
            if (quantity > stock) {
                showAlert(Alert.AlertType.ERROR, "Out of Stock",
                        "Only " + stock + " left. Please lower the quantity.");
                return;
            }

            Integer giftAmount = isGiftCard ? giftAmountSpinner.getValue() : null;

            double priceAtOrder = isGiftCard
                    ? giftAmount
                    : productDb.getPriceByProductId(productId);

            OrderLine line = new OrderLine(productId, quantity, priceAtOrder, giftAmount);

            List<OrderLine> lines = new ArrayList<>();
            lines.add(line);

            int id = generateOrderId();
            int orderNumber = generateOrderNumber();

            Order order = new Order(id, orderNumber, userId, lines, userFirstName, userLastName, userEmail);

            // Save order to "orders"
            OrderDatabase orderDb = new OrderDatabase(getMongoCollection());
            orderDb.addOrder(order, notes);
            OrderLineDatabase orderLineDb = new OrderLineDatabase();
            orderLineDb.addOrderLinesForOrder(order);

            // Reduce stock only for non-giftcards
            if (!isGiftCard) {
                productDb.recordSale(productId, quantity);
            }

            // ✅ Save receipt rows to "Receipt" collection (one per order line)
            ReceiptDatabase receiptDb = new ReceiptDatabase();
            receiptDb.addReceiptForOrder(order);

            // Switch this SAME window to Receipt.fxml
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
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save your order. Check console.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int generateOrderId() {
        return (int) (Math.random() * 100000);
    }

    private int generateOrderNumber() {
        return (int) (Math.random() * 1000000);
    }

    private MongoCollection<Document> getMongoCollection() {
        return DatabaseAllev.getInstance().getDatabase().getCollection("orders");
    }
}
