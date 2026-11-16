package com.dormmanager.model;

/**
 * Categories for dorm complaints.
 */
public enum ComplaintCategory {
    PLUMBING("Plumbing Issue"),
    ELECTRICAL("Electrical Issue"),
    STRUCTURAL("Structural Damage"),
    PEST_CONTROL("Pest Control"),
    NOISE("Noise Complaint"),
    HEATING_COOLING("Heating/Cooling Issue"),
    CLEANING("Cleaning Required"),
    OTHER("Other");

    private final String displayName;

    ComplaintCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parses a string to a ComplaintCategory (case-insensitive).
     *
     * @param value the string value
     * @return the matching category
     * @throws IllegalArgumentException if no match
     */
    public static ComplaintCategory fromString(String value) {
        for (ComplaintCategory cat : values()) {
            if (cat.name().equalsIgnoreCase(value) || cat.displayName.equalsIgnoreCase(value)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Unknown complaint category: " + value);
    }
}
