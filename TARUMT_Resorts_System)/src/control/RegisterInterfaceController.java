//Author: Chan Yu He
package control;

import Dao.MemberDao;
import entity.Member;
import entity.Booking;
import java.time.LocalDateTime;
import adt.CustomList;
import adt.CustomQueue;
import adt.CustomHashMap;
import adt.MapInterface;
import java.time.Duration;
import entity.LoyaltyRoomRequest;
import entity.Room;
import entity.GuestProfile;

public class RegisterInterfaceController {

    private MemberDao memberDAO;
    private MapInterface<String, Room> roomMap;
    private HousekeepingController housekeepingController;
    private VipRoomAllocationController vipController;

    private CustomQueue<Booking> bookingQueue;

    private CustomList<Booking> bookingHistory;
    private MapInterface<String, Booking> bookingConfirmationMap;
    private MapInterface<String, Booking> loyaltyBookingByRequestId;
    private MapInterface<String, GuestProfile> checkedInGuestProfiles;

    private int bookingCounter = 1;

    public RegisterInterfaceController(MemberDao memberDAO,MapInterface<String, Room> roomMap) {
        this(memberDAO, roomMap, null);
    }

    public RegisterInterfaceController(MemberDao memberDAO,MapInterface<String, Room> roomMap,
            HousekeepingController housekeepingController) {
        this(memberDAO, roomMap, housekeepingController, null);
    }

    public RegisterInterfaceController(MemberDao memberDAO,MapInterface<String, Room> roomMap,
            HousekeepingController housekeepingController,VipRoomAllocationController vipController) {

        this.memberDAO = memberDAO;
        this.roomMap = roomMap;
        this.housekeepingController = housekeepingController;
        this.vipController = vipController;

        bookingQueue = new CustomQueue<>();
        bookingHistory =new CustomList<>();

        bookingConfirmationMap = new CustomHashMap<>();
        loyaltyBookingByRequestId = new CustomHashMap<>();
        checkedInGuestProfiles = new CustomHashMap<>();
    }

    public Room[] getAvailableRooms(String roomType) {
        Room[] allRooms = roomMap.values(new Room[roomMap.size()]);
        int count = 0;

        for (Room room : allRooms) {
            if (room != null && room.getRoomType().equalsIgnoreCase(roomType)&& room.isAvailable()) {
                count++;
            }
        }

        Room[] availableRooms = new Room[count];
        int index = 0;
        for (Room room : allRooms) {
            if (room != null && room.getRoomType().equalsIgnoreCase(roomType)&& room.isAvailable()) {
                availableRooms[index++] = room;
            }
        }

        return availableRooms;
    }

    public Room[] getAllRooms() {
        return roomMap.values(new Room[roomMap.size()]);
    }

    public Member findMemberByIC(String icNumber) {

        return memberDAO.findMemberByIC(icNumber);
    }

    public void registerBooking(String icNumber,String roomType,int numberOfNights,String guestName) {
      
        Member member =memberDAO.findMemberByIC(icNumber);

        String bookingId =String.format("B%03d", bookingCounter);
        String confirmationNumber =String.format("%08d",bookingCounter);
        String waitingNumber =String.format("W%03d",bookingCounter);

        bookingCounter++;

   Booking booking;

    if (member != null) {

        booking = new Booking(bookingId,confirmationNumber,waitingNumber,member,roomType,numberOfNights);

    } else {

        booking = new Booking(bookingId,confirmationNumber,waitingNumber,guestName,icNumber,roomType,numberOfNights);
    }

    if (member != null && isLoyaltyTier(member.getMembershipType()) && vipController != null) {
        LoyaltyRoomRequest request = saveLoyaltyBooking(booking);

        System.out.println("\n===== LOYALTY REQUEST SUCCESSFUL =====");
        System.out.println("Request ID       : "+ request.getRequestId());
        System.out.println("Confirmation No. : "+ booking.getConfirmationNumber());
        System.out.println("Guest            : "+ request.getGuestName());
        System.out.println("Tier             : "+ request.getLoyaltyTier());
        System.out.println("Room Type        : " + request.getRoomType());
        System.out.println("Number of Nights : "+ request.getStayNights());
        System.out.println("\nAdded into loyalty priority tree.");
        return;
    }   
    bookingQueue.enqueue(booking);

    bookingHistory.add(booking);
    bookingConfirmationMap.put(booking.getConfirmationNumber(), booking);

        System.out.println("\n===== REGISTRATION SUCCESSFUL =====");
        System.out.println("Booking ID       : "+ booking.getBookingId());
        System.out.println("Confirmation No. : "+ booking.getConfirmationNumber());
        System.out.println("Waiting Number   : "+ booking.getWaitingNumber());
        System.out.println("Guest            : "+ booking.getGuestDisplayName());
        System.out.println("Type             : "+ booking.getMembershipType() );
        System.out.println("Room Type        : "+ booking.getRoomType());
        System.out.println("Number of Nights : "+ booking.getNumberOfNights());
        System.out.printf("Total Billing    : RM %.2f%n",booking.getTotalBilling());
        System.out.println("Registration Time: "+ booking.getFormattedRegistrationTime());
        System.out.println("Status           : "+ booking.getBookingStatus());
        System.out.println("\nPlease wait for your number to be called.");
    }


