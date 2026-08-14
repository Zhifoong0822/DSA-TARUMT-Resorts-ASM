/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.LinkedStack;
import adt.StackInterface;
import adt.MapInterface;
import Dao.RoomDAO;

import entity.HousekeepingStatus;
import entity.HousekeepingTask;
import entity.StatusChange;
import entity.Room;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
/**
 *
 * @author Gigabyte
 */
public class HousekeepingController {

    private HousekeepingTask[] tasks;

    private int taskCount;

    private static final int MAX_TASKS = 100;

    private StackInterface<StatusChange> statusHistory;

    private MapInterface<String, Room> roomMap;

    public HousekeepingController() {

        this(new RoomDAO().loadRooms());
    }

    public HousekeepingController(MapInterface<String, Room> roomMap) {

        tasks = new HousekeepingTask[MAX_TASKS];

        taskCount = 0;

        statusHistory = new LinkedStack<>();

        this.roomMap = roomMap;

        loadTasksFromRoomDAO();
    }

    // =====================================================
    // ADD TASK
    // =====================================================

    public boolean addTask(HousekeepingTask task) {

        if (taskCount >= MAX_TASKS) {
            return false;
        }

        // Prevent duplicate Task ID
        if (searchTaskIndexById(task.getTaskId()) != -1) {
            return false;
        }

        tasks[taskCount] = task;

        taskCount++;

        return true;
    }

    // =====================================================
    // LINEAR SEARCH BY TASK ID
    // =====================================================

    public HousekeepingTask searchTaskById(String taskId) {

        int index = searchTaskIndexById(taskId);

        if (index == -1) {
            return null;
        }

        return tasks[index];
    }

    private int searchTaskIndexById(String taskId) {

        for (int i = 0; i < taskCount; i++) {

            if (tasks[i].getTaskId().equalsIgnoreCase(taskId)) {

                return i;
            }
        }

        return -1;
    }

    // =====================================================
    // LINEAR SEARCH BY ROOM NUMBER
    // =====================================================

    public HousekeepingTask searchTaskByRoom(String roomNumber) {

        for (int i = 0; i < taskCount; i++) {

            if (tasks[i]
                    .getRoomNumber()
                    .equalsIgnoreCase(roomNumber)) {

                return tasks[i];
            }
        }

        return null;
    }

