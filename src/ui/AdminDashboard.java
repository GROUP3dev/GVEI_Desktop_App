package ui;

import db.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

/**
 * Admin Dashboard - AWT Version (Full CRUD Refactor)
 * -----------------
 * - Uses StatsReportPanel for modular reporting.
 * - Adds search/filter functionality for data views.
 * - Adds export functionality (to .csv/.txt).
 */
public class AdminDashboard extends Frame implements ActionListener {

    // --- State & Components ---
    private TextArea taData;
    private Button btnLoadUsers, btnLoadVehicles, btnLoadPendingOffers, btnLoadAllOffers;
    private Button btnApprove, btnReject;
    private Button btnAdd, btnUpdate, btnDelete, btnExport;
    private Button btnShowChart, btnHideChart, btnRefreshStats;

    // Search/Filter Components
    private TextField txtSearch;
    private Button btnSearch;

    private StatsReportPanel statsPanel;
    private Label lblStatus;

    private String currentView = "";

    // --- MenuBar Components ---
    private MenuBar menuBar;
    private Menu menuDashboard, menuUsers, menuVehicles, menuOffers;
    private MenuItem itemShowStats, itemHideStats, itemRefreshStats, itemExit;
    private MenuItem itemLoadUsers, itemAddUser;
    private MenuItem itemLoadVehicles, itemAddVehicle;
    private MenuItem itemLoadPendingOffers, itemLoadAllOffers;
    private MenuItem itemExportData;

