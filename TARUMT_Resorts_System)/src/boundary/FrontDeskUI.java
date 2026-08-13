// Author: [Chew Zhi Foong]
package boundary;

import control.FrontDeskController;
import control.HousekeepingController;
import entity.GuestProfile;
import entity.Room;
import adt.MapInterface;
import java.util.Scanner;
import entity.Booking;
import control.RegisterInterfaceController;

public class FrontDeskUI {

    private FrontDeskController controller;
    private HousekeepingController housekeepingController;
    private Scanner scanner;

    public FrontDeskUI() {
        this.controller = new FrontDeskController();
        this.housekeepingController = new HousekeepingController();
        this.scanner = new Scanner(System.in);
    }

    public FrontDeskUI(HousekeepingController housekeepingController) {
        this(housekeepingController, new Scanner(System.in));
    }

    public FrontDeskUI(HousekeepingController housekeepingController, Scanner scanner) {
        this.controller = new FrontDeskController();
        this.housekeepingController = housekeepingController;
        this.scanner = scanner;
    }

    public FrontDeskUI(HousekeepingController housekeepingController,
            MapInterface<String, Room> roomMap, Scanner scanner) {
        this.controller = new FrontDeskController(roomMap);
        this.housekeepingController = housekeepingController;
        this.scanner = scanner;
    }

