package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import model.Order;
import model.OrderLine;

public class OrderController {

    @FXML
    private ListView<String> orderLinesList;

    @FXML
    private Label totalItemsLabel;

    @FXML
    private Label totalAmountLabel;

    private Order order;

    private ObservableList<String> lineItems;

    @FXML
    public void initialize() {
        lineItems = FXCollections.observableArrayList();
        orderLinesList.setItems(lineItems);
    }

    public void setOrder(Order order) {
        this.order = order;
        refreshView();
    }

    private void refreshView() {
        lineItems.clear();
        for (OrderLine line : order.getLines()) {
            lineItems.add(line.toString());
        }
        totalItemsLabel.setText("Total items: " + order.getTotalItems());
        totalAmountLabel.setText(String.format("Total amount: %.2f", order.getTotalAmount()));
    }
}
