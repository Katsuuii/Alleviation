package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import Database.UserDatabase;
import org.bson.types.ObjectId;

public class LoginController {

    @FXML private TextField Userlogfield;
    @FXML private PasswordField UserPassField;
    @FXML private Label LogErrorLabel;


    @FXML
    public void GotoRegister(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/Register.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            LogErrorLabel.setText("Register screen load failed.");
            LogErrorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }


    @FXML
    public void GotoMainMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/Alleviation.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            LogErrorLabel.setText("Main menu load failed.");
            LogErrorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }


    @FXML
    public void handleLogButton(ActionEvent event) {
        String username = Userlogfield.getText();
        String password = UserPassField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            LogErrorLabel.setText("Username and password are required.");
            LogErrorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        UserDatabase userDb;
        try {
            userDb = new UserDatabase();
        } catch (RuntimeException e) {
            LogErrorLabel.setText("Database connection error.");
            LogErrorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        org.bson.Document authenticatedUser = userDb.authenticateUser(username, password);

        if (authenticatedUser != null) {

            LogErrorLabel.setText("Login Successful!");
            LogErrorLabel.setStyle("-fx-text-fill: green;");

            ObjectId objectId = authenticatedUser.getObjectId("_id");
            int userId = objectId.hashCode(); // unique int for controllers

            String firstName = authenticatedUser.getString("firstName");
            String lastName  = authenticatedUser.getString("lastName");
            String email     = authenticatedUser.getString("email");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserInterface.fxml"));
                Parent root = loader.load();

                UserController uc = loader.getController();
                uc.setLoggedInUser(userId, username, firstName, lastName, email);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                LogErrorLabel.setText("Error loading User Interface.");
                LogErrorLabel.setStyle("-fx-text-fill: red;");
                e.printStackTrace();
            }

        } else {
            LogErrorLabel.setText("Invalid username or password.");
            LogErrorLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
