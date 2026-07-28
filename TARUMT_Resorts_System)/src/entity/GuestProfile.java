// Author: Your Name
package entity;

public class GuestProfile {
    private String confirmationNumber; // 8-digit unique ID
    private String guestName;
    private String roomType;
    private double currentBilling;

    public GuestProfile(String confirmationNumber, String guestName, String roomType, double currentBilling) {
        this.confirmationNumber = confirmationNumber;
        this.guestName = guestName;
        this.roomType = roomType;
        this.currentBilling = currentBilling;
    }

    // Getters and Setters
    public String getConfirmationNumber() { return confirmationNumber; }
    public String getGuestName() { return guestName; }

    public String getRoomType() {
        return roomType;
    }

    public double getCurrentBilling() {
        return currentBilling;
    }
    
    @Override
    public String toString() {
        return "Guest: " + guestName + " | Room: " + roomType + " | Bill: RM" + currentBilling;
    }
}

