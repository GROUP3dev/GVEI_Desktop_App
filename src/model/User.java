package model;

/**
 * User
 * ----
 * Represents a system user (Citizen or Admin) in the GVEI application.
 */
public class User {

    // --- Fields ---
    private int userId;
    private String name;
    private String email;
    private String password;
    private String role;

    // --- Constructors ---

    /** Default constructor */
    public User() {}

    /**
     * Parameterized constructor
     * @param userId Unique user ID
     * @param name Full name of the user
     * @param email User's email address
     * @param password User's password (should be hashed in production)
     * @param role User role (e.g., "admin" or "citizen")
     */
    public User(int userId, String name, String email, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // --- Getters & Setters ---

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // --- Optional: toString for debugging ---
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
