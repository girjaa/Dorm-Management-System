package com.dormmanager.model;

import com.dormmanager.interfaces.Reportable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents an announcement posted by an admin to dorm residents.
 */
public class Announcement implements Reportable {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String announcementId;
    private String title;
    private String message;
    private String postedBy;
    private LocalDateTime postedAt;
    private String targetBuilding; // null means all buildings

    public Announcement(String title, String message, String postedBy, String targetBuilding) {
        this.announcementId = UUID.randomUUID().toString().substring(0, 8);
        this.title = title;
        this.message = message;
        this.postedBy = postedBy;
        this.postedAt = LocalDateTime.now();
        this.targetBuilding = targetBuilding;
    }

    private Announcement() {}

    @Override
    public String getReport() {
        return String.format("[%s] %s — %s (by %s, %s)",
                announcementId, title, message, postedBy,
                targetBuilding != null ? "Building: " + targetBuilding : "All buildings");
    }

    @Override
    public LocalDateTime getReportDate() {
        return postedAt;
    }

    public String toCsv() {
        return announcementId + "|" + title + "|" + message + "|" + postedBy + "|"
                + FMT.format(postedAt) + "|" + (targetBuilding != null ? targetBuilding : "ALL");
    }

    public static Announcement fromCsv(String line) {
        String[] parts = line.split("\\|");
        Announcement a = new Announcement();
        a.announcementId = parts[0].trim();
        a.title = parts[1].trim();
        a.message = parts[2].trim();
        a.postedBy = parts[3].trim();
        a.postedAt = LocalDateTime.parse(parts[4].trim(), FMT);
        a.targetBuilding = parts[5].trim().equals("ALL") ? null : parts[5].trim();
        return a;
    }

    public String getAnnouncementId() { return announcementId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getPostedBy() { return postedBy; }
    public LocalDateTime getPostedAt() { return postedAt; }
    public String getTargetBuilding() { return targetBuilding; }

    @Override
    public String toString() {
        return "Announcement{id='" + announcementId + "', title='" + title + "'}";
    }
}
