package Controller;

import Database.DatabaseAllev;
import Database.OrderDatabase;
import Database.ProductDatabase;
import OrderReceiptLogic.Order;
import OrderReceiptLogic.OrderLine;
import com.mongodb.client.MongoCollection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    // Must match DB names (or your getProductIdByName must be ignore-case)
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

        // Default gift amount
        giftAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 5));
    }

    public void setUserData(int userId, String firstName, String lastName, String email) {
        this.userId = userId;
        this.userFirstName = firstName;
        this.userLastName = lastName;
        this.userEmail = email;

        FirstandLast.setText(firstName + " " + lastName);
        EmailAddress.setText(email);
    }

    public void prefillProduct(String productName) {
        this.selectedProduct = productName;
        Product.setText(productName);

        // If not gift card mode, keep picker hidden
        if (!isGiftCard) {
            giftCardBox.setVisible(false);
            giftCardBox.setManaged(false);
        }
    }

    // ✅ Call this from GiftCardsController to enable choosing card type + amount
    public void enableGiftCardMode(String defaultCardName) {
        isGiftCard = true;

        giftCardBox.setVisible(true);
        giftCardBox.setManaged(true);

        // Fill the dropdown
        giftCardTypeCombo.getItems().setAll(ROBLOX, MINECRAFT, STEAM, VISA);

        // When user changes card, update product + amount range
        giftCardTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            selectedProduct = newVal;
            Product.setText(newVal);
            applyGiftRangeFor(newVal);
        });

        // Choose default selection
        if (defaultCardName != null && giftCardTypeCombo.getItems().contains(defaultCardName)) {
            giftCardTypeCombo.setValue(defaultCardName);
        } else {
            giftCardTypeCombo.setValue(ROBLOX);
        }

        // Apply initial range
        applyGiftRangeFor(giftCardTypeCombo.getValue());
    }

    private void applyGiftRangeFor(String cardName) {
        int min, max, step;

        switch (cardName) {
            case ROBLOX -> { min = 5; max = 50; step = 5; }
            case STEAM  -> { min = 5;  max = 20; step = 5; }
            case VISA   -> { min = 5;  max = 50; step = 5; }
            case MINECRAFT -> { min = 30; max = 30; step = 1; } // fixed value
            default     -> { min = 5;  max = 50; step = 5; }
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

            double priceAtOrder = isGiftCard
                    ? giftAmountSpinner.getValue()
                    : productDb.getPriceByProductId(productId);

            OrderLine line = new OrderLine(productId, quantity, priceAtOrder);
            List<OrderLine> lines = new ArrayList<>();
            lines.add(line);

            int id = generateOrderId();
            int orderNumber = generateOrderNumber();

            Order order = new Order(id, orderNumber, userId, lines, userFirstName, userLastName, userEmail);

            OrderDatabase orderDb = new OrderDatabase(getMongoCollection());
            orderDb.addOrder(order, notes);

            productDb.recordSale(productId, quantity);

            showAlert(Alert.AlertType.INFORMATION, "Order Submitted!", "Your order has been saved.");

            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();

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
