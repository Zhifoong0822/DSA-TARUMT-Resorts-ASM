// Author: Yong Shen
package boundary;

import control.RegisterInterfaceController;
import control.VipRoomAllocationController;
import adt.MapInterface;
import entity.LoyaltyRoomRequest;
import entity.Room;
import java.util.Scanner;

public class VipRoomAllocationUI {

    private VipRoomAllocationController controller;
    private RegisterInterfaceController registerController;
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

    public void startMenu() {
        int choice;
        do {
            System.out.println(" \nVIP & LOYALTY TIER PRIORITY ROOM ALLOCATION");
            System.out.println("-----------------------------------------------");
            System.out.println("1. Add room request");
            System.out.println("2. Search request by ID");
            System.out.println("3. Report: Waiting priority list");
            System.out.println("4. Report: Allocation summary");
            System.out.println("5. View room status");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1:
                    addRequest();
                    break;
                case 2:
                    searchRequest();
                    break;
                case 3:
                    waitingPriorityReport();
                    break;
                case 4:
                    allocationSummaryReport();
                    break;
                case 5:
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
        scanner.nextLine().trim();

        System.out.print("Guest name: ");
        String guestName = scanner.nextLine().trim();

        String loyaltyTier = readLoyaltyTier();

        System.out.print("Room type (Deluxe/Suite/Penthouse): ");
        String roomType = scanner.nextLine().trim();
        System.out.print("Stay nights: ");
        int stayNights = readInt();

        double spending = controller.calculateEstimatedSpending(roomType, stayNights);
        LoyaltyRoomRequest request = controller.addRequest(guestName, loyaltyTier, roomType, stayNights, spending);

        System.out.println("Request added to priority binary search tree.");
        System.out.println("Auto Request ID: " + request.getRequestId());
    }

    private void allocateNext() {
        LoyaltyRoomRequest request = controller.allocateNextRoom();
        if (request == null) {
            System.out.println("\nNo suitable room can be allocated now.");
        } else {
            System.out.println("\nAllocated successfully:");
            printRequestHeader();
            printRequestRow(request);
            printLine();
        }
    }

    private void allocateAll() {
        int count = controller.allocateAllPossibleRooms();
        System.out.println("\nTotal rooms allocated: " + count);
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

    private String readLoyaltyTier() {
        while (true) {
            System.out.print("Loyalty tier (Elite/Diamond/Platinum): ");
            String type = scanner.nextLine().trim();

            if (type.equalsIgnoreCase("Elite")) {
                return "Elite";
            }

            if (type.equalsIgnoreCase("Diamond")) {
                return "Diamond";
            }

            if (type.equalsIgnoreCase("Platinum")) {
                return "Platinum";
            }

            System.out.println("Please enter Elite, Diamond or Platinum.");
        }
    }

    public static void main(String[] args) {
        new VipRoomAllocationUI().startMenu();
    }
}
