package Dao;

import entity.Booking;
import java.time.LocalDateTime;

/** Supplies sample walk-in guests for the standard waiting queue at startup. */
public class WaitingQueueDAO {

    private final LocalDateTime projectStartTime = LocalDateTime.now();

    public Booking[] loadWaitingBookings() {
        return new Booking[]{
            createProjectStartBooking("B001", "00000001", "W001", "Ethan Goh",
                    "900101105121", "Deluxe", 2),
            createProjectStartBooking("B002", "00000002", "W002", "Farah Ali",
                    "920405085432", "Suite", 1),
            createProjectStartBooking("B003", "00000003", "W003", "Gan Wei Ming",
                    "880715106789", "Deluxe", 3),
            createProjectStartBooking("B004", "00000004", "W004", "Hannah Lee",
                    "950922145678", "Penthouse", 2)
        };
    }

    private Booking createProjectStartBooking(String bookingId, String confirmationNumber,
            String waitingNumber, String guestName, String guestIc, String roomType,
            int numberOfNights) {
        Booking booking = new Booking(bookingId, confirmationNumber, waitingNumber, guestName,
                guestIc, roomType, numberOfNights);
        booking.setRegistrationTime(projectStartTime);
        return booking;
    }
}
