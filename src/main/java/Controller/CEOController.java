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

    private Stage stage;
    private Scene scene;
    private Parent root;

    private final String CEO_SECRET_PASSWORD = "Xf9@Q!72&vP#Lm4^Rz8%TgC!5@Wd";

    private final CEODatabase db = new CEODatabase();

    @FXML
    private TextField CEOFirstField;

    @FXML
    private TextField CEOLastField;

    @FXML
    private PasswordField CEOPasswordField;

    @FXML
    private Label CEOTextField;


    @FXML
    public void backtoMenu(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/FXML/Alleviation.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void HandleLoginCEO(ActionEvent event) {

        String firstName = CEOFirstField.getText();
        String lastName = CEOLastField.getText();
        String password = CEOPasswordField.getText();

        // Input validation
        StringBuilder errors = new StringBuilder();

        if (firstName == null || firstName.isEmpty()) {
            errors.append("First name is required.\n");
        }

        if (lastName == null || lastName.isEmpty()) {
            errors.append("Last name is required.\n");
        }

        if (errors.length() > 0) {
            CEOTextField.setText(errors.toString());
            CEOTextField.setStyle("-fx-text-fill: red;");
            return;
        }


        Document existingCEO = db.findCEOByName(firstName, lastName);

        if (existingCEO != null) {
            // ✔ CEO recognized → instant access
            CEOTextField.setText("Welcome back, CEO " + firstName + " " + lastName + "!");
            CEOTextField.setStyle("-fx-text-fill: green;");

            // ------------ SWITCH SCENES HERE -------------
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/CEOInterface.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                CEOTextField.setText("Error loading CEO Interface.");
                CEOTextField.setStyle("-fx-text-fill: red;");
            }
            // ----------------------------------------------

            return;
        }



        if (password == null || password.isEmpty()) {
            CEOTextField.setText("CEO password is required for first-time setup.");
            CEOTextField.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!password.equals(CEO_SECRET_PASSWORD)) {
            CEOTextField.setText("Incorrect CEO setup password.");
            CEOTextField.setStyle("-fx-text-fill: red;");
            return;
        }


        boolean success = db.createCEO(firstName, lastName);

        if (success) {
            CEOTextField.setText("New CEO registered: " + firstName + " " + lastName);
            CEOTextField.setStyle("-fx-text-fill: green;");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/CEOInterface.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                CEOTextField.setText("Error loading CEO Interface.");
                CEOTextField.setStyle("-fx-text-fill: red;");
            }
    } else {
            CEOTextField.setText("Database error: Could not register new CEO.");
            CEOTextField.setStyle("-fx-text-fill: red;");
        }
    }
}
