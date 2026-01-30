package Controller;

import Database.CEODatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import java.io.IOException;

public class CEOController {

    @FXML private TextField CEOFirstField;
    @FXML private TextField CEOLastField;
    @FXML private PasswordField CEOPasswordField;
    @FXML private Label CEOTextField;

    private final String CEO_SECRET_PASSWORD = "Xf9@Q!72&vP#Lm4^Rz8%TgC!5@Wd";
    private final CEODatabase db = new CEODatabase();

    @FXML
    public void HandleLoginCEO(ActionEvent event) {
        String firstName = CEOFirstField.getText().trim();
        String lastName = CEOLastField.getText().trim();
        String password = CEOPasswordField.getText();


        if (firstName.isEmpty() || lastName.isEmpty()) {
            updateStatus("First and Last name are required.", "red");
            return;
        }


        Document existingCEO = db.findCEOByName(firstName, lastName);

        if (existingCEO != null) {

            navigateToInterface(event, firstName, lastName);
        } else {

            if (password.isEmpty()) {
                updateStatus("Setup password required for new CEO.", "red");
                return;
            }

            if (password.equals(CEO_SECRET_PASSWORD)) {
                if (db.createCEO(firstName, lastName)) {
                    navigateToInterface(event, firstName, lastName);
                } else {
                    updateStatus("Database error during registration.", "red");
                }
            } else {
                updateStatus("Incorrect setup password.", "red");
            }
        }
    }

    private void navigateToInterface(ActionEvent event, String fName, String lName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/CEOInterface.fxml"));
            Parent root = loader.load();


            CEOInterface controller = loader.getController();
            controller.ShowLabel(fName, lName);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("UI Error: Could not load interface.", "red");
        }
    }

    private void updateStatus(String message, String color) {
        CEOTextField.setText(message);
        CEOTextField.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    public void backtoMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/Alleviation.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}