package com.dormmanager.service;

import com.dormmanager.model.*;
import com.dormmanager.validation.InputValidator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing anonymous complaints.
 * Complaints contain no student references to preserve anonymity.
 */
public class ComplaintService {

    private static final String COMPLAINTS_FILE = "complaints.dat";

    private final List<Complaint> complaints;
    private final FileStorageService fileStorage;

    public ComplaintService(FileStorageService fileStorage) {
        this.complaints = new ArrayList<>();
        this.fileStorage = fileStorage;
    }

    /**
     * Submits an anonymous complaint.
     */
    public Complaint submitAnonymousComplaint(String description, ComplaintCategory category,
                                               Priority priority, Room room) {
        InputValidator.validateComplaintDescription(description);
        if (category == null) throw new IllegalArgumentException("Category cannot be null.");
        if (room == null) throw new IllegalArgumentException("Room cannot be null.");
        if (priority == null) priority = Priority.NORMAL;

        Complaint complaint = new Complaint(
                InputValidator.sanitizeInput(description), category, priority, room);
        complaints.add(complaint);
        return complaint;
    }

    public List<Complaint> getAllComplaints() {
        return Collections.unmodifiableList(complaints);
    }

    public List<Complaint> getComplaintsByStatus(ComplaintStatus status) {
        return complaints.stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Complaint> getComplaintsByCategory(ComplaintCategory category) {
        return complaints.stream()
                .filter(c -> c.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Complaint> getComplaintsByRoom(Room room) {
        return complaints.stream()
                .filter(c -> c.getRoom().getId().equals(room.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Updates the status of a complaint by ID.
     */
    public boolean updateComplaintStatus(String complaintId, ComplaintStatus newStatus) {
        for (Complaint c : complaints) {
            if (c.getComplaintId().equals(complaintId)) {
                c.updateStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    /**
     * Assigns a complaint to a maintenance staff member.
     */
    public boolean assignComplaint(String complaintId, String staffName) {
        for (Complaint c : complaints) {
            if (c.getComplaintId().equals(complaintId)) {
                c.assignTo(staffName);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all complaints sorted by creation date (newest first).
     */
    public List<Complaint> getComplaintHistory() {
        return complaints.stream()
                .sorted(Comparator.comparing(Complaint::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public void saveAll() {
        List<String> lines = complaints.stream().map(Complaint::toCsv).collect(Collectors.toList());
        fileStorage.saveToFile(COMPLAINTS_FILE, lines);
    }

    public void loadAll(Map<String, Room> roomMap) {
        complaints.clear();
        List<String> lines = fileStorage.loadFromFile(COMPLAINTS_FILE);
        for (String line : lines) {
            if (!line.isBlank()) {
                String[] parts = line.split("\\|");
                String roomId = parts.length > 7 ? parts[7].trim() : "";
                Room room = roomMap.get(roomId);
                if (room != null) {
                    complaints.add(Complaint.fromCsv(line, room));
                }
            }
        }
    }
}
