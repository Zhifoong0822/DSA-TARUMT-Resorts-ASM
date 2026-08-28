// Author: Yong Shen
package Dao;

import adt.PriorityBinarySearchTree;
import adt.PriorityBinarySearchTreeInterface;
import entity.LoyaltyRoomRequest;
import java.time.LocalDateTime;

public class VipRoomAllocationDAO {

    private final LocalDateTime projectStartTime = LocalDateTime.now();

    public PriorityBinarySearchTreeInterface<LoyaltyRoomRequest> loadWaitingRequests() {
        PriorityBinarySearchTreeInterface<LoyaltyRoomRequest> tree = new PriorityBinarySearchTree<>();

        tree.add(createProjectStartRequest("RQ1001", "Alicia Wong", "Platinum", "Penthouse", 3, 3600.00, 1));
        tree.add(createProjectStartRequest("RQ1002", "Bryan Lim", "Diamond", "Deluxe", 2, 600.00, 2));
        tree.add(createProjectStartRequest("RQ1003", "Chloe Tan", "Diamond", "Suite", 4, 3200.00, 3));
        tree.add(createProjectStartRequest("RQ1004", "Daniel Lee", "Elite", "Suite", 1, 800.00, 4));


        return tree;
    }

    private LoyaltyRoomRequest createProjectStartRequest(String requestId, String guestName,
            String loyaltyTier, String roomType, int stayNights, double totalSpending,
            int bookingOrder) {
        LoyaltyRoomRequest request = new LoyaltyRoomRequest(requestId, guestName, loyaltyTier,
                roomType, stayNights, totalSpending, bookingOrder);
        request.setRegistrationTime(projectStartTime);
        return request;
    }
}
