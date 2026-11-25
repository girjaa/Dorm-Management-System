package com.dormmanager.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class RoomInspectionTest {

    @Test
    void testConstructor() {
        Room room = new Room(101, 1, "Building A", 2);
        LocalDateTime date = LocalDateTime.now().plusDays(2);
        RoomInspection ri = new RoomInspection(room, date, "Inspector Popa");

        assertNotNull(ri.getInspectionId());
        assertEquals("Building A-1-101", ri.getRoom().getId());
        assertEquals(date, ri.getScheduledDate());
        assertEquals("Inspector Popa", ri.getInspectorName());
        assertEquals(InspectionStatus.SCHEDULED, ri.getStatus());
        assertNull(ri.getCompletedDate());
        assertTrue(ri.validate());
    }

    @Test
    void testCompleteAndCancel() {
        Room room = new Room(102, 1, "Building A", 2);
        RoomInspection ri = new RoomInspection(room, LocalDateTime.now().plusDays(1), "Inspector Radu");

        ri.complete("Fire detector checked and working");
        assertEquals(InspectionStatus.COMPLETED, ri.getStatus());
        assertEquals("Fire detector checked and working", ri.getNotes());
        assertNotNull(ri.getCompletedDate());

        ri.cancel();
        assertEquals(InspectionStatus.CANCELLED, ri.getStatus());
    }

    @Test
    void testCsvSerialization() {
        Room room = new Room(201, 2, "Building B", 3);
        LocalDateTime date = LocalDateTime.of(2026, 12, 1, 10, 0);
        RoomInspection ri = new RoomInspection(room, date, "Inspector Stan");
        String csv = ri.toCsv();

        RoomInspection parsed = RoomInspection.fromCsv(csv, room);
        assertEquals(ri.getInspectionId(), parsed.getInspectionId());
        assertEquals("Building B-2-201", parsed.getRoom().getId());
        assertEquals(date, parsed.getScheduledDate());
        assertEquals("Inspector Stan", parsed.getInspectorName());
        assertEquals(InspectionStatus.SCHEDULED, parsed.getStatus());
    }
}
