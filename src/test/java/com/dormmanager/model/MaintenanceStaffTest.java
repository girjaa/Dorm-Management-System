package com.dormmanager.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MaintenanceStaffTest {

    @Test
    void testConstructor() {
        MaintenanceStaff staff = new MaintenanceStaff("STF01", "Ion Popescu", "Plumbing", true);
        assertEquals("STF01", staff.getStaffId());
        assertEquals("STF01", staff.getId());
        assertEquals("Ion Popescu", staff.getName());
        assertEquals("Plumbing", staff.getSpecialization());
        assertTrue(staff.isAvailable());
        assertTrue(staff.validate());
    }

    @Test
    void testValidationFailure() {
        MaintenanceStaff invalid = new MaintenanceStaff("", "", "", false);
        assertFalse(invalid.validate());
    }

    @Test
    void testCsvSerialization() {
        MaintenanceStaff staff = new MaintenanceStaff("STF02", "Vasile Ionescu", "Electrical", false);
        String csv = staff.toCsv();
        MaintenanceStaff parsed = MaintenanceStaff.fromCsv(csv);
        assertEquals(staff.getStaffId(), parsed.getStaffId());
        assertEquals(staff.getName(), parsed.getName());
        assertEquals(staff.getSpecialization(), parsed.getSpecialization());
        assertFalse(parsed.isAvailable());
    }
}
