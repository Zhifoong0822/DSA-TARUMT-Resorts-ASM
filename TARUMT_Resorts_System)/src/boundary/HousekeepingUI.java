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
/**
 *
 * @author Gigabyte
 */
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

        int choice;

        do {

            displayMenu();

            System.out.print(
                    "Enter your choice: "
            );

            while (!scanner.hasNextInt()) {

                System.out.println(
                        "Invalid input."
                );

                scanner.next();

                System.out.print(
                        "Enter your choice: "
                );
            }

            choice
                    = scanner.nextInt();

            scanner.nextLine();

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
                    handleCompletedLateCheckOut();
                    break;

                case 6:
                    viewLatestUpdate();
                    break;

                case 7:
                    generateRoomStatusReport();
                    break;

                case 8:
                    generateStaffPerformanceReport();
                    break;

                case 0:
                    System.out.println(
                            "Returning to main menu..."
                    );
                    break;

                default:
                    System.out.println(
                            "Invalid selection."
                    );
            }

        } while (choice != 0);
    }

    // =====================================================
    // MENU
    // =====================================================

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

        System.out.println(
                "1. View Housekeeping Tasks"
        );

        System.out.println(
                "2. Search Housekeeping Task"
        );

        System.out.println(
                "3. Update Cleaning Status"
        );

        System.out.println(
                "4. Undo Latest Status Update"
        );

        System.out.println(
                "5. Check Late Check-Out Status"
        );

        System.out.println(
                "6. View Latest Status Update"
        );

        System.out.println(
                "7. Generate Room Status Report"
        );

        System.out.println(
                "8. Generate Staff Performance Report"
        );

        System.out.println(
                "0. Exit"
        );

        System.out.println(
                "=================================================="
        );
    }

    private void handleCompletedLateCheckOut() {

        System.out.print("Enter room number: ");

        String roomNumber = scanner.nextLine().trim();

        if (roomNumber.isEmpty()) {
            System.out.println("Room number cannot be blank.");
            return;
        }

        System.out.println(
                controller.handleCompletedLateCheckOut(roomNumber)
        );
    }

    // =====================================================
    // VIEW TASKS
    // =====================================================

    private void displayAllTasks() {

        System.out.println(
                controller.getAllTasks()
        );
    }

    // =====================================================
    // SEARCH
    // =====================================================

    private void searchTask() {

        System.out.print(
                "Enter Task ID: "
        );

        String taskId
                = scanner.nextLine();

        HousekeepingTask task
                = controller.searchTaskById(taskId);

        if (task == null) {

            System.out.println(
                    "Task not found."
            );

            return;
        }

        System.out.println();

        System.out.println(
                "Task found:"
        );

        System.out.println(
                "Task ID : "
                + task.getTaskId()
        );

        System.out.println(
                "Room    : "
                + task.getRoomNumber()
        );

        System.out.println(
                "Floor   : "
                + task.getFloor()
        );

        System.out.println(
                "Staff   : "
                + task.getStaffId()
        );

        System.out.println(
                "Status  : "
                + task.getStatus()
        );

        System.out.println(
                "Date    : "
                + task.getTaskDate()
        );
    }

    // =====================================================
    // UPDATE STATUS
    // =====================================================

    private void updateStatus() {
        System.out.println("\n--- UPDATE CLEANING STATUS ---");
        System.out.println("1. Assign Cleaning Task");
        System.out.println("2. Confirm Cleaning Completion");
        System.out.println("3. Inspection Done");
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
                        HousekeepingStatus.INSPECTED,
                        "CONFIRM CLEANING COMPLETION");
                break;
            case 3:
                processCleaningAction(HousekeepingStatus.INSPECTED,
                        HousekeepingStatus.READY_FOR_CHECK_IN,
                        "INSPECTION DONE");
                break;
            default:
                System.out.println("Invalid selection.");
        }
    }

    private void processCleaningAction(HousekeepingStatus currentStatus,
            HousekeepingStatus nextStatus, String actionTitle) {
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

        System.out.print("Enter room number to update: ");
        String roomNumber = scanner.nextLine().trim();
        HousekeepingTask selectedTask = controller.searchTaskByRoom(roomNumber);

        if (selectedTask == null || selectedTask.getStatus() != currentStatus) {
            System.out.println("Invalid room. Select a room from the displayed list.");
            return;
        }

        System.out.println(controller.updateTaskStatus(selectedTask.getTaskId(), nextStatus));
    }

    // =====================================================
    // UNDO
    // =====================================================

    private void undoLatestUpdate() {

        String result
                = controller
                        .undoLatestStatusChange();

        System.out.println(result);
    }

    // =====================================================
    // VIEW STACK TOP
    // =====================================================

    private void viewLatestUpdate() {

        StatusChange change
                = controller
                        .getLatestStatusChange();

        if (change == null) {

            System.out.println(
                    "No status update history."
            );

            return;
        }

        System.out.println();

        System.out.println(
                "Latest Status Update"
        );

        System.out.println(
                "Task ID: "
                + change.getTaskId()
        );

        System.out.println(
                "Previous Status: "
                + change.getPreviousStatus()
        );

        System.out.println(
                "New Status: "
                + change.getNewStatus()
        );

        System.out.println(
                "Update Time: "
                + change.getUpdateTime()
        );
    }

    // =====================================================
    // REPORT 1
    // =====================================================

    private void generateRoomStatusReport() {

        System.out.println();

        System.out.println(
                "ROOM STATUS REPORT FILTER"
        );

        System.out.print(
                "Enter Floor (0 = All): "
        );

        int floor
                = scanner.nextInt();

        scanner.nextLine();

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
                "3. Inspected"
        );

        System.out.println(
                "4. Ready for Check-In"
        );

        System.out.print(
                "Enter Status: "
        );

        int statusChoice
                = scanner.nextInt();

        scanner.nextLine();

        HousekeepingStatus status
                = null;

        switch (statusChoice) {

            case 1:
                status
                        = HousekeepingStatus.DIRTY;
                break;

            case 2:
                status
                        = HousekeepingStatus.CLEANING_IN_PROGRESS;
                break;

            case 3:
                status
                        = HousekeepingStatus.INSPECTED;
                break;

            case 4:
                status
                        = HousekeepingStatus.READY_FOR_CHECK_IN;
                break;
        }

        String report
                = controller
                        .generateRoomStatusReport(
                                floor,
                                status,
                                null
                        );

        System.out.println(report);
    }

    // =====================================================
    // REPORT 2
    // =====================================================

    private void generateStaffPerformanceReport() {

        /*
         * null means all dates.
         * You can later allow the user to enter a date.
         */

        String report
                = controller
                        .generateStaffPerformanceReport(
                                null
                        );

        System.out.println(report);
    }
}
