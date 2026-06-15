package org.example.repository

import org.example.model.Post
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
class JdbcPostRepository(
    private val jdbc: JdbcTemplate
) : PostRepository {

    private val rowMapper = RowMapper<Post> { rs: ResultSet, _ ->
        Post(
            id = rs.getLong("id"),
            title = rs.getString("title"),
            content = rs.getString("content"),
            authorId = rs.getLong("author_id"),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
        )
    }

    override fun findAll(): List<Post> {
        return jdbc.query("SELECT * FROM posts ORDER BY id", rowMapper)
    }

    override fun findById(id: Long): Post? {
        return jdbc.query("SELECT * FROM posts WHERE id = ?", rowMapper, id).firstOrNull()
    }

    override fun findByAuthorId(authorId: Long): List<Post> {
        return jdbc.query(
            "SELECT * FROM posts WHERE author_id = ? ORDER BY created_at DESC",
            rowMapper, authorId
        )
    }

    @Transactional
    override fun save(post: Post): Post {
        if (post.id == 0L) {
            val kh = GeneratedKeyHolder()
            jdbc.update({ con ->
                val ps = con.prepareStatement(
                    "INSERT INTO posts (title, content, author_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
                    arrayOf("id")
                )
                ps.setString(1, post.title)
                ps.setString(2, post.content)
                ps.setLong(3, post.authorId)
                ps.setTimestamp(4, Timestamp.valueOf(post.createdAt))
                ps.setTimestamp(5, Timestamp.valueOf(post.updatedAt))
                ps
            }, kh)
            val id = kh.key!!.toLong()
            return post.copy(id = id)
        } else {
            val now = LocalDateTime.now()
            jdbc.update(
                "UPDATE posts SET title = ?, content = ?, updated_at = ? WHERE id = ?",
                post.title, post.content, Timestamp.valueOf(now), post.id
            )
            return post.copy(updatedAt = now)
        }
    }

    @Transactional
    override fun deleteById(id: Long): Boolean {
        return jdbc.update("DELETE FROM posts WHERE id = ?", id) > 0
    }

    override fun findByCursor(cursor: LocalDateTime?, limit: Int): List<Post> {
        val sql = """
            SELECT * FROM posts
            WHERE (? IS NULL OR created_at < ?)
            ORDER BY created_at DESC, id DESC
            LIMIT ?
        """.trimIndent()
        return jdbc.query(sql, rowMapper, cursor, cursor, limit)
    }
}
