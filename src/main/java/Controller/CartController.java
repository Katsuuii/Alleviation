package Controller;

import Database.CartDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    // -------------------- SESSION --------------------
    public void setLoggedInUser(int userId, String username, String firstName, String lastName, String email) {
        this.loggedInUserId = userId;
        this.loggedInUsername = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        refreshCartUI();
    }

    // -------------------- UI --------------------
    private void refreshCartUI() {
        cartList.getChildren().clear();

        List<Document> items = cartDb.getCart(loggedInUsername);
        double total = 0.0;

        for (Document d : items) {
            final String productId = d.getString("productId");
            final String title = d.getString("title");
            final String displayPrice = d.getString("displayPrice");
            final String type = d.getString("type");
            final int qty = d.getInteger("qty", 1);
            final Integer unitAmount = d.getInteger("unitAmount");

            HBox card = new HBox(12);
            card.getStyleClass().add("card");
            card.setFillHeight(true);

            VBox info = new VBox(4);

            Label t = new Label(title);
            t.getStyleClass().add("card-title");

            Label sub = new Label("Price: " + displayPrice + "  |  Qty: " + qty);
            sub.getStyleClass().add("card-sub");

            info.getChildren().addAll(t, sub);

            // -------------------- Gift Card Amount Picker --------------------
            if ("GIFT_CARD".equals(type)) {
                int[] mm = parseMinMax(displayPrice);
                int min = mm[0];
                int max = mm[1];

                int current = (unitAmount == null || unitAmount < min) ? min : unitAmount;
                int step = (min == max) ? 1 : 5;

                Spinner<Integer> amountSpinner = new Spinner<>();
                amountSpinner.setValueFactory(
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, current, step)
                );

                amountSpinner.valueProperty().addListener((obs, oldV, newV) -> {
                    if (newV == null) return;
                    cartDb.setGiftCardAmount(loggedInUsername, productId, newV);
                    refreshCartUI();
                });

                info.getChildren().add(new Label("Amount: $" + current));
                info.getChildren().add(amountSpinner);

                total += current * qty;
            } else {
                Double numericPrice = parsePrice(displayPrice);
                if (numericPrice != null) {
                    total += numericPrice * qty;
                }
            }

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // -------------------- Qty Controls --------------------
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

        totalLabel.setText("Total: $" + DF.format(total));
    }

    // -------------------- NAV --------------------
    @FXML
    public void GoBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserInterface.fxml"));
            Parent root = loader.load();

            UserController uc = loader.getController();
            uc.setLoggedInUser(loggedInUserId, loggedInUsername, firstName, lastName, email);

            Scene scene = new Scene(root);
            addStylesheetIfExists(scene, "/CSS/alleviation.css");

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

            OrderController oc = loader.getController();

            // ✅ pass username too
            oc.setUserData(
                    loggedInUserId,
                    loggedInUsername,
                    firstName,
                    lastName,
                    email
            );


            Scene scene = new Scene(root);
            addStylesheetIfExists(scene, "/CSS/alleviation.css");
            // addStylesheetIfExists(scene, "/CSS/order.css"); // if you have one

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Checkout");
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

    // -------------------- HELPERS --------------------
    private Double parsePrice(String displayPrice) {
        try {
            if (displayPrice == null) return null;
            if (displayPrice.contains("–") || displayPrice.contains("-")) return null;
            return Double.parseDouble(displayPrice.replace("$", "").trim());
        } catch (Exception e) {
            return null;
        }
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

    private void addStylesheetIfExists(Scene scene, String path) {
        URL url = getClass().getResource(path);
        if (url != null) scene.getStylesheets().add(url.toExternalForm());
    }
}
