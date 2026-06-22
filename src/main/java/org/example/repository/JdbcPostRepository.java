package org.example.repository;

import org.example.model.Post;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Repository
@Profile("!default")
@Transactional(readOnly = true)
public class JdbcPostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final class PostRowMapper implements RowMapper<Post> {
        @Override
        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            Post p = new Post();
            p.setId(rs.getLong("id"));
            p.setTitle(rs.getString("title"));
            p.setContent(rs.getString("content"));
            p.setAuthorId(rs.getLong("author_id"));
            p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            p.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return p;
        }
    }

    private final RowMapper<Post> rowMapper = new PostRowMapper();

    @Override
    public List<Post> findAll() {
        // 전체 조회 - 생성일 내림차순
        return jdbcTemplate.query(
            "SELECT * FROM posts ORDER BY created_at DESC", rowMapper);
    }

    @Override
    public Post findById(Long id) {
        List<Post> results = jdbcTemplate.query(
            "SELECT * FROM posts WHERE id = ?", rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Post> findByAuthorId(Long authorId) {
        return jdbcTemplate.query(
            "SELECT * FROM posts WHERE author_id = ? ORDER BY created_at DESC",
            rowMapper, authorId);
    }

    @Override
    @Transactional
    public Post save(Post post) {
        if (post.getId() == null || post.getId() == 0L) {
            var now = LocalDateTime.now();
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO posts (title, content, author_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
                    new String[]{"id"}
                );
                ps.setString(1, post.getTitle());
                ps.setString(2, post.getContent());
                ps.setLong(3, post.getAuthorId());
                ps.setTimestamp(4, Timestamp.valueOf(now));
                ps.setTimestamp(5, Timestamp.valueOf(now));
                return ps;
            }, keyHolder);
            return findById(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            // UPDATE
            jdbcTemplate.update(
                "UPDATE posts SET title = ?, content = ?, updated_at = ? WHERE id = ?",
                post.getTitle(), post.getContent(),
                post.getUpdatedAt() != null ? post.getUpdatedAt() : LocalDateTime.now(),
                post.getId()
            );
            return findById(post.getId());
        }
    }

    @Override
    public boolean deleteById(Long id) {
        int affected = jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
        return affected > 0;
    }

    @Override
    public List<Post> findByCursor(LocalDateTime cursor, int limit) {
        if (cursor == null) {
            return jdbcTemplate.query(
                "SELECT * FROM posts ORDER BY created_at DESC LIMIT ?",
                rowMapper, limit);
        }
        return jdbcTemplate.query(
            "SELECT * FROM posts WHERE created_at < ? ORDER BY created_at DESC LIMIT ?",
            rowMapper, cursor, limit);
    }
}
