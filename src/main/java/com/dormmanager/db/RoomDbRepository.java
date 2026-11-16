package com.dormmanager.db;

import com.dormmanager.model.Room;
import com.dormmanager.validation.InputValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Repository for Room entities in MySQL.
 */
public class RoomDbRepository {

    private final DatabaseManager dbManager;

    public RoomDbRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<Room> findAll() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, room_number, floor, building, capacity FROM rooms ORDER BY building, floor, room_number";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room r = new Room(
                        rs.getInt("room_number"),
                        rs.getInt("floor"),
                        rs.getString("building"),
                        rs.getInt("capacity")
                );
                rooms.add(r);
            }
        }
        return rooms;
    }

    public Room findById(String roomId) throws SQLException {
        String sql = "SELECT room_id, room_number, floor, building, capacity FROM rooms WHERE room_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Room(
                            rs.getInt("room_number"),
                            rs.getInt("floor"),
                            rs.getString("building"),
                            rs.getInt("capacity")
                    );
                }
            }
        }
        return null;
    }

    public boolean insert(Room room) throws SQLException {
        InputValidator.validatePositiveNumber(room.getRoomNumber(), "Room number");
        InputValidator.validatePositiveNumber(room.getFloor(), "Floor");
        InputValidator.validateNotEmpty(room.getBuilding(), "Building");
        InputValidator.validatePositiveNumber(room.getCapacity(), "Capacity");

        String sql = "INSERT INTO rooms (room_id, room_number, floor, building, capacity) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getId());
            ps.setInt(2, room.getRoomNumber());
            ps.setInt(3, room.getFloor());
            ps.setString(4, room.getBuilding());
            ps.setInt(5, room.getCapacity());

            return ps.executeUpdate() > 0;
        }
    }
}
