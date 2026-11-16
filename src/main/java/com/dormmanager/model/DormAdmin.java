package com.dormmanager.model;

import com.dormmanager.interfaces.Manageable;

/**
 * Represents a dormitory administrator.
 */
public class DormAdmin implements Manageable {

    private String adminId;
    private String name;
    private String email;
    private String department;

    public DormAdmin(String adminId, String name, String email, String department) {
        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    @Override
    public String getId() { return adminId; }

    @Override
    public boolean validate() {
        return adminId != null && !adminId.isBlank()
                && name != null && !name.isBlank();
    }

    public String toCsv() {
        return adminId + "|" + name + "|" + email + "|" + department;
    }

    public static DormAdmin fromCsv(String line) {
        String[] parts = line.split("\\|");
        return new DormAdmin(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
    }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "DormAdmin{id='" + adminId + "', name='" + name + "'}";
    }
}
