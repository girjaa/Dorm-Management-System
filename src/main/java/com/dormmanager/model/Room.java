package com.dormmanager.model;

import com.dormmanager.interfaces.Manageable;
import com.dormmanager.interfaces.Reportable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a room in a dormitory building.
 * A room has a number, floor, building, capacity, and a list of occupants.
 */
public class Room implements Manageable, Reportable {

    private int roomNumber;
    private int floor;
    private String building;
    private int capacity;
    private final List<Student> occupants;

    /**
     * Constructs a new Room.
     *
     * @param roomNumber the room number
     * @param floor      the floor number
     * @param building   the building name
     * @param capacity   the maximum number of occupants
     */
    public Room(int roomNumber, int floor, String building, int capacity) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.building = building;
        this.capacity = capacity;
        this.occupants = new ArrayList<>();
    }

    // --- Manageable ---

    @Override
    public String getId() {
        return building + "-" + floor + "-" + roomNumber;
    }

    @Override
    public boolean validate() {
        return roomNumber > 0 && floor > 0
                && building != null && !building.isBlank()
                && capacity > 0;
    }

    // --- Reportable ---

    @Override
    public String getReport() {
        return String.format("Room %s | Floor %d | Building: %s | Occupants: %d/%d",
                roomNumber, floor, building, occupants.size(), capacity);
    }

    @Override
    public LocalDateTime getReportDate() {
        return LocalDateTime.now();
    }

    // --- Occupant management ---

    /**
     * Adds a student to this room if not full.
     *
     * @param student the student to add
     * @throws IllegalStateException if the room is already full
     */
    public void addOccupant(Student student) {
        if (isFull()) {
            throw new IllegalStateException("Room " + getId() + " is full (capacity: " + capacity + ")");
        }
        occupants.add(student);
    }

    /**
     * Removes a student from this room by student ID.
     *
     * @param studentId the ID of the student to remove
     * @return true if the student was removed, false if not found
     */
    public boolean removeOccupant(String studentId) {
        return occupants.removeIf(s -> s.getStudentId().equals(studentId));
    }

    public boolean isFull() {
        return occupants.size() >= capacity;
    }

    public int getOccupantCount() {
        return occupants.size();
    }

    public List<Student> getOccupants() {
        return Collections.unmodifiableList(occupants);
    }

    // --- CSV serialization ---

    /**
     * Serializes this room to a pipe-delimited CSV line.
     * Format: roomNumber|floor|building|capacity
     */
    public String toCsv() {
        return roomNumber + "|" + floor + "|" + building + "|" + capacity;
    }

    /**
     * Deserializes a room from a pipe-delimited CSV line.
     *
     * @param line the CSV line
     * @return the Room object
     */
    public static Room fromCsv(String line) {
        String[] parts = line.split("\\|");
        return new Room(
                Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[1].trim()),
                parts[2].trim(),
                Integer.parseInt(parts[3].trim())
        );
    }

    // --- Getters and setters ---

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override
    public String toString() {
        return "Room{" + getId() + ", occupants=" + occupants.size() + "/" + capacity + "}";
    }
}
