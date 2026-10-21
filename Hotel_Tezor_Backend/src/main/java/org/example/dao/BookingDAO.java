package org.example.dao;

import org.example.model.BookingView;
import org.example.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingDAO: Data Access Object for booking operations.
 * Handles all database queries related to bookings.
 */
public class BookingDAO {

    /**
     * Checks if a room is free (available) for a given date range.
     * Returns true if there are NO confirmed bookings that overlap the requested dates.
     * Overlap rule: existing.check_in < new.check_out AND existing.check_out > new.check_in
     * Strict < and > means same-day turnover is allowed (checkout day = checkin day).
     *
     * @param roomId the room ID to check
     * @param checkIn the check-in date
     * @param checkOut the check-out date
     * @return true if room is free, false if there's a conflict
     * @throws SQLException if a database error occurs
     */
    public static boolean isRoomFree(int roomId, LocalDate checkIn, LocalDate checkOut) throws SQLException {
        Connection conn = DBConnection.getConnection();

        try {
            // Query: count overlapping confirmed bookings for this room
            String query = "SELECT COUNT(*) as conflict_count FROM practice.bookings " +
                    "WHERE room_id = ? " +
                    "AND booking_status = 'CONFIRMED' " +
                    "AND check_in < ? " +
                    "AND check_out > ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, roomId);
            stmt.setDate(2, java.sql.Date.valueOf(checkOut));
            stmt.setDate(3, java.sql.Date.valueOf(checkIn));

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int conflictCount = rs.getInt("conflict_count");

            rs.close();
            stmt.close();

            // If there are no conflicts, the room is free
            return conflictCount == 0;

        } finally {
            conn.close();
        }
    }

