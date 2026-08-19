// Author: Yong Shen
package boundary;

import control.RegisterInterfaceController;
import control.VipRoomAllocationController;
import adt.MapInterface;
import entity.Booking;
import entity.LoyaltyRoomRequest;
import entity.Member;
import entity.Room;
import java.util.Scanner;

public class VipRoomAllocationUI {

    private VipRoomAllocationController controller;
    private RegisterInterfaceController registerController;
    private RegisterBookingUI registerBookingUI;
    private Scanner scanner;

    public VipRoomAllocationUI() {
        this(new VipRoomAllocationController(), new Scanner(System.in));
    }

    public VipRoomAllocationUI(MapInterface<String, Room> roomMap, Scanner scanner) {
        this(new VipRoomAllocationController(roomMap), scanner);
    }

    public VipRoomAllocationUI(VipRoomAllocationController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public VipRoomAllocationUI(VipRoomAllocationController controller,
            RegisterInterfaceController registerController,
            Scanner scanner) {
        this.controller = controller;
        this.registerController = registerController;
        this.scanner = scanner;
    }

    public void setRegisterBookingUI(RegisterBookingUI registerBookingUI) {
        this.registerBookingUI = registerBookingUI;
    }

    public void startMenu() {
        int choice;
        do {
            System.out.println(" \nVIP & LOYALTY TIER PRIORITY ROOM ALLOCATION");
            System.out.println("-----------------------------------------------");
            System.out.println("1. Add room request");
            System.out.println("2. Call Next Guest");
            System.out.println("3. Search request by ID");
            System.out.println("4. Report: Waiting priority list");
            System.out.println("5. Report: Allocation summary");
            System.out.println("6. View room status");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1:
                    addRequest();
                    break;
                case 2:
                    callNextGuest();
                    break;
                case 3:
                    searchRequest();
                    break;
                case 4:
                    waitingPriorityReport();
                    break;
                case 5:
                    allocationSummaryReport();
                    break;
                case 6:
                    displayRooms();
                    break;
                case 0:
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private void addRequest() {
        System.out.println("\n--- ADD ROOM REQUEST ---");

        System.out.print("Enter IC/Passport: ");
        String icNumber = scanner.nextLine().trim();

        Member member = registerController.findMemberByIC(icNumber);

        if (member == null || !isLoyaltyTier(member.getMembershipType())) {
            System.out.println("This IC is not a VIP/member.");
            System.out.println("Redirecting to Walk-In Registration...");

            if (registerBookingUI != null) {
                registerBookingUI.registerBookingWithCheckedIc(icNumber);
            }
            return;
        }

        addRequestFromCheckedMember(member);
    }

    public void addRequestFromCheckedMember(Member member) {
        System.out.println("\n--- ADD LOYALTY ROOM REQUEST ---");
        System.out.println("Name : " + member.getMemberName());
        System.out.println("Tier : " + member.getMembershipType());

        System.out.print("Room type (Deluxe/Suite/Penthouse): ");
        String roomType = scanner.nextLine().trim();
        System.out.print("Stay nights: ");
        int stayNights = readInt();

        double spending = controller.calculateEstimatedSpending(roomType, stayNights);
        LoyaltyRoomRequest request = controller.addRequest(member.getMemberName(),
                member.getMembershipType(), roomType, stayNights, spending);

        System.out.println("Request added to priority binary search tree.");
        System.out.println("Auto Request ID: " + request.getRequestId());
    }

    private void callNextGuest() {
        if (registerController == null) {
            System.out.println("Booking controller is not ready.");
            return;
        }

        System.out.println("\n===== CALL NEXT GUEST =====");

        Booking booking = registerController.peekNextEligibleBooking();

        if (booking == null) {
            System.out.println("There are no guests waiting.");
            return;
        }

        System.out.println("\nGuest Information");
        System.out.println("Waiting Number : " + booking.getWaitingNumber());
        System.out.println("Guest          : " + booking.getGuestDisplayName());
        System.out.println("Type           : " + booking.getMembershipType());
        System.out.println("Room Type      : " + booking.getRoomType());
        System.out.println("Number of Nights : " + booking.getNumberOfNights());

        Room[] availableRooms = registerController.getAvailableRooms(booking.getRoomType());

        if (availableRooms.length == 0) {
            System.out.println("\nNo assignable rooms are currently available.");
            return;
        }

        System.out.println("\n===== AVAILABLE ROOMS =====");
        System.out.printf("%-10s %-15s %-25s%n", "Room ID", "Room Type", "Status");
        System.out.println("------------------------------------------------");

        for (int i = 0; i < availableRooms.length; i++) {
            System.out.printf("%-10s %-15s %-25s%n",
                    availableRooms[i].getRoomNumber(),
                    availableRooms[i].getRoomType(),
                    availableRooms[i].getStatus());
        }

        System.out.println("------------------------------------------------");
        System.out.print("Enter Room ID to assign (Enter -1 to cancel): ");
        String roomId = scanner.nextLine();

        if (roomId.equals("-1")) {
            System.out.println("Room assignment cancelled.");
            return;
        }

        registerController.callNextGuest(roomId);
    }

    private void searchRequest() {
        System.out.print("\nEnter request ID: ");
        String requestId = scanner.nextLine().trim();
        LoyaltyRoomRequest request = controller.findRequestById(requestId);

        if (request == null) {
            System.out.println("No request found.");
        } else {
            printRequestHeader();
            printRequestRow(request);
            printLine();
        }
    }

    private void waitingPriorityReport() {
        System.out.println("\n--- WAITING PRIORITY REPORT ---");
        System.out.print("Room type filter (All/Deluxe/Suite/Penthouse): ");
        String roomType = scanner.nextLine().trim();
        System.out.print("Minimum tier (PLATINUM/DIAMOND/ELITE): ");
        String tier = scanner.nextLine().trim();

        LoyaltyRoomRequest[] report = controller.getWaitingPriorityReport(roomType, tier);
        System.out.println("\nFiltered by room type and tier. Sorted by highest priority.");
        displayRequestReport(report);

        if (registerController != null) {
            registerController.displayGuestQueueOnly();
        }
    }

    private void allocationSummaryReport() {
        System.out.println("\n--- ALLOCATION SUMMARY REPORT ---");
        System.out.print("Room type filter (All/Deluxe/Suite/Penthouse): ");
        String roomType = scanner.nextLine().trim();
        System.out.print("Minimum stay nights: ");
        int nights = readInt();

        LoyaltyRoomRequest[] report = controller.getAllocationSummaryReport(roomType, nights);
        System.out.println("\nFiltered by room type and stay nights. Sorted by tier and spending.");
        displayRequestReport(report);
        System.out.printf("Total guest spending: RM %.2f\n", controller.calculateTotalSpending(report));
    }

    private void displayRooms() {
        Room[] rooms = controller.getRooms();
        System.out.println("\nROOM STATUS");
        System.out.println("+----------+--------------+----------+------------+");
        System.out.printf("| %-8s | %-12s | %-8s | %-10s |\n", "Room No", "Room Type", "Class", "Status");
        System.out.println("+----------+--------------+----------+------------+");
        for (int i = 0; i < rooms.length; i++) {
            System.out.printf("| %-8s | %-12s | %-8s | %-10s |\n",
                    rooms[i].getRoomNumber(),
                    rooms[i].getRoomType(),
                    rooms[i].getRoomType().equalsIgnoreCase("Penthouse") ? "VIP" : "Normal",
                    rooms[i].isAvailable() ? "Available" : "Occupied");
        }
        System.out.println("+----------+--------------+----------+------------+");
    }

    private void displayRequestReport(LoyaltyRoomRequest[] report) {
        if (report.length == 0) {
            System.out.println("No records found.");
            return;
        }

        printRequestHeader();
        for (int i = 0; i < report.length; i++) {
            printRequestRow(report[i]);
        }
        printLine();
        System.out.println("Total records: " + report.length);
    }

    private void printRequestHeader() {
        printLine();
        System.out.printf("| %-8s | %-18s | %-10s | %-12s | %-6s | %-12s | %-8s | %-8s |\n",
                "Req ID", "Guest", "Tier", "Room Type", "Nights", "Spending", "Score", "Room");
        printLine();
    }

    private void printRequestRow(LoyaltyRoomRequest request) {
        System.out.printf("| %-8s | %-18s | %-10s | %-12s | %-6d | RM %-9.2f | %-8d | %-8s |\n",
                request.getRequestId(),
                request.getGuestName(),
                request.getLoyaltyTier(),
                request.getRoomType(),
                request.getStayNights(),
                request.getTotalSpending(),
                request.getPriorityScore(),
                request.getAllocatedRoomNo());
    }

    private void printLine() {
        System.out.println("+----------+--------------------+------------+--------------+--------+--------------+----------+----------+");
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean isLoyaltyTier(String tier) {
        return tier != null
                && (tier.equalsIgnoreCase("Platinum")
                || tier.equalsIgnoreCase("Diamond")
                || tier.equalsIgnoreCase("Elite"));
    }

    public static void main(String[] args) {
        new VipRoomAllocationUI().startMenu();
    }
}
