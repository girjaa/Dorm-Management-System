package com.dormmanager.model;

import com.dormmanager.interfaces.Manageable;
import com.dormmanager.interfaces.Reportable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a scheduled room inspection.
 */
public class RoomInspection implements Manageable, Reportable {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String inspectionId;
    private Room room;
    private LocalDateTime scheduledDate;
    private LocalDateTime completedDate; // nullable
    private String inspectorName;
    private String notes;
    private InspectionStatus status;

    public RoomInspection(Room room, LocalDateTime scheduledDate, String inspectorName) {
        this.inspectionId = UUID.randomUUID().toString().substring(0, 8);
        this.room = room;
        this.scheduledDate = scheduledDate;
        this.inspectorName = inspectorName;
        this.notes = "";
        this.status = InspectionStatus.SCHEDULED;
        this.completedDate = null;
    }

    private RoomInspection() {}

    @Override
    public String getId() { return inspectionId; }

    @Override
    public boolean validate() {
        return room != null && scheduledDate != null
                && inspectorName != null && !inspectorName.isBlank();
    }

    @Override
    public String getReport() {
        return String.format("[%s] Room %s | Scheduled: %s | Inspector: %s | Status: %s",
                inspectionId, room.getId(), FMT.format(scheduledDate),
                inspectorName, status.getDisplayName());
    }

    @Override
    public LocalDateTime getReportDate() {
        return scheduledDate;
    }

    public void complete(String notes) {
        this.notes = notes;
        this.status = InspectionStatus.COMPLETED;
        this.completedDate = LocalDateTime.now();
    }

    public void cancel() {
        this.status = InspectionStatus.CANCELLED;
    }

    public String toCsv() {
        return inspectionId + "|" + room.getId() + "|" + FMT.format(scheduledDate) + "|"
                + (completedDate != null ? FMT.format(completedDate) : "NONE") + "|"
                + inspectorName + "|" + notes + "|" + status.name();
    }

    public static RoomInspection fromCsv(String line, Room room) {
        String[] parts = line.split("\\|");
        RoomInspection ri = new RoomInspection();
        ri.inspectionId = parts[0].trim();
        ri.room = room;
        ri.scheduledDate = LocalDateTime.parse(parts[2].trim(), FMT);
        ri.completedDate = parts[3].trim().equals("NONE") ? null : LocalDateTime.parse(parts[3].trim(), FMT);
        ri.inspectorName = parts[4].trim();
        ri.notes = parts[5].trim();
        ri.status = InspectionStatus.valueOf(parts[6].trim());
        return ri;
    }

    public String getInspectionId() { return inspectionId; }
    public Room getRoom() { return room; }
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public LocalDateTime getCompletedDate() { return completedDate; }
    public String getInspectorName() { return inspectorName; }
    public String getNotes() { return notes; }
    public InspectionStatus getStatus() { return status; }

    @Override
    public String toString() {
        return "RoomInspection{id='" + inspectionId + "', room=" + room.getId() +
                ", status=" + status + "}";
    }
}
