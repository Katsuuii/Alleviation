import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CEOController extends Application {
    private Scene scene;
    private Parent root;
    @FXML
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("alleviation/CEOInterface.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}
}