    public LoyaltyRoomRequest registerLoyaltyBooking(Member member, String roomType,int numberOfNights) {
        if (member == null || vipController == null) {
            return null;
        }
        String bookingId = String.format("B%03d", bookingCounter);
        String confirmationNumber = String.format("%08d", bookingCounter);
        String waitingNumber = String.format("W%03d", bookingCounter);
        bookingCounter++;

        Booking booking = new Booking(bookingId, confirmationNumber, waitingNumber, member,roomType, numberOfNights);
        return saveLoyaltyBooking(booking);
    }

    private LoyaltyRoomRequest saveLoyaltyBooking(Booking booking) {
        LoyaltyRoomRequest request = vipController.addRequest(
                booking.getGuestDisplayName(), booking.getMembershipType(), booking.getRoomType(),
                booking.getNumberOfNights(), booking.getTotalBilling());

        bookingHistory.add(booking);
        bookingConfirmationMap.put(booking.getConfirmationNumber(), booking);
        loyaltyBookingByRequestId.put(request.getRequestId(), booking);
        return request;
    }

    public Booking findBookingForLoyaltyRequest(String requestId) {
        return loyaltyBookingByRequestId.get(requestId);
    }

   public void callNextGuest(String selectedRoomId) {

    Room room = roomMap.get(selectedRoomId);

    if (room == null) {
        System.out.println("\nRoom ID not found.");
        return;
    }

    if (!room.isAvailable()) {
        System.out.println("\nRoom " + selectedRoomId + " is not available.");
        System.out.println("Current status: " + room.getStatus());
        return;
    }

    LoyaltyRoomRequest loyaltyRequest = getNextAvailableLoyaltyRequest();
    if (loyaltyRequest != null) {
        if (!room.getRoomType().equalsIgnoreCase(loyaltyRequest.getRoomType())) {
            System.out.println("\nInvalid room selection.");
            System.out.println("Member requested : " + loyaltyRequest.getRoomType());
            System.out.println("Selected room    : " + room.getRoomType());
            return;
        }
        callNextLoyaltyMember(selectedRoomId);
        return;
    }

    Booking booking = peekNextEligibleBooking();
    if (booking == null) {

        System.out.println("\nThere are no guests waiting.");
        return;
    }

    System.out.println("\n===== CALL NEXT GUEST =====");
    System.out.println("Waiting Number : "+ booking.getWaitingNumber());
    System.out.println("Confirmation No. : "+ booking.getConfirmationNumber());
    System.out.println("Guest          : "+ booking.getGuestDisplayName());
    System.out.println("Type           : "+ booking.getMembershipType());
    System.out.println("Room Type      : "+ booking.getRoomType());
    System.out.println("Number of Nights : "+ booking.getNumberOfNights());
    System.out.printf("Total Billing    : RM %.2f%n",booking.getTotalBilling());

    if (!room.getRoomType().equalsIgnoreCase(booking.getRoomType())) {

        System.out.println("\nInvalid room selection.");
        System.out.println("Guest requested : "+ booking.getRoomType());
        System.out.println("Selected room   : "+ room.getRoomType());
        return;
    }
    booking = removeFirstBookingForRoomType(room.getRoomType());

    booking.setRoomId(room.getRoomNumber());
    booking.setRoomAssignmentTime(LocalDateTime.now());
    booking.setBookingStatus("ASSIGNED");
    createCheckedInGuestProfile(booking);
    room.setStatus("Occupied");
    if (housekeepingController != null) {
        housekeepingController.markRoomOccupied(room.getRoomNumber());
    }

    System.out.println("\n===== ROOM ASSIGNED =====");
    System.out.println("Waiting Number : "+ booking.getWaitingNumber());
    System.out.println("Confirmation No. : "+ booking.getConfirmationNumber());
    System.out.println("Guest          : "+ booking.getGuestDisplayName());
    System.out.println("Type           : "+ booking.getMembershipType());
    System.out.println("Room Type      : "+ booking.getRoomType());
    System.out.println("Room Number    : "+ booking.getRoomId());
    System.out.println("Number of Nights : "+ booking.getNumberOfNights());
    System.out.println("Registration Time : "+ booking.getFormattedRegistrationTime());
    System.out.println("Room Assigned Time : "+ booking.getFormattedRoomAssignmentTime());
    System.out.println("Status         : "+ booking.getBookingStatus());
    System.out.println("\nPlease proceed to room "+ booking.getRoomId()+ ".");
}

