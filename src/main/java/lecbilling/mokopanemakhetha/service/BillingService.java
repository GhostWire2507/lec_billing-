package lecbilling.mokopanemakhetha.service;

import lecbilling.mokopanemakhetha.config.DatabaseConfig;
import lecbilling.mokopanemakhetha.model.Bill;
import lecbilling.mokopanemakhetha.model.BillingCalculation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing billing operations and calculations
 */
public class BillingService {
    private static final Logger logger = LoggerFactory.getLogger(BillingService.class);
    private static BillingService instance;

    // Tiered pricing rates (in Maloti per kWh)
    private static final double RATE_TIER_1 = 1.20;  // 0-100 kWh
    private static final double RATE_TIER_2 = 1.50;  // 101-300 kWh
    private static final double RATE_TIER_3 = 2.00;  // Above 300 kWh

    private BillingService() {
    }

    public static synchronized BillingService getInstance() {
        if (instance == null) {
            instance = new BillingService();
        }
        return instance;
    }

    /**
     * Calculate bill amount based on electricity usage with tiered pricing
     */
    public BillingCalculation calculateBill(double usage) {
        logger.debug("Calculating bill for usage: {} kWh", usage);
        
        BillingCalculation calc = new BillingCalculation();
        calc.setTotalUsage(usage);
        
        double remainingUsage = usage;
        
        // Tier 3: Above 300 kWh
        if (remainingUsage > 300) {
            double tier3Usage = remainingUsage - 300;
            calc.setTier3Usage(tier3Usage);
            calc.setTier3Amount(tier3Usage * RATE_TIER_3);
            remainingUsage = 300;
        }
        
        // Tier 2: 101-300 kWh
        if (remainingUsage > 100) {
            double tier2Usage = remainingUsage - 100;
            calc.setTier2Usage(tier2Usage);
            calc.setTier2Amount(tier2Usage * RATE_TIER_2);
            remainingUsage = 100;
        }
        
        // Tier 1: 0-100 kWh
        if (remainingUsage > 0) {
            calc.setTier1Usage(remainingUsage);
            calc.setTier1Amount(remainingUsage * RATE_TIER_1);
        }
        
        double totalAmount = calc.getTier1Amount() + calc.getTier2Amount() + calc.getTier3Amount();
        calc.setTotalAmount(totalAmount);
        
        logger.debug("Bill calculated: {} kWh = M{}", usage, String.format("%.2f", totalAmount));
        return calc;
    }

