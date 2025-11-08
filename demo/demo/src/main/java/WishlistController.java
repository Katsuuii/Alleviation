import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class WishlistController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    public void GotoBack(ActionEvent event) {

        try {
            root = FXMLLoader.load(getClass().getResource("alleviation/UserInterface.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            }
    }
    @FXML private TextField productNameField;
    @FXML private TextField descriptionField;
    @FXML private TableView<Wishlist> wishlistTable;

    @FXML private TableColumn<Wishlist, String> idColumn;
    @FXML private TableColumn<Wishlist, String> productColumn;
    @FXML private TableColumn<Wishlist, String> descriptionColumn;

    private final WishlistDAOImpl dao = new WishlistDAOImpl();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId()));
        productColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        refreshTable();
    }

    @FXML
    public void addWishlist(ActionEvent event) {
        Wishlist w = new Wishlist(
                String.valueOf(System.currentTimeMillis()),
                productNameField.getText(),
                descriptionField.getText()
        );
        dao.insert(w);
        refreshTable();
        clearFields();
    }

    @FXML
    public void deleteWishlist(ActionEvent event) {
        Wishlist selected = wishlistTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.delete(selected.getId());
            refreshTable();
        }
    }

    private void refreshTable() {
        wishlistTable.getItems().setAll(dao.findAll());
    }

    private void clearFields() {
        productNameField.clear();
        descriptionField.clear();
    }


}
