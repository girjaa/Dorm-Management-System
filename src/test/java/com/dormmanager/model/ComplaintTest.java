package com.dormmanager.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ComplaintTest {

    @Test
    void testConstructor() {
        Room r = new Room(301, 3, "East Block", 2);
        Complaint c = new Complaint("The bathroom sink is clogged and water leaks onto the floor",
                ComplaintCategory.PLUMBING, Priority.HIGH, r);

        assertNotNull(c.getComplaintId());
        assertEquals("The bathroom sink is clogged and water leaks onto the floor", c.getDescription());
        assertEquals(ComplaintCategory.PLUMBING, c.getCategory());
        assertEquals(Priority.HIGH, c.getPriority());
        assertEquals(ComplaintStatus.OPEN, c.getStatus());
        assertEquals(r, c.getRoom());
        assertNull(c.getAssignedTo());
        assertNotNull(c.getCreatedAt());
        assertTrue(c.validate());
    }

    @Test
    void testUpdateStatusAndAssign() {
        Room r = new Room(301, 3, "East Block", 2);
        Complaint c = new Complaint("Ceiling is leaking when upper floor uses shower",
                ComplaintCategory.PLUMBING, Priority.URGENT, r);

        c.assignTo("Ion Popescu");
        assertEquals("Ion Popescu", c.getAssignedTo());
        assertEquals(ComplaintStatus.ASSIGNED, c.getStatus());

        c.updateStatus(ComplaintStatus.RESOLVED);
        assertEquals(ComplaintStatus.RESOLVED, c.getStatus());
    }

    @Test
    void testCsvSerialization() {
        Room r = new Room(301, 3, "East Block", 2);
        Complaint c = new Complaint("Heating radiator is making loud banging noises constantly",
                ComplaintCategory.HEATING_COOLING, Priority.NORMAL, r);
        c.assignTo("Alex Radu");

        String csv = c.toCsv();
        Complaint parsed = Complaint.fromCsv(csv, r);

        assertEquals(c.getComplaintId(), parsed.getComplaintId());
        assertEquals(c.getDescription(), parsed.getDescription());
        assertEquals(c.getCategory(), parsed.getCategory());
        assertEquals(c.getStatus(), parsed.getStatus());
        assertEquals(c.getPriority(), parsed.getPriority());
        assertEquals("Alex Radu", parsed.getAssignedTo());
        assertEquals(r.getId(), parsed.getRoom().getId());
    }
}
