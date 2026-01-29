package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AlleviationController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void MainMenuLog(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/FXML/login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void ToCEO(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/FXML/CEO.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void MainMenuReg(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/FXML/Register.fxml"));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private Button CloseButton;
    public void onCloseButtononAction(ActionEvent actionEvent) {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }
}
