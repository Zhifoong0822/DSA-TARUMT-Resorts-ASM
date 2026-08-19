/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java 
 */
import boundary.FrontDeskUI;
import boundary.HousekeepingUI;
import boundary.VipRoomAllocationUI;
import control.HousekeepingController;
import control.VipRoomAllocationController;
import Dao.RoomDAO;
import adt.MapInterface;
import entity.Member;
import entity.Room;
import java.util.Scanner;
import Dao.MemberDao;
import boundary.RegisterBookingUI;
import control.RegisterInterfaceController;
/**
 *
 * @author Gigabyte
 */
public class TARUMTResortsSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MapInterface<String, Room> roomMap = new RoomDAO().loadRooms();
        MemberDao memberDAO = new MemberDao();
        HousekeepingController housekeepingController = new HousekeepingController(roomMap);
        HousekeepingUI housekeepingUI = new HousekeepingUI(housekeepingController, scanner);
        VipRoomAllocationController vipController = new VipRoomAllocationController(roomMap);
        RegisterInterfaceController registerController =
                new RegisterInterfaceController(memberDAO, roomMap, housekeepingController,
                        vipController);
        VipRoomAllocationUI vipRoomAllocationUI =
                new VipRoomAllocationUI(vipController, registerController, scanner);
        FrontDeskUI frontDeskUI = new FrontDeskUI(housekeepingController, roomMap, scanner,
                registerController);
        RegisterBookingUI registerBookingUI = new RegisterBookingUI(registerController, roomMap, scanner,vipRoomAllocationUI);
        vipRoomAllocationUI.setRegisterBookingUI(registerBookingUI);
        int choice = -1;

        do {
            displayMainMenu();
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                    frontDeskUI.startMenu();
                    break;

                case 2:
                    housekeepingUI.run();
                    break;

                case 3:
                    checkIcAndRedirect(scanner, memberDAO, vipRoomAllocationUI, registerBookingUI);
                    break;

                case 4:
                    vipRoomAllocationUI.startMenu();
                    break;

                case 5:
                    registerBookingUI.start();
                    break;

                case 0:
                    System.out.println("\nThank you for using TARUMT Resorts System. Goodbye!");
                    break;

                default:
                    System.out.println("\nInvalid selection. Please enter a number from 0 to 5.");
            }
        } while (choice != 0);
    }

    private static void checkIcAndRedirect(Scanner scanner, MemberDao memberDAO,
            VipRoomAllocationUI vipRoomAllocationUI,
            RegisterBookingUI registerBookingUI) {

        System.out.println("\n===== CHECK IC / PASSPORT =====");
        System.out.print("Enter IC/Passport: ");
        String icNumber = scanner.nextLine().trim();

        Member member = memberDAO.findMemberByIC(icNumber);

        if (member != null && isLoyaltyTier(member.getMembershipType())) {
            System.out.println("Member found: " + member.getMemberName()
                    + " (" + member.getMembershipType() + ")");
            System.out.print("Redirect to VIP & Loyalty module? (Y/N): ");
            String choice = scanner.nextLine().trim();

            if (choice.equalsIgnoreCase("Y")) {
                vipRoomAllocationUI.addRequestFromCheckedMember(member);
            }
        } else {
            System.out.println("This IC is not a VIP/member.");
            System.out.print("Redirect to Walk-In Registration? (Y/N): ");
            String choice = scanner.nextLine().trim();

            if (choice.equalsIgnoreCase("Y")) {
                registerBookingUI.registerBookingWithCheckedIc(icNumber);
            }
        }
    }

    private static boolean isLoyaltyTier(String tier) {
        return tier != null
                && (tier.equalsIgnoreCase("Platinum")
                || tier.equalsIgnoreCase("Diamond")
                || tier.equalsIgnoreCase("Elite"));
    }

    private static void displayMainMenu() {
        System.out.println("\n==================================================");
        System.out.println("          TARUMT RESORTS MANAGEMENT SYSTEM");
        System.out.println("==================================================");
        System.out.println("1. Front-Desk Service");
        System.out.println("2. Housekeeping and Task Log");
        System.out.println("3. Check IC / Passport for Booking");
        System.out.println("4. VIP & Loyalty Tier Priority Room Allocation");
        System.out.println("5. Walk-In Registration & Standard Booking");
        System.out.println("0. Exit");
        System.out.println("==================================================");
    }
}
