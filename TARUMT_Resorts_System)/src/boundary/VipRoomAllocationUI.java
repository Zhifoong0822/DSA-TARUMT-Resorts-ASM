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

        if (registerController == null) {
            System.out.println("Booking integration is not ready. Open VIP allocation from the main system.");
            return;
        }

        System.out.print("Enter IC/Passport (Enter -1 to exit): ");
        String icNumber = scanner.nextLine().trim();
        if (icNumber.equals("-1")) {
            System.out.println("Request cancelled.");
            return;
        }

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

        int stayNights = inputStayNights();
        if (stayNights == -1) {
            return;
        }

        int numberOfRooms = inputNumberOfRooms();
        if (numberOfRooms == -1) {
            return;
        }

        String[] roomTypes = new String[numberOfRooms];
        for (int i = 0; i < numberOfRooms; i++) {
            roomTypes[i] = inputRoomType(i + 1);
            if (roomTypes[i] == null) {
                return;
            }
        }

        String[] uniqueTypes = new String[numberOfRooms];
        int[] quantities = new int[numberOfRooms];
        int uniqueCount = 0;
        for (int i = 0; i < roomTypes.length; i++) {
            int foundIndex = -1;
            for (int j = 0; j < uniqueCount; j++) {
                if (uniqueTypes[j].equalsIgnoreCase(roomTypes[i])) {
                    foundIndex = j;
                    break;
                }
            }

            if (foundIndex == -1) {
                uniqueTypes[uniqueCount] = roomTypes[i];
                quantities[uniqueCount] = 1;
                uniqueCount++;
            } else {
                quantities[foundIndex]++;
            }
        }

        double totalSpending = 0;
        System.out.println("\n===== LOYALTY REQUEST SUCCESSFUL =====");
        for (int i = 0; i < uniqueCount; i++) {
            LoyaltyRoomRequest request = registerController.registerLoyaltyBooking(member, uniqueTypes[i],
                    stayNights, quantities[i]);
            if (request == null) {
                System.out.println("[Error] Unable to create the VIP booking.");
                return;
            }

            Booking booking = registerController.findBookingForLoyaltyRequest(request.getRequestId());
            totalSpending += controller.calculateBill(uniqueTypes[i], stayNights) * quantities[i];

            System.out.println("\nRoom Request " + (i + 1));
            System.out.println("Auto Request ID : " + request.getRequestId());
            if (booking != null) {
                System.out.println("Confirmation No.: " + booking.getConfirmationNumber());
            }
            System.out.println("Room Type       : " + request.getRoomType());
            System.out.println("Number of Rooms : " + request.getNumberOfRooms());
            System.out.println("Stay Nights     : " + request.getStayNights());
            System.out.printf("Room Bill       : RM %.2f%n",
                    controller.calculateBill(uniqueTypes[i], stayNights) * quantities[i]);
        }

        System.out.println("\nAll request added to priority binary search tree.");
        System.out.printf("Total bill      : RM %.2f%n", totalSpending);
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
        System.out.println("Rooms Remaining: " + booking.getRemainingRooms());
        System.out.println("Number of Nights : " + booking.getNumberOfNights());
        System.out.printf("Total Bill       : RM %.2f%n", booking.getTotalBilling());

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
        System.out.print("\nEnter request ID (Enter -1 to exit): ");
        String requestId = scanner.nextLine().trim();
        if (requestId.equals("-1")) {
            System.out.println("Search cancelled.");
            return;
        }
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
        String roomType = inputRoomTypeFilter();
        if (roomType == null) {
            return;
        }
        String tier = inputMinimumTier();
        if (tier == null) {
            return;
        }

        LoyaltyRoomRequest[] report = controller.getWaitingPriorityReport(roomType, tier);
        System.out.println("\nFiltered by room type and tier. Sorted by highest priority.");
        displayRequestReport(report);

        if (registerController != null) {
            registerController.displayGuestQueueOnly();
        }
    }

    private void allocationSummaryReport() {
        System.out.println("\n--- ALLOCATION SUMMARY REPORT ---");
        String roomType = inputRoomTypeFilter();
        if (roomType == null) {
            return;
        }
        int nights = inputMinimumStayNights();
        if (nights == -1) {
            return;
        }

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
        System.out.printf("| %-8s | %-18s | %-10s | %-12s | %-6s | %-5s | %-5s | %-12s | %-8s | %-12s |\n",
                "Req ID", "Guest", "Tier", "Room Type", "Nights", "Rooms", "Left", "Spending", "Score", "Room");
        printLine();
    }

    private void printRequestRow(LoyaltyRoomRequest request) {
        System.out.printf("| %-8s | %-18s | %-10s | %-12s | %-6d | %-5d | %-5d | RM %-9.2f | %-8d | %-12s |\n",
                request.getRequestId(),
                request.getGuestName(),
                request.getLoyaltyTier(),
                request.getRoomType(),
                request.getStayNights(),
                request.getNumberOfRooms(),
                request.getRemainingRooms(),
                request.getTotalSpending(),
                request.getPriorityScore(),
                request.getAllocatedRoomNo());
    }

    private void printLine() {
        System.out.println("+----------+--------------------+------------+--------------+--------+-------+-------+--------------+----------+--------------+");
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int inputStayNights() {
        while (true) {
            System.out.print("Enter Stay Nights (Enter -1 to exit): ");
            String input = scanner.nextLine().trim();
            if (input.equals("-1")) {
                System.out.println("Request cancelled.");
                return -1;
            }
            try {
                int nights = Integer.parseInt(input);
                if (nights <= 0) {
                    System.out.println("Stay nights must be greater than 0.");
                    continue;
                }
                return nights;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private int inputNumberOfRooms() {
        while (true) {
            System.out.print("Enter Number of Rooms (Enter -1 to exit): ");
            String input = scanner.nextLine().trim();
            if (input.equals("-1")) {
                System.out.println("Request cancelled.");
                return -1;
            }
            try {
                int numberOfRooms = Integer.parseInt(input);
                if (numberOfRooms <= 0) {
                    System.out.println("Number of rooms must be greater than 0.");
                    continue;
                }
                return numberOfRooms;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private String inputRoomType(int roomNumber) {
        while (true) {
            System.out.print("Enter Room Type for Room " + roomNumber
                    + " (Deluxe/Suite/Penthouse, or -1 to exit): ");
            String roomType = scanner.nextLine().trim();
            if (roomType.equals("-1")) {
                System.out.println("Request cancelled.");
                return null;
            }
            if (isValidRoomType(roomType)) {
                return formatRoomType(roomType);
            }
            System.out.println("Room type must be Deluxe, Suite, or Penthouse.");
        }
    }

    private String inputRoomTypeFilter() {
        while (true) {
            System.out.print("Room type filter (All/Deluxe/Suite/Penthouse, or -1 to exit): ");
            String roomType = scanner.nextLine().trim();
            if (roomType.equals("-1")) {
                System.out.println("Report cancelled.");
                return null;
            }
            if (roomType.length() == 0 || roomType.equalsIgnoreCase("All") || isValidRoomType(roomType)) {
                return roomType;
            }
            System.out.println("Please enter All, Deluxe, Suite, or Penthouse.");
        }
    }

    private String inputMinimumTier() {
        while (true) {
            System.out.print("Minimum tier (Platinum/Diamond/Elite, or -1 to exit): ");
            String tier = scanner.nextLine().trim();
            if (tier.equals("-1")) {
                System.out.println("Report cancelled.");
                return null;
            }
            if (isLoyaltyTier(tier)) {
                return tier;
            }
            System.out.println("Please enter Platinum, Diamond, or Elite.");
        }
    }

    private int inputMinimumStayNights() {
        while (true) {
            System.out.print("Minimum stay nights (Enter -1 to exit): ");
            String input = scanner.nextLine().trim();
            if (input.equals("-1")) {
                System.out.println("Report cancelled.");
                return -1;
            }
            try {
                int nights = Integer.parseInt(input);
                if (nights < 0) {
                    System.out.println("Minimum stay nights cannot be negative.");
                    continue;
                }
                return nights;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private boolean isValidRoomType(String roomType) {
        return roomType != null
                && (roomType.equalsIgnoreCase("Deluxe")
                || roomType.equalsIgnoreCase("Suite")
                || roomType.equalsIgnoreCase("Penthouse"));
    }

    private String formatRoomType(String roomType) {
        if (roomType.equalsIgnoreCase("Deluxe")) {
            return "Deluxe";
        }
        if (roomType.equalsIgnoreCase("Suite")) {
            return "Suite";
        }
        return "Penthouse";
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
