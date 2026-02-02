package Controller;

import Database.CartDatabase;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WishlistController {

    private static final Map<String, String> GIFT_CARD_RANGES = Map.of(
            "Roblox Gift Card", "$10–$50",
            "Minecraft Gift Card", "$30",
            "Steam Gift Card", "$5–$20",
            "Visa Gift Card", "$5–$50"
    );

    @FXML private VBox wishlistContainer;
    @FXML private Label emptyLabel;

    private int loggedInUserId;
    private String loggedInUsername;
    private String loggedInFirstName;
    private String loggedInLastName;
    private String loggedInUserEmail;

    private final UserDatabase userDb = new UserDatabase();
    private final ProductDatabase productDb = new ProductDatabase();
    private final CartDatabase cartDb = new CartDatabase();

    public void setLoggedInUser(int userId, String username, String firstName, String lastName, String email) {
        this.loggedInUserId = userId;
        this.loggedInUsername = username;
        this.loggedInFirstName = firstName;
        this.loggedInLastName = lastName;
        this.loggedInUserEmail = email;

        refreshWishlist();
    }

    @FXML
    private void initialize() {
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

            final String finalProductId = productId;
            final String finalName = name;

// Compute texts first
            String rangeText = GIFT_CARD_RANGES.get(finalName);
            String priceText = "";

            if (rangeText != null) {
                priceText = rangeText;
            } else {
                try {
                    double price = productDb.getPriceByProductId(finalProductId);
                    priceText = String.format("$%.2f", price);
                } catch (Exception ignored) {}
            }

// Now freeze them for lambdas
            final String finalRangeText = rangeText;
            final String finalPriceText = priceText;


            Label title = new Label(finalName);
            title.getStyleClass().add("wishlist-title");

            Label priceLabel = new Label(priceText);
            priceLabel.getStyleClass().add("wishlist-price");

            Button addToCart = new Button("Add to Cart");
            addToCart.getStyleClass().add("primary-btn");
            addToCart.setOnAction(e -> {
                cartDb.addToCart(
                        loggedInUsername,
                        finalProductId,
                        finalName,
                        (finalRangeText != null ? finalRangeText : finalPriceText),
                        (finalRangeText != null ? "GIFT_CARD" : "GAME")
                );

                if (finalRangeText != null) {
                    int min = parseMin(finalRangeText);
                    cartDb.setGiftCardAmount(loggedInUsername, finalProductId, min);
                }
            });

            Button remove = new Button("Remove");
            remove.getStyleClass().add("mini-btn");
            remove.setOnAction(e -> {
                userDb.removeFromWishlist(loggedInUsername, finalProductId);
                refreshWishlist();
            });

            HBox row = new HBox(12, title, priceLabel, addToCart, remove);
            row.getStyleClass().add("wishlist-row");

            wishlistContainer.getChildren().add(row);
        }
    }

    private int parseMin(String range) {
        // "$10–$50" or "$30"
        try {
            String s = range.replace("$", "").trim();
            if (s.contains("–")) return Integer.parseInt(s.split("–")[0].trim());
            if (s.contains("-")) return Integer.parseInt(s.split("-")[0].trim());
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    @FXML
    public void GobackUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserInterface.fxml"));
            Parent root = loader.load();

            UserController uc = loader.getController();
            uc.setLoggedInUser(loggedInUserId, loggedInUsername, loggedInFirstName, loggedInLastName, loggedInUserEmail);

            Scene scene = new Scene(root);
            var cssUrl = getClass().getResource("/CSS/alleviation.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
