package com.dormmanager.service;

import com.dormmanager.model.InspectionStatus;
import com.dormmanager.model.Room;
import com.dormmanager.model.RoomInspection;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for scheduling and managing room inspections.
 */
public class InspectionService {

    private static final String INSPECTIONS_FILE = "inspections.dat";

    private final List<RoomInspection> inspections;
    private final FileStorageService fileStorage;

    public InspectionService(FileStorageService fileStorage) {
        this.inspections = new ArrayList<>();
        this.fileStorage = fileStorage;
    }

    public RoomInspection scheduleInspection(Room room, LocalDateTime date, String inspectorName) {
        if (room == null) throw new IllegalArgumentException("Room cannot be null.");
        if (date == null) throw new IllegalArgumentException("Date cannot be null.");
        if (inspectorName == null || inspectorName.isBlank())
            throw new IllegalArgumentException("Inspector name cannot be empty.");

        RoomInspection inspection = new RoomInspection(room, date, inspectorName);
        inspections.add(inspection);
        return inspection;
    }

    public boolean completeInspection(String inspectionId, String notes) {
        for (RoomInspection ri : inspections) {
            if (ri.getInspectionId().equals(inspectionId)) {
                ri.complete(notes);
                return true;
            }
        }
        return false;
    }

    public boolean cancelInspection(String inspectionId) {
        for (RoomInspection ri : inspections) {
            if (ri.getInspectionId().equals(inspectionId)) {
                ri.cancel();
                return true;
            }
        }
        return false;
    }

    public List<RoomInspection> getUpcomingInspections() {
        LocalDateTime now = LocalDateTime.now();
        return inspections.stream()
                .filter(ri -> ri.getStatus() == InspectionStatus.SCHEDULED
                        && ri.getScheduledDate().isAfter(now))
                .sorted(Comparator.comparing(RoomInspection::getScheduledDate))
                .collect(Collectors.toList());
    }

    public List<RoomInspection> getInspectionsByRoom(Room room) {
        return inspections.stream()
                .filter(ri -> ri.getRoom().getId().equals(room.getId()))
                .collect(Collectors.toList());
    }

    public List<RoomInspection> getAllInspections() {
        return Collections.unmodifiableList(inspections);
    }

    public void saveAll() {
        List<String> lines = inspections.stream().map(RoomInspection::toCsv).collect(Collectors.toList());
        fileStorage.saveToFile(INSPECTIONS_FILE, lines);
    }

    public void loadAll(Map<String, Room> roomMap) {
        inspections.clear();
        List<String> lines = fileStorage.loadFromFile(INSPECTIONS_FILE);
        for (String line : lines) {
            if (!line.isBlank()) {
                String[] parts = line.split("\\|");
                String roomId = parts.length > 1 ? parts[1].trim() : "";
                Room room = roomMap.get(roomId);
                if (room != null) {
                    inspections.add(RoomInspection.fromCsv(line, room));
                }
            }
        }
    }
}
