package walkinregistrationbooking;

/**
 *
 * @author Yu He
 */
public class Room {

    private String roomId;
    private String roomType;
    private String status;

    public Room(
            String roomId,
            String roomType,
            String status) {

        this.roomId = roomId;
        this.roomType = roomType;
        this.status = status;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {

        return roomId
                + " | "
                + roomType
                + " | "
                + status;
    }
}