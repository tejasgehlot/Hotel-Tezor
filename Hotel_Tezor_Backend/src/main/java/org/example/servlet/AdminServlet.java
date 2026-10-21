package org.example.servlet;

import org.example.dao.BookingDAO;
import org.example.dao.CustomerDAO;
import org.example.dao.RoomDAO;
import org.example.model.BookingView;
import org.example.model.Customer;
import org.example.model.Room;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * AdminServlet: Handles GET /admin to display the admin dashboard.
 * Loads bookings, rooms and customers and forwards to admin.jsp.
 */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    /**
     * Handles GET request to display admin dashboard with all bookings, rooms and customers.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Load all bookings, rooms and customers
            List<BookingView> bookings = BookingDAO.findAll();
            List<Room> rooms = RoomDAO.findAll();
            List<Customer> customers = CustomerDAO.findAll();

            // Set request attributes
            req.setAttribute("bookings", bookings);
            req.setAttribute("rooms", rooms);
            req.setAttribute("customers", customers);

            // Forward to admin.jsp
            req.getRequestDispatcher("WEB-INF/views/admin.jsp").forward(req, resp);

        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().println("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println("An error occurred: " + e.getMessage());
        }
    }

    /**
     * POST is not supported on this endpoint.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method is not allowed on this endpoint.");
    }
}
