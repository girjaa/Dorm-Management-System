package com.dormmanager.model;

/**
 * Status lifecycle for complaints.
 */
public enum ComplaintStatus {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    ASSIGNED("Assigned"),
    RESOLVED("Resolved"),
    CLOSED("Closed");

    private final String displayName;

    ComplaintStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
