package org.example.servlet;

import org.example.dao.BookingDAO;
import org.example.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * CancelBookingServlet: Handles POST /cancel-booking to cancel a booking.
 * Reads booking_id, calls BookingDAO.cancel, and redirects to admin.
 */
@WebServlet("/cancel-booking")
public class CancelBookingServlet extends HttpServlet {

    /**
     * Handles POST request to cancel a booking by booking_id.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Read and parse booking_id
            int bookingId = WebUtil.parseInt(req.getParameter("booking_id"));

            // Only proceed if booking_id is valid (> 0)
            if (bookingId > 0) {
                BookingDAO.cancel(bookingId);
            }

            // Redirect to admin dashboard
            resp.sendRedirect("admin");

        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().println("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println("An error occurred: " + e.getMessage());
        }
    }

    /**
     * GET is not supported on this endpoint.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not allowed on this endpoint.");
    }
}
