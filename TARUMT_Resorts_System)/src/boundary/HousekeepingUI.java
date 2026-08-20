/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import control.HousekeepingController;

import entity.HousekeepingStatus;
import entity.HousekeepingTask;
import entity.StatusChange;

import java.time.LocalDate;
import java.util.Scanner;
import utility.InputHelper;

//@author Daniel Kok Wei Zen

public class HousekeepingUI {

    private HousekeepingController controller;

    private Scanner scanner;

    public HousekeepingUI() {

        this(new HousekeepingController(), new Scanner(System.in));
    }

    public HousekeepingUI(HousekeepingController controller) {
        this(controller, new Scanner(System.in));
    }

    public HousekeepingUI(HousekeepingController controller, Scanner scanner) {

        this.controller = controller;

        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;

        while (running) {
            displayMenu();

            int choice = InputHelper.readIntInRange(scanner, "Enter your choice: ", 0, 7);

            switch (choice) {

                case 1:
                    displayAllTasks();
                    break;

                case 2:
                    searchTask();
                    break;

                case 3:
                    updateStatus();
                    break;

                case 4:
                    undoLatestUpdate();
                    break;

                case 5:
                    viewLatestUpdate();
                    break;

                case 6:
                    generateRoomStatusReport();
                    break;

                case 7:
                    generateStaffPerformanceReport();
                    break;

                case 0:
                    System.out.println(
                            "Returning to main menu..."
                    );
                    running = false;
                    break;

                default:
                    System.out.println(
                            "Invalid selection."
                    );
            }
        }
    }

    private void displayMenu() {

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "       TARUMT RESORTS HOUSEKEEPING SYSTEM"
        );

        System.out.println(
                "=================================================="
        );

        System.out.println("1. View Housekeeping Tasks");

        System.out.println("2. Search Housekeeping Task");

        System.out.println("3. Update Cleaning Status");

        System.out.println("4. Undo Latest Status Update");

        System.out.println("5. View Latest Status Update");

        System.out.println("6. Generate Room Status Report");

        System.out.println("7. Generate Staff Performance Report");

        System.out.println("0. Exit");

