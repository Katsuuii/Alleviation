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

public class GamesController {

    @FXML private AnchorPane Root; // add fx:id="Root" in Games.fxml

    private int loggedInUserId;
    private String loggedInFirstName;
    private String loggedInLastName;
    private String loggedInUserEmail;

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
        this.loggedInUserId = userId;
        this.loggedInFirstName = firstName;
        this.loggedInLastName = lastName;
        this.loggedInUserEmail = email;
    }

    // -------------------- NEW: "View" button handler (popup) --------------------
    @FXML
    private void openGame(ActionEvent event) {
        if (!(event.getSource() instanceof Button btn)) return;

        String id = (String) btn.getUserData(); // HONKAI / ZZZ / OVERCOOKED
        GameInfo info = games.get(id);
        if (info != null) showGamePopup(info, id);
    }

    private void showGamePopup(GameInfo info, String id) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);

        // If Root is available, make it owned by your current window (prevents behind-window popups)
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

        Button buy = new Button("Purchase");
        buy.getStyleClass().add("games-primary-btn");
        buy.setOnAction(ev -> {
            purchaseById(id);   // ✅ uses your existing Order flow
            dialog.close();
        });

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("mini-btn");
        cancel.setOnAction(ev -> dialog.close());

        HBox actions = new HBox(10, buy, cancel);

        VBox layout = new VBox(12, cover, title, desc, price, actions);
        layout.getStyleClass().add("game-dialog");

        Scene scene = new Scene(layout);

        // load your existing CSS + optional games.css if you added it
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

    // -------------------- Purchase Button Handlers --------------------
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

    // -------------------- Open Order Form --------------------
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

    // -------------------- Scene Navigation --------------------
    @FXML
    public void GobackUser(ActionEvent event) {
        switchSceneWithUser(event, "/FXML/UserInterface.fxml");
    }

    @FXML
    public void Loggingout(ActionEvent event) {
        switchScene(event, "/FXML/Alleviation.fxml");
    }

    // -------------------- Helper Methods --------------------
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
                uc.setLoggedInUser(loggedInUserId, loggedInFirstName, loggedInLastName, loggedInUserEmail);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