    private void callNextLoyaltyMember(String selectedRoomId) {

        LoyaltyRoomRequest request = getNextAvailableLoyaltyRequest();

        if (request == null) {
            System.out.println("\nThere are no loyalty members waiting.");
            return;
        }

    Room room = roomMap.get(selectedRoomId);
    if (room == null) {
        System.out.println("\nRoom ID not found.");
        return;
    }
    if (!room.getRoomType().equalsIgnoreCase(request.getRoomType())) {

        System.out.println("\nInvalid room selection.");
        System.out.println("Member requested : "+ request.getRoomType());
        System.out.println("Selected room    : "+ room.getRoomType());
        return;
    }

    if (!room.isAvailable()) {
        System.out.println("\nRoom "+ selectedRoomId+ " is not available.");
        System.out.println("Current status: "+ room.getStatus());
        return;
    }
    request = vipController.removeNextRequestForRoomType(room.getRoomType());
    vipController.saveAllocatedRequest(request, room.getRoomNumber());

    Booking booking = loyaltyBookingByRequestId.get(request.getRequestId());
    if (booking != null) {
        booking.setRoomId(room.getRoomNumber());
        booking.setRoomAssignmentTime(LocalDateTime.now());
        booking.setBookingStatus("ASSIGNED");
        createCheckedInGuestProfile(booking);
    }
    room.setStatus("Occupied");
    if (housekeepingController != null) {
        housekeepingController.markRoomOccupied(room.getRoomNumber());
    }
    System.out.println("\n===== LOYALTY MEMBER ROOM ASSIGNED =====");
    System.out.println("Request ID      : "+ request.getRequestId());
    System.out.println("Guest           : "+ request.getGuestName());
    System.out.println("Tier            : "+ request.getLoyaltyTier());
    System.out.println("Room Type       : "+ request.getRoomType());
    System.out.println("Room Number     : "+ request.getAllocatedRoomNo());
    System.out.println("Number of Nights: "+ request.getStayNights());
    System.out.println("\nPlease proceed to room "+ request.getAllocatedRoomNo()+ ".");
}  
      public void displayBookingQueue() {

        if (vipController != null && vipController.hasWaitingRequests()) {
            System.out.println("\n===== LOYALTY MEMBER PRIORITY TREE =====");
            displayLoyaltyWaitingList();
        }
        if (bookingQueue.isEmpty()) {
            if (vipController != null && vipController.hasWaitingRequests()) {
                return;
            }
            System.out.println("\nNo bookings are waiting.");
            return;
        }

        System.out.println("\n===== WAITING QUEUE =====");
        bookingQueue.display();
    }

    public void displayGuestQueueOnly() {
        if (bookingQueue.isEmpty()) {
            System.out.println("\nNo standard guests are waiting.");
            return;
        }
        System.out.println("\n===== STANDARD GUEST QUEUE =====");
        bookingQueue.display();
    }

