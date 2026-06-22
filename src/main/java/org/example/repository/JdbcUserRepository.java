package org.example.repository;

import org.example.model.User;
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
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper - DB 컬럼명과 매핑
    private static final class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User u = new User();
            u.setId(rs.getLong("id"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setNickname(rs.getString("nickname"));
            u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return u;
        }
    }

    private final RowMapper<User> rowMapper = new UserRowMapper();

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users ORDER BY id", rowMapper);
    }

    @Override
    public User findById(Long id) {
        List<User> results = jdbcTemplate.query(
            "SELECT * FROM users WHERE id = ?", rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public User findByUsername(String username) {
        List<User> results = jdbcTemplate.query(
            "SELECT * FROM users WHERE username = ?", rowMapper, username);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() == null || user.getId() == 0L) {
            var now = LocalDateTime.now();
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (username, password, nickname, created_at) VALUES (?, ?, ?, ?)",
                    new String[]{"id"}
                );
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getNickname());
                ps.setTimestamp(4, Timestamp.valueOf(now));
                return ps;
            }, keyHolder);
            return findById(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            // 기존 사용자 UPDATE
            jdbcTemplate.update(
                "UPDATE users SET nickname = ? WHERE id = ?",
                user.getNickname(), user.getId()
            );
            return findById(user.getId());
        }
    }

    @Override
    public boolean deleteById(Long id) {
        int affected = jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
        return affected > 0;
    }
}