    public HousekeepingTask[] getTasksByStatus(HousekeepingStatus status) {
        int count = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getStatus() == status) {
                count++;
            }
        }

        HousekeepingTask[] matchingTasks = new HousekeepingTask[count];
        int index = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getStatus() == status) {
                matchingTasks[index++] = tasks[i];
            }
        }

        insertionSortByRoomNumber(matchingTasks, matchingTasks.length);
        return matchingTasks;
    }

    public void markRoomOccupied(String roomNumber) {
        HousekeepingTask task = searchTaskByRoom(roomNumber);
        if (task != null) {
            task.setStatus(HousekeepingStatus.OCCUPIED);
        }
    }

    // =====================================================
    // NORMAL CHECK-OUT
    // =====================================================

    public String handleNormalCheckOut(String roomNumber) {

        Room room = roomMap.get(roomNumber);
        if (room == null) {
            return "Room " + roomNumber + " was not found in the room list.";
        }

        if (!room.getStatus().equalsIgnoreCase("Occupied")) {
            return "Current status: " + room.getStatus() + "\n"
                    + "Room " + roomNumber
                    + " cannot be checked out because it is not occupied.";
        }

        HousekeepingTask task = searchTaskByRoom(roomNumber);

        if (task == null) {
            return "Room " + roomNumber + " was not found in the housekeeping task list.";
        }

        // A walk-in room assignment updates the shared room map immediately.
        // Synchronize this housekeeping task before recording the checkout workflow.
        if (task.getStatus() != HousekeepingStatus.OCCUPIED) {
            task.setStatus(HousekeepingStatus.OCCUPIED);
        }

        StatusChange change = new StatusChange(
                searchTaskIndexById(task.getTaskId()),
                task.getTaskId(),
                HousekeepingStatus.OCCUPIED,
                HousekeepingStatus.DIRTY
        );

        statusHistory.push(change);
        task.setStatus(HousekeepingStatus.DIRTY);
        updateRoomStatus(task);

        return "Normal check-out completed.\n"
                + "Room " + task.getRoomNumber()
                + " status changed to Dirty. Housekeeping may now begin cleaning.";
    }

    // =====================================================
    // LATE CHECK-OUT
    // =====================================================

    public String HandleLateCheckOut(String roomNumber) {

        HousekeepingTask task = searchTaskByRoom(roomNumber);

        if (task == null) {
            return "Room " + roomNumber + " was not found in the housekeeping task list.";
        }

        if (task.getStatus() == HousekeepingStatus.OCCUPIED) {
            return "Room " + task.getRoomNumber()
                    + " is already Occupied. Housekeeping remains paused for the late check-out.";
        }

        int rollbackCount = 0;
        while (!statusHistory.isEmpty()
                && statusHistory.peek().getTaskId().equalsIgnoreCase(task.getTaskId())) {
            StatusChange latestChange = statusHistory.pop();
            task.setStatus(latestChange.getPreviousStatus());
            updateRoomStatus(task);
            rollbackCount++;
        }

        if (task.getStatus() != HousekeepingStatus.OCCUPIED) {
            return "Late check-out could not restore room " + task.getRoomNumber()
                    + " to Occupied because its earlier status change is not available on the rollback stack.";
        }

        return "Late check-out request recorded.\n"
                + "Rolled back " + rollbackCount + " status update(s).\n"
                + "Room " + task.getRoomNumber()
                + " restored to Occupied; housekeeping is paused until actual check-out.";
    }

    public String handleCompletedLateCheckOut(String roomNumber) {

        HousekeepingTask task = searchTaskByRoom(roomNumber);

        if (task == null) {
            return "Room " + roomNumber + " was not found in the housekeeping task list.";
        }

        if (task.getStatus() != HousekeepingStatus.OCCUPIED) {
            return "Current status: " + task.getStatus() + "\n"
                    + "Room " + task.getRoomNumber()
                    + " must be checked out by Front Desk before cleaning can begin.";
        }

        return "Late check-out is still active for room " + task.getRoomNumber()
                + ". When the guest leaves, Front Desk must perform Normal Check-Out "
                + "to change the room from Occupied to Dirty.";
    }

    // =====================================================
    // UPDATE STATUS
    // =====================================================

    public String updateTaskStatus(
            String taskId,
            HousekeepingStatus newStatus) {

        int index = searchTaskIndexById(taskId);

        if (index == -1) {

            return "Task not found.";
        }

        HousekeepingTask task = tasks[index];

        HousekeepingStatus oldStatus = task.getStatus();

        if (oldStatus == newStatus) {

            return "Task is already in this status.";
        }

        // Validate status progression
        if (!isValidStatusTransition(oldStatus, newStatus)) {

            return "Invalid status transition: "
                    + oldStatus
                    + " -> "
                    + newStatus;
        }

        // Store change into Stack before changing status
        StatusChange change = new StatusChange(
                index,
                taskId,
                oldStatus,
                newStatus
        );

        statusHistory.push(change);

        task.setStatus(newStatus);
        updateRoomStatus(task);

        // If completed
        if (newStatus
                == HousekeepingStatus.READY_FOR_CHECK_IN) {

            LocalTime completionTime = LocalTime.now();

            task.setCompletionTime(completionTime);

            long minutes = Duration.between(
                    task.getStartTime(),
                    completionTime
            ).toMinutes();

            if (minutes < 0) {
                minutes = 0;
            }

            task.setCompletionMinutes(
                    (int) minutes
            );
        }

        return "Status successfully updated from "
                + oldStatus
                + " to "
                + newStatus
                + ".";
    }

    // =====================================================
    // STATUS VALIDATION
    // =====================================================

    private boolean isValidStatusTransition(
            HousekeepingStatus current,
            HousekeepingStatus next) {

        if (current == HousekeepingStatus.DIRTY) {

            return next
                    == HousekeepingStatus.CLEANING_IN_PROGRESS;
        }

        if (current
                == HousekeepingStatus.CLEANING_IN_PROGRESS) {

            return next
                    == HousekeepingStatus.INSPECTED;
        }

        if (current == HousekeepingStatus.INSPECTED) {

            return next
                    == HousekeepingStatus.READY_FOR_CHECK_IN;
        }

        return false;
    }

    // =====================================================
    // UNDO / ROLLBACK USING STACK
    // =====================================================

    public String undoLatestStatusChange() {

        if (statusHistory.isEmpty()) {

            return "No status update available to undo.";
        }

        StatusChange latestChange
                = statusHistory.pop();

        int taskIndex
                = latestChange.getTaskIndex();

        HousekeepingTask task
                = tasks[taskIndex];

        task.setStatus(
                latestChange.getPreviousStatus()
        );
        updateRoomStatus(task);

        /*
         * If READY status is rolled back,
         * remove completion information.
         */
        if (latestChange.getNewStatus()
                == HousekeepingStatus.READY_FOR_CHECK_IN) {

            task.setCompletionTime(null);
            task.setCompletionMinutes(0);
        }

        return "Rollback successful.\n"
                + "Task: "
                + latestChange.getTaskId()
                + "\nStatus restored to: "
                + latestChange.getPreviousStatus();
    }

    // =====================================================
    // VIEW MOST RECENT CHANGE
    // =====================================================

    public StatusChange getLatestStatusChange() {

        return statusHistory.peek();
    }

    // ROOM STATUS REPORT
    // Filtering + Searching + Sorting
    public String generateRoomStatusReport(int floorFilter, HousekeepingStatus statusFilter, LocalDate dateFilter) {

        HousekeepingTask[] results = new HousekeepingTask[MAX_TASKS];

        int resultCount = 0;

        // FILTER / SEARCH
        for (int i = 0; i < taskCount; i++) {

            HousekeepingTask task = tasks[i];

            boolean floorMatch
                    = floorFilter == 0
                    || task.getFloor() == floorFilter;

            boolean statusMatch
                    = statusFilter == null
                    || task.getStatus() == statusFilter;

            boolean dateMatch
                    = dateFilter == null
                    || task.getTaskDate().equals(dateFilter);

            if (floorMatch
                    && statusMatch
                    && dateMatch) {

                results[resultCount] = task;

                resultCount++;
            }
        }

        // SORT BY ROOM NUMBER
        insertionSortByRoomNumber(
                results,
                resultCount
        );

        String floorLabel = floorFilter == 0
                ? "All"
                : String.valueOf(floorFilter);

        String statusLabel = statusFilter == null
                ? "All"
                : statusFilter.toString();

        StringBuilder report
                = new StringBuilder();

        report.append("\n");
        report.append(
                "=======================================================================\n"
        );

        report.append(
                "                    HOUSEKEEPING ROOM STATUS REPORT\n"
        );

        report.append(
                "          Filters: Floor = " + floorLabel
                + ", Status = " + statusLabel
                + " | Sorted by Room Number\n"
        );

        report.append(
                "=======================================================================\n"
        );

        report.append(
                String.format(
                        "%-8s %-10s %-8s %-10s %-25s\n",
                        "Task ID",
                        "Room",
                        "Floor",
                        "Staff",
                        "Status"
                )
        );

        report.append(
                "-----------------------------------------------------------------------\n"
        );

        for (int i = 0; i < resultCount; i++) {

            HousekeepingTask task
                    = results[i];

            report.append(
                    String.format(
                            "%-8s %-10s %-8d %-10s %-25s\n",
                            task.getTaskId(),
                            task.getRoomNumber(),
                            task.getFloor(),
                            task.getStaffId(),
                            task.getStatus()
                    )
            );
        }

        report.append(
                "-----------------------------------------------------------------------\n"
        );

        report.append(
                "Total matching rooms: "
        );

        report.append(resultCount);

        report.append("\n");

        report.append(
                "=======================================================================\n"
        );

        return report.toString();
    }

    // INSERTION SORT BY ROOM NUMBER
    private void insertionSortByRoomNumber(
            HousekeepingTask[] array,
            int count) {

        for (int i = 1; i < count; i++) {

            HousekeepingTask key
                    = array[i];

            int j = i - 1;

            while (j >= 0
                    && array[j]
                            .getRoomNumber()
                            .compareToIgnoreCase(
                                    key.getRoomNumber()
                            ) > 0) {

                array[j + 1] = array[j];

                j--;
            }

            array[j + 1] = key;
        }
    }

    // STAFF PERFORMANCE REPORT
    public String generateStaffPerformanceReport(
            LocalDate dateFilter) {

        return generateStaffPerformanceReport(dateFilter, null);
    }

    public String generateStaffPerformanceReport(
            LocalDate dateFilter,
            String roomTypeFilter) {

        String[] staffIds = new String[MAX_TASKS];

        int[] completedTasks = new int[MAX_TASKS];

        int[] totalMinutes = new int[MAX_TASKS];

        int staffCount = 0;

        // ============================================
        // SEARCH AND FILTER RECORDS
        // ============================================

        for (int i = 0; i < taskCount; i++) {

            HousekeepingTask task = tasks[i];

            boolean dateMatch
                    = dateFilter == null
                    || task.getTaskDate()
                            .equals(dateFilter);

            boolean completed
                    = task.getStatus()
                    == HousekeepingStatus.READY_FOR_CHECK_IN;

            Room room = roomMap.get(task.getRoomNumber());

            boolean roomTypeMatch
                    = roomTypeFilter == null
                    || roomTypeFilter.trim().isEmpty()
                    || roomTypeFilter.equalsIgnoreCase("All")
                    || (room != null && room.getRoomType()
                            .equalsIgnoreCase(roomTypeFilter));

            if (dateMatch && roomTypeMatch && completed) {

                int staffIndex
                        = findStaffIndex(
                                staffIds,
                                staffCount,
                                task.getStaffId()
                        );

                if (staffIndex == -1) {

                    staffIds[staffCount]
                            = task.getStaffId();

                    completedTasks[staffCount] = 1;

                    totalMinutes[staffCount]
                            = task.getCompletionMinutes();

                    staffCount++;

                } else {

                    completedTasks[staffIndex]++;

                    totalMinutes[staffIndex]
                            += task.getCompletionMinutes();
                }
            }
        }

        // SORT STAFF BY NUMBER OF COMPLETED TASKS
        // Highest -> Lowest
        insertionSortStaffPerformance(
                staffIds,
                completedTasks,
                totalMinutes,
                staffCount
        );

        StringBuilder report
                = new StringBuilder();

        report.append("\n");

        report.append(
                "================================================================\n"
        );

        report.append(
                "               HOUSEKEEPING STAFF PERFORMANCE REPORT\n"
        );

        report.append(
                "           Filter: Room Type = "
                + (roomTypeFilter == null ? "All" : roomTypeFilter)
                + " | Sorted by Tasks Completed"
                + "\n"
        );

        report.append(
                "================================================================\n"
        );

        report.append(
                String.format(
                        "%-6s %-12s %-18s\n",
                        "Rank",
                        "Staff ID",
                        "Tasks Completed"
                )
        );

        report.append(
                "----------------------------------------------------------------\n"
        );

        int overallCompleted = 0;

        for (int i = 0; i < staffCount; i++) {
            report.append(
                    String.format(
                            "%-6d %-12s %-18d\n",
                            i + 1,
                            staffIds[i],
                            completedTasks[i]
                    )
            );

            overallCompleted
                    += completedTasks[i];
        }

        report.append(
                "----------------------------------------------------------------\n"
        );

        report.append(
                "Total completed tasks: "
        );

        report.append(overallCompleted);

        report.append("\n");

        report.append(
                "================================================================\n"
        );

        return report.toString();
    }

    // =====================================================
    // SEARCH STAFF
    // =====================================================

    private int findStaffIndex(
            String[] staffIds,
            int staffCount,
            String targetStaffId) {

        for (int i = 0; i < staffCount; i++) {

            if (staffIds[i]
                    .equalsIgnoreCase(targetStaffId)) {

                return i;
            }
        }

        return -1;
    }

    // =====================================================
    // INSERTION SORT STAFF PERFORMANCE
    // =====================================================

    private void insertionSortStaffPerformance(
            String[] staffIds,
            int[] completedTasks,
            int[] totalMinutes,
            int staffCount) {

        for (int i = 1; i < staffCount; i++) {

            String keyStaff
                    = staffIds[i];

            int keyCompleted
                    = completedTasks[i];

            int keyMinutes
                    = totalMinutes[i];

            int j = i - 1;

            while (j >= 0
                    && completedTasks[j]
                    < keyCompleted) {

                staffIds[j + 1]
                        = staffIds[j];

                completedTasks[j + 1]
                        = completedTasks[j];

                totalMinutes[j + 1]
                        = totalMinutes[j];

                j--;
            }

            staffIds[j + 1]
                    = keyStaff;

            completedTasks[j + 1]
                    = keyCompleted;

            totalMinutes[j + 1]
                    = keyMinutes;
        }
    }

    // =====================================================
    // DISPLAY ALL TASKS
    // =====================================================

    public String getAllTasks() {

        StringBuilder output
                = new StringBuilder();

        output.append("\n");

        output.append(
                "================================================================================\n"
        );

        output.append(
                "                         HOUSEKEEPING TASK LIST\n"
        );

        output.append(
                "================================================================================\n"
        );

        output.append(
                String.format(
                        "%-8s %-8s %-6s %-10s %-25s %-12s\n",
                        "Task ID",
                        "Room",
                        "Floor",
                        "Staff",
                        "Status",
                        "Date"
                )
        );

        output.append(
                "--------------------------------------------------------------------------------\n"
        );

        for (int i = 0; i < taskCount; i++) {

            output.append(
                    tasks[i].toString()
            );

            output.append("\n");
        }

        output.append(
                "================================================================================\n"
        );

        return output.toString();
    }

    // =====================================================
    // BUILD HOUSEKEEPING TASKS FROM ROOM DAO DATA
    // =====================================================

    private void loadTasksFromRoomDAO() {

        Room[] rooms = roomMap.values(new Room[roomMap.size()]);
        int sequence = 1;

        for (Room room : rooms) {
            if (room == null) {
                continue;
            }

            addTask(new HousekeepingTask(
                    String.format("T%03d", sequence),
                    room.getRoomNumber(),
                    extractFloor(room.getRoomNumber()),
                    String.format("HK%03d", ((sequence - 1) % 3) + 1),
                    parseRoomStatus(room.getStatus()),
                    LocalDate.now(),
                    LocalTime.of(9, 0).plusMinutes((sequence - 1) * 10)
            ));
            sequence++;
        }
    }

    private HousekeepingStatus parseRoomStatus(String status) {
        for (HousekeepingStatus value : HousekeepingStatus.values()) {
            if (value.toString().equalsIgnoreCase(status)) {
                return value;
            }
        }
        return HousekeepingStatus.DIRTY;
    }

    private int extractFloor(String roomNumber) {
        String digits = roomNumber.replaceAll("\\D", "");
        return digits.isEmpty() ? 0 : Character.getNumericValue(digits.charAt(0));
    }

    private void updateRoomStatus(HousekeepingTask task) {
        Room room = roomMap.get(task.getRoomNumber());
        if (room != null) {
            room.setStatus(task.getStatus().toString());
        }
    }
}