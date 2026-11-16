package com.dormmanager.db;

import com.dormmanager.model.ForumPost;
import com.dormmanager.model.ForumReply;
import com.dormmanager.validation.InputValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Repository for the Student Forum.
 */
public class ForumDbRepository {

    private final DatabaseManager dbManager;

    public ForumDbRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<ForumPost> findAllPosts() throws SQLException {
        List<ForumPost> posts = new ArrayList<>();
        String sql = "SELECT post_id, title, content, author_name, created_at FROM forum_posts ORDER BY created_at DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ForumPost post = new ForumPost(
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("author_name")
                );
                // Load replies for this post
                loadRepliesForPost(conn, post, rs.getString("post_id"));
                posts.add(post);
            }
        }
        return posts;
    }

    private void loadRepliesForPost(Connection conn, ForumPost post, String postId) throws SQLException {
        String sql = "SELECT reply_id, post_id, content, author_name, created_at FROM forum_replies WHERE post_id = ? ORDER BY created_at ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ForumReply reply = new ForumReply(
                            postId,
                            rs.getString("content"),
                            rs.getString("author_name")
                    );
                    post.addReply(reply);
                }
            }
        }
    }

    public boolean insertPost(ForumPost post) throws SQLException {
        InputValidator.validateNotEmpty(post.getTitle(), "Post title");
        InputValidator.validateNotEmpty(post.getContent(), "Post content");
        InputValidator.validateNotEmpty(post.getAuthorName(), "Author name");

        String sql = "INSERT INTO forum_posts (post_id, title, content, author_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, post.getPostId());
            ps.setString(2, post.getTitle());
            ps.setString(3, post.getContent());
            ps.setString(4, post.getAuthorName());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean insertReply(ForumReply reply) throws SQLException {
        InputValidator.validateNotEmpty(reply.getContent(), "Reply content");
        InputValidator.validateNotEmpty(reply.getAuthorName(), "Author name");

        String sql = "INSERT INTO forum_replies (reply_id, post_id, content, author_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reply.getReplyId());
            ps.setString(2, reply.getPostId());
            ps.setString(3, reply.getContent());
            ps.setString(4, reply.getAuthorName());

            return ps.executeUpdate() > 0;
        }
    }
}
