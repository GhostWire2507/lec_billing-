package lecbilling.mokopanemakhetha;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lecbilling.mokopanemakhetha.model.BillingCalculation;
import lecbilling.mokopanemakhetha.service.BillingService;
import lecbilling.mokopanemakhetha.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BillCalculatorController {
    private static final Logger logger = LoggerFactory.getLogger(BillCalculatorController.class);

    @FXML private Label fxCustomerNameLabel;
    @FXML private TextField fxUsageField;
    @FXML private Label fxBillAmountLabel;
    @FXML private Button fxCalculateButton;
    @FXML private Button fxSaveButton;
    @FXML private Button fxCancelButton;

    private final CustomerService customerService = CustomerService.getInstance();
    private final BillingService billingService = BillingService.getInstance();
    private Customer customer;
    private CustomerTableController customerTableController;
    private User currentUser;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        fxCustomerNameLabel.setText("Customer: " + customer.getName());
        logger.debug("Bill calculator initialized for customer: {}", customer.getCustomerId());
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        logger.debug("Bill calculator initialized for user: {}", user.getUsername());
    }

    public void setCustomerTableController(CustomerTableController controller) {
        this.customerTableController = controller;
    }

    @FXML
    private void handleCalculate() {
        try {
            double usage = Double.parseDouble(fxUsageField.getText().trim());
            if (usage < 0) {
                logger.warn("Validation failed: Negative usage value");
                PrintUtil.showAlert("Validation Error", "Usage cannot be negative", Alert.AlertType.ERROR);
                return;
            }

            BillingCalculation calc = billingService.calculateBill(usage);
            fxBillAmountLabel.setText(String.format("M%.2f", calc.getTotalAmount()));
            logger.debug("Bill calculated: {} kWh = M{}", usage, calc.getTotalAmount());

        } catch (NumberFormatException e) {
            logger.warn("Invalid usage input: {}", fxUsageField.getText());
            PrintUtil.showAlert("Validation Error", "Please enter a valid number for usage", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSave() {
        try {
            double usage = Double.parseDouble(fxUsageField.getText().trim());
            BillingCalculation calc = billingService.calculateBill(usage);

            customer.setElectricityUsage(usage);
            customer.setBillAmount(calc.getTotalAmount());

            // Update customer in database
            if (customerService.updateCustomer(customer)) {
                logger.info("Bill saved for customer {}: {} kWh = M{}",
                    customer.getCustomerId(), usage, calc.getTotalAmount());
                PrintUtil.showAlert("Success",
                        String.format("Bill calculated: M%.2f for %.1f kWh", calc.getTotalAmount(), usage),
                        Alert.AlertType.INFORMATION);

                customerTableController.refreshTable();
                closeWindow();
            } else {
                logger.error("Failed to save bill for customer: {}", customer.getCustomerId());
                PrintUtil.showAlert("Error", "Failed to save bill to database", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            logger.warn("Invalid usage input: {}", fxUsageField.getText());
            PrintUtil.showAlert("Validation Error", "Please enter a valid number for usage", Alert.AlertType.ERROR);
        } catch (Exception e) {
            logger.error("Error saving bill", e);
            PrintUtil.showAlert("Error", "An error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) fxCancelButton.getScene().getWindow();
        stage.close();
    }
}