package lecbilling.mokopanemakhetha.model;

/**
 * Model class for report data
 */
public class ReportData {
    private String label;
    private double value;
    private int count;

    public ReportData() {
    }

    public ReportData(String label, double value) {
        this.label = label;
        this.value = value;
    }

    public ReportData(String label, double value, int count) {
        this.label = label;
        this.value = value;
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return String.format("ReportData{label='%s', value=%.2f, count=%d}", label, value, count);
    }
}

