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
            root = FXMLLoader.load(getClass().getResource("alleviation/Register.fxml"));
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
            root = FXMLLoader.load(getClass().getResource("alleviation/Alleviation.fxml"));
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
        } else {
            LogErrorLabel.setText("");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("alleviation/UserInterface.fxml"));
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
