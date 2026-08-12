// Author: Yong Shen
package Dao;

import adt.PriorityBinarySearchTree;
import adt.PriorityBinarySearchTreeInterface;
import entity.LoyaltyRoomRequest;

public class VipRoomAllocationDAO {

    public PriorityBinarySearchTreeInterface<LoyaltyRoomRequest> loadWaitingRequests() {
        PriorityBinarySearchTreeInterface<LoyaltyRoomRequest> tree = new PriorityBinarySearchTree<>();

        tree.add(new LoyaltyRoomRequest("RQ1001", "Alicia Wong", "VIP", "Penthouse", 3, 3600.00, 1));
        tree.add(new LoyaltyRoomRequest("RQ1002", "Bryan Lim", "NORMAL", "Deluxe", 2, 600.00, 2));
        tree.add(new LoyaltyRoomRequest("RQ1003", "Chloe Tan", "VIP", "Suite", 4, 3200.00, 3));
        tree.add(new LoyaltyRoomRequest("RQ1004", "Daniel Lee", "GUEST", "Deluxe", 1, 300.00, 4));

        return tree;
    }
}
