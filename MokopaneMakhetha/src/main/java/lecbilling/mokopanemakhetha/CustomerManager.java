package lecbilling.mokopanemakhetha;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Optional;

public class CustomerManager {
    private ObservableList<Customer> customers;
    private ObservableList<User> users;

    public CustomerManager() {
        customers = FXCollections.observableArrayList();
        users = FXCollections.observableArrayList();
        initializeSampleData();
    }

    private void initializeSampleData() {
        users.add(new User("admin", "admin123", "Administrator"));
        users.add(new User("staff", "staff123", "Staff"));

        // Add sample customers
        Customer customer1 = new Customer("C001", "John Molapo", "Maseru West", "MTR001");
        customer1.setElectricityUsage(150);
        customer1.setBillAmount(BillCalculator.calculateBill(150));

        Customer customer2 = new Customer("C002", "Mary Seleke", "Thetsane", "MTR002");
        customer2.setElectricityUsage(350);
        customer2.setBillAmount(BillCalculator.calculateBill(350));

        customers.addAll(customer1, customer2);
    }

    public ObservableList<Customer> getCustomers() { return customers; }

    public boolean addCustomer(Customer customer) {
        if (isCustomerIdExists(customer.getCustomerId())) {
            return false;
        }
        customers.add(customer);
        return true;
    }

    public boolean updateCustomer(String customerId, Customer updatedCustomer) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getCustomerId().equals(customerId)) {
                customers.set(i, updatedCustomer);
                return true;
            }
        }
        return false;
    }

    public boolean deleteCustomer(String customerId) {
        return customers.removeIf(customer -> customer.getCustomerId().equals(customerId));
    }

    public Optional<Customer> findCustomerById(String customerId) {
        return customers.stream()
                .filter(customer -> customer.getCustomerId().equals(customerId))
                .findFirst();
    }

    public ObservableList<Customer> searchCustomers(String searchTerm) {
        return customers.filtered(customer ->
                customer.getCustomerId().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        customer.getName().toLowerCase().contains(searchTerm.toLowerCase())
        );
    }

    public boolean isCustomerIdExists(String customerId) {
        return customers.stream().anyMatch(customer -> customer.getCustomerId().equals(customerId));
    }

    public boolean authenticateUser(String username, String password) {
        return users.stream().anyMatch(user ->
                user.getUsername().equals(username) && user.getPassword().equals(password));
    }
}