    private void displayLoyaltyWaitingList() {
        LoyaltyRoomRequest[] requests =vipController.getWaitingPriorityReport("All", "Elite");
        System.out.printf("%-10s %-15s %-12s %-12s %-6s%n",
                "Req ID","Name","Tier","Room Type","Nights");

        System.out.println("---------------------------------------------------------------");
        for (int i = 0; i < requests.length; i++) {
            System.out.printf("%-10s %-15s %-12s %-12s %-6d%n",
                    requests[i].getRequestId(),
                    requests[i].getGuestName(),
                    requests[i].getLoyaltyTier(),
                    requests[i].getRoomType(),
                    requests[i].getStayNights()
            );
        }
    }

    public Booking peekNextBooking() {
        return peekNextEligibleBooking();
    }

    public Booking peekNextEligibleBooking() {
        LoyaltyRoomRequest request = getNextAvailableLoyaltyRequest();
        if (request != null) {
            return new Booking(request.getRequestId(),request.getRequestId(),
                    request.getRequestId(),request.getGuestName(),"",
                    request.getLoyaltyTier(),request.getRoomType(),request.getStayNights()
            );
        }
        for (int i = 0; i < bookingQueue.size(); i++) {
            Booking booking = bookingQueue.get(i);
            if (getAvailableRooms(booking.getRoomType()).length > 0) {
                return booking;
            }
        }

        return null;
    }

    private LoyaltyRoomRequest getNextAvailableLoyaltyRequest() {
        if (vipController == null || !vipController.hasWaitingRequests()) {
            return null;
        }
        return vipController.peekNextRequestWithAvailableRoom();
    }

    private Booking removeFirstBookingForRoomType(String roomType) {
        for (int i = 0; i < bookingQueue.size(); i++) {
            Booking booking = bookingQueue.get(i);
            if (booking.getRoomType().equalsIgnoreCase(roomType)) {
                return bookingQueue.remove(i);
            }
        }
        return null;
    }

    public CustomList<Booking> getBookingHistory() {
        return bookingHistory;
    }

    public GuestProfile findGuestProfileByConfirmation(String confirmationNumber) {
        return checkedInGuestProfiles.get(confirmationNumber);
    }

    private void createCheckedInGuestProfile(Booking booking) {
        checkedInGuestProfiles.put(booking.getConfirmationNumber(), new GuestProfile(
                booking.getConfirmationNumber(), booking.getGuestDisplayName(), booking.getRoomId(),
                booking.getRoomType(), booking.getGuestIc() == null ? "-" : booking.getGuestIc(),
                "Checked-In", booking.getTotalBilling()));
    }

    public Booking findBookingById(
            String bookingId) {

        for (int i =0; i< bookingHistory.size();i++) {
            Booking booking=bookingHistory.get(i);
            
            if (booking.getBookingId().equalsIgnoreCase(bookingId)) {
                return booking;
            }
        }
        return null;
    }

    public Booking findBookingByConfirmation(String confirmationNumber) {
        return bookingConfirmationMap.get(confirmationNumber);
    }
    
