package Controller;

import Database.ProductDatabase;
import ProductStuff.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Optional;

public class ProdImplementController {

    private final ProductDatabase db = new ProductDatabase();
    private final DecimalFormat money = new DecimalFormat("#0.00");

    @FXML
    public void AddProduct() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Product");
        dialog.setHeaderText("Create a new inventory record");


        ButtonType addButtonType = new ButtonType("Add to Inventory", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);


        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(18, 18, 10, 18));


        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Overcooked");

        TextField priceField = new TextField();
        priceField.setPromptText("e.g. 26.49");

        TextField qtyField = new TextField();
        qtyField.setPromptText("e.g. 10");


        nameField.setPrefWidth(220);
        priceField.setPrefWidth(220);
        qtyField.setPrefWidth(220);


        priceField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*(\\.\\d{0,2})?") ? change : null;
        }));

        qtyField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        }));


        Label nameLbl = new Label("Name");
        Label priceLbl = new Label("Price ($)");
        Label qtyLbl = new Label("Stock");

        grid.add(nameLbl, 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(priceLbl, 0, 1);
        grid.add(priceField, 1, 1);

        grid.add(qtyLbl, 0, 2);
        grid.add(qtyField, 1, 2);

        dialog.getDialogPane().setContent(grid);


        Button addBtn = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addBtn.setDisable(true);

        Runnable validate = () -> {
            boolean nameOk = nameField.getText() != null && !nameField.getText().trim().isEmpty();
            boolean priceOk = priceField.getText() != null && !priceField.getText().trim().isEmpty();
            boolean qtyOk = qtyField.getText() != null && !qtyField.getText().trim().isEmpty();

            // extra: must parse > 0
            boolean parsedOk = false;
            if (nameOk && priceOk && qtyOk) {
                try {
                    double p = Double.parseDouble(priceField.getText());
                    int q = Integer.parseInt(qtyField.getText());
                    parsedOk = p > 0 && q >= 0;
                } catch (Exception ignored) {
                    parsedOk = false;
                }
            }
            addBtn.setDisable(!(nameOk && priceOk && qtyOk && parsedOk));
        };

        nameField.textProperty().addListener((obs, o, n) -> validate.run());
        priceField.textProperty().addListener((obs, o, n) -> validate.run());
        qtyField.textProperty().addListener((obs, o, n) -> validate.run());

        // Focus
        dialog.setOnShown(e -> nameField.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == addButtonType) {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText());
                int qty = Integer.parseInt(qtyField.getText());

                Product newProduct = new Product(name, price, qty);
                db.addProduct(newProduct);

                showSuccess(name, newProduct.getId(), price, qty);

            } catch (Exception e) {
                showError("Something went wrong while saving.\nPlease check the inputs and try again.");
            }
        }
    }
    @FXML private Button CEOButton1;
    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/CEOInterface.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSuccess(String name, String id, double price, int qty) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Product added to inventory");
        alert.setContentText(
                "Name: " + name +
                        "\nPrice: $" + money.format(price) +
                        "\nStock: " + qty +
                        "\n\nSaved ID: " + id
        );
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Unable to add product");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
