package lecbilling.mokopanemakhetha.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lecbilling.mokopanemakhetha.Customer;
import lecbilling.mokopanemakhetha.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing customer operations
 */
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private static CustomerService instance;

    private CustomerService() {
    }

    public static synchronized CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }

    /**
     * Get all active customers
     */
    public ObservableList<Customer> getAllCustomers() {
        logger.debug("Fetching all active customers");
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        
        String query = "SELECT customer_id, name, address, meter_number, phone_number, email " +
                      "FROM customers WHERE is_active = TRUE ORDER BY name";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("meter_number")
                );
                customers.add(customer);
            }
            
            logger.info("Retrieved {} customers from database", customers.size());
        } catch (SQLException e) {
            logger.error("Error loading customers from database", e);
        }
        
        return customers;
    }

    /**
     * Get customer by ID
     */
    public Customer getCustomerById(String customerId) {
        logger.debug("Fetching customer with ID: {}", customerId);
        
        String query = "SELECT customer_id, name, address, meter_number, phone_number, email " +
                      "FROM customers WHERE customer_id = ? AND is_active = TRUE";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("meter_number")
                    );
                    logger.debug("Customer found: {}", customerId);
                    return customer;
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching customer: {}", customerId, e);
        }
        
        logger.warn("Customer not found: {}", customerId);
        return null;
    }

    /**
     * Add a new customer
     */
    public boolean addCustomer(Customer customer) {
        logger.info("Adding new customer: {}", customer.getCustomerId());
        
        String query = "INSERT INTO customers (customer_id, name, address, meter_number, phone_number, created_by) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getMeterNumber());
            pstmt.setString(5, null); // Phone number - to be added to Customer class
            pstmt.setInt(6, 1); // Default to admin user - should be passed from session

            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Customer added successfully: {}", customer.getCustomerId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error adding customer: {}", customer.getCustomerId(), e);
        }
        
        return false;
    }

    /**
     * Update an existing customer
     */
    public boolean updateCustomer(Customer customer) {
        logger.info("Updating customer: {}", customer.getCustomerId());
        
        String query = "UPDATE customers SET name = ?, address = ?, meter_number = ? " +
                      "WHERE customer_id = ? AND is_active = TRUE";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getAddress());
            pstmt.setString(3, customer.getMeterNumber());
            pstmt.setString(4, customer.getCustomerId());

            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Customer updated successfully: {}", customer.getCustomerId());
                return true;
            } else {
                logger.warn("No customer found to update: {}", customer.getCustomerId());
            }
        } catch (SQLException e) {
            logger.error("Error updating customer: {}", customer.getCustomerId(), e);
        }
        
        return false;
    }

    /**
     * Delete a customer (soft delete)
     */
    public boolean deleteCustomer(String customerId) {
        logger.info("Deleting customer: {}", customerId);
        
        String query = "UPDATE customers SET is_active = FALSE WHERE customer_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Customer deleted successfully: {}", customerId);
                return true;
            } else {
                logger.warn("No customer found to delete: {}", customerId);
            }
        } catch (SQLException e) {
            logger.error("Error deleting customer: {}", customerId, e);
        }
        
        return false;
    }

    /**
     * Check if customer ID already exists
     */
    public boolean customerIdExists(String customerId) {
        String query = "SELECT COUNT(*) FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking customer ID existence: {}", customerId, e);
        }
        
        return false;
    }

    /**
     * Search customers by name or meter number
     */
    public List<Customer> searchCustomers(String searchTerm) {
        logger.debug("Searching customers with term: {}", searchTerm);
        List<Customer> customers = new ArrayList<>();
        
        String query = "SELECT customer_id, name, address, meter_number " +
                      "FROM customers WHERE is_active = TRUE AND " +
                      "(LOWER(name) LIKE LOWER(?) OR LOWER(meter_number) LIKE LOWER(?)) " +
                      "ORDER BY name";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Customer customer = new Customer(
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("meter_number")
                    );
                    customers.add(customer);
                }
            }
            
            logger.info("Found {} customers matching search term: {}", customers.size(), searchTerm);
        } catch (SQLException e) {
            logger.error("Error searching customers", e);
        }
        
        return customers;
    }

    /**
     * Get total customer count
     */
    public int getCustomerCount() {
        String query = "SELECT COUNT(*) FROM customers WHERE is_active = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting customer count", e);
        }
        
        return 0;
    }
}

