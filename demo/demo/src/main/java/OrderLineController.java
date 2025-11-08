import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;

public class OrderLineController {

    @FXML private TextField productNameField;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;
    @FXML private TableView<OrderLine> orderTable;
    @FXML private TableColumn<OrderLine, String> idColumn;
    @FXML private TableColumn<OrderLine, String> productColumn;
    @FXML private TableColumn<OrderLine, Integer> quantityColumn;
    @FXML private TableColumn<OrderLine, Double> priceColumn;

    private final ObservableList<OrderLine> orderLines = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        orderTable.setItems(orderLines);
    }

    @FXML
    private void addOrderLine(ActionEvent event) {
        String product = productNameField.getText();
        String quantityText = quantityField.getText();
        String priceText = priceField.getText();

        if (product.isEmpty() || quantityText.isEmpty() || priceText.isEmpty()) return;

        try {
            int quantity = Integer.parseInt(quantityText);
            double price = Double.parseDouble(priceText);

            OrderLine newLine = new OrderLine(String.valueOf(orderLines.size() + 1), product, quantity, price);
            orderLines.add(newLine);

            productNameField.clear();
            quantityField.clear();
            priceField.clear();
        } catch (NumberFormatException e) {
            System.err.println("Invalid number input: " + e.getMessage());
        }
    }

    @FXML
    private void deleteOrderLine(ActionEvent event) {
        OrderLine selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            orderLines.remove(selected);
        }
    }
}
