package org.example.repository

import org.example.model.User
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDateTime

@Repository
@Profile("production")
@Transactional(readOnly = true)
class JdbcUserRepository(
    private val jdbc: JdbcTemplate
) : UserRepository {

    private val rowMapper = RowMapper<User> { rs: ResultSet, _ ->
        User(
            id = rs.getLong("id"),
            username = rs.getString("username"),
            password = rs.getString("password"),
            nickname = rs.getString("nickname"),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
        )
    }

    override fun findAll(): List<User> {
        return jdbc.query("SELECT * FROM users ORDER BY id", rowMapper)
    }

    override fun findById(id: Long): User? {
        return jdbc.query("SELECT * FROM users WHERE id = ?", rowMapper, id).firstOrNull()
    }

    override fun findByUsername(username: String): User? {
        return jdbc.query("SELECT * FROM users WHERE username = ?", rowMapper, username).firstOrNull()
    }

    @Transactional
    override fun save(user: User): User {
        if (user.id == 0L) {
            val kh = GeneratedKeyHolder()
            jdbc.update({ con ->
                val ps = con.prepareStatement(
                    "INSERT INTO users (username, password, nickname, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
                    arrayOf("id")
                )
                ps.setString(1, user.username)
                ps.setString(2, user.password)
                ps.setString(3, user.nickname)
                ps.setTimestamp(4, Timestamp.valueOf(user.createdAt))
                ps.setTimestamp(5, Timestamp.valueOf(user.updatedAt))
                ps
            }, kh)
            val id = kh.key!!.toLong()
            return user.copy(id = id)
        } else {
            val now = LocalDateTime.now()
            jdbc.update(
                "UPDATE users SET nickname = ?, updated_at = ? WHERE id = ?",
                user.nickname, Timestamp.valueOf(now), user.id
            )
            return user.copy(updatedAt = now)
        }
    }

    @Transactional
    override fun deleteById(id: Long): Boolean {
        return jdbc.update("DELETE FROM users WHERE id = ?", id) > 0
    }
}
