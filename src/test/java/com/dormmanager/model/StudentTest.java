package com.dormmanager.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {

    @Test
    void testConstructor() {
        Room r = new Room(101, 1, "West Wing", 2);
        Student s = new Student("ST001", "Jane Doe", "jane@example.com", r);
        assertEquals("ST001", s.getStudentId());
        assertEquals("ST001", s.getId());
        assertEquals("Jane Doe", s.getName());
        assertEquals("jane@example.com", s.getEmail());
        assertEquals(r, s.getRoom());
        assertTrue(s.validate());
    }

    @Test
    void testValidationFailure() {
        Student invalid = new Student("", "", "", null);
        assertFalse(invalid.validate());
    }

    @Test
    void testCsvSerialization() {
        Room r = new Room(102, 1, "West Wing", 2);
        Student s = new Student("ST002", "Alex Taylor", "alex@example.com", r);
        String csv = s.toCsv();
        Student parsed = Student.fromCsv(csv, r);
        assertEquals("ST002", parsed.getStudentId());
        assertEquals("Alex Taylor", parsed.getName());
        assertEquals("alex@example.com", parsed.getEmail());
        assertEquals("West Wing-1-102", parsed.getRoom().getId());
    }
}
