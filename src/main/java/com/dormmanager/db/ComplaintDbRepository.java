package com.dormmanager.db;

import com.dormmanager.model.*;
import com.dormmanager.validation.InputValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Repository for Anonymous Complaints in MySQL.
 * Satisfies Requirement 6 (Query via JDBC) and Requirement 7 (Insert/Update via JDBC).
 */
public class ComplaintDbRepository {

    private final DatabaseManager dbManager;
    private final RoomDbRepository roomRepo;

    public ComplaintDbRepository(DatabaseManager dbManager, RoomDbRepository roomRepo) {
        this.dbManager = dbManager;
        this.roomRepo = roomRepo;
    }

    /**
     * Inserts an anonymous complaint into MySQL.
     * Note: student identity is never passed or stored.
     */
    public boolean insert(Complaint complaint) throws SQLException {
        InputValidator.validateComplaintDescription(complaint.getDescription());

        String sql = "INSERT INTO complaints (complaint_id, description, category, status, priority, room_id, assigned_to, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, complaint.getComplaintId());
            ps.setString(2, complaint.getDescription());
            ps.setString(3, complaint.getCategory().name());
            ps.setString(4, complaint.getStatus().name());
            ps.setString(5, complaint.getPriority().name());
            ps.setString(6, complaint.getRoom().getId());
            ps.setString(7, complaint.getAssignedTo());
            ps.setTimestamp(8, Timestamp.valueOf(complaint.getCreatedAt()));
            ps.setTimestamp(9, Timestamp.valueOf(complaint.getUpdatedAt()));

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Queries all complaints from MySQL.
     */
    public List<Complaint> findAll() throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT complaint_id, description, category, status, priority, room_id, assigned_to, created_at, updated_at " +
                     "FROM complaints ORDER BY created_at DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String roomId = rs.getString("room_id");
                Room room = roomRepo.findById(roomId);
                if (room == null) {
                    room = new Room(101, 1, "Unknown Building", 2);
                }

                Complaint c = new Complaint(
                        rs.getString("description"),
                        ComplaintCategory.valueOf(rs.getString("category")),
                        Priority.valueOf(rs.getString("priority")),
                        room
                );

                // Reassign database ID and state
                String assigned = rs.getString("assigned_to");
                if (assigned != null) {
                    c.assignTo(assigned);
                }
                c.updateStatus(ComplaintStatus.valueOf(rs.getString("status")));
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Updates complaint status in MySQL.
     */
    public boolean updateStatus(String complaintId, ComplaintStatus newStatus) throws SQLException {
        String sql = "UPDATE complaints SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE complaint_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.name());
            ps.setString(2, complaintId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Assigns maintenance staff to a complaint in MySQL.
     */
    public boolean assignStaff(String complaintId, String staffName) throws SQLException {
        InputValidator.validateNotEmpty(staffName, "Staff name");

        String sql = "UPDATE complaints SET assigned_to = ?, status = 'ASSIGNED', updated_at = CURRENT_TIMESTAMP WHERE complaint_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, staffName);
            ps.setString(2, complaintId);

            return ps.executeUpdate() > 0;
        }
    }
}
