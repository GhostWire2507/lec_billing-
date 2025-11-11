package lecbilling.mokopanemakhetha.model;

/**
 * Model class for billing calculation breakdown
 */
public class BillingCalculation {
    private double totalUsage;
    private double tier1Usage;
    private double tier2Usage;
    private double tier3Usage;
    private double tier1Amount;
    private double tier2Amount;
    private double tier3Amount;
    private double totalAmount;

    public BillingCalculation() {
    }

    // Getters and Setters
    public double getTotalUsage() {
        return totalUsage;
    }

    public void setTotalUsage(double totalUsage) {
        this.totalUsage = totalUsage;
    }

    public double getTier1Usage() {
        return tier1Usage;
    }

    public void setTier1Usage(double tier1Usage) {
        this.tier1Usage = tier1Usage;
    }

    public double getTier2Usage() {
        return tier2Usage;
    }

    public void setTier2Usage(double tier2Usage) {
        this.tier2Usage = tier2Usage;
    }

    public double getTier3Usage() {
        return tier3Usage;
    }

    public void setTier3Usage(double tier3Usage) {
        this.tier3Usage = tier3Usage;
    }

    public double getTier1Amount() {
        return tier1Amount;
    }

    public void setTier1Amount(double tier1Amount) {
        this.tier1Amount = tier1Amount;
    }

    public double getTier2Amount() {
        return tier2Amount;
    }

    public void setTier2Amount(double tier2Amount) {
        this.tier2Amount = tier2Amount;
    }

    public double getTier3Amount() {
        return tier3Amount;
    }

    public void setTier3Amount(double tier3Amount) {
        this.tier3Amount = tier3Amount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Get a formatted breakdown of the calculation
     */
    public String getBreakdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("Billing Calculation Breakdown:\n");
        sb.append("==============================\n");
        
        if (tier1Usage > 0) {
            sb.append(String.format("Tier 1 (0-100 kWh): %.2f kWh × M1.20 = M%.2f\n", 
                    tier1Usage, tier1Amount));
        }
        
        if (tier2Usage > 0) {
            sb.append(String.format("Tier 2 (101-300 kWh): %.2f kWh × M1.50 = M%.2f\n", 
                    tier2Usage, tier2Amount));
        }
        
        if (tier3Usage > 0) {
            sb.append(String.format("Tier 3 (Above 300 kWh): %.2f kWh × M2.00 = M%.2f\n", 
                    tier3Usage, tier3Amount));
        }
        
        sb.append("==============================\n");
        sb.append(String.format("Total Usage: %.2f kWh\n", totalUsage));
        sb.append(String.format("Total Amount: M%.2f\n", totalAmount));
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("BillingCalculation{usage=%.2f kWh, amount=M%.2f}", 
                totalUsage, totalAmount);
    }
}

