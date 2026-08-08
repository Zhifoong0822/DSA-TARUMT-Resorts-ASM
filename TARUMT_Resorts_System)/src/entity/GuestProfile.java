// Author: Chew Zhi Foong
package entity;

public class GuestProfile {
    private String confirmationNumber; // 8-digit unique ID
    private String guestName;
    private String roomNumber;
    private String roomType;
    private String contactNumber;
    private String stayStatus;
    private double currentBilling;

    public GuestProfile(String confirmationNumber, String guestName, String roomNumber,
            String roomType, String contactNumber, String stayStatus, double currentBilling) {
        this.confirmationNumber = confirmationNumber;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.contactNumber = contactNumber;
        this.stayStatus = stayStatus;
        this.currentBilling = currentBilling;
    }

    // Getters and Setters
    public String getConfirmationNumber() { return confirmationNumber; }
    public String getGuestName() { return guestName; }
    public String getRoomNumber() { return roomNumber; }
    public String getContactNumber() { return contactNumber; }
    public String getStayStatus() { return stayStatus; }

    public String getRoomType() {
        return roomType;
    }

    public double getCurrentBilling() {
        return currentBilling;
    }

    public boolean isCurrentlyStaying() {
        return "Checked-In".equalsIgnoreCase(stayStatus);
    }
    
    @Override
    public String toString() {
        return "Guest: " + guestName + " | Room: " + roomNumber + " (" + roomType
                + ") | Status: " + stayStatus + " | Bill: RM" + currentBilling;
    }
}

