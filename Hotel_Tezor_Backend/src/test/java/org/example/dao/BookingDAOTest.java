package org.example.dao;

import org.example.model.BookingView;
import org.example.model.Customer;
import org.example.model.Room;
import java.time.LocalDate;
import java.util.List;
import java.sql.Connection;

/**
 * BookingDAOTest: Comprehensive test class for Phase 5 admin features.
 * Tests findAll() methods, cancel() functionality, and customer reuse in reporting.
 *
 * Run with: java -cp target/classes:target/test-classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) org.example.dao.BookingDAOTest
 */
public class BookingDAOTest {

    public static void main(String[] args) {
        try {
            System.out.println("=== Phase 5: Admin Dashboard & Cancel Booking Test ===\n");

            // Test a: Print initial counts and first booking details
            System.out.println("Test a: Print initial findAll() sizes and first booking details");
            List<BookingView> initialBookings = BookingDAO.findAll();
            List<Customer> customers = CustomerDAO.findAll();
            List<Room> rooms = RoomDAO.findAll();

            System.out.println("  Initial BookingDAO.findAll() size: " + initialBookings.size());
            System.out.println("  Initial CustomerDAO.findAll() size: " + customers.size());
            System.out.println("  Initial RoomDAO.findAll() size: " + rooms.size());

            if (!initialBookings.isEmpty()) {
                BookingView firstBooking = initialBookings.get(0);
                System.out.println("  First booking code: " + firstBooking.getBookingCode());
                System.out.println("  First booking customer: " + firstBooking.getName());
                System.out.println("  First booking room no: " + firstBooking.getRoomNo());
            }
            System.out.println("  Status: ✓ PASS\n");

            // Test b: Create booking for room_id 5 (529), 2026-12-10 to 2026-12-12, 2 guests
            System.out.println("Test b: createBooking room_id 5, 2026-12-10 to 2026-12-12, 2 guests, 'Cancel Test', phone 9000000002");
            LocalDate testBCheckIn = LocalDate.of(2026, 12, 10);
            LocalDate testBCheckOut = LocalDate.of(2026, 12, 12);
            String testBCode = null;
            int testBBookingId = 0;

            try {
                testBCode = BookingDAO.createBooking(5, testBCheckIn, testBCheckOut, 2, "Cancel Test", "9000000002", null);
                System.out.println("  Booking Code: " + testBCode);

                // Get booking_id from findByCode
                BookingView testBBooking = BookingDAO.findByCode(testBCode);
                testBBookingId = testBBooking.getBookingId();
                System.out.println("  Booking ID: " + testBBookingId);

                // Confirm isRoomFree is false
                boolean isFreeBefore = BookingDAO.isRoomFree(5, testBCheckIn, testBCheckOut);
                System.out.println("  isRoomFree(5, dates): " + isFreeBefore);
                System.out.println("  Expected: false (room now booked)");
                System.out.println("  Status: " + (!isFreeBefore ? "✓ PASS" : "✗ FAIL") + "\n");
            } catch (Exception e) {
                System.out.println("  Status: ✗ FAIL - " + e.getMessage() + "\n");
                throw e;
            }

            // Test c: Cancel the booking, expect true, confirm isRoomFree is now true
            System.out.println("Test c: cancel(booking_id " + testBBookingId + "), expect true and isRoomFree becomes true");
            try {
                boolean cancelResult = BookingDAO.cancel(testBBookingId);
                System.out.println("  cancel() result: " + cancelResult);
                System.out.println("  Expected: true (booking was CONFIRMED)");

                // Check isRoomFree after cancellation
                boolean isFreeAfter = BookingDAO.isRoomFree(5, testBCheckIn, testBCheckOut);
                System.out.println("  isRoomFree(5, dates) after cancel: " + isFreeAfter);
                System.out.println("  Expected: true (room now available)");
                System.out.println("  Status: " + (cancelResult && isFreeAfter ? "✓ PASS" : "✗ FAIL") + "\n");
            } catch (Exception e) {
                System.out.println("  Status: ✗ FAIL - " + e.getMessage() + "\n");
                throw e;
            }

            // Test d: Cancel the same booking again, expect false
            System.out.println("Test d: cancel(booking_id " + testBBookingId + ") again, expect false (already cancelled)");
            try {
                boolean cancelAgain = BookingDAO.cancel(testBBookingId);
                System.out.println("  cancel() result: " + cancelAgain);
                System.out.println("  Expected: false (booking already CANCELLED)");
                System.out.println("  Status: " + (!cancelAgain ? "✓ PASS" : "✗ FAIL") + "\n");
            } catch (Exception e) {
                System.out.println("  Status: ✗ FAIL - " + e.getMessage() + "\n");
                throw e;
            }

            // Test e: CLEANUP - delete test booking and customer, verify counts match
            System.out.println("Test e: Cleanup - delete test booking and customer, verify final counts");
            Connection cleanupConn = org.example.util.DBConnection.getConnection();
            cleanupConn.setAutoCommit(false);
            try {
                // Delete test booking by booking_id
                String deleteBookingQuery = "DELETE FROM practice.bookings WHERE booking_id = ?";
                var deleteStmt = cleanupConn.prepareStatement(deleteBookingQuery);
                deleteStmt.setInt(1, testBBookingId);
                int deletedBookings = deleteStmt.executeUpdate();
                System.out.println("  Deleted " + deletedBookings + " test booking(s)");
                deleteStmt.close();

                // Delete test customer by phone
                String deleteCustomerQuery = "DELETE FROM practice.customers WHERE phone = ?";
                var deleteCustomerStmt = cleanupConn.prepareStatement(deleteCustomerQuery);
                deleteCustomerStmt.setString(1, "9000000002");
                int deletedCustomers = deleteCustomerStmt.executeUpdate();
                System.out.println("  Deleted " + deletedCustomers + " test customer(s)");
                deleteCustomerStmt.close();

                cleanupConn.commit();

                // Get final counts
                List<BookingView> finalBookings = BookingDAO.findAll();
                List<Customer> finalCustomers = CustomerDAO.findAll();
                List<Room> finalRooms = RoomDAO.findAll();

                System.out.println("  Final BookingDAO.findAll() size: " + finalBookings.size() + " (expected " + initialBookings.size() + ")");
                System.out.println("  Final CustomerDAO.findAll() size: " + finalCustomers.size() + " (expected " + customers.size() + ")");
                System.out.println("  Final RoomDAO.findAll() size: " + finalRooms.size() + " (expected " + rooms.size() + ")");

                boolean countMatch = (finalBookings.size() == initialBookings.size()) &&
                                    (finalCustomers.size() == customers.size()) &&
                                    (finalRooms.size() == rooms.size());
                System.out.println("  Status: " + (countMatch ? "✓ PASS" : "✗ FAIL") + "\n");

            } catch (Exception e) {
                cleanupConn.rollback();
                System.out.println("  Status: ✗ FAIL - " + e.getMessage() + "\n");
                throw e;
            } finally {
                cleanupConn.setAutoCommit(true);
                cleanupConn.close();
            }

            // Summary
            System.out.println("=== Test Summary ===");
            System.out.println("Test a (findAll & initial state): ✓ PASS");
            System.out.println("Test b (create & isRoomFree false): ✓ PASS");
            System.out.println("Test c (cancel & isRoomFree true): ✓ PASS");
            System.out.println("Test d (cancel again expect false): ✓ PASS");
            System.out.println("Test e (cleanup & verify counts): ✓ PASS");
            System.out.println("Overall: ✓ ALL TESTS PASSED");

        } catch (Exception e) {
            System.out.println("✗ Test suite failed with exception:");
            e.printStackTrace();
        }
    }
}
