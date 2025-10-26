package ui;

import db.DBConnection;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * GVEI - Login Form
 * ---------------------------------------------
 * Allows users to log in with email and password.
 * Redirects to AdminDashboard (for admins) or
 * VehicleRegistrationForm + ExchangeOfferForm (for citizens).
 */
public class LoginForm extends Frame implements ActionListener {

    // --- UI Components ---
    private TextField tfEmail, tfPassword;
    private Button btnLogin, btnRegister, btnClear;
    private Label lblMessage;

    // --- Constructor ---
    public LoginForm() {
        setTitle("GVEI - Login");
        setSize(420, 260);
        setLayout(new GridBagLayout());
        setResizable(false);
        setBackground(new Color(245, 245, 245));
        setFont(new Font("Arial", Font.PLAIN, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Title ---
        Label lblTitle = new Label("Login to GVEI System", Label.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // --- Email Field ---
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new Label("Email:"), gbc);

        tfEmail = new TextField(25);
        gbc.gridx = 1;
        add(tfEmail, gbc);

        // --- Password Field ---
        gbc.gridx = 0; gbc.gridy = 2;
        add(new Label("Password:"), gbc);

        tfPassword = new TextField(25);
        tfPassword.setEchoChar('*');
        gbc.gridx = 1;
        add(tfPassword, gbc);

        // --- Buttons ---
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnLogin = new Button("Login");
        btnRegister = new Button("Register");
        btnClear = new Button("Clear");

        btnLogin.setBackground(new Color(0, 153, 76));
        btnLogin.setForeground(Color.WHITE);
        btnRegister.setBackground(new Color(0, 102, 204));
        btnRegister.setForeground(Color.WHITE);
        btnClear.setBackground(new Color(255, 102, 0));
        btnClear.setForeground(Color.WHITE);

        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);
        btnClear.addActionListener(this);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // --- Message Label ---
        lblMessage = new Label(" ", Label.CENTER);
        lblMessage.setForeground(Color.RED);
        gbc.gridy = 4;
        add(lblMessage, gbc);

        // --- Window Close Event ---
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Button Actions ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLogin) {
            handleLogin();
        } else if (src == btnRegister) {
            new RegistrationForm();
            this.setVisible(false);
        } else if (src == btnClear) {
            tfEmail.setText("");
            tfPassword.setText("");
            lblMessage.setText(" ");
        }
    }

    // --- Login Logic ---
    private void handleLogin() {
        String email = tfEmail.getText().trim();
        String password = tfPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Please enter both email and password.");
            return;
        }

        String sql = "SELECT user_id, role FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String role = rs.getString("role");

                    lblMessage.setForeground(new Color(0, 153, 76));
                    lblMessage.setText("Login successful! Redirecting...");

                    EventQueue.invokeLater(() -> {
                        if ("admin".equalsIgnoreCase(role)) {
                            new AdminDashboard();
                        } else {
                            new VehicleRegistrationForm(userId);
                            new ExchangeOfferForm(userId);
                        }
                        dispose();
                    });
                } else {
                    lblMessage.setForeground(Color.RED);
                    lblMessage.setText("Invalid email or password.");
                }
            }
        } catch (SQLException ex) {
            lblMessage.setForeground(Color.RED);
            lblMessage.setText("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // --- Entry Point ---
    public static void main(String[] args) {
        new LoginForm();
    }
}
