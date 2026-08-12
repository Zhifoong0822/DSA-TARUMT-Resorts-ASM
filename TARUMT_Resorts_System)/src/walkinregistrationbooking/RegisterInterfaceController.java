package walkinregistrationbooking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegisterInterfaceController {

    private MemberDao memberDAO;
    private RoomDao roomDAO;

    private CustomQueue<Booking> bookingQueue;

    private List<Booking> bookingHistory;

    private int bookingCounter = 1;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public RegisterInterfaceController(
            MemberDao memberDAO,
            RoomDao roomDAO) {

        this.memberDAO = memberDAO;
        this.roomDAO = roomDAO;

        bookingQueue =
                new CustomQueue<>();

        bookingHistory =
                new ArrayList<>();
    }

    // =====================================================
    // FIND MEMBER BY IC
    // =====================================================

    public Member findMemberByIC(
            String icNumber) {

        return memberDAO.findMemberByIC(
                icNumber
        );
    }

    // =====================================================
    // REGISTER BOOKING
    // =====================================================

    public void registerBooking(
            String icNumber,
            String roomType,
            int numberOfNights,
            String guestName) {

        // Search member using IC
        Member member =
                memberDAO.findMemberByIC(
                        icNumber
                );

        // Generate booking ID
        String bookingId =
                String.format(
                        "B%03d",
                        bookingCounter
                );

        // Generate waiting number
        String waitingNumber =
                String.format(
                        "W%03d",
                        bookingCounter
                );

        bookingCounter++;

   Booking booking;

if (member != null) {

    // Registered member
    booking = new Booking(
            bookingId,
            waitingNumber,
            member,
            roomType,
            numberOfNights
    );

} else {

    // Non-member guest
    booking = new Booking(
            bookingId,
            waitingNumber,
            guestName,
            icNumber,
            roomType,
            numberOfNights
    );
}

// Insert according to priority
bookingQueue.enqueueByPriority(
        booking
);

// Save to history
bookingHistory.add(booking);
        // =================================================
        // DISPLAY RESULT
        // =================================================

        System.out.println(
                "\n===== REGISTRATION SUCCESSFUL ====="
        );

        System.out.println(
                "Booking ID       : "
                        + booking.getBookingId()
        );

        System.out.println(
                "Waiting Number   : "
                        + booking.getWaitingNumber()
        );

        System.out.println(
                "Guest            : "
                        + booking.getGuestDisplayName()
        );

        System.out.println(
                "Type             : "
                        + booking.getMembershipType()
        );

        System.out.println(
                "Room Type        : "
                        + booking.getRoomType()
        );

        System.out.println(
                "Number of Nights : "
                        + booking.getNumberOfNights()
        );

        System.out.println(
                "Registration Time: "
                        + booking
                        .getFormattedRegistrationTime()
        );

        System.out.println(
                "Status           : "
                        + booking.getBookingStatus()
        );

        System.out.println(
                "\nPlease wait for your number to be called."
        );
    }

    // =====================================================
    // CALL NEXT GUEST
    // =====================================================

   public void callNextGuest(String selectedRoomId) {

    if (bookingQueue.isEmpty()) {

        System.out.println(
                "\nThere are no guests waiting."
        );

        return;
    }

    // Look at first guest
    Booking booking =
            bookingQueue.peek();

    System.out.println(
            "\n===== CALL NEXT GUEST ====="
    );

    System.out.println(
            "Waiting Number : "
                    + booking.getWaitingNumber()
    );

    System.out.println(
            "Guest          : "
                    + booking.getGuestDisplayName()
    );

    System.out.println(
            "Type           : "
                    + booking.getMembershipType()
    );

    System.out.println(
            "Room Type      : "
                    + booking.getRoomType()
    );

    System.out.println(
            "Number of Nights : "
                    + booking.getNumberOfNights()
    );

    // =====================================================
    // FIND SELECTED ROOM
    // =====================================================

    Room room =
            roomDAO.findRoomById(
                    selectedRoomId
            );

    if (room == null) {

        System.out.println(
                "\nRoom ID not found."
        );

        return;
    }

    // =====================================================
    // CHECK ROOM TYPE
    // =====================================================

    if (!room.getRoomType()
            .equalsIgnoreCase(
                    booking.getRoomType()
            )) {

        System.out.println(
                "\nInvalid room selection."
        );

        System.out.println(
                "Guest requested : "
                        + booking.getRoomType()
        );

        System.out.println(
                "Selected room   : "
                        + room.getRoomType()
        );

        return;
    }

    // =====================================================
    // CHECK ROOM STATUS
    // =====================================================

    if (!room.getStatus()
            .equalsIgnoreCase(
                    "Ready For Check-In"
            )) {

        System.out.println(
                "\nRoom "
                        + selectedRoomId
                        + " is not available."
        );

        System.out.println(
                "Current status: "
                        + room.getStatus()
        );

        return;
    }

    // =====================================================
    // REMOVE FROM QUEUE
    // =====================================================

    booking =
            bookingQueue.dequeue();

    // =====================================================
    // ASSIGN ROOM
    // =====================================================

    booking.setRoomId(
            room.getRoomId()
    );

    booking.setRoomAssignmentTime(
            LocalDateTime.now()
    );

    booking.setBookingStatus(
            "ASSIGNED"
    );

    // =====================================================
    // UPDATE ROOM STATUS
    // =====================================================

    room.setStatus(
            "Occupied"
    );

    // =====================================================
    // DISPLAY RESULT
    // =====================================================

    System.out.println(
            "\n===== ROOM ASSIGNED ====="
    );

    System.out.println(
            "Waiting Number : "
                    + booking.getWaitingNumber()
    );

    System.out.println(
            "Guest          : "
                    + booking.getGuestDisplayName()
    );

    System.out.println(
            "Type           : "
                    + booking.getMembershipType()
    );

    System.out.println(
            "Room Type      : "
                    + booking.getRoomType()
    );

    System.out.println(
            "Room Number    : "
                    + booking.getRoomId()
    );

    System.out.println(
            "Number of Nights : "
                    + booking.getNumberOfNights()
    );

    System.out.println(
            "Registration Time : "
                    + booking
                    .getFormattedRegistrationTime()
    );

    System.out.println(
            "Room Assigned Time : "
                    + booking
                    .getFormattedRoomAssignmentTime()
    );

    System.out.println(
            "Status         : "
                    + booking.getBookingStatus()
    );

    System.out.println(
            "\nPlease proceed to room "
                    + booking.getRoomId()
                    + "."
    );
}    public void logoutGuest(
            String roomId) {

        // Find room
        Room room =
                roomDAO.findRoomById(
                        roomId
                );

        if (room == null) {

            System.out.println(
                    "Room ID not found."
            );

            return;
        }

        // Check room status
        if (!room.getStatus()
                .equalsIgnoreCase(
                        "Occupied"
                )) {

            System.out.println(
                    "This room is not currently occupied."
            );

            return;
        }

        Booking booking = null;

        // =================================================
        // SEARCH BOOKING HISTORY
        // =================================================

        for (Booking b : bookingHistory) {

            if (roomId.equalsIgnoreCase(
                    b.getRoomId())
                    && b.getBookingStatus()
                    .equalsIgnoreCase(
                            "ASSIGNED"
                    )) {

                booking = b;

                break;
            }
        }

        if (booking == null) {

            System.out.println(
                    "No active booking found for this room."
            );

            return;
        }

        // =================================================
        // UPDATE BOOKING
        // =================================================

        booking.setBookingStatus(
                "COMPLETED"
        );

        // =================================================
        // UPDATE ROOM
        // =================================================

        room.setStatus(
                "Cleaning In Progress"
        );

        // =================================================
        // DISPLAY RESULT
        // =================================================

        System.out.println(
                "\n===== GUEST LOGOUT SUCCESSFUL ====="
        );

        System.out.println(
                "Guest          : "
                        + booking
                        .getGuestDisplayName()
        );

        System.out.println(
                "Type           : "
                        + booking
                        .getMembershipType()
        );

        System.out.println(
                "Booking ID     : "
                        + booking
                        .getBookingId()
        );

        System.out.println(
                "Room ID        : "
                        + room.getRoomId()
        );

        System.out.println(
                "Booking Status : "
                        + booking
                        .getBookingStatus()
        );

        System.out.println(
                "Room Status    : "
                        + room.getStatus()
        );
    }

    // =====================================================
    // DISPLAY QUEUE
    // =====================================================

    public void displayBookingQueue() {

        if (bookingQueue.isEmpty()) {

            System.out.println(
                    "\nNo bookings are waiting."
            );

            return;
        }

        System.out.println(
                "\n===== WAITING QUEUE ====="
        );

        bookingQueue.display();
    }

    // =====================================================
    // PEEK
    // =====================================================

    public Booking peekNextBooking() {

        return bookingQueue.peek();
    }

    // =====================================================
    // QUEUE SIZE
    // =====================================================

    public int getQueueSize() {

        return bookingQueue.size();
    }

    // =====================================================
    // BOOKING HISTORY
    // =====================================================

    public List<Booking> getBookingHistory() {

        return bookingHistory;
    }

    // =====================================================
    // FIND BOOKING BY ID
    // =====================================================

    public Booking findBookingById(
            String bookingId) {

        for (Booking booking :
                bookingHistory) {

            if (booking.getBookingId()
                    .equalsIgnoreCase(
                            bookingId
                    )) {

                return booking;
            }
        }

        return null;
    }
}