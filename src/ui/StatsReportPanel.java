package ui;

import db.DBConnection;
import java.awt.*;
import java.sql.*;

/**
 * StatsReportPanel - A reusable AWT Panel to display key business and
 * environmental metrics by querying approved offers. It acts as the "canvas" 
 * for simple statistics display as requested.
 */
public class StatsReportPanel extends Panel {

    // Estimate: 5 metric tonnes of CO2 reduction per exchanged older vehicle.
    private static final double CARBON_REDUCTION_PER_VEHICLE_TONNES = 5.0; 
    
    private int totalApproved = 0;
    private double totalSubsidy = 0;
    private double carbonReduction = 0;

    public StatsReportPanel() {
        setLayout(new BorderLayout()); 
        setBackground(Color.WHITE);
        fetchData(); // Load initial data
    }

    /**
     * Connects to the database and fetches the core statistics.
     */
    public void fetchData() {
        // SQL query to get the count and sum of exchange value for approved offers
        String sql = "SELECT COUNT(*) AS totalApproved, IFNULL(SUM(exchange_value),0) AS totalSubsidy " +
                     "FROM exchange_offers WHERE status='Approved'";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                totalApproved = rs.getInt("totalApproved");
                totalSubsidy = rs.getDouble("totalSubsidy");
            }

            // Calculate Carbon Reduction
            carbonReduction = totalApproved * CARBON_REDUCTION_PER_VEHICLE_TONNES;

        } catch (SQLException ex) {
            System.err.println("DB Error retrieving statistics: " + ex.getMessage());
            // Signal an error state for painting
            totalApproved = -1; 
            totalSubsidy = -1.0; 
            carbonReduction = -1.0; 
        }
    }

    /**
     * Overridden paint method to draw the statistics report.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g); 
        int w = getWidth();
        
        int x = 20;
        int yStart = 30;
        
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.DARK_GRAY);
        g.drawString("📈 Environmental & Financial Reporting", x, yStart);
        
        // Draw Separator Line
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(x, yStart + 10, w - x, yStart + 10);
        
        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        int lineSpacing = 30;
        int currentY = yStart + lineSpacing + 5;

        // Check for DB error 
        if (totalApproved < 0) {
            g.setColor(Color.RED);
            g.drawString("ERROR: Could not connect to database to fetch stats.", x, currentY);
            return;
        }
        
        // 1. Total Exchanged Vehicles
        g.setColor(new Color(0, 100, 0)); // Dark Green
        String exchangedText = String.format("Total Exchanged Vehicles (Approved): %d units", totalApproved);
        g.drawString(exchangedText, x, currentY);

        currentY += lineSpacing;

        // 2. Total Subsidies Paid
        g.setColor(new Color(0, 0, 150)); // Dark Blue
        String subsidyText = String.format("Total Subsidies Paid: $%,.2f", totalSubsidy);
        g.drawString(subsidyText, x, currentY);
        
        currentY += lineSpacing;

        // 3. Estimated Carbon Reduction
        g.setColor(new Color(150, 0, 0)); // Dark Red
        String carbonText = String.format("Estimated Carbon Reduction: %,.2f Metric Tonnes", carbonReduction);
        g.drawString(carbonText, x, currentY);

        // Disclaimer/Annotation
        currentY += lineSpacing;
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.ITALIC, 10));
        g.drawString("*Carbon Reduction is an estimate based on " + CARBON_REDUCTION_PER_VEHICLE_TONNES + " tonnes/vehicle.", x, currentY);
    }
    
    /**
     * Public method to refresh data and redraw the panel.
     */
    public void refresh() {
        fetchData();
        repaint();
    }
}