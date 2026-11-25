package com.dormmanager.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DormAdminTest {

    @Test
    void testConstructor() {
        DormAdmin admin = new DormAdmin("ADM01", "Radu Popescu", "radu.admin@dorm.ro", "Facilities");
        assertEquals("ADM01", admin.getAdminId());
        assertEquals("ADM01", admin.getId());
        assertEquals("Radu Popescu", admin.getName());
        assertEquals("radu.admin@dorm.ro", admin.getEmail());
        assertEquals("Facilities", admin.getDepartment());
        assertTrue(admin.validate());
    }

    @Test
    void testValidationFailure() {
        DormAdmin invalid = new DormAdmin("", "", "", "");
        assertFalse(invalid.validate());
    }

    @Test
    void testCsvSerialization() {
        DormAdmin admin = new DormAdmin("ADM02", "Maria Enache", "maria@dorm.ro", "Student Affairs");
        String csv = admin.toCsv();
        DormAdmin parsed = DormAdmin.fromCsv(csv);
        assertEquals(admin.getAdminId(), parsed.getAdminId());
        assertEquals(admin.getName(), parsed.getName());
        assertEquals(admin.getEmail(), parsed.getEmail());
        assertEquals(admin.getDepartment(), parsed.getDepartment());
    }
}
