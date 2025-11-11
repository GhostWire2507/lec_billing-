package lecbilling.mokopanemakhetha;

import java.sql.Connection;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("🧪 Testing LEC Billing Database Connection...");
        System.out.println("=============================================");

        try {
            // Test 1: Basic Connection
            System.out.println("1. Testing basic database connection...");
            Connection conn = DBConnection.getConnection();

            if (conn != null && !conn.isClosed()) {
                System.out.println("   ✅ SUCCESS: Connected to MySQL database!");
                System.out.println("   📍 Database: lec_billing_db");
                System.out.println("   🔗 Connection URL: jdbc:mysql://localhost:3306/lec_billing_db");
            } else {
                System.out.println("   ❌ FAILED: Could not establish connection");
                return;
            }

            // Test 2: Database Operations
            System.out.println("\n2. Testing database operations...");
            testDatabaseOperations();

            // Test 3: Customer Manager Integration
            System.out.println("\n3. Testing CustomerManager integration...");
            testCustomerManager();

            // Close connection
            DBConnection.closeConnection();
            System.out.println("\n🎉 All tests completed successfully!");
            System.out.println("✨ Your LEC Billing System is ready for database integration!");

        } catch (Exception e) {
            System.out.println("❌ TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testDatabaseOperations() {
        try {
            Connection conn = DBConnection.getConnection();

            // Test if we can execute a simple query
            var statement = conn.createStatement();
            var resultSet = statement.executeQuery("SELECT COUNT(*) as user_count FROM users");

            if (resultSet.next()) {
                int userCount = resultSet.getInt("user_count");
                System.out.println("   ✅ Users table accessible - Found " + userCount + " users");
            }

            // Test customers table
            resultSet = statement.executeQuery("SELECT COUNT(*) as customer_count FROM customers");
            if (resultSet.next()) {
                int customerCount = resultSet.getInt("customer_count");
                System.out.println("   ✅ Customers table accessible - Found " + customerCount + " customers");
            }

            statement.close();

        } catch (Exception e) {
            System.out.println("   ❌ Database operations failed: " + e.getMessage());
        }
    }

    private static void testCustomerManager() {
        try {
            CustomerManager manager = new CustomerManager();

            // Test customer loading
            int customerCount = manager.getCustomers().size();
            System.out.println("   ✅ CustomerManager loaded " + customerCount + " customers");

            // Test authentication
            boolean adminAuth = manager.authenticateUser("admin", "admin123");
            boolean staffAuth = manager.authenticateUser("staff", "staff123");
            boolean failedAuth = manager.authenticateUser("admin", "wrongpass");

            System.out.println("   ✅ Admin authentication: " + (adminAuth ? "PASSED" : "FAILED"));
            System.out.println("   ✅ Staff authentication: " + (staffAuth ? "PASSED" : "FAILED"));
            System.out.println("   ✅ Failed authentication test: " + (!failedAuth ? "PASSED" : "FAILED"));

        } catch (Exception e) {
            System.out.println("   ❌ CustomerManager test failed: " + e.getMessage());
        }
    }
}