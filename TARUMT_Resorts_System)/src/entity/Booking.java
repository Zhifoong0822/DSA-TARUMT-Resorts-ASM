package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Booking {

    private String bookingId;
    private String confirmationNumber;
    private String waitingNumber;

    // Member information
    private Member member;

    // Guest information
    private String guestName;
    private String guestIc;

    private String roomType;
    private int numberOfNights;
    private double totalBilling;

    private String roomId;
    private String bookingStatus;

    private LocalDateTime registrationTime;
    private LocalDateTime roomAssignmentTime;

    // =====================================================
    // CONSTRUCTOR FOR MEMBER
    // =====================================================

    public Booking(
            String bookingId,
            String confirmationNumber,
            String waitingNumber,
            Member member,
            String roomType,
            int numberOfNights) {

        this.bookingId = bookingId;
        this.confirmationNumber = confirmationNumber;
        this.waitingNumber = waitingNumber;
        this.member = member;

        this.roomType = roomType;
        this.numberOfNights = numberOfNights;
        this.totalBilling = calculateTotalBilling(roomType, numberOfNights);

        this.registrationTime =
                LocalDateTime.now();

        this.bookingStatus = "WAITING";
    }

    // =====================================================
    // CONSTRUCTOR FOR GUEST
    // =====================================================

    public Booking(
            String bookingId,
            String confirmationNumber,
            String waitingNumber,
            String guestName,
            String guestIc,
            String roomType,
            int numberOfNights) {

        this.bookingId = bookingId;
        this.confirmationNumber = confirmationNumber;
        this.waitingNumber = waitingNumber;

        this.guestName = guestName;
        this.guestIc = guestIc;

        this.roomType = roomType;
        this.numberOfNights = numberOfNights;
        this.totalBilling = calculateTotalBilling(roomType, numberOfNights);

        this.registrationTime =
                LocalDateTime.now();

        this.bookingStatus = "WAITING";
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public String getBookingId() {
        return bookingId;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public String getWaitingNumber() {
        return waitingNumber;
    }

    public Member getMember() {
        return member;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getGuestIc() {
        return guestIc;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public double getTotalBilling() {
        return totalBilling;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public LocalDateTime getRoomAssignmentTime() {
        return roomAssignmentTime;
    }

    // =====================================================
    // SETTERS
    // =====================================================

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setBookingStatus(
            String bookingStatus) {

        this.bookingStatus = bookingStatus;
    }

    public void setRoomAssignmentTime(
            LocalDateTime roomAssignmentTime) {

        this.roomAssignmentTime =
                roomAssignmentTime;
    }

    // =====================================================
    // GET DISPLAY NAME
    // =====================================================

    public String getGuestDisplayName() {

        if (member != null) {

            return member.getMemberName();
        }

        return guestName;
    }

    // =====================================================
    // GET MEMBER TYPE
    // =====================================================

    public String getMembershipType() {

        if (member != null) {

            return member.getMembershipType();
        }

        return "GUEST";
    }

    // =====================================================
    // FORMATTED REGISTRATION TIME
    // =====================================================

    public String getFormattedRegistrationTime() {

        if (registrationTime == null) {

            return "-";
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss"
                );

        return registrationTime.format(
                formatter
        );
    }

    // =====================================================
    // FORMATTED ROOM ASSIGNMENT TIME
    // =====================================================

    public String getFormattedRoomAssignmentTime() {

        if (roomAssignmentTime == null) {

            return "-";
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss"
                );

        return roomAssignmentTime.format(
                formatter
        );
    }

    private double calculateTotalBilling(String roomType, int numberOfNights) {
        double roomPrice = 300.00;

        if (roomType.equalsIgnoreCase("Suite")) {
            roomPrice = 800.00;
        } else if (roomType.equalsIgnoreCase("Penthouse")) {
            roomPrice = 1200.00;
        }

        return roomPrice * numberOfNights;
    }

    // =====================================================
    // DISPLAY BOOKING
    // =====================================================

    @Override
    public String toString() {

        return String.format(
                "%-8s %-15s %-10s %-10s %-5d %-15s",
                waitingNumber,
                getGuestDisplayName(),
                getMembershipType(),
                roomType,
                numberOfNights,
                bookingStatus
        );
    }
}
