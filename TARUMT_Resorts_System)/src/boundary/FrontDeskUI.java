// Author: [Your Name]
package boundary;

import control.FrontDeskController;
import entity.GuestProfile;
import java.util.Scanner;

public class FrontDeskUI {

    private FrontDeskController controller = new FrontDeskController();
    private Scanner scanner = new Scanner(System.in);

    public void startMenu() {
        int choice = -1;
        do {
            System.out.println("\n================================================");
            System.out.println("     TARUMT RESORTS - FRONT DESK SYSTEM        ");
            System.out.println("================================================");
            System.out.println("1. Search Guest by 8-Digit Confirmation Number");
            System.out.println("2. Generate Report: High Outstanding Bills");
            System.out.println("3. Generate Report: Occupancy Roster by Room Type");
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
                    handleBillingReport();
                    break;
                case 3:
                    handleRoomRosterReport();
                    break;
                case 0:
                    System.out.println("\nExiting Front Desk System. Goodbye!");
                    break;
                default:
                    System.out.println("\n[Error] Invalid choice! Please enter a number from 0 to 3.");
            }
        } while (choice != 0);
    }

    private void handleGuestSearch() {
        System.out.println("\n--- GUEST INSTANT SEARCH ---");
        System.out.print("Enter 8-digit confirmation number: ");
        String confNum = scanner.nextLine().trim();

        GuestProfile guest = controller.findGuestByConfirmation(confNum);

        if (guest != null) {
            System.out.println("\n[MATCH FOUND]");
            printTableHeader();
            printTableRow(guest);
            printTableFooter();
        } else {
            System.out.println("\n[NOT FOUND] No record associated with ID: " + confNum);
        }
    }

    private void handleBillingReport() {
        System.out.println("\n--- REPORT: HIGH OUTSTANDING BILLS ---");
        System.out.print("Enter minimum billing threshold (RM): ");
        double threshold = 0;
        try {
            threshold = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[Warning] Invalid amount entered. Defaulting to RM 0.00");
        }

        GuestProfile[] reportData = controller.getHighOutstandingBillsReport(threshold);

        System.out.println("\n=========================================================================");
        System.out.println("           MANAGEMENT REPORT: OUTSTANDING BILLING SUMMARY                 ");
        System.out.println("           Filter: Balance >= RM " + String.format("%.2f", threshold) + " | Sorted: Highest Bill First");
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
        }
    }

    private void handleRoomRosterReport() {
        System.out.println("\n--- REPORT: ROOM TYPE OCCUPANCY ROSTER ---");
        System.out.print("Enter room category (e.g., Deluxe, Suite, Penthouse): ");
        String roomType = scanner.nextLine().trim();

        GuestProfile[] reportData = controller.getGuestsByRoomTypeReport(roomType);

        System.out.println("\n=========================================================================");
        System.out.println("           MANAGEMENT REPORT: ROOM OCCUPANCY ROSTER                      ");
        System.out.println("           Category: " + roomType.toUpperCase() + " | Sorted: Alphabetical (A-Z)");
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
        }
    }

    private void printTableHeader() {
        System.out.println("+---------------+----------------------+---------------+-----------------+");
        System.out.printf("| %-13s | %-20s | %-13s | %-15s |\n", "Conf. Number", "Guest Name", "Room Type", "Bill Amount(RM)");
        System.out.println("+---------------+----------------------+---------------+-----------------+");
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

    public static void main(String[] args) {
        new FrontDeskUI().startMenu();
    }
}