package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - simple JDBC connection helper.
 * Update URL, USER, PASS as necessary for your environment.
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gvei_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
