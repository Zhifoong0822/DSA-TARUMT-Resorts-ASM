// Author: [Chew Zhi Foong]
package control;

import adt.CustomHashMap;
import adt.MapInterface;
import Dao.GuestDAO;
import Dao.RoomDAO;
import entity.GuestProfile;
import entity.Room;
import entity.Booking;

public class FrontDeskController {

    // HIGHLIGHT YELLOW IN REPORT
    private MapInterface<String, GuestProfile> guestMap;
    private GuestDAO guestDAO;
    private MapInterface<String, Room> roomMap;
    private RoomDAO roomDAO;
    private RegisterInterfaceController bookingController;

    public FrontDeskController() {
        this.guestDAO = new GuestDAO();
        this.guestMap = guestDAO.loadGuests();
        this.roomDAO = new RoomDAO();
        this.roomMap = roomDAO.loadRooms();
    }

    public FrontDeskController(MapInterface<String, Room> roomMap) {
        this.guestDAO = new GuestDAO();
        this.guestMap = guestDAO.loadGuests();
        this.roomMap = roomMap;
    }

    public FrontDeskController(MapInterface<String, Room> roomMap,
            RegisterInterfaceController bookingController) {
        this(roomMap);
        this.bookingController = bookingController;
    }

    public GuestProfile findGuestByConfirmation(String confirmationNum) {
        return guestMap.get(confirmationNum);
    }

    public Booking findWalkInBookingByConfirmation(String confirmationNumber) {
        if (bookingController == null) {
            return null;
        }
        return bookingController.findBookingByConfirmation(confirmationNumber);
    }

    public Room[] getAvailableRoomsByType(String roomType) {
        Room[] allRooms = roomMap.values(new Room[roomMap.size()]);
        int count = 0;

        for (Room room : allRooms) {
            if (room != null && room.getRoomType().equalsIgnoreCase(roomType)
                    && room.isAvailable()) {
                count++;
            }
        }

        Room[] availableRooms = new Room[count];
        int index = 0;
        for (Room room : allRooms) {
            if (room != null && room.getRoomType().equalsIgnoreCase(roomType)
                    && room.isAvailable()) {
                availableRooms[index++] = room;
            }
        }

        return availableRooms;
    }

    // ==========================================
    // REPORT 1: High Outstanding Bills
    // (Filters by min bill, Sorts by Bill DESC)
    // ==========================================
    public GuestProfile[] getHighOutstandingBillsReport(double minBill, String roomType) {
        // HIGHLIGHT YELLOW IN REPORT
        GuestProfile[] allGuests = guestMap.values(new GuestProfile[guestMap.size()]);

        // 1. Count matching profiles for array sizing
        int count = 0;
        for (GuestProfile g : allGuests) {
            if (g != null && g.getCurrentBilling() >= minBill
                    && (roomType.equalsIgnoreCase("All")
                    || g.getRoomType().equalsIgnoreCase(roomType))) {
                count++;
            }
        }

        // 2. Filter matching profiles
        GuestProfile[] filtered = new GuestProfile[count];
        int index = 0;
        for (GuestProfile g : allGuests) {
            if (g != null && g.getCurrentBilling() >= minBill
                    && (roomType.equalsIgnoreCase("All")
                    || g.getRoomType().equalsIgnoreCase(roomType))) {
                filtered[index++] = g;
            }
        }

        // 3. Custom Insertion Sort (Descending by Billing Amount)
        sortByBillingDescending(filtered);

        return filtered;
    }

    // ==========================================
    // REPORT 2: Guest Roster by Room Category
    // (Filters by Room Type, Checked-In Status and Minimum Bill, Sorts by Name ASC)
    // ==========================================
    public GuestProfile[] getGuestsByRoomTypeReport(String roomType, double minBill) {
        // HIGHLIGHT YELLOW IN REPORT
        GuestProfile[] allGuests = guestMap.values(new GuestProfile[guestMap.size()]);

        // 1. Count matching room profiles
        int count = 0;
        for (GuestProfile g : allGuests) {
            if (g != null && g.getRoomType().equalsIgnoreCase(roomType)
                    && g.isCurrentlyStaying()
                    && g.getCurrentBilling() >= minBill) {
                count++;
            }
        }

        // 2. Filter matching room profiles
        GuestProfile[] filtered = new GuestProfile[count];
        int index = 0;
        for (GuestProfile g : allGuests) {
            if (g != null && g.getRoomType().equalsIgnoreCase(roomType)
                    && g.isCurrentlyStaying()
                    && g.getCurrentBilling() >= minBill) {
                filtered[index++] = g;
            }
        }

        // 3. Custom Insertion Sort (Ascending Alphabetical by Guest Name)
        sortByNameAscending(filtered);

        return filtered;
    }

    // ==========================================
    // CUSTOM SORTING ALGORITHMS (No JCF Allowed)
    // ==========================================
    
    // Sorts double values in descending order
    private void sortByBillingDescending(GuestProfile[] array) {
        int n = array.length;
        for (int i = 1; i < n; i++) {
            GuestProfile key = array[i];
            int j = i - 1;

            while (j >= 0 && array[j].getCurrentBilling() < key.getCurrentBilling()) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;
        }
    }

    // Sorts String values in ascending alphabetical order
    private void sortByNameAscending(GuestProfile[] array) {
        int n = array.length;
        for (int i = 1; i < n; i++) {
            GuestProfile key = array[i];
            int j = i - 1;

            while (j >= 0 && array[j].getGuestName().compareToIgnoreCase(key.getGuestName()) > 0) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;
        }
    }
}
