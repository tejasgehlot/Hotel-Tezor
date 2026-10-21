package org.example.servlet;

import org.example.dao.BookingDAO;
import org.example.model.BookingView;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * SuccessServlet: Handles GET /success?code=BOOKING_CODE to display booking confirmation.
 * Loads the booking details and forwards to success.jsp.
 */
@WebServlet("/success")
public class SuccessServlet extends HttpServlet {

    /**
     * Handles GET request to display booking confirmation with booking code.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String bookingCode = req.getParameter("code");

            // Redirect to index if code is missing or empty
            if (bookingCode == null || bookingCode.trim().isEmpty()) {
                resp.sendRedirect("index.html");
                return;
            }

            // Load booking by code
            BookingView booking = BookingDAO.findByCode(bookingCode);

            // Redirect to index if booking not found
            if (booking == null) {
                resp.sendRedirect("index.html");
                return;
            }

            // Set booking attribute and forward to success.jsp
            req.setAttribute("booking", booking);
            req.getRequestDispatcher("WEB-INF/views/success.jsp").forward(req, resp);

        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred.");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred.");
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
