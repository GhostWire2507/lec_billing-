package lecbilling.mokopanemakhetha;

public class BillCalculator {
    private static final double RATE_1 = 1.20;  // 0-100 kWh
    private static final double RATE_2 = 1.50;  // 101-300 kWh
    private static final double RATE_3 = 2.00;  // Above 300 kWh

    public static double calculateBill(double usage) {
        double total = 0.0;

        if (usage > 300) {
            total += (usage - 300) * RATE_3;
            usage = 300;
        }
        if (usage > 100) {
            total += (usage - 100) * RATE_2;
            usage = 100;
        }
        total += usage * RATE_1;

        return total;
    }
}