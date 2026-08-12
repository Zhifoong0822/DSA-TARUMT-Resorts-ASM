/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java 
 */
import boundary.FrontDeskUI;
import boundary.HousekeepingUI;
import boundary.VipRoomAllocationUI;
import control.HousekeepingController;
import Dao.RoomDAO;
import adt.MapInterface;
import entity.Room;
import java.util.Scanner;
import walkinregistrationbooking.MemberDao;
import walkinregistrationbooking.RegisterBookingUI;
import walkinregistrationbooking.RegisterInterfaceController;
/**
 *
 * @author Gigabyte
 */
public class TARUMTResortsSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MapInterface<String, Room> roomMap = new RoomDAO().loadRooms();
        HousekeepingController housekeepingController = new HousekeepingController(roomMap);
        FrontDeskUI frontDeskUI = new FrontDeskUI(housekeepingController, roomMap, scanner);
        HousekeepingUI housekeepingUI = new HousekeepingUI(housekeepingController, scanner);
        VipRoomAllocationUI vipRoomAllocationUI = new VipRoomAllocationUI(roomMap, scanner);
        walkinregistrationbooking.RoomDao bookingRoomDAO = new walkinregistrationbooking.RoomDao();
        RegisterInterfaceController registerController =
                new RegisterInterfaceController(new MemberDao(), bookingRoomDAO);
        RegisterBookingUI registerBookingUI = new RegisterBookingUI(registerController, bookingRoomDAO);
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
                    vipRoomAllocationUI.startMenu();
                    break;

                case 4:
                    registerBookingUI.start();
                    break;

                case 0:
                    System.out.println("\nThank you for using TARUMT Resorts System. Goodbye!");
                    break;

                default:
                    System.out.println("\nInvalid selection. Please enter a number from 0 to 4.");
            }
        } while (choice != 0);
    }

    private static void displayMainMenu() {
        System.out.println("\n==================================================");
        System.out.println("          TARUMT RESORTS MANAGEMENT SYSTEM");
        System.out.println("==================================================");
        System.out.println("1. Front-Desk Service");
        System.out.println("2. Housekeeping and Task Log");
        System.out.println("3. VIP & Loyalty Tier Priority Room Allocation");
        System.out.println("4. Walk-In Registration & Standard Booking");
        System.out.println("0. Exit");
        System.out.println("==================================================");
    }
}
