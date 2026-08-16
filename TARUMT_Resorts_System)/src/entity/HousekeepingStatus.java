/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Gigabyte
 */
public enum HousekeepingStatus {

    OCCUPIED("Occupied"),
    DIRTY("Dirty"),
    CLEANING_IN_PROGRESS("Cleaning In Progress"),
    INSPECTING("Inspecting"),
    READY_FOR_CHECK_IN("Ready for Check-In");

    private final String displayName;

    HousekeepingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
