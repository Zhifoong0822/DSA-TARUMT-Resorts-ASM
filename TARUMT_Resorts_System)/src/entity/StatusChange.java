/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDateTime;
/**
 *
 * @author Gigabyte
 */
public class StatusChange {

    private int taskIndex;

    private String taskId;

    private HousekeepingStatus previousStatus;
    private HousekeepingStatus newStatus;

    private LocalDateTime updateTime;

    public StatusChange(
            int taskIndex,
            String taskId,
            HousekeepingStatus previousStatus,
            HousekeepingStatus newStatus) {

        this.taskIndex = taskIndex;
        this.taskId = taskId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;

        this.updateTime = LocalDateTime.now();
    }

    public int getTaskIndex() {
        return taskIndex;
    }

    public String getTaskId() {
        return taskId;
    }

    public HousekeepingStatus getPreviousStatus() {
        return previousStatus;
    }

    public HousekeepingStatus getNewStatus() {
        return newStatus;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    @Override
    public String toString() {

        return taskId
                + ": "
                + previousStatus
                + " -> "
                + newStatus
                + " at "
                + updateTime;
    }
}