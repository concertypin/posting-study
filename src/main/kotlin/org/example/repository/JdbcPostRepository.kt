package org.example.repository

import jakarta.annotation.PostConstruct
import org.example.model.Post
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement
import java.sql.Timestamp
import java.time.LocalDateTime

@Repository
@Profile("deploy")
class JdbcPostRepository(
    private val jdbc: JdbcTemplate
) : PostRepository {

    @PostConstruct
    fun init() {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS posts (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                content TEXT NOT NULL,
                author_id BIGINT NOT NULL,
                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent())
    }

    override fun findAll(): List<Post> {
        return jdbc.query("SELECT * FROM posts ORDER BY id") { rs, _ ->
            Post(
                id = rs.getLong("id"),
                title = rs.getString("title"),
                content = rs.getString("content"),
                authorId = rs.getLong("author_id"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
            )
        }
    }

    override fun findById(id: Long): Post? {
        val results = jdbc.query("SELECT * FROM posts WHERE id = ?", { rs, _ ->
            Post(
                id = rs.getLong("id"),
                title = rs.getString("title"),
                content = rs.getString("content"),
                authorId = rs.getLong("author_id"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
            )
        }, id)
        return results.firstOrNull()
    }

    override fun findByAuthorId(authorId: Long): List<Post> {
        return jdbc.query("SELECT * FROM posts WHERE author_id = ? ORDER BY created_at DESC", { rs, _ ->
            Post(
                id = rs.getLong("id"),
                title = rs.getString("title"),
                content = rs.getString("content"),
                authorId = rs.getLong("author_id"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
            )
        }, authorId)
    }

    override fun save(post: Post): Post {
        val now = LocalDateTime.now()
        if (post.id == 0L) {
            val kh = GeneratedKeyHolder()
            jdbc.update({ con ->
                val ps = con.prepareStatement(
                    "INSERT INTO posts (title, content, author_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                )
                ps.setString(1, post.title)
                ps.setString(2, post.content)
                ps.setLong(3, post.authorId)
                ps.setTimestamp(4, Timestamp.valueOf(now))
                ps.setTimestamp(5, Timestamp.valueOf(now))
                ps
            }, kh)
            val id = kh.key!!.toLong()
            return post.copy(id = id, createdAt = now, updatedAt = now)
        } else {
            jdbc.update(
                "UPDATE posts SET title = ?, content = ?, updated_at = ? WHERE id = ?",
                post.title, post.content, Timestamp.valueOf(now), post.id
            )
            return post.copy(updatedAt = now)
        }
    }

    override fun deleteById(id: Long): Boolean {
        val affected = jdbc.update("DELETE FROM posts WHERE id = ?", id)
        return affected > 0
    }
}
