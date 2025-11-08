import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Wishlist {
    private String id;
    private String productName;
    private String description;

    public Wishlist() {}

    public Wishlist(String id, String productName, String description) {
        this.id = id;
        this.productName = productName;
        this.description = description;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    private Stage stage;
    private Scene scene;
    private Parent root;






}