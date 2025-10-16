package lecbilling.mokopanemakhetha;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DashboardController {
    @FXML private Button fxManageCustomersButton;
    @FXML private Button fxCalculateBillsButton;
    @FXML private Button fxViewReportsButton;
    @FXML private Button fxLogoutButton;

    private CustomerManager customerManager;

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    @FXML
    private void handleManageCustomers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("customerTable.fxml"));
            Parent root = loader.load();

            CustomerTableController controller = loader.getController();
            controller.setCustomerManager(customerManager);

            Stage stage = (Stage) fxManageCustomersButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
            stage.setTitle("LEC Billing - Customer Management");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) fxLogoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("LEC Billing - Login");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}