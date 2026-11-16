package com.dormmanager.service;

import com.dormmanager.exception.InvalidRoomException;
import com.dormmanager.model.Room;
import com.dormmanager.validation.InputValidator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing dorm rooms.
 */
public class RoomService {

    private static final String ROOMS_FILE = "rooms.dat";

    private final List<Room> rooms;
    private final FileStorageService fileStorage;

    public RoomService(FileStorageService fileStorage) {
        this.rooms = new ArrayList<>();
        this.fileStorage = fileStorage;
    }

    /**
     * Adds a new room to the system.
     *
     * @throws InvalidRoomException if validation fails
     */
    public Room addRoom(int roomNumber, int floor, String building, int capacity) {
        InputValidator.validatePositiveNumber(roomNumber, "Room number");
        InputValidator.validatePositiveNumber(floor, "Floor");
        InputValidator.validateNotEmpty(building, "Building");
        InputValidator.validatePositiveNumber(capacity, "Capacity");

        Room room = new Room(roomNumber, floor, building, capacity);
        if (!room.validate()) {
            throw new InvalidRoomException("Room validation failed for: " + room.getId());
        }

        // Check for duplicates
        if (findRoomById(room.getId()) != null) {
            throw new InvalidRoomException("Room already exists: " + room.getId());
        }

        rooms.add(room);
        return room;
    }

    /**
     * Finds a room by building, floor, and room number.
     */
    public Room findRoom(String building, int floor, int roomNumber) {
        String id = building + "-" + floor + "-" + roomNumber;
        return findRoomById(id);
    }

    /**
     * Finds a room by its composite ID.
     */
    public Room findRoomById(String roomId) {
        return rooms.stream()
                .filter(r -> r.getId().equals(roomId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns all rooms that are not full.
     */
    public List<Room> getAvailableRooms() {
        return rooms.stream()
                .filter(r -> !r.isFull())
                .collect(Collectors.toList());
    }

    public List<Room> getAllRooms() {
        return Collections.unmodifiableList(rooms);
    }

    /**
     * Returns a map of room IDs to Room objects.
     */
    public Map<String, Room> getRoomMap() {
        Map<String, Room> map = new HashMap<>();
        for (Room r : rooms) {
            map.put(r.getId(), r);
        }
        return map;
    }

    public void saveAll() {
        List<String> lines = rooms.stream().map(Room::toCsv).collect(Collectors.toList());
        fileStorage.saveToFile(ROOMS_FILE, lines);
    }

    public void loadAll() {
        rooms.clear();
        List<String> lines = fileStorage.loadFromFile(ROOMS_FILE);
        for (String line : lines) {
            if (!line.isBlank()) {
                rooms.add(Room.fromCsv(line));
            }
        }
    }
}
