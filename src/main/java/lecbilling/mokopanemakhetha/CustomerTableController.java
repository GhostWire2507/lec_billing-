package lecbilling.mokopanemakhetha;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lecbilling.mokopanemakhetha.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerTableController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerTableController.class);

    @FXML private TableView<Customer> fxCustomerTable;
    @FXML private TableColumn<Customer, String> fxCustomerIdColumn;
    @FXML private TableColumn<Customer, String> fxNameColumn;
    @FXML private TableColumn<Customer, String> fxAddressColumn;
    @FXML private TableColumn<Customer, String> fxMeterNumberColumn;
    @FXML private TableColumn<Customer, Double> fxUsageColumn;
    @FXML private TableColumn<Customer, Double> fxBillColumn;
    @FXML private TextField fxSearchField;
    @FXML private Button fxAddButton;
    @FXML private Button fxEditButton;
    @FXML private Button fxDeleteButton;
    @FXML private Button fxBackButton;
    @FXML private Button fxCalculateBillButton;

    private final CustomerService customerService = CustomerService.getInstance();
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        logger.info("Customer table initialized for user: {}", user.getUsername());
        initializeTable();
    }

    private void initializeTable() {
        try {
            fxCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            fxNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            fxAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
            fxMeterNumberColumn.setCellValueFactory(new PropertyValueFactory<>("meterNumber"));
            fxUsageColumn.setCellValueFactory(new PropertyValueFactory<>("electricityUsage"));
            fxBillColumn.setCellValueFactory(new PropertyValueFactory<>("billAmount"));

            fxCustomerTable.setItems(customerService.getAllCustomers());
            logger.info("Customer table initialized with {} customers", fxCustomerTable.getItems().size());

            fxSearchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
        } catch (Exception e) {
            logger.error("Error initializing customer table", e);
            PrintUtil.showAlert("Error", "Failed to load customers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddCustomer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("customerForm.fxml"));
            Parent root = loader.load();

            CustomerFormController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setCustomerTableController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 500, 500));
            stage.setTitle("Add New Customer");
            stage.showAndWait();

            refreshTable();
        } catch (Exception e) {
            logger.error("Error opening add customer form", e);
            PrintUtil.showAlert("Error", "Failed to open customer form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEditCustomer() {
        Customer selectedCustomer = fxCustomerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            PrintUtil.showAlert("Selection Error", "Please select a customer to edit", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("customerForm.fxml"));
            Parent root = loader.load();

            CustomerFormController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setCustomerTableController(this);
            controller.setCustomerForEdit(selectedCustomer);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 500, 500));
            stage.setTitle("Edit Customer");
            stage.showAndWait();

            refreshTable();
        } catch (Exception e) {
            logger.error("Error opening edit customer form", e);
            PrintUtil.showAlert("Error", "Failed to open customer form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteCustomer() {
        Customer selectedCustomer = fxCustomerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            PrintUtil.showAlert("Selection Error", "Please select a customer to delete", Alert.AlertType.WARNING);
            return;
        }

        if (PrintUtil.showConfirmation("Confirm Delete",
                "Are you sure you want to delete customer: " + selectedCustomer.getName() + "?")) {
            try {
                boolean success = customerService.deleteCustomer(selectedCustomer.getCustomerId());
                if (success) {
                    logger.info("Customer deleted: {}", selectedCustomer.getCustomerId());
                    PrintUtil.showAlert("Success", "Customer deleted successfully", Alert.AlertType.INFORMATION);
                    refreshTable();
                } else {
                    logger.warn("Failed to delete customer: {}", selectedCustomer.getCustomerId());
                    PrintUtil.showAlert("Error", "Failed to delete customer", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                logger.error("Error deleting customer: " + selectedCustomer.getCustomerId(), e);
                PrintUtil.showAlert("Error", "Failed to delete customer: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCalculateBill() {
        Customer selectedCustomer = fxCustomerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            PrintUtil.showAlert("Selection Error", "Please select a customer to calculate bill", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("billCalculator.fxml"));
            Parent root = loader.load();

            BillCalculatorController controller = loader.getController();
            controller.setCustomer(selectedCustomer);
            controller.setCurrentUser(currentUser);
            controller.setCustomerTableController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 450, 350));
            stage.setTitle("Calculate Bill - " + selectedCustomer.getName());
            stage.showAndWait();

            refreshTable();
        } catch (Exception e) {
            logger.error("Error opening bill calculator", e);
            PrintUtil.showAlert("Error", "Failed to open bill calculator: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String searchTerm = fxSearchField.getText();
            if (searchTerm.isEmpty()) {
                fxCustomerTable.setItems(customerService.getAllCustomers());
            } else {
                ObservableList<Customer> searchResults = FXCollections.observableArrayList(
                    customerService.searchCustomers(searchTerm)
                );
                fxCustomerTable.setItems(searchResults);
            }
            logger.debug("Search performed with term: {}", searchTerm);
        } catch (Exception e) {
            logger.error("Error searching customers", e);
            PrintUtil.showAlert("Error", "Failed to search customers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) fxBackButton.getScene().getWindow();
            stage.close();
            logger.info("Customer table closed");
        } catch (Exception e) {
            logger.error("Error closing customer table", e);
        }
    }

    public void refreshTable() {
        try {
            fxCustomerTable.setItems(customerService.getAllCustomers());
            fxCustomerTable.refresh();
            logger.debug("Customer table refreshed");
        } catch (Exception e) {
            logger.error("Error refreshing customer table", e);
        }
    }
}