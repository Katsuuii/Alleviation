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
import javafx.stage.Stage;
import javafx.stage.Modality;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Map;

public class GiftCardsController {

    @FXML private AnchorPane Root; // add fx:id="Root" in GiftCards.fxml

    private final UserDatabase userDb = new UserDatabase();
    private final ProductDatabase productDb = new ProductDatabase();
    private final CartDatabase cartDb = new CartDatabase();

    private int loggedInUserId;
    private String loggedInFirstName;
    private String loggedInLastName;
    private String loggedInUserEmail;
    private String loggedInUsername;
    private String username;
    // -------------------- Set logged-in user --------------------
    public void setLoggedInUser(int userId, String firstName, String lastName, String email) {
        setLoggedInUser(userId, null, firstName, lastName, email);
    }

    public void setLoggedInUser(int userId, String username, String firstName, String lastName, String email) {
        this.loggedInUserId = userId;
        this.loggedInUsername = username;
        this.loggedInFirstName = firstName;
        this.loggedInLastName = lastName;
        this.loggedInUserEmail = email;
    }

    private static final String ROBLOX_GC = "Roblox Gift Card";
    private static final String MINECRAFT_GC = "Minecraft Gift Card";
    private static final String STEAM_GC = "Steam Gift Card";
    private static final String VISA_GC = "Visa Gift Card";

    private record CardInfo(String title, String range, String imagePath, String desc) {}

    private final Map<String, CardInfo> cards = Map.of(
            "ROBLOX", new CardInfo(
                    ROBLOX_GC, "$10–$50", "/Pictures/GiftCard_Roblox.png",
                    "Roblox credits you can use for items, games, and subscriptions."
            ),
            "MINECRAFT", new CardInfo(
                    MINECRAFT_GC, "$30", "/Pictures/Minecraft.png",
                    "Minecraft gift card (fixed value)."
            ),
            "STEAM", new CardInfo(
                    STEAM_GC, "$5–$20", "/Pictures/Steam.png",
                    "Steam Wallet funds for games, DLC, and in-app purchases."
            ),
            "VISA", new CardInfo(
                    VISA_GC, "$5–$50", "/Pictures/Visa.png",
                    "Visa prepaid gift card usable for wider online purchases."
            )
    );

    // ✅ This now matches your reverted OrderController exactly
    private void openOrderForm(String productName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Order.fxml"));
            Parent root = loader.load();

            OrderController orderController = loader.getController();

            // ✅ matches your reverted OrderController: (int, firstName, lastName, email)
            orderController.setUserData(
                    loggedInUserId,
                    loggedInFirstName,
                    loggedInLastName,
                    loggedInUserEmail
            );


            Stage stage = new Stage();
            stage.setTitle("Order: " + productName);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // -------------------- Popup open --------------------
    @FXML
    private void openGiftCard(ActionEvent event) {
        if (!(event.getSource() instanceof Button btn)) return;

        String id = (String) btn.getUserData(); // ROBLOX / MINECRAFT / STEAM / VISA
        CardInfo info = cards.get(id);
        if (info != null) showCardPopup(info);
    }

    private void showCardPopup(CardInfo info) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);

        if (Root != null && Root.getScene() != null) {
            dialog.initOwner(Root.getScene().getWindow());
        }

        dialog.setResizable(false);
        dialog.setTitle("Buy: " + info.title());

        ImageView cover = new ImageView(new Image(getClass().getResource(info.imagePath()).toExternalForm()));
        cover.setFitWidth(220);
        cover.setFitHeight(220);
        cover.setPreserveRatio(true);
        cover.setSmooth(true);

        Label title = new Label(info.title());
        title.getStyleClass().add("gc-dialog-title");

        Label desc = new Label(info.desc());
        desc.getStyleClass().add("gc-dialog-desc");
        desc.setWrapText(true);

        Label range = new Label("Amount range: " + info.range());
        range.getStyleClass().add("gc-range");

        // -------------------- Wishlist heart --------------------
        String productId = productDb.getProductIdByName(info.title());
        Button heart = new Button();
        heart.getStyleClass().add("wishlist-btn");

        if (productId == null) {
            heart.setText("♡");
            heart.setDisable(true);
        } else {
            refreshHeart(heart, productId);
            heart.setOnAction(ev -> {
                toggleWishlist(productId);
                refreshHeart(heart, productId);
            });
        }

        Button addToCart = new Button("Add to Cart");
        addToCart.getStyleClass().add("giftcards-primary-btn");
        addToCart.setOnAction(ev -> {
            if (loggedInUsername == null || loggedInUsername.isBlank()) return;
            if (productId == null || productId.isBlank()) return;

            cartDb.addToCart(
                    loggedInUsername,
                    productId,
                    info.title(),
                    info.range(),     // displayPrice stores "$10–$50" etc
                    "GIFT_CARD"
            );

            dialog.close();
        });

        Button orderNow = new Button("Order Now");
        orderNow.getStyleClass().add("mini-btn");
        orderNow.setOnAction(ev -> {
            dialog.close();
            openOrderForm(info.title());
        });

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("mini-btn");
        cancel.setOnAction(ev -> dialog.close());

        HBox actions = new HBox(10, heart, addToCart, orderNow, cancel);

        VBox layout = new VBox(12, cover, title, desc, range, actions);
        layout.getStyleClass().add("gc-dialog");

        Scene scene = new Scene(layout);

        if (getClass().getResource("/CSS/alleviation.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/CSS/alleviation.css").toExternalForm());
        }
        if (getClass().getResource("/CSS/giftcards.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/CSS/giftcards.css").toExternalForm());
        }

        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Optional direct purchase buttons (if you still use them in GiftCards.fxml)
    @FXML private void handlePurchaseRoblox() { openOrderForm(ROBLOX_GC); }
    @FXML private void handlePurchaseMinecraft() { openOrderForm(MINECRAFT_GC); }
    @FXML private void handlePurchaseSteam() { openOrderForm(STEAM_GC); }
    @FXML private void handlePurchaseVisa() { openOrderForm(VISA_GC); }

    // -------------------- Navigation --------------------
    @FXML
    public void GobackUser(ActionEvent event) {
        switchSceneWithUser(event, "/FXML/UserInterface.fxml");
    }

    @FXML
    public void Loggingout(ActionEvent event) {
        switchScene(event, "/FXML/Alleviation.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchSceneWithUser(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserController uc) {
                uc.setLoggedInUser(loggedInUserId, loggedInUsername, loggedInFirstName, loggedInLastName, loggedInUserEmail);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------- Wishlist helpers --------------------
    private void toggleWishlist(String productId) {
        if (loggedInUsername == null || loggedInUsername.isBlank()) return;
        if (productId == null || productId.isBlank()) return;

        boolean on = userDb.isWishlisted(loggedInUsername, productId);
        if (on) userDb.removeFromWishlist(loggedInUsername, productId);
        else userDb.addToWishlist(loggedInUsername, productId);
    }

    private void refreshHeart(Button heart, String productId) {
        if (heart == null) return;

        boolean hasUser = loggedInUsername != null && !loggedInUsername.isBlank();
        if (!hasUser || productId == null || productId.isBlank()) {
            heart.setText("♡");
            heart.setDisable(true);
            return;
        }

        boolean on = userDb.isWishlisted(loggedInUsername, productId);
        heart.setText(on ? "♥" : "♡");
        heart.setDisable(false);
    }
}
