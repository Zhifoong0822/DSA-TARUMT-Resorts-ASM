// Author: Chew Zhi Foong
package Dao;
import adt.CustomHashMap;
import adt.MapInterface;
import entity.GuestProfile;

public class GuestDAO {

    // Method to load data into your custom Map ADT
    public MapInterface<String, GuestProfile> loadGuests() {
        MapInterface<String, GuestProfile> map = new CustomHashMap<>();

        // You can populate mock data here OR read from a text file (.txt)
        GuestProfile g1 = new GuestProfile("12345678", "Alex Tan", "D101", "Deluxe",
                "012-3456789", "Checked-In", 450.00);
        GuestProfile g2 = new GuestProfile("87654321", "Siti Aminah", "S201", "Suite",
                "013-9876543", "Checked-In", 1200.00);
        GuestProfile g3 = new GuestProfile("55667788", "Michael Jordan", "P301", "Penthouse",
                "014-5550123", "Checked-In", 5500.00);
        GuestProfile g4 = new GuestProfile("11223344", "Priya Kumar", "D104", "Deluxe",
                "011-2345678", "Checked-Out", 0.00);
        GuestProfile g5 = new GuestProfile("22334455", "Chan Mei Ling", "D105", "Deluxe",
                "016-3344556", "Checked-In", 875.00);
        GuestProfile g6 = new GuestProfile("99887766", "Farid Ismail", "S203", "Suite",
                "017-5566778", "Checked-In", 300.00);
        GuestProfile g7 = new GuestProfile("44556677", "Aisha Rahman", "S204", "Suite",
                "018-7788990", "Checked-Out", 50.00);
        GuestProfile g8 = new GuestProfile("33445566", "Daniel Lee", "P303", "Penthouse",
                "019-1122334", "Checked-In", 2650.00);
        GuestProfile g9 = new GuestProfile("66778899", "Nur Izzati", "D106", "Deluxe",
                "010-6677889", "Checked-In", 125.00);

        map.put(g1.getConfirmationNumber(), g1);
        map.put(g2.getConfirmationNumber(), g2);
        map.put(g3.getConfirmationNumber(), g3);
        map.put(g4.getConfirmationNumber(), g4);
        map.put(g5.getConfirmationNumber(), g5);
        map.put(g6.getConfirmationNumber(), g6);
        map.put(g7.getConfirmationNumber(), g7);
        map.put(g8.getConfirmationNumber(), g8);
        map.put(g9.getConfirmationNumber(), g9);

        return map;
    }
}

