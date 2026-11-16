package com.dormmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {

    private Room room;

    @Test
    void testConstructor() {
        Room r = new Room(101, 1, "Building A", 2);
        assertEquals(101, r.getRoomNumber());
        assertEquals(1, r.getFloor());
        assertEquals("Building A", r.getBuilding());
        assertEquals(2, r.getCapacity());
        assertEquals("Building A-1-101", r.getId());
        assertEquals(0, r.getOccupantCount());
        assertTrue(r.validate());
    }

    @BeforeEach
    void setUp() {
        room = new Room(205, 2, "North Hall", 2);
    }

    @Test
    void testAddOccupantAndCapacity() {
        Student s1 = new Student("S101", "Alice Smith", "alice@example.com", room);
        Student s2 = new Student("S102", "Bob Jones", "bob@example.com", room);

        room.addOccupant(s1);
        assertEquals(1, room.getOccupantCount());
        assertFalse(room.isFull());

        room.addOccupant(s2);
        assertEquals(2, room.getOccupantCount());
        assertTrue(room.isFull());

        Student s3 = new Student("S103", "Charlie", "charlie@example.com", room);
        assertThrows(IllegalStateException.class, () -> room.addOccupant(s3));
    }

    @Test
    void testRemoveOccupant() {
        Student s = new Student("S200", "Diana", "diana@example.com", room);
        room.addOccupant(s);
        assertTrue(room.removeOccupant("S200"));
        assertEquals(0, room.getOccupantCount());
        assertFalse(room.removeOccupant("S999"));
    }

    @Test
    void testCsvSerialization() {
        String csv = room.toCsv();
        Room parsed = Room.fromCsv(csv);
        assertEquals(room.getRoomNumber(), parsed.getRoomNumber());
        assertEquals(room.getFloor(), parsed.getFloor());
        assertEquals(room.getBuilding(), parsed.getBuilding());
        assertEquals(room.getCapacity(), parsed.getCapacity());
    }
}
