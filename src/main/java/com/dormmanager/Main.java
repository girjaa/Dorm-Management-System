package com.dormmanager;

import com.dormmanager.config.AppConfig;
import com.dormmanager.exception.*;
import com.dormmanager.model.*;
import com.dormmanager.service.*;
import com.dormmanager.validation.InputValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main entry point for the Dorm Management System.
 * Supports two modes via --mode=student|admin or config file.
 *
 * Usage:
 *   java -jar dorm-manager.jar                         (student mode, default config)
 *   java -jar dorm-manager.jar --mode=admin             (admin mode)
 *   java -jar dorm-manager.jar --config=config-admin.properties  (custom config file)
 */
public class Main {

    private static final String SEPARATOR = "═".repeat(50);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Scanner scanner;
    private final AppConfig config;
    private final FileStorageService fileStorage;
    private final RoomService roomService;
    private final StudentService studentService;
    private final ComplaintService complaintService;
    private final AnnouncementService announcementService;
    private final ForumService forumService;
    private final InspectionService inspectionService;

    public Main(String[] args) {
        this.scanner = new Scanner(System.in);
        this.config = AppConfig.getInstance();
        config.loadFromArgs(args);

        this.fileStorage = new FileStorageService(config.getDataDirectory());
        this.roomService = new RoomService(fileStorage);
        this.studentService = new StudentService(fileStorage);
        this.complaintService = new ComplaintService(fileStorage);
        this.announcementService = new AnnouncementService(fileStorage);
        this.forumService = new ForumService(fileStorage);
        this.inspectionService = new InspectionService(fileStorage);

        loadAllData();
    }

    public static void main(String[] args) {
        Main app = new Main(args);
        app.run();
    }

    private void run() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("  🏠 " + config.getAppName());
        System.out.println("  Mode: " + config.getAppMode().toUpperCase());
        System.out.println(SEPARATOR);

        if (config.isAdminMode()) {
            runAdminMenu();
        } else {
            runStudentMenu();
        }