    public AdminDashboard() {
        setTitle("GVEI - Admin Dashboard");
        setSize(900, 700);
        setLayout(new BorderLayout());
        setResizable(false);

        // --- 1. Top: MenuBar ---
        setupMenuBar();
        setMenuBar(menuBar);

        // --- 2. North Container: Stats and Filter Panels ---
        Panel northContainer = new Panel(new BorderLayout());

        // 2.1 Stats Panel
        statsPanel = new StatsReportPanel();
        statsPanel.setPreferredSize(new Dimension(900, 180));
        statsPanel.setVisible(false);
        northContainer.add(statsPanel, BorderLayout.NORTH);

        // 2.2 Filter Panel
        Panel filterPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filterPanel.setBackground(new Color(240, 240, 240));

        txtSearch = new TextField(30);
        btnSearch = new Button("Apply Filter/Search");
        btnSearch.addActionListener(this);

        filterPanel.add(new Label("Filter Current View:"));
        filterPanel.add(txtSearch);
        filterPanel.add(btnSearch);

        northContainer.add(filterPanel, BorderLayout.CENTER);

        add(northContainer, BorderLayout.NORTH);


        // --- 3. Center: Data TextArea ---
        taData = new TextArea(20, 100);
        taData.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(taData, BorderLayout.CENTER);

        // --- 4. South: Button Panels & Status ---
        Panel southPanel = new Panel(new BorderLayout());

        Panel panelDataButtons = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        btnLoadUsers = new Button("View All Users");
        btnLoadUsers.addActionListener(this);
        panelDataButtons.add(btnLoadUsers);

        btnLoadVehicles = new Button("View All Vehicles");
        btnLoadVehicles.addActionListener(this);
        panelDataButtons.add(btnLoadVehicles);

        btnLoadPendingOffers = new Button("View Pending Offers");
        btnLoadPendingOffers.addActionListener(this);
        panelDataButtons.add(btnLoadPendingOffers);

        btnLoadAllOffers = new Button("View ALL Offers");
        btnLoadAllOffers.addActionListener(this);
        panelDataButtons.add(btnLoadAllOffers);

        panelDataButtons.add(new Label(" | "));

        btnApprove = new Button("Approve Selected Offer");
        btnApprove.addActionListener(this);
        panelDataButtons.add(btnApprove);

        btnReject = new Button("Reject Selected Offer");
        btnReject.addActionListener(this);
        panelDataButtons.add(btnReject);

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

        panelCrudButtons.add(new Label(" | "));

        // Export Button
        btnExport = new Button("Export Data (.csv)");
        btnExport.addActionListener(this);
        panelCrudButtons.add(btnExport);

        panelCrudButtons.add(new Label(" | "));

        btnShowChart = new Button("Show Report");
        btnShowChart.addActionListener(this);
        panelCrudButtons.add(btnShowChart);

        btnHideChart = new Button("Hide Report");
        btnHideChart.addActionListener(this);
        panelCrudButtons.add(btnHideChart);

        btnRefreshStats = new Button("Refresh Statistics");
        btnRefreshStats.addActionListener(this);
        panelCrudButtons.add(btnRefreshStats);

        lblStatus = new Label("Welcome to the Admin Dashboard. Please load some data.", Label.CENTER);
        lblStatus.setBackground(Color.LIGHT_GRAY);

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
                System.exit(0);
            }
        });

        // Initial state
        updateButtonStates();
        setVisible(true);
        // FIX APPLIED HERE: Pass null to load all pending offers unfiltered
        loadPendingOffers(null);
    }

    private void setupMenuBar() {
        menuBar = new MenuBar();

        // --- Dashboard Menu ---
        menuDashboard = new Menu("Dashboard");
        itemShowStats = new MenuItem("Show Report Panel");
        itemShowStats.addActionListener(this);
        menuDashboard.add(itemShowStats);

        itemHideStats = new MenuItem("Hide Report Panel");
        itemHideStats.addActionListener(this);
        menuDashboard.add(itemHideStats);

        itemRefreshStats = new MenuItem("Refresh Stats");
        itemRefreshStats.addActionListener(this);
        menuDashboard.add(itemRefreshStats);

        menuDashboard.addSeparator();

        itemExportData = new MenuItem("Export Current Data (.csv)");
        itemExportData.addActionListener(this);
        menuDashboard.add(itemExportData);

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

        itemLoadPendingOffers = new MenuItem("View Pending Offers");
        itemLoadPendingOffers.addActionListener(this);
        menuOffers.add(itemLoadPendingOffers);

        itemLoadAllOffers = new MenuItem("View ALL Offers (History)");
        itemLoadAllOffers.addActionListener(this);
        menuOffers.add(itemLoadAllOffers);

        menuBar.add(menuDashboard);
        menuBar.add(menuUsers);
        menuBar.add(menuVehicles);
        menuBar.add(menuOffers);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // Data Loading Actions (FIX: Pass null to load all unfiltered data)
        if (source == btnLoadUsers || source == itemLoadUsers) loadUsers(null);
        else if (source == btnLoadVehicles || source == itemLoadVehicles) loadVehicles(null);
        else if (source == btnLoadPendingOffers || source == itemLoadPendingOffers) loadPendingOffers(null);
        else if (source == btnLoadAllOffers || source == itemLoadAllOffers) loadAllOffers(null);

            // Search/Filter Action
        else if (source == btnSearch) applyFilter();

            // Offer Processing
        else if (source == btnApprove) processSelectedOffer("Approved");
        else if (source == btnReject) processSelectedOffer("Rejected");

            // Report Controls
        else if (source == btnShowChart || source == itemShowStats) {
            statsPanel.setVisible(true);
            statsPanel.refresh();
        }
        else if (source == btnHideChart || source == itemHideStats) statsPanel.setVisible(false);
        else if (source == btnRefreshStats || source == itemRefreshStats) {
            statsPanel.refresh();
            lblStatus.setText("Statistics refreshed.");
        }

        // Export Action
        else if (source == btnExport || source == itemExportData) exportCurrentData();

            // CRUD Actions
        else if (source == btnAdd) handleAdd();
        else if (source == btnUpdate) handleUpdate();
        else if (source == btnDelete) handleDelete();

            // Menu "Add" shortcuts (FIX: Pass null when loading the initial view)
        else if (source == itemAddUser) {
            loadUsers(null);
            handleAdd();
        } else if (source == itemAddVehicle) {
            loadVehicles(null);
            handleAdd();
        }
    }

    /**
     * Triggers the reload of the current view with the applied filter from txtSearch.
     */
    private void applyFilter() {
        String filter = txtSearch.getText().trim();
        if (filter.isEmpty()) {
            lblStatus.setText("Filter cleared. Reloading " + currentView + ".");
        } else {
            lblStatus.setText("Applying filter: '" + filter + "' to " + currentView + ".");
        }

        switch (currentView) {
            case "USERS":
                loadUsers(filter);
                break;
            case "VEHICLES":
                loadVehicles(filter);
                break;
            case "OFFERS":
                loadPendingOffers(filter);
                break;
            case "ALL_OFFERS":
                loadAllOffers(filter);
                break;
            default:
                lblStatus.setText("Cannot filter. No view is currently loaded.");
        }
    }

    // --- Export Method ---
    private void exportCurrentData() {
        String data = taData.getText();
        if (data.trim().isEmpty() || currentView.isEmpty()) {
            lblStatus.setText("Cannot export empty data or when no view is loaded.");
            return;
        }

        // Use FileDialog for saving (Standard AWT component)
        FileDialog fileDialog = new FileDialog(this, "Save Data as CSV/TXT", FileDialog.SAVE);
        fileDialog.setFile("export_" + currentView.toLowerCase() + ".csv");
        fileDialog.setVisible(true);

        String filename = fileDialog.getFile();
        String directory = fileDialog.getDirectory();

        if (filename == null || directory == null) {
            lblStatus.setText("Export canceled.");
            return;
        }

        File file = new File(directory, filename);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write the contents of the TextArea directly to the file
            writer.print(data);
            lblStatus.setText("✅ Data successfully exported to: " + file.getAbsolutePath());
        } catch (IOException ex) {
            lblStatus.setText("Error saving file: Check console for path errors.");
            System.err.println("File Save Error: " + ex.getMessage());
        }
    }

    private void updateButtonStates() {
        switch (currentView) {
            case "USERS":
            case "VEHICLES":
                btnApprove.setEnabled(false);
                btnReject.setEnabled(false);
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnExport.setEnabled(true);
                btnAdd.setLabel(currentView.equals("USERS") ? "Add User..." : "Add Vehicle...");
                break;
            case "OFFERS":
                btnApprove.setEnabled(true);
                btnReject.setEnabled(true);
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                btnExport.setEnabled(true);
                btnAdd.setLabel("Add New...");
                break;
            case "ALL_OFFERS":
                btnApprove.setEnabled(false);
                btnReject.setEnabled(false);
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                btnExport.setEnabled(true);
                btnAdd.setLabel("Add New...");
                break;
            default:
                btnApprove.setEnabled(false);
                btnReject.setEnabled(false);
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                btnExport.setEnabled(false);
                break;
        }
    }

    private String getSelectedId() {
        String selectedLine = taData.getSelectedText();

        if (selectedLine == null || selectedLine.isEmpty()) {
            try {
                int caretPos = taData.getCaretPosition();
                int lineStart = taData.getText().lastIndexOf('\n', caretPos - 1) + 1;
                int lineEnd = taData.getText().indexOf('\n', caretPos);
                if (lineEnd == -1) lineEnd = taData.getText().length();

                selectedLine = taData.getText().substring(lineStart, lineEnd);
            } catch (Exception ex) {
                lblStatus.setText("Could not determine selected line.");
                return null;
            }
        }

        if (selectedLine.trim().startsWith("ID") || selectedLine.trim().startsWith("---")) {
            lblStatus.setText("Please select a valid data row (not a header).");
            return null;
        }

        String[] parts = selectedLine.trim().split("\\s+");
        if (parts.length > 0 && !parts[0].isEmpty()) {
            return parts[0];
        } else {
            lblStatus.setText("Could not parse ID from selected line.");
            return null;
        }
    }

    // --- Data Loading Methods (Accepts filter parameter) ---

    private void loadUsers(String filter) {
        currentView = "USERS";
        updateButtonStates();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-20s | %-30s | %-10s%n", "ID", "Name", "Email", "Role"));
        sb.append("------+----------------------+--------------------------------+-----------\n");

        String sql = "SELECT user_id, name, email, role FROM users";
        String whereClause = "";
        String filterTerm = (filter != null && !filter.isEmpty()) ? filter : "";

        if (!filterTerm.isEmpty()) {
            whereClause = " WHERE name LIKE ? OR email LIKE ?";
            sql += whereClause;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!filterTerm.isEmpty()) {
                ps.setString(1, "%" + filterTerm + "%");
                ps.setString(2, "%" + filterTerm + "%");
            }

            int count = 0;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sb.append(String.format("%-5d | %-20s | %-30s | %-10s%n",
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role")));
                    count++;
                }
            }
            taData.setText(sb.toString());
            lblStatus.setText("Loaded " + count + " users." + (filterTerm.isEmpty() ? "" : " (Filtered)"));
        } catch (SQLException ex) {
            taData.setText("DB Error: " + ex.getMessage());
            lblStatus.setText("DB Error loading users.");
        }
    }

    private void loadVehicles(String filter) {
        currentView = "VEHICLES";
        updateButtonStates();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-10s | %-12s | %-10s | %-5s | %-10s | %-5s | %-15s%n",
                "ID", "Plate", "Type", "Fuel", "Year", "Mileage", "OwnerID", "Owner Name"));
        sb.append("------+------------+--------------+------------+-------+------------+---------+----------------\n");

        String sql = "SELECT v.vehicle_id, v.plate_no, v.vehicle_type, v.fuel_type, v.year, v.mileage, v.owner_id, u.name AS owner " +
                "FROM vehicles v LEFT JOIN users u ON v.owner_id = u.user_id";

        String whereClause = "";
        String filterTerm = (filter != null && !filter.isEmpty()) ? filter : "";
        int paramIndex = 1;

        if (!filterTerm.isEmpty()) {
            whereClause = " WHERE v.plate_no LIKE ? OR v.vehicle_type LIKE ? OR u.name LIKE ?";
            sql += whereClause;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!filterTerm.isEmpty()) {
                ps.setString(paramIndex++, "%" + filterTerm + "%");
                ps.setString(paramIndex++, "%" + filterTerm + "%");
                ps.setString(paramIndex++, "%" + filterTerm + "%");
            }

            int count = 0;
            try (ResultSet rs = ps.executeQuery()) {
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
            }
            taData.setText(sb.toString());
            lblStatus.setText("Loaded " + count + " vehicles." + (filterTerm.isEmpty() ? "" : " (Filtered)"));
        } catch (SQLException ex) {
            taData.setText("DB Error: " + ex.getMessage());
            lblStatus.setText("DB Error loading vehicles.");
        }
    }

    private void loadPendingOffers(String filter) {
        currentView = "OFFERS";
        updateButtonStates();
        String filterTerm = (filter != null && !filter.isEmpty()) ? filter : "";
        loadOffersData("eo.status = 'Pending'", filterTerm, "pending offers");
    }

    private void loadAllOffers(String filter) {
        currentView = "ALL_OFFERS";
        updateButtonStates();
        String filterTerm = (filter != null && !filter.isEmpty()) ? filter : "";
        loadOffersData("1=1", filterTerm, "all offers (including history)");
    }

    private void loadOffersData(String baseWhere, String filter, String statusText) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-9s | %-10s | %-10s | %-10s | %-12s | %-10s | %-15s%n",
                "ID", "VehicleID", "Value", "Subsidy%", "Status", "Plate", "Type", "Owner"));
        sb.append("------+-----------+------------+------------+------------+--------------+------------+----------------\n");

        String filterTerm = filter.isEmpty() ? "" : filter;

        String sql = """
            SELECT eo.offer_id, eo.vehicle_id, eo.exchange_value, eo.subsidy_percent, eo.status,
                   v.plate_no, v.vehicle_type, u.name
            FROM exchange_offers eo
            LEFT JOIN vehicles v ON eo.vehicle_id = v.vehicle_id
            LEFT JOIN users u ON v.owner_id = u.user_id
            WHERE 
        """ + baseWhere;

        if (!filterTerm.isEmpty()) {
            sql += " AND (v.plate_no LIKE ? OR v.vehicle_type LIKE ? OR u.name LIKE ?)";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (!filterTerm.isEmpty()) {
                ps.setString(paramIndex++, "%" + filterTerm + "%");
                ps.setString(paramIndex++, "%" + filterTerm + "%");
                ps.setString(paramIndex++, "%" + filterTerm + "%");
            }

            int count = 0;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sb.append(String.format("%-5d | %-9d | %-10.2f | %-10.2f | %-10s | %-12s | %-10s | %-15s%n",
                            rs.getInt("offer_id"),
                            rs.getInt("vehicle_id"),
                            rs.getDouble("exchange_value"),
                            rs.getDouble("subsidy_percent"),
                            rs.getString("status"),
                            rs.getString("plate_no") != null ? rs.getString("plate_no") : "N/A",
                            rs.getString("vehicle_type") != null ? rs.getString("vehicle_type") : "N/A",
                            rs.getString("name") != null ? rs.getString("name") : "N/A"));
                    count++;
                }
            }
            taData.setText(sb.toString());
            lblStatus.setText("Loaded " + count + " " + statusText + "." + (filterTerm.isEmpty() ? "" : " (Filtered)"));
        } catch (SQLException ex) {
            taData.setText("DB Error: " + ex.getMessage());
            lblStatus.setText("DB Error loading offers.");
        }
    }

    // --- Specific Action Methods (Approve/Reject) ---

    private void processSelectedOffer(String newStatus) {
        if (!currentView.equals("OFFERS")) {
            lblStatus.setText("Please load the **Pending Offers** view to approve or reject.");
            return;
        }

        String idStr = getSelectedId();
        if (idStr == null) return;

        try {
            int offerId = Integer.parseInt(idStr);
            String sql = "UPDATE exchange_offers SET status=? WHERE offer_id=? AND status='Pending'";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, offerId);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    lblStatus.setText("✅ Offer " + offerId + " set to " + newStatus + ".");
                    loadPendingOffers(null); // Reload view without filter
                    statsPanel.refresh(); // Refresh the report panel after a status change
                } else {
                    lblStatus.setText("Offer " + offerId + " not found, already processed, or not updated.");
                }
            }
        } catch (NumberFormatException ex) {
            lblStatus.setText("Error: Selected ID '" + idStr + "' is not a valid number.");
        } catch (SQLException ex) {
            lblStatus.setText("DB Error: " + ex.getMessage());
        }
    }

    // --- CRUD Handlers (Unmodified for brevity) ---

    private void handleAdd() {
        switch (currentView) {
            case "USERS":
                new AddUserDialog(this);
                break;
            case "VEHICLES":
                new AddVehicleDialog(this);
                break;
            default:
                lblStatus.setText("No 'Add' action available for this view.");
        }
    }

    private void handleUpdate() {
        String idStr = getSelectedId();
        if (idStr == null) return;

        String selectedLine = taData.getSelectedText();
        if (selectedLine == null || selectedLine.isEmpty()) {
            try {
                int caretPos = taData.getCaretPosition();
                int lineStart = taData.getText().lastIndexOf('\n', caretPos - 1) + 1;
                int lineEnd = taData.getText().indexOf('\n', caretPos);
                if (lineEnd == -1) lineEnd = taData.getText().length();
                selectedLine = taData.getText().substring(lineStart, lineEnd);
            } catch (Exception ex) {
                lblStatus.setText("Please highlight the full line to update.");
                return;
            }
        }

        if (selectedLine.trim().startsWith("ID") || selectedLine.trim().startsWith("---")) {
            lblStatus.setText("Please highlight a valid data row to update.");
            return;
        }


        switch (currentView) {
            case "USERS":
                new UpdateUserDialog(this, selectedLine.trim());
                break;
            case "VEHICLES":
                new UpdateVehicleDialog(this, selectedLine.trim());
                break;
            default:
                lblStatus.setText("No 'Update' action available for this view.");
        }
    }

    private void handleDelete() {
        String idStr = getSelectedId();
        if (idStr == null) return;

        ConfirmDialog confirmDialog = new ConfirmDialog(this,
                "Delete Record?",
                "Are you sure you want to delete " + currentView + " record ID: " + idStr + "?");

        if (!confirmDialog.isConfirmed()) {
            lblStatus.setText("Delete operation canceled.");
            return;
        }

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
                if (currentView.equals("USERS")) loadUsers(null); // FIX: Pass null
                if (currentView.equals("VEHICLES")) loadVehicles(null); // FIX: Pass null
            } else {
                lblStatus.setText("Record " + idStr + " not found or not deleted.");
            }
        } catch (NumberFormatException ex) {
            lblStatus.setText("Error: Selected ID '" + idStr + "' is not a valid number.");
        } catch (SQLException ex) {
            if (ex.getMessage().contains("foreign key constraint")) {
                lblStatus.setText("Error: Cannot delete record " + idStr + ". It is being used by other records.");
            } else {
                lblStatus.setText("DB Error: " + ex.getMessage());
            }
        }
    }

    // --- Helper Dialog Classes (Unmodified for brevity) ---

    class ConfirmDialog extends Dialog implements ActionListener {
        private boolean confirmed = false;

        ConfirmDialog(Frame owner, String title, String message) {
            super(owner, title, true);
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
            setVisible(true);
        }

        public boolean isConfirmed() { return confirmed; }

        @Override
        public void actionPerformed(ActionEvent e) { /* Handled by lambdas */ }
    }

    abstract class CrudDialog extends Dialog implements ActionListener {
        protected Panel gridPanel;
        protected Button btnSave, btnCancel;
        protected Label lblError;
        protected AdminDashboard parent;

        CrudDialog(Frame owner, String title) {
            super(owner, title, true);
            this.parent = (AdminDashboard) owner;
            setLayout(new BorderLayout(10, 10));

            gridPanel = new Panel(new GridLayout(0, 2, 5, 5));
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
            String name = txtName.getText();
            String email = txtEmail.getText();
            String role = txtRole.getText();

            if (name.isEmpty() || email.isEmpty() || role.isEmpty()) {
                lblError.setText("All fields are required.");
                return;
            }

            if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
                lblError.setText("Role must be 'admin' or 'user'.");
                return;
            }

            String sql = "INSERT INTO users (name, email, role, password) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, role);
                ps.setString(4, "temp_pass");

                int added = ps.executeUpdate();
                if(added > 0) {
                    parent.lblStatus.setText("✅ User '" + name + "' added.");
                    parent.loadUsers(null); // FIX: Pass null
                    dispose();
                }

            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    lblError.setText("DB Error: Email address already exists.");
                } else {
                    lblError.setText("DB Error: " + ex.getMessage());
                }
            }
        }
    }

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

            try {
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
            String name = txtName.getText();
            String email = txtEmail.getText();
            String role = txtRole.getText();

            if (name.isEmpty() || email.isEmpty() || role.isEmpty()) {
                lblError.setText("All fields are required.");
                return;
            }

            if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
                lblError.setText("Role must be 'admin' or 'user'.");
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
                parent.loadUsers(null); // FIX: Pass null
                dispose();

            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    lblError.setText("DB Error: Email address already exists.");
                } else {
                    lblError.setText("DB Error: ".concat(ex.getMessage()));
                }
            }
        }
    }

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
            addField("Type (e.g., Car):", txtType);
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
                    parent.loadVehicles(null); // FIX: Pass null
                    dispose();
                }
            } catch (NumberFormatException nfe) {
                lblError.setText("Year, Mileage, and Owner ID must be valid numbers.");
            } catch (SQLException ex) {
                if(ex.getMessage().contains("foreign key constraint")) {
                    lblError.setText("DB Error: Owner ID " + txtOwnerId.getText() + " does not exist.");
                } else if(ex.getMessage().contains("plate_no")) {
                    lblError.setText("DB Error: Plate number already exists.");
                } else {
                    lblError.setText("DB Error: " + ex.getMessage());
                }
            }
        }
    }

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
            addField("Type (e.g., Car):", txtType);
            addField("Fuel (e.g., Petrol):", txtFuel);
            addField("Year:", txtYear);
            addField("Mileage:", txtMileage);
            addField("Owner User ID:", txtOwnerId);

            try {
                String[] parts = selectedLine.trim().split("\\s*\\|\\s*");

                this.vehicleId = Integer.parseInt(parts[0].trim());
                txtPlate.setText(parts[1].trim());
                txtType.setText(parts[2].trim());
                txtFuel.setText(parts[3].trim());
                txtYear.setText(parts[4].trim());
                txtMileage.setText(parts[5].trim());
                txtOwnerId.setText(parts[6].trim());

            } catch (Exception ex) {
                lblError.setText("Error parsing selected line: " + ex.getMessage() + ". Try selecting the full line carefully.");
                btnSave.setEnabled(false);
            }

            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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
                    parent.loadVehicles(null); // FIX: Pass null
                    dispose();
                }
            } catch (NumberFormatException nfe) {
                lblError.setText("Year, Mileage, and Owner ID must be valid numbers.");
            } catch (SQLException ex) {
                if(ex.getMessage().contains("foreign key constraint")) {
                    lblError.setText("DB Error: Owner ID " + txtOwnerId.getText() + " does not exist.");
                } else if(ex.getMessage().contains("plate_no")) {
                    lblError.setText("DB Error: Plate number already exists.");
                } else {
                    lblError.setText("DB Error: " + ex.getMessage());
                }
            }
        }
    }


    // Main method for testing
    public static void main(String[] args) {
        new AdminDashboard();
    }
}