package model;

/**
 * ExchangeOffer
 * --------------
 * Represents a vehicle exchange offer submitted by a user.
 */
public class ExchangeOffer {

    // --- Fields ---
    private int offerId;
    private int vehicleId;
    private double exchangeValue;
    private double subsidyPercent;
    private String status;

    // --- Constructors ---

    /** Default constructor */
    public ExchangeOffer() {}

    /**
     * Parameterized constructor
     * @param offerId ID of the offer
     * @param vehicleId ID of the vehicle
     * @param exchangeValue Calculated exchange value
     * @param subsidyPercent Subsidy percentage applied
     * @param status Current status of the offer (e.g., Pending, Approved, Rejected)
     */
    public ExchangeOffer(int offerId, int vehicleId, double exchangeValue, double subsidyPercent, String status) {
        this.offerId = offerId;
        this.vehicleId = vehicleId;
        this.exchangeValue = exchangeValue;
        this.subsidyPercent = subsidyPercent;
        this.status = status;
    }

    // --- Getters & Setters ---

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public double getExchangeValue() {
        return exchangeValue;
    }

    public void setExchangeValue(double exchangeValue) {
        this.exchangeValue = exchangeValue;
    }

    public double getSubsidyPercent() {
        return subsidyPercent;
    }

    public void setSubsidyPercent(double subsidyPercent) {
        this.subsidyPercent = subsidyPercent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // --- Optional: toString for easy logging ---
    @Override
    public String toString() {
        return "ExchangeOffer{" +
                "offerId=" + offerId +
                ", vehicleId=" + vehicleId +
                ", exchangeValue=" + exchangeValue +
                ", subsidyPercent=" + subsidyPercent +
                ", status='" + status + '\'' +
                '}';
    }
}
