package com.dormmanager.model;

import com.dormmanager.interfaces.Manageable;
import com.dormmanager.interfaces.Reportable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents an anonymous complaint about a dorm room.
 * Complaints have no reference to the submitting student to preserve anonymity.
 */
public class Complaint implements Manageable, Reportable {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String complaintId;
    private String description;
    private ComplaintCategory category;
    private ComplaintStatus status;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Room room;
    private String assignedTo; // maintenance staff name, nullable

    /**
     * Constructs a new anonymous complaint.
     *
     * @param description the complaint description
     * @param category    the complaint category
     * @param priority    the priority level
     * @param room        the room the complaint is about
     */
    public Complaint(String description, ComplaintCategory category, Priority priority, Room room) {
        this.complaintId = UUID.randomUUID().toString().substring(0, 8);
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = ComplaintStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.room = room;
        this.assignedTo = null;
    }

    /** Private constructor for deserialization. */
    private Complaint() {}

    // --- Manageable ---

    @Override
    public String getId() {
        return complaintId;
    }

    @Override
    public boolean validate() {
        return description != null && !description.isBlank() && description.length() > 10
                && category != null && room != null;
    }

    // --- Reportable ---

    @Override
    public String getReport() {
        return String.format("[%s] %s | Room: %s | Category: %s | Priority: %s | Status: %s | Assigned: %s",
                complaintId, description, room.getId(), category.getDisplayName(),
                priority.getDisplayName(), status.getDisplayName(),
                assignedTo != null ? assignedTo : "Unassigned");
    }

    @Override
    public LocalDateTime getReportDate() {
        return createdAt;
    }

    // --- Actions ---

    /**
     * Updates the complaint status and records the update time.
     *
     * @param newStatus the new status
     */
    public void updateStatus(ComplaintStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Assigns the complaint to a maintenance staff member.
     *
     * @param staffName the staff member's name
     */
    public void assignTo(String staffName) {
        this.assignedTo = staffName;
        this.status = ComplaintStatus.ASSIGNED;
        this.updatedAt = LocalDateTime.now();
    }

    // --- CSV serialization ---

    /**
     * Serializes to a pipe-delimited CSV line.
     * Format: complaintId|description|category|status|priority|createdAt|updatedAt|roomId|assignedTo
     */
    public String toCsv() {
        return complaintId + "|" + description + "|" + category.name() + "|" + status.name() + "|"
                + priority.name() + "|" + FMT.format(createdAt) + "|" + FMT.format(updatedAt) + "|"
                + room.getId() + "|" + (assignedTo != null ? assignedTo : "NONE");
    }

    /**
     * Deserializes from a pipe-delimited CSV line.
     *
     * @param line the CSV line
     * @param room the Room object (resolved externally)
     * @return the Complaint object
     */
    public static Complaint fromCsv(String line, Room room) {
        String[] parts = line.split("\\|");
        Complaint c = new Complaint();
        c.complaintId = parts[0].trim();
        c.description = parts[1].trim();
        c.category = ComplaintCategory.valueOf(parts[2].trim());
        c.status = ComplaintStatus.valueOf(parts[3].trim());
        c.priority = Priority.valueOf(parts[4].trim());
        c.createdAt = LocalDateTime.parse(parts[5].trim(), FMT);
        c.updatedAt = LocalDateTime.parse(parts[6].trim(), FMT);
        c.room = room;
        c.assignedTo = parts[8].trim().equals("NONE") ? null : parts[8].trim();
        return c;
    }

    // --- Getters and setters ---

    public String getComplaintId() { return complaintId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }

    public ComplaintStatus getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public String getAssignedTo() { return assignedTo; }

    @Override
    public String toString() {
        return "Complaint{id='" + complaintId + "', status=" + status + ", category=" + category + "}";
    }
}
