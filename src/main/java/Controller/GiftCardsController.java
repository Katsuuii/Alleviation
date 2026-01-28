package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GiftCardsController {

    private int loggedInUserId;
    private String loggedInFirstName;
    private String loggedInLastName;
    private String loggedInUserEmail;

    // ✅ Match GamesController: store logged-in user
    public void setLoggedInUser(int userId, String firstName, String lastName, String email) {
        this.loggedInUserId = userId;
        this.loggedInFirstName = firstName;
        this.loggedInLastName = lastName;
        this.loggedInUserEmail = email;
    }

    // ✅ Gift card display names (what shows in Order form)
    private static final String ROBLOX_GC = "Roblox Gift Card";
    private static final String MINECRAFT_GC = "Minecraft Gift Card";
    private static final String STEAM_GC = "Steam Gift Card";
    private static final String VISA_GC = "Visa Gift Card";

    // -------------------- Open Order Form (same as GamesController) --------------------
    private void openOrderForm(String productName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Order.fxml"));
            Parent root = loader.load();
            OrderController orderController = loader.getController();
            orderController.setUserData(loggedInUserId, loggedInFirstName, loggedInLastName, loggedInUserEmail);
            orderController.prefillProduct(productName);
            orderController.enableGiftCardMode(productName);


            Stage stage = new Stage();
            stage.setTitle("Order: " + productName);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------- Purchase Button Handlers --------------------
    @FXML
    private void handlePurchaseRoblox() {
        openOrderForm(ROBLOX_GC);
    }

    @FXML
    private void handlePurchaseMinecraft() {
        openOrderForm(MINECRAFT_GC);
    }

    @FXML
    private void handlePurchaseSteam() {
        openOrderForm(STEAM_GC);
    }

    @FXML
    private void handlePurchaseVisa() {
        openOrderForm(VISA_GC);
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
