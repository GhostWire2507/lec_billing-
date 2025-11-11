package lecbilling.mokopanemakhetha;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lecbilling.mokopanemakhetha.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the login screen
 */
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML private TextField fxUsernameField;
    @FXML private PasswordField fxPasswordField;
    @FXML private Button fxLoginButton;

    private final AuthenticationService authService = AuthenticationService.getInstance();

    public void initialize() {
        logger.debug("Login controller initialized");
    }

    @FXML
    private void handleLogin() {
        String username = fxUsernameField.getText().trim();
        String password = fxPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            logger.warn("Login attempt with empty credentials");
            PrintUtil.showAlert("Login Error", "Please enter both username and password", Alert.AlertType.ERROR);
            return;
        }

        User user = authService.authenticateUser(username, password);

        if (user != null) {
            try {
                logger.info("User {} logged in successfully", username);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                Parent root = loader.load();

                DashboardController controller = loader.getController();
                controller.setCurrentUser(user);

                Stage stage = (Stage) fxLoginButton.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 800));
                stage.setTitle("LEC Billing System - Dashboard");
                stage.centerOnScreen();
            } catch (Exception e) {
                logger.error("Error loading dashboard", e);
                PrintUtil.showAlert("Error", "Failed to load dashboard. Please try again.", Alert.AlertType.ERROR);
            }
        } else {
            logger.warn("Failed login attempt for username: {}", username);
            PrintUtil.showAlert("Login Failed", "Invalid username or password", Alert.AlertType.ERROR);
        }
    }
}