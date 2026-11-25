package com.dormmanager.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class AnnouncementTest {

    @Test
    void testConstructor() {
        Announcement a = new Announcement("Quiet Hours", "Enforced from 22:00", "Admin", "Building A");
        assertNotNull(a.getAnnouncementId());
        assertEquals("Quiet Hours", a.getTitle());
        assertEquals("Enforced from 22:00", a.getMessage());
        assertEquals("Admin", a.getPostedBy());
        assertEquals("Building A", a.getTargetBuilding());
        assertNotNull(a.getPostedAt());
        assertNotNull(a.getReportDate());
        assertTrue(a.getReport().contains("Quiet Hours"));
    }

    @Test
    void testCsvSerialization() {
        Announcement a = new Announcement("Notice", "Inspection tomorrow", "Admin", null);
        String csv = a.toCsv();
        Announcement parsed = Announcement.fromCsv(csv);
        assertEquals(a.getAnnouncementId(), parsed.getAnnouncementId());
        assertEquals(a.getTitle(), parsed.getTitle());
        assertEquals(a.getMessage(), parsed.getMessage());
        assertEquals(a.getPostedBy(), parsed.getPostedBy());
        assertNull(parsed.getTargetBuilding());
    }
}
