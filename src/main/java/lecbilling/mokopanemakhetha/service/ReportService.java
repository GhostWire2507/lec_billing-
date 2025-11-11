package lecbilling.mokopanemakhetha.service;

import lecbilling.mokopanemakhetha.config.DatabaseConfig;
import lecbilling.mokopanemakhetha.model.ReportData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating reports and analytics
 */
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private static ReportService instance;

    private ReportService() {
    }

    public static synchronized ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    /**
     * Get dashboard statistics
     */
    public Map<String, Object> getDashboardStats() {
        logger.debug("Fetching dashboard statistics");
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Total customers
            stats.put("totalCustomers", getCustomerCount(conn));
            
            // Total bills
            stats.put("totalBills", getTotalBillsCount(conn));
            
            // Unpaid bills
            stats.put("unpaidBills", getUnpaidBillsCount(conn));
            
            // Total revenue
            stats.put("totalRevenue", getTotalRevenue(conn));
            
            // Outstanding amount
            stats.put("outstandingAmount", getOutstandingAmount(conn));
            
            // This month's revenue
            stats.put("monthlyRevenue", getMonthlyRevenue(conn));
            
            logger.info("Dashboard statistics retrieved successfully");
        } catch (SQLException e) {
            logger.error("Error fetching dashboard statistics", e);
        }
        
        return stats;
    }

    /**
     * Get monthly revenue data for charts
     */
    public List<ReportData> getMonthlyRevenueReport(int months) {
        logger.debug("Fetching monthly revenue report for last {} months", months);
        List<ReportData> data = new ArrayList<>();
        
        String query = "SELECT TO_CHAR(billing_period_start, 'YYYY-MM') as month, " +
                      "SUM(bill_amount) as revenue, " +
                      "SUM(electricity_usage) as usage, " +
                      "COUNT(*) as bill_count " +
                      "FROM bills " +
                      "WHERE billing_period_start >= CURRENT_DATE - INTERVAL '" + months + " months' " +
                      "GROUP BY TO_CHAR(billing_period_start, 'YYYY-MM') " +
                      "ORDER BY month DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ReportData report = new ReportData();
                report.setLabel(rs.getString("month"));
                report.setValue(rs.getDouble("revenue"));
                report.setCount(rs.getInt("bill_count"));
                data.add(report);
            }
            
            logger.info("Retrieved {} months of revenue data", data.size());
        } catch (SQLException e) {
            logger.error("Error fetching monthly revenue report", e);
        }
        
        return data;
    }

    /**
     * Get payment status distribution
     */
    public Map<String, Integer> getPaymentStatusDistribution() {
        logger.debug("Fetching payment status distribution");
        Map<String, Integer> distribution = new HashMap<>();
        
        String query = "SELECT payment_status, COUNT(*) as count FROM bills GROUP BY payment_status";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                distribution.put(rs.getString("payment_status"), rs.getInt("count"));
            }
            
            logger.info("Payment status distribution retrieved");
        } catch (SQLException e) {
            logger.error("Error fetching payment status distribution", e);
        }
        
        return distribution;
    }

    /**
     * Get top customers by consumption
     */
    public List<Map<String, Object>> getTopCustomersByConsumption(int limit) {
        logger.debug("Fetching top {} customers by consumption", limit);
        List<Map<String, Object>> customers = new ArrayList<>();
        
        String query = "SELECT c.customer_id, c.name, SUM(b.electricity_usage) as total_usage, " +
                      "SUM(b.bill_amount) as total_billed " +
                      "FROM customers c " +
                      "JOIN bills b ON c.id = b.customer_id " +
                      "WHERE c.is_active = TRUE " +
                      "GROUP BY c.customer_id, c.name " +
                      "ORDER BY total_usage DESC " +
                      "LIMIT ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> customer = new HashMap<>();
                    customer.put("customerId", rs.getString("customer_id"));
                    customer.put("name", rs.getString("name"));
                    customer.put("totalUsage", rs.getDouble("total_usage"));
                    customer.put("totalBilled", rs.getDouble("total_billed"));
                    customers.add(customer);
                }
            }
            
            logger.info("Retrieved top {} customers by consumption", customers.size());
        } catch (SQLException e) {
            logger.error("Error fetching top customers", e);
        }
        
        return customers;
    }

    // Helper methods
    private int getCustomerCount(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM customers WHERE is_active = TRUE";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getTotalBillsCount(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM bills";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getUnpaidBillsCount(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM bills WHERE payment_status = 'UNPAID'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private double getTotalRevenue(Connection conn) throws SQLException {
        String query = "SELECT COALESCE(SUM(bill_amount), 0) FROM bills WHERE payment_status = 'PAID'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    private double getOutstandingAmount(Connection conn) throws SQLException {
        String query = "SELECT COALESCE(SUM(bill_amount), 0) FROM bills WHERE payment_status = 'UNPAID'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    private double getMonthlyRevenue(Connection conn) throws SQLException {
        String query = "SELECT COALESCE(SUM(bill_amount), 0) FROM bills " +
                      "WHERE billing_period_start >= DATE_TRUNC('month', CURRENT_DATE)";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }
}

