package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GamesController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    public void Loggingout(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/FXML/Alleviation.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }@FXML
    public void GobackUser(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/FXML/UserInterface.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }}
