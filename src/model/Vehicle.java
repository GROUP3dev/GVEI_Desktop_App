package model;

/**
 * Vehicle
 * -------
 * Represents a vehicle registered by a user in the GVEI system.
 */
public class Vehicle {

    // --- Fields ---
    private int vehicleId;
    private int ownerId;
    private String plateNo;
    private String vehicleType;
    private String fuelType;
    private int year;
    private double mileage;

    // --- Constructors ---

    /** Default constructor */
    public Vehicle() {}

    /**
     * Parameterized constructor
     * @param vehicleId Unique ID of the vehicle
     * @param ownerId ID of the vehicle owner (user)
     * @param plateNo Vehicle plate number
     * @param vehicleType Type of vehicle (Car, Bus, Motorcycle, etc.)
     * @param fuelType Fuel type (Petrol, Diesel, Electric, etc.)
     * @param year Year of manufacture
     * @param mileage Current mileage of the vehicle
     */
    public Vehicle(int vehicleId, int ownerId, String plateNo, String vehicleType, String fuelType, int year, double mileage) {
        this.vehicleId = vehicleId;
        this.ownerId = ownerId;
        this.plateNo = plateNo;
        this.vehicleType = vehicleType;
        this.fuelType = fuelType;
        this.year = year;
        this.mileage = mileage;
    }

    // --- Getters & Setters ---

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    // --- Optional: toString for debugging ---
    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + vehicleId +
                ", ownerId=" + ownerId +
                ", plateNo='" + plateNo + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", year=" + year +
                ", mileage=" + mileage +
                '}';
    }
}
