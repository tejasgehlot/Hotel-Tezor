package org.example.dao;

import org.example.model.Customer;
import org.example.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerDAO: Data Access Object for customer operations.
 * Handles all database queries related to customers.
 */
public class CustomerDAO {

    /**
     * Finds all customers in the database, ordered by customer_id.
     *
     * @return List of Customer objects, ordered by customer_id
     * @throws SQLException if a database error occurs
     */
    public static List<Customer> findAll() throws SQLException {
        Connection conn = DBConnection.getConnection();
        List<Customer> customers = new ArrayList<>();

        try {
            String query = "SELECT customer_id, customer_code, name, phone, email FROM practice.customers ORDER BY customer_id";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Customer customer = new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("customer_code"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getString("email")
                    );
                    customers.add(customer);
                }
                rs.close();
            }
        } finally {
            conn.close();
        }

        return customers;
    }
}
