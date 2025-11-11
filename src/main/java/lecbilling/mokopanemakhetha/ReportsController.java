package lecbilling.mokopanemakhetha;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lecbilling.mokopanemakhetha.service.CustomerService;
import lecbilling.mokopanemakhetha.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ReportsController {
    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @FXML private Label fxTotalCustomersLabel;
    @FXML private Label fxTotalRevenueLabel;
    @FXML private Label fxAvgBillLabel;
    @FXML private Label fxHighestUsageLabel;
    @FXML private BarChart<String, Number> fxUsageChart;
    @FXML private BarChart<String, Number> fxRevenueChart;
    @FXML private Button fxBackButton;
    @FXML private Button fxPrintReportButton;

    private final CustomerService customerService = CustomerService.getInstance();
    private final ReportService reportService = ReportService.getInstance();
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        logger.info("Reports dashboard initialized for user: {}", user.getUsername());
        initializeDashboard();
    }

    private void initializeDashboard() {
        try {
            updateStatsCards();
            initializeCharts();
            logger.info("Reports dashboard initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing reports dashboard", e);
            PrintUtil.showAlert("Error", "Failed to load reports: " + e.getMessage(),
                javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    private void updateStatsCards() {
        try {
            Map<String, Object> stats = reportService.getDashboardStats();

            int totalCustomers = (int) stats.getOrDefault("totalCustomers", 0);
            double totalRevenue = (double) stats.getOrDefault("totalRevenue", 0.0);
            double avgBill = totalCustomers > 0 ? totalRevenue / totalCustomers : 0;

            Customer highestUsageCustomer = customerService.getAllCustomers().stream()
                    .max((c1, c2) -> Double.compare(c1.getElectricityUsage(), c2.getElectricityUsage()))
                    .orElse(null);

            fxTotalCustomersLabel.setText(String.valueOf(totalCustomers));
            fxTotalRevenueLabel.setText(String.format("M%.2f", totalRevenue));
            fxAvgBillLabel.setText(String.format("M%.2f", avgBill));
            fxHighestUsageLabel.setText(highestUsageCustomer != null ?
                    String.format("%.1f kWh - %s", highestUsageCustomer.getElectricityUsage(), highestUsageCustomer.getName()) : "N/A");

            logger.debug("Stats updated: {} customers, M{} revenue", totalCustomers, totalRevenue);
        } catch (Exception e) {
            logger.error("Error updating stats cards", e);
        }
    }

    private void initializeCharts() {
        try {
            // Usage Chart
            XYChart.Series<String, Number> usageSeries = new XYChart.Series<>();
            usageSeries.setName("Electricity Usage (kWh)");

            for (Customer customer : customerService.getAllCustomers()) {
                if (customer.getElectricityUsage() > 0) {
                    usageSeries.getData().add(new XYChart.Data<>(
                            customer.getName(),
                            customer.getElectricityUsage()
                    ));
                }
            }
            fxUsageChart.getData().clear();
            fxUsageChart.getData().add(usageSeries);

            // Revenue Chart
            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            revenueSeries.setName("Bill Amount (M)");

            for (Customer customer : customerService.getAllCustomers()) {
                if (customer.getBillAmount() > 0) {
                    revenueSeries.getData().add(new XYChart.Data<>(
                            customer.getName(),
                            customer.getBillAmount()
                    ));
                }
            }
            fxRevenueChart.getData().clear();
            fxRevenueChart.getData().add(revenueSeries);

            logger.debug("Charts initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing charts", e);
        }
    }

    @FXML
    private void handlePrintReport() {
        try {
            // Generate printable report
            StringBuilder report = new StringBuilder();
            report.append("LEC Billing System - Report\n");
            report.append("===========================\n\n");
            report.append("Total Customers: ").append(fxTotalCustomersLabel.getText()).append("\n");
            report.append("Total Revenue: ").append(fxTotalRevenueLabel.getText()).append("\n");
            report.append("Average Bill: ").append(fxAvgBillLabel.getText()).append("\n\n");

            report.append("Customer Details:\n");
            for (Customer customer : customerService.getAllCustomers()) {
                report.append(String.format("- %s (ID: %s): %.1f kWh = %s\n",
                        customer.getName(), customer.getCustomerId(),
                        customer.getElectricityUsage(),
                        String.format("M%.2f", customer.getBillAmount())));
            }

            logger.info("Report generated by user: {}", currentUser.getUsername());
            PrintUtil.showAlert("Report Generated",
                    "Report is ready for printing:\n\n" + report.toString(),
                    javafx.scene.control.Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            logger.error("Error generating report", e);
            PrintUtil.showAlert("Error", "Failed to generate report: " + e.getMessage(),
                javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) fxBackButton.getScene().getWindow();
            stage.close();
            logger.info("Reports dashboard closed");
        } catch (Exception e) {
            logger.error("Error closing reports dashboard", e);
        }
    }
}