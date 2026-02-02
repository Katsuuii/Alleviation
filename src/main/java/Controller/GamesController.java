package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import Database.ProductDatabase;
import Database.UserDatabase;

public class GamesController {

    @FXML private AnchorPane Root; // add fx:id="Root" in Games.fxml

    private int loggedInUserId;
    private String loggedInFirstName;
    private String loggedInLastName;
    private String loggedInUserEmail;
    private String loggedInUsername;

    private final UserDatabase userDb = new UserDatabase();
    private final ProductDatabase productDb = new ProductDatabase();

    // ✅ Official display names (used for Order prefill)
    private static final String TARKOV_NAME = "Escape From Tarkov";
    private static final String ELDEN_NAME  = "Elden Ring";
    private static final String OVERCOOKED_NAME = "Overcooked";

    // --- Popup game data ---
    private record GameInfo(String title, String priceText, String imagePath, String description) {}

    private final Map<String, GameInfo> games = Map.of(
            "TARKOV", new GameInfo(
                    TARKOV_NAME,
                    "$50.00",
                    "/Pictures/Tarkov.jpg",
                    "Hardcore FPS extraction shooter. Plan raids, loot, and escape."
            ),
            "ELDEN", new GameInfo(
                    ELDEN_NAME,
                    "$59.99",
                    "/Pictures/Elden_Ring.jpg",
                    "Open-world action RPG. Explore, fight bosses, and build your character."
            ),
            "OVERCOOKED", new GameInfo(
                    OVERCOOKED_NAME,
                    "$26.49",
                    "/Pictures/Overcooked Image.jpg",
                    "Co-op cooking chaos. Coordinate, serve orders, survive the kitchen."
            )
    );

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

    // -------------------- NEW: "View" button handler (popup) --------------------
    @FXML
    private void openGame(ActionEvent event) {
        if (!(event.getSource() instanceof Button btn)) return;

        String id = (String) btn.getUserData();
        GameInfo info = games.get(id);
        if (info != null) showGamePopup(info, id);
    }

    private void showGamePopup(GameInfo info, String id) {
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
        title.getStyleClass().add("dialog-title");

        Label desc = new Label(info.description());
        desc.getStyleClass().add("dialog-desc");
        desc.setWrapText(true);

        Label price = new Label("Price: " + info.priceText());
        price.getStyleClass().add("game-price");

        // --- Wishlist heart ---
        String productId = productDb.getProductIdByName(info.title());
        Button heart = new Button();
        heart.getStyleClass().add("wishlist-btn");

        if (productId == null) {
            // If product name doesn't exist in Product collection, don't crash
            heart.setText("♡");
            heart.setDisable(true);
        } else {
            refreshHeart(heart, productId);

            heart.setOnAction(ev -> {
                toggleWishlist(productId);
                refreshHeart(heart, productId);
            });
        }


        Button buy = new Button("Purchase");
        buy.getStyleClass().add("games-primary-btn");
        buy.setOnAction(ev -> {
            purchaseById(id);
            dialog.close();
        });

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("mini-btn");
        cancel.setOnAction(ev -> dialog.close());

        // put heart beside buttons
        HBox actions = new HBox(10, heart, buy, cancel);

        VBox layout = new VBox(12, cover, title, desc, price, actions);
        layout.getStyleClass().add("game-dialog");

        Scene scene = new Scene(layout);


        if (getClass().getResource("/CSS/alleviation.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/CSS/alleviation.css").toExternalForm());
        }
        if (getClass().getResource("/CSS/games.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/CSS/games.css").toExternalForm());
        }

        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void purchaseById(String id) {
        switch (id) {
            case "TARKOV" -> handlePurchaseHonkai();
            case "ELDEN" -> handlePurchaseZZZ();
            case "OVERCOOKED" -> handlePurchaseOvercooked();
        }
    }


    @FXML
    private void handlePurchaseHonkai() {
        openOrderForm(TARKOV_NAME);
    }

    @FXML
    private void handlePurchaseZZZ() {
        openOrderForm(ELDEN_NAME);
    }

    @FXML
    private void handlePurchaseOvercooked() {
        openOrderForm(OVERCOOKED_NAME);
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
            stage.setTitle(productName != null ? "Order: " + productName : "Order Form");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
    // -------------------- Wishlist helpers --------------------
    private void toggleWishlist(String productId) {
        if (loggedInUsername == null || loggedInUsername.isBlank()) return;
        if (productId == null || productId.isBlank()) return;

        boolean on = userDb.isWishlisted(loggedInUsername, productId);

        if (on) {
            userDb.removeFromWishlist(loggedInUsername, productId);
        } else {
            userDb.addToWishlist(loggedInUsername, productId);
        }
    }

    private void refreshHeart(Button heart, String productId) {
        if (heart == null) return;

        boolean hasUser = loggedInUsername != null && !loggedInUsername.isBlank();
        if (!hasUser || productId == null || productId.isBlank()) {
            heart.setText("♡");
            heart.setDisable(true);
            heart.getStyleClass().remove("wishlist-on");
            return;
        }

        boolean on = userDb.isWishlisted(loggedInUsername, productId);
        heart.setText(on ? "♥" : "♡");

        if (on) {
            if (!heart.getStyleClass().contains("wishlist-on")) {
                heart.getStyleClass().add("wishlist-on");
            }
        } else {
            heart.getStyleClass().remove("wishlist-on");
        }

        heart.setDisable(false);
    }


}