/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package walkinregistrationbooking;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingReport {

    // =========================================================
    // REPORT 1: GUEST WAITING TIME REPORT
    // =========================================================

    public void generateWaitingTimeReport(
            List<Booking> bookings) {

        if (bookings == null || bookings.isEmpty()) {

            System.out.println(
                    "\nNo booking records available."
            );

            return;
        }

        System.out.println(
                "\n======================================================================"
        );

        System.out.println(
                "                     GUEST WAITING TIME REPORT"
        );

        System.out.println(
                "======================================================================"
        );

        System.out.printf(
                "%-8s %-15s %-10s %-18s %-18s %-12s%n",
                "Wait No",
                "Guest",
                "Type",
                "Register Time",
                "Room Time",
                "Wait Time"
        );

        System.out.println(
                "----------------------------------------------------------------------"
        );

        long vipTotalSeconds = 0;
        long normalTotalSeconds = 0;

        int vipCount = 0;
        int normalCount = 0;

        Booking shortestBooking = null;
        Booking longestBooking = null;

        long shortestSeconds = Long.MAX_VALUE;
        long longestSeconds = Long.MIN_VALUE;

        for (Booking booking : bookings) {

            LocalDateTime registerTime =
                    booking.getRegistrationTime();

            LocalDateTime roomTime =
                    booking.getRoomAssignmentTime();

            // Only calculate waiting time
            // if the guest has already received a room
            if (registerTime == null
                    || roomTime == null) {

                continue;
            }

            long waitingSeconds =
                    Duration.between(
                            registerTime,
                            roomTime
                    ).getSeconds();

            String type =
                    booking.getMember()
                            .getMembershipType();

            String waitTime =
                    formatDuration(waitingSeconds);

            System.out.printf(
                    "%-8s %-15s %-10s %-18s %-18s %-12s%n",
                    booking.getWaitingNumber(),
                    booking.getMember()
                            .getMemberName(),
                    type,
                    booking.getFormattedRegistrationTime(),
                    booking.getFormattedRoomAssignmentTime(),
                    waitTime
            );

            // Separate VIP and Normal
            if (type.equalsIgnoreCase("VIP")) {

                vipTotalSeconds += waitingSeconds;
                vipCount++;

            } else {

                normalTotalSeconds += waitingSeconds;
                normalCount++;
            }

            // Find shortest waiting time
            if (waitingSeconds < shortestSeconds) {

                shortestSeconds = waitingSeconds;
                shortestBooking = booking;
            }

            // Find longest waiting time
            if (waitingSeconds > longestSeconds) {

                longestSeconds = waitingSeconds;
                longestBooking = booking;
            }
        }

        System.out.println(
                "----------------------------------------------------------------------"
        );

        System.out.println(
                "\nTotal Guests         : "
                        + (vipCount + normalCount)
        );

        System.out.println(
                "VIP Guests           : "
                        + vipCount
        );

        System.out.println(
                "Normal Guests        : "
                        + normalCount
        );

        // VIP average
        if (vipCount > 0) {

            long averageVip =
                    vipTotalSeconds / vipCount;

            System.out.println(
                    "Average VIP Wait     : "
                            + formatDuration(averageVip)
            );

        } else {

            System.out.println(
                    "Average VIP Wait     : N/A"
            );
        }

        // Normal average
        if (normalCount > 0) {

            long averageNormal =
                    normalTotalSeconds / normalCount;

            System.out.println(
                    "Average Normal Wait  : "
                            + formatDuration(averageNormal)
            );

        } else {

            System.out.println(
                    "Average Normal Wait  : N/A"
            );
        }

        if (shortestBooking != null) {

            System.out.println(
                    "\nShortest Wait        : "
                            + shortestBooking
                            .getMember()
                            .getMemberName()
                            + " ("
                            + shortestBooking
                            .getMember()
                            .getMembershipType()
                            .toUpperCase()
                            + ") - "
                            + formatDuration(
                                    shortestSeconds
                            )
            );
        }

        if (longestBooking != null) {

            System.out.println(
                    "Longest Wait         : "
                            + longestBooking
                            .getMember()
                            .getMemberName()
                            + " ("
                            + longestBooking
                            .getMember()
                            .getMembershipType()
                            .toUpperCase()
                            + ") - "
                            + formatDuration(
                                    longestSeconds
                            )
            );
        }

        System.out.println(
                "\n======================================================================"
        );
    }


    // =========================================================
    // REPORT 2: QUEUE PRIORITY REPORT
    // =========================================================

    public void generateQueuePriorityReport(
            List<Booking> bookings) {

        if (bookings == null || bookings.isEmpty()) {

            System.out.println(
                    "\nNo booking records available."
            );

            return;
        }

        // Create a copy so the original bookingHistory
        // is not changed.
        List<Booking> registrationOrder =
                new ArrayList<>(bookings);

        // Sort according to registration time
        sortByRegistrationTime(
                registrationOrder
        );

        // Create another list for assignment order
        List<Booking> assignmentOrder =
                new ArrayList<>();

        for (Booking booking : bookings) {

            if (booking.getRoomAssignmentTime()
                    != null) {

                assignmentOrder.add(booking);
            }
        }

        // Sort according to room assignment time
        sortByAssignmentTime(
                assignmentOrder
        );

        System.out.println(
                "\n======================================================================"
        );

        System.out.println(
                "                       QUEUE PRIORITY REPORT"
        );

        System.out.println(
                "======================================================================"
        );

        System.out.printf(
                "%-10s %-8s %-15s %-10s %-16s %-14s%n",
                "Booking ID",
                "Wait No",
                "Guest",
                "Type",
                "Register Order",
                "Assign Order"
        );

        System.out.println(
                "----------------------------------------------------------------------"
        );

        for (Booking booking : registrationOrder) {

            int registerOrder =
                    getRegistrationOrder(
                            registrationOrder,
                            booking
                    );

            int assignOrder =
                    getAssignmentOrder(
                            assignmentOrder,
                            booking
                    );

            String assignment =
                    assignOrder == 0
                    ? "-"
                    : String.valueOf(assignOrder);

            System.out.printf(
                    "%-10s %-8s %-15s %-10s %-16d %-14s%n",
                    booking.getBookingId(),
                    booking.getWaitingNumber(),
                    booking.getMember()
                            .getMemberName(),
                    booking.getMember()
                            .getMembershipType(),
                    registerOrder,
                    assignment
            );
        }

        System.out.println(
                "----------------------------------------------------------------------"
        );

        int vipCount = 0;
        int normalCount = 0;
        int vipServedFirst = 0;

        for (Booking booking : bookings) {

            if (booking.getMember()
                    .getMembershipType()
                    .equalsIgnoreCase("VIP")) {

                vipCount++;

                int vipOrder =
                        getAssignmentOrder(
                                assignmentOrder,
                                booking
                        );

                if (vipOrder > 0) {

                    // Check whether this VIP was served
                    // before any NORMAL guest who registered
                    // before this VIP.
                    boolean priorityReceived =
                            true;

                    int vipRegistrationOrder =
                            getRegistrationOrder(
                                    registrationOrder,
                                    booking
                            );

                    for (Booking other
                            : registrationOrder) {

                        int otherRegistrationOrder =
                                getRegistrationOrder(
                                        registrationOrder,
                                        other
                                );

                        if (otherRegistrationOrder
                                < vipRegistrationOrder
                                && other.getMember()
                                .getMembershipType()
                                .equalsIgnoreCase("NORMAL")) {

                            int otherAssignmentOrder =
                                    getAssignmentOrder(
                                            assignmentOrder,
                                            other
                                    );

                            if (otherAssignmentOrder > 0
                                    && otherAssignmentOrder
                                    < vipOrder) {

                                priorityReceived = false;
                            }
                        }
                    }

                    if (priorityReceived) {

                        vipServedFirst++;
                    }
                }

            } else {

                normalCount++;
            }
        }

        System.out.println(
                "\nTotal VIP Guests          : "
                        + vipCount
        );

        System.out.println(
                "Total Normal Guests       : "
                        + normalCount
        );

        System.out.println(
                "VIP Guests Served First   : "
                        + vipServedFirst
        );

        System.out.println(
                "Normal Guests Served First: "
                        + getNormalServedFirst(
                                registrationOrder,
                                assignmentOrder
                        )
        );

        if (vipCount > 0) {

            double priorityRate =
                    (vipServedFirst * 100.0)
                    / vipCount;

            System.out.printf(
                    "VIP Priority Rate         : %.2f%%%n",
                    priorityRate
            );
        }

        if (!registrationOrder.isEmpty()) {

            Booking firstRegistered =
                    registrationOrder.get(0);

            System.out.println(
                    "\nFirst Registered           : "
                            + firstRegistered
                            .getMember()
                            .getMemberName()
                            + " ("
                            + firstRegistered
                            .getMember()
                            .getMembershipType()
                            .toUpperCase()
                            + ")"
            );
        }

        if (!assignmentOrder.isEmpty()) {

            Booking firstAssigned =
                    assignmentOrder.get(0);

            System.out.println(
                    "First Served               : "
                            + firstAssigned
                            .getMember()
                            .getMemberName()
                            + " ("
                            + firstAssigned
                            .getMember()
                            .getMembershipType()
                            .toUpperCase()
                            + ")"
            );
        }

        System.out.println(
                "\n======================================================================"
        );
    }


    // =========================================================
    // SORTING
    // =========================================================

    private void sortByRegistrationTime(
            List<Booking> bookings) {

        for (int i = 1;
                i < bookings.size();
                i++) {

            Booking current =
                    bookings.get(i);

            int j = i - 1;

            while (j >= 0
                    && bookings.get(j)
                    .getRegistrationTime()
                    .isAfter(
                            current
                            .getRegistrationTime()
                    )) {

                bookings.set(
                        j + 1,
                        bookings.get(j)
                );

                j--;
            }

            bookings.set(
                    j + 1,
                    current
            );
        }
    }


    private void sortByAssignmentTime(
            List<Booking> bookings) {

        for (int i = 1;
                i < bookings.size();
                i++) {

            Booking current =
                    bookings.get(i);

            int j = i - 1;

            while (j >= 0
                    && bookings.get(j)
                    .getRoomAssignmentTime()
                    .isAfter(
                            current
                            .getRoomAssignmentTime()
                    )) {

                bookings.set(
                        j + 1,
                        bookings.get(j)
                );

                j--;
            }

            bookings.set(
                    j + 1,
                    current
            );
        }
    }


    // =========================================================
    // SEARCHING
    // =========================================================

    private int getRegistrationOrder(
            List<Booking> bookings,
            Booking target) {

        for (int i = 0;
                i < bookings.size();
                i++) {

            if (bookings.get(i)
                    .getBookingId()
                    .equals(
                            target.getBookingId()
                    )) {

                return i + 1;
            }
        }

        return 0;
    }


    private int getAssignmentOrder(
            List<Booking> bookings,
            Booking target) {

        for (int i = 0;
                i < bookings.size();
                i++) {

            if (bookings.get(i)
                    .getBookingId()
                    .equals(
                            target.getBookingId()
                    )) {

                return i + 1;
            }
        }

        return 0;
    }


    private int getNormalServedFirst(
            List<Booking> registrationOrder,
            List<Booking> assignmentOrder) {

        int count = 0;

        for (Booking normal : registrationOrder) {

            if (!normal.getMember()
                    .getMembershipType()
                    .equalsIgnoreCase("NORMAL")) {

                continue;
            }

            int normalRegistration =
                    getRegistrationOrder(
                            registrationOrder,
                            normal
                    );

            int normalAssignment =
                    getAssignmentOrder(
                            assignmentOrder,
                            normal
                    );

            if (normalAssignment == 0) {
                continue;
            }

            boolean servedBeforeVip = true;

            for (Booking vip : registrationOrder) {

                if (!vip.getMember()
                        .getMembershipType()
                        .equalsIgnoreCase("VIP")) {

                    continue;
                }

                int vipRegistration =
                        getRegistrationOrder(
                                registrationOrder,
                                vip
                        );

                int vipAssignment =
                        getAssignmentOrder(
                                assignmentOrder,
                                vip
                        );

                if (vipRegistration < normalRegistration
                        && vipAssignment > 0
                        && vipAssignment < normalAssignment) {

                    servedBeforeVip = false;
                    break;
                }
            }

            if (servedBeforeVip) {
                count++;
            }
        }

        return count;
    }


    // =========================================================
    // FORMAT WAITING TIME
    // =========================================================

    private String formatDuration(
            long totalSeconds) {

        long minutes =
                totalSeconds / 60;

        long seconds =
                totalSeconds % 60;

        return String.format(
                "%dm %02ds",
                minutes,
                seconds
        );
    }
}