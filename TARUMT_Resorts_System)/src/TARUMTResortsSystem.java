/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import boundary.FrontDeskUI;
import boundary.HousekeepingUI;
import java.util.Scanner;
/**
 *
 * @author Gigabyte
 */
public class TARUMTResortsSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
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
                    new FrontDeskUI().startMenu();
                    break;

                case 2:
                    new HousekeepingUI().run();
                    break;

                case 3:
                    System.out.println("\nVIP & Loyalty Tier Priority Room Allocation is not available yet.");
                    break;

                case 4:
                    System.out.println("\nWalk-In Registration & Standard Booking is not available yet.");
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
