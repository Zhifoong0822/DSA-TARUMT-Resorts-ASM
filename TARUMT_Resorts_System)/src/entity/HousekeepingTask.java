/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author Gigabyte
 */
public class HousekeepingTask {

    private String taskId;
    private String roomNumber;
    private int floor;
    private String staffId;

    private HousekeepingStatus status;

    private LocalDate taskDate;
    private LocalTime startTime;
    private LocalTime completionTime;

    private int completionMinutes;

    public HousekeepingTask(
            String taskId,
            String roomNumber,
            int floor,
            String staffId,
            HousekeepingStatus status,
            LocalDate taskDate,
            LocalTime startTime) {

        this.taskId = taskId;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.staffId = staffId;
        this.status = status;
        this.taskDate = taskDate;
        this.startTime = startTime;

        this.completionTime = null;
        this.completionMinutes = 0;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public String getStaffId() {
        return staffId;
    }

    public HousekeepingStatus getStatus() {
        return status;
    }

    public LocalDate getTaskDate() {
        return taskDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getCompletionTime() {
        return completionTime;
    }

    public int getCompletionMinutes() {
        return completionMinutes;
    }

    public void setStatus(HousekeepingStatus status) {
        this.status = status;
    }

    public void setCompletionTime(LocalTime completionTime) {
        this.completionTime = completionTime;
    }

    public void setCompletionMinutes(int completionMinutes) {
        this.completionMinutes = completionMinutes;
    }

    @Override
    public String toString() {

        return String.format(
                "%-8s %-8s %-6d %-10s %-25s %-12s",
                taskId,
                roomNumber,
                floor,
                staffId,
                status,
                taskDate
        );
    }
}
