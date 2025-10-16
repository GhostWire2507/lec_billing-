package lecbilling.mokopanemakhetha;

import javafx.beans.property.*;

public class Customer {
    private final StringProperty customerId;
    private final StringProperty name;
    private final StringProperty address;
    private final StringProperty meterNumber;
    private final DoubleProperty electricityUsage;
    private final DoubleProperty billAmount;

    public Customer(String customerId, String name, String address, String meterNumber) {
        this.customerId = new SimpleStringProperty(customerId);
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
        this.meterNumber = new SimpleStringProperty(meterNumber);
        this.electricityUsage = new SimpleDoubleProperty(0.0);
        this.billAmount = new SimpleDoubleProperty(0.0);
    }

    // Property getters
    public StringProperty customerIdProperty() { return customerId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty addressProperty() { return address; }
    public StringProperty meterNumberProperty() { return meterNumber; }
    public DoubleProperty electricityUsageProperty() { return electricityUsage; }
    public DoubleProperty billAmountProperty() { return billAmount; }

    // Getters and Setters
    public String getCustomerId() { return customerId.get(); }
    public void setCustomerId(String customerId) { this.customerId.set(customerId); }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    public String getAddress() { return address.get(); }
    public void setAddress(String address) { this.address.set(address); }

    public String getMeterNumber() { return meterNumber.get(); }
    public void setMeterNumber(String meterNumber) { this.meterNumber.set(meterNumber); }

    public double getElectricityUsage() { return electricityUsage.get(); }
    public void setElectricityUsage(double electricityUsage) { this.electricityUsage.set(electricityUsage); }

    public double getBillAmount() { return billAmount.get(); }
    public void setBillAmount(double billAmount) { this.billAmount.set(billAmount); }
}