    public void generateWaitingTimeReport(CustomList<Booking> bookings) {

        if (bookings == null) {
            bookings = new CustomList<>();
        }
        System.out.println("\n======================================================================");
        System.out.println("                     GUEST WAITING TIME REPORT");
        System.out.println("======================================================================");
        System.out.printf("%-8s %-15s %-10s %-18s %-18s %-12s%n",
                "Wait No","Guest","Type","Register Time","Room Time","Wait Time");
        System.out.println("----------------------------------------------------------------------");

        long vipTotalSeconds = 0;
        long normalTotalSeconds = 0;

        int vipCount = 0;
        int normalCount = 0;

        String shortestGuestName = null;
        String shortestGuestType = null;
        String longestGuestName = null;
        String longestGuestType = null;

        long shortestSeconds = Long.MAX_VALUE;
        long longestSeconds = Long.MIN_VALUE;

        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            LocalDateTime registerTime =booking.getRegistrationTime();
            LocalDateTime roomTime =booking.getRoomAssignmentTime();

            if (registerTime == null|| roomTime == null) {
                continue;
            }

            long waitingSeconds =Duration.between(registerTime,roomTime).getSeconds();
            String type = booking.getMembershipType();
            String waitTime =formatDuration(waitingSeconds);
            System.out.printf("%-8s %-15s %-10s %-18s %-18s %-12s%n",
                    booking.getWaitingNumber(), booking.getGuestDisplayName(), type,
                    booking.getFormattedRegistrationTime(),booking.getFormattedRoomAssignmentTime(),waitTime);

            if (isLoyaltyTier(type)) {
                vipTotalSeconds += waitingSeconds;
                vipCount++;
            } else {
                normalTotalSeconds += waitingSeconds;
                normalCount++;
            }

            if (waitingSeconds < shortestSeconds) {

                shortestSeconds = waitingSeconds;
                shortestGuestName = booking.getGuestDisplayName();
                shortestGuestType = booking.getMembershipType();
            }

            if (waitingSeconds > longestSeconds) {
                longestSeconds = waitingSeconds;
                longestGuestName = booking.getGuestDisplayName();
                longestGuestType = booking.getMembershipType();
            }
        }

        LoyaltyRoomRequest[] daoVipRequests = getUnlinkedVipDaoRequests();
        long daoVipTotalSeconds = 0;
        int daoVipAllocatedCount = 0;
        for (int i = 0; i < daoVipRequests.length; i++) {
            LoyaltyRoomRequest request = daoVipRequests[i];
            LocalDateTime endTime = request.getRoomAssignmentTime() == null
                    ? LocalDateTime.now() : request.getRoomAssignmentTime();
            long waitingSeconds = Duration.between(request.getRegistrationTime(), endTime).getSeconds();
            if (request.getRoomAssignmentTime() != null) {
                daoVipTotalSeconds += waitingSeconds;
                daoVipAllocatedCount++;
                if (waitingSeconds < shortestSeconds) {
                    shortestSeconds = waitingSeconds;
                    shortestGuestName = request.getGuestName();
                    shortestGuestType = request.getLoyaltyTier();
                }
                if (waitingSeconds > longestSeconds) {
                    longestSeconds = waitingSeconds;
                    longestGuestName = request.getGuestName();
                    longestGuestType = request.getLoyaltyTier();
                }
            }
            System.out.printf("%-8s %-15s %-10s %-18s %-18s %-12s%n",
                    request.getRequestId(), request.getGuestName(), request.getLoyaltyTier(),
                    request.getFormattedRegistrationTime(), request.getFormattedRoomAssignmentTime(),
                    formatDuration(waitingSeconds));
        }

        System.out.println("----------------------------------------------------------------------");
        System.out.println("\nTotal Guests         : "+ (vipCount + normalCount + daoVipRequests.length));
        System.out.println("Loyalty Guests       : "+ (vipCount + daoVipRequests.length));
        System.out.println("Regular Guests       : "+ normalCount);
        System.out.println("VIP Requests Waiting : " + getWaitingVipRequestCount(daoVipRequests));
        System.out.println("VIP Requests Allocated : " + getAllocatedVipRequestCount(daoVipRequests));

        // VIP average
        int completedLoyaltyCount = vipCount + daoVipAllocatedCount;
        long totalLoyaltyWaitSeconds = vipTotalSeconds + daoVipTotalSeconds;
        if (completedLoyaltyCount > 0) {

            long averageVip = totalLoyaltyWaitSeconds / completedLoyaltyCount;
            System.out.println("Average Loyalty Wait : "+ formatDuration(averageVip));

        } else {
            System.out.println("Average Loyalty Wait : N/A");
        }
        // Normal average
        if (normalCount > 0) {

            long averageNormal =normalTotalSeconds / normalCount;
            System.out.println("Average Regular Wait : "+ formatDuration(averageNormal));
        } else {
            System.out.println("Average Regular Wait : N/A");
        }

        if (shortestGuestName != null) {

            System.out.println("\nShortest Wait        : " + shortestGuestName + " ("
                    + shortestGuestType.toUpperCase() + ") - " + formatDuration(shortestSeconds));
        }

