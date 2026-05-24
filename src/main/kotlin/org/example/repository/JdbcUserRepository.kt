package org.example.repository

import jakarta.annotation.PostConstruct
import org.example.model.User
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement
import java.sql.Timestamp
import java.time.LocalDateTime

@Repository
@Profile("deploy")
class JdbcUserRepository(
    private val jdbc: JdbcTemplate
) : UserRepository {

    @PostConstruct
    fun init() {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(255) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                nickname VARCHAR(255) NOT NULL,
                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent())
    }

    override fun findAll(): List<User> {
        return jdbc.query("SELECT * FROM users ORDER BY id") { rs, _ ->
            User(
                id = rs.getLong("id"),
                username = rs.getString("username"),
                password = rs.getString("password"),
                nickname = rs.getString("nickname"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime()
            )
        }
    }

    override fun findById(id: Long): User? {
        val results = jdbc.query("SELECT * FROM users WHERE id = ?", { rs, _ ->
            User(
                id = rs.getLong("id"),
                username = rs.getString("username"),
                password = rs.getString("password"),
                nickname = rs.getString("nickname"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime()
            )
        }, id)
        return results.firstOrNull()
    }

    override fun findByUsername(username: String): User? {
        val results = jdbc.query("SELECT * FROM users WHERE username = ?", { rs, _ ->
            User(
                id = rs.getLong("id"),
                username = rs.getString("username"),
                password = rs.getString("password"),
                nickname = rs.getString("nickname"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime()
            )
        }, username)
        return results.firstOrNull()
    }

    override fun save(user: User): User {
        val now = LocalDateTime.now()
        if (user.id == 0L) {
            val kh = GeneratedKeyHolder()
            jdbc.update({ con ->
                val ps = con.prepareStatement(
                    "INSERT INTO users (username, password, nickname, created_at) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                )
                ps.setString(1, user.username)
                ps.setString(2, user.password)
                ps.setString(3, user.nickname)
                ps.setTimestamp(4, Timestamp.valueOf(now))
                ps
            }, kh)
            val id = kh.key!!.toLong()
            return user.copy(id = id, createdAt = now)
        } else {
            jdbc.update(
                "UPDATE users SET nickname = ? WHERE id = ?",
                user.nickname, user.id
            )
            return user
        }
    }

    override fun deleteById(id: Long): Boolean {
        val affected = jdbc.update("DELETE FROM users WHERE id = ?", id)
        return affected > 0
    }
}
