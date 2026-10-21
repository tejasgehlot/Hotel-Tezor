package org.example.dao;

import org.example.model.Room;
import org.example.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * RoomDAO: Data Access Object for room operations.
 * Handles all database queries related to rooms, including availability checks.
 */
public class RoomDAO {

    /**
     * Finds all rooms available for a given date range and guest count.
     * A room is available if:
     * 1. Its capacity >= requested guests
     * 2. It has no confirmed bookings that overlap the requested dates
     * Overlap rule: existing.check_in < new.check_out AND existing.check_out > new.check_in
     *
     * @param checkIn the check-in date
     * @param checkOut the check-out date
     * @param guests the number of guests
     * @return List of available Room objects
     * @throws SQLException if a database error occurs
     */
    public static List<Room> findAvailable(LocalDate checkIn, LocalDate checkOut, int guests) throws SQLException {
        List<Room> availableRooms = new ArrayList<>();
        Connection conn = DBConnection.getConnection();

        try {
            // Query: find all rooms with capacity >= guests, excluding those with overlapping confirmed bookings
            String query = "SELECT r.room_id, r.room_no, r.room_type, r.capacity, r.price_per_night " +
                    "FROM practice.rooms r " +
                    "WHERE r.capacity >= ? " +
                    "AND r.room_id NOT IN (" +
                    "  SELECT DISTINCT b.room_id FROM practice.bookings b " +
                    "  WHERE b.booking_status = 'CONFIRMED' " +
                    "  AND b.check_in < ? " +
                    "  AND b.check_out > ? " +
                    ") " +
                    "ORDER BY r.room_id";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, guests);
            stmt.setDate(2, java.sql.Date.valueOf(checkOut));
            stmt.setDate(3, java.sql.Date.valueOf(checkIn));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_no"),
                        rs.getString("room_type"),
                        rs.getInt("capacity"),
                        rs.getDouble("price_per_night")
                );
                availableRooms.add(room);
            }

            rs.close();
            stmt.close();
        } finally {
            conn.close();
        }

        return availableRooms;
    }

    /**
     * Finds all rooms in the database, ordered by room_id.
     *
     * @return List of all Room objects, ordered by room_id
     * @throws SQLException if a database error occurs
     */
    public static List<Room> findAll() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT room_id, room_no, room_type, capacity, price_per_night " +
                    "FROM practice.rooms " +
                    "ORDER BY room_id";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Room room = new Room(
                            rs.getInt("room_id"),
                            rs.getString("room_no"),
                            rs.getString("room_type"),
                            rs.getInt("capacity"),
                            rs.getDouble("price_per_night")
                    );
                    rooms.add(room);
                }
                rs.close();
            }
        } finally {
            conn.close();
        }

        return rooms;
    }

    /**
     * Finds a single room by room_id.
     * Used to load room details for display after selection.
     *
     * @param roomId the room ID to look up
     * @return Room object or null if not found
     * @throws SQLException if a database error occurs
     */
    public static Room findById(int roomId) throws SQLException {
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT room_id, room_no, room_type, capacity, price_per_night " +
                    "FROM practice.rooms " +
                    "WHERE room_id = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, roomId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_no"),
                        rs.getString("room_type"),
                        rs.getInt("capacity"),
                        rs.getDouble("price_per_night")
                );
                rs.close();
                stmt.close();
                return room;
            }

            rs.close();
            stmt.close();
        } finally {
            conn.close();
        }

        return null;
    }
}
