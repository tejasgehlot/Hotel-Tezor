package org.example.dao;

import org.example.model.Room;
import java.time.LocalDate;
import java.util.List;

/**
 * RoomDAOTest: Test class to verify RoomDAO.findAvailable() works correctly.
 * Tests the overlap logic with known booking dates from the database.
 *
 * Expected test data:
 * - Room 123: booked 2026-11-01 to 11-03 and 11-10 to 11-12
 * - Room 223: booked 2026-11-05 to 11-07
 * - Rooms 224, 528, 529: unbooked
 *
 * Run with: mvn test -Dtest=RoomDAOTest
 */
public class RoomDAOTest {

    /**
     * Main method to run the tests.
     * Test 1: Search 2026-11-04 to 2026-11-06 with 2 guests
     *   Expected: 123 (free after 11-03), 528, 529
     *   (223 is booked 11-05 to 11-07, overlaps; 224 has capacity 1, can't fit 2 guests)
     *
     * Test 2: Search 2026-11-03 to 2026-11-05 with 2 guests
     *   Expected: 123 (same-day turnover, checkout 11-03 = checkin 11-03 allowed), 528, 529
     *   (223 starts at 11-05, not overlapping; 224 capacity issue)
     */
    public static void main(String[] args) {
        try {
            System.out.println("=== RoomDAO.findAvailable() Test ===\n");

            // Test 1: 2026-11-04 to 2026-11-06 with 2 guests
            System.out.println("Test 1: Search 2026-11-04 to 2026-11-06 with 2 guests");
            LocalDate checkIn1 = LocalDate.of(2026, 11, 4);
            LocalDate checkOut1 = LocalDate.of(2026, 11, 6);
            int guests1 = 2;

            List<Room> results1 = RoomDAO.findAvailable(checkIn1, checkOut1, guests1);
            System.out.println("  Results: " + results1.size() + " room(s) found");
            for (Room room : results1) {
                System.out.println("    - Room: " + room.getRoomNo() + " (ID: " + room.getRoomId() +
                        ", Type: " + room.getRoomType() + ", Capacity: " + room.getCapacity() + ")");
            }
            System.out.println("  Expected: 123, 528, 529");
            boolean test1Pass = results1.size() == 3 &&
                    results1.stream().anyMatch(r -> r.getRoomNo().equals("123")) &&
                    results1.stream().anyMatch(r -> r.getRoomNo().equals("528")) &&
                    results1.stream().anyMatch(r -> r.getRoomNo().equals("529"));
            System.out.println("  Status: " + (test1Pass ? "✓ PASS" : "✗ FAIL") + "\n");

            // Test 2: 2026-11-03 to 2026-11-05 with 2 guests (same-day turnover)
            System.out.println("Test 2: Search 2026-11-03 to 2026-11-05 with 2 guests (same-day turnover)");
            LocalDate checkIn2 = LocalDate.of(2026, 11, 3);
            LocalDate checkOut2 = LocalDate.of(2026, 11, 5);
            int guests2 = 2;

            List<Room> results2 = RoomDAO.findAvailable(checkIn2, checkOut2, guests2);
            System.out.println("  Results: " + results2.size() + " room(s) found");
            for (Room room : results2) {
                System.out.println("    - Room: " + room.getRoomNo() + " (ID: " + room.getRoomId() +
                        ", Type: " + room.getRoomType() + ", Capacity: " + room.getCapacity() + ")");
            }
            System.out.println("  Expected: 123, 223, 528, 529 (room 123 and 223 appear; same-day turnover works)");
            System.out.println("  Note: 223 is booked 11-05 to 11-07, not overlapping 11-03 to 11-05 (checkout=checkin allowed)");
            boolean test2Pass = results2.size() == 4 &&
                    results2.stream().anyMatch(r -> r.getRoomNo().equals("123")) &&
                    results2.stream().anyMatch(r -> r.getRoomNo().equals("223")) &&
                    results2.stream().anyMatch(r -> r.getRoomNo().equals("528")) &&
                    results2.stream().anyMatch(r -> r.getRoomNo().equals("529"));
            System.out.println("  Status: " + (test2Pass ? "✓ PASS" : "✗ FAIL") + "\n");

            // Summary
            System.out.println("=== Test Summary ===");
            System.out.println("Test 1 (overlap check): " + (test1Pass ? "✓ PASS" : "✗ FAIL"));
            System.out.println("Test 2 (same-day turnover): " + (test2Pass ? "✓ PASS" : "✗ FAIL"));
            System.out.println("Overall: " + (test1Pass && test2Pass ? "✓ ALL TESTS PASSED" : "✗ SOME TESTS FAILED"));

        } catch (Exception e) {
            System.out.println("✗ Test failed with exception:");
            e.printStackTrace();
        }
    }
}
