package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection: Utility class to manage database connections.
 * Provides a single getConnection() method that returns a MySQL connection
 * using environment variables for secure credential management.
 */
public class DBConnection {

    // Database connection details from environment variables
    private static final String DB_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    private static final String DB_PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
    private static final String DB_NAME = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "practice";
    private static final String DB_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "";
    private static final String DB_TIMEZONE = System.getenv("DB_TIMEZONE") != null ? System.getenv("DB_TIMEZONE") : "Asia/Kolkata";
    private static final String DB_URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=%s",
            DB_HOST, DB_PORT, DB_NAME, DB_TIMEZONE
    );
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Establishes and returns a connection to the MySQL database.
     * The connection is created fresh each time this method is called.
     *
     * @return a Connection object to the practice database
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            // If the driver is not found, throw an error
            throw new SQLException("MySQL JDBC Driver not found", e);
        }

        // Return a new connection to the database
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
