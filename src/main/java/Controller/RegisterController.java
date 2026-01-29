package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import org.mindrot.jbcrypt.BCrypt; // Import BCrypt

public class RegisterController {

    @FXML
    private TextField RegFirstnametextField;
    @FXML
    private TextField RegLastnameTextField;
    @FXML
    private TextField RegEmailTextField;
    @FXML
    private TextField RegUsernameTextField;
    @FXML
    private PasswordField RegPasswordField;
    @FXML
    private PasswordField RegConfirmPasswordField;
    @FXML
    private Label RegErrorLabel;

    @FXML
    public void GotoMainMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/Alleviation.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            if (RegErrorLabel != null) {
                RegErrorLabel.setText("Main menu load failed.");
                RegErrorLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    public void handleRegisterButton(ActionEvent event) {
        StringBuilder errors = getStringBuilder();

        if (!errors.isEmpty()) {
            RegErrorLabel.setText(errors.toString());
            RegErrorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String firstname = RegFirstnametextField.getText();
        String lastname = RegLastnameTextField.getText();
        String email = RegEmailTextField.getText();
        String username = RegUsernameTextField.getText();
        String password = RegPasswordField.getText();


        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());


        Database.UserDatabase userDb;
        try {
            userDb = new Database.UserDatabase();
        } catch (RuntimeException e) {
            RegErrorLabel.setText("Database connection error. Check server status.");
            RegErrorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (userDb.emailExists(email)) {
            RegErrorLabel.setText("Email already exists!");
            RegErrorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (userDb.usernameExists(username)) {
            RegErrorLabel.setText("Username already taken!");
            RegErrorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Pass the HASHED password to createUser
        if (!userDb.createUser(firstname, lastname, email, username, hashedPassword)) {
            RegErrorLabel.setText("Registration failed: Database write error. Check console for details!");
            RegErrorLabel.setStyle("-fx-text-fill: red;");
            return;
        }


        RegErrorLabel.setText("Registration successful!");
        RegErrorLabel.setStyle("-fx-text-fill: green;");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            RegErrorLabel.setText("Error loading next scene.");
            RegErrorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private StringBuilder getStringBuilder() {
        StringBuilder errors = new StringBuilder();
        String email = RegEmailTextField.getText();
        String firstname = RegFirstnametextField.getText();
        String lastname = RegLastnameTextField.getText();
        String Username = RegUsernameTextField.getText();
        String Password = RegPasswordField.getText();
        String ConfirmPass = RegConfirmPasswordField.getText();
        if (firstname == null || firstname.isEmpty()) {
            errors.append("First name is required.\n");
        }
        if (lastname == null || lastname.isEmpty()) {
            errors.append("Last name is required.\n");
        }
        if (email == null || email.isEmpty()) {
            errors.append("Email is required.\n");
        } else if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            errors.append("Invalid email format.\n");
        }
        if (Username == null || Username.isEmpty()) {
            errors.append("Username is required.\n");
        }
        if (Password == null || Password.isEmpty()) {
            errors.append("Password is required.\n");
        }
        if (ConfirmPass == null || ConfirmPass.isEmpty()) {
            errors.append("Confirm password is required.\n");
        }
        if (Password != null && ConfirmPass != null &&
                !Password.equals(ConfirmPass)) {
            errors.append("Passwords do not match.\n");
        }

        return errors;
    }

}