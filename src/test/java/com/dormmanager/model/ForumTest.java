package com.dormmanager.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ForumTest {

    @Test
    void testForumPostConstructor() {
        ForumPost post = new ForumPost("Study Group", "Anyone studying for algorithms?", "Mihai");
        assertNotNull(post.getPostId());
        assertEquals("Study Group", post.getTitle());
        assertEquals("Anyone studying for algorithms?", post.getContent());
        assertEquals("Mihai", post.getAuthorName());
        assertNotNull(post.getCreatedAt());
        assertTrue(post.getReplies().isEmpty());
        assertTrue(post.getReport().contains("Study Group"));
    }

    @Test
    void testForumReplyConstructor() {
        ForumReply reply = new ForumReply("POST01", "Count me in!", "Elena");
        assertNotNull(reply.getReplyId());
        assertEquals("POST01", reply.getPostId());
        assertEquals("Count me in!", reply.getContent());
        assertEquals("Elena", reply.getAuthorName());
        assertNotNull(reply.getCreatedAt());
    }

    @Test
    void testAddReplyToPost() {
        ForumPost post = new ForumPost("Notice", "Meeting at 18:00", "Admin");
        ForumReply reply = new ForumReply(post.getPostId(), "Noted", "Student1");
        post.addReply(reply);
        assertEquals(1, post.getReplies().size());
        assertEquals("Student1", post.getReplies().get(0).getAuthorName());
    }

    @Test
    void testForumCsvSerialization() {
        ForumPost post = new ForumPost("General", "Welcome to dorm", "Admin");
        String postCsv = post.toCsv();
        ForumPost parsedPost = ForumPost.fromCsv(postCsv);
        assertEquals(post.getPostId(), parsedPost.getPostId());
        assertEquals("General", parsedPost.getTitle());

        ForumReply reply = new ForumReply(post.getPostId(), "Thanks", "User1");
        String replyCsv = reply.toCsv();
        ForumReply parsedReply = ForumReply.fromCsv(replyCsv);
        assertEquals(reply.getReplyId(), parsedReply.getReplyId());
        assertEquals(post.getPostId(), parsedReply.getPostId());
    }
}
