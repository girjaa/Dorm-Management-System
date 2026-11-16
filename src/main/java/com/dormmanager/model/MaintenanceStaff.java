package com.dormmanager.model;

import com.dormmanager.interfaces.Manageable;

/**
 * Represents a maintenance staff member who can be assigned to complaints.
 */
public class MaintenanceStaff implements Manageable {

    private String staffId;
    private String name;
    private String specialization; // e.g. "Plumbing", "Electrical", "General"
    private boolean available;

    public MaintenanceStaff(String staffId, String name, String specialization, boolean available) {
        this.staffId = staffId;
        this.name = name;
        this.specialization = specialization;
        this.available = available;
    }

    @Override
    public String getId() { return staffId; }

    @Override
    public boolean validate() {
        return staffId != null && !staffId.isBlank()
                && name != null && !name.isBlank()
                && specialization != null && !specialization.isBlank();
    }

    public String toCsv() {
        return staffId + "|" + name + "|" + specialization + "|" + available;
    }

    public static MaintenanceStaff fromCsv(String line) {
        String[] parts = line.split("\\|");
        return new MaintenanceStaff(parts[0].trim(), parts[1].trim(),
                parts[2].trim(), Boolean.parseBoolean(parts[3].trim()));
    }

    public String getStaffId() { return staffId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "MaintenanceStaff{id='" + staffId + "', name='" + name +
                "', spec='" + specialization + "', available=" + available + "}";
    }
}
