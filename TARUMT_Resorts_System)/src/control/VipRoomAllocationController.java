// Author: Yong Shen
package control;

import Dao.RoomDAO;
import Dao.VipRoomAllocationDAO;
import adt.MapInterface;
import adt.PriorityBinarySearchTreeInterface;
import entity.LoyaltyRoomRequest;
import entity.Room;

public class VipRoomAllocationController {

    private PriorityBinarySearchTreeInterface<LoyaltyRoomRequest> waitingRequests;
    private LoyaltyRoomRequest[] allocatedRequests;
    private int allocatedCount;
    private Room[] rooms;
    private int nextBookingOrder;
    private int nextRequestNumber;

    public VipRoomAllocationController() {
        this(new RoomDAO().loadRooms());
    }

    public VipRoomAllocationController(MapInterface<String, Room> roomMap) {
        VipRoomAllocationDAO dao = new VipRoomAllocationDAO();
        waitingRequests = dao.loadWaitingRequests();
        rooms = roomMap.values(new Room[roomMap.size()]);
        allocatedRequests = new LoyaltyRoomRequest[20];
        allocatedCount = 0;
        nextBookingOrder = waitingRequests.getNumberOfEntries() + 1;
        nextRequestNumber = 1001 + waitingRequests.getNumberOfEntries();
    }

    public LoyaltyRoomRequest addRequest(String guestName, String loyaltyTier,
            String roomType, int stayNights, double totalSpending) {
        String requestId = generateRequestId();
        LoyaltyRoomRequest request = new LoyaltyRoomRequest(requestId, guestName, loyaltyTier,
                roomType, stayNights, totalSpending, nextBookingOrder);

        waitingRequests.add(request);
        nextBookingOrder++;
        return request;
    }

    public boolean hasWaitingRequests() {
        return !waitingRequests.isEmpty();
    }

    public LoyaltyRoomRequest peekNextRequest() {
        return waitingRequests.getTop();
    }

    public LoyaltyRoomRequest removeNextRequest() {
        return waitingRequests.removeTop();
    }

    public LoyaltyRoomRequest peekNextRequestWithAvailableRoom() {
        LoyaltyRoomRequest[] requests = waitingRequests.toArray(
                new LoyaltyRoomRequest[waitingRequests.getNumberOfEntries()]);

        for (LoyaltyRoomRequest request : requests) {
            if (findAvailableRoom(request.getRoomType(), request.getTierRank()) != null) {
                return request;
            }
        }
        return null;
    }

    /** Removes the highest-priority request for this room type. */
    public LoyaltyRoomRequest removeNextRequestForRoomType(String roomType) {
        LoyaltyRoomRequest[] skipped = new LoyaltyRoomRequest[waitingRequests.getNumberOfEntries()];
        int skippedCount = 0;
        LoyaltyRoomRequest selected = null;

        while (!waitingRequests.isEmpty()) {
            LoyaltyRoomRequest request = waitingRequests.removeTop();
            if (selected == null && request.getRoomType().equalsIgnoreCase(roomType)) {
                selected = request;
                break;
            }
            skipped[skippedCount++] = request;
        }

        for (int i = 0; i < skippedCount; i++) {
            waitingRequests.add(skipped[i]);
        }
        return selected;
    }

    public void saveAllocatedRequest(LoyaltyRoomRequest request, String roomNumber) {
        if (request == null) {
            return;
        }
        request.setAllocatedRoomNo(roomNumber);
        addAllocatedRequest(request);
    }

    public LoyaltyRoomRequest allocateNextRoom() {
        if (waitingRequests.isEmpty()) {
            return null;
        }

        LoyaltyRoomRequest[] skipped = new LoyaltyRoomRequest[waitingRequests.getNumberOfEntries()];
        int skippedCount = 0;
        LoyaltyRoomRequest allocated = null;

        while (!waitingRequests.isEmpty() && allocated == null) {
            LoyaltyRoomRequest current = waitingRequests.removeTop();
            Room room = findAvailableRoom(current.getRoomType(), current.getTierRank());

            if (room != null) {
                room.setStatus("Occupied");
                current.setAllocatedRoomNo(room.getRoomNumber());
                addAllocatedRequest(current);
                allocated = current;
            } else {
                skipped[skippedCount] = current;
                skippedCount++;
            }
        }

        for (int i = 0; i < skippedCount; i++) {
            waitingRequests.add(skipped[i]);
        }

        return allocated;
    }

    public int allocateAllPossibleRooms() {
        int count = 0;
        LoyaltyRoomRequest allocated;

        do {
            allocated = allocateNextRoom();
            if (allocated != null) {
                count++;
            }
        } while (allocated != null);

        return count;
    }

    public LoyaltyRoomRequest findRequestById(String requestId) {
        LoyaltyRoomRequest allocated = findAllocatedRequest(requestId);
        if (allocated != null) {
            return allocated;
        }

        LoyaltyRoomRequest[] waiting = waitingRequests.toArray(new LoyaltyRoomRequest[waitingRequests.getNumberOfEntries()]);
        for (int i = 0; i < waiting.length; i++) {
            if (waiting[i] != null && waiting[i].getRequestId().equalsIgnoreCase(requestId)) {
                return waiting[i];
            }
        }
        return null;
    }