    /**
     * Create a new bill for a customer
     */
    public boolean createBill(String customerId, double currentReading, double previousReading,
                             LocalDate periodStart, LocalDate periodEnd) {
        logger.info("Creating bill for customer: {}", customerId);
        
        double usage = currentReading - previousReading;
        if (usage < 0) {
            logger.error("Invalid reading: current reading is less than previous reading");
            return false;
        }
        
        BillingCalculation calc = calculateBill(usage);
        String billNumber = generateBillNumber();
        LocalDate dueDate = periodEnd.plusDays(15); // 15 days after period end
        
        String query = "INSERT INTO bills (bill_number, customer_id, billing_period_start, " +
                      "billing_period_end, previous_reading, current_reading, electricity_usage, " +
                      "rate_tier_1_usage, rate_tier_2_usage, rate_tier_3_usage, " +
                      "rate_tier_1_amount, rate_tier_2_amount, rate_tier_3_amount, " +
                      "bill_amount, due_date, payment_status, created_by) " +
                      "SELECT ?, c.id, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'UNPAID', ? " +
                      "FROM customers c WHERE c.customer_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, billNumber);
            pstmt.setDate(2, Date.valueOf(periodStart));
            pstmt.setDate(3, Date.valueOf(periodEnd));
            pstmt.setDouble(4, previousReading);
            pstmt.setDouble(5, currentReading);
            pstmt.setDouble(6, usage);
            pstmt.setDouble(7, calc.getTier1Usage());
            pstmt.setDouble(8, calc.getTier2Usage());
            pstmt.setDouble(9, calc.getTier3Usage());
            pstmt.setDouble(10, calc.getTier1Amount());
            pstmt.setDouble(11, calc.getTier2Amount());
            pstmt.setDouble(12, calc.getTier3Amount());
            pstmt.setDouble(13, calc.getTotalAmount());
            pstmt.setDate(14, Date.valueOf(dueDate));
            pstmt.setInt(15, 1); // Default to admin - should come from session
            pstmt.setString(16, customerId);

            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Bill created successfully: {} for customer: {}", billNumber, customerId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating bill for customer: {}", customerId, e);
        }
        
        return false;
    }

    /**
     * Get all bills for a customer
     */
    public List<Bill> getCustomerBills(String customerId) {
        logger.debug("Fetching bills for customer: {}", customerId);
        List<Bill> bills = new ArrayList<>();
        
        String query = "SELECT b.bill_number, b.billing_period_start, b.billing_period_end, " +
                      "b.electricity_usage, b.bill_amount, b.payment_status, b.due_date, b.created_at " +
                      "FROM bills b " +
                      "JOIN customers c ON b.customer_id = c.id " +
                      "WHERE c.customer_id = ? " +
                      "ORDER BY b.billing_period_start DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bill bill = new Bill();
                    bill.setBillNumber(rs.getString("bill_number"));
                    bill.setPeriodStart(rs.getDate("billing_period_start").toLocalDate());
                    bill.setPeriodEnd(rs.getDate("billing_period_end").toLocalDate());
                    bill.setUsage(rs.getDouble("electricity_usage"));
                    bill.setAmount(rs.getDouble("bill_amount"));
                    bill.setPaymentStatus(rs.getString("payment_status"));
                    bill.setDueDate(rs.getDate("due_date").toLocalDate());
                    bills.add(bill);
                }
            }
            
            logger.info("Retrieved {} bills for customer: {}", bills.size(), customerId);
        } catch (SQLException e) {
            logger.error("Error fetching bills for customer: {}", customerId, e);
        }
        
        return bills;
    }

    /**
     * Mark a bill as paid
     */
    public boolean markBillAsPaid(String billNumber) {
        logger.info("Marking bill as paid: {}", billNumber);
        
        String query = "UPDATE bills SET payment_status = 'PAID', payment_date = ? " +
                      "WHERE bill_number = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(2, billNumber);

            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Bill marked as paid: {}", billNumber);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error marking bill as paid: {}", billNumber, e);
        }
        
        return false;
    }

    /**
     * Get unpaid bills count
     */
    public int getUnpaidBillsCount() {
        String query = "SELECT COUNT(*) FROM bills WHERE payment_status = 'UNPAID'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting unpaid bills count", e);
        }
        
        return 0;
    }

    /**
     * Get total outstanding amount
     */
    public double getTotalOutstandingAmount() {
        String query = "SELECT COALESCE(SUM(bill_amount), 0) FROM bills WHERE payment_status = 'UNPAID'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting total outstanding amount", e);
        }
        
        return 0.0;
    }

    /**
     * Generate a unique bill number
     */
    private String generateBillNumber() {
        String query = "SELECT COUNT(*) FROM bills";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("BILL-%d-%05d", LocalDate.now().getYear(), count);
            }
        } catch (SQLException e) {
            logger.error("Error generating bill number", e);
        }
        
        return String.format("BILL-%d-%05d", LocalDate.now().getYear(), 
                           System.currentTimeMillis() % 100000);
    }

    /**
     * Get pricing rates information
     */
    public String getPricingInfo() {
        return String.format(
            "Tiered Pricing Rates:\n" +
            "Tier 1 (0-100 kWh): M%.2f per kWh\n" +
            "Tier 2 (101-300 kWh): M%.2f per kWh\n" +
            "Tier 3 (Above 300 kWh): M%.2f per kWh",
            RATE_TIER_1, RATE_TIER_2, RATE_TIER_3
        );
    }
}

