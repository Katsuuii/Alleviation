package Controller;

import Database.ProductDatabase;
import Database.UserDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class WishlistController {

    @FXML private VBox wishlistContainer;
    @FXML private Label emptyLabel;

    private int loggedInUserId;
    private String loggedInUsername;
    private String loggedInFirstName;
    private String loggedInLastName;
    private String loggedInUserEmail;

    private final UserDatabase userDb = new UserDatabase();
    private final ProductDatabase productDb = new ProductDatabase();

    // Called by UserController to pass logged-in user info
    public void setLoggedInUser(int loggedInUserId,
                                String loggedInUsername,
                                String loggedInFirstName,
                                String loggedInLastName,
                                String loggedInUserEmail) {

        this.loggedInUserId = loggedInUserId;
        this.loggedInUsername = loggedInUsername;
        this.loggedInFirstName = loggedInFirstName;
        this.loggedInLastName = loggedInLastName;
        this.loggedInUserEmail = loggedInUserEmail;

        refreshWishlist();
    }

    @FXML
    private void initialize() {
        // UI may load before user info is set
        if (emptyLabel != null) {
            emptyLabel.setText("Loading wishlist...");
            emptyLabel.setVisible(true);
        }
    }

    private void refreshWishlist() {
        if (wishlistContainer == null || emptyLabel == null) return;

        wishlistContainer.getChildren().clear();

        if (loggedInUsername == null || loggedInUsername.isBlank()) {
            emptyLabel.setText("No user logged in.");
            emptyLabel.setVisible(true);
            return;
        }

        List<String> ids = userDb.getWishlist(loggedInUsername);

        if (ids == null || ids.isEmpty()) {
            emptyLabel.setText("Your wishlist is empty.");
            emptyLabel.setVisible(true);
            return;
        }

        emptyLabel.setVisible(false);

        for (String productId : ids) {
            String name = productDb.getProductNameById(productId);
            if (name == null) name = "(Unknown Product)";

            final String finalName = name;
            final String finalProductId = productId;

            double price = 0.0;
            try {
                price = productDb.getPriceByProductId(finalProductId);
            } catch (Exception ignored) {}

            Label title = new Label(finalName);
            title.getStyleClass().add("wishlist-title");

            String rangeText = GIFT_CARD_RANGES.get(finalName);

            String priceText;
            if (rangeText != null) {
                // ✅ Gift card: show range
                priceText = rangeText;
            } else if (price > 0) {
                // ✅ Normal product: show price
                priceText = String.format("$%.2f", price);
            } else {
                priceText = "";
            }

            Label priceLabel = new Label(priceText);
            priceLabel.getStyleClass().add("wishlist-price");


            Button buy = new Button("Buy");
            buy.getStyleClass().add("primary-btn");
            buy.setOnAction(e -> openOrderForm(finalName));

            Button remove = new Button("Remove");
            remove.getStyleClass().add("mini-btn");
            // remove.getStyleClass().add("danger-btn");

            remove.setOnAction(e -> {
                userDb.removeFromWishlist(loggedInUsername, finalProductId);
                refreshWishlist();
            });

            HBox row = new HBox(12, title, priceLabel, buy, remove);
            row.getStyleClass().add("wishlist-row");

            wishlistContainer.getChildren().add(row);
        }
    }

    private void openOrderForm(String productName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Order.fxml"));
            Parent root = loader.load();

            OrderController orderController = loader.getController();
            orderController.setUserData(loggedInUserId, loggedInFirstName, loggedInLastName, loggedInUserEmail);

            if (productName != null) {
                orderController.prefillProduct(productName);
            }

            Stage stage = new Stage();
            stage.setTitle("Order: " + productName);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Back button in Wishlist.fxml calls this
    @FXML
    public void GobackUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserInterface.fxml"));
            Parent root = loader.load();

            UserController uc = loader.getController();
            uc.setLoggedInUser(
                    loggedInUserId,
                    loggedInUsername,
                    loggedInFirstName,
                    loggedInLastName,
                    loggedInUserEmail
            );

            Scene scene = new Scene(root);

            // Optional: load your main css so it keeps theme
            var cssUrl = getClass().getResource("/CSS/alleviation.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }private static final java.util.Map<String, String> GIFT_CARD_RANGES = java.util.Map.of(
            "Roblox Gift Card", "$10–$50",
            "Minecraft Gift Card", "$30",
            "Steam Gift Card", "$5–$20",
            "Visa Gift Card", "$5–$50"
    );
}
