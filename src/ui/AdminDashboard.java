package ui;

import db.DBConnection; // Assumes you have this class
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Admin Dashboard - AWT Version (Full CRUD Refactor)
 * -----------------
 * - Top MenuBar for navigation.
 * - View users, vehicles, exchange offers in a formatted TextArea.
 * - Context-aware CRUD buttons (Add, Update, Delete) at the bottom.
 * - Safe operations using modal Dialogs for add, update, and delete confirmation.
 * - Approve/reject selected offers (specific to Offers view).
 * - Horizontal bar chart showing both count and total value of approved offers.
 */
public class AdminDashboard extends Frame implements ActionListener {

    // --- State & Components ---
    private TextArea taData;
    private Button btnLoadUsers, btnLoadVehicles, btnLoadOffers;
    private Button btnApprove, btnReject; // For offers
    private Button btnAdd, btnUpdate, btnDelete; // CRUD Buttons
    private Button btnShowChart, btnHideChart, btnRefreshStats;
    private StatsCanvas statsCanvas;
    private Label lblStatus; // Status bar

    /**
     * Tracks the current data being viewed ("USERS", "VEHICLES", "OFFERS")
     * to make the CRUD buttons context-aware.
     */
    private String currentView = "";

    // --- MenuBar Components ---
    private MenuBar menuBar;
    private Menu menuDashboard, menuUsers, menuVehicles, menuOffers;
    private MenuItem itemShowStats, itemHideStats, itemRefreshStats, itemExit;
    private MenuItem itemLoadUsers, itemAddUser;
    private MenuItem itemLoadVehicles, itemAddVehicle;
    private MenuItem itemLoadOffers;