    public LoyaltyRoomRequest[] getWaitingPriorityReport(String roomType, String minimumTier) {
        LoyaltyRoomRequest[] allWaiting = waitingRequests.toArray(new LoyaltyRoomRequest[waitingRequests.getNumberOfEntries()]);
        int minimumRank = getTierRank(minimumTier);
        int count = 0;

        for (int i = 0; i < allWaiting.length; i++) {
            if (matchRoomType(allWaiting[i].getRoomType(), roomType)
                    && allWaiting[i].getTierRank() >= minimumRank) {
                count++;
            }
        }

        LoyaltyRoomRequest[] report = new LoyaltyRoomRequest[count];
        int index = 0;
        for (int i = 0; i < allWaiting.length; i++) {
            if (matchRoomType(allWaiting[i].getRoomType(), roomType)
                    && allWaiting[i].getTierRank() >= minimumRank) {
                report[index] = allWaiting[i];
                index++;
            }
        }

        sortByPriority(report);
        return report;
    }

    public LoyaltyRoomRequest[] getAllocationSummaryReport(String roomType, int minimumNights) {
        int count = 0;
        for (int i = 0; i < allocatedCount; i++) {
            if (matchRoomType(allocatedRequests[i].getRoomType(), roomType)
                    && allocatedRequests[i].getStayNights() >= minimumNights) {
                count++;
            }
        }

        LoyaltyRoomRequest[] report = new LoyaltyRoomRequest[count];
        int index = 0;
        for (int i = 0; i < allocatedCount; i++) {
            if (matchRoomType(allocatedRequests[i].getRoomType(), roomType)
                    && allocatedRequests[i].getStayNights() >= minimumNights) {
                report[index] = allocatedRequests[i];
                index++;
            }
        }

        sortByTierThenSpending(report);
        return report;
    }

    public Room[] getRooms() {
        return rooms;
    }

    public double calculateTotalSpending(LoyaltyRoomRequest[] requests) {
        double total = 0;
        for (int i = 0; i < requests.length; i++) {
            total += requests[i].getTotalSpending();
        }
        return total;
    }

    public double calculateEstimatedSpending(String roomType, int stayNights) {
        double price = 300;

        if (roomType.equalsIgnoreCase("Suite")) {
            price = 800;
        } else if (roomType.equalsIgnoreCase("Penthouse")) {
            price = 1200;
        }

        return price * stayNights;
    }

    private Room findAvailableRoom(String roomType, int tierRank) {
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i].isAvailable() && rooms[i].getRoomType().equalsIgnoreCase(roomType)) {
                return rooms[i];
            }
        }
        return null;
    }

    private void addAllocatedRequest(LoyaltyRoomRequest request) {
        if (allocatedCount == allocatedRequests.length) {
            doubleAllocatedArray();
        }
        allocatedRequests[allocatedCount] = request;
        allocatedCount++;
    }

    private LoyaltyRoomRequest findAllocatedRequest(String requestId) {
        for (int i = 0; i < allocatedCount; i++) {
            if (allocatedRequests[i].getRequestId().equalsIgnoreCase(requestId)) {
                return allocatedRequests[i];
            }
        }
        return null;
    }

    private boolean matchRoomType(String requestRoomType, String filterRoomType) {
        return filterRoomType == null || filterRoomType.equalsIgnoreCase("All")
                || filterRoomType.length() == 0 || requestRoomType.equalsIgnoreCase(filterRoomType);
    }

    private int getTierRank(String tier) {
        LoyaltyRoomRequest temp = new LoyaltyRoomRequest("TEMP", "TEMP", tier, "TEMP", 0, 0, 0);
        return temp.getTierRank();
    }

    private String generateRequestId() {
        String requestId = String.format("RQ%04d", nextRequestNumber);
        nextRequestNumber++;
        return requestId;
    }

    private void sortByPriority(LoyaltyRoomRequest[] array) {
        for (int i = 1; i < array.length; i++) {
            LoyaltyRoomRequest key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j].compareTo(key) < 0) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }

    private void sortByTierThenSpending(LoyaltyRoomRequest[] array) {
        for (int i = 1; i < array.length; i++) {
            LoyaltyRoomRequest key = array[i];
            int j = i - 1;
            while (j >= 0 && compareTierSpending(array[j], key) < 0) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }

    private int compareTierSpending(LoyaltyRoomRequest first, LoyaltyRoomRequest second) {
        if (first.getTierRank() != second.getTierRank()) {
            return first.getTierRank() - second.getTierRank();
        }
        if (first.getTotalSpending() > second.getTotalSpending()) {
            return 1;
        }
        if (first.getTotalSpending() < second.getTotalSpending()) {
            return -1;
        }
        return second.getBookingOrder() - first.getBookingOrder();
    }

    private void doubleAllocatedArray() {
        LoyaltyRoomRequest[] oldArray = allocatedRequests;
        allocatedRequests = new LoyaltyRoomRequest[oldArray.length * 2];
        for (int i = 0; i < oldArray.length; i++) {
            allocatedRequests[i] = oldArray[i];
        }
    }
}
