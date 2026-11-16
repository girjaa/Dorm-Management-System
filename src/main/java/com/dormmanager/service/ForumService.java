package com.dormmanager.service;

import com.dormmanager.model.ForumPost;
import com.dormmanager.model.ForumReply;
import com.dormmanager.validation.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing the student forum.
 */
public class ForumService {

    private static final String POSTS_FILE = "forum_posts.dat";
    private static final String REPLIES_FILE = "forum_replies.dat";

    private final List<ForumPost> posts;
    private final FileStorageService fileStorage;

    public ForumService(FileStorageService fileStorage) {
        this.posts = new ArrayList<>();
        this.fileStorage = fileStorage;
    }

    public ForumPost createPost(String title, String content, String authorName) {
        InputValidator.validateNotEmpty(title, "Post title");
        InputValidator.validateNotEmpty(content, "Post content");
        InputValidator.validateNotEmpty(authorName, "Author name");

        ForumPost post = new ForumPost(
                InputValidator.sanitizeInput(title),
                InputValidator.sanitizeInput(content),
                InputValidator.sanitizeInput(authorName));
        posts.add(post);
        return post;
    }

    public ForumReply addReply(String postId, String content, String authorName) {
        InputValidator.validateNotEmpty(content, "Reply content");
        InputValidator.validateNotEmpty(authorName, "Author name");

        ForumPost post = getPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found: " + postId);
        }

        ForumReply reply = new ForumReply(postId,
                InputValidator.sanitizeInput(content),
                InputValidator.sanitizeInput(authorName));
        post.addReply(reply);
        return reply;
    }

    public List<ForumPost> getAllPosts() {
        return Collections.unmodifiableList(posts);
    }

    public ForumPost getPostById(String postId) {
        return posts.stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst()
                .orElse(null);
    }

    public void saveAll() {
        List<String> postLines = posts.stream().map(ForumPost::toCsv).collect(Collectors.toList());
        fileStorage.saveToFile(POSTS_FILE, postLines);

        List<String> replyLines = new ArrayList<>();
        for (ForumPost post : posts) {
            for (ForumReply reply : post.getReplies()) {
                replyLines.add(reply.toCsv());
            }
        }
        fileStorage.saveToFile(REPLIES_FILE, replyLines);
    }

    public void loadAll() {
        posts.clear();

        // Load posts first
        List<String> postLines = fileStorage.loadFromFile(POSTS_FILE);
        for (String line : postLines) {
            if (!line.isBlank()) {
                posts.add(ForumPost.fromCsv(line));
            }
        }

        // Load replies and attach to posts
        List<String> replyLines = fileStorage.loadFromFile(REPLIES_FILE);
        for (String line : replyLines) {
            if (!line.isBlank()) {
                ForumReply reply = ForumReply.fromCsv(line);
                ForumPost post = getPostById(reply.getPostId());
                if (post != null) {
                    post.addReply(reply);
                }
            }
        }
    }
}