        saveAllData();
        System.out.println("\n" + SEPARATOR);
        System.out.println("  Data saved. Goodbye! 👋");
        System.out.println(SEPARATOR);
    }

    // ==================== STUDENT MENU ====================

    private void runStudentMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. Submit anonymous complaint");
            System.out.println("2. View announcements");
            System.out.println("3. Forum - View posts");
            System.out.println("4. Forum - Create post");
            System.out.println("5. Forum - Reply to post");
            System.out.println("6. View rooms");
            System.out.println("0. Exit");
            System.out.print("Choice: ");

            try {
                int choice = InputValidator.parseInteger(scanner.nextLine(), "Menu choice");
                switch (choice) {
                    case 1 -> submitComplaint();
                    case 2 -> viewAnnouncements();
                    case 3 -> viewForumPosts();
                    case 4 -> createForumPost();
                    case 5 -> replyToForumPost();
                    case 6 -> viewRooms();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (InvalidInputException e) {
                System.out.println("⚠ Invalid input: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("⚠ Please enter a number.");
            }
        }
    }

    // ==================== ADMIN MENU ====================

    private void runAdminMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1.  View all complaints");
            System.out.println("2.  View complaints by status");
            System.out.println("3.  Update complaint status");
            System.out.println("4.  Assign complaint to staff");
            System.out.println("5.  Post announcement");
            System.out.println("6.  Schedule room inspection");
            System.out.println("7.  View upcoming inspections");
            System.out.println("8.  Manage rooms (add room)");
            System.out.println("9.  View all rooms");
            System.out.println("10. Register student");
            System.out.println("11. View all students");
            System.out.println("12. Complaint history (audit trail)");
            System.out.println("13. Forum - View posts");
            System.out.println("0.  Exit");
            System.out.print("Choice: ");

            try {
                int choice = InputValidator.parseInteger(scanner.nextLine(), "Menu choice");
                switch (choice) {
                    case 1 -> viewAllComplaints();
                    case 2 -> viewComplaintsByStatus();
                    case 3 -> updateComplaintStatus();
                    case 4 -> assignComplaint();
                    case 5 -> postAnnouncement();
                    case 6 -> scheduleInspection();
                    case 7 -> viewUpcomingInspections();
                    case 8 -> addRoom();
                    case 9 -> viewRooms();
                    case 10 -> registerStudent();
                    case 11 -> viewAllStudents();
                    case 12 -> viewComplaintHistory();
                    case 13 -> viewForumPosts();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (InvalidInputException e) {
                System.out.println("⚠ Invalid input: " + e.getMessage());
            } catch (InvalidRoomException e) {
                System.out.println("⚠ Room error: " + e.getMessage());
            } catch (StudentNotFoundException e) {
                System.out.println("⚠ Student error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("⚠ Please enter a valid number.");
            }
        }
    }

    // ==================== COMPLAINT ACTIONS ====================

    private void submitComplaint() {
        System.out.println("\n--- Submit Anonymous Complaint ---");

        // Select room
        List<Room> rooms = roomService.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms registered. Ask an admin to add rooms first.");
            return;
        }
        System.out.println("Available rooms:");
        for (int i = 0; i < rooms.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, rooms.get(i).getReport());
        }
        System.out.print("Select room number: ");
        int roomIdx = InputValidator.parseInteger(scanner.nextLine(), "Room selection") - 1;
        if (roomIdx < 0 || roomIdx >= rooms.size()) {
            System.out.println("Invalid room selection.");
            return;
        }
        Room room = rooms.get(roomIdx);

        // Select category
        System.out.println("Categories:");
        ComplaintCategory[] cats = ComplaintCategory.values();
        for (int i = 0; i < cats.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, cats[i].getDisplayName());
        }
        System.out.print("Select category: ");
        int catIdx = InputValidator.parseInteger(scanner.nextLine(), "Category") - 1;
        if (catIdx < 0 || catIdx >= cats.length) {
            System.out.println("Invalid category.");
            return;
        }

        // Select priority
        System.out.println("Priority: 1=Low, 2=Normal, 3=High, 4=Urgent");
        System.out.print("Select priority: ");
        int priIdx = InputValidator.parseInteger(scanner.nextLine(), "Priority") - 1;
        Priority[] pris = Priority.values();
        if (priIdx < 0 || priIdx >= pris.length) priIdx = 1; // default Normal

        // Description
        System.out.print("Describe the issue (min 11 chars): ");
        String desc = scanner.nextLine();

        try {
            Complaint c = complaintService.submitAnonymousComplaint(desc, cats[catIdx], pris[priIdx], room);
            System.out.println("✅ Complaint submitted anonymously! ID: " + c.getComplaintId());
        } catch (InvalidInputException e) {
            System.out.println("⚠ " + e.getMessage());
        }
    }

    private void viewAllComplaints() {
        List<Complaint> all = complaintService.getAllComplaints();
        if (all.isEmpty()) {
            System.out.println("No complaints yet.");
            return;
        }
        System.out.println("\n--- All Complaints (" + all.size() + ") ---");
        for (Complaint c : all) {
            System.out.println("  " + c.getReport());
        }
    }

    private void viewComplaintsByStatus() {
        System.out.println("Statuses: OPEN, IN_PROGRESS, ASSIGNED, RESOLVED, CLOSED");
        System.out.print("Enter status: ");
        String input = scanner.nextLine().trim().toUpperCase();
        try {
            ComplaintStatus status = ComplaintStatus.valueOf(input);
            List<Complaint> filtered = complaintService.getComplaintsByStatus(status);
            if (filtered.isEmpty()) {
                System.out.println("No complaints with status: " + status.getDisplayName());
            } else {
                for (Complaint c : filtered) {
                    System.out.println("  " + c.getReport());
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("⚠ Invalid status: " + input);
        }
    }

    private void updateComplaintStatus() {
        viewAllComplaints();
        System.out.print("Enter complaint ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("New status (OPEN/IN_PROGRESS/ASSIGNED/RESOLVED/CLOSED): ");
        String statusStr = scanner.nextLine().trim().toUpperCase();
        try {
            ComplaintStatus status = ComplaintStatus.valueOf(statusStr);
            if (complaintService.updateComplaintStatus(id, status)) {
                System.out.println("✅ Status updated.");
            } else {
                System.out.println("Complaint not found: " + id);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("⚠ Invalid status.");
        }
    }

    private void assignComplaint() {
        viewAllComplaints();
        System.out.print("Enter complaint ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter staff name to assign: ");
        String staff = scanner.nextLine().trim();
        InputValidator.validateNotEmpty(staff, "Staff name");
        if (complaintService.assignComplaint(id, staff)) {
            System.out.println("✅ Complaint assigned to: " + staff);
        } else {
            System.out.println("Complaint not found: " + id);
        }
    }

    private void viewComplaintHistory() {
        List<Complaint> history = complaintService.getComplaintHistory();
        if (history.isEmpty()) {
            System.out.println("No complaint history.");
            return;
        }
        System.out.println("\n--- Complaint History (newest first) ---");
        for (Complaint c : history) {
            System.out.printf("  [%s] %s — %s — %s%n",
                    DATE_FMT.format(c.getCreatedAt()), c.getComplaintId(),
                    c.getCategory().getDisplayName(), c.getStatus().getDisplayName());
        }
    }

    // ==================== ANNOUNCEMENT ACTIONS ====================

    private void postAnnouncement() {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Message: ");
        String message = scanner.nextLine();
        System.out.print("Target building (leave empty for all): ");
        String building = scanner.nextLine().trim();

        try {
            Announcement ann = announcementService.postAnnouncement(title, message, "Admin", building);
            System.out.println("✅ Announcement posted! ID: " + ann.getAnnouncementId());
        } catch (InvalidInputException e) {
            System.out.println("⚠ " + e.getMessage());
        }
    }

    private void viewAnnouncements() {
        List<Announcement> anns = announcementService.getAllAnnouncements();
        if (anns.isEmpty()) {
            System.out.println("No announcements.");
            return;
        }
        System.out.println("\n--- Announcements ---");
        for (Announcement a : anns) {
            System.out.println("  " + a.getReport());
        }
    }

    // ==================== INSPECTION ACTIONS ====================

    private void scheduleInspection() {
        List<Room> rooms = roomService.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms to inspect.");
            return;
        }
        for (int i = 0; i < rooms.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, rooms.get(i).getId());
        }
        System.out.print("Select room: ");
        int idx = InputValidator.parseInteger(scanner.nextLine(), "Room") - 1;
        if (idx < 0 || idx >= rooms.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        System.out.print("Date and time (yyyy-MM-dd HH:mm): ");
        String dateStr = scanner.nextLine().trim();
        LocalDateTime date;
        try {
            date = LocalDateTime.parse(dateStr, DATE_FMT);
        } catch (DateTimeParseException e) {
            System.out.println("⚠ Invalid date format. Use: yyyy-MM-dd HH:mm");
            return;
        }

        System.out.print("Inspector name: ");
        String inspector = scanner.nextLine().trim();

        try {
            RoomInspection ri = inspectionService.scheduleInspection(rooms.get(idx), date, inspector);
            System.out.println("✅ Inspection scheduled! ID: " + ri.getInspectionId());
        } catch (IllegalArgumentException e) {
            System.out.println("⚠ " + e.getMessage());
        }
    }

    private void viewUpcomingInspections() {
        List<RoomInspection> upcoming = inspectionService.getUpcomingInspections();
        if (upcoming.isEmpty()) {
            System.out.println("No upcoming inspections.");
            return;
        }
        System.out.println("\n--- Upcoming Inspections ---");
        for (RoomInspection ri : upcoming) {
            System.out.println("  " + ri.getReport());
        }
    }

    // ==================== ROOM ACTIONS ====================

    private void addRoom() {
        System.out.print("Building name: ");
        String building = scanner.nextLine().trim();
        System.out.print("Floor: ");
        int floor = InputValidator.parseInteger(scanner.nextLine(), "Floor");
        System.out.print("Room number: ");
        int number = InputValidator.parseInteger(scanner.nextLine(), "Room number");
        System.out.print("Capacity: ");
        int capacity = InputValidator.parseInteger(scanner.nextLine(), "Capacity");

        Room room = roomService.addRoom(number, floor, building, capacity);
        System.out.println("✅ Room added: " + room.getId());
    }

    private void viewRooms() {
        List<Room> rooms = roomService.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms registered.");
            return;
        }
        System.out.println("\n--- Rooms (" + rooms.size() + ") ---");
        for (Room r : rooms) {
            System.out.println("  " + r.getReport());
        }
    }

    // ==================== STUDENT ACTIONS ====================

    private void registerStudent() {
        System.out.print("Student ID (3-20 alphanumeric): ");
        String id = scanner.nextLine().trim();
        System.out.print("Full name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        // Room selection
        List<Room> available = roomService.getAvailableRooms();
        Room room = null;
        if (!available.isEmpty()) {
            System.out.println("Available rooms:");
            for (int i = 0; i < available.size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, available.get(i).getReport());
            }
            System.out.print("Assign to room (number, or 0 for none): ");
            int roomIdx = InputValidator.parseInteger(scanner.nextLine(), "Room") - 1;
            if (roomIdx >= 0 && roomIdx < available.size()) {
                room = available.get(roomIdx);
            }
        }

        try {
            Student s = studentService.registerStudent(id, name, email, room);
            System.out.println("✅ Student registered: " + s);
        } catch (Exception e) {
            System.out.println("⚠ " + e.getMessage());
        }
    }

    private void viewAllStudents() {
        List<Student> students = studentService.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students registered.");
            return;
        }
        System.out.println("\n--- Students (" + students.size() + ") ---");
        for (Student s : students) {
            System.out.println("  " + s);
        }
    }

    // ==================== FORUM ACTIONS ====================

    private void viewForumPosts() {
        List<ForumPost> posts = forumService.getAllPosts();
        if (posts.isEmpty()) {
            System.out.println("No forum posts yet. Be the first!");
            return;
        }
        System.out.println("\n--- Forum (" + posts.size() + " posts) ---");
        for (ForumPost p : posts) {
            System.out.println("  " + p.getReport());
            for (ForumReply r : p.getReplies()) {
                System.out.printf("    ↳ %s (%s): %s%n", r.getAuthorName(),
                        DATE_FMT.format(r.getCreatedAt()), r.getContent());
            }
        }
    }

    private void createForumPost() {
        System.out.print("Your name: ");
        String author = scanner.nextLine().trim();
        System.out.print("Post title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Content: ");
        String content = scanner.nextLine().trim();

        try {
            ForumPost post = forumService.createPost(title, content, author);
            System.out.println("✅ Post created! ID: " + post.getPostId());
        } catch (InvalidInputException e) {
            System.out.println("⚠ " + e.getMessage());
        }
    }

    private void replyToForumPost() {
        viewForumPosts();
        System.out.print("Post ID to reply to: ");
        String postId = scanner.nextLine().trim();
        System.out.print("Your name: ");
        String author = scanner.nextLine().trim();
        System.out.print("Reply: ");
        String content = scanner.nextLine().trim();

        try {
            ForumReply reply = forumService.addReply(postId, content, author);
            System.out.println("✅ Reply posted! ID: " + reply.getReplyId());
        } catch (Exception e) {
            System.out.println("⚠ " + e.getMessage());
        }
    }

    // ==================== DATA PERSISTENCE ====================

    private void loadAllData() {
        try {
            roomService.loadAll();
            Map<String, Room> roomMap = roomService.getRoomMap();
            studentService.loadAll(roomMap);
            complaintService.loadAll(roomMap);
            announcementService.loadAll();
            forumService.loadAll();
            inspectionService.loadAll(roomMap);
            System.out.println("Data loaded successfully.");
        } catch (DataLoadException e) {
            System.err.println("Warning: Could not load some data: " + e.getMessage());
        }
    }

    private void saveAllData() {
        try {
            roomService.saveAll();
            studentService.saveAll();
            complaintService.saveAll();
            announcementService.saveAll();
            forumService.saveAll();
            inspectionService.saveAll();
        } catch (DataLoadException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
}
