package org.example.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DBConnectionTest: A simple test class to verify the database connection works.
 * This class is temporary and should be deleted after Phase 1 verification.
 *
 * Run this with: mvn exec:java -Dexec.mainClass="org.example.util.DBConnectionTest"
 * (requires maven-exec-plugin in pom.xml)
 */
public class DBConnectionTest {

    /**
     * Main method to test the database connection and query the rooms table.
     * Attempts to get a connection and prints success/failure status,
     * then queries and displays all room numbers.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        Connection conn = null;

        try {
            // Attempt to get a connection from DBConnection utility
            conn = DBConnection.getConnection();

            if (conn != null) {
                System.out.println("✓ Database connection successful!");
                System.out.println("  Connected to: jdbc:mysql://localhost:3306/practice");
                System.out.println("  Driver: " + conn.getMetaData().getDriverName());
                System.out.println("  Database Product: " + conn.getMetaData().getDatabaseProductName());

                // Query the rooms table and print room numbers
                System.out.println("\n✓ Querying rooms table...");
                String query = "SELECT room_id, room_no FROM practice.rooms ORDER BY room_id";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                System.out.println("  Room numbers found:");
                while (rs.next()) {
                    int roomId = rs.getInt("room_id");
                    String roomNo = rs.getString("room_no");
                    System.out.println("    - Room ID: " + roomId + ", Room No: " + roomNo);
                }

                rs.close();
                stmt.close();

                // Close the connection
                conn.close();
                System.out.println("\n✓ Connection closed successfully");
            } else {
                System.out.println("✗ Connection returned null");
            }
        } catch (SQLException e) {
            System.out.println("✗ Database operation failed!");
            System.out.println("  Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure connection is closed
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }
}
