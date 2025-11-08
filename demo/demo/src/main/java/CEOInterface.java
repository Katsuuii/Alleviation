import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class CEOInterface {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    public void Orderlineviewing(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("alleviation/orderline-view.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
