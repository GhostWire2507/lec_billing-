package lecbilling.mokopanemakhetha;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CustomerTableController {
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

    private CustomerManager customerManager;

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
        initializeTable();
    }

    private void initializeTable() {
        fxCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        fxNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        fxAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        fxMeterNumberColumn.setCellValueFactory(new PropertyValueFactory<>("meterNumber"));
        fxUsageColumn.setCellValueFactory(new PropertyValueFactory<>("electricityUsage"));
        fxBillColumn.setCellValueFactory(new PropertyValueFactory<>("billAmount"));

        fxCustomerTable.setItems(customerManager.getCustomers());
    }

    @FXML
    private void handleAddCustomer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("customerForm.fxml"));
            Parent root = loader.load();

            CustomerFormController controller = loader.getController();
            controller.setCustomerManager(customerManager);
            controller.setCustomerTableController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 500, 500));
            stage.setTitle("Add New Customer");
            stage.showAndWait();

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
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
            controller.setCustomerManager(customerManager);
            controller.setCustomerTableController(this);
            controller.setCustomerForEdit(selectedCustomer);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 500, 500));
            stage.setTitle("Edit Customer");
            stage.showAndWait();

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
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
            customerManager.deleteCustomer(selectedCustomer.getCustomerId());
            refreshTable();
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
            controller.setCustomerManager(customerManager);
            controller.setCustomerTableController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("Calculate Bill - " + selectedCustomer.getName());
            stage.showAndWait();

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = fxSearchField.getText();
        if (searchTerm.isEmpty()) {
            fxCustomerTable.setItems(customerManager.getCustomers());
        } else {
            fxCustomerTable.setItems(customerManager.searchCustomers(searchTerm));
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setCustomerManager(customerManager);

            Stage stage = (Stage) fxBackButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setTitle("LEC Billing - Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshTable() {
        fxCustomerTable.refresh();
    }
}