    public void startMenu() {
        int choice = -1;
        do {
            System.out.println("\n================================================");
            System.out.println("     TARUMT RESORTS - FRONT DESK SYSTEM        ");
            System.out.println("================================================");
            System.out.println("1. Search Guest by 8-Digit Confirmation Number");
            System.out.println("2. Search Available Rooms by Room Type");
            System.out.println("3. Generate Report: High Outstanding Bills");
            System.out.println("4. Generate Report: Occupancy Roster by Room Type");
            System.out.println("5. Handle Normal Check-Out");
            System.out.println("6. Handle Late Check-Out Request");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                    handleGuestSearch();
                    break;
                case 2:
                    handleRoomAvailabilitySearch();
                    break;
                case 3:
                    handleBillingReport();
                    break;
                case 4:
                    handleRoomRosterReport();
                    break;
                case 5:
                    handleNormalCheckOut();
                    break;
                case 6:
                    HandleLateCheckOut();
                    break;
                case 0:
                    System.out.println("\nExiting Front Desk System. Goodbye!");
                    break;
                default:
                    System.out.println("\n[Error] Invalid choice! Please enter a number from 0 to 6.");
            }
        } while (choice != 0);
    }

    private void HandleLateCheckOut() {
        System.out.println("\n--- LATE CHECK-OUT REQUEST ---");
        System.out.print("Enter room number requesting late check-out: ");
        String roomNumber = scanner.nextLine().trim();

        if (roomNumber.isEmpty()) {
            System.out.println("[Error] Room number cannot be blank.");
            return;
        }

        System.out.println(housekeepingController.HandleLateCheckOut(roomNumber));
    }

    public FrontDeskUI(HousekeepingController housekeepingController,
            MapInterface<String, Room> roomMap, Scanner scanner,
            RegisterInterfaceController bookingController) {
        this.controller = new FrontDeskController(roomMap, bookingController);
        this.housekeepingController = housekeepingController;
        this.scanner = scanner;
    }

    private void handleNormalCheckOut() {
        System.out.println("\n--- NORMAL CHECK-OUT ---");
        System.out.print("Enter room number for check-out: ");
        String roomNumber = scanner.nextLine().trim();

        if (roomNumber.isEmpty()) {
            System.out.println("[Error] Room number cannot be blank.");
            return;
        }

        System.out.println(housekeepingController.handleNormalCheckOut(roomNumber));
    }

    private void handleGuestSearch() {
        System.out.println("\n--- GUEST INSTANT SEARCH ---");
        System.out.print("Enter 8-digit confirmation number: ");
        String confNum = scanner.nextLine().trim();

        if (!confNum.matches("\\d{8}")) {
            System.out.println("[Error] Confirmation number must contain exactly 8 digits.");
            return;
        }

        Booking booking = controller.findWalkInBookingByConfirmation(confNum);
        if (booking != null) {
            System.out.println("\n[BOOKING MATCH FOUND]");
            printBookingDetails(booking);
            return;
        }

        GuestProfile guest = controller.findGuestByConfirmation(confNum);

        if (guest != null) {
            System.out.println("\n[MATCH FOUND]");
            printGuestDetails(guest);
        } else {
            System.out.println("\n[NOT FOUND] No record associated with ID: " + confNum);
        }
    }

    private void handleBillingReport() {
        System.out.println("\n--- REPORT: HIGH OUTSTANDING BILLS ---");
        System.out.print("Enter minimum billing threshold (RM): ");
        double threshold;
        try {
            threshold = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Billing threshold must be a valid amount.");
            return;
        }

        if (!Double.isFinite(threshold) || threshold < 0) {
            System.out.println("[Error] Billing threshold must be a non-negative amount.");
            return;
        }

        System.out.print("Enter room category (or All): ");
        String roomType = scanner.nextLine().trim();
        if (roomType.isEmpty()) {
            System.out.println("[Error] Room category cannot be blank.");
            return;
        }

        GuestProfile[] reportData = controller.getHighOutstandingBillsReport(threshold, roomType);

        System.out.println("\n=========================================================================");
        System.out.println("           MANAGEMENT REPORT: OUTSTANDING BILLING SUMMARY                 ");
        System.out.println("           Filters: Balance >= RM " + String.format("%.2f", threshold)
                + " | Room Type: " + roomType.toUpperCase() + " | Sorted: Highest Bill First");
        System.out.println("=========================================================================");

        if (reportData.length == 0) {
            System.out.println("No records match the specified billing threshold.");
        } else {
            printTableHeader();
            for (GuestProfile g : reportData) {
                printTableRow(g);
            }
            printTableFooter();
            System.out.println("Total Records Generated: " + reportData.length);
            System.out.println("Total Outstanding Billing: RM "
                    + String.format("%.2f", calculateTotalBilling(reportData)));
        }
    }

    private void handleRoomAvailabilitySearch() {
        System.out.println("\n--- ROOM AVAILABILITY SEARCH ---");
        System.out.print("Enter room category (e.g., Deluxe, Suite, Penthouse): ");
        String roomType = scanner.nextLine().trim();

        if (roomType.isEmpty()) {
            System.out.println("[Error] Room category cannot be blank.");
            return;
        }

        Room[] availableRooms = controller.getAvailableRoomsByType(roomType);
        if (availableRooms.length == 0) {
            System.out.println("No rooms ready for check-in were found for: " + roomType);
            return;
        }

        System.out.println("\nAvailable " + roomType + " Rooms:");
        System.out.println("+-------------+---------------+--------------------+");
        System.out.printf("| %-11s | %-13s | %-18s |%n", "Room Number", "Room Type", "Status");
        System.out.println("+-------------+---------------+--------------------+");
        for (Room room : availableRooms) {
            System.out.printf("| %-11s | %-13s | %-18s |%n",
                    room.getRoomNumber(), room.getRoomType(), room.getStatus());
        }
        System.out.println("+-------------+---------------+--------------------+");
        System.out.println("Total Available Rooms: " + availableRooms.length);
    }

    private void handleRoomRosterReport() {
        System.out.println("\n--- REPORT: ROOM TYPE OCCUPANCY ROSTER ---");
        System.out.print("Enter room category (e.g., Deluxe, Suite, Penthouse): ");
        String roomType = scanner.nextLine().trim();

        if (roomType.isEmpty()) {
            System.out.println("[Error] Room category cannot be blank.");
            return;
        }

        System.out.print("Enter minimum current bill (RM, enter 0 to include all): ");
        double minBill;
        try {
            minBill = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Minimum bill must be a valid amount.");
            return;
        }

        if (!Double.isFinite(minBill) || minBill < 0) {
            System.out.println("[Error] Minimum bill must be a non-negative amount.");
            return;
        }

        GuestProfile[] reportData = controller.getGuestsByRoomTypeReport(roomType, minBill);

        System.out.println("\n=========================================================================");
        System.out.println("           MANAGEMENT REPORT: ROOM OCCUPANCY ROSTER                      ");
        System.out.println("           Filters: Category: " + roomType.toUpperCase()
                + " | Bill >= RM " + String.format("%.2f", minBill) + " | Sorted: Alphabetical (A-Z)");
        System.out.println("=========================================================================");

        if (reportData.length == 0) {
            System.out.println("No active guests found for room type: " + roomType);
        } else {
            printTableHeader();
            for (GuestProfile g : reportData) {
                printTableRow(g);
            }
            printTableFooter();
            System.out.println("Total Occupants: " + reportData.length);
            System.out.println("Total Current Billing: RM "
                    + String.format("%.2f", calculateTotalBilling(reportData)));
        }
    }

    private void printTableHeader() {
        System.out.println("+---------------+----------------------+---------------+-----------------+");
        System.out.printf("| %-13s | %-20s | %-13s | %-15s |\n", "Conf. Number", "Guest Name", "Room Type", "Bill Amount(RM)");
        System.out.println("+---------------+----------------------+---------------+-----------------+");
    }

    private void printGuestDetails(GuestProfile guest) {
        System.out.println("+------------------------------------------------+");
        System.out.printf("| Confirmation Number : %-25s |%n", guest.getConfirmationNumber());
        System.out.printf("| Guest Name          : %-25s |%n", guest.getGuestName());
        System.out.printf("| Contact Number      : %-25s |%n", guest.getContactNumber());
        System.out.printf("| Room                : %-25s |%n",
                guest.getRoomNumber() + " (" + guest.getRoomType() + ")");
        System.out.printf("| Stay Status         : %-25s |%n", guest.getStayStatus());
        System.out.printf("| Current Bill        : RM %-22.2f |%n", guest.getCurrentBilling());
        System.out.println("+------------------------------------------------+");
    }

    private void printBookingDetails(Booking booking) {
        System.out.println("+------------------------------------------------+");
        System.out.printf("| Confirmation Number : %-25s |%n", booking.getConfirmationNumber());
        System.out.printf("| Booking ID          : %-25s |%n", booking.getBookingId());
        System.out.printf("| Guest Name          : %-25s |%n", booking.getGuestDisplayName());
        System.out.printf("| Membership Type     : %-25s |%n", booking.getMembershipType());
        System.out.printf("| Room Type Requested : %-25s |%n", booking.getRoomType());
        System.out.printf("| Assigned Room       : %-25s |%n",
                booking.getRoomId() == null ? "Not assigned" : booking.getRoomId());
        System.out.printf("| Booking Status      : %-25s |%n", booking.getBookingStatus());
        System.out.printf("| Number of Nights    : %-25d |%n", booking.getNumberOfNights());
        System.out.println("+------------------------------------------------+");
    }

    private void printTableRow(GuestProfile g) {
        System.out.printf("| %-13s | %-20s | %-13s | RM %-12.2f |\n",
                g.getConfirmationNumber(),
                g.getGuestName(),
                g.getRoomType(),
                g.getCurrentBilling());
    }

    private void printTableFooter() {
        System.out.println("+---------------+----------------------+---------------+-----------------+");
    }

    private double calculateTotalBilling(GuestProfile[] guests) {
        double total = 0;
        for (GuestProfile guest : guests) {
            total += guest.getCurrentBilling();
        }
        return total;
    }

    public static void main(String[] args) {
        new FrontDeskUI().startMenu();
    }
}
