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

public class RegisterController {
    private Stage stage;
    private Scene scene;
    private Parent root;

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
            root = FXMLLoader.load(getClass().getResource("alleviation/Alleviation.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
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
        } else {
            RegErrorLabel.setText("");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("alleviation/login.fxml"));
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
    }

    private StringBuilder getStringBuilder() {
        StringBuilder errors = new StringBuilder();

        if (RegFirstnametextField == null || RegFirstnametextField.getText().isEmpty()) {
            errors.append("First name is required.\n");
        }
        if (RegLastnameTextField == null || RegLastnameTextField.getText().isEmpty()) {
            errors.append("Last name is required.\n");
        }
        if (RegEmailTextField == null || RegEmailTextField.getText().isEmpty()) {
            errors.append("Email is required.\n");
        }
        if (RegUsernameTextField == null || RegUsernameTextField.getText().isEmpty()) {
            errors.append("Username is required.\n");
        }
        if (RegPasswordField == null || RegPasswordField.getText().isEmpty()) {
            errors.append("Password is required.\n");
        }
        if (RegConfirmPasswordField == null || RegConfirmPasswordField.getText().isEmpty()) {
            errors.append("Confirm password is required.\n");
        }
        if (RegPasswordField != null && RegConfirmPasswordField != null &&
                !RegPasswordField.getText().equals(RegConfirmPasswordField.getText())) {
            errors.append("Passwords do not match.\n");
        }
        return errors;
    }
}
