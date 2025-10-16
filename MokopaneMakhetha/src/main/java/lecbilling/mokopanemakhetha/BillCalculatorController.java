package lecbilling.mokopanemakhetha;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BillCalculatorController {
    @FXML private Label fxCustomerNameLabel;
    @FXML private TextField fxUsageField;
    @FXML private Label fxBillAmountLabel;
    @FXML private Button fxCalculateButton;
    @FXML private Button fxSaveButton;
    @FXML private Button fxCancelButton;

    private Customer customer;
    private CustomerManager customerManager;
    private CustomerTableController customerTableController;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        fxCustomerNameLabel.setText("Customer: " + customer.getName());
    }

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    public void setCustomerTableController(CustomerTableController controller) {
        this.customerTableController = controller;
    }

    @FXML
    private void handleCalculate() {
        try {
            double usage = Double.parseDouble(fxUsageField.getText().trim());
            if (usage < 0) {
                PrintUtil.showAlert("Validation Error", "Usage cannot be negative", Alert.AlertType.ERROR);
                return;
            }

            double billAmount = BillCalculator.calculateBill(usage);
            fxBillAmountLabel.setText(String.format("M%.2f", billAmount));

        } catch (NumberFormatException e) {
            PrintUtil.showAlert("Validation Error", "Please enter a valid number for usage", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSave() {
        try {
            double usage = Double.parseDouble(fxUsageField.getText().trim());
            double billAmount = BillCalculator.calculateBill(usage);

            customer.setElectricityUsage(usage);
            customer.setBillAmount(billAmount);

            PrintUtil.showAlert("Success",
                    String.format("Bill calculated: M%.2f for %.1f kWh", billAmount, usage),
                    Alert.AlertType.INFORMATION);

            customerTableController.refreshTable();
            closeWindow();

        } catch (NumberFormatException e) {
            PrintUtil.showAlert("Validation Error", "Please enter a valid number for usage", Alert.AlertType.ERROR);
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