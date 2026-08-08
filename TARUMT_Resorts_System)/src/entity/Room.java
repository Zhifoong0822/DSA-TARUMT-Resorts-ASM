// Author: Chew Zhi Foong
package entity;

public class Room {
    private String roomNumber;
    private String roomType;
    private String status;

    public Room(String roomNumber, String roomType, String status) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = status;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return "Ready for Check-In".equalsIgnoreCase(status);
    }
}