        if (longestGuestName != null) {
            System.out.println("Longest Wait         : " + longestGuestName + " ("
                            + longestGuestType.toUpperCase() + ") - " + formatDuration(longestSeconds));
        }
        System.out.println("\n======================================================================");
    }
    // =========================================================
    // REPORT 2: QUEUE PRIORITY REPORT
    // =========================================================
    public void generateQueuePriorityReport(CustomList<Booking> bookings) {

        if (bookings == null) {
            bookings = new CustomList<>();
        }

        CustomList<Booking> registrationOrder;
        registrationOrder = new CustomList<>();

        for (int i = 0; i < bookings.size(); i++) {
            registrationOrder.add(bookings.get(i));
        }

        sortByRegistrationTime(registrationOrder);
        CustomList<Booking> assignmentOrder =new CustomList<>();

       for (int i =0; i< bookings.size();i++) {
            Booking booking=bookings.get(i);
            if (booking.getRoomAssignmentTime()!= null) {
                assignmentOrder.add(booking);
            }
        }
        sortByAssignmentTime(assignmentOrder);
        System.out.println("\n======================================================================");
        System.out.println("                       QUEUE PRIORITY REPORT");
        System.out.println("======================================================================");
        System.out.printf("%-10s %-8s %-15s %-12s %-16s %-16s%n",
                "Booking ID", "Wait No", "Guest", "Guest Type", "Register Order", "Allocation Order");
        System.out.println("----------------------------------------------------------------------");

       for (int i =0; i< registrationOrder.size();i++) {
            Booking booking=registrationOrder.get(i);
            
            int registerOrder =getRegistrationOrder(registrationOrder,booking);

            int assignOrder =
                    getAssignmentOrder(assignmentOrder,booking);

            String assignment =assignOrder == 0 ? "-": String.valueOf(assignOrder);
            System.out.printf("%-10s %-8s %-15s %-12s %-16d %-16s%n",
                    booking.getBookingId(), booking.getWaitingNumber(), booking.getGuestDisplayName(),
                    booking.getMembershipType(), registerOrder, assignment);
        }

        LoyaltyRoomRequest[] daoVipRequests = getUnlinkedVipDaoRequests();
        for (int i = 0; i < daoVipRequests.length; i++) {
            LoyaltyRoomRequest request = daoVipRequests[i];
            int allocationOrder = getDaoAllocationOrder(request, assignmentOrder, daoVipRequests);
            String allocation = allocationOrder == 0 ? "Waiting" : String.valueOf(allocationOrder);
            System.out.printf("%-10s %-8s %-15s %-12s %-16d %-16s%n",
                    request.getRequestId(), request.getRequestId(), request.getGuestName(),
                    request.getLoyaltyTier(), request.getBookingOrder(), allocation);
        }

        System.out.println("----------------------------------------------------------------------");
        int loyaltyCount = 0;
        int regularCount = 0;
        int loyaltyServed = 0;
        int loyaltyPriorityHonoured = 0;
        for (int i =0; i< bookings.size();i++) {
            Booking booking=bookings.get(i);

            if (isLoyaltyTier(booking.getMembershipType())) {

                loyaltyCount++;
                int loyaltyOrder = getAssignmentOrder(assignmentOrder, booking);

                if (loyaltyOrder > 0) {
                    loyaltyServed++;
                   
                    boolean priorityReceived =true;
                    int loyaltyRegistrationOrder = getRegistrationOrder(registrationOrder, booking);
                    
                    for (int j=0;j<registrationOrder.size();j++) {
                        Booking other = registrationOrder.get(j);
                        
                        int otherRegistrationOrder =getRegistrationOrder(registrationOrder,other);

                        if (otherRegistrationOrder < loyaltyRegistrationOrder
                                && !isLoyaltyTier(other.getMembershipType())) {

                            int otherAssignmentOrder =getAssignmentOrder(assignmentOrder,other);

                            if (otherAssignmentOrder > 0 && otherAssignmentOrder < loyaltyOrder) {
                                priorityReceived = false;
                            }
                        }
                    }

                    if (priorityReceived) {
                        loyaltyPriorityHonoured++;
                    }
                }
            } else {
                regularCount++;
            }
        }
        loyaltyCount += daoVipRequests.length;
        int daoAllocatedCount = getAllocatedVipRequestCount(daoVipRequests);
        loyaltyServed += daoAllocatedCount;
        loyaltyPriorityHonoured += getDaoPriorityHonouredCount(daoVipRequests, bookings);
        System.out.println("\nTotal Loyalty Guests      : " + loyaltyCount);
        System.out.println("Total Regular Guests      : " + regularCount);
        System.out.println("VIP Requests Waiting      : " + getWaitingVipRequestCount(daoVipRequests));
        System.out.println("VIP Requests Allocated    : " + getAllocatedVipRequestCount(daoVipRequests));
        System.out.println("Loyalty Guests Assigned   : " + loyaltyServed);
        System.out.println("Priority Assignments Honoured: " + loyaltyPriorityHonoured);

        if (loyaltyServed > 0) {
            double priorityRate = (loyaltyPriorityHonoured * 100.0) / loyaltyServed;
            System.out.printf("Loyalty Priority Rate     : %.2f%%%n", priorityRate);
        } else {
            System.out.println("Loyalty Priority Rate     : N/A (no loyalty guest assigned)");
        }

        System.out.println("\n======================================================================");
    }

    /** Returns DAO-loaded VIP requests that do not already have a booking row. */
    private LoyaltyRoomRequest[] getUnlinkedVipDaoRequests() {
        if (vipController == null) {
            return new LoyaltyRoomRequest[0];
        }

        LoyaltyRoomRequest[] waitingRequests = vipController.getWaitingPriorityReport("All", "Elite");
        LoyaltyRoomRequest[] allocatedRequests = vipController.getAllocatedRequests();
        LoyaltyRoomRequest[] requests = new LoyaltyRoomRequest[
                waitingRequests.length + allocatedRequests.length];
        int requestIndex = 0;
        for (int i = 0; i < waitingRequests.length; i++) {
            requests[requestIndex++] = waitingRequests[i];
        }
        for (int i = 0; i < allocatedRequests.length; i++) {
            requests[requestIndex++] = allocatedRequests[i];
        }
        int count = 0;
        for (int i = 0; i < requests.length; i++) {
            if (findBookingForLoyaltyRequest(requests[i].getRequestId()) == null) {
                count++;
            }
        }

        LoyaltyRoomRequest[] unlinkedRequests = new LoyaltyRoomRequest[count];
        int index = 0;
        for (int i = 0; i < requests.length; i++) {
            LoyaltyRoomRequest request = requests[i];
            if (findBookingForLoyaltyRequest(request.getRequestId()) == null) {
                unlinkedRequests[index++] = request;
            }
        }
        sortVipRequestsByRegistrationOrder(unlinkedRequests);
        return unlinkedRequests;
    }

    private void sortVipRequestsByRegistrationOrder(LoyaltyRoomRequest[] requests) {
        for (int i = 1; i < requests.length; i++) {
            LoyaltyRoomRequest current = requests[i];
            int j = i - 1;
            while (j >= 0 && requests[j].getBookingOrder() > current.getBookingOrder()) {
                requests[j + 1] = requests[j];
                j--;
            }
            requests[j + 1] = current;
        }
    }

    private int getWaitingVipRequestCount(LoyaltyRoomRequest[] requests) {
        int count = 0;
        for (int i = 0; i < requests.length; i++) {
            if (requests[i].getRoomAssignmentTime() == null) {
                count++;
            }
        }
        return count;
    }

    private int getAllocatedVipRequestCount(LoyaltyRoomRequest[] requests) {
        return requests.length - getWaitingVipRequestCount(requests);
    }

    private int getDaoAllocationOrder(LoyaltyRoomRequest target,
            CustomList<Booking> bookingAssignments, LoyaltyRoomRequest[] daoRequests) {
        if (target.getRoomAssignmentTime() == null) {
            return 0;
        }

        int order = 0;
        for (int i = 0; i < bookingAssignments.size(); i++) {
            if (!bookingAssignments.get(i).getRoomAssignmentTime().isAfter(target.getRoomAssignmentTime())) {
                order++;
            }
        }
        for (int i = 0; i < daoRequests.length; i++) {
            LocalDateTime assignmentTime = daoRequests[i].getRoomAssignmentTime();
            if (assignmentTime != null && !assignmentTime.isAfter(target.getRoomAssignmentTime())) {
                order++;
            }
        }
        return order;
    }

    private int getDaoPriorityHonouredCount(LoyaltyRoomRequest[] daoRequests,
            CustomList<Booking> bookings) {
        int count = 0;
        for (int i = 0; i < daoRequests.length; i++) {
            LoyaltyRoomRequest request = daoRequests[i];
            if (request.getRoomAssignmentTime() == null) {
                continue;
            }

            boolean priorityHonoured = true;
            for (int j = 0; j < bookings.size(); j++) {
                Booking booking = bookings.get(j);
                if (!isLoyaltyTier(booking.getMembershipType())
                        && booking.getRoomAssignmentTime() != null
                        && booking.getRegistrationTime().isBefore(request.getRegistrationTime())
                        && booking.getRoomAssignmentTime().isBefore(request.getRoomAssignmentTime())) {
                    priorityHonoured = false;
                    break;
                }
            }
            if (priorityHonoured) {
                count++;
            }
        }
        return count;
    }

    private void sortByRegistrationTime(CustomList<Booking> bookings) {

        for (int i = 1;i < bookings.size();i++) {

            Booking current =bookings.get(i);
            int j = i - 1;
            while (j >= 0&& bookings.get(j).getRegistrationTime().isAfter(current.getRegistrationTime())) {

                bookings.set(j + 1,bookings.get(j));
                j--;
            }

            bookings.set(j + 1,current);
        }
    }
    
    private void sortByAssignmentTime(CustomList<Booking> bookings) {

        for (int i = 1;i < bookings.size();i++) {

            Booking current =bookings.get(i);
            int j = i - 1;
            while (j >= 0&& bookings.get(j).getRoomAssignmentTime().isAfter(current.getRoomAssignmentTime())) {

                bookings.set(j + 1,bookings.get(j));
                j--;
            }
            bookings.set(j + 1,current);
        }
    }

    private int getRegistrationOrder(CustomList<Booking> bookings,Booking target) {

        for (int i = 0;i < bookings.size();i++) {

            if (bookings.get(i).getBookingId().equals(target.getBookingId())) {
                return i + 1;
            }
        }
        return 0;
    }

    private int getAssignmentOrder(CustomList<Booking> bookings,Booking target) {

        for (int i = 0;i < bookings.size();i++) {

            if (bookings.get(i).getBookingId().equals(target.getBookingId())) {
                return i + 1;
            }
        }

        return 0;
    }


    private int getNormalServedFirst(
            CustomList<Booking> registrationOrder,
            CustomList<Booking> assignmentOrder) {

        int count = 0;

        for (int i=0;i< registrationOrder.size();i++) {
            Booking normal = registrationOrder.get(i);
            
            if (!normal.getMembershipType().equalsIgnoreCase("NORMAL")) {
                continue;
            }

            int normalRegistration =getRegistrationOrder(registrationOrder,normal);
            int normalAssignment =getAssignmentOrder(assignmentOrder,normal);

            if (normalAssignment == 0) {
                continue;
            }

            boolean servedBeforeVip = true;

            for (int j =0; j< registrationOrder.size();j++) {
            Booking vip= registrationOrder.get(j);

                if (!vip.getMembershipType()
                        .equalsIgnoreCase("VIP")) {

                    continue;
                }

                int vipRegistration =getRegistrationOrder(registrationOrder, vip);

                int vipAssignment =getAssignmentOrder(assignmentOrder,vip);
                if (vipRegistration < normalRegistration && vipAssignment > 0 && vipAssignment < normalAssignment) {
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

    private boolean isLoyaltyTier(String tier) {
        return tier != null&& (tier.equalsIgnoreCase("Platinum")|| tier.equalsIgnoreCase("Diamond")|| tier.equalsIgnoreCase("Elite"));
    }

    private String formatDuration(long totalSeconds) {

        long minutes =totalSeconds / 60;
        long seconds =totalSeconds % 60;
        return String.format("%dm %02ds",minutes,seconds);
    }
    
}
