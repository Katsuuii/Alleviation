package Controller;

import Database.CartDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

public class CartController {

    @FXML private VBox cartList;
    @FXML private Label totalLabel;

    private int loggedInUserId;
    private String loggedInUsername;
    private String firstName;
    private String lastName;
    private String email;

    private final CartDatabase cartDb = new CartDatabase();

    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public void setLoggedInUser(int userId, String username, String firstName, String lastName, String email) {
        this.loggedInUserId = userId;
        this.loggedInUsername = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        refreshCartUI();
    }

    private void refreshCartUI() {
        cartList.getChildren().clear();

        List<Document> items = cartDb.getCart(loggedInUsername);

        double total = 0.0;

        for (Document d : items) {
            final String productId = d.getString("productId");
            final String title = d.getString("title");
            final String displayPrice = d.getString("displayPrice");
            final int qty = d.getInteger("qty", 1);

            Double numericPrice = parsePrice(displayPrice);
            if (numericPrice != null) total += numericPrice * qty;

            HBox card = new HBox(12);
            card.getStyleClass().add("card");
            card.setFillHeight(true);

            VBox info = new VBox(4);
            Label t = new Label(title);
            t.getStyleClass().add("card-title");

            Label sub = new Label("Price: " + displayPrice + "  |  Qty: " + qty);
            sub.getStyleClass().add("card-sub");
            info.getChildren().addAll(t, sub);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // qty controls
            Button minus = new Button("−");
            minus.getStyleClass().add("qty-btn");
            minus.setOnAction(e -> {
                cartDb.decrementQty(loggedInUsername, productId);
                refreshCartUI();
            });

            Button plus = new Button("+");
            plus.getStyleClass().add("qty-btn");
            plus.setOnAction(e -> {
                cartDb.incrementQty(loggedInUsername, productId);
                refreshCartUI();
            });

            Button remove = new Button("Remove");
            remove.getStyleClass().add("remove-btn");
            remove.setOnAction(e -> {
                cartDb.removeItem(loggedInUsername, productId);
                refreshCartUI();
            });

            HBox controls = new HBox(8, minus, plus, remove);

            card.getChildren().addAll(info, spacer, controls);
            cartList.getChildren().add(card);
        }

        totalLabel.setText("Total: $" + DF.format(total) + " (Gift cards not summed)");
    }

    private Double parsePrice(String displayPrice) {
        // Only sums if it’s a single numeric price like "$59.99"
        // Ignores ranges like "$10–$50"
        try {
            if (displayPrice == null) return null;
            String s = displayPrice.trim();
            if (s.contains("–") || s.contains("-")) return null;
            s = s.replace("$", "");
            return Double.parseDouble(s);
        } catch (Exception ignored) {
            return null;
        }
    }

    @FXML
    public void GoBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserInterface.fxml"));
            Parent root = loader.load();

            UserController userController = loader.getController();
            userController.setLoggedInUser(
                    loggedInUserId,
                    loggedInUsername,
                    firstName,
                    lastName,
                    email
            );

            Scene scene = new Scene(root);
            addStylesheetIfExists(scene, "/CSS/alleviation.css");
            // addStylesheetIfExists(scene, "/CSS/User-Dashboard.css"); // optional

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Checkout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Order.fxml"));
            Parent root = loader.load();

            OrderController orderController = loader.getController();
            orderController.setUserData(
                    loggedInUserId,
                    firstName,
                    lastName,
                    email
            );


            Scene scene = new Scene(root);
            addStylesheetIfExists(scene, "/CSS/alleviation.css");
            addStylesheetIfExists(scene, "/CSS/receipt.css"); // if your order uses receipt theme
            // addStylesheetIfExists(scene, "/CSS/order.css"); // if you have one

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ClearCart(ActionEvent event) {
        cartDb.clearCart(loggedInUsername);
        refreshCartUI();
    }

    // same helper style as your UserController
    private void addStylesheetIfExists(Scene scene, String resourcePath) {
        URL url = getClass().getResource(resourcePath);
        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
            System.out.println("Loaded CSS: " + resourcePath);
        } else {
            System.out.println("CSS NOT FOUND: " + resourcePath);
        }
    }
}
