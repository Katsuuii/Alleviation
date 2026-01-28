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

    private int loggedInUserId;
    private String loggedInFirstName;
    private String loggedInLastName;
    private String loggedInUserEmail;

    // ✅ Official display names
    private static final String HONKAI_NAME = "Honkai: Star Rail";
    private static final String ZZZ_NAME = "Zenless Zone Zero";
    private static final String OVERCOOKED_NAME = "Overcooked";

    // -------------------- Set logged-in user --------------------
    public void setLoggedInUser(int userId, String firstName, String lastName, String email) {
        this.loggedInUserId = userId;
        this.loggedInFirstName = firstName;
        this.loggedInLastName = lastName;
        this.loggedInUserEmail = email;
    }

    // -------------------- Purchase Button Handlers --------------------
    @FXML
    private void handlePurchaseHonkai() {
        openOrderForm(HONKAI_NAME);
    }

    @FXML
    private void handlePurchaseZZZ() {
        openOrderForm(ZZZ_NAME);
    }

    @FXML
    private void handlePurchaseOvercooked() {
        openOrderForm(OVERCOOKED_NAME);
    }

    // -------------------- Open Order Form --------------------
    private void openOrderForm(String productName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Order.fxml"));
            Parent root = loader.load();

            OrderController orderController = loader.getController();
            orderController.setUserData(loggedInUserId, loggedInFirstName, loggedInLastName, loggedInUserEmail);

            if (productName != null) {
                orderController.prefillProduct(productName);
            }

            Stage stage = new Stage();
            stage.setTitle(productName != null ? "Order: " + productName : "Order Form");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------- Scene Navigation --------------------
    @FXML
    public void GobackUser(ActionEvent event) {
        switchSceneWithUser(event, "/FXML/UserInterface.fxml");
    }

    @FXML
    public void Loggingout(ActionEvent event) {
        switchScene(event, "/FXML/Alleviation.fxml");
    }

    // -------------------- Helper Methods --------------------
    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchSceneWithUser(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserController uc) {
                uc.setLoggedInUser(loggedInUserId, loggedInFirstName, loggedInLastName, loggedInUserEmail);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
