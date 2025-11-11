package lecbilling.mokopanemakhetha;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lecbilling.mokopanemakhetha.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerFormController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerFormController.class);

    @FXML private TextField fxCustomerIdField;
    @FXML private TextField fxNameField;
    @FXML private TextField fxAddressField;
    @FXML private TextField fxMeterNumberField;
    @FXML private Button fxSaveButton;
    @FXML private Button fxCancelButton;

    private final CustomerService customerService = CustomerService.getInstance();
    private CustomerTableController customerTableController;
    private Customer customerToEdit;
    private boolean isEditMode = false;
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        logger.debug("Customer form initialized for user: {}", user.getUsername());
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
            logger.warn("Validation failed: Empty fields");
            PrintUtil.showAlert("Validation Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        if (!isEditMode && customerService.customerIdExists(customerId)) {
            logger.warn("Validation failed: Customer ID {} already exists", customerId);
            PrintUtil.showAlert("Validation Error", "Customer ID already exists", Alert.AlertType.ERROR);
            return;
        }

        try {
            if (isEditMode) {
                // Update existing customer
                customerToEdit.setName(name);
                customerToEdit.setAddress(address);
                customerToEdit.setMeterNumber(meterNumber);

                // Update in database
                if (customerService.updateCustomer(customerToEdit)) {
                    logger.info("Customer updated successfully: {}", customerId);
                    PrintUtil.showAlert("Success", "Customer updated successfully", Alert.AlertType.INFORMATION);
                } else {
                    logger.error("Failed to update customer: {}", customerId);
                    PrintUtil.showAlert("Error", "Failed to update customer in database", Alert.AlertType.ERROR);
                    return;
                }
            } else {
                // Add new customer
                Customer newCustomer = new Customer(customerId, name, address, meterNumber);
                if (customerService.addCustomer(newCustomer)) {
                    logger.info("Customer added successfully: {}", customerId);
                    PrintUtil.showAlert("Success", "Customer added successfully", Alert.AlertType.INFORMATION);
                } else {
                    logger.error("Failed to add customer: {}", customerId);
                    PrintUtil.showAlert("Error", "Failed to add customer to database", Alert.AlertType.ERROR);
                    return;
                }
            }

            if (customerTableController != null) {
                customerTableController.refreshTable();
            }

            closeWindow();
        } catch (Exception e) {
            logger.error("Error saving customer", e);
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