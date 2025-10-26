package ui;

import db.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * ExchangeOfferForm
 * -----------------
 * Allows users to:
 *  - Check if their vehicle is eligible for an exchange.
 *  - Apply for an exchange offer if eligible.
 */
public class ExchangeOfferForm extends Frame implements ActionListener {

    private int userId;
    private TextField tfVehicleId, tfExchangeValue, tfSubsidy;
    private Button btnCheck, btnApply;
    private Label lblMessage;

    // --- Constructor ---
    public ExchangeOfferForm(int userId) {
        this.userId = userId;

        setTitle("GVEI - Vehicle Exchange Offer");
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        setSize(420, 280);
        setResizable(false);
        setBackground(new Color(245, 245, 245));

        // --- Vehicle Input ---
        add(new Label("Your Vehicle ID:"));
        tfVehicleId = new TextField(10);
        add(tfVehicleId);

        btnCheck = new Button("Check Eligibility");
        btnCheck.addActionListener(this);
        btnCheck.setBackground(new Color(0, 120, 215));
        btnCheck.setForeground(Color.WHITE);
        add(btnCheck);

        // --- Result Fields ---
        add(new Label("Calculated Exchange Value:"));
        tfExchangeValue = new TextField(15);
        tfExchangeValue.setEditable(false);
        add(tfExchangeValue);

        add(new Label("Subsidy (%):"));
        tfSubsidy = new TextField(8);
        tfSubsidy.setEditable(false);
        add(tfSubsidy);

        // --- Apply Button ---
        btnApply = new Button("Apply for Exchange");
        btnApply.addActionListener(this);
        btnApply.setBackground(new Color(0, 180, 80));
        btnApply.setForeground(Color.WHITE);
        add(btnApply);

        // --- Message Label ---
        lblMessage = new Label(" ");
        lblMessage.setForeground(Color.DARK_GRAY);
        add(lblMessage);

        // --- Window close action ---
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    // --- Event Handling ---
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCheck) {
            checkEligibility();
        } else if (e.getSource() == btnApply) {
            applyForExchange();
        }
    }

    // ------------------------------------------------------------
    //  METHOD: Check vehicle eligibility
    // ------------------------------------------------------------
    private void checkEligibility() {
        String vidStr = tfVehicleId.getText().trim();

        if (vidStr.isEmpty()) {
            lblMessage.setText("⚠️ Please enter your Vehicle ID first.");
            return;
        }

        int vehicleId;
        try {
            vehicleId = Integer.parseInt(vidStr);
        } catch (NumberFormatException ex) {
            lblMessage.setText("❌ Invalid Vehicle ID.");
            return;
        }

        String sql = "SELECT owner_id, fuel_type, year FROM vehicles WHERE vehicle_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    lblMessage.setText("❌ Vehicle not found.");
                    return;
                }

                int ownerId = rs.getInt("owner_id");
                String fuelType = rs.getString("fuel_type");
                int year = rs.getInt("year");

                // Validate ownership
                if (ownerId != userId) {
                    lblMessage.setText("⚠️ This vehicle does not belong to your account.");
                    return;
                }

                // Determine eligibility
                int currentYear = java.time.Year.now().getValue();
                int age = currentYear - year;
                boolean eligible = (fuelType.equalsIgnoreCase("Petrol") ||
                        fuelType.equalsIgnoreCase("Diesel"))
                        && (age > 5);

                if (eligible) {
                    double baseValue = 10000.0;
                    double depreciation = age * 800.0;
                    double exchangeValue = Math.max(500.0, baseValue - depreciation);
                    double subsidyPercent = 20.0; // Example fixed subsidy rate

                    tfExchangeValue.setText(String.format("%.2f", exchangeValue));
                    tfSubsidy.setText(String.format("%.2f", subsidyPercent));
                    lblMessage.setText("✅ Your vehicle is eligible for exchange.");
                } else {
                    tfExchangeValue.setText("");
                    tfSubsidy.setText("");
                    lblMessage.setText("❌ Vehicle not eligible for exchange.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblMessage.setText("⚠️ Database Error: " + ex.getMessage());
        }
    }

    // ------------------------------------------------------------
    //  METHOD: Apply for vehicle exchange
    // ------------------------------------------------------------
    private void applyForExchange() {
        String vidStr = tfVehicleId.getText().trim();
        String valueStr = tfExchangeValue.getText().trim();
        String subStr = tfSubsidy.getText().trim();

        // Must check eligibility first
        if (vidStr.isEmpty() || valueStr.isEmpty() || subStr.isEmpty()) {
            lblMessage.setText("⚠️ Please check eligibility before applying.");
            return;
        }

        try {
            int vehicleId = Integer.parseInt(vidStr);
            double exchangeValue = Double.parseDouble(valueStr);
            double subsidy = Double.parseDouble(subStr);

            String sql = "INSERT INTO exchange_offers (vehicle_id, exchange_value, subsidy_percent, status) VALUES (?, ?, ?, 'Pending')";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, vehicleId);
                ps.setDouble(2, exchangeValue);
                ps.setDouble(3, subsidy);
                ps.executeUpdate();

                lblMessage.setText("✅ Exchange application submitted successfully. Await admin approval.");
                tfVehicleId.setText("");
                tfExchangeValue.setText("");
                tfSubsidy.setText("");

            }
        } catch (NumberFormatException ex) {
            lblMessage.setText("❌ Invalid numeric data. Please check your input.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblMessage.setText("⚠️ Database Error: " + ex.getMessage());
        }
    }
}
