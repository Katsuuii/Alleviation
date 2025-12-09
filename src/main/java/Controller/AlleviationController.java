package Controller;

import javafx.application.Application;
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
    public static class Main extends Application {
        @Override
        public void start(Stage stage) {
            try {
                Parent root = FXMLLoader.load(
                        getClass().getResource("/FXML/Alleviation.fxml")
                );
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    public void MainMenuLog(ActionEvent event)throws IOException {
        root = FXMLLoader.load(getClass().getResource("/FXML/login.fxml"));
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
    @FXML
    protected void onCloseButtononAction(ActionEvent event) {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }


}

