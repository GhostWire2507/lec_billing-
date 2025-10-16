package lecbilling.mokopanemakhetha;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CustomerFormController {
    @FXML private TextField fxCustomerIdField;
    @FXML private TextField fxNameField;
    @FXML private TextField fxAddressField;
    @FXML private TextField fxMeterNumberField;
    @FXML private Button fxSaveButton;
    @FXML private Button fxCancelButton;

    private CustomerManager customerManager;
    private CustomerTableController customerTableController;
    private Customer customerToEdit;
    private boolean isEditMode = false;

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    public void setCustomerTableController(CustomerTableController controller) {
        this.customerTableController = controller;
    }

    public void setCustomerForEdit(Customer customer) {
        this.customerToEdit = customer;
        this.isEditMode = true;
        populateFields();
    }

    private void populateFields() {
        if (customerToEdit != null) {
            fxCustomerIdField.setText(customerToEdit.getCustomerId());
            fxNameField.setText(customerToEdit.getName());
            fxAddressField.setText(customerToEdit.getAddress());
            fxMeterNumberField.setText(customerToEdit.getMeterNumber());
            fxCustomerIdField.setDisable(true); // Can't edit ID
        }
    }

    @FXML
    private void handleSave() {
        String customerId = fxCustomerIdField.getText().trim();
        String name = fxNameField.getText().trim();
        String address = fxAddressField.getText().trim();
        String meterNumber = fxMeterNumberField.getText().trim();

        // Validation
        if (customerId.isEmpty() || name.isEmpty() || address.isEmpty() || meterNumber.isEmpty()) {
            PrintUtil.showAlert("Validation Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        if (!isEditMode && customerManager.isCustomerIdExists(customerId)) {
            PrintUtil.showAlert("Validation Error", "Customer ID already exists", Alert.AlertType.ERROR);
            return;
        }

        if (isEditMode) {
            // Update existing customer
            customerToEdit.setName(name);
            customerToEdit.setAddress(address);
            customerToEdit.setMeterNumber(meterNumber);
            PrintUtil.showAlert("Success", "Customer updated successfully", Alert.AlertType.INFORMATION);
        } else {
            // Add new customer
            Customer newCustomer = new Customer(customerId, name, address, meterNumber);
            if (customerManager.addCustomer(newCustomer)) {
                PrintUtil.showAlert("Success", "Customer added successfully", Alert.AlertType.INFORMATION);
            } else {
                PrintUtil.showAlert("Error", "Failed to add customer", Alert.AlertType.ERROR);
                return;
            }
        }

        closeWindow();
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