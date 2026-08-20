// Author: Tan Yong Shen
package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoyaltyRoomRequest implements Comparable<LoyaltyRoomRequest> {

    private String requestId;
    private String guestName;
    private String loyaltyTier;
    private String roomType;
    private int stayNights;
    private double totalSpending;
    private int bookingOrder;
    private String allocatedRoomNo;
    private LocalDateTime registrationTime;
    private LocalDateTime roomAssignmentTime;

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
        this.registrationTime = LocalDateTime.now();
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

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getFormattedRegistrationTime() {
        if (registrationTime == null) {
            return "-";
        }
        return registrationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public LocalDateTime getRoomAssignmentTime() {
        return roomAssignmentTime;
    }

    public void setRoomAssignmentTime(LocalDateTime roomAssignmentTime) {
        this.roomAssignmentTime = roomAssignmentTime;
    }

    public String getFormattedRoomAssignmentTime() {
        if (roomAssignmentTime == null) {
            return "-";
        }
        return roomAssignmentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public int getTierRank() {
        if (loyaltyTier == null) {
            return 0;
        }
        if (loyaltyTier.equalsIgnoreCase("Platinum")) {
            return 3;
        }
        if (loyaltyTier.equalsIgnoreCase("Diamond")) {
            return 2;
        }
        if (loyaltyTier.equalsIgnoreCase("Elite")) {
            return 1;
        }
        return 0;
    }

    public int getPriorityScore() {
        return (getTierRank() * 10000) - bookingOrder;
    }

    @Override
    public int compareTo(LoyaltyRoomRequest other) {
        if (getPriorityScore() != other.getPriorityScore()) {
            return getPriorityScore() - other.getPriorityScore();
        }
        return requestId.compareToIgnoreCase(other.requestId);
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
