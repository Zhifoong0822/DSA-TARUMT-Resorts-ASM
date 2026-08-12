// Author: Yong Shen
package entity;

public class LoyaltyRoomRequest implements Comparable<LoyaltyRoomRequest> {

    private String requestId;
    private String guestName;
    private String loyaltyTier;
    private String roomType;
    private int stayNights;
    private double totalSpending;
    private int bookingOrder;
    private String allocatedRoomNo;

    public LoyaltyRoomRequest() {
    }

    public LoyaltyRoomRequest(String requestId, String guestName, String loyaltyTier,
            String roomType, int stayNights, double totalSpending, int bookingOrder) {
        this.requestId = requestId;
        this.guestName = guestName;
        this.loyaltyTier = loyaltyTier;
        this.roomType = roomType;
        this.stayNights = stayNights;
        this.totalSpending = totalSpending;
        this.bookingOrder = bookingOrder;
        this.allocatedRoomNo = "-";
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getStayNights() {
        return stayNights;
    }

    public void setStayNights(int stayNights) {
        this.stayNights = stayNights;
    }

    public double getTotalSpending() {
        return totalSpending;
    }

    public void setTotalSpending(double totalSpending) {
        this.totalSpending = totalSpending;
    }

    public int getBookingOrder() {
        return bookingOrder;
    }

    public void setBookingOrder(int bookingOrder) {
        this.bookingOrder = bookingOrder;
    }

    public String getAllocatedRoomNo() {
        return allocatedRoomNo;
    }

    public void setAllocatedRoomNo(String allocatedRoomNo) {
        this.allocatedRoomNo = allocatedRoomNo;
    }

    public int getTierRank() {
        if (loyaltyTier == null) {
            return 0;
        }
        if (loyaltyTier.equalsIgnoreCase("Diamond") || loyaltyTier.equalsIgnoreCase("VIP")) {
            return 5;
        }
        if (loyaltyTier.equalsIgnoreCase("Platinum")) {
            return 4;
        }
        if (loyaltyTier.equalsIgnoreCase("Gold")) {
            return 3;
        }
        if (loyaltyTier.equalsIgnoreCase("Silver") || loyaltyTier.equalsIgnoreCase("NORMAL")) {
            return 2;
        }
        return 1;
    }

    public int getPriorityScore() {
        return (getTierRank() * 10000) + ((int) totalSpending / 10) + (stayNights * 80) - bookingOrder;
    }

    @Override
    public int compareTo(LoyaltyRoomRequest other) {
        if (getTierRank() != other.getTierRank()) {
            return getTierRank() - other.getTierRank();
        }
        if (totalSpending > other.totalSpending) {
            return 1;
        }
        if (totalSpending < other.totalSpending) {
            return -1;
        }
        if (stayNights != other.stayNights) {
            return stayNights - other.stayNights;
        }
        return other.bookingOrder - bookingOrder;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LoyaltyRoomRequest other = (LoyaltyRoomRequest) obj;
        return requestId != null && requestId.equalsIgnoreCase(other.requestId);
    }

    @Override
    public String toString() {
        return String.format("%-8s %-18s %-10s %-12s %-6d RM %-10.2f %-8d %-8s",
                requestId, guestName, loyaltyTier, roomType, stayNights, totalSpending,
                getPriorityScore(), allocatedRoomNo);
    }
}
