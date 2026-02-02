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
import java.net.URL;
import java.util.Objects;

public class UserController {

    @FXML private Label FullNameLabel;
    @FXML private Label EmailLabel;

    private int loggedInUserId;
    private String loggedInFirstName;
    private String loggedInLastName;
    private String loggedInUserEmail;
    private String loggedInUsername;

    /**
     * Called by LoginController or previous scene to pass logged-in user info
     */
    public void setLoggedInUser(int userId, String firstName, String lastName, String email) {
        setLoggedInUser(userId, null, firstName, lastName, email);
    }

    public void setLoggedInUser(int userId, String username, String firstName, String lastName, String email) {
        this.loggedInUserId = userId;
        this.loggedInUsername = username;
        this.loggedInFirstName = firstName;
        this.loggedInLastName = lastName;
        this.loggedInUserEmail = email;

        if (FullNameLabel != null) FullNameLabel.setText(firstName + " " + lastName);
        if (EmailLabel != null) EmailLabel.setText(email);
    }

    @FXML
    public void GotoGames(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Games.fxml"));
            Parent root = loader.load();

            GamesController gamesController = loader.getController();
            gamesController.setLoggedInUser(
                    loggedInUserId,
                    loggedInUsername,
                    loggedInFirstName,
                    loggedInLastName,
                    loggedInUserEmail
            );

            Scene scene = new Scene(root);

            // Optional: load global css if you want games theme consistent
            addStylesheetIfExists(scene, "/CSS/alleviation.css");
            addStylesheetIfExists(scene, "/CSS/games.css");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void GotoGiftCards(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GiftCards.fxml"));
            Parent root = loader.load();

            GiftCardsController gc = loader.getController();
            gc.setLoggedInUser(
                    loggedInUserId,
                    loggedInUsername,
                    loggedInFirstName,
                    loggedInLastName,
                    loggedInUserEmail
            );

            Scene scene = new Scene(root);

            addStylesheetIfExists(scene, "/CSS/alleviation.css");
            addStylesheetIfExists(scene, "/CSS/giftcards.css");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void GotoWishlist(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Wishlist.fxml"));
            Parent root = loader.load();

            WishlistController wc = loader.getController();
            wc.setLoggedInUser(
                    loggedInUserId,
                    loggedInUsername,
                    loggedInFirstName,
                    loggedInLastName,
                    loggedInUserEmail
            );

            Scene scene = new Scene(root);

            // ✅ Load your global theme if you want
            addStylesheetIfExists(scene, "/CSS/alleviation.css");

            // ✅ Load wishlist.css and PRINT if missing
            addStylesheetIfExists(scene, "/CSS/wishlist.css");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Loggingout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FXML/Alleviation.fxml")));
            Scene scene = new Scene(root);

            addStylesheetIfExists(scene, "/CSS/alleviation.css");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void GotoMainMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/FXML/UserInterface.fxml")));
            Parent root = loader.load();

            UserController userController = loader.getController();
            userController.setLoggedInUser(
                    loggedInUserId,
                    loggedInUsername,
                    loggedInFirstName,
                    loggedInLastName,
                    loggedInUserEmail
            );

            Scene scene = new Scene(root);

            addStylesheetIfExists(scene, "/CSS/alleviation.css");
            // If you have a dashboard css file, you can load it too:
            // addStylesheetIfExists(scene, "/CSS/User-Dashboard.css");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------- CSS Helper --------------------
    private void addStylesheetIfExists(Scene scene, String resourcePath) {
        URL url = getClass().getResource(resourcePath);
        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
            System.out.println("Loaded CSS: " + resourcePath);
        } else {
            System.out.println("CSS NOT FOUND: " + resourcePath + "  (Check src/main/Resources path + filename)");
        }
    }
}
