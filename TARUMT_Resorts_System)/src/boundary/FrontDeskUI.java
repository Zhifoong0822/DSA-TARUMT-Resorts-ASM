// Author: Your Name
package boundary;

import control.FrontDeskController;
import entity.GuestProfile;
import java.util.Scanner;

public class FrontDeskUI {
    private FrontDeskController controller = new FrontDeskController();
    private Scanner scanner = new Scanner(System.in);

    public void startMenu() {
        System.out.println("=== TARUMT Resorts Front-Desk Service ===");
        System.out.print("Enter 8-digit confirmation number to search: ");
        String searchInput = scanner.nextLine();

        GuestProfile result = controller.findGuestByConfirmation(searchInput);

        if (result != null) {
            System.out.println("\n[Match Found]");
            System.out.println(result);
        } else {
            System.out.println("\n[Error] No guest profile found for ID: " + searchInput);
        }
    }

    public static void main(String[] args) {
        new FrontDeskUI().startMenu();
    }
}