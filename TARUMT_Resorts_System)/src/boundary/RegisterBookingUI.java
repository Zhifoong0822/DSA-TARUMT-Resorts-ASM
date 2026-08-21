//Author: Chan Yu He
package boundary;

import control.RegisterInterfaceController;
import Dao.MemberDao;
import Dao.RoomDAO;
import entity.Member;
import entity.Booking;
import entity.Room;
import adt.MapInterface;
import control.VipRoomAllocationController;
import java.util.Scanner;

public class RegisterBookingUI {

    private Scanner scanner;

    private RegisterInterfaceController controller;
    private VipRoomAllocationUI vipRoomAllocationUI;
   
    private boolean loggedIn = true;
    MapInterface<String, Room> roomMap = new RoomDAO().loadRooms();
    VipRoomAllocationController vipController = new VipRoomAllocationController(roomMap);

    public RegisterBookingUI(
            RegisterInterfaceController controller,
            MapInterface<String, Room> roomMap,
            Scanner scanner,
            VipRoomAllocationUI vipRoomAllocationUI) {

        this.controller = controller;

        this.roomMap = roomMap;

        this.scanner = scanner;
        this.vipRoomAllocationUI = vipRoomAllocationUI;
    }

    public void start() {
        loggedIn = true;

        while (loggedIn) {

            displayMenu();

            int choice =getIntegerInput("Enter your choice: ");

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
                    viewWaitingTimeReport();
                    break;

                case 6:
                    viewQueuePriorityReport();
                    break;

                case 7:
                    logout();
                    break;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private void displayMenu() {

        System.out.println("\n================================");
        System.out.println("       HOTEL BOOKING SYSTEM");
        System.out.println("================================");
        System.out.println("1. Register Booking");
        System.out.println("2. Call Next Guest");
        System.out.println("3. View Waiting Queue");
        System.out.println("4. View Rooms");
        System.out.println("5. Guest Waiting Time Report");
        System.out.println("6. Queue Priority Report");
        System.out.println("7. return to main menu");
        System.out.println("================================");
    }


    private void registerBooking() {
        System.out.println("\n===== REGISTER WALK-IN =====");
        System.out.print("Enter IC Number (Enter -1 to exit): ");
        
        String icNumber =scanner.nextLine();

        if (icNumber.equals("-1")) {
            System.out.println("Registration cancelled.");
            return;
        }

        registerBookingWithCheckedIc(icNumber);
    }

    public void registerBookingWithCheckedIc(String icNumber) {

        Member member =controller.findMemberByIC(icNumber);

        if (member != null && isLoyaltyTier(member.getMembershipType())) {

            System.out.println("\nThis IC belongs to a loyalty member.");
            System.out.println("Redirecting to VIP & Loyalty Tier Priority Room Allocation...");
            if (vipRoomAllocationUI != null) {
                vipRoomAllocationUI.addRequestFromCheckedMember(member);
            }
            return;
        }

        System.out.println("\n===== GUEST REQUEST =====");
        System.out.println("IC Number is not registered as VIP/member.");
        System.out.print("Enter Guest Name (Enter -1 to exit): ");

        String guestName = scanner.nextLine();

        if (guestName.equals("-1")) {
            System.out.println("Registration cancelled.");
            return;
        }

        System.out.print("\nEnter Room Type ((Penthouse/Deluxe/Suite)or -1 to exit): ");

        String roomType =scanner.nextLine();

        if (roomType.equals("-1")) {
            System.out.println("Registration cancelled.");
            return;
        }

        int numberOfNights = inputNumberOfNights();
        if (numberOfNights == -1) {
            return;
        }

        controller.registerBooking(icNumber,roomType,numberOfNights,guestName);
    }

    private int inputNumberOfNights() {

        while (true) {

            System.out.print("Enter Number of Nights (Enter -1 to exit): ");

            String input = scanner.nextLine();

            if (input.equals("-1")) {
                System.out.println("Registration cancelled.");
                return -1;
            }

            try {
                int numberOfNights =Integer.parseInt(input);

                if (numberOfNights <= 0) {
                    System.out.println("Number of nights must be greater than 0.");
                    continue;
                }

                return numberOfNights;

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }


    private void callNextGuest() {

        System.out.println("\n===== CALL NEXT GUEST =====");

        Booking booking =controller.peekNextEligibleBooking();

        if (booking == null) {

            System.out.println("There are no guests waiting.");

            return;
        }

        System.out.println("\nGuest Information");
        System.out.println("Waiting Number : "+ booking.getWaitingNumber());
        System.out.println("Guest          : "+ booking.getGuestDisplayName());
        System.out.println("Type           : "+ booking.getMembershipType());
        System.out.println("Room Type      : "+ booking.getRoomType());
        System.out.println("Number of Nights : "+ booking.getNumberOfNights());

        Room[] availableRooms = controller.getAvailableRooms(booking.getRoomType());

        if (availableRooms.length == 0) {

            System.out.println("\nNo assignable rooms are currently available.");

            return;
        }

        System.out.println("\n===== AVAILABLE ROOMS =====");
        System.out.printf("%-10s %-15s %-25s%n",
            "Room ID",
            "Room Type",
            "Status"
        );
        System.out.println("------------------------------------------------");

        for (Room room : availableRooms) {
            
            System.out.printf("%-10s %-15s %-25s%n",
                room.getRoomNumber(),
                room.getRoomType(),
                room.getStatus()
            );
        }

        System.out.println("------------------------------------------------");

        System.out.print("Enter Room ID to assign (Enter -1 to cancel): ");

        String roomId =
            scanner.nextLine();

    if (roomId.equals("-1")) {

        System.out.println("Room assignment cancelled.");

        return;
    }

    controller.callNextGuest(
            roomId
    );
}

    private void viewWaitingQueue() {

        controller.displayBookingQueue();
    }

    private void viewRooms() {

        Room[] rooms = controller.getAllRooms();
        System.out.println("\n===== ROOM LIST =====");
        System.out.println("Room ID | Room Type | Status");
        System.out.println("----------------------------------------");
        for (Room room : rooms) {
            if (room != null) {
                System.out.println(room);
            }
        }
    }

    private void viewWaitingTimeReport() {

        controller.generateWaitingTimeReport(
                controller.getBookingHistory()
        );
    }

    private void viewQueuePriorityReport() {

        controller.generateQueuePriorityReport(
                controller.getBookingHistory()
        );
    }

    private void logout() {

        loggedIn = false;

        System.out.println("\nYou have logged out of the system.");
    }

    private int getIntegerInput(
            String message) {

        while (true) {

            System.out.print(message);

            try {

                return Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {

                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private boolean isLoyaltyTier(String tier) {
        return tier != null
                && (tier.equalsIgnoreCase("Platinum")
                || tier.equalsIgnoreCase("Diamond")
                || tier.equalsIgnoreCase("Elite"));
    }
  
}