        System.out.println(
                "=================================================="
        );
    }

    // VIEW HOUSEKEEPING TASKS
    private void displayAllTasks() {
        System.out.println(
                controller.getAllTasks()
        );
    }

    // SEARCH HOUSEKEEPING TASK
    private void searchTask() {

        String taskId = InputHelper.readNonBlankLine(scanner, "Enter Task ID: ");

        HousekeepingTask task = controller.searchTaskById(taskId);

        if (task == null) {

            System.out.println("Task not found.");

            return;
        }

        System.out.println();

        System.out.println("Task found:");

        System.out.println("Task ID : " + task.getTaskId());

        System.out.println("Room    : " + task.getRoomNumber());

        System.out.println("Floor   : " + task.getFloor());

        System.out.println("Staff   : " + task.getStaffId());

        System.out.println("Status  : " + task.getStatus());

        System.out.println("Date    : " + task.getTaskDate());
    }

    // UPDATE STATUS
    private void updateStatus() {
        System.out.println("\n--- UPDATE CLEANING STATUS ---");
        System.out.println("1. Assign Cleaning Task");
        System.out.println("2. Confirm Cleaning Completion");
        System.out.println("3. Confirm Inspection Completion");
        System.out.print("Enter choice: ");

        String input = scanner.nextLine().trim();
        int choice;
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid selection.");
            return;
        }

        switch (choice) {
            case 1:
                processCleaningAction(HousekeepingStatus.DIRTY,
                        HousekeepingStatus.CLEANING_IN_PROGRESS,
                        "ASSIGN CLEANING TASK");
                break;
            case 2:
                processCleaningAction(HousekeepingStatus.CLEANING_IN_PROGRESS,
                        HousekeepingStatus.INSPECTING,
                        "CONFIRM CLEANING TASK COMPLETION");
                break;
            case 3:
                completeInspection();
                break;
            default:
                System.out.println("Invalid selection.");
        }
    }

    private void processCleaningAction(HousekeepingStatus currentStatus, HousekeepingStatus nextStatus, String actionTitle) {
        HousekeepingTask[] tasks = controller.getTasksByStatus(currentStatus);

        System.out.println("\n--- " + actionTitle + " ---");
        if (tasks.length == 0) {
            System.out.println("No rooms are currently " + currentStatus + ".");
            return;
        }

        System.out.printf("%-10s %-10s %-8s %-10s %-25s%n",
                "Task ID", "Room", "Floor", "Staff", "Status");
        System.out.println("-----------------------------------------------------------------------");
        for (HousekeepingTask task : tasks) {
            System.out.printf("%-10s %-10s %-8d %-10s %-25s%n",
                    task.getTaskId(), task.getRoomNumber(), task.getFloor(),
                    task.getStaffId(), task.getStatus());
        }

        String roomNumber = InputHelper.readNonBlankLine(scanner, "Enter Room Number to update: ");
        HousekeepingTask selectedTask = controller.searchTaskByRoom(roomNumber);

        if (selectedTask == null || selectedTask.getStatus() != currentStatus) {
            System.out.println("Invalid room. Select a room from the displayed list.");
            return;
        }

        System.out.println(controller.updateTaskStatus(selectedTask.getTaskId(), nextStatus));
    }

    private void completeInspection() {
        HousekeepingTask[] tasks = controller.getTasksByStatus(HousekeepingStatus.INSPECTING);

        System.out.println("\n--- CONFIRM INSPECTION COMPLETION ---");
        if (tasks.length == 0) {
            System.out.println("No rooms are currently " + HousekeepingStatus.INSPECTING + ".");
            return;
        }

        System.out.printf("%-10s %-10s %-8s %-10s %-25s%n",
                "Task ID", "Room", "Floor", "Staff", "Status");
        System.out.println("-----------------------------------------------------------------------");
        for (HousekeepingTask task : tasks) {
            System.out.printf("%-10s %-10s %-8d %-10s %-25s%n",
                    task.getTaskId(), task.getRoomNumber(), task.getFloor(),
                    task.getStaffId(), task.getStatus());
        }

        String roomNumber = InputHelper.readNonBlankLine(scanner, "Enter Room Number to inspect: ");
        HousekeepingTask selectedTask = controller.searchTaskByRoom(roomNumber);

        if (selectedTask == null || selectedTask.getStatus() != HousekeepingStatus.INSPECTING) {
            System.out.println("Invalid room. Select a room from the displayed list.");
            return;
        }

        HousekeepingStatus nextStatus = readInspectionResult();
        System.out.println(controller.updateTaskStatus(selectedTask.getTaskId(), nextStatus));
    }

    private HousekeepingStatus readInspectionResult() {
        while (true) {
            String result = InputHelper.readNonBlankLine(scanner,
                    "Is the room clean after inspection? (yes/no): ").toLowerCase();

            if ("yes".equals(result) || "y".equals(result)) {
                return HousekeepingStatus.READY_FOR_CHECK_IN;
            }

            if ("no".equals(result) || "n".equals(result)) {
                return HousekeepingStatus.DIRTY;
            }

            System.out.println("Please enter yes or no.");
        }
    }

    // UNDO LATEST STATUS UPDATE
    private void undoLatestUpdate() {

        String result = controller.undoLatestStatusChange();

        System.out.println(result);
    }

    // VIEW STACK TOP
    private void viewLatestUpdate() {
        StatusChange change = controller.getLatestStatusChange();

        if (change == null) {
            System.out.println("No status update history.");

            return;
        }

        System.out.println();

        System.out.println("Latest Status Update");

        System.out.println("Task ID: " + change.getTaskId());

        System.out.println("Previous Status: " + change.getPreviousStatus());

        System.out.println("New Status: " + change.getNewStatus());

        System.out.println("Update Time: " + change.getUpdateTime());
    }

    // ROOM STATUS REPORT 
    private void generateRoomStatusReport() {

        System.out.println();

        System.out.println("ROOM STATUS REPORT FILTER");

        int floor = InputHelper.readIntInRange(scanner, "Enter Floor (0 = All): ", 0, 3);

        System.out.println();

        System.out.println(
                "Status Filter"
        );

        System.out.println(
                "0. All"
        );

        System.out.println(
                "1. Dirty"
        );

        System.out.println(
                "2. Cleaning In Progress"
        );

        System.out.println(
                "3. Inspecting"
        );

        System.out.println(
                "4. Ready for Check-In"
        );

        int statusChoice = InputHelper.readIntInRange(scanner, "Enter Status: ", 0, 4);

        HousekeepingStatus status = null;

        switch (statusChoice) {

            case 1:
                status = HousekeepingStatus.DIRTY;
                break;

            case 2:
                status = HousekeepingStatus.CLEANING_IN_PROGRESS;
                break;

            case 3:
                status = HousekeepingStatus.INSPECTING;
                break;

            case 4:
                status = HousekeepingStatus.READY_FOR_CHECK_IN;
                break;
        }

        String report = controller.generateRoomStatusReport(floor, status, null);

        System.out.println(report);
    }

    // STAFF PERFORMANCE REPORT
    private void generateStaffPerformanceReport() {

        System.out.println("\nSTAFF PERFORMANCE REPORT FILTER");
        System.out.println("0. All Room Types");
        System.out.println("1. Deluxe");
        System.out.println("2. Suite");
        System.out.println("3. Penthouse");
        System.out.print("Enter Room Type: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Invalid selection.");
            scanner.nextLine();
            return;
        }

        int roomTypeChoice = InputHelper.readIntInRange(scanner, "Enter Room Type: ", 0, 3);

        String roomTypeFilter;
        switch (roomTypeChoice) {
            case 0:
                roomTypeFilter = null;
                break;
            case 1:
                roomTypeFilter = "Deluxe";
                break;
            case 2:
                roomTypeFilter = "Suite";
                break;
            case 3:
                roomTypeFilter = "Penthouse";
                break;
            default:
                System.out.println("Invalid selection.");
                return;
        }

        String report = controller.generateStaffPerformanceReport(null, roomTypeFilter);

        System.out.println(report);
    }
}
