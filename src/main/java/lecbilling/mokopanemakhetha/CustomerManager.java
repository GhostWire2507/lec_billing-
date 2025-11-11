package lecbilling.mokopanemakhetha;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.Optional;

public class CustomerManager {
    private ObservableList<Customer> customers;
    private ObservableList<User> users;

    public CustomerManager() {
        customers = FXCollections.observableArrayList();
        users = FXCollections.observableArrayList();
        loadUsersFromDatabase();
        loadCustomersFromDatabase();
    }

    private void loadUsersFromDatabase() {
        String query = "SELECT username, password, role, full_name FROM users";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
            // Fallback to sample data
            initializeSampleUsers();
        }
    }

    private void loadCustomersFromDatabase() {
        String query = "SELECT customer_id, name, address, meter_number, electricity_usage, bill_amount FROM customers";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("meter_number")
                );
                customer.setElectricityUsage(rs.getDouble("electricity_usage"));
                customer.setBillAmount(rs.getDouble("bill_amount"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error loading customers: " + e.getMessage());
            // Fallback to sample data
            initializeSampleCustomers();
        }
    }

    private void initializeSampleUsers() {
        users.add(new User("admin", "admin123", "Administrator"));
        users.add(new User("staff", "staff123", "Staff"));
    }

    private void initializeSampleCustomers() {
        Customer customer1 = new Customer("C001", "John Molapo", "Maseru West", "MTR001");
        customer1.setElectricityUsage(150);
        customer1.setBillAmount(BillCalculator.calculateBill(150));

        Customer customer2 = new Customer("C002", "Mary Seleke", "Thetsane", "MTR002");
        customer2.setElectricityUsage(350);
        customer2.setBillAmount(BillCalculator.calculateBill(350));

        customers.addAll(customer1, customer2);
    }

    public ObservableList<Customer> getCustomers() {
        return customers;
    }

    public boolean addCustomer(Customer customer) {
        if (isCustomerIdExists(customer.getCustomerId())) {
            return false;
        }

        String query = "INSERT INTO customers (customer_id, name, address, meter_number, electricity_usage, bill_amount) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getMeterNumber());
            pstmt.setDouble(5, customer.getElectricityUsage());
            pstmt.setDouble(6, customer.getBillAmount());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                customers.add(customer);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
        }
        return false;
    }

    public boolean updateCustomer(String customerId, Customer updatedCustomer) {
        String query = "UPDATE customers SET name = ?, address = ?, meter_number = ?, electricity_usage = ?, bill_amount = ? WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, updatedCustomer.getName());
            pstmt.setString(2, updatedCustomer.getAddress());
            pstmt.setString(3, updatedCustomer.getMeterNumber());
            pstmt.setDouble(4, updatedCustomer.getElectricityUsage());
            pstmt.setDouble(5, updatedCustomer.getBillAmount());
            pstmt.setString(6, customerId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Update local list
                for (int i = 0; i < customers.size(); i++) {
                    if (customers.get(i).getCustomerId().equals(customerId)) {
                        customers.set(i, updatedCustomer);
                        break;
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCustomer(String customerId) {
        String query = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                customers.removeIf(customer -> customer.getCustomerId().equals(customerId));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
        }
        return false;
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
        String query = "SELECT COUNT(*) FROM customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking customer ID: " + e.getMessage());
        }
        return false;
    }

    public boolean authenticateUser(String username, String password) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            // Fallback to local authentication
            return users.stream().anyMatch(user ->
                    user.getUsername().equals(username) && user.getPassword().equals(password));
        }
        return false;
    }
}