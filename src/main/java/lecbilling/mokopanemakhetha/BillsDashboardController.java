package lecbilling.mokopanemakhetha;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lecbilling.mokopanemakhetha.model.BillingCalculation;
import lecbilling.mokopanemakhetha.service.BillingService;
import lecbilling.mokopanemakhetha.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BillsDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(BillsDashboardController.class);

    @FXML private TableView<Customer> fxCustomerTable;
    @FXML private TableColumn<Customer, String> fxCustomerIdColumn;
    @FXML private TableColumn<Customer, String> fxNameColumn;
    @FXML private TableColumn<Customer, Double> fxCurrentUsageColumn;
    @FXML private TableColumn<Customer, Double> fxCurrentBillColumn;
    @FXML private TextField fxNewUsageField;
    @FXML private Label fxCalculatedBillLabel;
    @FXML private Label fxCustomerNameLabel;
    @FXML private Button fxCalculateButton;
    @FXML private Button fxUpdateBillButton;
    @FXML private Button fxPrintReceiptButton;
    @FXML private Button fxBackButton;
    @FXML private Label fxTotalBillsLabel;
    @FXML private Label fxTotalRevenueLabel;

    private final CustomerService customerService = CustomerService.getInstance();
    private final BillingService billingService = BillingService.getInstance();
    private Customer selectedCustomer;
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        logger.info("Bills dashboard initialized for user: {}", user.getUsername());
        initializeTable();
        updateStats();
    }

    private void initializeTable() {
        try {
            fxCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            fxNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            fxCurrentUsageColumn.setCellValueFactory(new PropertyValueFactory<>("electricityUsage"));
            fxCurrentBillColumn.setCellValueFactory(new PropertyValueFactory<>("billAmount"));

            fxCustomerTable.setItems(customerService.getAllCustomers());
            logger.info("Bills dashboard table initialized with {} customers", fxCustomerTable.getItems().size());

            fxCustomerTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> handleCustomerSelection(newValue)
            );
        } catch (Exception e) {
            logger.error("Error initializing bills dashboard table", e);
            PrintUtil.showAlert("Error", "Failed to load customers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleCustomerSelection(Customer customer) {
        this.selectedCustomer = customer;
        if (customer != null) {
            fxCustomerNameLabel.setText("Selected: " + customer.getName());
            fxNewUsageField.setText(String.valueOf(customer.getElectricityUsage()));
            fxCalculatedBillLabel.setText(String.format("M%.2f", customer.getBillAmount()));
        } else {
            fxCustomerNameLabel.setText("No customer selected");
            fxNewUsageField.clear();
            fxCalculatedBillLabel.setText("M0.00");
        }
    }

    private void updateStats() {
        try {
            double totalRevenue = customerService.getAllCustomers().stream()
                    .mapToDouble(Customer::getBillAmount)
                    .sum();
            int billedCustomers = (int) customerService.getAllCustomers().stream()
                    .filter(c -> c.getBillAmount() > 0)
                    .count();

            fxTotalBillsLabel.setText(String.valueOf(billedCustomers));
            fxTotalRevenueLabel.setText(String.format("M%.2f", totalRevenue));
            logger.debug("Stats updated: {} billed customers, M{} total revenue", billedCustomers, totalRevenue);
        } catch (Exception e) {
            logger.error("Error updating stats", e);
        }
    }

    @FXML
    private void handleCalculate() {
        if (selectedCustomer == null) {
            PrintUtil.showAlert("Selection Error", "Please select a customer first", javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }

        try {
            double newUsage = Double.parseDouble(fxNewUsageField.getText().trim());
            if (newUsage < 0) {
                PrintUtil.showAlert("Validation Error", "Usage cannot be negative", javafx.scene.control.Alert.AlertType.ERROR);
                return;
            }

            BillingCalculation calc = billingService.calculateBill(newUsage);
            fxCalculatedBillLabel.setText(String.format("M%.2f", calc.getTotalAmount()));
            logger.info("Bill calculated for customer {}: {} kWh = M{}",
                selectedCustomer.getCustomerId(), newUsage, calc.getTotalAmount());

        } catch (NumberFormatException e) {
            logger.warn("Invalid usage input: {}", fxNewUsageField.getText());
            PrintUtil.showAlert("Validation Error", "Please enter a valid number for usage", javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdateBill() {
        if (selectedCustomer == null) {
            PrintUtil.showAlert("Selection Error", "Please select a customer first", javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }

        try {
            double newUsage = Double.parseDouble(fxNewUsageField.getText().trim());
            BillingCalculation calc = billingService.calculateBill(newUsage);

            // Update in database
            selectedCustomer.setElectricityUsage(newUsage);
            selectedCustomer.setBillAmount(calc.getTotalAmount());

            boolean success = customerService.updateCustomer(selectedCustomer);
            if (success) {
                logger.info("Bill updated for customer {}: {} kWh = M{}",
                    selectedCustomer.getCustomerId(), newUsage, calc.getTotalAmount());
                PrintUtil.showAlert("Success",
                        String.format("Bill updated for %s: %.1f kWh = M%.2f\n\n%s",
                                selectedCustomer.getName(), newUsage, calc.getTotalAmount(), calc.getBreakdown()),
                        javafx.scene.control.Alert.AlertType.INFORMATION);

                fxCustomerTable.refresh();
                updateStats();
            } else {
                logger.warn("Failed to update bill for customer: {}", selectedCustomer.getCustomerId());
                PrintUtil.showAlert("Error", "Failed to update bill in database", javafx.scene.control.Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            logger.warn("Invalid usage input for update: {}", fxNewUsageField.getText());
            PrintUtil.showAlert("Validation Error", "Please enter a valid number for usage", javafx.scene.control.Alert.AlertType.ERROR);
        } catch (Exception e) {
            logger.error("Error updating bill", e);
            PrintUtil.showAlert("Error", "Failed to update bill: " + e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlePrintReceipt() {
        if (selectedCustomer == null) {
            PrintUtil.showAlert("Selection Error", "Please select a customer first", javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }

        if (selectedCustomer.getBillAmount() <= 0) {
            PrintUtil.showAlert("Bill Error", "No bill amount calculated for this customer", javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }

        try {
            // Generate receipt
            StringBuilder receipt = new StringBuilder();
            receipt.append("LESOTHO ELECTRICITY COMPANY\n");
            receipt.append("============================\n");
            receipt.append("ELECTRICITY BILL RECEIPT\n");
            receipt.append("============================\n\n");
            receipt.append("Customer ID: ").append(selectedCustomer.getCustomerId()).append("\n");
            receipt.append("Customer Name: ").append(selectedCustomer.getName()).append("\n");
            receipt.append("Address: ").append(selectedCustomer.getAddress()).append("\n");
            receipt.append("Meter Number: ").append(selectedCustomer.getMeterNumber()).append("\n\n");
            receipt.append("Electricity Usage: ").append(String.format("%.1f kWh", selectedCustomer.getElectricityUsage())).append("\n");
            receipt.append("Bill Amount: ").append(String.format("M%.2f", selectedCustomer.getBillAmount())).append("\n\n");
            receipt.append("Billing Date: ").append(java.time.LocalDate.now()).append("\n");
            receipt.append("Thank you for choosing LEC!\n");
            receipt.append("============================");

            logger.info("Receipt generated for customer: {}", selectedCustomer.getCustomerId());
            PrintUtil.showAlert("Receipt Generated",
                    "Receipt is ready for printing:\n\n" + receipt.toString(),
                    javafx.scene.control.Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            logger.error("Error generating receipt", e);
            PrintUtil.showAlert("Error", "Failed to generate receipt: " + e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) fxBackButton.getScene().getWindow();
            stage.close();
            logger.info("Bills dashboard closed");
        } catch (Exception e) {
            logger.error("Error closing bills dashboard", e);
        }
    }
}