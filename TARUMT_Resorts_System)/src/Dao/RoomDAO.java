// Author: Chew Zhi Foong
package Dao;

import adt.CustomHashMap;
import adt.MapInterface;
import entity.Room;

public class RoomDAO {

    public MapInterface<String, Room> loadRooms() {
        MapInterface<String, Room> map = new CustomHashMap<>();

        Room r1 = new Room("D101", "Deluxe", "Occupied");
        Room r2 = new Room("D102", "Deluxe", "Cleaning In Progress");
        Room r3 = new Room("D103", "Deluxe", "Ready for Check-In");
        Room r4 = new Room("S201", "Suite", "Occupied");
        Room r5 = new Room("S202", "Suite", "Ready for Check-In");
        Room r6 = new Room("P301", "Penthouse", "Occupied");
        Room r7 = new Room("P302", "Penthouse", "Ready for Check-In");
        Room r8 = new Room("D104", "Deluxe", "Cleaning In Progress");
        Room r9 = new Room("D105", "Deluxe", "Occupied");
        Room r10 = new Room("S203", "Suite", "Occupied");
        Room r11 = new Room("S204", "Suite", "Cleaning In Progress");
        Room r12 = new Room("P303", "Penthouse", "Occupied");
        Room r13 = new Room("D106", "Deluxe", "Occupied");

        map.put(r1.getRoomNumber(), r1);
        map.put(r2.getRoomNumber(), r2);
        map.put(r3.getRoomNumber(), r3);
        map.put(r4.getRoomNumber(), r4);
        map.put(r5.getRoomNumber(), r5);
        map.put(r6.getRoomNumber(), r6);
        map.put(r7.getRoomNumber(), r7);
        map.put(r8.getRoomNumber(), r8);
        map.put(r9.getRoomNumber(), r9);
        map.put(r10.getRoomNumber(), r10);
        map.put(r11.getRoomNumber(), r11);
        map.put(r12.getRoomNumber(), r12);
        map.put(r13.getRoomNumber(), r13);

        return map;
    }
}
