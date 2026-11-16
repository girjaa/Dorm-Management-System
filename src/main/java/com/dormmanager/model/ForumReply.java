package com.dormmanager.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a reply to a forum post.
 */
public class ForumReply {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String replyId;
    private String postId;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;

    public ForumReply(String postId, String content, String authorName) {
        this.replyId = UUID.randomUUID().toString().substring(0, 8);
        this.postId = postId;
        this.content = content;
        this.authorName = authorName;
        this.createdAt = LocalDateTime.now();
    }

    private ForumReply() {}

    public String toCsv() {
        return replyId + "|" + postId + "|" + content + "|" + authorName + "|" + FMT.format(createdAt);
    }

    public static ForumReply fromCsv(String line) {
        String[] parts = line.split("\\|");
        ForumReply r = new ForumReply();
        r.replyId = parts[0].trim();
        r.postId = parts[1].trim();
        r.content = parts[2].trim();
        r.authorName = parts[3].trim();
        r.createdAt = LocalDateTime.parse(parts[4].trim(), FMT);
        return r;
    }

    public String getReplyId() { return replyId; }
    public String getPostId() { return postId; }
    public String getContent() { return content; }
    public String getAuthorName() { return authorName; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "ForumReply{id='" + replyId + "', author='" + authorName + "'}";
    }
}
