package org.example.servlet;

import org.example.dao.RoomDAO;
import org.example.model.Room;
import org.example.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * SearchRoomsServlet: Handles the /search-rooms GET request.
 * Validates user input (check_in, check_out, guests), queries available rooms,
 * and forwards to rooms.jsp for display. If no rooms are available, displays a notice.
 */
@WebServlet("/search-rooms")
public class SearchRoomsServlet extends HttpServlet {

    /**
     * Handles GET requests for room search.
     * Parameters: check_in (YYYY-MM-DD), check_out (YYYY-MM-DD), guests (1-4)
     * Forwards to rooms.jsp with attributes: rooms, checkIn, checkOut, guests, nights, errorMessage
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String checkInStr = request.getParameter("check_in");
        String checkOutStr = request.getParameter("check_out");
        String guestsStr = request.getParameter("guests");

        // Parse inputs
        LocalDate checkIn = WebUtil.parseDate(checkInStr);
        LocalDate checkOut = WebUtil.parseDate(checkOutStr);
        int guests = WebUtil.parseInt(guestsStr);

        // Validate stay
        String error = WebUtil.validateStay(checkIn, checkOut, guests);
        if (error != null) {
            request.setAttribute("errorMessage", error);
            request.getRequestDispatcher("/WEB-INF/views/failure.jsp").forward(request, response);
            return;
        }

        try {
            // Query available rooms
            List<Room> availableRooms = RoomDAO.findAvailable(checkIn, checkOut, guests);

            // Calculate nights for display
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

            // Forward to rooms.jsp
            request.setAttribute("rooms", availableRooms);
            request.setAttribute("checkIn", checkInStr);
            request.setAttribute("checkOut", checkOutStr);
            request.setAttribute("guests", guests);
            request.setAttribute("nights", nights);

            request.getRequestDispatcher("/WEB-INF/views/rooms.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while searching for rooms.");
            request.getRequestDispatcher("/WEB-INF/views/failure.jsp").forward(request, response);
        }
    }
}
