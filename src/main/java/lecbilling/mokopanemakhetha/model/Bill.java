package lecbilling.mokopanemakhetha.model;

import java.time.LocalDate;

/**
 * Model class representing a customer bill
 */
public class Bill {
    private String billNumber;
    private String customerId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private double previousReading;
    private double currentReading;
    private double usage;
    private double amount;
    private String paymentStatus;
    private LocalDate dueDate;
    private LocalDate paymentDate;

    public Bill() {
    }

    public Bill(String billNumber, String customerId, LocalDate periodStart, LocalDate periodEnd,
                double usage, double amount, String paymentStatus, LocalDate dueDate) {
        this.billNumber = billNumber;
        this.customerId = customerId;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.usage = usage;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public double getPreviousReading() {
        return previousReading;
    }

    public void setPreviousReading(double previousReading) {
        this.previousReading = previousReading;
    }

    public double getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(double currentReading) {
        this.currentReading = currentReading;
    }

    public double getUsage() {
        return usage;
    }

    public void setUsage(double usage) {
        this.usage = usage;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public boolean isPaid() {
        return "PAID".equalsIgnoreCase(paymentStatus);
    }

    public boolean isOverdue() {
        return !isPaid() && dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        return String.format("Bill{number='%s', customer='%s', period=%s to %s, usage=%.2f kWh, amount=M%.2f, status=%s}",
                billNumber, customerId, periodStart, periodEnd, usage, amount, paymentStatus);
    }
}

