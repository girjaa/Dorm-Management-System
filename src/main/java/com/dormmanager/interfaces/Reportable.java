package com.dormmanager.interfaces;

import java.time.LocalDateTime;

/**
 * Interface for entities that can generate reports.
 * Implemented by Complaint, Room, Announcement, ForumPost, and RoomInspection.
 */
public interface Reportable {

    /**
     * Returns a formatted report string describing this entity.
     *
     * @return the report text
     */
    String getReport();

    /**
     * Returns the date/time relevant to this report (creation, last update, etc.).
     *
     * @return the report date
     */
    LocalDateTime getReportDate();
}
