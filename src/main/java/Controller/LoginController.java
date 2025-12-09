package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import java.io.IOException;
import Database.UserDatabase; // Import the UserDatabase class

public class LoginController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField Userlogfield;

    @FXML
    private PasswordField UserPassField;

    @FXML
    private Label LogErrorLabel;

    @FXML
    public void GotoRegister(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/FXML/Register.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            if (LogErrorLabel != null) {
                LogErrorLabel.setText("Register screen load failed.");
                LogErrorLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    public void GotoMainMenu(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/FXML/Alleviation.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            if (LogErrorLabel != null) {
                LogErrorLabel.setText("Main menu load failed.");
                LogErrorLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    public void handleLogButton(ActionEvent event) {
        StringBuilder errors = getStringBuilder();

        if (!errors.isEmpty()) {
            LogErrorLabel.setText(errors.toString());
            LogErrorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String username = Userlogfield.getText();
        String password = UserPassField.getText();

        UserDatabase userDb;
        try {
            userDb = new UserDatabase();
        } catch (RuntimeException e) {
            // Handle fatal connection failure
            LogErrorLabel.setText("Database connection error. Try again later.");
            LogErrorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // 💡 CRITICAL IMPLEMENTATION: Attempt secure authentication
        org.bson.Document authenticatedUser = userDb.authenticateUser(username, password);

        if (authenticatedUser != null) {
            // Authentication SUCCESS!
            LogErrorLabel.setText("Login Successful!");
            LogErrorLabel.setStyle("-fx-text-fill: green;");

            // Proceed to the next scene (UserInterface.fxml)
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserInterface.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                LogErrorLabel.setText("Error loading next scene.");
                LogErrorLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            // Authentication FAILURE!
            LogErrorLabel.setText("Invalid Username or Password.");
            LogErrorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private StringBuilder getStringBuilder() {
        StringBuilder errors = new StringBuilder();
        if (Userlogfield == null || Userlogfield.getText().isEmpty()) {
            errors.append("Username is required.\n");
        }
        if (UserPassField == null || UserPassField.getText().isEmpty()) {
            errors.append("Password is required.\n");
        }
        return errors;
    }
}