    public AdminDashboard() {
        setTitle("GVEI - Admin Dashboard");
        setSize(900, 700); // Increased height for new buttons/status
        setLayout(new BorderLayout());
        setResizable(false);

        // --- 1. Top: MenuBar ---
        setupMenuBar();
        setMenuBar(menuBar);

        // --- 2. North: Stats Canvas (initially hidden) ---
        statsCanvas = new StatsCanvas();
        statsCanvas.setPreferredSize(new Dimension(900, 180)); // Taller for 2 bars
        statsCanvas.setVisible(false);
        add(statsCanvas, BorderLayout.NORTH);

        // --- 3. Center: Data TextArea ---
        taData = new TextArea(20, 100);
        taData.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Use Monospaced font
        add(taData, BorderLayout.CENTER);

        // --- 4. South: Button Panels & Status ---
        Panel southPanel = new Panel(new BorderLayout());

        // Panel for main data-loading and offer buttons
        Panel panelDataButtons = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnLoadUsers = new Button("View All Users");
        btnLoadUsers.addActionListener(this);
        panelDataButtons.add(btnLoadUsers);

        btnLoadVehicles = new Button("View All Vehicles");
        btnLoadVehicles.addActionListener(this);
        panelDataButtons.add(btnLoadVehicles);

        btnLoadOffers = new Button("View Pending Offers");
        btnLoadOffers.addActionListener(this);
        panelDataButtons.add(btnLoadOffers);

        panelDataButtons.add(new Label(" | ")); // Separator

        btnApprove = new Button("Approve Selected Offer");
        btnApprove.addActionListener(this);
        panelDataButtons.add(btnApprove);

        btnReject = new Button("Reject Selected Offer");
        btnReject.addActionListener(this);
        panelDataButtons.add(btnReject);

        // Panel for new CRUD buttons
        Panel panelCrudButtons = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnAdd = new Button("Add New...");
        btnAdd.addActionListener(this);
        panelCrudButtons.add(btnAdd);

        btnUpdate = new Button("Update Selected...");
        btnUpdate.addActionListener(this);
        panelCrudButtons.add(btnUpdate);

        btnDelete = new Button("Delete Selected");
        btnDelete.addActionListener(this);
        panelCrudButtons.add(btnDelete);

        panelCrudButtons.add(new Label(" | ")); // Separator

        // Panel for Chart controls
        btnShowChart = new Button("Show Chart");
        btnShowChart.addActionListener(this);
        panelCrudButtons.add(btnShowChart);

        btnHideChart = new Button("Hide Chart");
        btnHideChart.addActionListener(this);
        panelCrudButtons.add(btnHideChart);

        btnRefreshStats = new Button("Refresh Statistics");
        btnRefreshStats.addActionListener(this);
        panelCrudButtons.add(btnRefreshStats);

        // Status bar label
        lblStatus = new Label("Welcome to the Admin Dashboard. Please load some data.", Label.CENTER);
        lblStatus.setBackground(Color.LIGHT_GRAY);

        // Add all button panels and status bar to the south panel
        Panel buttonContainer = new Panel(new GridLayout(2, 1));
        buttonContainer.add(panelDataButtons);
        buttonContainer.add(panelCrudButtons);

        southPanel.add(buttonContainer, BorderLayout.NORTH);
        southPanel.add(lblStatus, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // --- Close Window ---
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                // new LoginForm(); // uncomment if login form exists
                System.exit(0); // Force exit for testing
            }
        });

        // Initial state
        updateButtonStates();
        setVisible(true);
        loadPendingOffers(); // Load offers by default
    }

    private void setupMenuBar() {
        menuBar = new MenuBar();

        // --- Dashboard Menu ---
        menuDashboard = new Menu("Dashboard");
        itemShowStats = new MenuItem("Show Stats Chart");
        itemShowStats.addActionListener(this);
        menuDashboard.add(itemShowStats);

        itemHideStats = new MenuItem("Hide Stats Chart");
        itemHideStats.addActionListener(this);
        menuDashboard.add(itemHideStats);

        itemRefreshStats = new MenuItem("Refresh Stats");
        itemRefreshStats.addActionListener(this);
        menuDashboard.add(itemRefreshStats);

        menuDashboard.addSeparator();
        itemExit = new MenuItem("Exit");
        itemExit.addActionListener(e -> System.exit(0));
        menuDashboard.add(itemExit);

        // --- Users Menu ---
        menuUsers = new Menu("Manage Users");
        itemLoadUsers = new MenuItem("View All Users");
        itemLoadUsers.addActionListener(this);
        menuUsers.add(itemLoadUsers);

        itemAddUser = new MenuItem("Add New User...");
        itemAddUser.addActionListener(this);
        menuUsers.add(itemAddUser);

        // --- Vehicles Menu ---
        menuVehicles = new Menu("Manage Vehicles");
        itemLoadVehicles = new MenuItem("View All Vehicles");
        itemLoadVehicles.addActionListener(this);
        menuVehicles.add(itemLoadVehicles);

        itemAddVehicle = new MenuItem("Add New Vehicle...");
        itemAddVehicle.addActionListener(this);
        menuVehicles.add(itemAddVehicle);

        // --- Offers Menu ---
        menuOffers = new Menu("Manage Offers");
        itemLoadOffers = new MenuItem("View Pending Offers");
        itemLoadOffers.addActionListener(this);
        menuOffers.add(itemLoadOffers);

        // Add menus to bar
        menuBar.add(menuDashboard);
        menuBar.add(menuUsers);
        menuBar.add(menuVehicles);
        menuBar.add(menuOffers);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // Menu or Button actions for loading data
        if (source == btnLoadUsers || source == itemLoadUsers) loadUsers();
        else if (source == btnLoadVehicles || source == itemLoadVehicles) loadVehicles();
        else if (source == btnLoadOffers || source == itemLoadOffers) loadPendingOffers();

            // Offer-specific actions
        else if (source == btnApprove) processSelectedOffer("Approved");
        else if (source == btnReject) processSelectedOffer("Rejected");

            // Chart controls
        else if (source == btnShowChart || source == itemShowStats) statsCanvas.setVisible(true);
        else if (source == btnHideChart || source == itemHideStats) statsCanvas.setVisible(false);
        else if (source == btnRefreshStats || source == itemRefreshStats) {
            statsCanvas.repaint();
            lblStatus.setText("Statistics refreshed.");
        }

        // CRUD Actions
        else if (source == btnAdd) handleAdd();
        else if (source == btnUpdate) handleUpdate();
        else if (source == btnDelete) handleDelete();

            // Menu "Add" shortcuts
        else if (source == itemAddUser) {
            loadUsers(); // Switch to user view first
            handleAdd(); // Open AddUserDialog
        } else if (source == itemAddVehicle) {
            loadVehicles(); // Switch to vehicle view first
            handleAdd(); // Open AddVehicleDialog
        }
    }

    /**
     * Updates the enabled/disabled state of buttons based on the currentView.
     */
    private void updateButtonStates() {
        switch (currentView) {
            case "USERS":
                btnApprove.setEnabled(false);
                btnReject.setEnabled(false);
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnAdd.setLabel("Add User...");
                break;
            case "VEHICLES":
                btnApprove.setEnabled(false);
                btnReject.setEnabled(false);
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnAdd.setLabel("Add Vehicle...");
                break;
            case "OFFERS":
                btnApprove.setEnabled(true);
                btnReject.setEnabled(true);
                // CRUD is disabled for Offers (use Approve/Reject)
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                btnAdd.setLabel("Add New...");
                break;
            default: // No view selected
                btnApprove.setEnabled(false);
                btnReject.setEnabled(false);
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                break;
        }
    }

    /**
     * Helper to get the first word (assumed to be ID) from the selected line.
     * @return The ID as a String, or null if no line is selected or parsable.
     */
    private String getSelectedId() {
        String selectedLine = taData.getSelectedText();

        // If no text is highlighted, try to get the line at the cursor
        if (selectedLine == null || selectedLine.isEmpty()) {
            try {
                int caretPos = taData.getCaretPosition();
                int lineStart = taData.getText().lastIndexOf('\n', caretPos - 1) + 1;
                int lineEnd = taData.getText().indexOf('\n', caretPos);
                if (lineEnd == -1) lineEnd = taData.getText().length(); // Last line

                selectedLine = taData.getText().substring(lineStart, lineEnd);
            } catch (Exception ex) {
                lblStatus.setText("Could not determine selected line.");
                return null;
            }
        }

        // Ignore header lines
        if (selectedLine.trim().startsWith("ID") || selectedLine.trim().startsWith("---")) {
            lblStatus.setText("Please select a valid data row (not a header).");
            return null;
        }

        String[] parts = selectedLine.trim().split("\\s+");
        if (parts.length > 0 && !parts[0].isEmpty()) {
            return parts[0]; // Return the first part, which is the ID
        } else {
            lblStatus.setText("Could not parse ID from selected line.");
            return null;
        }
    }

    // --- Data Loading Methods ---

    private void loadUsers() {
        currentView = "USERS";
        updateButtonStates();
        StringBuilder sb = new StringBuilder();
        // Header
        sb.append(String.format("%-5s | %-20s | %-30s | %-10s%n", "ID", "Name", "Email", "Role"));
        sb.append("------+----------------------+--------------------------------+-----------\n");
        String sql = "SELECT user_id, name, email, role FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int count = 0;
            while (rs.next()) {
                sb.append(String.format("%-5d | %-20s | %-30s | %-10s%n",
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")));
                count++;
            }
            taData.setText(sb.toString());
            lblStatus.setText("Loaded " + count + " users.");
        } catch (SQLException ex) {
            taData.setText("DB Error: " + ex.getMessage());
            lblStatus.setText("DB Error loading users.");
        }
    }

    private void loadVehicles() {
        currentView = "VEHICLES";
        updateButtonStates();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-10s | %-12s | %-10s | %-5s | %-10s | %-5s | %-15s%n",
                "ID", "Plate", "Type", "Fuel", "Year", "Mileage", "OwnerID", "Owner Name"));
        sb.append("------+------------+--------------+------------+-------+------------+---------+----------------\n");
        String sql = "SELECT v.vehicle_id, v.plate_no, v.vehicle_type, v.fuel_type, v.year, v.mileage, v.owner_id, u.name AS owner " +
                "FROM vehicles v LEFT JOIN users u ON v.owner_id = u.user_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                sb.append(String.format("%-5d | %-10s | %-12s | %-10s | %-5d | %-10.2f | %-7d | %-15s%n",
                        rs.getInt("vehicle_id"),
                        rs.getString("plate_no"),
                        rs.getString("vehicle_type"),
                        rs.getString("fuel_type"),
                        rs.getInt("year"),
                        rs.getDouble("mileage"),
                        rs.getInt("owner_id"),
                        rs.getString("owner") != null ? rs.getString("owner") : "N/A"));
                count++;
            }
            taData.setText(sb.toString());
            lblStatus.setText("Loaded " + count + " vehicles.");
        } catch (SQLException ex) {
            taData.setText("DB Error: " + ex.getMessage());
            lblStatus.setText("DB Error loading vehicles.");
        }
    }

    private void loadPendingOffers() {
        currentView = "OFFERS";
        updateButtonStates();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-9s | %-10s | %-10s | %-10s | %-12s | %-15s%n",
                "ID", "VehicleID", "Value", "Subsidy%", "Plate", "Type", "Owner"));
        sb.append("------+-----------+------------+------------+------------+--------------+----------------\n");
        String sql = """
            SELECT eo.offer_id, eo.vehicle_id, eo.exchange_value, eo.subsidy_percent,
                   v.plate_no, v.vehicle_type, u.name
            FROM exchange_offers eo
            LEFT JOIN vehicles v ON eo.vehicle_id = v.vehicle_id
            LEFT JOIN users u ON v.owner_id = u.user_id
            WHERE eo.status = 'Pending'
        """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                sb.append(String.format("%-5d | %-9d | %-10.2f | %-10.2f | %-10s | %-12s | %-15s%n",
                        rs.getInt("offer_id"),
                        rs.getInt("vehicle_id"),
                        rs.getDouble("exchange_value"),
                        rs.getDouble("subsidy_percent"),
                        rs.getString("plate_no") != null ? rs.getString("plate_no") : "N/A",
                        rs.getString("vehicle_type") != null ? rs.getString("vehicle_type") : "N/A",
                        rs.getString("name") != null ? rs.getString("name") : "N/A"));
                count++;
            }
            taData.setText(sb.toString());
            lblStatus.setText("Loaded " + count + " pending offers.");
        } catch (SQLException ex) {
            taData.setText("DB Error: " + ex.getMessage());
            lblStatus.setText("DB Error loading offers.");
        }
    }

    // --- Specific Action Methods (Approve/Reject) ---

    private void processSelectedOffer(String newStatus) {
        if (!currentView.equals("OFFERS")) {
            lblStatus.setText("Please load Offers to approve or reject.");
            return;
        }

        String idStr = getSelectedId();
        if (idStr == null) return; // Error message already set by getSelectedId()

        try {
            int offerId = Integer.parseInt(idStr);
            String sql = "UPDATE exchange_offers SET status=? WHERE offer_id=?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, offerId);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    lblStatus.setText("✅ Offer " + offerId + " set to " + newStatus + ".");
                    loadPendingOffers(); // Refresh list
                    statsCanvas.repaint(); // Refresh chart
                } else {
                    lblStatus.setText("Offer " + offerId + " not found or not updated.");
                }
            }
        } catch (NumberFormatException ex) {
            lblStatus.setText("Error: Selected ID '" + idStr + "' is not a valid number.");
        } catch (SQLException ex) {
            lblStatus.setText("DB Error: " + ex.getMessage());
        }
    }

    // --- CRUD Handlers ---

    private void handleAdd() {
        switch (currentView) {
            case "USERS":
                // Open the Add User Dialog
                new AddUserDialog(this);
                break;
            case "VEHICLES":
                // Open the Add Vehicle Dialog
                new AddVehicleDialog(this);
                break;
            default:
                lblStatus.setText("No 'Add' action available for this view.");
        }
    }

    private void handleUpdate() {
        String idStr = getSelectedId();
        if (idStr == null) return;

        // We need the *full line* of data to pre-fill the dialog
        String selectedLine = taData.getSelectedText();
        if (selectedLine == null || selectedLine.isEmpty()) {
            lblStatus.setText("Please highlight the full line to update.");
            return;
        }

        switch (currentView) {
            case "USERS":
                // Open the Update User Dialog
                new UpdateUserDialog(this, selectedLine.trim());
                break;
            case "VEHICLES":
                // Open the Update Vehicle Dialog
                new UpdateVehicleDialog(this, selectedLine.trim());
                break;
            default:
                lblStatus.setText("No 'Update' action available for this view.");
        }
    }

    private void handleDelete() {
        String idStr = getSelectedId();
        if (idStr == null) return;

        // Show confirmation dialog
        ConfirmDialog confirmDialog = new ConfirmDialog(this,
                "Delete Record?",
                "Are you sure you want to delete " + currentView + " record ID: " + idStr + "?");

        if (!confirmDialog.isConfirmed()) {
            lblStatus.setText("Delete operation canceled.");
            return;
        }

        // User confirmed, proceed with delete
        String table = "";
        String idColumn = "";
        switch (currentView) {
            case "USERS":
                table = "users";
                idColumn = "user_id";
                break;
            case "VEHICLES":
                table = "vehicles";
                idColumn = "vehicle_id";
                break;
            default:
                lblStatus.setText("No 'Delete' action available for this view.");
                return;
        }

        String sql = "DELETE FROM " + table + " WHERE " + idColumn + " = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(idStr));
            int deleted = ps.executeUpdate();

            if (deleted > 0) {
                lblStatus.setText("✅ Record " + idStr + " deleted from " + table + ".");
                // Refresh the view
                if (currentView.equals("USERS")) loadUsers();
                if (currentView.equals("VEHICLES")) loadVehicles();
            } else {
                lblStatus.setText("Record " + idStr + " not found or not deleted.");
            }
        } catch (NumberFormatException ex) {
            lblStatus.setText("Error: Selected ID '" + idStr + "' is not a valid number.");
        } catch (SQLException ex) {
            // Handle foreign key constraint violations
            if (ex.getMessage().contains("foreign key constraint")) {
                lblStatus.setText("Error: Cannot delete record " + idStr + ". It is being used by other records.");
            } else {
                lblStatus.setText("DB Error: " + ex.getMessage());
            }
        }
    }

    // --- Inner class: Stats Canvas ---
    class StatsCanvas extends Canvas {
        @Override
        public void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            g.setColor(Color.WHITE); // Clear background
            g.fillRect(0, 0, w, h);

            g.setColor(Color.BLACK);
            int x = 20, y = 30;
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("📊 Approved Offers Statistics", x, y);

            int totalApproved = 0;
            double totalValue = 0;

            String sql = "SELECT COUNT(*) AS totalApproved, IFNULL(SUM(exchange_value),0) AS totalValue " +
                    "FROM exchange_offers WHERE status='Approved'";
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    totalApproved = rs.getInt("totalApproved");
                    totalValue = rs.getDouble("totalValue");
                }
            } catch (SQLException ex) {
                g.setColor(Color.RED);
                g.drawString("DB Error: " + ex.getMessage(), x, y + 25);
                return;
            }

            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.setColor(Color.BLACK);

            // --- Bar 1: Count ---
            String countLabel = "Total Approved Offers: " + totalApproved;
            g.drawString(countLabel, x, y + 25);

            // Scale: 20 pixels per offer, max 800 pixels
            int countBarLength = Math.min(800, totalApproved * 20);
            g.drawRect(x, y + 40, 820, 25); // Bar outline
            g.setColor(Color.GREEN);
            g.fillRect(x + 1, y + 41, countBarLength, 24);

            // --- Bar 2: Value ---
            g.setColor(Color.BLACK);
            String valueLabel = "Total Exchange Value: $" + String.format("%.2f", totalValue);
            g.drawString(valueLabel, x, y + 85);

            // Scale: 1 pixel per $1000, max 800 pixels
            int valueBarLength = Math.min(800, (int)(totalValue / 1000));
            g.drawRect(x, y + 100, 820, 25); // Bar outline
            g.setColor(Color.BLUE);
            g.fillRect(x + 1, y + 101, valueBarLength, 24);

            // Legend
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.ITALIC, 12));
            g.drawString("Green Bar: Offer Count (1 offer = 20px). Blue Bar: Total Value (1k = 1px).", x, y + 140);
        }
    }

    // --- Helper Dialog Classes ---

    /**
     * A generic, modal "Are you sure?" dialog.
     */
    class ConfirmDialog extends Dialog implements ActionListener {
        private boolean confirmed = false;

        ConfirmDialog(Frame owner, String title, String message) {
            super(owner, title, true); // true = modal
            setLayout(new BorderLayout(10, 10));

            add(new Label(message, Label.CENTER), BorderLayout.CENTER);

            Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
            Button btnYes = new Button("Yes");
            Button btnNo = new Button("No");

            btnYes.addActionListener(e -> {
                confirmed = true;
                dispose();
            });
            btnNo.addActionListener(e -> {
                confirmed = false;
                dispose();
            });

            buttonPanel.add(btnYes);
            buttonPanel.add(btnNo);
            add(buttonPanel, BorderLayout.SOUTH);

            setSize(400, 150);
            setLocationRelativeTo(owner);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
            setVisible(true); // Blocks until disposed
        }

        public boolean isConfirmed() { return confirmed; }

        @Override
        public void actionPerformed(ActionEvent e) { /* Handled by lambdas */ }
    }

    /**
     * A generic base class for Add/Update dialogs
     */
    abstract class CrudDialog extends Dialog implements ActionListener {
        protected Panel gridPanel;
        protected Button btnSave, btnCancel;
        protected Label lblError;
        protected AdminDashboard parent; // To call refresh methods

        CrudDialog(Frame owner, String title) {
            super(owner, title, true); // Modal
            this.parent = (AdminDashboard) owner;
            setLayout(new BorderLayout(10, 10));

            gridPanel = new Panel(new GridLayout(0, 2, 5, 5)); // 0 rows = dynamic
            add(gridPanel, BorderLayout.CENTER);

            lblError = new Label("", Label.CENTER);
            lblError.setForeground(Color.RED);
            add(lblError, BorderLayout.NORTH);

            Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
            btnSave = new Button("Save");
            btnSave.addActionListener(this);
            buttonPanel.add(btnSave);

            btnCancel = new Button("Cancel");
            btnCancel.addActionListener(e -> dispose());
            buttonPanel.add(btnCancel);

            add(buttonPanel, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
        }

        protected void addField(String label, Component field) {
            gridPanel.add(new Label(label, Label.RIGHT));
            gridPanel.add(field);
        }
    }

    // --- Dialog for Adding a User ---
    class AddUserDialog extends CrudDialog {
        TextField txtName, txtEmail, txtRole;

        AddUserDialog(Frame owner) {
            super(owner, "Add New User");

            txtName = new TextField(30);
            txtEmail = new TextField(30);
            txtRole = new TextField(10);

            addField("Name:", txtName);
            addField("Email:", txtEmail);
            addField("Role (admin/user):", txtRole);

            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Save button clicked
            String name = txtName.getText();
            String email = txtEmail.getText();
            String role = txtRole.getText();

            if (name.isEmpty() || email.isEmpty() || role.isEmpty()) {
                lblError.setText("All fields are required.");
                return;
            }

            String sql = "INSERT INTO users (name, email, role, password) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, role);
                ps.setString(4, "temp_pass"); // Default password

                ps.executeUpdate();
                parent.lblStatus.setText("✅ User '" + name + "' added.");
                parent.loadUsers(); // Refresh the list
                dispose();

            } catch (SQLException ex) {
                lblError.setText("DB Error: " + ex.getMessage());
            }
        }
    }

    // --- Dialog for Updating a User ---
    class UpdateUserDialog extends CrudDialog {
        TextField txtName, txtEmail, txtRole;
        int userId;

        UpdateUserDialog(Frame owner, String selectedLine) {
            super(owner, "Update User");

            txtName = new TextField(30);
            txtEmail = new TextField(30);
            txtRole = new TextField(10);

            addField("Name:", txtName);
            addField("Email:", txtEmail);
            addField("Role (admin/user):", txtRole);

            // Parse the selected line to pre-fill fields
            try {
                // "ID | Name | Email | Role"
                String[] parts = selectedLine.split("\\s*\\|\\s*");
                this.userId = Integer.parseInt(parts[0].trim());
                txtName.setText(parts[1].trim());
                txtEmail.setText(parts[2].trim());
                txtRole.setText(parts[3].trim());
            } catch (Exception ex) {
                lblError.setText("Error parsing selected line: " + ex.getMessage());
                btnSave.setEnabled(false);
            }

            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Save button clicked
            String name = txtName.getText();
            String email = txtEmail.getText();
            String role = txtRole.getText();

            if (name.isEmpty() || email.isEmpty() || role.isEmpty()) {
                lblError.setText("All fields are required.");
                return;
            }

            String sql = "UPDATE users SET name = ?, email = ?, role = ? WHERE user_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, role);
                ps.setInt(4, this.userId);

                ps.executeUpdate();
                parent.lblStatus.setText("✅ User " + this.userId + " updated.");
                parent.loadUsers(); // Refresh the list
                dispose();

            } catch (SQLException ex) {
                lblError.setText("DB Error: ".concat(ex.getMessage()));
            }
        }
    }

    // --- Dialog for Adding a Vehicle ---
    class AddVehicleDialog extends CrudDialog {
        TextField txtPlate, txtType, txtFuel, txtYear, txtMileage, txtOwnerId;

        AddVehicleDialog(Frame owner) {
            super(owner, "Add New Vehicle");

            txtPlate = new TextField(10);
            txtType = new TextField(15);
            txtFuel = new TextField(10);
            txtYear = new TextField(4);
            txtMileage = new TextField(10);
            txtOwnerId = new TextField(5);

            addField("Plate No:", txtPlate);
            addField("Type (e.g., Sedan):", txtType);
            addField("Fuel (e.g., Petrol):", txtFuel);
            addField("Year:", txtYear);
            addField("Mileage:", txtMileage);
            addField("Owner User ID:", txtOwnerId);

            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Save button clicked
            try {
                String sql = "INSERT INTO vehicles (plate_no, vehicle_type, fuel_type, year, mileage, owner_id) VALUES (?, ?, ?, ?, ?, ?)";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, txtPlate.getText());
                    ps.setString(2, txtType.getText());
                    ps.setString(3, txtFuel.getText());
                    ps.setInt(4, Integer.parseInt(txtYear.getText()));
                    ps.setDouble(5, Double.parseDouble(txtMileage.getText()));
                    ps.setInt(6, Integer.parseInt(txtOwnerId.getText()));

                    ps.executeUpdate();
                    parent.lblStatus.setText("✅ Vehicle '" + txtPlate.getText() + "' added.");
                    parent.loadVehicles(); // Refresh the list
                    dispose();
                }
            } catch (NumberFormatException nfe) {
                lblError.setText("Year, Mileage, and Owner ID must be valid numbers.");
            } catch (SQLException ex) {
                if(ex.getMessage().contains("foreign key constraint")) {
                    lblError.setText("DB Error: Owner ID " + txtOwnerId.getText() + " does not exist.");
                } else {
                    lblError.setText("DB Error: " + ex.getMessage());
                }
            }
        }
    }

    // --- Dialog for Updating a Vehicle ---
    class UpdateVehicleDialog extends CrudDialog {
        TextField txtPlate, txtType, txtFuel, txtYear, txtMileage, txtOwnerId;
        int vehicleId;

        UpdateVehicleDialog(Frame owner, String selectedLine) {
            super(owner, "Update Vehicle");

            txtPlate = new TextField(10);
            txtType = new TextField(15);
            txtFuel = new TextField(10);
            txtYear = new TextField(4);
            txtMileage = new TextField(10);
            txtOwnerId = new TextField(5);

            addField("Plate No:", txtPlate);
            addField("Type (e.g., Sedan):", txtType);
            addField("Fuel (e.g., Petrol):", txtFuel);
            addField("Year:", txtYear);
            addField("Mileage:", txtMileage);
            addField("Owner User ID:", txtOwnerId);

            // Parse the selected line to pre-fill fields
            try {
                // "ID | Plate | Type | Fuel | Year | Mileage | OwnerID | Owner Name"
                String[] parts = selectedLine.split("\\s*\\|\\s*");
                this.vehicleId = Integer.parseInt(parts[0].trim());
                txtPlate.setText(parts[1].trim());
                txtType.setText(parts[2].trim());
                txtFuel.setText(parts[3].trim());
                txtYear.setText(parts[4].trim());
                txtMileage.setText(parts[5].trim());
                txtOwnerId.setText(parts[6].trim());
            } catch (Exception ex) {
                ex.printStackTrace();
                lblError.setText("Error parsing selected line: " + ex.getMessage());
                btnSave.setEnabled(false);
            }

            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Save button clicked
            try {
                String sql = "UPDATE vehicles SET plate_no=?, vehicle_type=?, fuel_type=?, year=?, mileage=?, owner_id=? WHERE vehicle_id=?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, txtPlate.getText());
                    ps.setString(2, txtType.getText());
                    ps.setString(3, txtFuel.getText());
                    ps.setInt(4, Integer.parseInt(txtYear.getText()));
                    ps.setDouble(5, Double.parseDouble(txtMileage.getText()));
                    ps.setInt(6, Integer.parseInt(txtOwnerId.getText()));
                    ps.setInt(7, this.vehicleId);

                    ps.executeUpdate();
                    parent.lblStatus.setText("✅ Vehicle " + this.vehicleId + " updated.");
                    parent.loadVehicles(); // Refresh the list
                    dispose();
                }
            } catch (NumberFormatException nfe) {
                lblError.setText("Year, Mileage, and Owner ID must be valid numbers.");
            } catch (SQLException ex) {
                if(ex.getMessage().contains("foreign key constraint")) {
                    lblError.setText("DB Error: Owner ID " + txtOwnerId.getText() + " does not exist.");
                } else {
                    lblError.setText("DB Error: " + ex.getMessage());
                }
            }
        }
    }


    // Main method for testing
    public static void main(String[] args) {
        // You MUST configure your DBConnection class first
        // Example (you will need your own details):
        /*
        try {
            // Set the connection details for your DBConnection class
            db.DBConnection.setURL("jdbc:mysql://localhost:3306/your_db_name", "your_username", "your_password");
        } catch (Exception e) {
            e.printStackTrace();
            // Show a simple AWT dialog if connection fails
            Frame errorFrame = new Frame("DB Connection Error");
            errorFrame.add(new TextArea("Could not set DB connection details:\n" + e.getMessage()));
            errorFrame.pack();
            errorFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) { System.exit(1); }
            });
            errorFrame.setVisible(true);
            return;
        }
        */

        // Once DB is configured, run the dashboard
        new AdminDashboard();
    }
}