package com.dormmanager.interfaces;

/**
 * Interface for entities that can be managed (identified and validated).
 * Implemented by Student, Room, Complaint, DormAdmin, MaintenanceStaff, RoomInspection.
 */
public interface Manageable {

    /**
     * Returns the unique identifier for this entity.
     *
     * @return the entity ID
     */
    String getId();

    /**
     * Validates the entity's state.
     *
     * @return true if the entity is in a valid state, false otherwise
     */
    boolean validate();
}
