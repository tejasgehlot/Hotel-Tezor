package org.example.servlet;

import org.example.dao.RoomDAO;
import org.example.dao.BookingDAO;
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

/**
 * BookRoomServlet: Handles the /book-room GET request.
 * Validates room selection, checks availability, loads room details,
 * and forwards to book.jsp for customer information entry.
 */
@WebServlet("/book-room")
public class BookRoomServlet extends HttpServlet {

    /**
     * Handles GET requests for room booking.
     * Parameters: room_id, check_in (YYYY-MM-DD), check_out (YYYY-MM-DD), guests (1-4)
     * Forwards to book.jsp with attributes: room, checkIn, checkOut, guests, nights
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roomIdStr = request.getParameter("room_id");
        String checkInStr = request.getParameter("check_in");
        String checkOutStr = request.getParameter("check_out");
        String guestsStr = request.getParameter("guests");

        // Parse inputs
        int roomId = WebUtil.parseInt(roomIdStr);
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
            // Load the room
            Room room = RoomDAO.findById(roomId);
            if (room == null) {
                request.setAttribute("errorMessage", "Room not found.");
                request.getRequestDispatcher("/WEB-INF/views/failure.jsp").forward(request, response);
                return;
            }

            // Check guests <= capacity
            if (guests > room.getCapacity()) {
                request.setAttribute("errorMessage", "Room capacity (" + room.getCapacity() + " guests) is less than requested guests (" + guests + ").");
                request.getRequestDispatcher("/WEB-INF/views/failure.jsp").forward(request, response);
                return;
            }

            // Check if room is free for the requested dates
            if (!BookingDAO.isRoomFree(roomId, checkIn, checkOut)) {
                request.setAttribute("errorMessage", "This room is no longer available for the selected dates. Please choose different dates or a different room.");
                request.getRequestDispatcher("/WEB-INF/views/failure.jsp").forward(request, response);
                return;
            }

            // Calculate nights for display
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

            // Forward to book.jsp
            request.setAttribute("room", room);
            request.setAttribute("checkIn", checkInStr);
            request.setAttribute("checkOut", checkOutStr);
            request.setAttribute("guests", guests);
            request.setAttribute("nights", nights);

            request.getRequestDispatcher("/WEB-INF/views/book.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while loading the room.");
            request.getRequestDispatcher("/WEB-INF/views/failure.jsp").forward(request, response);
        }
    }
}
