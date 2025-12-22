package Controller;

import Database.ProductDatabase;
import ProductStuff.Product;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.Optional;

public class ProdImplementController {

    private final ProductDatabase db = new ProductDatabase();

    @FXML
    public void AddProduct() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Create a new inventory record:");

        ButtonType addButtonType = new ButtonType("Add to Inventory", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField qtyField = new TextField();
        qtyField.setPromptText("Initial Stock");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Stock:"), 0, 2);
        grid.add(qtyField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == addButtonType) {
            try {
                // Generate the object
                Product newProduct = new Product(
                        nameField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(qtyField.getText())
                );

                // Send to MongoDB
                db.addProduct(newProduct);

                // Feedback
                showSuccess(newProduct.getName(), newProduct.getId());

            } catch (NumberFormatException e) {
                showError("Please enter valid numeric values for Price and Stock.");
            }
        }
    }

    private void showSuccess(String name, String id) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Product '" + name + "' saved with ID: " + id);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }
}