package boundary;

import control.RegisterInterfaceController;
import Dao.MemberDao;
import entity.Member;
import entity.Booking;
import java.util.Scanner;
import walkinregistrationbooking.BookingReport;
import walkinregistrationbooking.Room;
import walkinregistrationbooking.RoomDao;

public class RegisterBookingUI {

    private Scanner scanner;

    private RegisterInterfaceController controller;

    private RoomDao roomDAO;

    private BookingReport report;

    private boolean loggedIn = true;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public RegisterBookingUI(
            RegisterInterfaceController controller,
            RoomDao roomDAO) {

        this.controller = controller;

        this.roomDAO = roomDAO;

        this.scanner =
                new Scanner(System.in);

        this.report =
                new BookingReport();
    }

    // =====================================================
    // START
    // =====================================================

    public void start() {

        while (loggedIn) {

            displayMenu();

            int choice =
                    getIntegerInput(
                            "Enter your choice: "
                    );

            switch (choice) {

                case 1:
                    registerBooking();
                    break;

                case 2:
                    callNextGuest();
                    break;

                case 3:
                    viewWaitingQueue();
                    break;

                case 4:
                    viewRooms();
                    break;

                case 5:
                    logoutGuest();
                    break;

                case 6:
                    viewWaitingTimeReport();
                    break;

                case 7:
                    viewQueuePriorityReport();
                    break;

                case 8:
                    logout();
                    break;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    // =====================================================
    // MENU
    // =====================================================

    private void displayMenu() {

        System.out.println(
                "\n================================"
        );

        System.out.println(
                "       HOTEL BOOKING SYSTEM"
        );

        System.out.println(
                "================================"
        );

        System.out.println(
                "1. Register Booking"
        );

        System.out.println(
                "2. Call Next Guest"
        );

        System.out.println(
                "3. View Waiting Queue"
        );

        System.out.println(
                "4. View Rooms"
        );

        System.out.println(
                "5. Guest Logout"
        );

        System.out.println(
                "6. Guest Waiting Time Report"
        );

        System.out.println(
                "7. Queue Priority Report"
        );

        System.out.println(
                "8. Logout System"
        );

        System.out.println(
                "================================"
        );
    }

    // =====================================================
    // REGISTER BOOKING
    // =====================================================

    private void registerBooking() {

        System.out.println(
                "\n===== REGISTER WALK-IN ====="
        );

        // -----------------------------------------------
        // IC NUMBER
        // -----------------------------------------------

        System.out.print(
                "Enter IC Number (Enter -1 to exit): "
        );

        String icNumber =
                scanner.nextLine();

        if (icNumber.equals("-1")) {

            System.out.println(
                    "Registration cancelled."
            );

            return;
        }

        // -----------------------------------------------
        // SEARCH MEMBER
        // -----------------------------------------------

        Member member =
                controller.findMemberByIC(
                        icNumber
                );

        String guestName = "";

        // -----------------------------------------------
        // MEMBER FOUND
        // -----------------------------------------------

        if (member != null) {

            System.out.println(
                    "\n===== MEMBER FOUND ====="
            );

            System.out.println(
                    "Name       : "
                            + member.getMemberName()
            );

            System.out.println(
                    "Membership : "
                            + member.getMembershipType()
            );

            System.out.println(
                    "IC Number  : "
                            + member.getIcNumber()
            );

            if (member.getMembershipType()
                    .equalsIgnoreCase("VIP")) {

                System.out.println(
                        "\nVIP REQUEST"
                );

                System.out.println(
                        "VIP member will receive queue priority."
                );

            } else {

                System.out.println(
                        "\nNORMAL MEMBER REQUEST"
                );
            }
        }

        // -----------------------------------------------
        // GUEST
        // -----------------------------------------------

        else {

            System.out.println(
                    "\n===== GUEST REQUEST ====="
            );

            System.out.println(
                    "IC Number is not registered."
            );

            System.out.print(
                    "Enter Guest Name (Enter -1 to exit): "
            );

            guestName =
                    scanner.nextLine();

            if (guestName.equals("-1")) {

                System.out.println(
                        "Registration cancelled."
                );

                return;
            }
        }

        // -----------------------------------------------
        // ROOM TYPE
        // -----------------------------------------------

        System.out.print(
                "\nEnter Room Type ((Penthouse/Deluxe/Suite)or -1 to exit): "
        );

        String roomType =
                scanner.nextLine();

        if (roomType.equals("-1")) {

            System.out.println(
                    "Registration cancelled."
            );

            return;
        }

        // -----------------------------------------------
        // NUMBER OF NIGHTS
        // -----------------------------------------------

        int numberOfNights;

        while (true) {

            System.out.print(
                    "Enter Number of Nights (Enter -1 to exit): "
            );

            String input =
                    scanner.nextLine();

            if (input.equals("-1")) {

                System.out.println(
                        "Registration cancelled."
                );

                return;
            }

            try {

                numberOfNights =
                        Integer.parseInt(
                                input
                        );

                if (numberOfNights <= 0) {

                    System.out.println(
                            "Number of nights must be greater than 0."
                    );

                    continue;
                }

                break;

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number."
                );
            }
        }

        // -----------------------------------------------
        // SEND TO CONTROLLER
        // -----------------------------------------------

        controller.registerBooking(
                icNumber,
                roomType,
                numberOfNights,
                guestName
        );
    }

    // =====================================================
    // CALL NEXT GUEST
    // =====================================================

 private void callNextGuest() {

    System.out.println(
            "\n===== CALL NEXT GUEST ====="
    );

    // =====================================================
    // CHECK QUEUE
    // =====================================================

    Booking booking =
            controller.peekNextBooking();

    if (booking == null) {

        System.out.println(
                "There are no guests waiting."
        );

        return;
    }

    // =====================================================
    // DISPLAY GUEST
    // =====================================================

    System.out.println(
            "\nGuest Information"
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
    // GET AVAILABLE ROOMS
    // =====================================================

    java.util.List<Room> availableRooms =
            roomDAO.getAvailableRooms(
                    booking.getRoomType()
            );

    if (availableRooms.isEmpty()) {

        System.out.println(
                "\nNo available "
                        + booking.getRoomType()
                        + " rooms."
        );

        System.out.println(
                booking.getWaitingNumber()
                        + " remains in the queue."
        );

        return;
    }

    // =====================================================
    // DISPLAY AVAILABLE ROOMS
    // =====================================================

    System.out.println(
            "\n===== AVAILABLE ROOMS ====="
    );

    System.out.printf(
            "%-10s %-15s %-25s%n",
            "Room ID",
            "Room Type",
            "Status"
    );

    System.out.println(
            "------------------------------------------------"
    );

    for (Room room : availableRooms) {

        System.out.printf(
                "%-10s %-15s %-25s%n",
                room.getRoomId(),
                room.getRoomType(),
                room.getStatus()
        );
    }

    System.out.println(
            "------------------------------------------------"
    );

    // =====================================================
    // ADMIN SELECT ROOM
    // =====================================================

    System.out.print(
            "Enter Room ID to assign (Enter -1 to cancel): "
    );

    String roomId =
            scanner.nextLine();

    if (roomId.equals("-1")) {

        System.out.println(
                "Room assignment cancelled."
        );

        return;
    }

    // =====================================================
    // SEND SELECTED ROOM TO CONTROLLER
    // =====================================================

    controller.callNextGuest(
            roomId
    );
}

    // =====================================================
    // VIEW QUEUE
    // =====================================================

    private void viewWaitingQueue() {

        controller.displayBookingQueue();
    }

    // =====================================================
    // VIEW ROOMS
    // =====================================================

    private void viewRooms() {

        roomDAO.displayRooms();
    }

    // =====================================================
    // GUEST LOGOUT
    // =====================================================

    private void logoutGuest() {

        System.out.println(
                "\n===== GUEST LOGOUT ====="
        );

        System.out.print(
                "Enter Room ID (Enter -1 to exit): "
        );

        String roomId =
                scanner.nextLine();

        if (roomId.equals("-1")) {

            System.out.println(
                    "Logout cancelled."
            );

            return;
        }

        controller.logoutGuest(
                roomId
        );
    }

    // =====================================================
    // WAITING TIME REPORT
    // =====================================================

    private void viewWaitingTimeReport() {

        report.generateWaitingTimeReport(
                controller.getBookingHistory()
        );
    }

    // =====================================================
    // QUEUE PRIORITY REPORT
    // =====================================================

    private void viewQueuePriorityReport() {

        report.generateQueuePriorityReport(
                controller.getBookingHistory()
        );
    }

    // =====================================================
    // SYSTEM LOGOUT
    // =====================================================

    private void logout() {

        loggedIn = false;

        System.out.println(
                "\nYou have logged out of the system."
        );
    }

    // =====================================================
    // INTEGER INPUT
    // =====================================================

    private int getIntegerInput(
            String message) {

        while (true) {

            System.out.print(message);

            try {

                return Integer.parseInt(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input. Please enter a number."
                );
            }
        }
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {

        MemberDao memberDAO =
                new MemberDao();

        RoomDao roomDAO =
                new RoomDao();

        RegisterInterfaceController controller =
                new RegisterInterfaceController(
                        memberDAO,
                        roomDAO
                );

        RegisterBookingUI ui =
                new RegisterBookingUI(
                        controller,
                        roomDAO
                );

        ui.start();
    }
}