package Controller;

import Database.ProductRepository;
import ProductStuff.ProductRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.util.Optional;

public class ProductCheckController {

    private static final int LOW_STOCK_THRESHOLD = 5;

    // FXML
    @FXML private TableView<ProductRow> productTable;
    @FXML private TableColumn<ProductRow, String> colId;
    @FXML private TableColumn<ProductRow, String> colName;
    @FXML private TableColumn<ProductRow, Double> colPrice;
    @FXML private TableColumn<ProductRow, Integer> colQty;
    @FXML private TableColumn<ProductRow, Integer> colSales;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> productCombo;
    @FXML private Spinner<Integer> restockSpinner;
    @FXML private Label statusLabel;
    @FXML private Label actionLabel;

    // Data
    private final ProductRepository repo = new ProductRepository();
    private final ObservableList<ProductRow> masterRows = FXCollections.observableArrayList();
    private FilteredList<ProductRow> filteredRows;

    @FXML
    public void initialize() {
        setupTable();
        setupFiltering();
        setupControls();
        loadProducts();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSales.setCellValueFactory(new PropertyValueFactory<>("sales"));

        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colQty.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer qty, boolean empty) {
                super.updateItem(qty, empty);
                if (empty || qty == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(String.valueOf(qty));
                setStyle(qty < LOW_STOCK_THRESHOLD
                        ? "-fx-background-color: rgba(255,107,138,0.35); -fx-font-weight: 900;"
                        : "");
            }
        });
    }

    private void setupFiltering() {
        filteredRows = new FilteredList<>(masterRows, p -> true);
        productTable.setItems(filteredRows);

        searchField.textProperty().addListener((obs, oldText, newText) -> applyFilter(newText));
    }

    private void applyFilter(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();

        filteredRows.setPredicate(row -> {
            if (q.isEmpty()) return true;
            return row.getId().toLowerCase().contains(q) || row.getName().toLowerCase().contains(q);
        });

        statusLabel.setText("Showing " + filteredRows.size() + " of " + masterRows.size() + " product(s).");
    }

    private void setupControls() {
        restockSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
    }

    private void loadProducts() {
        masterRows.clear();
        productCombo.getItems().clear();
        actionLabel.setText("");

        for (Document doc : repo.findAll()) {
            ProductRow row = toRow(doc);
            masterRows.add(row);
            productCombo.getItems().add(row.getId() + " | " + row.getName());
        }

        applyFilter(searchField.getText());
    }

    private ProductRow toRow(Document doc) {
        String id = doc.getString("_id");
        String name = doc.getString("name");
        double price = ((Number) doc.get("price")).doubleValue();
        int qty = ((Number) doc.get("quantity")).intValue();
        int sales = ((Number) doc.get("sales")).intValue();
        return new ProductRow(id, name, price, qty, sales);
    }

    // ===== Buttons =====
    @FXML
    private void handleRename() {
        actionLabel.setStyle("-fx-text-fill: #D6002B; -fx-font-weight: 700;");
        actionLabel.setText("");

        String selected = productCombo.getValue();
        if (selected == null || selected.isBlank()) {
            actionLabel.setText("Please select a product first.");
            return;
        }

        String productId = selected.split("\\|")[0].trim();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename Product");
        dialog.setHeaderText("Rename product: " + productId);
        dialog.setContentText("New name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String newName = result.get().trim();
        if (newName.isEmpty()) {
            actionLabel.setText("Name cannot be empty.");
            return;
        }

        // ✅ Allow duplicates: no check needed
        repo.renameProduct(productId, newName);

        actionLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: 800;");
        actionLabel.setText("Renamed to: " + newName);

        loadProducts();
    }


    @FXML
    private void handleRefresh() {
        loadProducts();
    }

    @FXML
    private void handleRestock() {
        actionLabel.setStyle("-fx-text-fill: #D6002B; -fx-font-weight: 700;");
        actionLabel.setText("");

        String productId = getSelectedProductId();
        if (productId == null) return;

        int amount = restockSpinner.getValue();
        repo.restock(productId, amount);

        successMsg("Stock increased by " + amount + ".");
        loadProducts();
    }

    @FXML
    private void handleDelete() {
        actionLabel.setStyle("-fx-text-fill: #D6002B; -fx-font-weight: 700;");
        actionLabel.setText("");

        String productId = getSelectedProductId();
        if (productId == null) return;

        if (!confirmDelete(productId)) return;

        repo.deleteById(productId);
        successMsg("Deleted product: " + productId);
        loadProducts();
    }

    private String getSelectedProductId() {
        String selected = productCombo.getValue();
        if (selected == null || selected.isBlank()) {
            actionLabel.setText("Please select a product first.");
            return null;
        }
        return selected.split("\\|")[0].trim();
    }

    private boolean confirmDelete(String productId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete this product?");
        confirm.setContentText("This will permanently remove:\n" + productId);

        Optional<ButtonType> result = confirm.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void successMsg(String msg) {
        actionLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: 800;");
        actionLabel.setText(msg);
    }

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
}
