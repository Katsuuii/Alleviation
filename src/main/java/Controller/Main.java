package Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var url = getClass().getResource("/FXML/Alleviation.fxml");
        System.out.println(url);
        FXMLLoader loader = new FXMLLoader(url);

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Alleviation");
        stage.show();
    }

}
