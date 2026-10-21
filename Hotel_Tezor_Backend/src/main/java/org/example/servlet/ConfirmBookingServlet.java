package org.example.servlet;

import org.example.dao.BookingDAO;
import org.example.dao.BookingException;
import org.example.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/**
 * ConfirmBookingServlet: Handles POST /confirm-booking to create a new booking with customer details.
 * Validates input, calls BookingDAO.createBooking, and redirects to success or failure page.
 */
@WebServlet("/confirm-booking")
public class ConfirmBookingServlet extends HttpServlet {

    /**
     * Handles POST request to confirm and create a booking.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            // Read and trim form parameters
            int roomId = Integer.parseInt(req.getParameter("room_id") != null ? req.getParameter("room_id").trim() : "0");
            LocalDate checkIn = LocalDate.parse(req.getParameter("check_in") != null ? req.getParameter("check_in").trim() : "");
            LocalDate checkOut = LocalDate.parse(req.getParameter("check_out") != null ? req.getParameter("check_out").trim() : "");
            int guests = Integer.parseInt(req.getParameter("guests") != null ? req.getParameter("guests").trim() : "0");
            String name = req.getParameter("name") != null ? req.getParameter("name").trim() : "";
            String phone = req.getParameter("phone") != null ? req.getParameter("phone").trim() : "";
            String email = req.getParameter("email") != null ? req.getParameter("email").trim() : "";

            // Convert empty email to null
            if (email.isEmpty()) {
                email = null;
            }

            // Validate stay (dates and guest count)
            String stayError = WebUtil.validateStay(checkIn, checkOut, guests);
            if (stayError != null) {
                showErrorPage(req, resp, stayError);
                return;
            }

            // Validate name
            String nameError = WebUtil.validateName(name);
            if (nameError != null) {
                showErrorPage(req, resp, nameError);
                return;
            }

            // Validate phone
            String phoneError = WebUtil.validatePhone(phone);
            if (phoneError != null) {
                showErrorPage(req, resp, phoneError);
                return;
            }

            // Validate email length if present
            if (email != null && email.length() > 100) {
                showErrorPage(req, resp, "Email must be at most 100 characters.");
                return;
            }

            // Create booking
            String bookingCode = BookingDAO.createBooking(roomId, checkIn, checkOut, guests, name, phone, email);

            // Redirect to success with booking code
            resp.sendRedirect("success?code=" + bookingCode);

        } catch (BookingException e) {
            // Booking-related error: show on failure.jsp
            showErrorPage(req, resp, e.getMessage());
        } catch (Exception e) {
            // Other errors (database, parsing, etc.)
            e.printStackTrace();
            showErrorPage(req, resp, "An error occurred while processing your booking. Please try again.");
        }
    }

    /**
     * Helper method to show error page with error message attribute.
     */
    private void showErrorPage(HttpServletRequest req, HttpServletResponse resp, String errorMessage) throws ServletException, IOException {
        req.setAttribute("errorMessage", errorMessage);
        req.getRequestDispatcher("WEB-INF/views/failure.jsp").forward(req, resp);
    }

    /**
     * GET is not supported on this endpoint.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not allowed on this endpoint.");
    }
}

