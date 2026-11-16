package com.dormmanager.model;

import com.dormmanager.interfaces.Reportable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a forum post where students can discuss dorm-related topics.
 */
public class ForumPost implements Reportable {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String postId;
    private String title;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
    private final List<ForumReply> replies;

    public ForumPost(String title, String content, String authorName) {
        this.postId = UUID.randomUUID().toString().substring(0, 8);
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.createdAt = LocalDateTime.now();
        this.replies = new ArrayList<>();
    }

    private ForumPost(String postId) {
        this.postId = postId;
        this.replies = new ArrayList<>();
    }

    @Override
    public String getReport() {
        return String.format("[%s] %s — by %s (%d replies)", postId, title, authorName, replies.size());
    }

    @Override
    public LocalDateTime getReportDate() {
        return createdAt;
    }

    public void addReply(ForumReply reply) {
        replies.add(reply);
    }

    public String toCsv() {
        return postId + "|" + title + "|" + content + "|" + authorName + "|" + FMT.format(createdAt);
    }

    public static ForumPost fromCsv(String line) {
        String[] parts = line.split("\\|");
        ForumPost p = new ForumPost(parts[0].trim());
        p.title = parts[1].trim();
        p.content = parts[2].trim();
        p.authorName = parts[3].trim();
        p.createdAt = LocalDateTime.parse(parts[4].trim(), FMT);
        return p;
    }

    public String getPostId() { return postId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthorName() { return authorName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<ForumReply> getReplies() { return Collections.unmodifiableList(replies); }

    @Override
    public String toString() {
        return "ForumPost{id='" + postId + "', title='" + title + "', replies=" + replies.size() + "}";
    }
}
