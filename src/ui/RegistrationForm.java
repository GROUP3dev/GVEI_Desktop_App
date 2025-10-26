package ui;

import db.DBConnection;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * GVEI - Registration Form
 * ---------------------------------------------
 * Allows users (citizen or admin) to register
 * by providing their name, email, password, and role.
 */
public class RegistrationForm extends Frame implements ActionListener {

    // --- UI Components ---
    private TextField tfName, tfEmail, tfPassword;
    private Choice chRole;
    private Button btnRegister, btnClear, btnBack;
    private Label lblMessage;

    // --- Constructor ---
    public RegistrationForm() {
        setTitle("GVEI - Registration");
        setSize(420, 340);
        setLayout(new GridBagLayout());
        setResizable(false);
        setBackground(new Color(245, 245, 245));
        setFont(new Font("Arial", Font.PLAIN, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Title ---
        Label lblTitle = new Label("Create Your GVEI Account", Label.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // --- Name ---
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new Label("Full Name:"), gbc);

        tfName = new TextField(25);
        gbc.gridx = 1;
        add(tfName, gbc);

        // --- Email ---
        gbc.gridx = 0; gbc.gridy = 2;
        add(new Label("Email:"), gbc);

        tfEmail = new TextField(25);
        gbc.gridx = 1;
        add(tfEmail, gbc);

        // --- Password ---
        gbc.gridx = 0; gbc.gridy = 3;
        add(new Label("Password:"), gbc);

        tfPassword = new TextField(25);
        tfPassword.setEchoChar('*');
        gbc.gridx = 1;
        add(tfPassword, gbc);

        // --- Role ---
        gbc.gridx = 0; gbc.gridy = 4;
        add(new Label("Role:"), gbc);

        chRole = new Choice();
        chRole.add("citizen");
        chRole.add("admin"); // for testing/admin creation
        gbc.gridx = 1;
        add(chRole, gbc);

        // --- Buttons ---
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnRegister = new Button("Register");
        btnClear = new Button("Clear");
        btnBack = new Button("Back to Login");

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

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // --- Message Label ---
        lblMessage = new Label(" ", Label.CENTER);
        lblMessage.setForeground(Color.RED);
        gbc.gridy = 6;
        add(lblMessage, gbc);

        // --- Window Close ---
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                new LoginForm();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Button Actions ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnRegister) {
            handleRegistration();
        } else if (src == btnClear) {
            clearFields();
        } else if (src == btnBack) {
            dispose();
            new LoginForm();
        }
    }

    // --- Clear Input Fields ---
    private void clearFields() {
        tfName.setText("");
        tfEmail.setText("");
        tfPassword.setText("");
        chRole.select(0);
        lblMessage.setText(" ");
    }

    // --- Registration Logic ---
    private void handleRegistration() {
        String name = tfName.getText().trim();
        String email = tfEmail.getText().trim();
        String password = tfPassword.getText().trim();
        String role = chRole.getSelectedItem();

        // --- Validation ---
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            lblMessage.setText("All fields are required.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            lblMessage.setText("Invalid email format.");
            return;
        }

        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password); // TODO: Hash password in production
            ps.setString(4, role);

            ps.executeUpdate();

            lblMessage.setForeground(new Color(0, 153, 76));
            lblMessage.setText("Registration successful! Please log in.");

            // Redirect to login after short delay
            new java.util.Timer().schedule(new java.util.TimerTask() {
                public void run() {
                    dispose();
                    new LoginForm();
                }
            }, 1500);

        } catch (SQLIntegrityConstraintViolationException ex) {
            lblMessage.setForeground(Color.RED);
            lblMessage.setText("Email already exists. Try another.");
        } catch (SQLException ex) {
            lblMessage.setForeground(Color.RED);
            lblMessage.setText("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // --- Entry Point for Testing ---
    public static void main(String[] args) {
        new RegistrationForm();
    }
}
