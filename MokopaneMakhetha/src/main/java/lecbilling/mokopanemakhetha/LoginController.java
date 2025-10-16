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

public class LoginController {
    @FXML private TextField fxUsernameField;
    @FXML private PasswordField fxPasswordField;
    @FXML private Button fxLoginButton;

    private CustomerManager customerManager;

    public void initialize() {
        customerManager = new CustomerManager();
    }

    @FXML
    private void handleLogin() {
        String username = fxUsernameField.getText();
        String password = fxPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            PrintUtil.showAlert("Login Error", "Please enter both username and password", Alert.AlertType.ERROR);
            return;
        }

        if (customerManager.authenticateUser(username, password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                Parent root = loader.load();

                DashboardController controller = loader.getController();
                controller.setCustomerManager(customerManager);

                Stage stage = (Stage) fxLoginButton.getScene().getWindow();
                stage.setScene(new Scene(root, 1000, 700));
                stage.setTitle("LEC Billing - Dashboard");
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PrintUtil.showAlert("Login Failed", "Invalid username or password", Alert.AlertType.ERROR);
        }
    }
}