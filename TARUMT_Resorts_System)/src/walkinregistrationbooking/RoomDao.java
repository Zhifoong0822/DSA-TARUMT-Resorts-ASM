package walkinregistrationbooking;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yu He
 */
public class RoomDao {

    private List<Room> rooms;

    public RoomDao() {

        rooms = new ArrayList<>();

        // Hardcoded rooms
        rooms.add(
                new Room(
                        "P301", "Penthouse", "Occupied"
                )
        );

        rooms.add(
                new Room(
                        "P302", "Penthouse", "Occupied"
                )
        );

        rooms.add(
                new Room(
                        "D101",
                        "Deluxe",
                        "Ready For Check-In"
                )
        );

        rooms.add(
                new Room(
                        "D102",
                        "Deluxe",
                        "Ready For Check-In"
                )
        );

        rooms.add(
                new Room(
                        "S201",
                        "Suite",
                        "Cleaning In Progress"
                )
        );
    }

    public List<Room> getAvailableRooms(String roomType) {

    List<Room> availableRooms =
            new ArrayList<>();

    for (Room room : rooms) {

        if (room.getRoomType()
                .equalsIgnoreCase(roomType)
                && room.getStatus()
                .equalsIgnoreCase(
                        "Ready For Check-In")) {

            availableRooms.add(room);
        }
    }

    return availableRooms;
}
    
    public Room findAvailableRoom(String roomType) {

        for (Room room : rooms) {

            if (room.getRoomType()
                    .equalsIgnoreCase(roomType)
                    && room.getStatus()
                    .equalsIgnoreCase(
                            "Ready For Check-In")) {

                return room;
            }
        }

        return null;
    }

    public Room findRoomById(String roomId) {

        for (Room room : rooms) {

            if (room.getRoomId()
                    .equalsIgnoreCase(roomId)) {

                return room;
            }
        }

        return null;
    }

    public List<Room> getAllRooms() {

        return rooms;
    }

    public void displayRooms() {

        System.out.println(
                "\n===== ROOM LIST ====="
        );

        System.out.println(
                "Room ID | Room Type | Status"
        );

        System.out.println(
                "----------------------------------------"
        );

        for (Room room : rooms) {

            System.out.println(room);
        }
    }
}