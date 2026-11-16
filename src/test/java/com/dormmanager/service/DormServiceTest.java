package com.dormmanager.service;

import com.dormmanager.exception.InvalidInputException;
import com.dormmanager.exception.InvalidRoomException;
import com.dormmanager.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DormServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService storageService;
    private RoomService roomService;
    private ComplaintService complaintService;
    private ForumService forumService;

    @BeforeEach
    void setUp() {
        storageService = new FileStorageService(tempDir.toString());
        roomService = new RoomService(storageService);
        complaintService = new ComplaintService(storageService);
        forumService = new ForumService(storageService);
    }

    @Test
    void testAddRoomAndValidation() {
        Room r = roomService.addRoom(101, 1, "Campus A", 2);
        assertNotNull(r);
        assertEquals("Campus A-1-101", r.getId());

        // Duplicate room should throw InvalidRoomException
        assertThrows(InvalidRoomException.class, () -> roomService.addRoom(101, 1, "Campus A", 2));

        // Negative floor should throw InvalidInputException
        assertThrows(InvalidInputException.class, () -> roomService.addRoom(102, -1, "Campus A", 2));
    }

    @Test
    void testSubmitAnonymousComplaintAndPersistence() {
        Room r = roomService.addRoom(202, 2, "Campus B", 3);
        Complaint c = complaintService.submitAnonymousComplaint(
                "Ceiling leaking in bathroom during rain",
                ComplaintCategory.PLUMBING,
                Priority.HIGH,
                r
        );

        assertNotNull(c);
        assertEquals(ComplaintStatus.OPEN, c.getStatus());
        assertEquals(1, complaintService.getAllComplaints().size());

        // Save to file and reload
        complaintService.saveAll();
        ComplaintService freshService = new ComplaintService(storageService);
        freshService.loadAll(roomService.getRoomMap());

        assertEquals(1, freshService.getAllComplaints().size());
        assertEquals(c.getComplaintId(), freshService.getAllComplaints().get(0).getComplaintId());
    }

    @Test
    void testForumDiscussionThreading() {
        ForumPost post = forumService.createPost("Quiet Hours Reminder", "Please keep noise down after 10 PM.", "Resident Assistant");
        assertNotNull(post);

        ForumReply reply = forumService.addReply(post.getPostId(), "Thank you, very needed!", "Student X");
        assertNotNull(reply);

        List<ForumPost> posts = forumService.getAllPosts();
        assertEquals(1, posts.size());
        assertEquals(1, posts.get(0).getReplies().size());
        assertEquals("Student X", posts.get(0).getReplies().get(0).getAuthorName());
    }
}