    /**
     * Creates a new booking in a single transaction with overlap re-check and customer find-or-create.
     * Generates unique booking_code (TZBK + 6 random digits) and customer_code if needed (TZID + 6 random digits).
     * Calculates amount server-side using BigDecimal and ChronoUnit.DAYS.between.
     *
     * @param roomId the room ID
     * @param checkIn the check-in date
     * @param checkOut the check-out date
     * @param guests number of guests
     * @param name customer name
     * @param phone customer phone
     * @param email customer email (null or empty string)
     * @return the generated booking code
     * @throws SQLException if a database error occurs
     * @throws BookingException if validation fails or room is unavailable
     */
    public static String createBooking(int roomId, LocalDate checkIn, LocalDate checkOut, int guests,
                                       String name, String phone, String email) throws SQLException, BookingException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            // Step 1: Load room with FOR UPDATE lock
            String roomQuery = "SELECT capacity, price_per_night FROM practice.rooms WHERE room_id = ? FOR UPDATE";
            try (PreparedStatement stmt = conn.prepareStatement(roomQuery)) {
                stmt.setInt(1, roomId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new BookingException("This room does not exist.");
                }
                int capacity = rs.getInt("capacity");
                BigDecimal pricePerNight = rs.getBigDecimal("price_per_night");

                // Step 2: Validate guest count
                if (guests < 1 || guests > capacity) {
                    throw new BookingException("Guests must be between 1 and " + capacity + " for this room.");
                }

                // Step 3: Re-check overlap on same connection
                String overlapQuery = "SELECT COUNT(*) as conflict_count FROM practice.bookings " +
                        "WHERE room_id = ? " +
                        "AND booking_status = 'CONFIRMED' " +
                        "AND check_in < ? " +
                        "AND check_out > ?";
                try (PreparedStatement overlapStmt = conn.prepareStatement(overlapQuery)) {
                    overlapStmt.setInt(1, roomId);
                    overlapStmt.setDate(2, java.sql.Date.valueOf(checkOut));
                    overlapStmt.setDate(3, java.sql.Date.valueOf(checkIn));
                    ResultSet overlapRs = overlapStmt.executeQuery();
                    overlapRs.next();
                    int conflictCount = overlapRs.getInt("conflict_count");
                    if (conflictCount > 0) {
                        throw new BookingException("Sorry, this room is no longer available for your dates.");
                    }
                }

                // Step 4: Find or create customer by phone
                int customerId;
                String findCustomerQuery = "SELECT customer_id FROM practice.customers WHERE phone = ?";
                try (PreparedStatement findStmt = conn.prepareStatement(findCustomerQuery)) {
                    findStmt.setString(1, phone);
                    ResultSet findRs = findStmt.executeQuery();
                    if (findRs.next()) {
                        customerId = findRs.getInt("customer_id");
                    } else {
                        // Generate unique customer code
                        String customerCode = generateUniqueCustomerCode(conn);
                        String insertCustomerQuery = "INSERT INTO practice.customers (customer_code, name, phone, email) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertCustomerQuery, Statement.RETURN_GENERATED_KEYS)) {
                            insertStmt.setString(1, customerCode);
                            insertStmt.setString(2, name);
                            insertStmt.setString(3, phone);
                            insertStmt.setString(4, email != null && !email.isEmpty() ? email : null);
                            insertStmt.executeUpdate();
                            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                customerId = generatedKeys.getInt(1);
                            } else {
                                throw new BookingException("Failed to create customer record.");
                            }
                        }
                    }
                }

                // Step 5: Calculate amount
                long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
                BigDecimal amount = pricePerNight.multiply(new BigDecimal(nights));

                // Step 6: Generate unique booking code and insert booking
                String bookingCode = generateUniqueBookingCode(conn);
                String insertBookingQuery = "INSERT INTO practice.bookings (booking_code, room_id, customer_id, check_in, check_out, guests, amount, booking_status, payment_status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, 'CONFIRMED', 'PENDING')";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertBookingQuery)) {
                    insertStmt.setString(1, bookingCode);
                    insertStmt.setInt(2, roomId);
                    insertStmt.setInt(3, customerId);
                    insertStmt.setDate(4, java.sql.Date.valueOf(checkIn));
                    insertStmt.setDate(5, java.sql.Date.valueOf(checkOut));
                    insertStmt.setInt(6, guests);
                    insertStmt.setBigDecimal(7, amount);
                    insertStmt.executeUpdate();
                }

                // Step 7: Commit transaction
                conn.commit();
                return bookingCode;
            }

        } catch (BookingException | SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Generates a unique customer code (TZID + 6 random digits).
     * Checks against existing codes to ensure uniqueness.
     */
    private static String generateUniqueCustomerCode(Connection conn) throws SQLException {
        while (true) {
            String code = "TZID" + String.format("%06d", (int)(Math.random() * 1000000));
            String checkQuery = "SELECT COUNT(*) as cnt FROM practice.customers WHERE customer_code = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
                stmt.setString(1, code);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                if (rs.getInt("cnt") == 0) {
                    return code;
                }
            }
        }
    }

    /**
     * Generates a unique booking code (TZBK + 6 random digits).
     * Checks against existing codes to ensure uniqueness.
     */
    private static String generateUniqueBookingCode(Connection conn) throws SQLException {
        while (true) {
            String code = "TZBK" + String.format("%06d", (int)(Math.random() * 1000000));
            String checkQuery = "SELECT COUNT(*) as cnt FROM practice.bookings WHERE booking_code = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
                stmt.setString(1, code);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                if (rs.getInt("cnt") == 0) {
                    return code;
                }
            }
        }
    }

    /**
     * Finds a booking by booking code, joining with customer and room details.
     * Returns a BookingView (booking + customer + room) or null if not found.
     *
     * @param bookingCode the booking code to look up
     * @return BookingView with all booking details, or null if not found
     * @throws SQLException if a database error occurs
     */
    public static BookingView findByCode(String bookingCode) throws SQLException {
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT b.booking_id, b.booking_code, b.room_id, b.customer_id, b.check_in, b.check_out, " +
                    "b.guests, b.amount, b.booking_status, b.payment_status, " +
                    "c.customer_id, c.customer_code, c.name, c.phone, c.email, " +
                    "r.room_id, r.room_no, r.room_type, r.capacity, r.price_per_night " +
                    "FROM practice.bookings b " +
                    "JOIN practice.customers c ON b.customer_id = c.customer_id " +
                    "JOIN practice.rooms r ON b.room_id = r.room_id " +
                    "WHERE b.booking_code = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, bookingCode);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    return null;
                }

                BookingView view = new BookingView();
                // Booking fields
                view.setBookingId(rs.getInt("b.booking_id"));
                view.setBookingCode(rs.getString("b.booking_code"));
                view.setRoomId(rs.getInt("b.room_id"));
                view.setCustomerId(rs.getInt("b.customer_id"));
                view.setCheckIn(rs.getDate("b.check_in").toLocalDate());
                view.setCheckOut(rs.getDate("b.check_out").toLocalDate());
                view.setGuests(rs.getInt("b.guests"));
                view.setAmount(rs.getBigDecimal("b.amount"));
                view.setBookingStatus(rs.getString("b.booking_status"));
                view.setPaymentStatus(rs.getString("b.payment_status"));

                // Customer fields
                view.setCustomerCode(rs.getString("c.customer_code"));
                view.setName(rs.getString("c.name"));
                view.setPhone(rs.getString("c.phone"));
                view.setEmail(rs.getString("c.email"));

                // Room fields
                view.setRoomNo(String.valueOf(rs.getInt("r.room_no")));
                view.setRoomType(rs.getString("r.room_type"));
                view.setCapacity(rs.getInt("r.capacity"));
                view.setPricePerNight(rs.getBigDecimal("r.price_per_night"));

                return view;
            }

        } finally {
            conn.close();
        }
    }

    /**
     * Finds all bookings in the database, newest first, joined with customer and room details.
     * Returns a list of BookingView objects ordered by booking_id DESC.
     *
     * @return List of BookingView objects, newest first
     * @throws SQLException if a database error occurs
     */
    public static List<BookingView> findAll() throws SQLException {
        Connection conn = DBConnection.getConnection();
        List<BookingView> bookings = new ArrayList<>();

        try {
            String query = "SELECT b.booking_id, b.booking_code, b.room_id, b.customer_id, b.check_in, b.check_out, " +
                    "b.guests, b.amount, b.booking_status, b.payment_status, " +
                    "c.customer_id, c.customer_code, c.name, c.phone, c.email, " +
                    "r.room_id, r.room_no, r.room_type, r.capacity, r.price_per_night " +
                    "FROM practice.bookings b " +
                    "JOIN practice.customers c ON b.customer_id = c.customer_id " +
                    "JOIN practice.rooms r ON b.room_id = r.room_id " +
                    "ORDER BY b.booking_id DESC";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    BookingView view = new BookingView();
                    // Booking fields
                    view.setBookingId(rs.getInt("b.booking_id"));
                    view.setBookingCode(rs.getString("b.booking_code"));
                    view.setRoomId(rs.getInt("b.room_id"));
                    view.setCustomerId(rs.getInt("b.customer_id"));
                    view.setCheckIn(rs.getDate("b.check_in").toLocalDate());
                    view.setCheckOut(rs.getDate("b.check_out").toLocalDate());
                    view.setGuests(rs.getInt("b.guests"));
                    view.setAmount(rs.getBigDecimal("b.amount"));
                    view.setBookingStatus(rs.getString("b.booking_status"));
                    view.setPaymentStatus(rs.getString("b.payment_status"));

                    // Customer fields
                    view.setCustomerCode(rs.getString("c.customer_code"));
                    view.setName(rs.getString("c.name"));
                    view.setPhone(rs.getString("c.phone"));
                    view.setEmail(rs.getString("c.email"));

                    // Room fields
                    view.setRoomNo(String.valueOf(rs.getInt("r.room_no")));
                    view.setRoomType(rs.getString("r.room_type"));
                    view.setCapacity(rs.getInt("r.capacity"));
                    view.setPricePerNight(rs.getBigDecimal("r.price_per_night"));

                    bookings.add(view);
                }
                rs.close();
            }

        } finally {
            conn.close();
        }

        return bookings;
    }

    /**
     * Cancels a booking by setting its booking_status to 'CANCELLED'.
     * Only cancels if the booking is currently 'CONFIRMED'.
     * This is a soft cancel (no deletion).
     *
     * @param bookingId the booking ID to cancel
     * @return true if a row was updated (booking was cancelled), false otherwise
     * @throws SQLException if a database error occurs
     */
    public static boolean cancel(int bookingId) throws SQLException {
        Connection conn = DBConnection.getConnection();

        try {
            String query = "UPDATE practice.bookings SET booking_status = 'CANCELLED' " +
                    "WHERE booking_id = ? AND booking_status = 'CONFIRMED'";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, bookingId);
                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }

        } finally {
            conn.close();
        }
    }
}
