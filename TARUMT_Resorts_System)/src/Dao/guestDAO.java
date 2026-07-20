// Author: [Your Name]
package dao;

import adt.CustomHashMap;
import adt.MapInterface;
import entity.GuestProfile;

public class GuestDAO {

    // Method to load data into your custom Map ADT
    public MapInterface<String, GuestProfile> loadGuests() {
        MapInterface<String, GuestProfile> map = new CustomHashMap<>();

        // You can populate mock data here OR read from a text file (.txt)
        GuestProfile g1 = new GuestProfile("12345678", "Alex Tan", "Deluxe", 450.00);
        GuestProfile g2 = new GuestProfile("87654321", "Siti Aminah", "Suite", 1200.00);
        GuestProfile g3 = new GuestProfile("55667788", "Michael Jordan", "Penthouse", 5500.00);

        map.put(g1.getConfirmationNumber(), g1);
        map.put(g2.getConfirmationNumber(), g2);
        map.put(g3.getConfirmationNumber(), g3);

        return map;
    }
}
