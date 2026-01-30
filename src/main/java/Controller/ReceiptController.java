package Controller;

import OrderReceiptLogic.Order;
import OrderReceiptLogic.OrderLine;
import OrderReceiptLogic.Receipt;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ReceiptController {

    // ---- Header/meta labels ----
    @FXML private Label orderNumberLabel;
    @FXML private Label dateLabel;

    // ---- Customer labels ----
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;

    // ---- Notes + total ----
    @FXML private Label notesLabel;
    @FXML private Label totalLabel;

    // ---- Table ----
    @FXML private TableView<ReceiptRow> itemsTable;
    @FXML private TableColumn<ReceiptRow, String> colProduct;
    @FXML private TableColumn<ReceiptRow, Number> colQty;
    @FXML private TableColumn<ReceiptRow, String> colUnitPrice;
    @FXML private TableColumn<ReceiptRow, String> colSubtotal;

    private Receipt receipt;

    // Simple row model for TableView
    public static class ReceiptRow {
        private final SimpleStringProperty product;
        private final SimpleIntegerProperty qty;
        private final SimpleStringProperty unitPrice;
        private final SimpleStringProperty subtotal;

        public ReceiptRow(String product, int qty, String unitPrice, String subtotal) {
            this.product = new SimpleStringProperty(product);
            this.qty = new SimpleIntegerProperty(qty);
            this.unitPrice = new SimpleStringProperty(unitPrice);
            this.subtotal = new SimpleStringProperty(subtotal);
        }

        public String getProduct() { return product.get(); }
        public int getQty() { return qty.get(); }
        public String getUnitPrice() { return unitPrice.get(); }
        public String getSubtotal() { return subtotal.get(); }
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
        showReceipt();
    }

    private void showReceipt() {
        if (receipt == null) return;

        Order order = receipt.getOrder();

        // Order meta
        orderNumberLabel.setText(String.valueOf(order.getOrderNumber()));
        dateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        // Customer
        String fullName = (order.getFirstName() + " " + order.getLastName()).trim();
        fullNameLabel.setText(fullName.isBlank() ? "—" : fullName);
        emailLabel.setText(order.getEmail() == null ? "—" : order.getEmail());

        // Notes (your current Receipt class doesn't store notes; keep placeholder)
        notesLabel.setText("—");

        // Table columns
        colProduct.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduct()));
        colQty.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQty()));
        colUnitPrice.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUnitPrice()));
        colSubtotal.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSubtotal()));

        // Fill items
        var rows = FXCollections.<ReceiptRow>observableArrayList();
        double total = 0;

        for (OrderLine line : order.getOrderLines()) {
            // Display name: productId (+ denomination if gift card)
            String displayProduct = line.getProductId();
            if (line.getGiftAmount() != null) {
                displayProduct = displayProduct + " ($" + line.getGiftAmount() + ")";
            }

            double unit = line.getPriceAtOrder();
            double sub = unit * line.getQuantity();
            total += sub;

            rows.add(new ReceiptRow(
                    displayProduct,
                    line.getQuantity(),
                    String.format(Locale.US, "$%.2f", unit),
                    String.format(Locale.US, "$%.2f", sub)
            ));
        }

        itemsTable.setItems(rows);
        totalLabel.setText(String.format(Locale.US, "$%.2f", total));
    }

    // ---- Buttons from Receipt.fxml ----
    @FXML
    private void handleClose() {
        Stage stage = (Stage) itemsTable.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleBack() {
        // If you open Receipt in a new Stage, "Back" can just close it.
        handleClose();
    }

    @FXML
    private void handlePrint() {
        // Optional: implement printing later
        // For now, you can just close or show an alert
        // handleClose();
    }
}
