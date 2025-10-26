package ui;

import db.DBConnection;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * GVEI - Vehicle Registration Form
 * ---------------------------------------------
 * Allows a logged-in citizen to register their vehicle.
 */
public class VehicleRegistrationForm extends Frame implements ActionListener {

    // --- Fields ---
    private int userId;
    private TextField tfPlate, tfYear, tfMileage;
    private Choice chVehicleType, chFuelType;
    private Button btnRegister, btnClear, btnBack;
    private Label lblMessage;

    // --- Constructor ---
    public VehicleRegistrationForm(int userId) {
        this.userId = userId;

        setTitle("GVEI - Vehicle Registration");
        setSize(460, 400);
        setLayout(new GridBagLayout());
        setResizable(false);
        setBackground(new Color(245, 245, 245));
        setFont(new Font("Arial", Font.PLAIN, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Title ---
        Label lblTitle = new Label("Register Your Vehicle", Label.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // --- Plate Number ---
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new Label("Plate Number:"), gbc);

        tfPlate = new TextField(25);
        gbc.gridx = 1;
        add(tfPlate, gbc);

        // --- Vehicle Type ---
        gbc.gridx = 0; gbc.gridy = 2;
        add(new Label("Vehicle Type:"), gbc);

        chVehicleType = new Choice();
        chVehicleType.add("Car");
        chVehicleType.add("Bus");
        chVehicleType.add("Motorcycle");
        chVehicleType.add("Truck");
        gbc.gridx = 1;
        add(chVehicleType, gbc);

        // --- Fuel Type ---
        gbc.gridx = 0; gbc.gridy = 3;
        add(new Label("Fuel Type:"), gbc);

        chFuelType = new Choice();
        chFuelType.add("Petrol");
        chFuelType.add("Diesel");
        chFuelType.add("Hybrid");
        chFuelType.add("Electric");
        gbc.gridx = 1;
        add(chFuelType, gbc);

        // --- Manufacture Year ---
        gbc.gridx = 0; gbc.gridy = 4;
        add(new Label("Manufacture Year:"), gbc);

        tfYear = new TextField(10);
        gbc.gridx = 1;
        add(tfYear, gbc);

        // --- Mileage ---
        gbc.gridx = 0; gbc.gridy = 5;
        add(new Label("Estimated Mileage (km):"), gbc);

        tfMileage = new TextField(10);
        gbc.gridx = 1;
        add(tfMileage, gbc);

        // --- Buttons Panel ---
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        btnRegister = new Button("Register Vehicle");
        btnClear = new Button("Clear");
        btnBack = new Button("Back");

        btnRegister.setBackground(new Color(0, 153, 76));
        btnRegister.setForeground(Color.WHITE);
        btnClear.setBackground(new Color(255, 140, 0));
        btnClear.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(0, 102, 204));
        btnBack.setForeground(Color.WHITE);

        btnRegister.addActionListener(this);
        btnClear.addActionListener(this);
        btnBack.addActionListener(this);

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // --- Message Label ---
        lblMessage = new Label(" ", Label.CENTER);
        lblMessage.setForeground(Color.RED);
        gbc.gridy = 7;
        add(lblMessage, gbc);

        // --- Window Close ---
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Handle Button Actions ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnRegister) {
            registerVehicle();
        } else if (src == btnClear) {
            clearFields();
        } else if (src == btnBack) {
            dispose();
            // Optionally go back to previous form (e.g., ExchangeOfferForm or Login)
        }
    }

    // --- Clear Input Fields ---
    private void clearFields() {
        tfPlate.setText("");
        tfYear.setText("");
        tfMileage.setText("");
        chVehicleType.select(0);
        chFuelType.select(0);
        lblMessage.setText(" ");
    }

    // --- Register Vehicle Logic ---
    private void registerVehicle() {
        String plate = tfPlate.getText().trim();
        String vehicleType = chVehicleType.getSelectedItem();
        String fuelType = chFuelType.getSelectedItem();
        String yearStr = tfYear.getText().trim();
        String mileageStr = tfMileage.getText().trim();

        // Validation
        if (plate.isEmpty() || yearStr.isEmpty() || mileageStr.isEmpty()) {
            lblMessage.setText("All fields are required.");
            return;
        }

        int year;
        double mileage;
        try {
            year = Integer.parseInt(yearStr);
            mileage = Double.parseDouble(mileageStr);

            if (year < 1980 || year > 2025) {
                lblMessage.setText("Enter a valid manufacture year (1980–2025).");
                return;
            }

            if (mileage < 0) {
                lblMessage.setText("Mileage must be a positive number.");
                return;
            }
        } catch (NumberFormatException ex) {
            lblMessage.setText("Invalid number for year or mileage.");
            return;
        }

        String sql = "INSERT INTO vehicles (owner_id, plate_no, vehicle_type, fuel_type, year, mileage) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, plate);
            ps.setString(3, vehicleType);
            ps.setString(4, fuelType);
            ps.setInt(5, year);
            ps.setDouble(6, mileage);

            ps.executeUpdate();

            lblMessage.setForeground(new Color(0, 153, 76));
            lblMessage.setText("Vehicle registered successfully!");

            clearFields();

        } catch (SQLIntegrityConstraintViolationException ex) {
            lblMessage.setForeground(Color.RED);
            lblMessage.setText("This plate number is already registered.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblMessage.setForeground(Color.RED);
            lblMessage.setText("Database error: " + ex.getMessage());
        }
    }

    // --- Entry Point for Testing ---
    public static void main(String[] args) {
        new VehicleRegistrationForm(1);
    }
}
