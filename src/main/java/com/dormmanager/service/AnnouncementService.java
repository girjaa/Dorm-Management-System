package com.dormmanager.service;

import com.dormmanager.model.Announcement;
import com.dormmanager.validation.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing admin announcements.
 */
public class AnnouncementService {

    private static final String ANNOUNCEMENTS_FILE = "announcements.dat";

    private final List<Announcement> announcements;
    private final FileStorageService fileStorage;

    public AnnouncementService(FileStorageService fileStorage) {
        this.announcements = new ArrayList<>();
        this.fileStorage = fileStorage;
    }

    public Announcement postAnnouncement(String title, String message, String adminName,
                                          String targetBuilding) {
        InputValidator.validateNotEmpty(title, "Announcement title");
        InputValidator.validateNotEmpty(message, "Announcement message");

        Announcement ann = new Announcement(
                InputValidator.sanitizeInput(title),
                InputValidator.sanitizeInput(message),
                adminName,
                targetBuilding != null && !targetBuilding.isBlank() ? targetBuilding : null);
        announcements.add(ann);
        return ann;
    }

    public List<Announcement> getAllAnnouncements() {
        return Collections.unmodifiableList(announcements);
    }

    public List<Announcement> getAnnouncementsForBuilding(String building) {
        return announcements.stream()
                .filter(a -> a.getTargetBuilding() == null
                        || a.getTargetBuilding().equalsIgnoreCase(building))
                .collect(Collectors.toList());
    }

    public void saveAll() {
        List<String> lines = announcements.stream().map(Announcement::toCsv).collect(Collectors.toList());
        fileStorage.saveToFile(ANNOUNCEMENTS_FILE, lines);
    }

    public void loadAll() {
        announcements.clear();
        List<String> lines = fileStorage.loadFromFile(ANNOUNCEMENTS_FILE);
        for (String line : lines) {
            if (!line.isBlank()) {
                announcements.add(Announcement.fromCsv(line));
            }
        }
    }
}
