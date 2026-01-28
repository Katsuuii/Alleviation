package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class UserController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML private Label FullNameLabel;
    @FXML private Label EmailLabel;

    private int loggedInUserId;
    private String loggedInFirstName;
    private String loggedInLastName;
    private String loggedInUserEmail;

    /**
     * Called by LoginController or previous scene to pass logged-in user info
     */
    public void setLoggedInUser(int userId, String firstName, String lastName, String email) {
        this.loggedInUserId = userId;
        this.loggedInFirstName = firstName;
        this.loggedInLastName = lastName;
        this.loggedInUserEmail = email;

        // Update labels immediately
        if (FullNameLabel != null) FullNameLabel.setText(firstName + " " + lastName);
        if (EmailLabel != null) EmailLabel.setText(email);
    }

    // ---------------- Scene Navigation ----------------

    @FXML
    public void GotoGames(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Games.fxml"));
            Parent root = loader.load();

            GamesController gamesController = loader.getController();
            gamesController.setLoggedInUser(loggedInUserId, loggedInFirstName, loggedInLastName, loggedInUserEmail);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void GotoGiftCards(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/FXML/GiftCards.fxml")));
            Parent root = loader.load();

            // If GiftCardsController needs user info, you can pass it here similarly
            // GiftCardsController giftController = loader.getController();
            // giftController.setLoggedInUser(...);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Loggingout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FXML/Alleviation.fxml")));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void GotoMainMenu(ActionEvent event) {
        try {
            // Reload the same UserInterface.fxml with current user info
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/FXML/UserInterface.fxml")));
            Parent root = loader.load();

            UserController userController = loader.getController();
            userController.setLoggedInUser(loggedInUserId, loggedInFirstName, loggedInLastName, loggedInUserEmail);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
