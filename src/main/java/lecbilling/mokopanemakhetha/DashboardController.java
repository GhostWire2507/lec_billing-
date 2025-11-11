package lecbilling.mokopanemakhetha;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lecbilling.mokopanemakhetha.service.CustomerService;
import lecbilling.mokopanemakhetha.service.BillingService;
import lecbilling.mokopanemakhetha.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Main dashboard controller with improved workflow and statistics
 */
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML private Label fxWelcomeLabel;
    @FXML private Label fxTotalCustomersLabel;
    @FXML private Label fxUnpaidBillsLabel;
    @FXML private Label fxOutstandingAmountLabel;
    @FXML private Label fxMonthlyRevenueLabel;

    @FXML private Button fxManageCustomersButton;
    @FXML private Button fxCalculateBillsButton;
    @FXML private Button fxViewReportsButton;
    @FXML private Button fxLogoutButton;

    private User currentUser;
    private final CustomerService customerService = CustomerService.getInstance();
    private final BillingService billingService = BillingService.getInstance();
    private final ReportService reportService = ReportService.getInstance();

    public void initialize() {
        logger.debug("Dashboard controller initialized");
        loadDashboardStatistics();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (fxWelcomeLabel != null) {
            fxWelcomeLabel.setText("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
        }
        logger.info("Dashboard loaded for user: {}", user.getUsername());
    }

    /**
     * Load and display dashboard statistics
     */
    private void loadDashboardStatistics() {
        try {
            Map<String, Object> stats = reportService.getDashboardStats();

            if (fxTotalCustomersLabel != null) {
                fxTotalCustomersLabel.setText(String.valueOf(stats.getOrDefault("totalCustomers", 0)));
            }

            if (fxUnpaidBillsLabel != null) {
                fxUnpaidBillsLabel.setText(String.valueOf(stats.getOrDefault("unpaidBills", 0)));
            }

            if (fxOutstandingAmountLabel != null) {
                double outstanding = (double) stats.getOrDefault("outstandingAmount", 0.0);
                fxOutstandingAmountLabel.setText(String.format("M%.2f", outstanding));
            }

            if (fxMonthlyRevenueLabel != null) {
                double monthly = (double) stats.getOrDefault("monthlyRevenue", 0.0);
                fxMonthlyRevenueLabel.setText(String.format("M%.2f", monthly));
            }

            logger.debug("Dashboard statistics loaded successfully");
        } catch (Exception e) {
            logger.error("Error loading dashboard statistics", e);
        }
    }

    @FXML
    private void handleManageCustomers() {
        logger.info("Opening Customer Management module");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("customerTable.fxml"));
            Parent root = loader.load();

            CustomerTableController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            openNewWindow(root, "Customer Management", 1200, 800);
        } catch (Exception e) {
            logger.error("Error opening Customer Management", e);
            PrintUtil.showAlert("Error", "Failed to open Customer Management module", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCalculateBills() {
        logger.info("Opening Billing module");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("billsDashboard.fxml"));
            Parent root = loader.load();

            BillsDashboardController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            openNewWindow(root, "Calculate & Manage Bills", 1200, 800);
        } catch (Exception e) {
            logger.error("Error opening Billing module", e);
            PrintUtil.showAlert("Error", "Failed to open Billing module", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleViewReports() {
        logger.info("Opening Reports module");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reports.fxml"));
            Parent root = loader.load();

            ReportsController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            openNewWindow(root, "Reports & Analytics", 1200, 800);
        } catch (Exception e) {
            logger.error("Error opening Reports module", e);
            PrintUtil.showAlert("Error", "Failed to open Reports module", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRefreshStats() {
        logger.debug("Refreshing dashboard statistics");
        loadDashboardStatistics();
        PrintUtil.showAlert("Refreshed", "Dashboard statistics updated", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleLogout() {
        logger.info("User {} logging out", currentUser != null ? currentUser.getUsername() : "unknown");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) fxLogoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("LEC Billing System - Login");
            stage.centerOnScreen();
        } catch (Exception e) {
            logger.error("Error during logout", e);
            PrintUtil.showAlert("Error", "Failed to logout properly", Alert.AlertType.ERROR);
        }
    }

    /**
     * Helper method to open modules in new windows (keeping dashboard accessible)
     */
    private void openNewWindow(Parent root, String title, int width, int height) {
        Stage newStage = new Stage();
        newStage.setTitle("LEC Billing System - " + title);
        newStage.setScene(new Scene(root, width, height));
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.centerOnScreen();
        newStage.show();

        logger.debug("Opened new window: {}", title);
    }
}